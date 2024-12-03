/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$IdProperties
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.reference.ExpandedReference
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.search.ContainerSummary
 *  com.atlassian.confluence.api.model.search.ContentSearchResult
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.serialization.RestEnrichable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.ContainerSummary;
import com.atlassian.confluence.api.model.search.ContentSearchResult;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Optional;
import java.util.function.Function;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
@Internal
public class CQLSearchResult {
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String excerpt;
    @JsonProperty
    private final String url;
    @JsonProperty
    private final String iconCssClass;
    @JsonProperty
    private final DateTime lastModified;
    @JsonProperty
    private final String friendlyLastModified;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private Reference<Content> content;
    @JsonProperty
    String entityType;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=ContainerSummary.class)
    @JsonProperty
    private final Reference<ContainerSummary> resultParentContainer;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=ContainerSummary.class)
    @JsonProperty
    private final Reference<ContainerSummary> resultGlobalContainer;
    @JsonProperty
    private final Float pageScore;

    public CQLSearchResult(SearchResult searchResult, Function<Long, Float> getScore) {
        this.title = searchResult.getTitle();
        this.excerpt = searchResult.getExcerpt();
        this.url = searchResult.getUrl();
        this.iconCssClass = searchResult.getIconCssClass();
        this.lastModified = searchResult.getLastModified();
        this.friendlyLastModified = searchResult.getFriendlyLastModified();
        this.entityType = searchResult.getEntityType();
        if (searchResult instanceof ContentSearchResult) {
            this.content = searchResult.getEntityRef();
        }
        this.resultParentContainer = searchResult.getResultParentRef();
        this.resultGlobalContainer = searchResult.getResultGlobalContainerRef();
        this.pageScore = CQLSearchResult.getContentId(searchResult).map(getScore).orElse(null);
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

    public DateTime getLastModified() {
        return this.lastModified;
    }

    public String getFriendlyLastModified() {
        return this.friendlyLastModified;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public Float getPageScore() {
        return this.pageScore;
    }

    public ContainerSummary getResultParentContainer() {
        return this.resultParentContainer == null ? null : (ContainerSummary)this.resultParentContainer.get();
    }

    public ContainerSummary getResultGlobalContainer() {
        return this.resultGlobalContainer == null ? null : (ContainerSummary)this.resultGlobalContainer.get();
    }

    public Content getContent() {
        return this.content == null ? null : (Content)this.content.get();
    }

    private static Optional<Long> getContentId(SearchResult searchResult) {
        if (searchResult instanceof ContentSearchResult) {
            ContentId contentId = (ContentId)searchResult.getEntityRef().getIdProperty((Enum)Content.IdProperties.id);
            return Optional.of(contentId.asLong());
        }
        return Optional.empty();
    }
}

