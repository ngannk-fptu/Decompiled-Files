/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.model;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

@ReturnValuesAreNonnullByDefault
public class AuditCategory {
    private final String category;
    private final Optional<String> categoryI18nKey;

    public AuditCategory(@Nonnull String category, String categoryI18nKey) {
        this.category = Objects.requireNonNull(category, "category");
        this.categoryI18nKey = Optional.ofNullable(categoryI18nKey);
    }

    public String getCategory() {
        return this.category;
    }

    public Optional<String> getCategoryI18nKey() {
        return this.categoryI18nKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditCategory that = (AuditCategory)o;
        return this.category.equals(that.category) && this.categoryI18nKey.equals(that.categoryI18nKey);
    }

    public int hashCode() {
        return Objects.hash(this.category, this.categoryI18nKey);
    }

    public String toString() {
        return "AuditCategory{category='" + this.category + '\'' + ", categoryI18nKey=" + this.categoryI18nKey + '}';
    }
}

