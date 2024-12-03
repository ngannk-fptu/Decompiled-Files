/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;

public class Notification
extends ConfluenceEntityObject {
    private ConfluenceUser receiver;
    private Space space;
    private boolean digest;
    private ContentTypeEnum type;
    private boolean network;
    private ContentEntityObject content;
    private Label label;

    public ConfluenceUser getReceiver() {
        return this.receiver;
    }

    public void setReceiver(ConfluenceUser receiver) {
        this.receiver = receiver;
    }

    public Space getSpace() {
        return this.space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public boolean isUserChange() {
        if (this.receiver == null) {
            throw new IllegalStateException("Username cannot be null");
        }
        if (this.content != null) {
            if (this.content.isNew()) {
                return this.receiver.equals(this.content.getCreator());
            }
            return this.receiver.equals(this.content.getLastModifier());
        }
        if (this.space != null) {
            if (this.space.getCreationDate().equals(this.space.getLastModificationDate())) {
                return this.receiver.equals(this.space.getCreator());
            }
            return this.receiver.equals(this.space.getLastModifier());
        }
        throw new IllegalStateException("Page and Space cannot be null");
    }

    public boolean isPageNotification() {
        return this.getContent() instanceof AbstractPage;
    }

    public boolean isContentNotification() {
        return this.getContent() != null;
    }

    public boolean isSpaceNotification() {
        return !this.isContentNotification() && this.getSpace() != null;
    }

    public boolean isDigest() {
        return this.digest;
    }

    public void setDigest(boolean digest) {
        this.digest = digest;
    }

    public ContentTypeEnum getType() {
        return this.type;
    }

    public void setType(ContentTypeEnum type) {
        this.type = type;
    }

    public boolean isNetworkNotification() {
        return this.network;
    }

    @Deprecated
    public boolean isNetwork() {
        return this.isNetworkNotification();
    }

    public void setNetwork(boolean network) {
        this.network = network;
    }

    public boolean matchesContentType(AbstractPage page) {
        return this.type == null || this.type == page.getTypeEnum();
    }

    public WatchType getWatchType() {
        if (this.type == ContentTypeEnum.BLOG && this.content == null) {
            return this.space != null ? WatchType.SPACE_BLOGS : WatchType.SITE_BLOGS;
        }
        if (this.isPageNotification()) {
            return WatchType.SINGLE_PAGE;
        }
        if (this.isSpaceNotification()) {
            return WatchType.SPACE_ALL;
        }
        if (this.isNetworkNotification()) {
            return WatchType.NETWORK;
        }
        return null;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public void setContent(ContentEntityObject content) {
        this.content = content;
    }

    public Label getLabel() {
        return this.label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public String toString() {
        return "Notification (" + this.getId() + "): " + this.receiver + " for label=" + this.label + ", network=" + this.network + ", space=" + this.space + ", content=" + this.content + ", type=" + this.type + ", digest=" + this.digest;
    }

    public static enum WatchType {
        SPACE_BLOGS,
        SPACE_ALL,
        SITE_BLOGS,
        SINGLE_PAGE,
        NETWORK;

    }
}

