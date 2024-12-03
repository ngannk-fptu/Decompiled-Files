/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.requestaccess.events;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.requestaccess.resource.PageRestrictionResource;
import com.atlassian.confluence.user.ConfluenceUser;

public abstract class AbstractAccessEvent {
    private final ConfluenceUser sourceUser;
    private final ConfluenceUser targetUser;
    private final ContentEntityObject content;
    private final PageRestrictionResource.AccessType accessType;
    private final String spaceKey;

    public AbstractAccessEvent(ConfluenceUser sourceUser, ConfluenceUser targetUser, ContentEntityObject content, PageRestrictionResource.AccessType accessType, String spaceKey) {
        this.sourceUser = sourceUser;
        this.targetUser = targetUser;
        this.content = content;
        this.accessType = accessType;
        this.spaceKey = spaceKey;
    }

    public ConfluenceUser getSourceUser() {
        return this.sourceUser;
    }

    public ConfluenceUser getTargetUser() {
        return this.targetUser;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public PageRestrictionResource.AccessType getAccessType() {
        return this.accessType;
    }

    public abstract String getUserRole();

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

