/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.ImmutableList
 *  com.opensymphony.xwork2.config.ConfigurationManager
 *  com.opensymphony.xwork2.config.RuntimeConfiguration
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.dispatcher.mapper.ActionMapping
 *  org.apache.struts2.dispatcher.mapper.DefaultActionMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence;

import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.ConfluenceActionResolver;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableList;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Struts2ActionResolver
implements ConfluenceActionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Struts2ActionResolver.class);
    static final List<String> REQUIRED_STRUTS2_CLASSES = ImmutableList.of((Object)"com.opensymphony.xwork2.config.ConfigurationManager", (Object)"org.apache.struts2.dispatcher.mapper.DefaultActionMapper");
    private final DefaultActionMapper actionMapper;
    private final ConfigurationManager configManager;

    Struts2ActionResolver() {
        this(Struts2ActionResolver.lookupConfigurationManager());
    }

    Struts2ActionResolver(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.actionMapper = new DefaultActionMapper();
    }

    @Override
    public Optional<String> getActionConfigClassName(HttpServletRequest httpRequest) {
        ActionMapping mapping = this.actionMapper.getMapping(httpRequest, this.configManager);
        return this.getActionConfig(this.configManager, mapping).map(ActionConfig::getClassName);
    }

    private Optional<ActionConfig> getActionConfig(ConfigurationManager configurationManager, ActionMapping mapping) {
        try {
            RuntimeConfiguration config = configurationManager.getConfiguration().getRuntimeConfiguration();
            return Optional.ofNullable(config.getActionConfig(mapping.getNamespace(), mapping.getName()));
        }
        catch (Exception e) {
            LOGGER.debug("Failed to load Struts 2 ActionConfig", (Throwable)e);
            return Optional.empty();
        }
    }

    private static ConfigurationManager lookupConfigurationManager() {
        return (ConfigurationManager)ContainerManager.getComponent((String)"strutsConfigurationManager", ConfigurationManager.class);
    }
}

