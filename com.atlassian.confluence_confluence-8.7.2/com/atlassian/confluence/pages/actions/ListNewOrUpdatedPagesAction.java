/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.ContentFacade;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Date;
import java.util.List;

public class ListNewOrUpdatedPagesAction
extends ConfluenceActionSupport {
    private ContentFacade contentFacade;
    private List pagesCreatedOrUpdatedSinceLastLogin;

    public List getPagesCreatedOrUpdatedSinceLastLogin() {
        return this.pagesCreatedOrUpdatedSinceLastLogin;
    }

    public void setPagesCreatedOrUpdatedSinceLastLogin(List pagesCreatedOrUpdatedSinceLastLogin) {
        this.pagesCreatedOrUpdatedSinceLastLogin = pagesCreatedOrUpdatedSinceLastLogin;
    }

    public void setContentFacade(ContentFacade contentFacade) {
        this.contentFacade = contentFacade;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.getAuthenticatedUser() == null) {
            return "loginrequired";
        }
        Date previousLoginDate = this.getPreviousLoginDate();
        this.pagesCreatedOrUpdatedSinceLastLogin = this.contentFacade.getRecentlyUpdatedPagesForUserSince(this.getAuthenticatedUser(), previousLoginDate);
        return super.execute();
    }
}

