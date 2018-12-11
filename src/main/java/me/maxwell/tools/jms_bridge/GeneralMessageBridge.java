package me.maxwell.tools.jms_bridge;

import me.maxwell.tools.jms_bridge.common.ActiveMQClient;

import javax.jms.Message;
import java.io.Closeable;
import java.io.IOException;

/**
 * 接收通用消息；
 * @author Maxwell.Lee
 * @date 2018-12-11 17:30
 * @since 1.0.0
 */
public class GeneralMessageBridge implements Closeable {

    private static final long DefaultWaitTime = 30000;

    private ActiveMQClient srcClient;

    private String  srcQueue;

    private ActiveMQClient dstClient;

    private String  dstQueue;

    public GeneralMessageBridge(ActiveMQClient srcClient, String srcQueue,
                                ActiveMQClient dstClient, String dstQueue) {
        this.srcClient = srcClient;
        this.dstClient = dstClient;
    }

    public void handle() {
        srcClient.startSession();

        final Message msg = srcClient.receive(srcQueue, DefaultWaitTime);

        dstClient.startSession();
        dstClient.send(dstQueue, msg);

        dstClient.closeSession();
        srcClient.closeSession();
    }

    public void close() throws IOException {
        if (srcClient != null) {
            this.srcClient.close();
            this.srcClient = null;
        }
        if (dstClient != null) {
            this.dstClient.close();
            this.dstClient = null;
        }
    }

}
