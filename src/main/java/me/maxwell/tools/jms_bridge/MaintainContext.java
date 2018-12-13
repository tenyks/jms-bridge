package me.maxwell.tools.jms_bridge;

import java.util.List;

/**
 * @author Maxwell.Lee
 * @date 2018-12-12 12:00
 * @since 1.0.0
 */
public class MaintainContext {

    private List<GeneralMessageBridge> bridges;

    public MaintainContext(List<GeneralMessageBridge> bridges) {
        this.bridges = bridges;
    }

    public List<GeneralMessageBridge> getBridges() {
        return bridges;
    }

}
