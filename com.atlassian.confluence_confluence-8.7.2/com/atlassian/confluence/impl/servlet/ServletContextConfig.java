/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.security.core.session.SessionRegistry
 *  org.springframework.security.core.session.SessionRegistryImpl
 */
package com.atlassian.confluence.impl.servlet;

import com.atlassian.confluence.servlet.simpledisplay.DefaultPathConverterManager;
import com.atlassian.confluence.servlet.simpledisplay.PathConverterManager;
import com.atlassian.confluence.servlet.simpledisplay.SimpleDisplayServlet;
import com.atlassian.plugin.spring.AvailableToPlugins;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
public class ServletContextConfig {
    @Bean
    @AvailableToPlugins
    PathConverterManager pathConverterManager() {
        return new DefaultPathConverterManager();
    }

    @Bean
    SimpleDisplayServlet simpleDisplayServlet() {
        SimpleDisplayServlet bean = new SimpleDisplayServlet();
        bean.setPathConverterManager(this.pathConverterManager());
        return bean;
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}

