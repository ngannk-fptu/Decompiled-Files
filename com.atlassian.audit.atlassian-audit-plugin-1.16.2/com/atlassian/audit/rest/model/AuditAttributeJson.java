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
public class AuditAttributeJson {
    @Nonnull
    private final String name;
    @Nonnull
    private final String nameI18nKey;
    @Nullable
    private final String value;

    @JsonCreator
    public AuditAttributeJson(@JsonProperty(value="nameI18nKey") @Nonnull String nameI18nKey, @JsonProperty(value="name") @Nonnull String name, @JsonProperty(value="value") @Nullable String value) {
        this.nameI18nKey = Objects.requireNonNull(nameI18nKey);
        this.name = Objects.requireNonNull(name);
        this.value = value;
    }

    @Nonnull
    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @Nonnull
    @JsonProperty(value="nameI18nKey")
    public String getNameI18nKey() {
        return this.nameI18nKey;
    }

    @Nullable
    @JsonProperty(value="value")
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
        AuditAttributeJson that = (AuditAttributeJson)o;
        return this.name.equals(that.name) && this.nameI18nKey.equals(that.nameI18nKey) && Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.nameI18nKey, this.value);
    }

    public String toString() {
        return "AuditAttributeJson{name='" + this.name + '\'' + ", nameI18nKey='" + this.nameI18nKey + '\'' + ", value='" + this.value + '\'' + '}';
    }
}

