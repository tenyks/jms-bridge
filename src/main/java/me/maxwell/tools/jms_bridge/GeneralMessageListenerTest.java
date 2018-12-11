package me.maxwell.tools.jms_bridge;


import org.junit.Test;

public class GeneralMessageListenerTest {

    private GeneralMessageBridge listener = new GeneralMessageBridge(ClientFactory.getSrcClient(), "demo.src",
            ClientFactory.getDstClient(), "demo.dst");

    @Test
    public void trans() {
        listener.handle();
    }

}