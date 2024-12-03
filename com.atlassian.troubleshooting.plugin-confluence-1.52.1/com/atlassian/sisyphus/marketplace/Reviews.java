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

import com.atlassian.sisyphus.marketplace.Link;
import com.atlassian.sisyphus.marketplace.Review;
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
@JsonPropertyOrder(value={"links", "reviews"})
public class Reviews {
    @JsonProperty(value="links")
    private List<Link> links = new ArrayList<Link>();
    @JsonProperty(value="reviews")
    private List<Review> reviews = new ArrayList<Review>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="links")
    public List<Link> getLinks() {
        return this.links;
    }

    @JsonProperty(value="links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(value="reviews")
    public List<Review> getReviews() {
        return this.reviews;
    }

    @JsonProperty(value="reviews")
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
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

