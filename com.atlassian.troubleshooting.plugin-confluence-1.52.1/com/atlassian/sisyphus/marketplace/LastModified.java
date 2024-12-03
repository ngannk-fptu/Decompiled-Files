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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder(value={"by", "date"})
public class LastModified {
    @JsonProperty(value="by")
    private String by;
    @JsonProperty(value="date")
    private String date;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty(value="by")
    public String getBy() {
        return this.by;
    }

    @JsonProperty(value="by")
    public void setBy(String by) {
        this.by = by;
    }

    @JsonProperty(value="date")
    public String getDate() {
        return this.date;
    }

    @JsonProperty(value="date")
    public void setDate(String date) {
        this.date = date;
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

