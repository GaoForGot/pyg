package cn.goals.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicProducer {
    public static void main(String[] args) throws JMSException {
        //1, 创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.100:61616");
        //2, 获取连接
        Connection connection = connectionFactory.createConnection();
        //3, 启动连接
        connection.start();
        //4, 获取session, 参数1: 是否启动事务. 参数2: 消息确认模式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5, 创建topic对象
        Topic topic = session.createTopic("test-topic");
        //6, 创建生产者对象
        MessageProducer producer = session.createProducer(topic);
        //7, 创建消息对象
        TextMessage message = session.createTextMessage("我是习近平他爹");
        //8, 发送消息
        producer.send(message);
        //9, 关闭资源
        producer.close();
        session.close();
        connection.close();
    }
}
