package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${pageDir}")
    private String pageDir;

    @Override
    public boolean genItemHtml(Long goodsId) {

        try {
            //创建freemarker模板
            Configuration config = freeMarkerConfig.getConfiguration();
            Template template = config.getTemplate("item.ftl");

            //创建数据模型
            Map dataModel = new HashMap();

            //从dao层获取数据模型
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            //获取sku列表数据
            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria itemcriteria = itemExample.createCriteria();
            itemcriteria.andStatusEqualTo("1");//上架状态
            itemcriteria.andGoodsIdEqualTo(goodsId);//属于该SPU的所有SKU
            itemExample.setOrderByClause("is_default DESC");//根据是否是默认SKU降序排序, 大的在前, null值会被放在最后
            List<TbItem> itemList = itemMapper.selectByExample(itemExample);


            dataModel.put("goods", goods);//SPU数据
            dataModel.put("goodsDesc", goodsDesc);//SPU扩展表数据
            dataModel.put("itemCat1", itemCat1);//规格面包屑数据
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);
            dataModel.put("itemList", itemList);
            //创建输出流
            Writer out = new FileWriter(pageDir+goodsId+".html");

            //输出注入数据后的html页面
            template.process(dataModel, out);

            //关闭资源
            out.close();
            //创建页面成功
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //如果出现异常, 创建页面失败
            return false;
        }

    }

    @Override
    public boolean delItemHtml(Long goodsId) {
        try {
            boolean delResult = new File(pageDir + goodsId + ".html").delete();
            if (delResult) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


}
