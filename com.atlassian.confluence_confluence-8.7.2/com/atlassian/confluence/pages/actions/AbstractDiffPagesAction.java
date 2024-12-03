/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.diff.InterruptedDiffException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.TinyUrlAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.user.User;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDiffPagesAction
extends AbstractPageAwareAction
implements TinyUrlAware {
    private static Logger log = LoggerFactory.getLogger(AbstractDiffPagesAction.class);
    protected AbstractPage originalPage;
    protected String diff;
    protected PageManager pageManager;
    private Differ differ;
    private List<VersionHistorySummary> previousVersions;

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public List<VersionHistorySummary> getPreviousVersions() {
        if (this.previousVersions == null) {
            this.previousVersions = this.pageManager.getVersionHistorySummaries(this.getPage());
            this.previousVersions.remove(0);
        }
        return this.previousVersions;
    }

    public boolean isRevertPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.getPage());
    }

    public abstract AbstractPage getOriginalPage();

    public AbstractPage getRevisedPage() {
        return this.getPage();
    }

    public AbstractPage getLatestVersion() {
        return this.getRevisedPage().getLatestVersion();
    }

    @HtmlSafe
    public String getDiff() {
        return this.diff;
    }

    public String execute() throws Exception {
        try {
            this.diff = this.differ.diff(this.getOriginalPage(), this.getRevisedPage());
        }
        catch (InterruptedDiffException ide) {
            this.addActionError("diff.pages.error.diffing.timeout", TimeUnit.SECONDS.convert(ide.getTimeout(), TimeUnit.MILLISECONDS));
            if (log.isDebugEnabled()) {
                log.debug("Error while generating diff: (" + this.getOriginalPage() + " vs " + this.getRevisedPage() + ")", (Throwable)ide);
            } else {
                log.warn("Error while generating diff: (" + this.getOriginalPage() + " vs " + this.getRevisedPage() + ")");
            }
            return "error";
        }
        catch (DiffException e) {
            this.addActionError("diff.pages.error.diffing", new Object[0]);
            log.error("Error while generating diff: (" + this.getOriginalPage() + " vs " + this.getRevisedPage() + ")", (Throwable)e);
            return "error";
        }
        return "success";
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public boolean isPermitted() {
        return super.isPermitted() && this.originalPageIsNullOrPermitted();
    }

    private boolean originalPageIsNullOrPermitted() {
        return this.getOriginalPage() == null || this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getOriginalPage().getLatestVersion());
    }

    public long getPageIdOfVersionAfter(AbstractPage page) {
        ContentEntityObject entity = this.pageManager.getNextVersion(page);
        return entity == null ? -1L : entity.getId();
    }

    public long getPageIdOfVersionBefore(AbstractPage page) {
        ContentEntityObject entity = this.pageManager.getPreviousVersion(page);
        return entity == null ? -1L : entity.getId();
    }

    public boolean hasPreviousVersion(AbstractPage page) {
        try {
            return this.getPageIdOfVersionBefore(page) > 0L;
        }
        catch (Exception e) {
            log.error("Error retrieving version of page previous to: " + page, (Throwable)e);
            return false;
        }
    }

    public boolean hasNextVersion(AbstractPage page) {
        try {
            return this.getPageIdOfVersionAfter(page) > 0L;
        }
        catch (Exception e) {
            log.error("Error retrieving version of page after: " + page, (Throwable)e);
            return false;
        }
    }

    @Override
    public String getTinyUrl() {
        if (this.getPage() == null) {
            return null;
        }
        return new TinyUrl(this.getPage()).getIdentifier();
    }

    public void setHtmlDiffer(Differ differ) {
        this.differ = differ;
    }
}

