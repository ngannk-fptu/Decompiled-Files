/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.content.service.BlogPostService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractPageAction;
import com.atlassian.confluence.pages.exceptions.ExternalChangesException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@ReadOnlyAccessBlocked
public class RevertPageBackToVersionAction
extends AbstractPageAction {
    private int version;
    private AbstractPage pageToRevert;
    private ServiceCommand serviceCommand;
    private PageService pageService;
    private BlogPostService blogPostService;
    private boolean isRevertTitle = true;
    private String revertComment;

    private ServiceCommand getServiceCommand() {
        if (this.serviceCommand == null) {
            if (this.getPage() instanceof Page) {
                this.serviceCommand = this.pageService.newRevertPageCommand(this.pageService.getIdPageLocator(this.getPageId()), this.version, this.revertComment, this.isRevertTitle);
            } else if (this.getPage() instanceof BlogPost) {
                this.serviceCommand = this.blogPostService.newRevertBlogPostCommand(this.blogPostService.getIdBlogPostLocator(this.getPageId()), this.version, this.revertComment, this.isRevertTitle);
            }
        }
        return this.serviceCommand;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.isRevertTitle = !this.isTitleConflict();
        return "success";
    }

    public String doRevert() {
        try {
            this.getServiceCommand().execute();
            return "success";
        }
        catch (ExternalChangesException e) {
            return "notpermitted";
        }
    }

    private boolean isTitleConflict() {
        this.getServiceCommand().isValid();
        return this.getServiceCommand().getValidationErrors().size() == 1 && this.getServiceCommand().getValidationErrors().toArray(new ValidationError[0])[0].getMessageKey().equals("reverting.entity.back.produces.title.conflict");
    }

    @Override
    public void validate() {
        this.getServiceCommand().isValid();
        for (ValidationError error : this.getServiceCommand().getValidationErrors()) {
            if (error.getMessageKey().equals("reverting.entity.back.produces.title.conflict")) continue;
            this.addActionError(error.getMessageKey(), error.getArgs());
        }
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isRevertTitle() {
        return this.isRevertTitle;
    }

    public void setRevertTitle(boolean isRevertTitle) {
        this.isRevertTitle = isRevertTitle;
    }

    private AbstractPage getPageToRevert() {
        if (this.pageToRevert == null) {
            this.pageToRevert = this.pageManager.getPageByVersion(this.getPage(), this.version);
        }
        return this.pageToRevert;
    }

    public String getTitleToRevert() {
        return this.getPageToRevert().getTitle();
    }

    @Override
    public boolean isPermitted() {
        return this.getServiceCommand().isAuthorized();
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    public void setBlogPostService(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    public void setRevertComment(String revertComment) {
        this.revertComment = revertComment;
    }

    public String getDefaultComment() {
        return this.getI18n().getText("revert.content.comment", new Object[]{Integer.toString(this.version)});
    }
}

