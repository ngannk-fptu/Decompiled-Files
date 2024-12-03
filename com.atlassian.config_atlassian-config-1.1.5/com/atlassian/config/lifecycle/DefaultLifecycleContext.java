/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.JohnsonEventContainer
 *  javax.servlet.ServletContext
 */
package com.atlassian.config.lifecycle;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import javax.servlet.ServletContext;

public class DefaultLifecycleContext
implements LifecycleContext {
    private final ServletContext servletContext;
    private final JohnsonEventContainer johnsonEventContainer;

    public DefaultLifecycleContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.johnsonEventContainer = Johnson.getEventContainer((ServletContext)servletContext);
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public JohnsonEventContainer getAgentJohnson() {
        return this.johnsonEventContainer;
    }
}

