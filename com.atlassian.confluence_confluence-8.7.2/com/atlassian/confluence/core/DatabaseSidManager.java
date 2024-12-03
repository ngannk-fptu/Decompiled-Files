/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.ConfigurationException
 */
package com.atlassian.confluence.core;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;

public class DatabaseSidManager
implements ConfluenceSidManager {
    private BandanaManager bandanaManager;
    private ConfluenceSidManager bootstrapSidManager;
    public static final String CONFLUENCE_SERVER_ID = "confluence.server.id";

    @Override
    public void initSid() throws ConfigurationException {
        if (this.isSidSet()) {
            throw new ConfigurationException("Server ID already initialised");
        }
        if (!this.bootstrapSidManager.isSidSet()) {
            throw new ConfigurationException("No SID found from setup");
        }
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), CONFLUENCE_SERVER_ID, (Object)this.bootstrapSidManager.getSid());
    }

    @Override
    public String getSid() throws ConfigurationException {
        return (String)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), CONFLUENCE_SERVER_ID);
    }

    @Override
    public boolean isSidSet() throws ConfigurationException {
        return this.getSid() != null;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setBootstrapSidManager(ConfluenceSidManager bootstrapSidManager) {
        this.bootstrapSidManager = bootstrapSidManager;
    }
}

