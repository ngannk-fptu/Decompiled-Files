/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.user.history.UserHistoryHelper;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;

@RequiresAnyConfluenceAccess
public class ViewUserHistoryAction
extends ConfluenceActionSupport {
    private ContentEntityManager contentEntityManager;
    private List<ContentEntityObject> historyPages;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public List<ContentEntityObject> getHistory() {
        if (this.historyPages == null) {
            UserHistoryHelper userHistoryHelper = new UserHistoryHelper(this.getAuthenticatedUser(), this.contentEntityManager, this.permissionManager);
            this.historyPages = userHistoryHelper.getHistoryContent(new ContentTypeEnum[0]);
        }
        return this.historyPages;
    }
}

