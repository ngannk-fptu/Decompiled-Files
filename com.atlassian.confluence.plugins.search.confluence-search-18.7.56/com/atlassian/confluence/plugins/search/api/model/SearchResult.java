/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.search.api.model;

import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchResultContainer;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class SearchResult {
    private long id;
    private String title;
    private String bodyTextHighlights;
    private String url;
    private SearchResultContainer searchResultContainer;
    private String friendlyDate;
    @XmlTransient
    private SearchExplanation explanation;
    private String contentType;
    private Map<String, String> metadata;

    private SearchResult() {
    }

    public SearchResult(long id, String contentType, String title, String bodyTextHighlights, String url, SearchResultContainer searchResultContainer, String friendlyDate, SearchExplanation explanation, Map<String, String> metadata) {
        this.id = id;
        this.contentType = contentType;
        this.title = title;
        this.bodyTextHighlights = bodyTextHighlights;
        this.url = url;
        this.searchResultContainer = searchResultContainer;
        this.friendlyDate = friendlyDate;
        this.explanation = explanation;
        this.metadata = metadata;
        this.explanation = explanation;
    }

    @HtmlSafe
    public long getId() {
        return this.id;
    }

    @HtmlSafe
    public String getTitle() {
        return this.title;
    }

    @HtmlSafe
    public String getBodyTextHighlights() {
        return this.bodyTextHighlights;
    }

    public String getUrl() {
        return this.url;
    }

    public SearchResultContainer getSearchResultContainer() {
        return this.searchResultContainer;
    }

    public String getFriendlyDate() {
        return this.friendlyDate;
    }

    public SearchExplanation getExplanation() {
        return this.explanation;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }
}

