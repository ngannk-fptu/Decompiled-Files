/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.spaces.SpaceManager
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.ia.impl.AbstractSidebarService;
import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.SpaceManager;

public class DefaultSidebarService
extends AbstractSidebarService
implements SidebarService {
    private final BandanaManager bandanaManager;

    public DefaultSidebarService(PermissionManager permissionManager, SpaceManager spaceManager, BandanaManager bandanaManager) {
        super(permissionManager, spaceManager);
        this.bandanaManager = bandanaManager;
    }

    @Override
    public String getOption(String spaceKey, String option) {
        return (String)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "sidebar." + option);
    }

    @Override
    public void setOption(String spaceKey, String option, String value) throws NotPermittedException {
        this.checkPermissions(spaceKey);
        this.forceSetOption(spaceKey, option, value);
    }

    @Override
    public void forceSetOption(String spaceKey, String option, String value) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "sidebar." + option, (Object)value);
    }
}

