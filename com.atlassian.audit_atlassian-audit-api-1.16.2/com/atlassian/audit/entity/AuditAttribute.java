/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.entity;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuditAttribute {
    private final String name;
    private final String nameI18nKey;
    private final String value;

    @Deprecated
    public AuditAttribute(String name, String value) {
        this.nameI18nKey = name;
        this.name = name;
        this.value = value;
    }

    private AuditAttribute(Builder builder) {
        this.nameI18nKey = Objects.requireNonNull(builder.nameI18nKey);
        this.name = builder.name;
        this.value = builder.value;
    }

    public static Builder fromI18nKeys(@Nonnull String nameI18nKey, String value) {
        return new Builder(nameI18nKey, value);
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nonnull
    public String getNameI18nKey() {
        return this.nameI18nKey;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditAttribute that = (AuditAttribute)o;
        return Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getNameI18nKey(), that.getNameI18nKey()) && Objects.equals(this.getValue(), that.getValue());
    }

    public int hashCode() {
        return Objects.hash(this.getName(), this.getNameI18nKey(), this.getValue());
    }

    public String toString() {
        return "AuditAttribute{name='" + this.name + '\'' + ", nameI18nKey='" + this.nameI18nKey + '\'' + ", value='" + this.value + '\'' + '}';
    }

    public static class Builder {
        private String name;
        private String nameI18nKey;
        private String value;

        public Builder(@Nonnull String nameI18nKey, String value) {
            this.nameI18nKey = Objects.requireNonNull(nameI18nKey);
            this.value = value;
        }

        public Builder withNameTranslation(String name) {
            this.name = name;
            return this;
        }

        public AuditAttribute build() {
            return new AuditAttribute(this);
        }
    }
}

