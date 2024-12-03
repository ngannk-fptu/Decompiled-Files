/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.content.render.xhtml.storage.Summariser;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AlternativePagesLocator;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.beans.PageReference;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class PageNotFoundAction
extends ConfluenceActionSupport
implements Spaced {
    private String spaceKey;
    private String title;
    private Space space;
    private SpaceManager spaceManager;
    private PageManager pageManager;
    private AlternativePagesLocator pageLocator;
    private Summariser xhtmlSummariser;
    private ThemeManager themeManager;

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }

    @Override
    public Space getSpace() {
        if (this.space == null && StringUtils.isNotEmpty((CharSequence)this.spaceKey)) {
            this.space = this.spaceManager.getSpace(this.spaceKey);
        }
        return this.space;
    }

    @PermittedMethods(value={HttpMethod.ANY_METHOD})
    @XsrfProtectionExcluded
    public String execute() throws IOException {
        if (StringUtils.isBlank((CharSequence)this.getTitle())) {
            PageReference reference = PageReference.get(ServletActionContext.getRequest());
            if (reference != null) {
                this.setTitle(reference.getPageTitle());
                this.setSpaceKey(reference.getSpaceKey());
            } else {
                return "error";
            }
        }
        this.pageLocator = new AlternativePagesLocator(this.pageManager, this.permissionManager, this.getSpace(), this.getTitle());
        if (!this.pageLocator.hasAlternatives()) {
            return "error";
        }
        ServletActionContext.getResponse().setStatus(404);
        return "success";
    }

    public List<AbstractPage> getPossibleAlternativesElsewhere() {
        return this.pageLocator.getPagesInOtherSpaces();
    }

    public List<AbstractPage> getPossibleAlternativesInSpace() {
        return this.pageLocator.getRenamedPagesInSpace();
    }

    public List<AbstractPage> getPossibleAlternativesInTrash() {
        return this.pageLocator.getPossiblesInTrash();
    }

    public List<AbstractPage> getPossibleAlternativesInSpaceSearch() {
        return this.pageLocator.getPagesWithSimilarTitleInSpace();
    }

    public boolean isSpaceAdminUser() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.space);
    }

    public String stripToPlainText(String html) {
        return this.xhtmlSummariser.summarise(html);
    }

    public void setXhtmlSummariser(Summariser xhtmlSummariser) {
        this.xhtmlSummariser = xhtmlSummariser;
    }
}

