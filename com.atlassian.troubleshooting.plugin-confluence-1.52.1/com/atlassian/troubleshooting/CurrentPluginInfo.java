/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CurrentPluginInfo
implements PluginInfo {
    private static final String PLUGIN_PROP_FILE = "troubleshooting-plugin.properties";
    private static final String PLUGINKEY_PROP = "troubleshooting.pluginKey";
    private static final String VERSION_PROP = "troubleshooting.pluginVersion";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentPluginInfo.class);
    private final Supplier<Properties> troubleshootingProps = Suppliers.memoize(() -> {
        Properties properties = new Properties();
        try (InputStream propertiesStream = this.getPropertiesStream(pluginAccessor);){
            properties.load(propertiesStream);
        }
        catch (Exception e) {
            LOGGER.error("Failed to load troubleshooting properties!", (Throwable)e);
        }
        return properties;
    });

    @Autowired
    public CurrentPluginInfo(PluginAccessor pluginAccessor) {
    }

    private InputStream getPropertiesStream(PluginAccessor pluginAccessor) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return Optional.ofNullable(classLoader.getResourceAsStream(PLUGIN_PROP_FILE)).orElse(pluginAccessor.getDynamicResourceAsStream(PLUGIN_PROP_FILE));
    }

    @Override
    public String getPluginKey() {
        return ((Properties)this.troubleshootingProps.get()).getProperty(PLUGINKEY_PROP);
    }

    @Override
    public String getPluginVersion() {
        return ((Properties)this.troubleshootingProps.get()).getProperty(VERSION_PROP);
    }
}

