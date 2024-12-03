/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.setup.SidUtils;
import com.google.common.base.Preconditions;

public class BootstrapSidManager
implements ConfluenceSidManager {
    public static final String CONFLUENCE_SETUP_SERVER_ID = "confluence.setup.server.id";
    private final SidUtils sidUtils = new SidUtils();
    private final ApplicationConfiguration applicationConfig;

    public BootstrapSidManager(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = (ApplicationConfiguration)Preconditions.checkNotNull((Object)applicationConfig);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void initSid() throws ConfigurationException {
        if (this.isSidSet()) {
            throw new ConfigurationException("Server ID already initialised");
        }
        String sid = this.sidUtils.generateSID();
        ApplicationConfiguration applicationConfiguration = this.applicationConfig;
        synchronized (applicationConfiguration) {
            this.applicationConfig.setProperty((Object)CONFLUENCE_SETUP_SERVER_ID, (Object)sid);
            this.applicationConfig.save();
        }
    }

    @Override
    public String getSid() throws ConfigurationException {
        return (String)this.applicationConfig.getProperty((Object)CONFLUENCE_SETUP_SERVER_ID);
    }

    @Override
    public boolean isSidSet() throws ConfigurationException {
        return this.getSid() != null;
    }
}

