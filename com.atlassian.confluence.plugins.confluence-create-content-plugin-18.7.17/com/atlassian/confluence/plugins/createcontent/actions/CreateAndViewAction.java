/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.actions.AbstractCreateBlueprintPageAction;
import com.atlassian.confluence.util.GeneralUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class CreateAndViewAction
extends AbstractCreateBlueprintPageAction {
    private static final Logger log = LoggerFactory.getLogger(CreateAndViewAction.class);
    private Page indexPage;
    private static final String VIEW_INDEX_PAGE = "view-index";

    public void validate() {
        try {
            this.setPage((AbstractPage)this.populateBlueprintPage());
            this.validateDuplicatePageTitle();
            this.validatePageTitleAgainstIndexPageTitle();
        }
        catch (Exception e) {
            log.error("User tried to create a Blueprint that couldn't be loaded:", (Throwable)e);
            this.addActionError(this.getText("create.content.plugin.cannot.create.and.view"));
        }
        super.validate();
    }

    public void createPage() {
    }

    protected String beforeAdd() throws Exception {
        Page indexPage;
        String result = super.beforeAdd();
        if (!result.equals("success")) {
            return result;
        }
        this.indexPage = indexPage = this.getOrCreateIndexPage();
        Page parentPage = this.getFromPage();
        if (parentPage == null) {
            parentPage = indexPage;
        }
        if (parentPage != null) {
            parentPage.addChild((Page)this.getPage());
        }
        return "success";
    }

    protected String afterAdd() {
        String result = super.afterAdd();
        if (!result.equals("success")) {
            return result;
        }
        this.sendBlueprintPageCreateEvent((Page)this.getPage());
        if (this.getGoToIndexPage()) {
            return VIEW_INDEX_PAGE;
        }
        return "success";
    }

    public Page getIndexPage() {
        return this.indexPage;
    }

    public String getIndexPageUrl() {
        return GeneralUtil.getPageUrl((AbstractPage)this.getIndexPage());
    }
}

