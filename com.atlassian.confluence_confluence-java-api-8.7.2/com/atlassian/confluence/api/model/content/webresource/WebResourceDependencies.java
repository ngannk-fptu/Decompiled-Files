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
import com.atlassian.confluence.api.model.content.webresource.SuperBatchWebResources;
import com.atlassian.confluence.api.model.content.webresource.WebResourcesBuilder;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ModelListBuilder;
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
public class WebResourceDependencies {
    @JsonProperty
    private List<String> keys;
    @JsonProperty
    private List<String> contexts;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="WebResourceUris")
    private Map<ResourceType, List<URI>> uris;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="WebResourceTags")
    private Map<ResourceType, HtmlString> tags;
    @JsonProperty
    private Reference<SuperBatchWebResources> superbatch;

    @JsonCreator
    public WebResourceDependencies(@JsonProperty(value="keys") List<String> keys, @JsonProperty(value="contexts") List<String> contexts, @JsonProperty(value="uris") Map<ResourceType, List<URI>> uris, @JsonProperty(value="tags") Map<ResourceType, HtmlString> tags, @JsonProperty(value="superbatch") SuperBatchWebResources superbatch) {
        this(((WebResourceDependenciesBuilder)((WebResourceDependenciesBuilder)WebResourceDependencies.builder().keys(keys == null ? Collections.emptyList() : keys).contexts(contexts == null ? Collections.emptyList() : contexts).uris(uris == null ? Collections.emptyMap() : uris)).tag(tags == null ? Collections.emptyMap() : tags)).superbatch(superbatch));
    }

    WebResourceDependencies(WebResourceDependenciesBuilder builder) {
        this.keys = builder.keys.build();
        this.contexts = builder.contexts.build();
        this.uris = builder.uris.build();
        this.tags = builder.tags.build();
        this.superbatch = Reference.orEmpty(builder.superbatch, SuperBatchWebResources.class);
    }

    public static WebResourceDependenciesBuilder builder() {
        return new WebResourceDependenciesBuilder();
    }

    public List<String> getContexts() {
        return this.contexts;
    }

    public List<String> getKeys() {
        return this.keys;
    }

    public Map<ResourceType, List<URI>> getUris() {
        return this.uris;
    }

    public Map<ResourceType, HtmlString> getTags() {
        return this.tags;
    }

    public SuperBatchWebResources getSuperbatch() {
        if (this.superbatch.isExpanded()) {
            return this.superbatch.get();
        }
        return SuperBatchWebResources.builder().build();
    }

    public static final class Expansions {
        public static final String keys = "keys";
        public static final String contexts = "contexts";
        public static final String tags = "tags";
        public static final String uris = "uris";
        public static final String superbatch = "superbatch";
    }

    public static class WebResourceDependenciesBuilder
    implements WebResourcesBuilder {
        private final ModelListBuilder<String> keys = ModelListBuilder.newInstance();
        private final ModelListBuilder<String> contexts = ModelListBuilder.newInstance();
        private final ModelMapBuilder<ResourceType, List<URI>> uris = ModelMapBuilder.newInstance();
        private final ModelMapBuilder<ResourceType, HtmlString> tags = ModelMapBuilder.newInstance();
        private SuperBatchWebResources superbatch = SuperBatchWebResources.builder().build();

        WebResourceDependenciesBuilder() {
        }

        public WebResourceDependenciesBuilder superbatch(SuperBatchWebResources superbatch) {
            this.superbatch = superbatch;
            return this;
        }

        public WebResourceDependenciesBuilder keys(Iterable<String> keys) {
            this.keys.putAll(keys);
            return this;
        }

        @Override
        public WebResourceDependenciesBuilder uris(ResourceType type, List<URI> uris) {
            if (type != ResourceType.DATA) {
                this.uris.put(type, uris);
            }
            return this;
        }

        @Override
        public WebResourceDependenciesBuilder uris(Map<ResourceType, List<URI>> uris) {
            for (Map.Entry<ResourceType, List<URI>> entry : uris.entrySet()) {
                this.uris(entry.getKey(), (List)entry.getValue());
            }
            return this;
        }

        @Override
        public WebResourceDependenciesBuilder addCollapsedUris(ResourceType type) {
            if (type != ResourceType.DATA) {
                this.uris.addCollapsedEntry(type);
            }
            return this;
        }

        public WebResourceDependenciesBuilder contexts(Iterable<String> contexts) {
            this.contexts.putAll(contexts);
            return this;
        }

        @Override
        public WebResourceDependenciesBuilder tag(ResourceType type, HtmlString htmlString) {
            this.tags.put(type, htmlString);
            return this;
        }

        @Override
        public WebResourceDependenciesBuilder tag(Map<ResourceType, HtmlString> tags) {
            for (Map.Entry<ResourceType, HtmlString> entry : tags.entrySet()) {
                this.tag(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public WebResourceDependenciesBuilder addCollapsedTag(ResourceType tagType) {
            this.tags.addCollapsedEntry(tagType);
            return this;
        }

        public WebResourceDependencies build() {
            return new WebResourceDependencies(this);
        }
    }
}

