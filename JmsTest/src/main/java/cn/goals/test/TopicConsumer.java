package cn.goals.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

public class TopicConsumer {
    public static void main(String[] args) throws JMSException, IOException {
        //1, 创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.100:61616");
        //2, 创建连接对象
        Connection connection = connectionFactory.createConnection();
        //3, 启动连接
        connection.start();
        //4, 创建session对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5, 创建queue对象
        Topic topic = session.createTopic("test-topic");
        //6, 创建消费者对象
        MessageConsumer consumer = session.createConsumer(topic);
        //7, 开启监听
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println(textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //8, 因为监听是在另一个线程, 所以需要手动挂起程序, 需要时再关闭资源
        System.in.read();
        //9, 关闭资源
        consumer.close();
        session.close();
        connection.close();

    }
}
