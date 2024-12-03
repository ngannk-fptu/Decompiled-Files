/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.zaxxer.hikari.HikariConfig
 *  org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator
 */
package org.hibernate.hikaricp.internal;

import com.zaxxer.hikari.HikariConfig;
import java.util.Map;
import java.util.Properties;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;

public class HikariConfigurationUtil {
    public static final String CONFIG_PREFIX = "hibernate.hikari.";

    public static HikariConfig loadConfiguration(Map props) {
        Properties hikariProps = new Properties();
        HikariConfigurationUtil.copyProperty("hibernate.connection.autocommit", props, "autoCommit", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.driver_class", props, "driverClassName", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.url", props, "jdbcUrl", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.username", props, "username", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.password", props, "password", hikariProps);
        HikariConfigurationUtil.copyIsolationSetting(props, hikariProps);
        for (Object keyo : props.keySet()) {
            String key;
            if (!(keyo instanceof String) || !(key = (String)keyo).startsWith(CONFIG_PREFIX)) continue;
            hikariProps.setProperty(key.substring(CONFIG_PREFIX.length()), (String)props.get(key));
        }
        return new HikariConfig(hikariProps);
    }

    private static void copyProperty(String srcKey, Map src, String dstKey, Properties dst) {
        if (src.containsKey(srcKey)) {
            dst.setProperty(dstKey, (String)src.get(srcKey));
        }
    }

    private static void copyIsolationSetting(Map props, Properties hikariProps) {
        Integer isolation = ConnectionProviderInitiator.extractIsolation((Map)props);
        if (isolation != null) {
            hikariProps.put("transactionIsolation", ConnectionProviderInitiator.toIsolationConnectionConstantName((Integer)isolation));
        }
    }
}

