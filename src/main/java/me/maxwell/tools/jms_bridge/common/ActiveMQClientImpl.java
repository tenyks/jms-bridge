package me.maxwell.tools.jms_bridge.common;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author Maxwell.Lee
 * @date 2018-12-11 19:10
 * @since 1.0.0
 */
public class ActiveMQClientImpl implements ActiveMQClient {

    private static final Logger log = LoggerFactory.getLogger(ActiveMQClientImpl.class);

    private String name;

    private Connection connection;

    private Session session;

    private MessageConsumer consumer;

    private MessageProducer producer;

    public ActiveMQClientImpl(String name, String url) {
        this.name = name;

        try {
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(url);
            cf.setAlwaysSessionAsync(false);
            cf.setAlwaysSyncSend(true);

            Connection connection = cf.createConnection();
            connection.start();

            this.connection = connection;
            this.session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        } catch (Exception e) {
            log.error("[0x09AMQCI3536]初始化时异常失败：", e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void commitTransaction() throws JMSException {
        this.session.commit();
    }

    @Override
    public void rollbackTransaction() throws JMSException {
        this.session.rollback();
    }

    @Override
    public Message receive(String queue, long waitTime) throws JMSException {
        if (consumer == null) {
            Destination destination = session.createQueue(queue);
            consumer = session.createConsumer(destination);
        }

        Message msg = consumer.receive(waitTime);
//        consumer.close();

        return msg;
    }

    @Override
    public void send(String queue, Message msg) throws JMSException {
        if (msg == null) return ;

        if (producer == null) {
            Destination destination = session.createQueue(queue);
            producer = session.createProducer(destination);
        }

        producer.send(msg);
//        producer.close();
    }

    @Override
    public void close() throws IOException {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                log.error("[0x09AMQCI10337]关闭Session时异常失败：", e);
            }
            session = null;
        }

        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (JMSException e) {
                log.error("[0x09AMQCI11137]关闭Connection时异常失败：", e);
            }
            this.connection = null;
        }
    }
}
