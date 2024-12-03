/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth.consumer.spring;

import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeans {
    @Bean
    public ConsumerService consumerService() {
        return (ConsumerService)OsgiServices.importOsgiService(ConsumerService.class);
    }

    @Bean
    public TemplateRenderer templateRenderer() {
        return (TemplateRenderer)OsgiServices.importOsgiService(TemplateRenderer.class);
    }
}

