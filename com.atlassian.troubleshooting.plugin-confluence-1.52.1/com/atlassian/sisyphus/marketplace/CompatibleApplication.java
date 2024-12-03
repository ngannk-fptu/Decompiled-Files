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
import com.atlassian.sisyphus.marketplace.Status;
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
@JsonPropertyOrder(value={"links", "key", "name", "introduction", "order", "status", "availableOnDemand", "pluginCount"})
public class CompatibleApplication {
    @JsonProperty(value="links")
    private List<Link> links = new ArrayList<Link>();
    @JsonProperty(value="key")
    private String key;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="introduction")
    private String introduction;
    @JsonProperty(value="order")
    private Long order;
    @JsonProperty(value="status")
    private Status status;
    @JsonProperty(value="availableOnDemand")
    private Boolean availableOnDemand;
    @JsonProperty(value="pluginCount")
    private Long pluginCount;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="links")
    public List<Link> getLinks() {
        return this.links;
    }

    @JsonProperty(value="links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(value="key")
    public String getKey() {
        return this.key;
    }

    @JsonProperty(value="key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @JsonProperty(value="name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value="introduction")
    public String getIntroduction() {
        return this.introduction;
    }

    @JsonProperty(value="introduction")
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @JsonProperty(value="order")
    public Long getOrder() {
        return this.order;
    }

    @JsonProperty(value="order")
    public void setOrder(Long order) {
        this.order = order;
    }

    @JsonProperty(value="status")
    public Status getStatus() {
        return this.status;
    }

    @JsonProperty(value="status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty(value="availableOnDemand")
    public Boolean getAvailableOnDemand() {
        return this.availableOnDemand;
    }

    @JsonProperty(value="availableOnDemand")
    public void setAvailableOnDemand(Boolean availableOnDemand) {
        this.availableOnDemand = availableOnDemand;
    }

    @JsonProperty(value="pluginCount")
    public Long getPluginCount() {
        return this.pluginCount;
    }

    @JsonProperty(value="pluginCount")
    public void setPluginCount(Long pluginCount) {
        this.pluginCount = pluginCount;
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

