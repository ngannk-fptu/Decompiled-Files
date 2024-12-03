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
public class Task
implements JsonObject {
    @JsonProperty
    private long id;
    @JsonProperty
    private String applicationLinkId;
    @JsonProperty
    private String title;
    @JsonIgnore
    private String user;
    @JsonProperty
    private String notes;
    @JsonProperty
    private Status status;
    @JsonProperty
    private String application;
    @JsonProperty
    private String entity;
    @JsonProperty
    private long created;
    @JsonProperty
    private long updated;
    @JsonProperty
    private String globalId;
    @JsonProperty
    private ObjectNode metadata;
    @JsonProperty
    private Item item;

    private Task() {
        this(0L, null, null, null, null, null, null, null, 0L, 0L, null, null, null);
    }

    public Task(long id, String applicationLinkId, String title, String user, String notes, Status status, String application, String entity, long created, long updated, String globalId, ObjectNode metadata, Item item) {
        this.id = id;
        this.applicationLinkId = applicationLinkId;
        this.title = title;
        this.user = user;
        this.notes = notes;
        this.status = status != null ? status : Status.TODO;
        this.application = application;
        this.entity = entity;
        this.created = created;
        this.updated = updated;
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

    public String getTitle() {
        return this.title;
    }

    public String getUser() {
        return this.user;
    }

    public String getNotes() {
        return this.notes;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getApplication() {
        return this.application;
    }

    public String getEntity() {
        return this.entity;
    }

    public long getCreated() {
        return this.created;
    }

    public long getUpdated() {
        return this.updated;
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

