package me.maxwell.tools.jms_bridge;

/**
 * @author Maxwell.Lee
 * @version 1.0.0
 * @company Scho Techonlogy Co. Ltd
 * @date 2020/9/21 17:50
 */
public class MessageBridgeBean {

    /**
     * 源端的名称；
     */
    private String   srcName;

    /**
     * 源端的JMS服务器连接URL；
     */
    private String   srcUrl;

    /**
     * 源端的JMS队列编码；
     */
    private String   srcQueue;

    /**
     * 目标端的名称；
     */
    private String   dstName;

    /**
     * 目标端的JMS服务器连接URL；
     */
    private String   dstUrl;

    /**
     * 目标端的JMS队列编码；
     */
    private String   dstQueue;

    /**
     * 目标端的JMS Clone队列编码；
     */
    private String   dstQueueClone;

    /**
     * 备注或描述；
     */
    private String   description;

    /**
     * 遇到错误时的暂停时长，单位：毫秒；
     */
    private Long    delayTimeOnError = 30 * 1000L;//30秒；

    /**
     * 每个时代的时长，单位：毫秒；
     */
    private Long    epochDuration = 5 * 60 * 1000L;//5分钟；

    public String   checkRequiredFields() {
        return null;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getSrcQueue() {
        return srcQueue;
    }

    public void setSrcQueue(String srcQueue) {
        this.srcQueue = srcQueue;
    }

    public String getDstName() {
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;
    }

    public String getDstUrl() {
        return dstUrl;
    }

    public void setDstUrl(String dstUrl) {
        this.dstUrl = dstUrl;
    }

    public String getDstQueue() {
        return dstQueue;
    }

    public void setDstQueue(String dstQueue) {
        this.dstQueue = dstQueue;
    }

    public String getDstQueueClone() {
        return dstQueueClone;
    }

    public void setDstQueueClone(String dstQueueClone) {
        this.dstQueueClone = dstQueueClone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageBridgeBean{");
        sb.append("srcName='").append(srcName).append('\'');
        sb.append(", srcUrl='").append(srcUrl).append('\'');
        sb.append(", srcQueue='").append(srcQueue).append('\'');
        sb.append(", dstName='").append(dstName).append('\'');
        sb.append(", dstUrl='").append(dstUrl).append('\'');
        sb.append(", dstQueue='").append(dstQueue).append('\'');
        sb.append(", dstQueueClone='").append(dstQueueClone).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", delayTimeOnError=").append(delayTimeOnError);
        sb.append(", epochDuration=").append(epochDuration);
        sb.append('}');
        return sb.toString();
    }
}
