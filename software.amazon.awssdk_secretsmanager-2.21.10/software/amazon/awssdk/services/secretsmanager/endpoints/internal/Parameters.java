/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Parameter;

@SdkInternalApi
public class Parameters {
    private final List<Parameter> parameters;

    private Parameters(Builder b) {
        this.parameters = b.parameters;
    }

    public List<Parameter> toList() {
        return this.parameters;
    }

    public Optional<Parameter> get(Identifier name) {
        return this.parameters.stream().filter(param -> param.getName().equals(name)).findFirst();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Parameters that = (Parameters)o;
        return this.parameters != null ? this.parameters.equals(that.parameters) : that.parameters == null;
    }

    public int hashCode() {
        return this.parameters != null ? this.parameters.hashCode() : 0;
    }

    public String toString() {
        return "Parameters{parameters=" + this.parameters + '}';
    }

    public static Parameters fromNode(JsonNode node) {
        Map paramsObj = node.asObject();
        Builder b = Parameters.builder();
        paramsObj.forEach((name, obj) -> b.addParameter(Parameter.fromNode(name, obj)));
        return b.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Parameter> parameters = new ArrayList<Parameter>();

        public Builder addParameter(Parameter parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Parameters build() {
            return new Parameters(this);
        }
    }
}

