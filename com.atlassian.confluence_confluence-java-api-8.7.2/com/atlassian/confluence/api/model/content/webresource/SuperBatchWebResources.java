/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content.webresource;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.HtmlString;
import com.atlassian.confluence.api.model.content.webresource.ResourceType;
import com.atlassian.confluence.api.model.content.webresource.WebResourcesBuilder;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class SuperBatchWebResources {
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="WebResourceUris")
    private Map<ResourceType, List<URI>> uris;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="WebResourceTags")
    private Map<ResourceType, HtmlString> tags;
    @JsonProperty
    private Reference<HtmlString> metatags;

    @JsonCreator
    private SuperBatchWebResources(@JsonProperty(value="uris") Map<ResourceType, List<URI>> uris, @JsonProperty(value="tags") Map<ResourceType, HtmlString> tags, @JsonProperty(value="metatags") HtmlString metatags) {
        this(((SuperBatchWebResourcesBuilder)((SuperBatchWebResourcesBuilder)SuperBatchWebResources.builder().uris(uris == null ? Collections.emptyMap() : uris)).tag(tags == null ? Collections.emptyMap() : tags)).metatags(metatags == null ? new HtmlString("") : metatags));
    }

    private SuperBatchWebResources(SuperBatchWebResourcesBuilder builder) {
        this.uris = builder.uris.build();
        this.tags = builder.tags.build();
        this.metatags = builder.metatags;
    }

    public static SuperBatchWebResourcesBuilder builder() {
        return new SuperBatchWebResourcesBuilder();
    }

    public Map<ResourceType, List<URI>> getUris() {
        return this.uris;
    }

    public Map<ResourceType, HtmlString> getTags() {
        return this.tags;
    }

    public HtmlString getMetatags() {
        if (this.metatags.isExpanded()) {
            return this.metatags.get();
        }
        return new HtmlString("");
    }

    public static final class Expansions {
        public static final String tags = "tags";
        public static final String uris = "uris";
        public static final String metatags = "metatags";
    }

    public static class SuperBatchWebResourcesBuilder
    implements WebResourcesBuilder {
        private final ModelMapBuilder<ResourceType, List<URI>> uris = ModelMapBuilder.newInstance();
        private final ModelMapBuilder<ResourceType, HtmlString> tags = ModelMapBuilder.newInstance();
        private Reference<HtmlString> metatags = Reference.collapsed(HtmlString.class);

        SuperBatchWebResourcesBuilder() {
        }

        public SuperBatchWebResourcesBuilder metatags(HtmlString metatags) {
            this.metatags = Reference.to(metatags);
            return this;
        }

        @Override
        public SuperBatchWebResourcesBuilder uris(ResourceType type, List<URI> uris) {
            if (type != ResourceType.DATA) {
                this.uris.put(type, uris);
            }
            return this;
        }

        @Override
        public SuperBatchWebResourcesBuilder uris(Map<ResourceType, List<URI>> uris) {
            for (Map.Entry<ResourceType, List<URI>> entry : uris.entrySet()) {
                this.uris(entry.getKey(), (List)entry.getValue());
            }
            return this;
        }

        @Override
        public SuperBatchWebResourcesBuilder addCollapsedUris(ResourceType type) {
            if (type != ResourceType.DATA) {
                this.uris.addCollapsedEntry(type);
            }
            return this;
        }

        @Override
        public SuperBatchWebResourcesBuilder tag(ResourceType type, HtmlString htmlString) {
            this.tags.put(type, htmlString);
            return this;
        }

        @Override
        public SuperBatchWebResourcesBuilder tag(Map<ResourceType, HtmlString> tags) {
            for (Map.Entry<ResourceType, HtmlString> entry : tags.entrySet()) {
                this.tag(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public SuperBatchWebResourcesBuilder addCollapsedTag(ResourceType tagType) {
            this.tags.addCollapsedEntry(tagType);
            return this;
        }

        public SuperBatchWebResources build() {
            return new SuperBatchWebResources(this);
        }
    }
}

