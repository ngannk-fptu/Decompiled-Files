/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestedClaimAdditionalInfo {
    @JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
    @JsonProperty(value="essential")
    boolean essential;
    @JsonProperty(value="value")
    String value;
    @JsonProperty(value="values")
    List<String> values;

    public boolean isEssential() {
        return this.essential;
    }

    public String getValue() {
        return this.value;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void setEssential(boolean essential) {
        this.essential = essential;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public RequestedClaimAdditionalInfo(boolean essential, String value, List<String> values) {
        this.essential = essential;
        this.value = value;
        this.values = values;
    }
}

