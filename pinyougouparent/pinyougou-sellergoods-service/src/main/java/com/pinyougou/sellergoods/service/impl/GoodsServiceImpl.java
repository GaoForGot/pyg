package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.grouppojo.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import entities.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getTbGoods();
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        //设置审核状态为0, 未审核
        tbGoods.setAuditStatus("0");
        //添加商品
        goodsMapper.insert(tbGoods);
        //给goodsDesc设置主键
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        //添加商品描述
        goodsDescMapper.insert(tbGoodsDesc);
        //添加sku列表
        insertItemList(goods);

    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        TbGoods tbGoods = goods.getTbGoods();
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        //更新goods
        goodsMapper.updateByPrimaryKey(tbGoods);
        //更新goodsDesc
        goodsDescMapper.updateByPrimaryKey(tbGoodsDesc);
        //更新sku
        //先删除再插入
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(tbGoods.getId());
        itemMapper.deleteByExample(example);
        //插入传过来的list
        insertItemList(goods);
    }


    /**
     * 添加sku时  添加属性的重复代码
     * @param tbGoodsDesc
     * @param item
     * @param tbGoods
     */
    private void setItem(TbGoodsDesc tbGoodsDesc, TbItem item, TbGoods tbGoods) {
        //图片
        String itemImages = tbGoodsDesc.getItemImages();
        List<Map> imgMaps = JSON.parseArray(itemImages, Map.class);
        String url = (String) imgMaps.get(0).get("url");
        item.setImage(url);
        //分类ID
        Long category3Id = tbGoods.getCategory3Id();
        item.setCategoryid(category3Id);
        //创建/更新日期
        Date date = new Date();
        item.setCreateTime(date);
        item.setUpdateTime(date);
        //goodsId和sellerId
        item.setGoodsId(tbGoods.getId());
        item.setSellerId(tbGoods.getSellerId());
        //分类名
        TbItemCat cat = itemCatMapper.selectByPrimaryKey(category3Id);
        item.setCategory(cat.getName());
        //品牌名
        TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
        item.setBrand(brand.getName());
        //店铺名
        TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
        item.setSeller(seller.getNickName());
    }

    /**
     * 添加sku列表
     * @param goods
     */
    private void insertItemList(Goods goods) {
        List<TbItem> itemList = goods.getTbItemList();
        TbGoods tbGoods = goods.getTbGoods();
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        if ("1".equals(tbGoods.getIsEnableSpec())) {//开启规格
            for (TbItem item : itemList) {

                //标题, 不同规格
                String title = tbGoods.getGoodsName();
                String spec = item.getSpec();
                Map<String, Object> specMap = (Map<String, Object>) JSON.parse(spec);
                for (String key : specMap.keySet()) {
                    Object option = specMap.get(key);
                    title += " " + option;
                }
                item.setTitle(title);
                //其他属性
                setItem(tbGoodsDesc, item, tbGoods);
                //插入数据
                itemMapper.insert(item);
            }

        } else {//关闭规格, 意味着只有一条sku
            TbItem item = new TbItem();
            //标题, 没有规格
            String goodsName = tbGoods.getGoodsName();
            item.setTitle(goodsName);
            //价格, 库存, 是否启用, 是否默认
            item.setPrice(tbGoods.getPrice());
            item.setNum(9999);
            item.setStatus("1");
            item.setIsDefault("1");
            //其他属性
            setItem(tbGoodsDesc, item, tbGoods);
            //插入数据
            itemMapper.insert(item);

        }
    }




    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);
        goods.setTbGoodsDesc(tbGoodsDesc);
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setTbItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

}
