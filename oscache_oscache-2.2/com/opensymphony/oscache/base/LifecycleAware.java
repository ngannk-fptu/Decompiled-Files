/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.FinalizationException;
import com.opensymphony.oscache.base.InitializationException;

public interface LifecycleAware {
    public void initialize(Cache var1, Config var2) throws InitializationException;

    public void finialize() throws FinalizationException;
}

