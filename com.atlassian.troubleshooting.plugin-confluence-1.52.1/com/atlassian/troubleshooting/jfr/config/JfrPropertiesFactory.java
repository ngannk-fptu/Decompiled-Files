/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.troubleshooting.jfr.config;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import com.atlassian.troubleshooting.jfr.config.JfrPropertyDefaults;
import com.atlassian.troubleshooting.jfr.config.JfrPropertyStore;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JfrPropertiesFactory {
    private static final String JFR_PROP_FILE = "jfr.properties";
    private static final String JFR_PROP_PRODUCT_SPECIFIC_FILE = "product-jfr.properties";
    private static final String JFR_ADDITIONAL_PROP_FILE = "jfr.config.location";
    private static final Logger LOGGER = LoggerFactory.getLogger(JfrPropertiesFactory.class);
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;
    private final JfrPropertyStore jfrPropertyStore;

    @Autowired
    JfrPropertiesFactory(PluginAccessor pluginAccessor, EventPublisher eventPublisher, JfrPropertyStore jfrPropertyStore) {
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
        this.jfrPropertyStore = jfrPropertyStore;
    }

    public JfrProperties create() {
        Properties properties = this.loadDefaultProperties(this.pluginAccessor);
        this.overrideDefaultPropertiesFromProduct(this.pluginAccessor, properties);
        this.overrideDefaultPropertiesFromEnv(properties);
        JfrPropertyDefaults defaultProperties = new JfrPropertyDefaults.Builder().maxAge(Long.parseLong(properties.getProperty(JfrProperty.MAX_AGE.getPropertyName()))).maxSize(Long.parseLong(properties.getProperty(JfrProperty.MAX_SIZE.getPropertyName()))).numberOfFilesToRemain(Integer.parseInt(properties.getProperty(JfrProperty.FILE_TO_REMAIN.getPropertyName()))).recordingPath(properties.getProperty(JfrProperty.RECORDING_PATH.getPropertyName())).threadDumpPath(properties.getProperty(JfrProperty.THREAD_DUMP_PATH.getPropertyName())).dumpCronExpression(properties.getProperty(JfrProperty.DUMP_CRON_EXPRESSION.getPropertyName())).jfrTemplatePath(properties.getProperty(JfrProperty.JFR_TEMPLATE_PATH.getPropertyName())).build();
        return new JfrProperties(defaultProperties, this.eventPublisher, this.jfrPropertyStore);
    }

    private Properties loadDefaultProperties(PluginAccessor pluginAccessor) {
        Properties properties = new Properties();
        try (InputStream propertiesStream = this.getPropertiesStream(pluginAccessor, JFR_PROP_FILE);){
            properties.load(propertiesStream);
        }
        catch (Exception e) {
            throw new JfrException("Failed to load jfr properties!", e);
        }
        return properties;
    }

    private InputStream getPropertiesStream(PluginAccessor pluginAccessor, String propertiesFile) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return Optional.ofNullable(classLoader.getResourceAsStream(propertiesFile)).orElse(pluginAccessor.getDynamicResourceAsStream(propertiesFile));
    }

    private void overrideDefaultPropertiesFromProduct(PluginAccessor pluginAccessor, Properties properties) {
        try (InputStream propertiesStream = this.getPropertiesStream(pluginAccessor, JFR_PROP_PRODUCT_SPECIFIC_FILE);){
            properties.load(propertiesStream);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void overrideDefaultPropertiesFromEnv(Properties properties) {
        String jfrPropertyAdditionalLocation = System.getProperty(JFR_ADDITIONAL_PROP_FILE);
        if (jfrPropertyAdditionalLocation != null) {
            try (InputStream is = Files.newInputStream(Paths.get(jfrPropertyAdditionalLocation, new String[0]), new OpenOption[0]);){
                properties.load(is);
            }
            catch (IOException e) {
                LOGGER.error("Failed to load additional jfr properties!", (Throwable)e);
            }
        }
        Arrays.stream(JfrProperty.values()).forEach(property -> {
            String propertyName = property.getPropertyName();
            String propertyValue = System.getProperty(propertyName);
            if (propertyValue != null) {
                properties.put(propertyName, propertyValue);
                property.setOverridden(true);
            }
        });
    }
}

