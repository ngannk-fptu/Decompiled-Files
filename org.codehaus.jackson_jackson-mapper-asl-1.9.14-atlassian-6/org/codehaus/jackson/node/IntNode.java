/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonParser$NumberType
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.io.NumberOutput
 */
package org.codehaus.jackson.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.io.NumberOutput;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.node.NumericNode;

public final class IntNode
extends NumericNode {
    static final int MIN_CANONICAL = -1;
    static final int MAX_CANONICAL = 10;
    private static final IntNode[] CANONICALS;
    final int _value;

    public IntNode(int v) {
        this._value = v;
    }

    public static IntNode valueOf(int i) {
        if (i > 10 || i < -1) {
            return new IntNode(i);
        }
        return CANONICALS[i - -1];
    }

    public JsonToken asToken() {
        return JsonToken.VALUE_NUMBER_INT;
    }

    public JsonParser.NumberType getNumberType() {
        return JsonParser.NumberType.INT;
    }

    public boolean isIntegralNumber() {
        return true;
    }

    public boolean isInt() {
        return true;
    }

    public Number getNumberValue() {
        return this._value;
    }

    public int getIntValue() {
        return this._value;
    }

    public long getLongValue() {
        return this._value;
    }

    public double getDoubleValue() {
        return this._value;
    }

    public BigDecimal getDecimalValue() {
        return BigDecimal.valueOf(this._value);
    }

    public BigInteger getBigIntegerValue() {
        return BigInteger.valueOf(this._value);
    }

    public String asText() {
        return NumberOutput.toString((int)this._value);
    }

    public boolean asBoolean(boolean defaultValue) {
        return this._value != 0;
    }

    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNumber(this._value);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return ((IntNode)o)._value == this._value;
    }

    public int hashCode() {
        return this._value;
    }

    static {
        int count = 12;
        CANONICALS = new IntNode[count];
        for (int i = 0; i < count; ++i) {
            IntNode.CANONICALS[i] = new IntNode(-1 + i);
        }
    }
}

