/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config;

import com.atlassian.config.ConfigurationException;

public interface ConfigElement<T, C> {
    public String getPropertyName();

    public void setPropertyName(String var1);

    public Class<T> getObjectClass();

    public C getContext();

    public void setContext(C var1);

    public void save(T var1) throws ConfigurationException;

    public T load() throws ConfigurationException;
}

