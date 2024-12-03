/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

public interface SingletonBeanRegistry {
    public void registerSingleton(String var1, Object var2);

    @Nullable
    public Object getSingleton(String var1);

    public boolean containsSingleton(String var1);

    public String[] getSingletonNames();

    public int getSingletonCount();

    public Object getSingletonMutex();
}

