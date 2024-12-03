/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.mywork.model.NotificationBuilder
 *  com.atlassian.mywork.util.GlobalIdFactory
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.util.GlobalIdFactory;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;

public class FieldHelper {
    static final String APP_CONFLUENCE = FieldHelper.class.getPackage().getName();
    private final InternalHostApplication internalHostApplication;
    private final UserAccessor userAccessor;

    public FieldHelper(InternalHostApplication internalHostApplication, UserAccessor userAccessor) {
        this.internalHostApplication = internalHostApplication;
        this.userAccessor = userAccessor;
    }

    public NotificationBuilder buildNotification(ContentEntityObject content) {
        return this.buildNotification(content, (User)this.getUser().getOrNull());
    }

    public NotificationBuilder buildNotification(ContentEntityObject content, User fromUser) {
        String url = content.getUrlPath();
        String gravatarUrl = this.createGravatarUrl(fromUser);
        return new NotificationBuilder().applicationLinkId(this.internalHostApplication.getId().get()).globalId(this.createGlobalId(FieldHelper.getContentType(content), content.getId())).application(APP_CONFLUENCE).entity(FieldHelper.getContentType(content)).itemTitle(content.getDisplayTitle()).iconUrl(gravatarUrl).itemUrl(url).url(url);
    }

    public String createGravatarUrl(User user) {
        return this.userAccessor.getUserProfilePicture(user).getDownloadPath();
    }

    public static String getContentTypeDescription(ContentEntityObject content) {
        String contentTypeDescription;
        switch (content.getTypeEnum()) {
            default: {
                contentTypeDescription = "a page";
                break;
            }
            case COMMENT: {
                contentTypeDescription = "a comment";
                break;
            }
            case BLOG: {
                contentTypeDescription = "a blog post";
            }
        }
        return contentTypeDescription;
    }

    public static String getContentType(ContentEntityObject content) {
        String contentType;
        switch (content.getTypeEnum()) {
            default: {
                contentType = "page";
                break;
            }
            case COMMENT: {
                contentType = "comment";
                break;
            }
            case BLOG: {
                contentType = "blog";
            }
        }
        return contentType;
    }

    public String getHostId() {
        return this.internalHostApplication.getId().get();
    }

    public Option<ConfluenceUser> getUser() {
        return Option.option((Object)AuthenticatedUserThreadLocal.get());
    }

    String createGlobalId(String entity, Long id) {
        return GlobalIdFactory.encode((List)ImmutableList.of((Object)"appId", (Object)"entity", (Object)"id"), (Map)ImmutableMap.of((Object)"appId", (Object)this.internalHostApplication.getId().get(), (Object)"entity", (Object)entity, (Object)"id", (Object)id.toString()));
    }
}

