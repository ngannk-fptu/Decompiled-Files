/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ChangedValueJson {
    @Nonnull
    private final String key;
    @Nonnull
    private final String i18nKey;
    @Nullable
    private final String from;
    @Nullable
    private final String to;

    @JsonCreator
    public ChangedValueJson(@JsonProperty(value="key") @Nonnull String key, @JsonProperty(value="i18nKey") @Nonnull String i18nKey, @JsonProperty(value="from") @Nullable String from, @JsonProperty(value="to") @Nullable String to) {
        this.key = key;
        this.i18nKey = i18nKey;
        this.from = from;
        this.to = to;
    }

    @Nonnull
    @JsonProperty(value="key")
    public String getKey() {
        return this.key;
    }

    @Nonnull
    @JsonProperty(value="i18nKey")
    public String getI18nKey() {
        return this.i18nKey;
    }

    @Nullable
    @JsonProperty(value="from")
    public String getFrom() {
        return this.from;
    }

    @Nullable
    @JsonProperty(value="to")
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
        ChangedValueJson that = (ChangedValueJson)o;
        return this.key.equals(that.key) && Objects.equals(this.i18nKey, that.i18nKey) && Objects.equals(this.from, that.from) && Objects.equals(this.to, that.to);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.i18nKey, this.from, this.to);
    }

    public String toString() {
        return "ChangedValueJson{key='" + this.key + '\'' + ", i18nKey='" + this.i18nKey + '\'' + ", from='" + this.from + '\'' + ", to='" + this.to + '\'' + '}';
    }
}

