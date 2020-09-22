package me.maxwell.tools.jms_bridge.config;

import com.alibaba.fastjson.TypeReference;
import me.maxwell.tools.jms_bridge.GeneralMessageBridge;
import me.maxwell.tools.jms_bridge.MessageBridgeBean;
import me.maxwell.tools.jms_bridge.common.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxwell.Lee
 * @version 1.0.0
 * @date 2020/9/21 16:42
 */
@Configuration
public class SpringBeanConfig {

    @Bean(name = "maintainContext")
    public MaintainContext getConfigContext() {
        List<MessageBridgeBean> bean = loadConfig();

        return new MaintainContext(buildMessageBridges(bean));
    }

    private List<GeneralMessageBridge> buildMessageBridges(List<MessageBridgeBean> beans) {
        if (beans == null || beans.isEmpty()) return null;

        List<GeneralMessageBridge> rst = new ArrayList<>();

        for (MessageBridgeBean bean : beans) {
            rst.add(new GeneralMessageBridge(bean));
        }

        return rst;
    }

    private List<MessageBridgeBean> loadConfig() {
        String confJsonTxt = readFromConfigFile();

        TypeReference<List<MessageBridgeBean>> type = new TypeReference<List<MessageBridgeBean>>() {};
        return JsonUtils.parseFromStr(confJsonTxt, type);
    }

    private String readFromConfigFile() {
        String file = System.getProperty("configFile");

        try {
            InputStream is;
            if (file != null) {
                is = new FileInputStream(file);
            } else {
                is = SpringBeanConfig.class.getClassLoader().getResourceAsStream("config.json");

                if (is == null) return null;
            }

            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            char[] buffer = new char[4096 * 100];
            int count = IOUtils.read(reader, buffer, 0, buffer.length);

            return new String(buffer, 0, count);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("文件不存在：%s", file), e);
        } catch (IOException e) {
            throw new RuntimeException("从配置文件读取配置失败：", e);
        }
    }

}
