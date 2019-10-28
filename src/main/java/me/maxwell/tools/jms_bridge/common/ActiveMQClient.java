package me.maxwell.tools.jms_bridge.common;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.Closeable;

/**
 * @author Maxwell.Lee
 * @date 2018-12-11 19:04
 * @since 1.0.0
 */
public interface ActiveMQClient {

    String getName();

    void commitTransaction() throws JMSException;

    void rollbackTransaction() throws JMSException;

    Message receive(String queue, long waitTime) throws JMSException;

    void send(String queue, Message msg) throws JMSException;

    void send(String queue, String queue2, Message msg) throws JMSException;

    void close();
}
