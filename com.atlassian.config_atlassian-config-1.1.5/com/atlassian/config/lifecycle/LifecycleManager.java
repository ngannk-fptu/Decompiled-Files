/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.atlassian.config.lifecycle;

import javax.servlet.ServletContext;

public interface LifecycleManager {
    public void startUp(ServletContext var1);

    public void shutDown(ServletContext var1);

    public boolean isStartedUp();
}

