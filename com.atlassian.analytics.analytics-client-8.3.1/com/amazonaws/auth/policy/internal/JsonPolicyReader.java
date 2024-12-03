/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.internal;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.policy.Action;
import com.amazonaws.auth.policy.Condition;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.PolicyReaderOptions;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonPolicyReader {
    private static final String PRINCIPAL_SCHEMA_USER = "AWS";
    private static final String PRINCIPAL_SCHEMA_SERVICE = "Service";
    private static final String PRINCIPAL_SCHEMA_FEDERATED = "Federated";
    private final PolicyReaderOptions options;

    public JsonPolicyReader() {
        this(new PolicyReaderOptions());
    }

    public JsonPolicyReader(PolicyReaderOptions options) {
        this.options = options;
    }

    public Policy createPolicyFromJsonString(String jsonString) {
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON string cannot be null");
        }
        Policy policy = new Policy();
        LinkedList<Statement> statements = new LinkedList<Statement>();
        try {
            JsonNode statementsNode;
            JsonNode policyNode = Jackson.jsonNodeOf(jsonString);
            JsonNode idNode = policyNode.get("Id");
            if (this.isNotNull(idNode)) {
                policy.setId(idNode.asText());
            }
            if (this.isNotNull(statementsNode = policyNode.get("Statement"))) {
                if (statementsNode.isObject()) {
                    statements.add(this.statementOf(statementsNode));
                } else if (statementsNode.isArray()) {
                    for (JsonNode statementNode : statementsNode) {
                        statements.add(this.statementOf(statementNode));
                    }
                }
            }
        }
        catch (Exception e) {
            String message = "Unable to generate policy object fron JSON string " + e.getMessage();
            throw new IllegalArgumentException(message, e);
        }
        policy.setStatements(statements);
        return policy;
    }

    private Statement statementOf(JsonNode jStatement) {
        JsonNode principalNodes;
        JsonNode conditionNodes;
        JsonNode notResourceNodes;
        JsonNode actionNodes;
        JsonNode effectNode = jStatement.get("Effect");
        Statement.Effect effect = this.isNotNull(effectNode) ? Statement.Effect.valueOf(effectNode.asText()) : Statement.Effect.Deny;
        Statement statement = new Statement(effect);
        JsonNode id = jStatement.get("Sid");
        if (this.isNotNull(id)) {
            statement.setId(id.asText());
        }
        if (this.isNotNull(actionNodes = jStatement.get("Action"))) {
            statement.setActions(this.actionsOf(actionNodes));
        }
        LinkedList<Resource> resources = new LinkedList<Resource>();
        JsonNode resourceNodes = jStatement.get("Resource");
        if (this.isNotNull(resourceNodes)) {
            resources.addAll(this.resourcesOf(resourceNodes, false));
        }
        if (this.isNotNull(notResourceNodes = jStatement.get("NotResource"))) {
            resources.addAll(this.resourcesOf(notResourceNodes, true));
        }
        if (!resources.isEmpty()) {
            statement.setResources(resources);
        }
        if (this.isNotNull(conditionNodes = jStatement.get("Condition"))) {
            statement.setConditions(this.conditionsOf(conditionNodes));
        }
        if (this.isNotNull(principalNodes = jStatement.get("Principal"))) {
            statement.setPrincipals(this.principalOf(principalNodes));
        }
        return statement;
    }

    private List<Action> actionsOf(JsonNode actionNodes) {
        LinkedList<Action> actions = new LinkedList<Action>();
        if (actionNodes.isArray()) {
            for (JsonNode action : actionNodes) {
                actions.add(new NamedAction(action.asText()));
            }
        } else {
            actions.add(new NamedAction(actionNodes.asText()));
        }
        return actions;
    }

    private List<Resource> resourcesOf(JsonNode resourceNodes, boolean isNotType) {
        LinkedList<Resource> resources = new LinkedList<Resource>();
        if (resourceNodes.isArray()) {
            for (JsonNode resource : resourceNodes) {
                resources.add(new Resource(resource.asText()).withIsNotType(isNotType));
            }
        } else {
            resources.add(new Resource(resourceNodes.asText()).withIsNotType(isNotType));
        }
        return resources;
    }

    private List<Principal> principalOf(JsonNode principalNodes) {
        LinkedList<Principal> principals = new LinkedList<Principal>();
        if (principalNodes.asText().equals("*")) {
            principals.add(Principal.All);
            return principals;
        }
        Iterator<Map.Entry<String, JsonNode>> mapOfPrincipals = principalNodes.fields();
        while (mapOfPrincipals.hasNext()) {
            Map.Entry<String, JsonNode> principal = mapOfPrincipals.next();
            String schema = principal.getKey();
            JsonNode principalNode = principal.getValue();
            if (principalNode.isArray()) {
                Iterator<JsonNode> elements = principalNode.elements();
                while (elements.hasNext()) {
                    principals.add(this.createPrincipal(schema, elements.next()));
                }
                continue;
            }
            principals.add(this.createPrincipal(schema, principalNode));
        }
        return principals;
    }

    private Principal createPrincipal(String schema, JsonNode principalNode) {
        if (schema.equalsIgnoreCase(PRINCIPAL_SCHEMA_USER)) {
            return new Principal(PRINCIPAL_SCHEMA_USER, principalNode.asText(), this.options.isStripAwsPrincipalIdHyphensEnabled());
        }
        if (schema.equalsIgnoreCase(PRINCIPAL_SCHEMA_SERVICE)) {
            return new Principal(schema, principalNode.asText());
        }
        if (schema.equalsIgnoreCase(PRINCIPAL_SCHEMA_FEDERATED)) {
            if (Principal.WebIdentityProviders.fromString(principalNode.asText()) != null) {
                return new Principal(Principal.WebIdentityProviders.fromString(principalNode.asText()));
            }
            return new Principal(PRINCIPAL_SCHEMA_FEDERATED, principalNode.asText());
        }
        throw new SdkClientException("Schema " + schema + " is not a valid value for the principal.");
    }

    private List<Condition> conditionsOf(JsonNode conditionNodes) {
        LinkedList<Condition> conditionList = new LinkedList<Condition>();
        Iterator<Map.Entry<String, JsonNode>> mapOfConditions = conditionNodes.fields();
        while (mapOfConditions.hasNext()) {
            Map.Entry<String, JsonNode> condition = mapOfConditions.next();
            this.convertConditionRecord(conditionList, condition.getKey(), condition.getValue());
        }
        return conditionList;
    }

    private void convertConditionRecord(List<Condition> conditions, String conditionType, JsonNode conditionNode) {
        Iterator<Map.Entry<String, JsonNode>> mapOfFields = conditionNode.fields();
        while (mapOfFields.hasNext()) {
            LinkedList<String> values = new LinkedList<String>();
            Map.Entry<String, JsonNode> field = mapOfFields.next();
            JsonNode fieldValue = field.getValue();
            if (fieldValue.isArray()) {
                Iterator<JsonNode> elements = fieldValue.elements();
                while (elements.hasNext()) {
                    values.add(elements.next().asText());
                }
            } else {
                values.add(fieldValue.asText());
            }
            conditions.add(new Condition().withType(conditionType).withConditionKey(field.getKey()).withValues(values));
        }
    }

    private boolean isNotNull(Object object) {
        return null != object;
    }

    private static class NamedAction
    implements Action {
        private String actionName;

        public NamedAction(String actionName) {
            this.actionName = actionName;
        }

        @Override
        public String getActionName() {
            return this.actionName;
        }
    }
}

