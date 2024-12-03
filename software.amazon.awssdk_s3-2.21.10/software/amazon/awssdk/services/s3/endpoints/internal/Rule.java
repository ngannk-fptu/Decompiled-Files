/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.s3.endpoints.internal.Condition;
import software.amazon.awssdk.services.s3.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.s3.endpoints.internal.EndpointRule;
import software.amazon.awssdk.services.s3.endpoints.internal.ErrorRule;
import software.amazon.awssdk.services.s3.endpoints.internal.Literal;
import software.amazon.awssdk.services.s3.endpoints.internal.RuleValueVisitor;
import software.amazon.awssdk.services.s3.endpoints.internal.TreeRule;

@SdkInternalApi
public abstract class Rule {
    public static final String CONDITIONS = "conditions";
    public static final String DOCUMENTATION = "documentation";
    public static final String ENDPOINT = "endpoint";
    public static final String ERROR = "error";
    public static final String TREE = "tree";
    public static final String RULES = "rules";
    public static final String TYPE = "type";
    protected final List<Condition> conditions;
    protected final String documentation;

    protected Rule(Builder builder) {
        this.conditions = builder.conditions;
        this.documentation = builder.documentation;
    }

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public abstract <T> T accept(RuleValueVisitor<T> var1);

    public static Rule fromNode(JsonNode node) {
        String type;
        Map objNode = node.asObject();
        Builder builder = Rule.builder();
        ((JsonNode)objNode.get(CONDITIONS)).asArray().forEach(cn -> builder.addCondition(Condition.fromNode(cn)));
        JsonNode documentation = (JsonNode)objNode.get(DOCUMENTATION);
        if (documentation != null) {
            builder.documentation(documentation.asString());
        }
        switch (type = ((JsonNode)objNode.get(TYPE)).asString()) {
            case "endpoint": {
                return builder.endpoint(EndpointResult.fromNode((JsonNode)objNode.get(ENDPOINT)));
            }
            case "error": {
                return builder.error(((JsonNode)objNode.get(ERROR)).asString());
            }
            case "tree": {
                return builder.treeRule(((JsonNode)objNode.get(RULES)).asArray().stream().map(Rule::fromNode).collect(Collectors.toList()));
            }
        }
        throw new IllegalStateException("Unexpected rule type: " + type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String documentation;
        private final List<Condition> conditions = new ArrayList<Condition>();

        public Builder addCondition(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder documentation(String documentation) {
            this.documentation = documentation;
            return this;
        }

        public EndpointRule endpoint(EndpointResult endpoint) {
            return new EndpointRule(this, endpoint);
        }

        public ErrorRule error(String error) {
            return new ErrorRule(this, Literal.fromStr(error));
        }

        public TreeRule treeRule(List<Rule> rules) {
            return new TreeRule(this, rules);
        }
    }
}

