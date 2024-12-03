/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.JohnsonEventContainer
 *  javax.servlet.ServletContext
 */
package com.atlassian.config.lifecycle;

import com.atlassian.johnson.JohnsonEventContainer;
import javax.servlet.ServletContext;

public interface LifecycleContext {
    public ServletContext getServletContext();

    public JohnsonEventContainer getAgentJohnson();
}

