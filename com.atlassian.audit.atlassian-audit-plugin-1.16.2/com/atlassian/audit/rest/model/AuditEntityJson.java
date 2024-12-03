/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.rest.model.AuditAttributeJson;
import com.atlassian.audit.rest.model.AuditAuthorJson;
import com.atlassian.audit.rest.model.AuditResourceJson;
import com.atlassian.audit.rest.model.AuditTypeJson;
import com.atlassian.audit.rest.model.ChangedValueJson;
import com.atlassian.audit.rest.model.converter.ZonedDateTimeDeserializer;
import com.atlassian.audit.rest.model.converter.ZonedDateTimeSerializer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditEntityJson {
    @Nonnull
    private final ZonedDateTime timestamp;
    @Nonnull
    private final AuditAuthorJson author;
    @Nonnull
    private final AuditTypeJson type;
    @Nonnull
    private final List<AuditResourceJson> affectedObjects;
    @Nonnull
    private final List<ChangedValueJson> changedValues;
    @Nullable
    private final String source;
    @Nullable
    private final String system;
    @Nullable
    private final String node;
    @Nonnull
    private final String method;
    @Nonnull
    private final List<AuditAttributeJson> extraAttributes;

    @JsonCreator
    public AuditEntityJson(@JsonProperty(value="timestamp") @JsonDeserialize(using=ZonedDateTimeDeserializer.class) @Nonnull ZonedDateTime timestamp, @JsonProperty(value="author") @Nonnull AuditAuthorJson author, @JsonProperty(value="type") @Nonnull AuditTypeJson type, @JsonProperty(value="affectedObjects") @Nonnull List<AuditResourceJson> affectedObjects, @JsonProperty(value="changedValues") @Nonnull List<ChangedValueJson> changedValues, @JsonProperty(value="source") @Nullable String source, @JsonProperty(value="system") @Nullable String system, @JsonProperty(value="node") @Nullable String node, @JsonProperty(value="method") @Nonnull String method, @JsonProperty(value="extraAttributes") @Nonnull List<AuditAttributeJson> extraAttributes) {
        this.timestamp = timestamp;
        this.author = author;
        this.type = type;
        this.affectedObjects = affectedObjects;
        this.changedValues = changedValues;
        this.source = source;
        this.system = system;
        this.node = node;
        this.method = method;
        this.extraAttributes = extraAttributes;
    }

    @Nonnull
    @JsonProperty(value="timestamp")
    @JsonSerialize(using=ZonedDateTimeSerializer.class)
    public ZonedDateTime getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    @JsonProperty(value="author")
    public AuditAuthorJson getAuthor() {
        return this.author;
    }

    @Nonnull
    @JsonProperty(value="type")
    public AuditTypeJson getType() {
        return this.type;
    }

    @Nonnull
    @JsonProperty(value="affectedObjects")
    public List<AuditResourceJson> getAffectedObjects() {
        return this.affectedObjects;
    }

    @Nonnull
    @JsonProperty(value="changedValues")
    public List<ChangedValueJson> getChangedValues() {
        return this.changedValues;
    }

    @Nullable
    @JsonProperty(value="source")
    public String getSource() {
        return this.source;
    }

    @Nullable
    @JsonProperty(value="system")
    public String getSystem() {
        return this.system;
    }

    @Nullable
    @JsonProperty(value="node")
    public String getNode() {
        return this.node;
    }

    @Nonnull
    @JsonProperty(value="method")
    public String getMethod() {
        return this.method;
    }

    @Nonnull
    @JsonProperty(value="extraAttributes")
    public List<AuditAttributeJson> getExtraAttributes() {
        return this.extraAttributes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditEntityJson that = (AuditEntityJson)o;
        return Objects.equals(this.timestamp, that.timestamp) && Objects.equals(this.author, that.author) && Objects.equals(this.type, that.type) && Objects.equals(this.affectedObjects, that.affectedObjects) && Objects.equals(this.changedValues, that.changedValues) && Objects.equals(this.source, that.source) && Objects.equals(this.system, that.system) && Objects.equals(this.node, that.node) && Objects.equals(this.method, that.method) && Objects.equals(this.extraAttributes, that.extraAttributes);
    }

    public int hashCode() {
        return Objects.hash(this.timestamp, this.author, this.type, this.affectedObjects, this.changedValues, this.source, this.system, this.node, this.method, this.extraAttributes);
    }

    public String toString() {
        return "AuditEntityJson{timestamp=" + this.timestamp + ", author=" + this.author + ", type='" + this.type + '\'' + ", affectedObjects=" + this.affectedObjects + ", changedValues=" + this.changedValues + ", source='" + this.source + '\'' + ", system='" + this.system + '\'' + ", node='" + this.node + '\'' + ", method='" + this.method + '\'' + ", extraAttributes=" + this.extraAttributes + '}';
    }
}

