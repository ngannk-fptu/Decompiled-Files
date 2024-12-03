/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.entity;

import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuditType {
    private final CoverageArea area;
    private final String category;
    private final String categoryI18nKey;
    private final String action;
    private final String actionI18nKey;
    private final CoverageLevel level;

    private AuditType(Builder builder) {
        this.area = Objects.requireNonNull(builder.area);
        this.categoryI18nKey = Objects.requireNonNull(builder.categoryI18nKey);
        this.actionI18nKey = Objects.requireNonNull(builder.actionI18nKey);
        this.category = builder.category;
        this.action = builder.action;
        this.level = Objects.requireNonNull(builder.level);
    }

    @Deprecated
    public AuditType(@Nonnull CoverageArea area, @Nonnull String category, @Nonnull String action, @Nonnull CoverageLevel level) {
        this.area = Objects.requireNonNull(area);
        this.categoryI18nKey = Objects.requireNonNull(category);
        this.actionI18nKey = Objects.requireNonNull(action);
        this.category = Objects.requireNonNull(category);
        this.action = Objects.requireNonNull(action);
        this.level = level;
    }

    @Nonnull
    public CoverageArea getArea() {
        return this.area;
    }

    @Nonnull
    public String getCategoryI18nKey() {
        return this.categoryI18nKey;
    }

    @Nullable
    public String getCategory() {
        return this.category;
    }

    @Nonnull
    public String getActionI18nKey() {
        return this.actionI18nKey;
    }

    @Nullable
    public String getAction() {
        return this.action;
    }

    @Nonnull
    public CoverageLevel getLevel() {
        return this.level;
    }

    public String toString() {
        return "AuditType{area=" + (Object)((Object)this.area) + ", categoryI18nKey='" + this.categoryI18nKey + '\'' + ", category='" + this.category + '\'' + ", actionI18nKey='" + this.actionI18nKey + '\'' + ", action='" + this.action + '\'' + ", level=" + (Object)((Object)this.level) + '}';
    }

    public static Builder fromI18nKeys(@Nonnull CoverageArea area, @Nonnull CoverageLevel level, @Nonnull String categoryI18nKey, @Nonnull String actionI18nKey) {
        return new Builder(area, level, categoryI18nKey, actionI18nKey);
    }

    public static Builder builder(@Nonnull AuditType auditType) {
        return new Builder(auditType);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditType auditType = (AuditType)o;
        return this.area == auditType.area && this.categoryI18nKey.equals(auditType.categoryI18nKey) && Objects.equals(this.category, auditType.category) && this.actionI18nKey.equals(auditType.actionI18nKey) && Objects.equals(this.action, auditType.action) && this.level == auditType.level;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.area, this.category, this.categoryI18nKey, this.action, this.actionI18nKey, this.level});
    }

    public static class Builder {
        private CoverageArea area;
        private String category;
        private String categoryI18nKey;
        private String action;
        private String actionI18nKey;
        private CoverageLevel level;

        private Builder(@Nonnull CoverageArea area, @Nonnull CoverageLevel level, @Nonnull String categoryI18nKey, @Nonnull String actionI18nKey) {
            this.area = Objects.requireNonNull(area);
            this.categoryI18nKey = Objects.requireNonNull(categoryI18nKey);
            this.actionI18nKey = Objects.requireNonNull(actionI18nKey);
            this.level = Objects.requireNonNull(level);
        }

        private Builder(AuditType auditType) {
            this.area = Objects.requireNonNull(auditType.getArea());
            this.categoryI18nKey = Objects.requireNonNull(auditType.getCategoryI18nKey());
            this.actionI18nKey = Objects.requireNonNull(auditType.getActionI18nKey());
            this.category = auditType.getCategory();
            this.action = auditType.getAction();
            this.level = Objects.requireNonNull(auditType.getLevel());
        }

        public Builder withCategoryTranslation(String category) {
            this.category = category;
            return this;
        }

        public Builder withActionTranslation(String action) {
            this.action = action;
            return this;
        }

        public AuditType build() {
            return new AuditType(this);
        }
    }
}

