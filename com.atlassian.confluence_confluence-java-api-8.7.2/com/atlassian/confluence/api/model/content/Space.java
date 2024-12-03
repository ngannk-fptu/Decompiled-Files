/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.graphql.annotations.GraphQLIDType
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  org.apache.commons.lang3.reflect.TypeUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.FormattedBody;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.MetadataValueDeserializer;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.graphql.annotations.GraphQLIDType;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class Space
implements Container,
NavigationAware,
Relatable {
    @JsonProperty
    @GraphQLIDType
    private final Long id;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String name;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Icon.class)
    @JsonProperty
    private final Reference<Icon> icon;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="SpaceDescriptions")
    private final Map<ContentRepresentation, FormattedBody> description;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private final Reference<Content> homepage;
    @GraphQLName
    private final ContentId homepageId;
    @JsonProperty
    @GraphQLTypeName(value="ContentLinks")
    private final Map<LinkType, Link> links;
    @JsonProperty
    private final SpaceType type;
    @JsonDeserialize(as=EnrichableMap.class, contentUsing=MetadataValueDeserializer.class)
    @JsonProperty
    private final Map<String, Object> metadata;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=SpaceRetentionPolicy.class)
    @JsonProperty
    private final Reference<SpaceRetentionPolicy> retentionPolicy;

    public static String getSpaceKey(Reference<Space> reference) {
        return (String)reference.getIdProperty(IdProperties.key);
    }

    public static Reference<Space> buildReference(String spaceKey) {
        return Reference.collapsed(Space.class, Collections.singletonMap(IdProperties.key, spaceKey));
    }

    public static SpaceBuilder builder() {
        return new SpaceBuilder();
    }

    public static SpaceBuilder builder(Space space) {
        return Space.builder().name(space.name).key(space.key).id(space.getId()).icon(space.icon).description(space.description).homepage(space.homepage).type(space.type).metadata(space.metadata).retentionPolicy(space.retentionPolicy);
    }

    private Space(SpaceBuilder builder) {
        this.name = builder.name;
        this.key = builder.key;
        this.id = builder.id;
        this.icon = Reference.orEmpty(builder.icon, Icon.class);
        this.description = BuilderUtils.modelMap(builder.description);
        this.homepage = Reference.orEmpty(builder.homepage, Content.class);
        this.homepageId = Content.getSelector(builder.homepage).getId();
        this.links = Collections.unmodifiableMap(builder.links);
        this.type = builder.type;
        this.metadata = BuilderUtils.modelMap(builder.metadata);
        this.retentionPolicy = Reference.orEmpty(builder.retentionPolicy, SpaceRetentionPolicy.class);
    }

    @JsonCreator
    private Space() {
        this(Space.builder());
    }

    public long getId() {
        if (this.id == null) {
            return 0L;
        }
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public Reference<Icon> getIconRef() {
        return Reference.orEmpty(this.icon, Icon.class);
    }

    public Map<ContentRepresentation, FormattedBody> getDescription() {
        return this.description;
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    @Deprecated
    @JsonIgnore
    public Map<ContentRepresentation, Reference<FormattedBody>> getDescriptionRefs() {
        HashMap<ContentRepresentation, Reference<FormattedBody>> referenceMap = new HashMap<ContentRepresentation, Reference<FormattedBody>>();
        for (Map.Entry<ContentRepresentation, FormattedBody> entry : this.description.entrySet()) {
            referenceMap.put(entry.getKey(), Reference.to(entry.getValue()));
        }
        if (this.description instanceof EnrichableMap) {
            EnrichableMap enrichableMap = (EnrichableMap)this.description;
            for (ContentRepresentation representation : enrichableMap.getCollapsedEntries()) {
                Reference<FormattedBody> collapsed = Reference.collapsed(FormattedBody.class);
                referenceMap.put(representation, collapsed);
            }
        }
        return Collections.unmodifiableMap(referenceMap);
    }

    @JsonIgnore
    public Reference<Content> getHomepageRef() {
        return Reference.orEmpty(this.homepage, Content.class);
    }

    @JsonIgnore
    public Map<LinkType, Link> getLinks() {
        return this.links;
    }

    public SpaceType getType() {
        return this.type;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().space(this);
    }

    public String toString() {
        return "Space{key='" + this.key + '\'' + ", name='" + this.name + '\'' + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Space)) {
            return false;
        }
        return Objects.equals(this.getKey(), ((Space)obj).getKey());
    }

    public int hashCode() {
        return Objects.hash(this.getKey());
    }

    @Deprecated
    @Internal
    public static List<MetadataProperty> getMetadataProperties() {
        ParameterizedType labelsPropertyType = TypeUtils.parameterize(PageResponse.class, (Type[])new Type[]{Label.class});
        return new ArrayList<MetadataProperty>(Collections.singletonList(new MetadataProperty("labels", labelsPropertyType)));
    }

    public static class Expansions {
        public static final String DESCRIPTION = "description";
        public static final String HOMEPAGE = "homepage";
        public static final String ICON = "icon";
        public static final String METADATA = "metadata";
        public static final String RETENTION_POLICY = "retentionPolicy";
    }

    @Internal
    public static class MetadataKeys {
        public static final String LABELS = "labels";
    }

    public static class SpaceBuilder {
        private Long id = null;
        private String key = null;
        private String name = null;
        private Reference<Icon> icon = null;
        private ModelMapBuilder<ContentRepresentation, FormattedBody> description = ModelMapBuilder.newInstance();
        private Reference<Content> homepage = null;
        private final Map<LinkType, Link> links = new HashMap<LinkType, Link>();
        private SpaceType type = SpaceType.GLOBAL;
        private ModelMapBuilder<String, Object> metadata = ModelMapBuilder.newExpandedInstance();
        private Reference<SpaceRetentionPolicy> retentionPolicy = null;

        private SpaceBuilder() {
        }

        public Space build() {
            return new Space(this);
        }

        public SpaceBuilder description(ContentRepresentation representation, String value) {
            return this.description(representation, ((FormattedBody.FormattedBodyBuilder)((FormattedBody.FormattedBodyBuilder)((FormattedBody.FormattedBodyBuilder)FormattedBody.builder().representation(representation)).value(value)).webresource((Reference)Reference.empty(WebResourceDependencies.class))).build());
        }

        public SpaceBuilder description(ContentRepresentation representation, FormattedBody formattedBody) {
            this.description.put(representation, formattedBody);
            return this;
        }

        public SpaceBuilder description(Map<ContentRepresentation, FormattedBody> description) {
            this.description.copy(description);
            return this;
        }

        @Deprecated
        public SpaceBuilder description(ContentRepresentation representation, Reference<FormattedBody> formattedBody) {
            if (formattedBody.isExpanded()) {
                this.description(representation, formattedBody.get());
            }
            return this;
        }

        public SpaceBuilder icon(Icon icon) {
            this.icon = Reference.orEmpty(icon, Icon.class);
            return this;
        }

        public SpaceBuilder icon(Reference<Icon> iconRef) {
            this.icon = iconRef;
            return this;
        }

        public SpaceBuilder id(long id) {
            this.id = id;
            return this;
        }

        public SpaceBuilder key(String key) {
            this.key = key;
            return this;
        }

        public SpaceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SpaceBuilder homepage(Reference<Content> homepageRef) {
            this.homepage = homepageRef;
            return this;
        }

        public SpaceBuilder homepage(Content homepage) {
            this.homepage = Reference.to(homepage);
            return this;
        }

        public SpaceBuilder addLink(Link link) {
            this.links.put(link.getType(), link);
            return this;
        }

        public SpaceBuilder addLink(LinkType type, String path) {
            return this.addLink(new Link(type, path));
        }

        public SpaceBuilder type(SpaceType type) {
            this.type = type;
            return this;
        }

        public SpaceBuilder metadata(Map<String, Object> metadata) {
            this.metadata.copy(metadata);
            return this;
        }

        public SpaceBuilder retentionPolicy(SpaceRetentionPolicy retentionPolicy) {
            this.retentionPolicy = Reference.orEmpty(retentionPolicy, SpaceRetentionPolicy.class);
            return this;
        }

        public SpaceBuilder retentionPolicy(Reference<SpaceRetentionPolicy> retentionPolicyRef) {
            this.retentionPolicy = retentionPolicyRef;
            return this;
        }
    }

    static enum IdProperties {
        key;

    }
}

