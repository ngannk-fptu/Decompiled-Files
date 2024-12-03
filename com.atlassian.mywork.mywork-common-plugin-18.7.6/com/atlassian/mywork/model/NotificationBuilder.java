/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.model;

import com.atlassian.mywork.model.ApplicationLinkIdBuilder;
import com.atlassian.mywork.model.Item;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.util.JsonHelper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class NotificationBuilder
implements ApplicationLinkIdBuilder<NotificationBuilder> {
    private long id;
    private String applicationLinkId;
    private String user;
    private String iconUrl;
    private String title;
    private String description;
    private String url;
    private String action;
    private String actionIconUrl;
    private long created;
    private long updated;
    private Status status;
    private boolean read;
    private boolean pinned;
    private String groupingId;
    private String globalId;
    private String itemIconUrl;
    private String itemTitle;
    private String itemUrl;
    private String application;
    private String entity;
    private ObjectNode metadata;

    public NotificationBuilder() {
    }

    public NotificationBuilder(Notification notification) {
        this.id = notification.getId();
        this.applicationLinkId = notification.getApplicationLinkId();
        this.user = notification.getUser();
        this.iconUrl = notification.getIconUrl();
        this.title = notification.getTitle();
        this.description = notification.getDescription();
        this.url = notification.getUrl();
        this.action = notification.getAction();
        this.actionIconUrl = notification.getActionIconUrl();
        this.created = notification.getCreated();
        this.updated = notification.getUpdated();
        this.status = notification.getStatus();
        this.read = notification.isRead();
        this.pinned = notification.isPinned();
        this.groupingId = notification.getGroupingId();
        this.globalId = notification.getGlobalId();
        this.metadata = notification.getMetadata();
        this.itemIconUrl = notification.getItem().getIconUrl();
        this.itemTitle = notification.getItem().getTitle();
        this.itemUrl = notification.getItem().getUrl();
        this.application = notification.getApplication();
        this.entity = notification.getEntity();
    }

    public NotificationBuilder id(long id) {
        this.id = id;
        return this;
    }

    @Override
    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    @Override
    public NotificationBuilder applicationLinkId(String applicationLinkId) {
        this.applicationLinkId = applicationLinkId;
        return this;
    }

    public NotificationBuilder user(String user) {
        this.user = user;
        return this;
    }

    public NotificationBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public NotificationBuilder title(String title) {
        this.title = title;
        return this;
    }

    public NotificationBuilder description(String description) {
        this.description = description;
        return this;
    }

    public NotificationBuilder url(String url) {
        this.url = url;
        return this;
    }

    public NotificationBuilder action(String action) {
        this.action = action;
        return this;
    }

    public NotificationBuilder actionIconUrl(String actionIconUrl) {
        this.actionIconUrl = actionIconUrl;
        return this;
    }

    public NotificationBuilder created(long created) {
        this.created = created;
        return this;
    }

    public NotificationBuilder updated(long updated) {
        this.updated = updated;
        return this;
    }

    public NotificationBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public NotificationBuilder read(boolean read) {
        this.read = read;
        return this;
    }

    public NotificationBuilder pinned(boolean pinned) {
        this.pinned = pinned;
        return this;
    }

    public NotificationBuilder groupingId(String groupingId) {
        this.groupingId = groupingId;
        return this;
    }

    public NotificationBuilder globalId(String globalId) {
        this.globalId = globalId;
        return this;
    }

    public NotificationBuilder itemIconUrl(String itemIconUrl) {
        this.itemIconUrl = itemIconUrl;
        return this;
    }

    public NotificationBuilder itemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
        return this;
    }

    public NotificationBuilder itemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
        return this;
    }

    public NotificationBuilder application(String application) {
        this.application = application;
        return this;
    }

    public NotificationBuilder entity(String entity) {
        this.entity = entity;
        return this;
    }

    public NotificationBuilder metadata(ObjectNode metadata) {
        this.metadata = metadata;
        return this;
    }

    public NotificationBuilder metadata(String jsonMetadata) {
        this.metadata = JsonHelper.parseObject(jsonMetadata);
        return this;
    }

    public Notification createNotification() {
        ObjectNode metadata = this.metadata != null ? JsonHelper.copy(this.metadata) : JsonNodeFactory.instance.objectNode();
        Item item = new Item(this.itemIconUrl, this.itemTitle, this.itemUrl);
        return new Notification(this.id, this.applicationLinkId, this.user, this.iconUrl, this.title, this.description, this.url, this.application, this.entity, this.action, this.actionIconUrl, this.created, this.updated, this.status, this.read, this.pinned, this.groupingId, this.globalId, metadata, item);
    }
}

