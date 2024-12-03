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

import com.atlassian.sisyphus.marketplace.Version;
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
@JsonPropertyOrder(value={"versions", "count", "draftMarketplaceVersions", "submittedMarketplaceVersions", "rejectedMarketplaceVersions"})
public class Versions {
    @JsonProperty(value="versions")
    private List<Version> versions = new ArrayList<Version>();
    @JsonProperty(value="count")
    private Long count;
    @JsonProperty(value="draftMarketplaceVersions")
    private List<Object> draftMarketplaceVersions = new ArrayList<Object>();
    @JsonProperty(value="submittedMarketplaceVersions")
    private List<Object> submittedMarketplaceVersions = new ArrayList<Object>();
    @JsonProperty(value="rejectedMarketplaceVersions")
    private List<Object> rejectedMarketplaceVersions = new ArrayList<Object>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="versions")
    public List<Version> getVersions() {
        return this.versions;
    }

    @JsonProperty(value="versions")
    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    @JsonProperty(value="count")
    public Long getCount() {
        return this.count;
    }

    @JsonProperty(value="count")
    public void setCount(Long count) {
        this.count = count;
    }

    @JsonProperty(value="draftMarketplaceVersions")
    public List<Object> getDraftMarketplaceVersions() {
        return this.draftMarketplaceVersions;
    }

    @JsonProperty(value="draftMarketplaceVersions")
    public void setDraftMarketplaceVersions(List<Object> draftMarketplaceVersions) {
        this.draftMarketplaceVersions = draftMarketplaceVersions;
    }

    @JsonProperty(value="submittedMarketplaceVersions")
    public List<Object> getSubmittedMarketplaceVersions() {
        return this.submittedMarketplaceVersions;
    }

    @JsonProperty(value="submittedMarketplaceVersions")
    public void setSubmittedMarketplaceVersions(List<Object> submittedMarketplaceVersions) {
        this.submittedMarketplaceVersions = submittedMarketplaceVersions;
    }

    @JsonProperty(value="rejectedMarketplaceVersions")
    public List<Object> getRejectedMarketplaceVersions() {
        return this.rejectedMarketplaceVersions;
    }

    @JsonProperty(value="rejectedMarketplaceVersions")
    public void setRejectedMarketplaceVersions(List<Object> rejectedMarketplaceVersions) {
        this.rejectedMarketplaceVersions = rejectedMarketplaceVersions;
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

