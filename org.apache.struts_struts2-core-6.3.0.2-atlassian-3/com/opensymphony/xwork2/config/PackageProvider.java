/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;

public interface PackageProvider {
    public void init(Configuration var1) throws ConfigurationException;

    public boolean needsReload();

    public void loadPackages() throws ConfigurationException;
}

