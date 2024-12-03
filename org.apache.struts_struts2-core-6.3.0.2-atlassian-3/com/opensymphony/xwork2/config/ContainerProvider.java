/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public interface ContainerProvider {
    public void destroy();

    public void init(Configuration var1) throws ConfigurationException;

    public boolean needsReload();

    public void register(ContainerBuilder var1, LocatableProperties var2) throws ConfigurationException;
}

