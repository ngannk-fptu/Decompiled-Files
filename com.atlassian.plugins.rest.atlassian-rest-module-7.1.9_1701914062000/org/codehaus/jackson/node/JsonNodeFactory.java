/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BigIntegerNode;
import org.codehaus.jackson.node.BinaryNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.DecimalNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.node.LongNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.NumericNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.POJONode;
import org.codehaus.jackson.node.TextNode;
import org.codehaus.jackson.node.ValueNode;

public class JsonNodeFactory {
    public static final JsonNodeFactory instance = new JsonNodeFactory();

    protected JsonNodeFactory() {
    }

    public BooleanNode booleanNode(boolean v) {
        return v ? BooleanNode.getTrue() : BooleanNode.getFalse();
    }

    public NullNode nullNode() {
        return NullNode.getInstance();
    }

    public NumericNode numberNode(byte v) {
        return IntNode.valueOf(v);
    }

    public ValueNode numberNode(Byte value) {
        return value == null ? this.nullNode() : IntNode.valueOf(value.intValue());
    }

    public NumericNode numberNode(short v) {
        return IntNode.valueOf(v);
    }

    public ValueNode numberNode(Short value) {
        return value == null ? this.nullNode() : IntNode.valueOf(value.shortValue());
    }

    public NumericNode numberNode(int v) {
        return IntNode.valueOf(v);
    }

    public ValueNode numberNode(Integer value) {
        return value == null ? this.nullNode() : IntNode.valueOf(value);
    }

    public NumericNode numberNode(long v) {
        return LongNode.valueOf(v);
    }

    public ValueNode numberNode(Long value) {
        return value == null ? this.nullNode() : LongNode.valueOf(value);
    }

    public NumericNode numberNode(BigInteger v) {
        return BigIntegerNode.valueOf(v);
    }

    public NumericNode numberNode(float v) {
        return DoubleNode.valueOf(v);
    }

    public ValueNode numberNode(Float value) {
        return value == null ? this.nullNode() : DoubleNode.valueOf(value.doubleValue());
    }

    public NumericNode numberNode(double v) {
        return DoubleNode.valueOf(v);
    }

    public ValueNode numberNode(Double value) {
        return value == null ? this.nullNode() : DoubleNode.valueOf(value);
    }

    public NumericNode numberNode(BigDecimal v) {
        return DecimalNode.valueOf(v);
    }

    public TextNode textNode(String text) {
        return TextNode.valueOf(text);
    }

    public BinaryNode binaryNode(byte[] data) {
        return BinaryNode.valueOf(data);
    }

    public BinaryNode binaryNode(byte[] data, int offset, int length) {
        return BinaryNode.valueOf(data, offset, length);
    }

    public ArrayNode arrayNode() {
        return new ArrayNode(this);
    }

    public ObjectNode objectNode() {
        return new ObjectNode(this);
    }

    public POJONode POJONode(Object pojo) {
        return new POJONode(pojo);
    }
}

