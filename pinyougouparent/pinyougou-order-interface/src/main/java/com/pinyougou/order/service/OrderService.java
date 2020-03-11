package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import entities.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

    /**
     * 返回全部列表
     * @return
     */
    public List<TbOrder> findAll();


    /**
     * 返回分页列表
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbOrder order);


    //根据用户名, 从redis获取未完成的支付日志
    TbPayLog queryPayLogByUsernameFromRedis(String username);

    /**
     * 支付成功后, 更新支付状态
     * 更新payLog表 和 order表
     * 删除redis中当前用户的payLog缓存
     * @param out_trade_no 要更新的payLog的唯一标识
     * @param transaction_id 插入payLog的微信支付唯一标识
     */
    void updatePaymentStatus(String out_trade_no, String transaction_id);

    /**
     * 修改
     */
    public void update(TbOrder order);


    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    public TbOrder findOne(Long id);


    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     * @param pageNum 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbOrder order, int pageNum, int pageSize);

}
