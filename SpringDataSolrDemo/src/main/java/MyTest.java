import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.NamedList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Crotch;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-solr.xml")
public class MyTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void test1() {
        TbItem item = new TbItem();
        item.setId(666L);
        item.setGoodsId(1L);
        item.setTitle("苹果100s");
        item.setPrice(new BigDecimal(8899.66));
        item.setCategory("手机");
        item.setSeller("店");
        item.setBrand("Apple");

        UpdateResponse updateResponse = solrTemplate.saveBean(item);
        solrTemplate.commit();
        NamedList<Object> response = updateResponse.getResponse();
        System.out.println(response);
    }

    @Test
    public void test2() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test
    public void test3() {
        TbItem item = solrTemplate.getById("1", TbItem.class);
        System.out.println(item);

    }

    @Test
    public void test4() {
        List<TbItem> list = new ArrayList<TbItem>();
        for (int i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(i + 1L);
            item.setGoodsId(1L);
            item.setTitle("苹果" + (i + 1));
            item.setPrice(new BigDecimal(1000 + i));
            item.setCategory("手机");
            item.setSeller("Apple旗舰店");
            item.setBrand("Apple");
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();


    }

    @Test
    public void test5() {
        //分页查询
        Query query = new SimpleQuery("*:*");
        query.setOffset(0);
        query.setRows(20);
        Criteria criteria = new Criteria("item_seller").is("旗舰店");
        criteria = criteria.and("item_title").contains("1");
        query.addCriteria(criteria);

        ScoredPage<TbItem> response = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> items = response.getContent();
        for (TbItem item : items) {
            System.out.println(item.getId() + item.getTitle());
        }
    }

    @Test
    public void test6() {
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();

    }

}
