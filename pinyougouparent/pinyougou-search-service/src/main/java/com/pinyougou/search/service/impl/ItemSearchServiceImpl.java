package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据searchMap中的所有条件, 返回封装好的查询结果map
     * 最终返回的map中有四个键, results, categories, brands, specs
     * @param searchMap
     * @return
     */
    @Override
    public Map searchKeywords(Map searchMap) {

        //1, 处理空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replaceAll(" ", ""));

        Map map = new HashMap();
        //2, 根据关键字搜索, 返回itemList, 键为results, 放入结果map
        map.putAll(searchItemList(searchMap));

        //3, 根据关键字分组搜索, 返回  查询结果中的  所有分类名
        if (!"".equals(searchMap.get("category"))) {//如果searchMap中有分组条件
            //将已选择的分类  直接放入结果map中
            List catList = new ArrayList();
            catList.add(searchMap.get("category"));
            map.put("categories", catList);
        } else {//searchMap中没有分组条件
            //根据关键字 查询所有分类
            Map catListMap = searchCatList(searchMap);
            map.putAll(catListMap);
        }

        //4, 根据排在第一个的分类名  获取品牌和规格列表
        List catList = (List) map.get("categories");
        if (catList != null && catList.size() > 0) {
            map.putAll(searchBrandAndSpec((String) catList.get(0)));
        }
        //5, 最终返回的map中主要有四个键, results, categories, brands, specs, 还有分页相关的几个参数
        return map;
    }

    /**
     * Spring Data Solr 高亮查询+条件过滤查询+分页查询
     * 根据关键字搜索, 返回itemList,  并且高亮展示关键词
     *
     * @param searchMap 搜索条件关键词封装在一个map中,  键为keywords
     * @return 搜索结果的List集合封装在一个map中, 键为results
     */
    private Map searchItemList(Map searchMap) {
        //1.1 添加查询条件和高亮字段
        //创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();

        //创建查询条件对象,  添加条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        //创建高亮选项对象,  添加高亮字段
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");

        query.addCriteria(criteria);//为查询添加条件
        query.setHighlightOptions(highlightOptions);//为查询添加高亮

        //1.2 过滤分类
        if (!"".equals(searchMap.get("category"))) {//前端传来的搜索条件中有分类, 才进行过滤查询
            Criteria catCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery catFilter = new SimpleFilterQuery(catCriteria);
            query.addFilterQuery(catFilter);
        }

        //1.3 过滤品牌
        if (!"".equals(searchMap.get("brand"))) {
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery brandFilter = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(brandFilter);
        }

        //1.4 过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map<String, String>) searchMap.get("spec");
            Set<String> specNames = spec.keySet();
            for (String specName : specNames) {
                String specOption = spec.get(specName);
                Criteria specCriteria = new Criteria("item_spec_" + specName).is(specOption);
                FilterQuery specFilter = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(specFilter);
            }

        }

        //1.5 过滤价格
        if (!"".equals(searchMap.get("price"))) {
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            //从0开始则开左区间
            if (!"0".equals(split[0])) {
                Criteria priceCriteriaL = new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery priceFilterL = new SimpleFilterQuery(priceCriteriaL);
                query.addFilterQuery(priceFilterL);
            }
            //不限制大小则开右区间
            if (!"*".equals(split[1])) {
                Criteria priceCriteriaR = new Criteria("item_price").lessThanEqual(split[1]);
                FilterQuery priceFilterR = new SimpleFilterQuery(priceCriteriaR);
                query.addFilterQuery(priceFilterR);
            }
        }

        //1.6 分页
        /*
            1, 分页需要: 页码, 每页数据量
            2, 分页返回: 页码, 每页数据量, 总页数, 数据结果集, 数据总量
        */
        Integer pageNo = (Integer) searchMap.get("pageNo");

        if (pageNo == null) {//如果前端没选择第几页, 就按第一页来
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }
        //设置分页起始索引--从第几个(包括这个)开始
        // 公式--pageSize * (pageNo-1)
        query.setOffset(pageSize * (pageNo - 1));
        //设置每页条数
        query.setRows(pageSize);

        //1.7排序
        String sort = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (sort != null && !"".equals(sort)) {
            if (sort.equals("ASC")) {
                query.addSort(new Sort(Sort.Direction.ASC, "item_" + sortField));
            }
            if (sort.equals("DESC")) {
                query.addSort(new Sort(Sort.Direction.DESC, "item_" + sortField));
            }
        }


        //查询高亮对象页
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //将高亮字段放入查询结果中
        //高亮入口集合--->每条记录的内容--->一次查询会有多条记录
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {//循环查询结果中的每一个实体类
            //判断有结果, 并且结果中有查询关键字被设置高亮
            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
                //获取实体类
                TbItem item = entry.getEntity();
                //获取高亮后的值内容
                //获取当前实体的所有高亮域中的第一个高亮域中的第一个值
                //因为solr中一个域可以有多个值 比如复制域,  如果是单值域, 就只有一个snipplet
                //这个snipplet就是添加过高亮的域值
                String s = entry.getHighlights().get(0).getSnipplets().get(0);
                //将高亮的内容放入实体类
                item.setTitle(s);
            }
        }
        Map map = new HashMap();
        //page.getContent()返回的list和page.getHighlighted()返回的list中的查询结果, 都是同一个引用
        //也就是说, 在HighLightEntry中的对象和page.getContent()中的对象是同一个
        map.put("results", page.getContent());
        map.put("totalPages", page.getTotalPages());//总页数
        map.put("total", page.getTotalElements());//总记录数
        return map;
    }

    /**
     * Spring Data Solr 分类查询 -- solrTemplate.queryForGroupPage(query, TbItem.class)
     * 根据关键字搜索,  查询所有符合条件商品的三级分类的名字,  封装成list放入map集合返回
     *
     * @param searchMap
     * @return 键为categories
     */
    private Map searchCatList(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //创建条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //创建分组对象
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        //添加条件对象和分组对象
        query.addCriteria(criteria);
        query.setGroupOptions(groupOptions);
        //执行查询, 获取group page对象
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果--->可能有多个分组
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组入口
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取entry的list集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        //遍历得到分组值
        for (GroupEntry<TbItem> entry : entryList) {
            String group = entry.getGroupValue();
            list.add(group);
        }
        map.put("categories", list);
        return map;
    }

    /**
     * Spring Data Redis
     * redisTemplate
     * 根据分类名, 查询品牌和规格和规格选项
     *
     * @param catName
     * @return  键为brands和specs
     */
    private Map searchBrandAndSpec(String catName) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCatTypeId").get(catName);
        if (typeId != null) {
            List brands = (List) redisTemplate.boundHashOps("brands").get(typeId);
            List specs = (List) redisTemplate.boundHashOps("specs").get(typeId);
            map.put("brands", brands);
            map.put("specs", specs);
        }
        return map;

    }

    //提供一个服务, 将新增过审的SKU上传到solr服务器
    public void importItems(List<TbItem> itemList) {
        for (TbItem item : itemList) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            //给动态域赋值,  solr动态域需要是map对象,  map中每一个键值对是一个域
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteItems(Long[] ids) {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


}
