/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.TinyUrlAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.confluence.util.synchrony.SynchronyConfigurationReader;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;

@RequiresAnyConfluenceAccess
public class ViewPreviousVersionsAction
extends AbstractPageAwareAction
implements TinyUrlAware,
ContentDetailAction {
    private List<VersionHistorySummary> allVersions;
    private PageManager pageManager;
    private SynchronyConfigurationReader synchronyConfigurationReader;

    public List<VersionHistorySummary> getAllVersions() {
        return this.allVersions;
    }

    public boolean isOfflineCollabEditingMode() {
        return !this.synchronyConfigurationReader.isSharedDraftsEnabled();
    }

    public boolean isRevertPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.getPage());
    }

    public boolean isRemoveHistoricalVersionPermitted() {
        return !this.accessModeService.isReadOnlyAccessModeEnabled() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.getSpace());
    }

    public boolean isShowActions() {
        return true;
    }

    public List<VersionHistorySummary> getPreviousVersions() {
        return this.allVersions.subList(1, this.allVersions.size());
    }

    public Page getPreviousPage(Page page) {
        return (Page)this.pageManager.getPreviousVersion(page);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.allVersions = this.pageManager.getVersionHistorySummaries(this.getPage());
        return super.execute();
    }

    @Override
    public String getTinyUrl() {
        if (this.getPage() == null) {
            return null;
        }
        return new TinyUrl(this.getPage()).getIdentifier();
    }

    public void setSynchronyConfigurationReader(SynchronyConfigurationReader synchronyConfigurationReader) {
        this.synchronyConfigurationReader = synchronyConfigurationReader;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }
}

