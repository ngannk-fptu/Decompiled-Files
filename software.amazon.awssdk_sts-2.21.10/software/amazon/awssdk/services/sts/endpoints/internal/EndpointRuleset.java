/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.sts.endpoints.internal.Parameters;
import software.amazon.awssdk.services.sts.endpoints.internal.Rule;

@SdkInternalApi
public final class EndpointRuleset {
    private static final String SERVICE_ID = "serviceId";
    private static final String VERSION = "version";
    private static final String PARAMETERS = "parameters";
    private static final String RULES = "rules";
    private final String serviceId;
    private final List<Rule> rules;
    private final String version;
    private final Parameters parameters;

    private EndpointRuleset(Builder b) {
        this.serviceId = b.serviceId;
        this.rules = b.rules;
        this.version = b.version;
        this.parameters = b.parameters;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public String getVersion() {
        return this.version;
    }

    public Parameters getParameters() {
        return this.parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static EndpointRuleset fromNode(JsonNode node) {
        JsonNode versionNode;
        Builder b = EndpointRuleset.builder();
        Map obj = node.asObject();
        JsonNode serviceIdNode = (JsonNode)obj.get(SERVICE_ID);
        if (serviceIdNode != null) {
            b.serviceId(serviceIdNode.asString());
        }
        if ((versionNode = (JsonNode)obj.get(VERSION)) != null) {
            b.version(versionNode.asString());
        }
        b.parameters(Parameters.fromNode((JsonNode)obj.get(PARAMETERS)));
        ((JsonNode)obj.get(RULES)).asArray().forEach(rn -> b.addRule(Rule.fromNode(rn)));
        return b.build();
    }

    public String toString() {
        return "EndpointRuleset{serviceId='" + this.serviceId + '\'' + ", rules=" + this.rules + ", version='" + this.version + '\'' + ", parameters=" + this.parameters + '}';
    }

    public static class Builder {
        private String serviceId;
        private final List<Rule> rules = new ArrayList<Rule>();
        private String version;
        private Parameters parameters;

        public Builder serviceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder withDefaultVersion() {
            this.version = "1.0";
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder addRule(Rule rule) {
            this.rules.add(rule);
            return this;
        }

        public Builder parameters(Parameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public EndpointRuleset build() {
            return new EndpointRuleset(this);
        }
    }
}

