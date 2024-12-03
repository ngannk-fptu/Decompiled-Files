/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAnyGetter
 *  com.fasterxml.jackson.annotation.JsonAnySetter
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.annotation.JsonPropertyOrder
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize$Inclusion
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.sisyphus.marketplace;

import com.atlassian.sisyphus.marketplace.Author;
import com.atlassian.sisyphus.marketplace.Link;
import com.atlassian.sisyphus.marketplace.Response;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder(value={"links", "pluginName", "author", "stars", "date", "review", "response", "flagged", "flaggedByUser", "flags"})
public class Review {
    @JsonProperty(value="links")
    private List<Link> links = new ArrayList<Link>();
    @JsonProperty(value="pluginName")
    private String pluginName;
    @JsonProperty(value="author")
    private Author author;
    @JsonProperty(value="stars")
    private Long stars;
    @JsonProperty(value="date")
    private String date;
    @JsonProperty(value="review")
    private String review;
    @JsonProperty(value="response")
    private Response response;
    @JsonProperty(value="flagged")
    private Boolean flagged;
    @JsonProperty(value="flaggedByUser")
    private Boolean flaggedByUser;
    @JsonProperty(value="flags")
    private List<Object> flags = new ArrayList<Object>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="links")
    public List<Link> getLinks() {
        return this.links;
    }

    @JsonProperty(value="links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(value="pluginName")
    public String getPluginName() {
        return this.pluginName;
    }

    @JsonProperty(value="pluginName")
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    @JsonProperty(value="author")
    public Author getAuthor() {
        return this.author;
    }

    @JsonProperty(value="author")
    public void setAuthor(Author author) {
        this.author = author;
    }

    @JsonProperty(value="stars")
    public Long getStars() {
        return this.stars;
    }

    @JsonProperty(value="stars")
    public void setStars(Long stars) {
        this.stars = stars;
    }

    @JsonProperty(value="date")
    public String getDate() {
        return this.date;
    }

    @JsonProperty(value="date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty(value="review")
    public String getReview() {
        return this.review;
    }

    @JsonProperty(value="review")
    public void setReview(String review) {
        this.review = review;
    }

    @JsonProperty(value="response")
    public Response getResponse() {
        return this.response;
    }

    @JsonProperty(value="response")
    public void setResponse(Response response) {
        this.response = response;
    }

    @JsonProperty(value="flagged")
    public Boolean getFlagged() {
        return this.flagged;
    }

    @JsonProperty(value="flagged")
    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    @JsonProperty(value="flaggedByUser")
    public Boolean getFlaggedByUser() {
        return this.flaggedByUser;
    }

    @JsonProperty(value="flaggedByUser")
    public void setFlaggedByUser(Boolean flaggedByUser) {
        this.flaggedByUser = flaggedByUser;
    }

    @JsonProperty(value="flags")
    public List<Object> getFlags() {
        return this.flags;
    }

    @JsonProperty(value="flags")
    public void setFlags(List<Object> flags) {
        this.flags = flags;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

