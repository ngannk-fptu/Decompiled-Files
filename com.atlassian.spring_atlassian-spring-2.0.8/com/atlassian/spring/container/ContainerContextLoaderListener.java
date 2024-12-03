/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContextEvent
 *  org.springframework.web.context.ContextLoaderListener
 */
package com.atlassian.spring.container;

import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.SpringContainerContext;
import javax.servlet.ServletContextEvent;
import org.springframework.web.context.ContextLoaderListener;

public class ContainerContextLoaderListener
extends ContextLoaderListener {
    public void contextInitialized(ServletContextEvent event) {
        if (this.canInitialiseContainer()) {
            super.contextInitialized(event);
            this.postInitialiseContext(event);
        }
        SpringContainerContext springCC = this.getNewSpringContainerContext();
        springCC.setServletContext(event.getServletContext());
        ContainerManager.getInstance().setContainerContext(springCC);
        springCC.contextReloaded();
    }

    protected void postInitialiseContext(ServletContextEvent event) {
    }

    protected SpringContainerContext getNewSpringContainerContext() {
        return new SpringContainerContext();
    }

    public boolean canInitialiseContainer() {
        return true;
    }
}

