package me.maxwell.tools.jms_bridge.common;

import javax.jms.Message;
import java.io.Closeable;

/**
 * @author Maxwell.Lee
 * @date 2018-12-11 19:04
 * @since 1.0.0
 */
public interface ActiveMQClient extends Closeable {

    void startSession();

    void closeSession();

    Message receive(String queue, long waitTime);

    void send(String queue, Message msg);

}
