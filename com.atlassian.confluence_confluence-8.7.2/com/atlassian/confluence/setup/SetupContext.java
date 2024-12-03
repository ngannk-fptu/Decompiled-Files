/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.plugin.Plugin
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.plugin.ConfluencePluginManager;
import com.atlassian.confluence.setup.ConfluenceAnnotationConfigApplicationContext;
import com.atlassian.confluence.setup.SetupAppConfig;
import com.atlassian.plugin.Plugin;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SetupContext {
    private static final Logger logger = LoggerFactory.getLogger(SetupContext.class);
    private static AnnotationConfigApplicationContext context;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void init(ServletContext servletContext) {
        Class<SetupContext> clazz = SetupContext.class;
        synchronized (SetupContext.class) {
            if (context != null) {
                throw new IllegalStateException("Setup context has already been initialised");
            }
            context = new ConfluenceAnnotationConfigApplicationContext(BootstrapUtils.getBootstrapContext(), servletContext);
            context.register(new Class[]{SetupAppConfig.class});
            context.setAllowBeanDefinitionOverriding(false);
            context.refresh();
            ConfluencePluginManager pluginManager = (ConfluencePluginManager)((Object)context.getBean("pluginController", ConfluencePluginManager.class));
            pluginManager.init();
            SetupContext.loadExtraJerseyClass(pluginManager);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private static void loadExtraJerseyClass(ConfluencePluginManager pluginManager) {
        try {
            logger.debug("Trying to preload WriterUtil in atlassian-rest-module plugin class loader to prevent ClassNotFoundException when upload file");
            Plugin restModule = pluginManager.getEnabledPlugin("com.atlassian.plugins.rest.atlassian-rest-module");
            restModule.getClassLoader().loadClass("com.sun.jersey.core.impl.provider.header.WriterUtil");
        }
        catch (Exception e) {
            logger.error("Cannot preload WriterUtil in atlassian-rest-module plugin class loader", (Throwable)e);
        }
    }

    public static boolean isAvailable() {
        return context != null;
    }

    public static ConfigurableApplicationContext get() {
        return context;
    }

    public static void destroy() {
        if (context != null) {
            context.close();
        }
        context = null;
    }
}

