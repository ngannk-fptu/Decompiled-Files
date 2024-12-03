/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.cql.rest;

import com.atlassian.confluence.api.model.content.ContentType;
import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties
public class DisplayableType {
    @JsonProperty
    private final String type;
    @JsonProperty
    private final String i18nKey;
    @JsonProperty
    private final String label;

    public DisplayableType(Builder builder) {
        this.type = (String)Preconditions.checkNotNull((Object)builder.type);
        this.i18nKey = builder.i18nKey;
        this.label = builder.label;
    }

    public String getType() {
        return this.type;
    }

    public String getI18nKey() {
        return this.i18nKey;
    }

    public String getLabel() {
        return this.label;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;
        private String i18nKey;
        private String label;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder type(ContentType contentType) {
            this.type = contentType.serialise();
            return this;
        }

        public Builder i18nKey(String i18nKey) {
            this.i18nKey = i18nKey;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public DisplayableType build() {
            return new DisplayableType(this);
        }
    }
}

