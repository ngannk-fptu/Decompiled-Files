/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.entity;

import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
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

public class AuditEvent {
    @Nullable
    private final String action;
    @Nonnull
    private final String actionI18nKey;
    @Nullable
    private final String category;
    @Nonnull
    private final String categoryI18nKey;
    @Nonnull
    private final CoverageLevel level;
    @Nullable
    private final CoverageArea area;
    @Nonnull
    private final List<AuditResource> affectedObjects;
    @Nonnull
    private final List<ChangedValue> changedValues;
    @Nonnull
    private final Set<AuditAttribute> extraAttributes;

    private AuditEvent(Builder builder) {
        this.action = builder.action;
        this.actionI18nKey = Objects.requireNonNull(builder.actionI18nKey);
        this.category = builder.category;
        this.categoryI18nKey = Objects.requireNonNull(builder.categoryI18nKey);
        this.level = Objects.requireNonNull(builder.level);
        this.area = builder.area;
        this.affectedObjects = Collections.unmodifiableList(builder.affectedObjects);
        this.changedValues = Collections.unmodifiableList(builder.changedValues);
        this.extraAttributes = Collections.unmodifiableSet(builder.extraAttributes);
    }

    @Nonnull
    public String getActionI18nKey() {
        return this.actionI18nKey;
    }

    @Nullable
    @Deprecated
    public String getAction() {
        return this.action;
    }

    @Nonnull
    public String getCategoryI18nKey() {
        return this.categoryI18nKey;
    }

    @Nullable
    @Deprecated
    public String getCategory() {
        return this.category;
    }

    @Nullable
    public CoverageArea getArea() {
        return this.area;
    }

    @Nonnull
    public CoverageLevel getLevel() {
        return this.level;
    }

    @Nonnull
    public List<AuditResource> getAffectedObjects() {
        return this.affectedObjects;
    }

    @Nonnull
    public List<ChangedValue> getChangedValues() {
        return this.changedValues;
    }

    @Nonnull
    public Collection<AuditAttribute> getExtraAttributes() {
        return this.extraAttributes;
    }

    public Optional<String> getExtraAttribute(@Nonnull String name) {
        return this.extraAttributes.stream().filter(a -> Objects.requireNonNull(name).equals(a.getName())).findFirst().map(AuditAttribute::getValue);
    }

    public Optional<String> getExtraAttributeByKey(@Nonnull String nameKey) {
        return this.extraAttributes.stream().filter(a -> Objects.requireNonNull(nameKey).equals(a.getNameI18nKey())).findFirst().map(AuditAttribute::getValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditEvent that = (AuditEvent)o;
        return Objects.equals(this.getAction(), that.getAction()) && Objects.equals(this.getActionI18nKey(), that.getActionI18nKey()) && Objects.equals(this.getCategory(), that.getCategory()) && Objects.equals(this.getCategoryI18nKey(), that.getCategoryI18nKey()) && this.getLevel() == that.getLevel() && this.getArea() == that.getArea() && Objects.equals(this.getAffectedObjects(), that.getAffectedObjects()) && Objects.equals(this.getChangedValues(), that.getChangedValues()) && Objects.equals(this.getExtraAttributes(), that.getExtraAttributes());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getAction(), this.getActionI18nKey(), this.getCategory(), this.getCategoryI18nKey(), this.getLevel(), this.getArea(), this.getAffectedObjects(), this.getChangedValues(), this.getExtraAttributes()});
    }

    public String toString() {
        return "AuditEvent{action='" + this.action + '\'' + ", actionI18nKey='" + this.actionI18nKey + '\'' + ", category='" + this.category + '\'' + ", categoryI18nKey='" + this.categoryI18nKey + '\'' + ", level=" + (Object)((Object)this.level) + ", area=" + (Object)((Object)this.area) + ", affectedObjects=" + this.affectedObjects + ", changedValues=" + this.changedValues + ", extraAttributes=" + this.extraAttributes + '}';
    }

    @Deprecated
    public static Builder builder(@Nonnull String action, @Nonnull String category, @Nonnull CoverageLevel level) {
        return new Builder(action, category, level);
    }

    public static Builder fromI18nKeys(@Nonnull String categoryI18nKey, @Nonnull String actionI18nKey, @Nonnull CoverageLevel level, @Nullable CoverageArea area) {
        return new Builder(level, actionI18nKey, categoryI18nKey, area);
    }

    public static Builder fromI18nKeys(@Nonnull String categoryI18nKey, @Nonnull String actionI18nKey, @Nonnull CoverageLevel level) {
        return new Builder(level, actionI18nKey, categoryI18nKey, null);
    }

    public static Builder builder(@Nonnull AuditType type) {
        return new Builder(type);
    }

    public static class Builder {
        @Nonnull
        private String actionI18nKey;
        @Nullable
        private String action;
        @Nonnull
        private String categoryI18nKey;
        @Nullable
        private String category;
        @Nonnull
        private CoverageLevel level;
        @Nullable
        private CoverageArea area;
        @Nonnull
        private List<AuditResource> affectedObjects = new ArrayList<AuditResource>();
        @Nonnull
        private List<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        @Nonnull
        private Set<AuditAttribute> extraAttributes = new HashSet<AuditAttribute>();

        public Builder(@Nonnull AuditType type) {
            this.actionI18nKey = Objects.requireNonNull(type.getActionI18nKey(), "actionI18nKey");
            this.action = type.getAction();
            this.categoryI18nKey = Objects.requireNonNull(type.getCategoryI18nKey(), "categoryI18nKey");
            this.category = type.getCategory();
            this.level = Objects.requireNonNull(type.getLevel(), "level");
            this.area = type.getArea();
        }

        private Builder(@Nonnull CoverageLevel level, @Nonnull String actionI18nKey, @Nonnull String categoryI18nKey, @Nullable CoverageArea area) {
            this.actionI18nKey = Objects.requireNonNull(actionI18nKey, "actionI18nKey");
            this.categoryI18nKey = Objects.requireNonNull(categoryI18nKey, "categoryI18nKey");
            this.level = Objects.requireNonNull(level, "level");
            this.area = area;
        }

        @Deprecated
        public Builder(@Nonnull String action, @Nonnull String category, @Nonnull CoverageLevel level) {
            this(action, category, level, null);
        }

        @Deprecated
        public Builder(@Nonnull String action, @Nonnull String category, @Nonnull CoverageLevel level, @Nullable CoverageArea area) {
            this.action = Objects.requireNonNull(action, "action");
            this.actionI18nKey = action;
            this.category = Objects.requireNonNull(category, "category");
            this.categoryI18nKey = category;
            this.level = Objects.requireNonNull(level, "level");
            this.area = area;
        }

        public Builder(@Nonnull AuditEvent event) {
            this.action = event.action;
            this.actionI18nKey = event.actionI18nKey;
            this.category = event.category;
            this.categoryI18nKey = event.categoryI18nKey;
            this.level = event.level;
            this.area = event.area;
            this.affectedObjects = new ArrayList<AuditResource>(event.affectedObjects);
            this.changedValues = new ArrayList<ChangedValue>(event.changedValues);
            this.extraAttributes = new HashSet<AuditAttribute>(event.extraAttributes);
        }

        @Deprecated
        public Builder action(@Nonnull String action) {
            this.action = Objects.requireNonNull(action);
            return this;
        }

        public Builder actionI18nKey(@Nonnull String actionI18nKey) {
            this.actionI18nKey = Objects.requireNonNull(actionI18nKey);
            return this;
        }

        public Builder categoryI18nKey(@Nonnull String categoryI18nKey) {
            this.categoryI18nKey = Objects.requireNonNull(categoryI18nKey);
            return this;
        }

        @Deprecated
        public Builder category(@Nonnull String category) {
            this.category = Objects.requireNonNull(category);
            return this;
        }

        public Builder level(@Nonnull CoverageLevel level) {
            this.level = Objects.requireNonNull(level);
            return this;
        }

        public Builder area(@Nonnull CoverageArea area) {
            this.area = Objects.requireNonNull(area);
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

        public Builder affectedObject(@Nonnull AuditResource affectedObject) {
            this.affectedObjects.add(Objects.requireNonNull(affectedObject));
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

        public Builder changedValue(@Nonnull ChangedValue changedValue) {
            this.changedValues.add(Objects.requireNonNull(changedValue));
            return this;
        }

        public Builder addChangedValueIfDifferent(@Nonnull ChangedValue changedValue) {
            if (!Objects.equals(changedValue.getFrom(), changedValue.getTo())) {
                this.changedValue(changedValue);
            }
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

        public Builder extraAttribute(@Nonnull AuditAttribute extraAttribute) {
            this.extraAttributes.add(Objects.requireNonNull(extraAttribute));
            return this;
        }

        public AuditEvent build() {
            return new AuditEvent(this);
        }
    }
}

