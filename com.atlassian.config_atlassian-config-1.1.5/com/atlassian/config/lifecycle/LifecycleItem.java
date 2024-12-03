/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.lifecycle;

import com.atlassian.config.lifecycle.LifecycleContext;

public interface LifecycleItem {
    public void startup(LifecycleContext var1) throws Exception;

    public void shutdown(LifecycleContext var1) throws Exception;
}

