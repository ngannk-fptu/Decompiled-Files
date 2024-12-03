/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.configuration.CacheConfiguration;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.repository.RepositoryIdentifier;
import java.util.Set;

public interface RepositoryConfiguration {
    public RepositoryIdentifier getIdentifier();

    public void addComponent(String var1, Object var2);

    public Object getComponent(String var1);

    public String getStringComponent(String var1);

    public boolean hasComponent(String var1);

    public String getComponentClassName(String var1);

    public RepositoryAccessor configure() throws ConfigurationException;

    public boolean hasClassForComponent(String var1);

    public Set getComponentNames();

    public void setCacheConfiguration(CacheConfiguration var1);

    public boolean isCachingEnabled();

    public CacheConfiguration getCacheConfiguration();
}

