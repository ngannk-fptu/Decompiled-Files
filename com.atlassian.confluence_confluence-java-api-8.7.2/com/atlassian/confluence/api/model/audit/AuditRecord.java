/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.api.model.audit;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.audit.AffectedObject;
import com.atlassian.confluence.api.model.audit.ChangedValue;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.serialization.DateTimeLongDeserializer;
import com.atlassian.confluence.api.serialization.DateTimeLongSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@Deprecated
public final class AuditRecord {
    @JsonProperty
    private final User author;
    @JsonProperty
    private final String remoteAddress;
    @JsonDeserialize(using=DateTimeLongDeserializer.class)
    @JsonSerialize(using=DateTimeLongSerializer.class)
    @JsonProperty
    private final DateTime creationDate;
    @JsonProperty
    private final String summary;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final String category;
    @JsonProperty
    private final boolean sysAdmin;
    @JsonProperty
    private final AffectedObject affectedObject;
    @JsonProperty
    private final List<ChangedValue> changedValues;
    @JsonProperty
    private final Set<AffectedObject> associatedObjects;

    @JsonCreator
    private AuditRecord() {
        this(AuditRecord.builder());
    }

    private AuditRecord(Builder builder) {
        this.author = builder.author;
        this.remoteAddress = builder.remoteAddress;
        this.creationDate = builder.createdDate;
        this.summary = builder.summary;
        this.description = builder.description;
        this.category = builder.category;
        this.affectedObject = builder.affectedObject;
        this.changedValues = builder.changedValues;
        this.associatedObjects = builder.associatedObjects;
        this.sysAdmin = builder.isSysAdmin;
    }

    public User getAuthor() {
        return this.author;
    }

    public DateTime getCreationDate() {
        return this.creationDate;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCategory() {
        return this.category;
    }

    public AffectedObject getAffectedObject() {
        return this.affectedObject;
    }

    public List<ChangedValue> getChangedValues() {
        return this.changedValues;
    }

    public Set<AffectedObject> getAssociatedObjects() {
        return this.associatedObjects;
    }

    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    public boolean isSysAdmin() {
        return this.sysAdmin;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuditRecord other) {
        return new Builder(other);
    }

    public boolean equals(Object other) {
        if (!(other instanceof AuditRecord)) {
            return false;
        }
        AuditRecord auditRecord = (AuditRecord)other;
        return Objects.equals(auditRecord.author, this.author) && Objects.equals(auditRecord.remoteAddress, this.remoteAddress) && Objects.equals(auditRecord.creationDate, this.creationDate) && Objects.equals(auditRecord.summary, this.summary) && Objects.equals(auditRecord.description, this.description) && Objects.equals(auditRecord.category, this.category) && Objects.equals(auditRecord.affectedObject, this.affectedObject) && Objects.equals(auditRecord.changedValues, this.changedValues) && Objects.equals(auditRecord.associatedObjects, this.associatedObjects);
    }

    public int hashCode() {
        return Objects.hash(this.author, this.remoteAddress, this.creationDate, this.summary, this.description, this.category, this.affectedObject, this.changedValues, this.associatedObjects);
    }

    public static class Builder {
        private User author = new User(null, "", "", "");
        private String summary = "";
        private String category = "";
        private String remoteAddress;
        private String description;
        private AffectedObject affectedObject = AffectedObject.none();
        private boolean isSysAdmin = false;
        private DateTime createdDate = DateTime.now();
        private List<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        private Set<AffectedObject> associatedObjects = new HashSet<AffectedObject>();

        public Builder() {
        }

        public Builder(AuditRecord other) {
            this.author = other.getAuthor();
            this.affectedObject = other.getAffectedObject();
            this.associatedObjects = other.getAssociatedObjects();
            this.category = other.getCategory();
            this.changedValues = other.getChangedValues();
            this.createdDate = other.getCreationDate();
            this.description = other.getDescription();
            this.isSysAdmin = other.isSysAdmin();
            this.remoteAddress = other.getRemoteAddress();
            this.summary = other.getSummary();
        }

        public Builder author(User user) {
            this.author = user;
            return this;
        }

        public Builder remoteAddress(String remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        public Builder createdDate(DateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder affectedObject(@NonNull AffectedObject affectedObject) {
            this.affectedObject = affectedObject;
            return this;
        }

        public Builder changedValues(@NonNull List<ChangedValue> changedValues) {
            this.changedValues = changedValues;
            return this;
        }

        public Builder changedValue(@NonNull ChangedValue changedValue) {
            this.changedValues = new ArrayList<ChangedValue>(Collections.singleton(changedValue));
            return this;
        }

        public Builder associatedObjects(@NonNull Set<AffectedObject> associatedObjects) {
            this.associatedObjects = associatedObjects;
            return this;
        }

        public Builder associatedObject(@NonNull AffectedObject associatedObject) {
            this.associatedObjects = new HashSet<AffectedObject>(Collections.singleton(associatedObject));
            return this;
        }

        public Builder isSysAdmin(boolean sysAdmin) {
            this.isSysAdmin = sysAdmin;
            return this;
        }

        public AuditRecord build() {
            return new AuditRecord(this);
        }
    }
}

