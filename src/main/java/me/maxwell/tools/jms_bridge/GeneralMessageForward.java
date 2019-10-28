package me.maxwell.tools.jms_bridge;

import me.maxwell.tools.jms_bridge.common.ActiveMQClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 接收通用消息；
 * @author Maxwell.Lee
 * @date 2018-12-11 17:30
 * @since 1.0.0
 */
public class GeneralMessageForward implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(GeneralMessageForward.class);

    private static final long DefaultWaitTime = 30000L;

    private ActiveMQClient srcClient;

    private String  srcQueue;

    private ActiveMQClient dstClient;

    private String  dstQueue;

    private String  dstQueueClone;

    private String  description;

    private AtomicLong lastHeartBeat;

    public GeneralMessageForward(ActiveMQClient srcClient, String srcQueue,
                                 ActiveMQClient dstClient, String dstQueue,
                                 AtomicLong lastHeartBeat) {
        if (srcClient == null || srcQueue == null || dstClient == null || dstQueue == null) {
            throw new IllegalArgumentException("参数不全。[0x02GMB3664]");
        }

        this.srcClient = srcClient;
        this.dstClient = dstClient;

        this.srcQueue = srcQueue;
        this.dstQueue = dstQueue;

        this.lastHeartBeat = lastHeartBeat;
        this.description = String.format("%s/%s => %s/%s", srcClient.getName(), srcQueue, dstClient.getName(), dstQueue);
    }

    public GeneralMessageForward(ActiveMQClient srcClient, String srcQueue,
                                 ActiveMQClient dstClient, String dstQueue, String dstQueueClone,
                                 AtomicLong lastHeartBeat) {
        if (srcClient == null || srcQueue == null || dstClient == null || dstQueue == null || dstQueueClone == null) {
            throw new IllegalArgumentException("参数不全。[0x02GMB3664]");
        }

        this.srcClient = srcClient;
        this.dstClient = dstClient;

        this.srcQueue = srcQueue;
        this.dstQueue = dstQueue;
        this.dstQueueClone = dstQueueClone;

        this.lastHeartBeat = lastHeartBeat;
        this.description = String.format("%s/%s => %s/[%s,%s]", srcClient.getName(), srcQueue,
                                        dstClient.getName(), dstQueue, dstQueueClone);
    }

    public int start(long durationTime) throws JMSException {
        long endTime = System.currentTimeMillis() + durationTime;

        int count = 0;
        while (endTime > System.currentTimeMillis()) {
            int flag = forwardOneMessage();
            if (flag > 0) count ++;
        }

        return count;
    }

    private int forwardOneMessage() throws JMSException {
        try {
            lastHeartBeat.set(System.currentTimeMillis());
            Message msg = srcClient.receive(srcQueue, DefaultWaitTime);
            if (msg == null) {
                log.debug("[Forward][{}]没有消息需要转发。", description);
                return 0;
            }

            if (dstQueueClone != null) {
                dstClient.send(dstQueue, dstQueueClone, msg);
            } else {
                dstClient.send(dstQueue, msg);
            }
            dstClient.commitTransaction();

            if (log.isInfoEnabled()) {
                log.info("[Forward][{}]{}", description, msg);
            }
        } catch (JMSException e) {
            srcClient.rollbackTransaction();
            throw e;
        } finally {
            srcClient.commitTransaction();
        }

        return 1;
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
