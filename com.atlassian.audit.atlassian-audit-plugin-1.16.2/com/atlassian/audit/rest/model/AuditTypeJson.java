/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditTypeJson {
    @Nonnull
    private final String category;
    @Nonnull
    private final String categoryI18nKey;
    @Nonnull
    private final String action;
    @Nonnull
    private final String actionI18nKey;

    @JsonCreator
    public AuditTypeJson(@JsonProperty(value="categoryI18nKey") @Nonnull String categoryI18nKey, @JsonProperty(value="category") @Nonnull String category, @JsonProperty(value="actionI18nKey") @Nonnull String actionI18nKey, @JsonProperty(value="action") @Nonnull String action) {
        this.category = category;
        this.action = action;
        this.actionI18nKey = actionI18nKey;
        this.categoryI18nKey = categoryI18nKey;
    }

    @Nonnull
    @JsonProperty(value="category")
    public String getCategory() {
        return this.category;
    }

    @Nonnull
    @JsonProperty(value="action")
    public String getAction() {
        return this.action;
    }

    @Nonnull
    @JsonProperty(value="categoryI18nKey")
    public String getCategoryI18nKey() {
        return this.categoryI18nKey;
    }

    @Nonnull
    @JsonProperty(value="actionI18nKey")
    public String getActionI18nKey() {
        return this.actionI18nKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditTypeJson that = (AuditTypeJson)o;
        return Objects.equals(this.category, that.category) && Objects.equals(this.action, that.action) && Objects.equals(this.categoryI18nKey, that.categoryI18nKey) && Objects.equals(this.actionI18nKey, that.actionI18nKey);
    }

    public int hashCode() {
        return Objects.hash(this.category, this.categoryI18nKey, this.action, this.actionI18nKey);
    }

    public String toString() {
        return "AuditTypeJson{category='" + this.category + '\'' + ", categoryI18nKey='" + this.categoryI18nKey + '\'' + ", action='" + this.action + ", actionI18nKey='" + this.actionI18nKey + '}';
    }
}

