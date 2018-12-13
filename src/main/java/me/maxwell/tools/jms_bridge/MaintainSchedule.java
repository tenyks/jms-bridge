package me.maxwell.tools.jms_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Maxwell.Lee
 * @date 2018-12-12 11:37
 * @since 1.0.0
 */
@Component
public class MaintainSchedule {

    private static final Logger log = LoggerFactory.getLogger(MaintainSchedule.class);

    @Autowired @Qualifier("maintainContext")
    private MaintainContext context;

    private List<BridgeRunningContext>  brCtxList;

    @PostConstruct
    public void initMethod() {
        log.info("[Maintain]运维调动初始化开始...");

        if (brCtxList != null) return ;

        if (context == null) return ;

        if (context.getBridges() != null) {
            brCtxList = new ArrayList<>();
            for (GeneralMessageBridge bridge : context.getBridges()) {
                brCtxList.add(new BridgeRunningContext(bridge));
            }
        }

        log.info("[Maintain]运维调动初始化结束.");
    }

    @Scheduled(fixedDelay = 10000)
    public void maintain() {
        if (brCtxList == null) {
            log.warn("[Maintain]BridgeRunningContext为空，执行结束。");
            return ;
        }

        for (BridgeRunningContext brCtx : brCtxList) {
            if (!brCtx.isLaunched()) {
                log.info("[Maintain]启动{}", brCtx.getDescription());
                brCtx.launch();
            } else {
                if (brCtx.checkAlive()) {
                    log.info("[Maintain]{}自检通过。", brCtx.getDescription());
                } else {
                    log.warn("[Maintain]{}自检不通过。", brCtx.getDescription());
                }
            }
        }
    }

    private static class BridgeRunningContext {

        private Thread  thread;

        private GeneralMessageBridge    bridge;

        private AtomicBoolean isLaunched;

        public BridgeRunningContext(GeneralMessageBridge bridge) {
            this.bridge = bridge;
            this.thread = new Thread(bridge);
            this.isLaunched = new AtomicBoolean(false);
        }

        public boolean isLaunched() {
            return isLaunched.get();
        }

        public void launch() {
            thread.start();
            isLaunched.set(true);
        }

        public boolean checkAlive() {
            return thread.isAlive() && bridge.checkAlive();
        }

        public String getDescription() {
            return bridge.getDescription();
        }
    }

}
