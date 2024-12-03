/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config;

import com.atlassian.config.ConfigurationException;
import java.io.InputStream;

public interface ConfigurationPersister {
    public void addConfigMapping(Class<?> var1, Class<?> var2);

    public void save(String var1, String var2) throws ConfigurationException;

    public Object load(String var1, String var2) throws ConfigurationException;

    public Object load(InputStream var1) throws ConfigurationException;

    public void addConfigElement(Object var1, String var2) throws ConfigurationException;

    public Object getConfigElement(Class var1, String var2) throws ConfigurationException;

    public String getStringConfigElement(String var1) throws ConfigurationException;

    public void clear();
}

