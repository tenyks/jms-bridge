package me.maxwell.tools.jms_bridge.common;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author Maxwell.Lee
 * @date 2018-12-11 19:10
 * @since 1.0.0
 */
public class ActiveMQClientImpl implements ActiveMQClient {

    private Connection connection;

    private Session session;

    public ActiveMQClientImpl(String url) {
        try {
            ConnectionFactory   cf = new ActiveMQConnectionFactory(url);
            Connection connection = cf.createConnection();
            connection.start();

            this.connection = connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startSession() {
        try {
            this.session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeSession() {
        try {
            this.session.commit();
            this.session.close();

            this.session = null;
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message receive(String queue, long waitTime) {
        try {
            Destination destination = session.createQueue(queue);
            MessageConsumer consumer = session.createConsumer(destination);

            Message msg = consumer.receive(waitTime);
            consumer.close();

            return msg;
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void send(String queue, Message msg) {
        if (msg == null) return ;

        try {
            Destination destination = session.createQueue(queue);
            MessageProducer producer = session.createProducer(destination);

            producer.send(msg);
            producer.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (session != null) {
            closeSession();
        }

        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            this.connection = null;
        }
    }
}
