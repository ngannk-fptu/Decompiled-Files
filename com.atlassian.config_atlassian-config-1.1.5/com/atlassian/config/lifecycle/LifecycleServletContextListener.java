/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package com.atlassian.config.lifecycle;

import com.atlassian.config.lifecycle.LifecycleManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.spring.container.ContainerManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LifecycleServletContextListener
implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (this.shouldRunLifecycle()) {
            this.getLifecycleManager().startUp(servletContextEvent.getServletContext());
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (this.shouldRunLifecycle()) {
            this.getLifecycleManager().shutDown(servletContextEvent.getServletContext());
        }
    }

    private LifecycleManager getLifecycleManager() {
        return (LifecycleManager)ContainerManager.getComponent((String)"lifecycleManager");
    }

    private boolean shouldRunLifecycle() {
        return ContainerManager.isContainerSetup() && BootstrapUtils.getBootstrapManager().isSetupComplete();
    }
}

