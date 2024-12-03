/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.CamelCaseUtils;
import com.amazonaws.jmespath.Comparator;
import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathAndExpression;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathField;
import com.amazonaws.jmespath.JmesPathFilter;
import com.amazonaws.jmespath.JmesPathFlatten;
import com.amazonaws.jmespath.JmesPathFunction;
import com.amazonaws.jmespath.JmesPathIdentity;
import com.amazonaws.jmespath.JmesPathLiteral;
import com.amazonaws.jmespath.JmesPathMultiSelectList;
import com.amazonaws.jmespath.JmesPathNotExpression;
import com.amazonaws.jmespath.JmesPathProjection;
import com.amazonaws.jmespath.JmesPathSubExpression;
import com.amazonaws.jmespath.JmesPathValueProjection;
import com.amazonaws.jmespath.JmesPathVisitor;
import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JmesPathEvaluationVisitor
implements JmesPathVisitor<JsonNode, JsonNode> {
    @Override
    public JsonNode visit(JmesPathSubExpression subExpression, JsonNode input) throws InvalidTypeException {
        JsonNode prelimnaryResult = subExpression.getExpressions().get(0).accept(this, input);
        for (int i = 1; i < subExpression.getExpressions().size(); ++i) {
            prelimnaryResult = subExpression.getExpressions().get(i).accept(this, prelimnaryResult);
        }
        return prelimnaryResult;
    }

    @Override
    public JsonNode visit(JmesPathField fieldNode, JsonNode input) {
        if (input.isObject()) {
            return input.get(CamelCaseUtils.toCamelCase(fieldNode.getValue()));
        }
        return NullNode.getInstance();
    }

    @Override
    public JsonNode visit(JmesPathProjection jmesPathProjection, JsonNode input) throws InvalidTypeException {
        JsonNode lhsResult = jmesPathProjection.getLhsExpr().accept(this, input);
        if (lhsResult.isArray()) {
            Iterator<JsonNode> elements = lhsResult.elements();
            ArrayNode projectedArrayNode = ObjectMapperSingleton.getObjectMapper().createArrayNode();
            while (elements.hasNext()) {
                JsonNode projectedElement = jmesPathProjection.getProjectionExpr().accept(this, elements.next());
                if (projectedElement == null) continue;
                projectedArrayNode.add(projectedElement);
            }
            return projectedArrayNode;
        }
        return NullNode.getInstance();
    }

    @Override
    public JsonNode visit(JmesPathFlatten flatten, JsonNode input) throws InvalidTypeException {
        JsonNode flattenResult = flatten.getFlattenExpr().accept(this, input);
        if (flattenResult.isArray()) {
            Iterator<JsonNode> elements = flattenResult.elements();
            ArrayNode flattenedArray = ObjectMapperSingleton.getObjectMapper().createArrayNode();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                if (element == null) continue;
                if (element.isArray()) {
                    for (JsonNode innerElement : element) {
                        if (innerElement == null) continue;
                        flattenedArray.add(innerElement);
                    }
                    continue;
                }
                flattenedArray.add(element);
            }
            return flattenedArray;
        }
        return NullNode.getInstance();
    }

    @Override
    public JsonNode visit(JmesPathIdentity jmesPathIdentity, JsonNode input) {
        return input;
    }

    @Override
    public JsonNode visit(JmesPathValueProjection valueProjection, JsonNode input) throws InvalidTypeException {
        JsonNode projectedResult = valueProjection.getLhsExpr().accept(this, input);
        if (projectedResult.isObject()) {
            ArrayNode projectedArrayNode = ObjectMapperSingleton.getObjectMapper().createArrayNode();
            Iterator<JsonNode> elements = projectedResult.elements();
            while (elements.hasNext()) {
                JsonNode projectedElement = valueProjection.getRhsExpr().accept(this, elements.next());
                if (projectedElement == null) continue;
                projectedArrayNode.add(projectedElement);
            }
            return projectedArrayNode;
        }
        return NullNode.getInstance();
    }

    @Override
    public JsonNode visit(JmesPathFunction function, JsonNode input) throws InvalidTypeException {
        ArrayList<JsonNode> evaluatedArguments = new ArrayList<JsonNode>();
        List<JmesPathExpression> arguments = function.getExpressions();
        for (JmesPathExpression arg : arguments) {
            evaluatedArguments.add(arg.accept(this, input));
        }
        return function.evaluate(evaluatedArguments);
    }

    @Override
    public JsonNode visit(JmesPathLiteral literal, JsonNode input) {
        return literal.getValue();
    }

    @Override
    public JsonNode visit(JmesPathFilter filter, JsonNode input) throws InvalidTypeException {
        JsonNode filterExpression = filter.getLhsExpr().accept(this, input);
        if (filterExpression.isArray()) {
            Iterator<JsonNode> elements = filterExpression.elements();
            ArrayNode projectedArrayNode = ObjectMapperSingleton.getObjectMapper().createArrayNode();
            while (elements.hasNext()) {
                JsonNode projectedElement;
                JsonNode element = elements.next();
                if (!filter.getComparator().accept(this, element).equals(BooleanNode.TRUE) || (projectedElement = filter.getRhsExpr().accept(this, element)) == null) continue;
                projectedArrayNode.add(projectedElement);
            }
            return projectedArrayNode;
        }
        return NullNode.getInstance();
    }

    @Override
    public JsonNode visit(Comparator op, JsonNode input) {
        JsonNode rhsNode;
        JsonNode lhsNode = op.getLhsExpr().accept(this, input);
        if (op.matches(lhsNode, rhsNode = op.getRhsExpr().accept(this, input))) {
            return BooleanNode.TRUE;
        }
        return BooleanNode.FALSE;
    }

    @Override
    public JsonNode visit(JmesPathNotExpression notExpression, JsonNode input) throws InvalidTypeException {
        JsonNode resultExpr = notExpression.getExpr().accept(this, input);
        if (resultExpr != BooleanNode.TRUE) {
            return BooleanNode.TRUE;
        }
        return BooleanNode.FALSE;
    }

    @Override
    public JsonNode visit(JmesPathAndExpression andExpression, JsonNode input) throws InvalidTypeException {
        JsonNode lhsNode = andExpression.getLhsExpr().accept(this, input);
        JsonNode rhsNode = andExpression.getRhsExpr().accept(this, input);
        if (lhsNode == BooleanNode.TRUE) {
            return rhsNode;
        }
        return lhsNode;
    }

    @Override
    public JsonNode visit(JmesPathMultiSelectList multiSelectList, JsonNode input) throws InvalidTypeException {
        List<JmesPathExpression> expressionsList = multiSelectList.getExpressions();
        ArrayNode evaluatedExprList = ObjectMapperSingleton.getObjectMapper().createArrayNode();
        for (JmesPathExpression expression : expressionsList) {
            evaluatedExprList.add(expression.accept(this, input));
        }
        return evaluatedExprList;
    }
}

