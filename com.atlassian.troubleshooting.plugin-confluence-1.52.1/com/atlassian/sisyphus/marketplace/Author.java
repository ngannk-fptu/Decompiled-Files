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
@JsonPropertyOrder(value={"id", "links", "name"})
public class Author {
    @JsonProperty(value="id")
    private Long id;
    @JsonProperty(value="links")
    private List<Link> links = new ArrayList<Link>();
    @JsonProperty(value="name")
    private String name;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="id")
    public Long getId() {
        return this.id;
    }

    @JsonProperty(value="id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty(value="links")
    public List<Link> getLinks() {
        return this.links;
    }

    @JsonProperty(value="links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @JsonProperty(value="name")
    public void setName(String name) {
        this.name = name;
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

