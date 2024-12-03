/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.diff.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.diff.beans.ConfluenceDiffDraftBean;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class ViewDiffOfDraftAction
extends AbstractPageAwareAction
implements Beanable {
    private ConfluenceDiffDraftBean bean;
    private DraftManager draftManager;
    private String username;
    private Draft draft;
    private Differ htmlDiffer;

    @Override
    public void validate() {
        if (this.getDraft() == null) {
            this.addActionError("No draft could be found");
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.bean = new ConfluenceDiffDraftBean(this.getPage(), this.getDraft(), this.htmlDiffer);
        return "success";
    }

    public Draft getDraft() {
        if (this.draft == null) {
            this.draft = this.draftManager.findDraft(this.getPageId(), this.getUsername(), this.getPage().getType(), this.getSpaceKey());
        }
        return this.draft;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public ConfluenceDiffDraftBean getBean() {
        return this.bean;
    }

    public void setDraftManager(DraftManager draftManager) {
        this.draftManager = draftManager;
    }

    public String getUsername() {
        ConfluenceUser user = this.userAccessor.getUserByName(this.username);
        return user != null ? user.getName() : null;
    }

    public void setHtmlDiffer(Differ htmlDiffer) {
        this.htmlDiffer = htmlDiffer;
    }
}

