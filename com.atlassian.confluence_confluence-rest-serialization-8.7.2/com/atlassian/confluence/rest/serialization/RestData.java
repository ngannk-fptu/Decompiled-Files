/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class RestData {
    @JsonProperty
    private final OperationKey operationKey;
    @JsonProperty
    private final Map<String, String> params;

    private RestData(RestDataBuilder builder) {
        this.operationKey = builder.operationKey;
        this.params = builder.parameters.build();
    }

    @JsonCreator
    private RestData() {
        this(RestData.builder());
    }

    public OperationKey getOperationKey() {
        return this.operationKey;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public static RestDataBuilder builder() {
        return new RestDataBuilder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RestData restData = (RestData)o;
        return Objects.equals(this.operationKey, restData.operationKey) && Objects.equals(this.params, restData.params);
    }

    public int hashCode() {
        return this.operationKey.hashCode() + this.params.hashCode();
    }

    public static class RestDataBuilder {
        private OperationKey operationKey;
        private ImmutableMap.Builder<String, String> parameters = ImmutableMap.builder();

        private RestDataBuilder() {
        }

        public RestDataBuilder params(Map<String, String> propertyBag) {
            this.parameters.putAll(propertyBag);
            return this;
        }

        public RestDataBuilder operation(OperationKey operationKey) {
            this.operationKey = operationKey;
            return this;
        }

        public RestDataBuilder addParam(String key, String value) {
            this.parameters.put((Object)key, (Object)value);
            return this;
        }

        public RestData build() {
            Objects.requireNonNull(this.operationKey);
            return new RestData(this);
        }
    }
}

