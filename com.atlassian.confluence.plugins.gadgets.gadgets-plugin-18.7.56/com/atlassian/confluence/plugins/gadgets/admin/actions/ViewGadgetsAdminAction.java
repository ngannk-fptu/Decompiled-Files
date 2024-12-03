/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.plugins.gadgets.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;

public class ViewGadgetsAdminAction
extends ConfluenceActionSupport {
    private List<String> installedGadgets = null;
    private List<SubscribedGadgetFeed> installedGadgetFeeds = null;
    protected ExternalGadgetSpecStore clusterSafeGadgetDirectoryStore;
    protected SubscribedGadgetFeedStore subscribedGadgetFeedStore;
    protected String activeTab;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public List<String> getInstalledGadgets() {
        if (this.installedGadgets == null) {
            this.installedGadgets = new ArrayList<String>();
            for (ExternalGadgetSpec externalGadgetSpec : this.clusterSafeGadgetDirectoryStore.entries()) {
                this.installedGadgets.add(externalGadgetSpec.getSpecUri().toString());
            }
        }
        return this.installedGadgets;
    }

    public List<SubscribedGadgetFeed> getInstalledGadgetFeeds() {
        if (this.installedGadgetFeeds == null) {
            this.installedGadgetFeeds = new ArrayList<SubscribedGadgetFeed>();
            Iterables.addAll(this.installedGadgetFeeds, (Iterable)this.subscribedGadgetFeedStore.getAllFeeds());
        }
        return this.installedGadgetFeeds;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public void setClusterSafeGadgetDirectoryStore(ExternalGadgetSpecStore clusterSafeGadgetDirectoryStore) {
        this.clusterSafeGadgetDirectoryStore = clusterSafeGadgetDirectoryStore;
    }

    public void setSubscribedGadgetFeedStore(SubscribedGadgetFeedStore subscribedGadgetFeedStore) {
        this.subscribedGadgetFeedStore = subscribedGadgetFeedStore;
    }

    public void setActiveTab(String activeTab) {
        this.activeTab = activeTab;
    }

    public String getActiveTab() {
        return this.activeTab;
    }
}

