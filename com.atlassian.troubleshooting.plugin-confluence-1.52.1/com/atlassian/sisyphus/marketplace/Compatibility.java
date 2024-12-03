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
 */
package com.atlassian.sisyphus.marketplace;

import com.atlassian.sisyphus.marketplace.Max;
import com.atlassian.sisyphus.marketplace.Min;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder(value={"applicationName", "min", "max"})
public class Compatibility {
    @JsonProperty(value="applicationName")
    private String applicationName;
    @JsonProperty(value="min")
    private Min min;
    @JsonProperty(value="max")
    private Max max;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="applicationName")
    public String getApplicationName() {
        return this.applicationName;
    }

    @JsonProperty(value="applicationName")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @JsonProperty(value="min")
    public Min getMin() {
        return this.min;
    }

    @JsonProperty(value="min")
    public void setMin(Min min) {
        this.min = min;
    }

    @JsonProperty(value="max")
    public Max getMax() {
        return this.max;
    }

    @JsonProperty(value="max")
    public void setMax(Max max) {
        this.max = max;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String toString() {
        return "Compatibility{max=" + this.max + ", min=" + this.min + '}';
    }
}

