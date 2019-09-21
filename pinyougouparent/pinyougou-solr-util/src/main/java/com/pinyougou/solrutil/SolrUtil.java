package com.pinyougou.solrutil;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper mapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData() {

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> items = mapper.selectByExample(example);
        for (TbItem item : items) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();

    }

    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");
        solrUtil.importItemData();
    }

}
