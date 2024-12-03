/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.search.ResourceType
 *  com.atlassian.sal.api.search.SearchMatch
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.api.model.search;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.ContainerSummary;
import com.atlassian.confluence.api.model.search.ContentSearchResult;
import com.atlassian.confluence.api.model.search.SpaceSearchResult;
import com.atlassian.confluence.api.model.search.UserSearchResult;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.serialization.RestEnrichableProperty;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import com.atlassian.sal.api.search.ResourceType;
import com.atlassian.sal.api.search.SearchMatch;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="entityType")
@JsonSubTypes(value={@JsonSubTypes.Type(value=ContentSearchResult.class, name="content"), @JsonSubTypes.Type(value=UserSearchResult.class, name="user"), @JsonSubTypes.Type(value=SpaceSearchResult.class, name="space")})
@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public abstract class SearchResult<T>
implements SearchMatch {
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String excerpt;
    @JsonProperty
    private final String url;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=ContainerSummary.class)
    @JsonProperty
    private final Reference<ContainerSummary> resultParentContainer;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=ContainerSummary.class)
    @JsonProperty
    private final Reference<ContainerSummary> resultGlobalContainer;
    @JsonIgnore
    @RestEnrichableProperty
    private final String entityType;
    @JsonProperty
    private final String iconCssClass;
    @JsonProperty
    private final DateTime lastModified;
    @JsonProperty
    private final String friendlyLastModified;

    @JsonCreator
    SearchResult(String entityType) {
        this.title = "";
        this.excerpt = "";
        this.url = "";
        this.resultGlobalContainer = Reference.empty(ContainerSummary.class);
        this.resultParentContainer = Reference.empty(ContainerSummary.class);
        this.entityType = entityType;
        this.iconCssClass = "";
        this.lastModified = null;
        this.friendlyLastModified = null;
    }

    SearchResult(Builder<T> builder, String entityType) {
        this.title = ((Builder)builder).title;
        this.excerpt = ((Builder)builder).bodyExcerpt;
        this.url = ((Builder)builder).url;
        this.resultParentContainer = ((Builder)builder).entityParentContainer;
        this.resultGlobalContainer = ((Builder)builder).entityGlobalContainer;
        this.entityType = entityType;
        this.iconCssClass = ((Builder)builder).iconCssClass;
        this.lastModified = ((Builder)builder).lastModified;
        this.friendlyLastModified = ((Builder)builder).friendlyLastModified;
    }

    public T getEntity() {
        return this.getEntityRef().get();
    }

    public abstract Reference<T> getEntityRef();

    public final String getEntityType() {
        return this.entityType;
    }

    public String getTitle() {
        return this.title;
    }

    public String getExcerpt() {
        return this.excerpt;
    }

    public String getUrl() {
        return this.url;
    }

    public String getIconCssClass() {
        return this.iconCssClass;
    }

    @Deprecated
    public DateTime getLastModified() {
        return this.lastModified;
    }

    @JsonIgnore
    public OffsetDateTime getLastModifiedAt() {
        return JodaTimeUtils.convert(this.lastModified);
    }

    public String getFriendlyLastModified() {
        return this.friendlyLastModified;
    }

    public Reference<ContainerSummary> getResultParentRef() {
        return this.resultParentContainer;
    }

    @Deprecated
    @JsonIgnore
    public ContainerSummary getResultParent() {
        return this.getResultParentContainer();
    }

    public ContainerSummary getResultParentContainer() {
        return this.resultParentContainer.get();
    }

    public Reference<ContainerSummary> getResultGlobalContainerRef() {
        return this.resultGlobalContainer;
    }

    public ContainerSummary getResultGlobalContainer() {
        return this.resultGlobalContainer.get();
    }

    public ResourceType getResourceType() {
        return null;
    }

    public static <T> Builder<T> builder(T entity) {
        Objects.requireNonNull(entity);
        return new Builder<T>(entity);
    }

    public static class Builder<T> {
        private String title;
        private String bodyExcerpt;
        private String url;
        private Reference<T> entityRef;
        private String iconCssClass;
        private DateTime lastModified;
        private String friendlyLastModified;
        private Reference<ContainerSummary> entityParentContainer = Reference.empty(ContainerSummary.class);
        private Reference<ContainerSummary> entityGlobalContainer = Reference.empty(ContainerSummary.class);

        protected Builder(T entity) {
            this.entityRef = Reference.to(entity);
        }

        public Builder entityParentContainer(ContainerSummary entity) {
            this.entityParentContainer = Reference.orEmpty(entity, ContainerSummary.class);
            return this;
        }

        public Builder resultGlobalContainer(ContainerSummary space) {
            this.entityGlobalContainer = Reference.orEmpty(space, ContainerSummary.class);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder bodyExcerpt(String excerpt) {
            this.bodyExcerpt = excerpt;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder iconCssClass(String iconCssClass) {
            this.iconCssClass = iconCssClass;
            return this;
        }

        @Deprecated
        public Builder lastModified(DateTime lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder lastModified(OffsetDateTime lastModified) {
            this.lastModified = JodaTimeUtils.convert(lastModified);
            return this;
        }

        public Builder friendlyLastModified(String friendlyLastModified) {
            this.friendlyLastModified = friendlyLastModified;
            return this;
        }

        public SearchResult<T> build() {
            T entity = this.entityRef.get();
            if (entity instanceof Content) {
                return new ContentSearchResult(this);
            }
            if (entity instanceof Space) {
                return new SpaceSearchResult(this);
            }
            if (entity instanceof User) {
                return new UserSearchResult(this);
            }
            throw new IllegalStateException("Cannot build search result to entity : " + entity.getClass());
        }

        protected final Reference<T> getEntityRef() {
            return this.entityRef;
        }
    }
}

