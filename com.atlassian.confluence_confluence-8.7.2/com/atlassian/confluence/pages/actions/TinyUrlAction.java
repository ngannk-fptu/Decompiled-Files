/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyUrlAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(TinyUrlAction.class);
    private PageManager pageManager;
    private String urlPath;
    private String urlIdentifier;
    private static final String NOT_FOUND = "pagenotfound";

    public void setUrlIdentifier(String urlIdentifier) {
        this.urlIdentifier = urlIdentifier;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws IOException {
        if (StringUtils.isBlank((CharSequence)this.urlIdentifier)) {
            return NOT_FOUND;
        }
        try {
            TinyUrl url = new TinyUrl(this.urlIdentifier);
            AbstractPage page = this.pageManager.getAbstractPage(url.getPageId());
            if (page == null) {
                return NOT_FOUND;
            }
            this.urlPath = this.getUrlPath(page);
            return "success";
        }
        catch (Exception e) {
            log.error("Unable to retrieve page from TinyUrl: " + e.getMessage(), (Throwable)e);
            return "error";
        }
    }

    private String getUrlPath(AbstractPage page) {
        if (this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, page)) {
            return page.getUrlPath();
        }
        return "/pages/viewpage.action?pageId=" + page.getId();
    }

    public String getUrlPath() {
        return this.urlPath;
    }
}

