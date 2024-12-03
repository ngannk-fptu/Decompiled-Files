/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.plugins.like.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import org.apache.struts2.ServletActionContext;

@RequiresAnyConfluenceAccess
@ReadOnlyAccessBlocked
public class LikeAction
extends ConfluenceActionSupport {
    private LikeManager likeManager;
    private ContentEntityManager contentEntityManager;
    private long contentId;
    private String url;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() {
        ContentEntityObject content = this.contentEntityManager.getById(this.contentId);
        if (content == null) {
            ServletActionContext.getResponse().setStatus(404);
            return "error";
        }
        this.likeManager.addLike(content, (User)AuthenticatedUserThreadLocal.get());
        this.url = content.getUrlPath();
        if (!(content instanceof Comment)) {
            this.url = this.url + "#like-section";
        }
        return "success";
    }

    public String getRedirectUrl() {
        return this.url;
    }

    public void setLikeManager(LikeManager likeManager) {
        this.likeManager = likeManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }
}

