/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser$NumberType
 */
package org.codehaus.jackson.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.node.ValueNode;

public abstract class NumericNode
extends ValueNode {
    protected NumericNode() {
    }

    public final boolean isNumber() {
        return true;
    }

    public abstract JsonParser.NumberType getNumberType();

    public abstract Number getNumberValue();

    public abstract int getIntValue();

    public abstract long getLongValue();

    public abstract double getDoubleValue();

    public abstract BigDecimal getDecimalValue();

    public abstract BigInteger getBigIntegerValue();

    public abstract String asText();

    public int asInt() {
        return this.getIntValue();
    }

    public int asInt(int defaultValue) {
        return this.getIntValue();
    }

    public long asLong() {
        return this.getLongValue();
    }

    public long asLong(long defaultValue) {
        return this.getLongValue();
    }

    public double asDouble() {
        return this.getDoubleValue();
    }

    public double asDouble(double defaultValue) {
        return this.getDoubleValue();
    }
}

