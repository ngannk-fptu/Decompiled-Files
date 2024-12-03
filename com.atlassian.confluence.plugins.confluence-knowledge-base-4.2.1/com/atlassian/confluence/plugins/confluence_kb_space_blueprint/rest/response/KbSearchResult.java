/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.search.api.model.SearchResultContainer
 *  com.google.common.collect.Maps
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response;

import com.atlassian.confluence.plugins.search.api.model.SearchResultContainer;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class KbSearchResult {
    private final Long id;
    private final String title;
    private final String bodyTextHighlights;
    private final String url;
    private final String contentType;
    private final Map<String, String> metadata;
    private final String spaceKey;
    private final String spaceName;
    private final SearchResultContainer searchResultContainer;

    @JsonCreator
    public KbSearchResult(@JsonProperty(value="id") Long id, @JsonProperty(value="title") String title, @JsonProperty(value="bodyTextHighlights") String bodyTextHighlights, @JsonProperty(value="url") String url, @JsonProperty(value="contentType") String contentType, @JsonProperty(value="metadata") Map<String, String> metadata, @JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="spaceName") String spaceName, @JsonProperty(value="searchResultContainer") SearchResultContainer searchResultContainer) {
        this.id = id;
        this.title = title;
        this.bodyTextHighlights = bodyTextHighlights;
        this.url = url;
        this.contentType = contentType;
        this.metadata = metadata;
        this.spaceKey = spaceKey;
        this.spaceName = spaceName;
        this.searchResultContainer = searchResultContainer;
    }

    @JsonProperty(value="id")
    public Long getId() {
        return this.id;
    }

    @JsonProperty(value="title")
    public String getTitle() {
        return this.title;
    }

    @JsonProperty(value="bodyTextHighlights")
    public String getBodyTextHighlights() {
        return this.bodyTextHighlights;
    }

    @JsonProperty(value="url")
    public String getUrl() {
        return this.url;
    }

    @JsonProperty(value="contentType")
    public String getContentType() {
        return this.contentType;
    }

    @JsonProperty(value="metadata")
    public Map<String, String> getMetadata() {
        return this.metadata == null ? Maps.newHashMap() : this.metadata;
    }

    @JsonProperty(value="spaceKey")
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @JsonProperty(value="spaceName")
    public String getSpaceName() {
        return this.spaceName;
    }

    @JsonProperty(value="searchResultContainer")
    public SearchResultContainer getSearchResultContainer() {
        return this.searchResultContainer;
    }
}

