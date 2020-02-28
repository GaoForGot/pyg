package cn.goals.test;

import cn.goals.demo.QueueProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-jms-producer.xml")
public class TestProducer {

    @Autowired
    private QueueProducer queueProducer;
    @Test
    public void sendMessage() {
        queueProducer.sendMessage("哈哈哈哈啊");
    }
    @Test
    public void sendTopMessage() {
        queueProducer.sendTopicMessage("6666666");
    }
}
