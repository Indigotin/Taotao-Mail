package com.taotao.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;


public class TestActiveMq {
	
	/*
	 * 
	 * //1.创建一个连接工厂ConnectionFactory对象。需要指定mq服务的ip和port（与activemq服务端进行连接
			//2.使用ConnectionFactory来创建一个Connection对象
			//3.开启连接。调用Connection对象的start方法
			//4.使用Connection对象来创建一个Session对象
			//5.使用Session对象来创建一个Destination对象，两种形式queue、topic。现在使用queue
			//6.使用Session对象创建一个Producer对象
			//7.创建一个TextMessage对象
			//8.发送消息
			//9.关闭资源。
	 */
	//queue 点到点形式的
	//Producer
	@Test
	public void testQueueProducer() throws Exception{
		//1.创建一个连接工厂ConnectionFactory对象。(单例的)需要指定mq服务的ip和port（与activemq服务端进行连接
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//2.使用ConnectionFactory来创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		//3.开启连接。调用Connection对象的start方法
		connection.start();
		//4.使用Connection对象来创建一个Session对象
		//第一个参数是是否开启activemq的事务（分布式事务，性能较差）,一般不使用事务。保证数据的最终一致，可以使用消息队列实现。
		//如果第一个参数为true，第二个参数自动忽略。如果不开启事务false，第二个参数为消息的应答模式。一般使用自动应答。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.使用Session对象来创建一个Destination对象，两种形式queue、topic。现在使用queue,参数为指定当前消息队列的名称。queue继承自Destination
		Queue queue = session.createQueue("test-queue2");
		//6.使用Session对象创建一个Producer生产者对象
		MessageProducer producer = session.createProducer(queue);
		//7.创建一个TextMessage对象
		/*TextMessage textMessage = new ActiveMQTextMessage();
		textMessage.setText("hello activemq");*/
		TextMessage textMessage = session.createTextMessage("hello activemq11!");
		//8.发送消息
		producer.send(textMessage);
		//9.关闭资源。
		producer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void testQueueConsumer() throws Exception{
		//创建一个连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//使用连接工厂对象创建一个连接
		Connection connection = connectionFactory.createConnection();
		//开启连接
		connection.start();
		//使用连接对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//使用Session创建一个Destination，Destination应该和消息的发送端一致
		Queue queue = session.createQueue("test-queue");
		//使用Session创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		//向Consumer对象中设置一个MessageListener对象，用来接收消息
		//匿名内部类
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				//取消息内容
				if(message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage)message;
					try {
						String text = textMessage.getText();
						//打印消息内容
						System.out.println(text);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		//系统等待接收消息
		/*while(true) {
			Thread.sleep(100);
		}*/
		System.in.read();
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	//topic
	//producer
	@Test
	public void testTopicProducer() throws Exception{
		
		//创建一个连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//创建连接
		Connection connection = connectionFactory.createConnection();
		//开启连接
		connection.start();
		//创建一个session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//创建一个destination对象，使用topic
		Topic topic = session.createTopic("test-topic");
		//创建一个producer对象
		MessageProducer producer = session.createProducer(topic);
		//创建一个textMessage对象
		TextMessage textMessage = session.createTextMessage("hello activemq11!");
		//发送消息
		producer.send(textMessage);
		//关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	
	//queue消息如果没有被接收会缓存在服务端，默认持久化。而topic默认不持久化。
	@Test
	public void testTopicConsumer() throws Exception{
		//创建一个连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//创建连接
		Connection connection = connectionFactory.createConnection();
		//开启连接
		connection.start();
		//创建一个session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//创建一个destination对象，使用topic
		Topic topic = session.createTopic("test-topic");
		//创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(topic);
		//向Consumer对象中设置一个MessageListener对象，用来接收消息
		//匿名内部类
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
			//取消息内容
			if(message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage)message;
					try {
						String text = textMessage.getText();
						//打印消息内容
						System.out.println(text);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		//系统等待接收消息
		/*while(true) {
			Thread.sleep(100);
		}*/
		System.out.println("消费者3");
		System.in.read();
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
}
