import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class Demo01 {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test1() {
        redisTemplate.boundValueOps("name").set("高达牛逼");
    }
    @Test
    public void test2() {
        String name = (String) redisTemplate.boundValueOps("name").get();
        System.out.println(name);
    }

    @Test
    public void test3() {
        redisTemplate.delete("name");
    }

    @Test
    public void test4() {
        Long res = redisTemplate.boundSetOps("set集合").add("a", "b", "c", "a", "b");
        System.out.println(res);
    }

    @Test
    public void test5() {
        Set set = redisTemplate.boundSetOps("set集合").members();
        System.out.println(set);
    }

    @Test
    public void test6() {
        Long res = redisTemplate.boundSetOps("set集合").remove("a", "b", "c");
        System.out.println(res);
    }

    @Test
    public void test7() {
        redisTemplate.delete("set集合");
    }

    @Test
    public void test8() {
        Long aLong = redisTemplate.boundListOps("listNames").rightPushAll("张飞", "关羽", "点赞双击么么哒");
        System.out.println(aLong);
    }

    @Test
    public void test9() {
        List list = redisTemplate.boundListOps("listNames").range(0, 10);
        System.out.println(list);
    }

    @Test
    public void test10() {
        redisTemplate.boundListOps("listNames2").leftPush("张飞");
        redisTemplate.boundListOps("listNames2").leftPush("关羽");
        redisTemplate.boundListOps("listNames2").leftPush("双击点赞么么哒");

    }

    @Test
    public void test11() {
        List list = redisTemplate.boundListOps("listNames2").range(0, 10);
        System.out.println(list);
    }

    @Test
    public void test12() {
        Long remove = redisTemplate.boundListOps("listNames2").remove(1, "张飞");
        System.out.println(remove);

    }

    @Test
    public void test13() {
        Long remove = redisTemplate.boundListOps("listNames2").remove(2, "双击点赞么么哒");
        System.out.println(remove);

    }

    @Test
    public void test14() {
        redisTemplate.boundHashOps("newHash").put("a", "玉皇大帝");
        redisTemplate.boundHashOps("newHash").put("b", "远古天尊");
        redisTemplate.boundHashOps("newHash").put("c", "太上老君");
        redisTemplate.boundHashOps("newHash").put("d", "真至圣贤");

    }

    @Test
    public void test144() {
        redisTemplate.boundHashOps("newHash").put("a", "cc");
        redisTemplate.boundHashOps("newHash").put("b", "bbbbbb");
        redisTemplate.boundHashOps("newHash").put("c", "太上老君");
/*
        redisTemplate.boundHashOps("newHash").put("d", "真至圣贤");
*/

    }
    @Test
    public void test15() {
        Set newHash = redisTemplate.boundHashOps("itemCatTypeId").keys();
        System.out.println(newHash);
    }

    @Test
    public void test16() {
        List newHash = redisTemplate.boundHashOps("itemCatTypeId").values();
        System.out.println(newHash);

    }

    @Test
    public void test17() {
        Object o = redisTemplate.boundHashOps("newHash").get("d");
        System.out.println(o);
    }

    @Test
    public void test18() {
        Map newHash = redisTemplate.boundHashOps("newHash").entries();
        System.out.println(newHash);

    }

    @Test
    public void test19() {
        Long delete = redisTemplate.boundHashOps("newHash").delete("a", "b");
        System.out.println(delete);
    }
    @Test
    public void test20() {
        redisTemplate.delete("listNames2");

    }


}
