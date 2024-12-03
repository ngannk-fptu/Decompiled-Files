/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.qom;

import java.util.Locale;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.Row;
import javax.jcr.query.qom.BindVariableValue;
import javax.jcr.query.qom.FullTextSearchScore;
import javax.jcr.query.qom.Length;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.LowerCase;
import javax.jcr.query.qom.NodeLocalName;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.Operand;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.StaticOperand;
import javax.jcr.query.qom.UpperCase;

public class OperandEvaluator {
    private final ValueFactory factory;
    private final Map<String, Value> variables;
    private final Locale locale;

    public OperandEvaluator(ValueFactory factory, Map<String, Value> variables, Locale locale) {
        this.factory = factory;
        this.variables = variables;
        this.locale = locale;
    }

    public OperandEvaluator(ValueFactory factory, Map<String, Value> variables) {
        this(factory, variables, Locale.ENGLISH);
    }

    public Value getValue(StaticOperand operand, int type) throws RepositoryException {
        Value value = this.getValue(operand);
        if (type == 0 || type == value.getType()) {
            return value;
        }
        if (type == 3) {
            return this.factory.createValue(value.getLong());
        }
        if (type == 4) {
            return this.factory.createValue(value.getDouble());
        }
        if (type == 5) {
            return this.factory.createValue(value.getDate());
        }
        return this.factory.createValue(value.getString(), type);
    }

    public Value getValue(StaticOperand operand) throws RepositoryException {
        if (operand instanceof Literal) {
            Literal literal = (Literal)operand;
            return literal.getLiteralValue();
        }
        if (operand instanceof BindVariableValue) {
            BindVariableValue bvv = (BindVariableValue)operand;
            Value value = this.variables.get(bvv.getBindVariableName());
            if (value != null) {
                return value;
            }
            throw new RepositoryException("Unknown bind variable: " + bvv.getBindVariableName());
        }
        throw new UnsupportedRepositoryOperationException("Unknown static operand type: " + operand);
    }

    public Value getValue(Operand operand, Row row) throws RepositoryException {
        Value[] values = this.getValues(operand, row);
        if (values.length == 1) {
            return values[0];
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; ++i) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(values[i].getString());
        }
        return this.factory.createValue(builder.toString());
    }

    public Value[] getValues(Operand operand, Row row) throws RepositoryException {
        if (operand instanceof StaticOperand) {
            StaticOperand so = (StaticOperand)operand;
            return new Value[]{this.getValue(so)};
        }
        if (operand instanceof FullTextSearchScore) {
            FullTextSearchScore ftss = (FullTextSearchScore)operand;
            double score = row.getScore(ftss.getSelectorName());
            return new Value[]{this.factory.createValue(score)};
        }
        if (operand instanceof NodeName) {
            NodeName nn = (NodeName)operand;
            String name = row.getNode(nn.getSelectorName()).getName();
            if ("".equals(name)) {
                return new Value[]{this.factory.createValue(name, 1)};
            }
            return new Value[]{this.factory.createValue(name, 7)};
        }
        if (operand instanceof Length) {
            return this.getLengthValues((Length)operand, row);
        }
        if (operand instanceof LowerCase) {
            return this.getLowerCaseValues((LowerCase)operand, row);
        }
        if (operand instanceof UpperCase) {
            return this.getUpperCaseValues((UpperCase)operand, row);
        }
        if (operand instanceof NodeLocalName) {
            return this.getNodeLocalNameValues((NodeLocalName)operand, row);
        }
        if (operand instanceof PropertyValue) {
            return this.getPropertyValues((PropertyValue)operand, row);
        }
        throw new UnsupportedRepositoryOperationException("Unknown operand type: " + operand);
    }

    public Value[] getValues(Operand operand, Node node) throws RepositoryException {
        if (operand instanceof StaticOperand) {
            StaticOperand so = (StaticOperand)operand;
            return new Value[]{this.getValue(so)};
        }
        if (operand instanceof FullTextSearchScore) {
            double defaultScore = 0.0;
            return new Value[]{this.factory.createValue(0.0)};
        }
        if (operand instanceof NodeName) {
            String name = node.getName();
            if ("".equals(name)) {
                return new Value[]{this.factory.createValue(name, 1)};
            }
            return new Value[]{this.factory.createValue(name, 7)};
        }
        if (operand instanceof Length) {
            return this.getLengthValues((Length)operand, node);
        }
        if (operand instanceof LowerCase) {
            return this.getLowerCaseValues((LowerCase)operand, node);
        }
        if (operand instanceof UpperCase) {
            return this.getUpperCaseValues((UpperCase)operand, node);
        }
        if (operand instanceof NodeLocalName) {
            return this.getNodeLocalNameValues((NodeLocalName)operand, node);
        }
        if (operand instanceof PropertyValue) {
            return this.getPropertyValues((PropertyValue)operand, node);
        }
        throw new UnsupportedRepositoryOperationException("Unknown operand type: " + operand);
    }

    private Value[] getLengthValues(Length operand, Row row) throws RepositoryException {
        Property property = this.getProperty(operand.getPropertyValue(), row);
        if (property == null) {
            return new Value[0];
        }
        if (property.isMultiple()) {
            long[] lengths = property.getLengths();
            Value[] values = new Value[lengths.length];
            for (int i = 0; i < lengths.length; ++i) {
                values[i] = this.factory.createValue(lengths[i]);
            }
            return values;
        }
        long length = property.getLength();
        return new Value[]{this.factory.createValue(length)};
    }

    private Value[] getLengthValues(Length operand, Node n) throws RepositoryException {
        Property property = this.getProperty(operand.getPropertyValue(), n);
        if (property == null) {
            return new Value[0];
        }
        if (property.isMultiple()) {
            long[] lengths = property.getLengths();
            Value[] values = new Value[lengths.length];
            for (int i = 0; i < lengths.length; ++i) {
                values[i] = this.factory.createValue(lengths[i]);
            }
            return values;
        }
        long length = property.getLength();
        return new Value[]{this.factory.createValue(length)};
    }

    private Value[] getLowerCaseValues(LowerCase operand, Row row) throws RepositoryException {
        Value[] values = this.getValues((Operand)operand.getOperand(), row);
        for (int i = 0; i < values.length; ++i) {
            String lower;
            String value = values[i].getString();
            if (value.equals(lower = value.toLowerCase(this.locale))) continue;
            values[i] = this.factory.createValue(lower);
        }
        return values;
    }

    private Value[] getLowerCaseValues(LowerCase operand, Node node) throws RepositoryException {
        Value[] values = this.getValues((Operand)operand.getOperand(), node);
        for (int i = 0; i < values.length; ++i) {
            String lower;
            String value = values[i].getString();
            if (value.equals(lower = value.toLowerCase(this.locale))) continue;
            values[i] = this.factory.createValue(lower);
        }
        return values;
    }

    private Value[] getUpperCaseValues(UpperCase operand, Row row) throws RepositoryException {
        Value[] values = this.getValues((Operand)operand.getOperand(), row);
        for (int i = 0; i < values.length; ++i) {
            String upper;
            String value = values[i].getString();
            if (value.equals(upper = value.toUpperCase(this.locale))) continue;
            values[i] = this.factory.createValue(upper);
        }
        return values;
    }

    private Value[] getUpperCaseValues(UpperCase operand, Node node) throws RepositoryException {
        Value[] values = this.getValues((Operand)operand.getOperand(), node);
        for (int i = 0; i < values.length; ++i) {
            String upper;
            String value = values[i].getString();
            if (value.equals(upper = value.toUpperCase(this.locale))) continue;
            values[i] = this.factory.createValue(upper);
        }
        return values;
    }

    private Value[] getNodeLocalNameValues(NodeLocalName operand, Row row) throws RepositoryException {
        return this.getNodeLocalNameValues(operand, row.getNode(operand.getSelectorName()));
    }

    private Value[] getNodeLocalNameValues(NodeLocalName operand, Node node) throws RepositoryException {
        String name = node.getName();
        if ("".equals(name)) {
            return new Value[]{this.factory.createValue("", 1)};
        }
        int colon = name.indexOf(58);
        if (colon != -1) {
            name = name.substring(colon + 1);
        }
        return new Value[]{this.factory.createValue(name, 7)};
    }

    private Value[] getPropertyValues(PropertyValue operand, Row row) throws RepositoryException {
        Property property = this.getProperty(operand, row);
        if (property == null) {
            return new Value[0];
        }
        if (property.isMultiple()) {
            return property.getValues();
        }
        return new Value[]{property.getValue()};
    }

    private Value[] getPropertyValues(PropertyValue operand, Node node) throws RepositoryException {
        Property property = this.getProperty(operand, node);
        if (property == null) {
            return new Value[0];
        }
        if (property.isMultiple()) {
            return property.getValues();
        }
        return new Value[]{property.getValue()};
    }

    private Property getProperty(PropertyValue operand, Row row) throws RepositoryException {
        return this.getProperty(operand, row.getNode(operand.getSelectorName()));
    }

    private Property getProperty(PropertyValue operand, Node node) throws RepositoryException {
        if (node == null) {
            return null;
        }
        try {
            return node.getProperty(operand.getPropertyName());
        }
        catch (PathNotFoundException e) {
            return null;
        }
    }
}

