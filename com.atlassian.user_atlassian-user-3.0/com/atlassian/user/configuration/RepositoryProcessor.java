/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;

public interface RepositoryProcessor {
    public RepositoryAccessor process(RepositoryConfiguration var1) throws ConfigurationException;
}

