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

public class ChangedValue {
    private final String key;
    private final String i18nKey;
    private final String from;
    private final String to;

    @Deprecated
    public ChangedValue(@Nonnull String key) {
        this(key, null, null);
    }

    @Deprecated
    public ChangedValue(@Nonnull String key, String to) {
        this(key, null, to);
    }

    @Deprecated
    public ChangedValue(@Nonnull String key, String from, String to) {
        this.key = Objects.requireNonNull(key);
        this.i18nKey = key;
        this.from = from;
        this.to = to;
    }

    public ChangedValue(Builder builder) {
        this.i18nKey = Objects.requireNonNull(builder.i18nKey);
        this.key = builder.keyTranslation;
        this.from = builder.from;
        this.to = builder.to;
    }

    public static Builder fromI18nKeys(@Nonnull String i18nKey) {
        return new Builder(i18nKey);
    }

    @Nonnull
    public String getI18nKey() {
        return this.i18nKey;
    }

    @Nonnull
    public String getKey() {
        return this.key;
    }

    @Nullable
    public String getFrom() {
        return this.from;
    }

    @Nullable
    public String getTo() {
        return this.to;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChangedValue that = (ChangedValue)o;
        return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.i18nKey, that.i18nKey) && Objects.equals(this.getFrom(), that.getFrom()) && Objects.equals(this.getTo(), that.getTo());
    }

    public String toString() {
        return "ChangedValue{i18nKey='" + this.i18nKey + '\'' + ", key='" + this.key + '\'' + ", from='" + this.from + '\'' + ", to='" + this.to + '\'' + '}';
    }

    public int hashCode() {
        return Objects.hash(this.i18nKey, this.key, this.from, this.to);
    }

    public static class Builder {
        private String i18nKey;
        private String keyTranslation;
        private String from;
        private String to;

        public Builder(@Nonnull String i18nKey) {
            this.i18nKey = Objects.requireNonNull(i18nKey);
        }

        public Builder withKeyTranslation(String keyTranslation) {
            this.keyTranslation = keyTranslation;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public ChangedValue build() {
            return new ChangedValue(this);
        }
    }
}

