/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.mywork.rest;

import com.atlassian.mywork.model.Item;
import com.atlassian.mywork.model.Notification;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonNotificationItem {
    @JsonProperty
    private String iconUrl;
    @JsonProperty
    private String title;
    @JsonProperty
    private String url;
    @JsonProperty
    private String applicationLinkId;
    @JsonProperty
    private String application;
    @JsonProperty
    private String entity;
    @JsonProperty
    private String action;
    @JsonProperty
    private boolean pinned;
    @JsonProperty
    private String groupingId;
    @JsonProperty
    private String aggregateKey;

    private JsonNotificationItem() {
    }

    public JsonNotificationItem(Notification notification, String aggregateKey) {
        Item item = notification.getItem();
        this.iconUrl = item.getIconUrl();
        this.title = item.getTitle();
        this.url = item.getUrl();
        this.applicationLinkId = notification.getApplicationLinkId();
        this.application = notification.getApplication();
        this.entity = notification.getEntity();
        this.action = notification.getAction();
        this.pinned = notification.isPinned();
        this.groupingId = notification.getGroupingId();
        this.aggregateKey = aggregateKey;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }

    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    public String getApplication() {
        return this.application;
    }

    public String getEntity() {
        return this.entity;
    }

    public String getAction() {
        return this.action;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public String getGroupingId() {
        return this.groupingId;
    }

    public String getAggregateKey() {
        return this.aggregateKey;
    }
}

