/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.entity;

import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuditEntity {
    private static final String version = "1.0";
    private final Long id;
    private final Instant timestamp;
    private final AuditAuthor author;
    private final AuditType auditType;
    private final List<AuditResource> affectedObjects;
    private final List<ChangedValue> changedValues;
    private final String source;
    private final String system;
    private final String node;
    private final String method;
    private final Set<AuditAttribute> extraAttributes;

    private AuditEntity(Builder builder) {
        this.id = builder.id;
        this.timestamp = builder.timestamp;
        this.author = builder.author;
        this.auditType = builder.type;
        this.affectedObjects = Collections.unmodifiableList(builder.affectedObjects);
        this.changedValues = Collections.unmodifiableList(builder.changedValues);
        this.source = builder.source;
        this.system = builder.system;
        this.node = builder.node;
        this.method = builder.method;
        this.extraAttributes = Collections.unmodifiableSet(builder.extraAttributes);
    }

    @Nullable
    public Long getId() {
        return this.id;
    }

    @Nonnull
    public String getVersion() {
        return version;
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    public AuditAuthor getAuthor() {
        return this.author;
    }

    @Nonnull
    public AuditType getAuditType() {
        return this.auditType;
    }

    @Nonnull
    public List<AuditResource> getAffectedObjects() {
        return this.affectedObjects;
    }

    @Nonnull
    public List<ChangedValue> getChangedValues() {
        return this.changedValues;
    }

    @Nullable
    public String getSource() {
        return this.source;
    }

    @Nullable
    public String getSystem() {
        return this.system;
    }

    @Nullable
    public String getNode() {
        return this.node;
    }

    @Nullable
    public String getMethod() {
        return this.method;
    }

    @Nonnull
    public Collection<AuditAttribute> getExtraAttributes() {
        return this.extraAttributes;
    }

    public Optional<String> getExtraAttribute(@Nonnull String name) {
        Objects.requireNonNull(name);
        return this.extraAttributes.stream().filter(a -> name.equals(a.getName())).findFirst().map(AuditAttribute::getValue);
    }

    public Optional<String> getExtraAttributeByI18nKey(@Nonnull String i18nKey) {
        Objects.requireNonNull(i18nKey);
        return this.extraAttributes.stream().filter(a -> i18nKey.equals(a.getNameI18nKey())).findFirst().map(AuditAttribute::getValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditEntity that = (AuditEntity)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getTimestamp(), that.getTimestamp()) && Objects.equals(this.getAuthor(), that.getAuthor()) && Objects.equals(this.getAuditType(), that.getAuditType()) && Objects.equals(this.getAffectedObjects(), that.getAffectedObjects()) && Objects.equals(this.getChangedValues(), that.getChangedValues()) && Objects.equals(this.getSource(), that.getSource()) && Objects.equals(this.getSystem(), that.getSystem()) && Objects.equals(this.getNode(), that.getNode()) && Objects.equals(this.getMethod(), that.getMethod()) && Objects.equals(this.getExtraAttributes(), that.getExtraAttributes());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getTimestamp(), this.getAuthor(), this.getAuditType(), this.getAffectedObjects(), this.getChangedValues(), this.getSource(), this.getSystem(), this.getNode(), this.getMethod(), this.getExtraAttributes());
    }

    public String toString() {
        return "AuditEntity{id ='" + this.id + '\'' + ", version='" + version + '\'' + ", timestamp=" + this.timestamp + ", author=" + this.author + ", auditType='" + this.auditType + '\'' + ", affectedObjects=" + this.affectedObjects + ", changedValues=" + this.changedValues + ", source='" + this.source + '\'' + ", system='" + this.system + '\'' + ", node='" + this.node + '\'' + ", method=" + this.method + ", extraAttributes=" + this.extraAttributes + '}';
    }

    public static Builder builder(AuditType type) {
        return new Builder(type);
    }

    public static class Builder {
        private Long id;
        private Instant timestamp;
        private AuditAuthor author;
        private AuditType type;
        private List<AuditResource> affectedObjects = new ArrayList<AuditResource>();
        private List<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        private String source;
        private String system;
        private String node;
        private String method;
        private Set<AuditAttribute> extraAttributes = new HashSet<AuditAttribute>();

        public Builder(AuditType type) {
            Objects.requireNonNull(type);
            this.type = type;
        }

        public Builder(AuditEntity entity) {
            this.id = entity.id;
            this.timestamp = entity.timestamp;
            this.author = entity.author;
            this.type = entity.auditType;
            this.affectedObjects = new ArrayList<AuditResource>(entity.affectedObjects);
            this.changedValues = new ArrayList<ChangedValue>(entity.changedValues);
            this.source = entity.source;
            this.system = entity.system;
            this.node = entity.node;
            this.method = entity.method;
            this.extraAttributes = new HashSet<AuditAttribute>(entity.extraAttributes);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp.truncatedTo(ChronoUnit.MILLIS);
            return this;
        }

        public Builder author(AuditAuthor author) {
            this.author = author;
            return this;
        }

        public Builder type(AuditType type) {
            this.type = type;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder system(String system) {
            this.system = system;
            return this;
        }

        public Builder node(String node) {
            this.node = node;
            return this;
        }

        public Builder affectedObjects(@Nonnull List<AuditResource> affectedObjects) {
            this.affectedObjects = Objects.requireNonNull(affectedObjects);
            return this;
        }

        public Builder appendAffectedObjects(@Nonnull List<AuditResource> affectedObjects) {
            this.affectedObjects.addAll((Collection<AuditResource>)Objects.requireNonNull(affectedObjects));
            return this;
        }

        public Builder affectedObject(AuditResource affectedObject) {
            this.affectedObjects.add(affectedObject);
            return this;
        }

        public Builder changedValues(@Nonnull List<ChangedValue> changedValues) {
            this.changedValues = Objects.requireNonNull(changedValues);
            return this;
        }

        public Builder appendChangedValues(@Nonnull Collection<ChangedValue> changedValues) {
            this.changedValues.addAll(Objects.requireNonNull(changedValues));
            return this;
        }

        public Builder changedValue(ChangedValue changedValue) {
            this.changedValues.add(changedValue);
            return this;
        }

        public Builder addChangedValueIfDifferent(ChangedValue changedValue) {
            if (!Objects.equals(changedValue.getFrom(), changedValue.getTo())) {
                this.changedValue(changedValue);
            }
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder extraAttributes(@Nonnull Collection<AuditAttribute> extraAttributes) {
            this.extraAttributes = new HashSet<AuditAttribute>(Objects.requireNonNull(extraAttributes));
            return this;
        }

        public Builder appendExtraAttributes(@Nonnull Collection<AuditAttribute> extraAttributes) {
            this.extraAttributes.addAll(Objects.requireNonNull(extraAttributes));
            return this;
        }

        public Builder extraAttribute(AuditAttribute extraAttribute) {
            this.extraAttributes.add(extraAttribute);
            return this;
        }

        public AuditEntity build() {
            if (this.timestamp == null) {
                this.timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
            }
            if (this.author == null) {
                this.author = AuditAuthor.SYSTEM_AUTHOR;
            }
            return new AuditEntity(this);
        }
    }
}

