package me.maxwell.tools.jms_bridge;

import me.maxwell.tools.jms_bridge.common.ActiveMQClient;
import me.maxwell.tools.jms_bridge.common.ActiveMQClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Maxwell.Lee
 * @date 2018-12-12 10:32
 * @since 1.0.0
 */
public class GeneralMessageBridge implements Runnable {

    private static final Logger     log = LoggerFactory.getLogger(GeneralMessageBridge.class);

    private MessageBridgeBean   bean;

    private final AtomicLong lastHeartBeatTime = new AtomicLong(0);

    public GeneralMessageBridge(MessageBridgeBean bean) {
        if (bean.getDescription() == null) {
            bean.setDescription(String.format("Bridge(%s => %s)", bean.getSrcName(), bean.getDstName()));
        }

        this.bean = bean;
    }

    public boolean checkAlive() {
        long lt = lastHeartBeatTime.get();

        return (System.currentTimeMillis() - lt < bean.getDelayTimeOnError() * 2);
    }

    @Override
    public void run() {
        int seqNo = 1;

        while (true) {
            int flag = launchOneEpoch(seqNo++);

            if (flag < 0) {
                log.info("[{}]等待{}毫秒，再运行。", bean.getDescription(), bean.getDelayTimeOnError());
                try {
                    Thread.sleep(bean.getDelayTimeOnError());
                } catch (InterruptedException e) {
                    log.info("[{}]收到停机信号，退出。", bean.getDescription());
                    return ;
                }
            } else {
                log.info("[{}]即将开始新得世代。", bean.getDescription());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.info("[{}]收到停机信号，退出。", bean.getDescription());
                    return ;
                }
            }
        }
    }

    public String   getDescription() {
        return bean.getDescription();
    }

    private int launchOneEpoch(int seqNo) {
        long bt = System.currentTimeMillis();

        ActiveMQClient srcClient = null;
        ActiveMQClient dstClient = null;

        try {
            log.info("[{}]第{}世代开始...", bean.getDescription(), seqNo);

            srcClient = new ActiveMQClientImpl(bean.getSrcName(), bean.getSrcUrl());
            dstClient = new ActiveMQClientImpl(bean.getDstName(), bean.getDstUrl());

            GeneralMessageForward forward;
            if (bean.getDstQueueClone() == null) {
                forward = new GeneralMessageForward(srcClient, bean.getSrcQueue(), dstClient, bean.getDstQueue(),
                                                    lastHeartBeatTime);
            } else {
                forward = new GeneralMessageForward(srcClient, bean.getSrcQueue(), dstClient, bean.getDstQueue(),
                                                    bean.getDstQueueClone(), lastHeartBeatTime);
            }

            int flag = forward.start(bean.getEpochDuration());
            String epochTimeDesc = getEpochTimeDesc(bt, System.currentTimeMillis());
            log.info("[{}]第{}世代结束, {}, flag={}。", bean.getDescription(), seqNo, epochTimeDesc, flag);

            return flag;
        } catch (Exception e) {
            String epochTimeDesc = getEpochTimeDesc(bt, System.currentTimeMillis());
            log.error("[{}]第{}世代异常结束， {}：", bean.getDescription(), seqNo, epochTimeDesc, e);
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
        return bean.getDelayTimeOnError();
    }

    public Long getEpochDuration() {
        return bean.getEpochDuration();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneralMessageBridge{");
        sb.append("bean=").append(bean);
        sb.append(", lastHeartBeatTime=").append(lastHeartBeatTime);
        sb.append('}');
        return sb.toString();
    }
}
