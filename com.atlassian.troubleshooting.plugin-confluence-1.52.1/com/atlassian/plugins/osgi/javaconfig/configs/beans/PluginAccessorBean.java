/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.PluginAccessor
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.plugins.osgi.javaconfig.configs.beans;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@PublicApi
public class PluginAccessorBean {
    @Bean
    public PluginAccessor pluginAccessor() {
        return OsgiServices.importOsgiService(PluginAccessor.class);
    }
}

