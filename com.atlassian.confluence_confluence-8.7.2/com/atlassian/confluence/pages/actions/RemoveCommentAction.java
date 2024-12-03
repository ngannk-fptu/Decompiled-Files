/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.validation.ApiBackedActionHelper;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class RemoveCommentAction
extends AbstractPageAwareAction {
    private ContentService contentService;
    private ValidationResult validationResult;
    private long commentId;
    private String confirm;

    public long getCommentId() {
        return this.commentId;
    }

    @Override
    public void validate() {
        new ApiBackedActionHelper(this.getValidationResult()).addValidationErrors(this);
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        if (this.confirm == null || !this.confirm.equals("yes") && !this.confirm.equals("OK")) {
            return "notconfirmed";
        }
        this.contentService.delete(this.getContent());
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.getValidationResult().isAuthorized();
    }

    private ValidationResult getValidationResult() {
        if (this.validationResult == null) {
            this.validationResult = this.contentService.validator().validateDelete(this.getContent());
        }
        return this.validationResult;
    }

    private Content getContent() {
        return Content.builder((ContentType)ContentType.COMMENT, (long)this.commentId).build();
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public void setApiContentService(ContentService contentService) {
        this.contentService = contentService;
    }
}

