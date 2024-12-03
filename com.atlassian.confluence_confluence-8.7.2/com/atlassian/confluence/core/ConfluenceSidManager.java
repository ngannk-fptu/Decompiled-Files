/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 */
package com.atlassian.confluence.core;

import com.atlassian.config.ConfigurationException;

public interface ConfluenceSidManager {
    public void initSid() throws ConfigurationException;

    public String getSid() throws ConfigurationException;

    public boolean isSidSet() throws ConfigurationException;
}

