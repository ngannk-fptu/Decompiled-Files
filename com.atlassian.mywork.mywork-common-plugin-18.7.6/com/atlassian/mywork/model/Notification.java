/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.model;

import com.atlassian.mywork.model.Item;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.rest.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Notification
implements JsonObject {
    @JsonProperty
    private long id;
    @JsonProperty
    private String applicationLinkId;
    @JsonIgnore
    private String user;
    @JsonProperty
    private String iconUrl;
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private String url;
    @JsonProperty
    private String application;
    @JsonProperty
    private String entity;
    @JsonProperty
    private String action;
    @JsonProperty
    private String actionIconUrl;
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
    @JsonProperty
    private String groupingId;
    @JsonProperty
    private String globalId;
    @JsonProperty
    private ObjectNode metadata;
    @JsonProperty
    private Item item;

    private Notification() {
        this(0L, null, null, null, null, null, null, null, null, null, null, 0L, 0L, null, false, false, null, null, null, null);
    }

    public Notification(long id, String applicationLinkId, String user, String iconUrl, String title, String description, String url, String application, String entity, String action, String actionIconUrl, long created, long updated, Status status, boolean read, boolean pinned, String groupingId, String globalId, ObjectNode metadata, Item item) {
        this.id = id;
        this.applicationLinkId = applicationLinkId;
        this.user = user;
        this.iconUrl = iconUrl;
        this.title = title;
        this.description = description;
        this.url = url;
        this.application = application;
        this.entity = entity;
        this.action = action;
        this.actionIconUrl = actionIconUrl;
        this.created = created;
        this.updated = updated;
        this.status = status != null ? status : Status.DONE;
        this.read = read;
        this.pinned = pinned;
        this.groupingId = groupingId;
        this.globalId = globalId;
        this.metadata = metadata != null ? metadata : JsonNodeFactory.instance.objectNode();
        this.item = item != null ? item : new Item(null, null, null);
    }

    public long getId() {
        return this.id;
    }

    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    public String getUser() {
        return this.user;
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

    public String getUrl() {
        return this.url;
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

    public String getActionIconUrl() {
        return this.actionIconUrl;
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

    public String getGroupingId() {
        return this.groupingId;
    }

    public String getGlobalId() {
        return this.globalId;
    }

    public ObjectNode getMetadata() {
        return this.metadata;
    }

    public Item getItem() {
        return this.item;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals((Object)this, (Object)o, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }
}

