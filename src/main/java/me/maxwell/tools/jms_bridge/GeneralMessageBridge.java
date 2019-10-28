package me.maxwell.tools.jms_bridge;

import me.maxwell.tools.jms_bridge.common.ActiveMQClient;
import me.maxwell.tools.jms_bridge.common.ActiveMQClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Maxwell.Lee
 * @date 2018-12-12 10:32
 * @since 1.0.0
 */
public class GeneralMessageBridge implements Runnable {

    private static final Logger     log = LoggerFactory.getLogger(GeneralMessageBridge.class);

    private String   srcName;

    private String   srcUrl;

    private String   srcQueue;

    private String   dstName;

    private String   dstUrl;

    private String   dstQueue;

    private String   dstQueueClone;

    private String   description;

    private Long    delayTimeOnError = 30 * 1000L;//30秒；

    private Long    epochDuration = 5 * 60 * 1000L;//5分钟；

    private final AtomicLong lastHeartBeatTime = new AtomicLong(0);

    public GeneralMessageBridge(String srcName, String srcUrl, String srcQueue,
                                String dstName, String dstUrl, String dstQueue) {
        this.srcName = srcName;
        this.srcUrl = srcUrl;
        this.srcQueue = srcQueue;

        this.dstName = dstName;
        this.dstUrl = dstUrl;
        this.dstQueue = dstQueue;

        this.description = String.format("Bridge(%s => %s)", srcName, dstName);
    }

    public GeneralMessageBridge(String srcName, String srcUrl, String srcQueue,
                                String dstName, String dstUrl, String dstQueue, String dstQueueClone) {
        this.srcName = srcName;
        this.srcUrl = srcUrl;
        this.srcQueue = srcQueue;

        this.dstName = dstName;
        this.dstUrl = dstUrl;
        this.dstQueue = dstQueue;
        this.dstQueueClone = dstQueueClone;

        this.description = String.format("Bridge(%s => %s)", srcName, dstName);
    }

    public boolean checkAlive() {
        long lt = lastHeartBeatTime.get();

        return (System.currentTimeMillis() - lt < delayTimeOnError * 2);
    }

    @Override
    public void run() {
        int seqNo = 1;

        while (true) {
            int flag = launchOneEpoch(seqNo++);

            if (flag < 0) {
                log.info("[{}]等待{}毫秒，再运行。", description, delayTimeOnError);
                try {
                    Thread.sleep(delayTimeOnError);
                } catch (InterruptedException e) {
                    log.info("[{}]收到停机信号，退出。", description);
                    return ;
                }
            } else {
                log.info("[{}]即将开始新得世代。", description);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.info("[{}]收到停机信号，退出。", description);
                    return ;
                }
            }
        }
    }

    public String   getDescription() {
        return description;
    }

    private int launchOneEpoch(int seqNo) {
        long bt = System.currentTimeMillis();

        ActiveMQClient srcClient = null;
        ActiveMQClient dstClient = null;

        try {
            log.info("[{}]第{}世代开始...", description, seqNo);

            srcClient = new ActiveMQClientImpl(srcName, srcUrl);
            dstClient = new ActiveMQClientImpl(dstName, dstUrl);

            GeneralMessageForward forward;
            if (dstQueueClone == null) {
                forward = new GeneralMessageForward(srcClient, srcQueue, dstClient, dstQueue, lastHeartBeatTime);
            } else {
                forward = new GeneralMessageForward(srcClient, srcQueue, dstClient, dstQueue, dstQueueClone, lastHeartBeatTime);
            }

            int flag = forward.start(epochDuration);
            String epochTimeDesc = getEpochTimeDesc(bt, System.currentTimeMillis());
            log.info("[{}]第{}世代结束, {}, flag={}。", description, seqNo, epochTimeDesc, flag);

            return flag;
        } catch (Exception e) {
            String epochTimeDesc = getEpochTimeDesc(bt, System.currentTimeMillis());
            log.error("[{}]第{}世代异常结束， {}：", description, seqNo, epochTimeDesc, e);
            return -1;
        } finally {
            if (srcClient != null) {
                srcClient.close();
                srcClient = null;
            }
            if (dstClient != null) {
                dstClient.close();
                dstClient = null;
            }
        }
    }

    private String getEpochTimeDesc(long beginTime, long endTime) {
        Date bt = new Date(beginTime);
        Date et = new Date(endTime);

        String btStr = String.format("%tm%td%tH%tM%tS", bt, bt, bt, bt, bt);
        String etStr = String.format("%tm%td%tH%tM%tS", et, et, et, et, et);

        return String.format("%s -> %s", btStr, etStr);
    }

    public Long getDelayTimeOnError() {
        return delayTimeOnError;
    }

    public void setDelayTimeOnError(Long delayTimeOnError) {
        this.delayTimeOnError = delayTimeOnError;
    }

    public Long getEpochDuration() {
        return epochDuration;
    }

    public void setEpochDuration(Long epochDuration) {
        this.epochDuration = epochDuration;
    }
}
