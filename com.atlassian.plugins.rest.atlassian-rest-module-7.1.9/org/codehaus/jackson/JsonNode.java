/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class JsonNode
implements Iterable<JsonNode> {
    protected static final List<JsonNode> NO_NODES = Collections.emptyList();
    protected static final List<String> NO_STRINGS = Collections.emptyList();

    protected JsonNode() {
    }

    public boolean isValueNode() {
        return false;
    }

    public boolean isContainerNode() {
        return false;
    }

    public boolean isMissingNode() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isPojo() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isIntegralNumber() {
        return false;
    }

    public boolean isFloatingPointNumber() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isBigDecimal() {
        return false;
    }

    public boolean isBigInteger() {
        return false;
    }

    public boolean isTextual() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isBinary() {
        return false;
    }

    public abstract JsonToken asToken();

    public abstract JsonParser.NumberType getNumberType();

    public String getTextValue() {
        return null;
    }

    public byte[] getBinaryValue() throws IOException {
        return null;
    }

    public boolean getBooleanValue() {
        return false;
    }

    public Number getNumberValue() {
        return null;
    }

    public int getIntValue() {
        return 0;
    }

    public long getLongValue() {
        return 0L;
    }

    public double getDoubleValue() {
        return 0.0;
    }

    public BigDecimal getDecimalValue() {
        return BigDecimal.ZERO;
    }

    public BigInteger getBigIntegerValue() {
        return BigInteger.ZERO;
    }

    public JsonNode get(int index) {
        return null;
    }

    public JsonNode get(String fieldName) {
        return null;
    }

    public abstract String asText();

    public int asInt() {
        return this.asInt(0);
    }

    public int asInt(int defaultValue) {
        return defaultValue;
    }

    public long asLong() {
        return this.asLong(0L);
    }

    public long asLong(long defaultValue) {
        return defaultValue;
    }

    public double asDouble() {
        return this.asDouble(0.0);
    }

    public double asDouble(double defaultValue) {
        return defaultValue;
    }

    public boolean asBoolean() {
        return this.asBoolean(false);
    }

    public boolean asBoolean(boolean defaultValue) {
        return defaultValue;
    }

    @Deprecated
    public String getValueAsText() {
        return this.asText();
    }

    @Deprecated
    public int getValueAsInt() {
        return this.asInt(0);
    }

    @Deprecated
    public int getValueAsInt(int defaultValue) {
        return this.asInt(defaultValue);
    }

    @Deprecated
    public long getValueAsLong() {
        return this.asLong(0L);
    }

    @Deprecated
    public long getValueAsLong(long defaultValue) {
        return this.asLong(defaultValue);
    }

    @Deprecated
    public double getValueAsDouble() {
        return this.asDouble(0.0);
    }

    @Deprecated
    public double getValueAsDouble(double defaultValue) {
        return this.asDouble(defaultValue);
    }

    @Deprecated
    public boolean getValueAsBoolean() {
        return this.asBoolean(false);
    }

    @Deprecated
    public boolean getValueAsBoolean(boolean defaultValue) {
        return this.asBoolean(defaultValue);
    }

    public boolean has(String fieldName) {
        return this.get(fieldName) != null;
    }

    public boolean has(int index) {
        return this.get(index) != null;
    }

    public abstract JsonNode findValue(String var1);

    public final List<JsonNode> findValues(String fieldName) {
        List<JsonNode> result = this.findValues(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public final List<String> findValuesAsText(String fieldName) {
        List<String> result = this.findValuesAsText(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public abstract JsonNode findPath(String var1);

    public abstract JsonNode findParent(String var1);

    public final List<JsonNode> findParents(String fieldName) {
        List<JsonNode> result = this.findParents(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public abstract List<JsonNode> findValues(String var1, List<JsonNode> var2);

    public abstract List<String> findValuesAsText(String var1, List<String> var2);

    public abstract List<JsonNode> findParents(String var1, List<JsonNode> var2);

    public int size() {
        return 0;
    }

    @Override
    public final Iterator<JsonNode> iterator() {
        return this.getElements();
    }

    public Iterator<JsonNode> getElements() {
        return NO_NODES.iterator();
    }

    public Iterator<String> getFieldNames() {
        return NO_STRINGS.iterator();
    }

    public Iterator<Map.Entry<String, JsonNode>> getFields() {
        List coll = Collections.emptyList();
        return coll.iterator();
    }

    public abstract JsonNode path(String var1);

    @Deprecated
    public final JsonNode getPath(String fieldName) {
        return this.path(fieldName);
    }

    public abstract JsonNode path(int var1);

    @Deprecated
    public final JsonNode getPath(int index) {
        return this.path(index);
    }

    public JsonNode with(String propertyName) {
        throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + this.getClass().getName() + "), can not call with() on it");
    }

    public abstract JsonParser traverse();

    public abstract String toString();

    public abstract boolean equals(Object var1);
}

