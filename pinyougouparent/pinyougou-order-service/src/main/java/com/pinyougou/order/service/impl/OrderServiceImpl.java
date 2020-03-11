package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.pinyougou.grouppojo.Cart;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.util.IdWorker;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.order.service.OrderService;
import entities.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     * order中应封装了 控制层: userId, 订单来源   前端: 地址, 手机号, 收件人, 付款方式
     * redis购物车列表中应封装了 orderItem除了主键id和订单id的所有信息
     * 新建订单后, 需要给新建的订单生成一条支付日志记录, 然后存入数据库和redis, 供表现层调用
     * 在创建支付时, 会从redis中获取该支付的支付id和支付金额
     */
    @Override
    public void add(TbOrder order) {
        //支付的总支付金额, 是所有订单金额的总和, 在循环中累加
        long payLogTotalFee = 0;
        //支付订单id列表, 保存一次支付中的所有订单id, 在循环中添加
        List payLogOrderIds = new ArrayList();
        // 根据userId从redis中获取购物车列表
        String username = order.getUserId();
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartLists").get(username);
        // 遍历购物车列表, 封装数据, 存入数据库, 一个购物车一个order, 一个item一个orderItem
        for (Cart cart : cartList) {
            //补全order
            order.setOrderId(idWorker.nextId());//设置主键orderId
            String sellerId = cart.getSellerId();
            order.setSellerId(sellerId);//设置商家id
            order.setStatus("1");//设置状态为未付款
            order.setCreateTime(new Date());//订单创建时间
            order.setUpdateTime(new Date());//订单更新时间
            double payment = 0.0;//总金额, 遍历累加后赋值

            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                //补全orderItem
                orderItem.setId(idWorker.nextId());//设置主键id
                orderItem.setOrderId(order.getOrderId());//设置orderId
                orderItemMapper.insert(orderItem);//存入数据库
                payment += orderItem.getTotalFee().doubleValue();
            }
            order.setPayment(new BigDecimal(payment));//设置总金额
            orderMapper.insert(order);//存入数据库
            payLogTotalFee += payment*100;//支付日志中的金额单位为分
            payLogOrderIds.add(order.getOrderId());
        }
        // 从redis删除购物车列表
        redisTemplate.boundHashOps("cartLists").delete(username);
        //新建paylog对象, 存入数据库和redis
        //缺省的支付时间, 微信交易id需支付后添加
        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(idWorker.nextId()+"");//支付id
        payLog.setCreateTime(new Date());//支付创建日期
        payLog.setTotalFee(payLogTotalFee);//支付总金额
        payLog.setUserId(username);//用户名
        payLog.setTradeState("0");//支付状态, 0未支付, 1已支付
        //将list字符串的[]括号去除
        String orderList = payLogOrderIds.toString().replace("[", "").replace("]", "").replace(" ","");
        payLog.setOrderList(orderList);//所支付订单的id集合(一次支付多个订单)
        payLog.setPayType("1");//支付类型, 1微信
        //存入redis和数据库
        payLogMapper.insert(payLog);
        redisTemplate.boundHashOps("payLogs").put(username, payLog);
    }

    //根据用户名, 从redis获取未完成的支付日志
    @Override
    public TbPayLog queryPayLogByUsernameFromRedis(String username) {
        return (TbPayLog) redisTemplate.boundHashOps("payLogs").get(username);
    }

    // 更新支付日志表和订单表, 并删除redis中的缓存数据, 供表现层调用
    // 需传入支付id和微信交易id
    // username从paylog表中获取
    @Override
    public void updatePaymentStatus(String out_trade_no, String transaction_id) {
        //更新payLog
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLog.setTradeState("1");//支付状态, 0未支付, 1已支付
        //更新order表
        String[] orderIds = payLog.getOrderList().split(",");
        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
            order.setStatus("2");//支付状态: 1未付款, 2已付款
            order.setUpdateTime(new Date());
            order.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKey(order);//更新order表
        }
        //删除缓存
        redisTemplate.boundHashOps("payLogs").delete(payLog.getUserId());
        //更新payLog表
        payLogMapper.updateByPrimaryKey(payLog);
    }





    /**
     * 修改
     */
    @Override
    public void update(TbOrder order){
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id){
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example=new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if(order!=null){
            if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
                criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
            }
            if(order.getPostFee()!=null && order.getPostFee().length()>0){
                criteria.andPostFeeLike("%"+order.getPostFee()+"%");
            }
            if(order.getStatus()!=null && order.getStatus().length()>0){
                criteria.andStatusLike("%"+order.getStatus()+"%");
            }
            if(order.getShippingName()!=null && order.getShippingName().length()>0){
                criteria.andShippingNameLike("%"+order.getShippingName()+"%");
            }
            if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
                criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
            }
            if(order.getUserId()!=null && order.getUserId().length()>0){
                criteria.andUserIdLike("%"+order.getUserId()+"%");
            }
            if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
                criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
            }
            if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
                criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
            }
            if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
                criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
            }
            if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
                criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
            }
            if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
                criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
            }
            if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
                criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
            }
            if(order.getReceiver()!=null && order.getReceiver().length()>0){
                criteria.andReceiverLike("%"+order.getReceiver()+"%");
            }
            if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
                criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
            }
            if(order.getSourceType()!=null && order.getSourceType().length()>0){
                criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
            }
            if(order.getSellerId()!=null && order.getSellerId().length()>0){
                criteria.andSellerIdLike("%"+order.getSellerId()+"%");
            }

        }

        Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
