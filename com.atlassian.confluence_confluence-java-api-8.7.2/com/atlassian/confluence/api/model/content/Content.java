/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 *  com.atlassian.graphql.annotations.GraphQLIgnore
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.ContainerMap;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.InternalDeserializers;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationCheckResult;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.ModelListBuilder;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.MetadataValueDeserializer;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import com.atlassian.graphql.annotations.GraphQLIgnore;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class Content
implements Container,
NavigationAware,
Relatable {
    @JsonProperty
    private final ContentId id;
    @JsonProperty
    private final ContentType type;
    @JsonProperty
    private final ContentStatus status;
    @JsonProperty
    private final String title;
    @JsonProperty
    @GraphQLTypeName(value="ContentLinks")
    private final Map<LinkType, Link> links;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Space.class)
    @JsonProperty
    private final Reference<Space> space;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=History.class)
    @JsonProperty
    private final Reference<History> history;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Version.class)
    @JsonProperty
    private final Reference<Version> version;
    @JsonProperty
    private final List<Content> ancestors;
    @JsonProperty
    private final List<OperationCheckResult> operations;
    @JsonDeserialize(contentAs=PageResponseImpl.class)
    @JsonProperty
    @GraphQLIgnore
    private final Map<ContentType, PageResponse<Content>> children;
    @JsonDeserialize(contentAs=PageResponseImpl.class)
    @JsonProperty
    @GraphQLTypeName(value="ContentsByType")
    @GraphQLIgnore
    private final Map<ContentType, PageResponse<Content>> descendants;
    @JsonDeserialize(using=InternalDeserializers.ContainerMapDeserializer.class)
    @JsonProperty
    private final Reference<? extends Container> container;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="ContentBodyPerRepresentation")
    private final Map<ContentRepresentation, ContentBody> body;
    @JsonDeserialize(as=EnrichableMap.class, contentUsing=MetadataValueDeserializer.class)
    @JsonProperty
    private final Map<String, Object> metadata;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    private final Map<String, Object> extensions;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    @GraphQLTypeName(value="ContentRestrictions")
    private final Map<OperationKey, ContentRestriction> restrictions;

    @Deprecated
    public static ContentId getContentId(Reference<Content> contentRef) {
        return Content.getSelector(contentRef).getId();
    }

    public static ContentSelector getSelector(Reference<Content> contentRef) {
        if (contentRef == null || !contentRef.exists()) {
            return ContentSelector.UNSET;
        }
        return ContentSelector.builder().id((ContentId)contentRef.getIdProperty(IdProperties.id)).status((ContentStatus)contentRef.getIdProperty(IdProperties.status)).version(Version.getVersionNumber((Reference)contentRef.getIdProperty(IdProperties.version))).build();
    }

    public static ContentBuilder builder() {
        return new ContentBuilder();
    }

    public static ContentBuilder builder(ContentType type) {
        return new ContentBuilder(type);
    }

    public static ContentBuilder builder(ContentType type, long id) {
        return new ContentBuilder(type, id);
    }

    public static ContentBuilder builder(ContentType type, ContentId id) {
        return new ContentBuilder(type, id);
    }

    public static ContentBuilder builder(Content content) {
        return new ContentBuilder(content.type).id(content.id).status(content.status).title(content.title).version(content.version).space(content.space).ancestors(content.ancestors).operations(content.operations).children(content.children).descendants(content.descendants).body(content.body).container(content.container).history(content.history).metadata(content.metadata).extensions(content.extensions).restrictions(content.restrictions);
    }

    @Deprecated
    public static Reference<Content> buildReference(ContentId id) {
        if (id == null) {
            return Reference.empty(Content.class);
        }
        return Reference.collapsed(Content.builder().id(id).build());
    }

    public static Reference<Content> buildReference(ContentSelector selector) {
        if (selector == null) {
            return Reference.empty(Content.class);
        }
        return Reference.collapsed(Content.builder().id(selector.getId()).status(selector.getStatus()).version(Version.buildReference(selector.getVersion())).build());
    }

    @JsonCreator
    private Content() {
        this(Content.builder());
    }

    private Content(ContentBuilder builder) {
        this.id = builder.id;
        this.status = builder.status != null ? builder.status : ContentStatus.CURRENT;
        this.space = Reference.orEmpty(builder.space, Space.class);
        this.type = builder.type;
        this.title = builder.title;
        this.links = Collections.unmodifiableMap(builder.links);
        this.history = Reference.orEmpty(builder.history, History.class);
        this.container = builder.container == null ? Reference.empty(Container.class) : builder.container;
        this.ancestors = builder.ancestors.build();
        this.operations = builder.operations.build();
        this.children = BuilderUtils.modelMap(builder.children);
        this.descendants = BuilderUtils.modelMap(builder.descendants);
        this.body = BuilderUtils.modelMap(builder.bodyMapBuilder);
        this.metadata = BuilderUtils.modelMap(builder.metadata);
        this.extensions = BuilderUtils.modelMap(builder.extensions);
        this.version = Reference.orEmpty(builder.version, Version.class);
        this.restrictions = BuilderUtils.modelMap(builder.restrictions);
    }

    @JsonIgnore
    public ContentSelector getSelector() {
        return ContentSelector.from(this);
    }

    public ContentId getId() {
        return this.id;
    }

    public ContentStatus getStatus() {
        return this.status != null ? this.status : ContentStatus.CURRENT;
    }

    public ContentType getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    @JsonIgnore
    public Map<LinkType, Link> getLinks() {
        return this.links;
    }

    public Reference<History> getHistoryRef() {
        if (this.history == null) {
            return Reference.empty(History.class);
        }
        return this.history;
    }

    public History getHistory() {
        return this.getHistoryRef().get();
    }

    public Reference<Space> getSpaceRef() {
        if (this.space == null) {
            return Reference.empty(Space.class);
        }
        return this.space;
    }

    public Space getSpace() {
        return this.getSpaceRef().get();
    }

    public Container getContainer() {
        return this.getContainerRef().get();
    }

    public Reference<? extends Container> getContainerRef() {
        if (this.container == null) {
            return Reference.empty(Container.class);
        }
        if (this.container.isExpanded() && this.container.exists() && this.container.get() instanceof ContainerMap) {
            ContainerMap containerMap = (ContainerMap)this.container.get();
            return Reference.to(containerMap.convertTo(this.getContainerType()));
        }
        return this.container;
    }

    protected Class<? extends Container> getContainerType() {
        if (this.type.equals(ContentType.PAGE) || this.type.equals(ContentType.BLOG_POST)) {
            return Space.class;
        }
        return Content.class;
    }

    @JsonIgnore
    public ContentId getParentId() {
        return this.getOptionalParent().map(Content::getId).orElse(ContentId.UNSET);
    }

    @JsonIgnore
    @Deprecated
    public Option<Content> getParent() {
        return FugueConversionUtil.toComOption(this.getOptionalParent());
    }

    @JsonIgnore
    public Optional<Content> getOptionalParent() {
        List<Content> ancestors = this.getAncestors();
        return ancestors.stream().findFirst();
    }

    public List<Content> getAncestors() {
        return this.ancestors != null ? this.ancestors : Collections.emptyList();
    }

    public List<OperationCheckResult> getOperations() {
        return this.operations != null ? this.operations : Collections.emptyList();
    }

    public Map<ContentType, PageResponse<Content>> getChildren() {
        return this.children;
    }

    public Map<ContentType, PageResponse<Content>> getDescendants() {
        return this.descendants;
    }

    public Map<OperationKey, ContentRestriction> getRestrictions() {
        return this.restrictions;
    }

    public Map<ContentRepresentation, ContentBody> getBody() {
        return this.body != null ? this.body : Collections.emptyMap();
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public Map<String, Object> getExtensions() {
        return this.extensions != null ? this.extensions : Collections.emptyMap();
    }

    public Object getExtension(String extensionKey) {
        return this.getExtensions().get(extensionKey);
    }

    public Reference<Version> getVersionRef() {
        if (this.version == null) {
            return Reference.empty(Version.class);
        }
        return this.version;
    }

    public Version getVersion() {
        return this.getVersionRef().get();
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().content(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Content content = (Content)o;
        if (this.id == null || this.type == null) {
            return false;
        }
        return Objects.equals(this.id, content.id) && Objects.equals(this.type, content.type) && this.getVersionRef().equals(content.getVersionRef());
    }

    public int hashCode() {
        return Objects.hash(this.id, this.type, this.getVersionRef());
    }

    public String toString() {
        return "Content{id='" + this.id + '\'' + ", type=" + this.type + ", title='" + this.title + '\'' + ", status=" + this.status + ", space=" + this.space + ", history=" + this.history + ", version=" + this.version + ", ancestors=" + this.ancestors + ", container=" + this.container + '}';
    }

    public static class Expansions {
        public static final String ANCESTORS = "ancestors";
        public static final String BODY = "body";
        public static final String CHILDREN = "children";
        public static final String CONTAINER = "container";
        public static final String DESCENDANTS = "descendants";
        public static final String HISTORY = "history";
        public static final String METADATA = "metadata";
        public static final String OPERATIONS = "operations";
        public static final String PERMISSIONS = "permissions";
        public static final String SPACE = "space";
        public static final String STATUS = "status";
        public static final String VERSION = "version";
        public static final String RESTRICTIONS = "restrictions";
    }

    public static class ContentBuilder {
        private ContentId id = null;
        private ContentType type = null;
        private ContentStatus status = null;
        private String title = null;
        private final Map<LinkType, Link> links = new HashMap<LinkType, Link>();
        private Reference<History> history = null;
        private Reference<Space> space = null;
        private Reference<? extends Container> container = null;
        private ModelListBuilder<Content> ancestors = ModelListBuilder.newExpandedInstance();
        private ModelListBuilder<OperationCheckResult> operations = ModelListBuilder.newExpandedInstance();
        private ModelMapBuilder<ContentRepresentation, ContentBody> bodyMapBuilder = ModelMapBuilder.newExpandedInstance();
        private ModelMapBuilder<String, Object> metadata = ModelMapBuilder.newExpandedInstance();
        private ModelMapBuilder<String, Object> extensions = ModelMapBuilder.newExpandedInstance();
        private Reference<Version> version = null;
        private ModelMapBuilder<ContentType, PageResponse<Content>> children = ModelMapBuilder.newExpandedInstance();
        private ModelMapBuilder<ContentType, PageResponse<Content>> descendants = ModelMapBuilder.newExpandedInstance();
        private ModelMapBuilder<OperationKey, ContentRestriction> restrictions = ModelMapBuilder.newExpandedInstance();

        private ContentBuilder() {
        }

        protected ContentBuilder(ContentType type) {
            this.type = type;
        }

        protected ContentBuilder(ContentType type, ContentId id) {
            this.type = type;
            this.id = id;
        }

        protected ContentBuilder(ContentType type, long id) {
            this.type = type;
            this.id = ContentId.of(type, id);
        }

        public ContentBuilder collapsed() {
            this.space = Reference.collapsed(Space.class);
            this.container = Reference.collapsed(Container.class);
            this.history = Reference.collapsed(History.class);
            this.version = Reference.collapsed(Version.class);
            this.ancestors = ModelListBuilder.newInstance();
            this.operations = ModelListBuilder.newInstance();
            this.bodyMapBuilder = ModelMapBuilder.newInstance();
            this.children = ModelMapBuilder.newInstance();
            this.descendants = ModelMapBuilder.newInstance();
            this.metadata = ModelMapBuilder.newInstance();
            this.extensions = ModelMapBuilder.newInstance();
            this.restrictions = ModelMapBuilder.newInstance();
            return this;
        }

        public ContentBuilder type(ContentType type) {
            this.type = type;
            return this;
        }

        public Content build() {
            return new Content(this);
        }

        public ContentBuilder id(ContentId id) {
            this.id = id;
            return this;
        }

        public ContentBuilder status(ContentStatus status) {
            this.status = status;
            return this;
        }

        public ContentBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ContentBuilder addLink(Link link) {
            this.links.put(link.getType(), link);
            return this;
        }

        public ContentBuilder addLink(LinkType type, String path) {
            return this.addLink(new Link(type, path));
        }

        public ContentBuilder history(Reference<History> history) {
            this.history = history;
            return this;
        }

        public ContentBuilder history(History history) {
            this.history = Reference.to(history);
            return this;
        }

        public ContentBuilder space(String spaceKey) {
            return this.space(Space.builder().key(spaceKey).build());
        }

        public ContentBuilder space(Space space) {
            return this.space(Reference.to(space));
        }

        public ContentBuilder space(Reference<Space> space) {
            this.space = space;
            return this;
        }

        public ContentBuilder container(Container container) {
            return this.container(Reference.to(container));
        }

        public ContentBuilder container(Reference<? extends Container> container) {
            this.container = container;
            return this;
        }

        public ContentBuilder parent(Content parent) {
            if (parent == null) {
                this.ancestors = ModelListBuilder.newExpandedInstance();
                return this;
            }
            if (this.type != null && !this.type.equals(parent.getType())) {
                throw new IllegalArgumentException(String.format("parent (%s) must be same type as this content (%s)", parent.getType(), this.type));
            }
            this.ancestors.copy(Collections.singletonList(parent));
            return this;
        }

        public ContentBuilder ancestors(Iterable<Content> ancestors) {
            this.ancestors.putAll(ancestors);
            return this;
        }

        public ContentBuilder operations(Iterable<OperationCheckResult> operations) {
            this.operations.putAll(operations);
            return this;
        }

        public ContentBuilder children(Map<ContentType, PageResponse<Content>> children) {
            this.children.copy(children);
            return this;
        }

        public ContentBuilder descendants(Map<ContentType, PageResponse<Content>> descendants) {
            this.descendants.copy(descendants);
            return this;
        }

        public ContentBuilder body(String value, ContentRepresentation format) {
            ContentBody contentBody = ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().representation(format)).value(value)).content(ContentSelector.fromId(this.id)).build();
            this.bodyMapBuilder.put(format, contentBody);
            return this;
        }

        public ContentBuilder body(Map<ContentRepresentation, ContentBody> body) {
            this.bodyMapBuilder.copy(body);
            return this;
        }

        public ContentBuilder body(ContentBody body) {
            this.bodyMapBuilder.put(body.getRepresentation(), body);
            return this;
        }

        public ContentBuilder metadata(Map<String, Object> metadata) {
            this.metadata.copy(metadata);
            return this;
        }

        public ContentBuilder extensions(Map<String, Object> extensions) {
            this.extensions.copy(extensions);
            return this;
        }

        public ContentBuilder extension(String key, Object value) {
            this.extensions.put(key, value);
            return this;
        }

        public ContentBuilder version(Version version) {
            this.version = Reference.to(version);
            return this;
        }

        public ContentBuilder version(Reference<Version> version) {
            this.version = version;
            return this;
        }

        public ContentBuilder restrictions(Map<OperationKey, ContentRestriction> restrictionsByOperationMap) {
            if (this.restrictions != null) {
                this.restrictions.copy(restrictionsByOperationMap);
            }
            return this;
        }
    }

    public static enum IdProperties {
        id,
        status,
        version;

    }
}

