/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.mywork.rest;

import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.rest.JsonObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonGroupNotification
implements JsonObject {
    @JsonProperty
    private long id;
    @JsonProperty
    private String iconUrl;
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private String actionIconUrl;
    @JsonProperty
    private String url;
    @JsonProperty
    private String globalId;
    @JsonProperty
    private JsonNode metadata;
    @JsonProperty
    private long created;
    @JsonProperty
    private long updated;
    @JsonProperty
    private Status status;
    @JsonProperty
    private boolean read;
    @JsonProperty
    private boolean pinned;

    private JsonGroupNotification() {
    }

    public JsonGroupNotification(String aggregateKey, Notification notification) {
        this.id = notification.getId();
        this.iconUrl = notification.getIconUrl();
        this.title = notification.getTitle();
        this.description = notification.getDescription();
        this.actionIconUrl = notification.getActionIconUrl();
        this.created = notification.getCreated();
        this.updated = notification.getUpdated();
        this.status = notification.getStatus();
        this.read = notification.isRead();
        this.pinned = notification.isPinned();
        this.url = notification.getUrl();
        this.globalId = notification.getGlobalId();
        this.metadata = notification.getMetadata();
    }

    public long getId() {
        return this.id;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getActionIconUrl() {
        return this.actionIconUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public String getGlobalId() {
        return this.globalId;
    }

    public JsonNode getMetadata() {
        return this.metadata;
    }

    public long getCreated() {
        return this.created;
    }

    public long getUpdated() {
        return this.updated;
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean isRead() {
        return this.read;
    }

    public boolean isPinned() {
        return this.pinned;
    }
}

