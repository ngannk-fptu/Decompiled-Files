/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content.template;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintSpec;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public final class ContentBlueprintInstance {
    @JsonProperty
    private final Content content;
    @JsonProperty
    private final ContentBlueprintSpec contentBlueprintSpec;

    @JsonCreator
    private ContentBlueprintInstance() {
        this(ContentBlueprintInstance.builder());
    }

    private ContentBlueprintInstance(Builder builder) {
        this.content = builder.content;
        this.contentBlueprintSpec = builder.contentBlueprintSpec;
    }

    public Content getContent() {
        return this.content;
    }

    public ContentBlueprintSpec getContentBlueprintSpec() {
        return this.contentBlueprintSpec;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Content content;
        private ContentBlueprintSpec contentBlueprintSpec;

        private Builder() {
        }

        public Builder content(Content content) {
            this.content = content;
            return this;
        }

        public Builder contentBlueprintSpec(ContentBlueprintSpec contentBlueprintSpec) {
            this.contentBlueprintSpec = contentBlueprintSpec;
            return this;
        }

        public ContentBlueprintInstance build() {
            Objects.requireNonNull(this.content);
            Objects.requireNonNull(this.contentBlueprintSpec);
            return new ContentBlueprintInstance(this);
        }
    }
}

