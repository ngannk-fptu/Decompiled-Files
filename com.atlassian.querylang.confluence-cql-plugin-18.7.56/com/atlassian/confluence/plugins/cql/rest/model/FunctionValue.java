/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.cql.rest.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FunctionValue {
    @JsonProperty
    private final String functionName;
    @JsonProperty
    private final List<String> parameters;

    @JsonCreator
    private FunctionValue() {
        this(null, (List<String>)ImmutableList.of());
    }

    public FunctionValue(String functionName, List<String> parameters) {
        this.functionName = functionName;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FunctionValue)) {
            return false;
        }
        FunctionValue other = (FunctionValue)obj;
        return Objects.equals(other.functionName, this.functionName) && Objects.equals(other.parameters, this.parameters);
    }

    public int hashCode() {
        return Objects.hash(this.functionName, this.parameters);
    }
}

