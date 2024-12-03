/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.lifecycle.LifecycleManager;
import com.atlassian.confluence.impl.setup.DefaultLifecycleManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LifecycleManagerContextConfig {
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private PluginAccessor pluginAccessor;

    @Bean
    @AvailableToPlugins
    LifecycleManager lifecycleManager() {
        return new DefaultLifecycleManager(this.eventPublisher, this.pluginAccessor);
    }
}

