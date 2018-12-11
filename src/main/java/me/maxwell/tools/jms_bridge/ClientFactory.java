package me.maxwell.tools.jms_bridge;

import me.maxwell.tools.jms_bridge.common.ActiveMQClient;
import me.maxwell.tools.jms_bridge.common.ActiveMQClientImpl;

/**
 * @author Maxwell.Lee
 * @date 2018-12-11 19:20
 * @since 1.0.0
 */
public class ClientFactory {

    public static ActiveMQClient getSrcClient() {
        return new ActiveMQClientImpl("tcp://192.168.4.250:61616?tcpNoDelay=true");
    }

    public static ActiveMQClient getDstClient() {
        return new ActiveMQClientImpl("tcp://192.168.1.197:61616?tcpNoDelay=true");
    }
}
