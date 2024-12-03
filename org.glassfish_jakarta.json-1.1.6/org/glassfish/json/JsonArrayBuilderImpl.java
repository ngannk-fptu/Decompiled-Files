/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.glassfish.json.JsonMessages;
import org.glassfish.json.JsonNumberImpl;
import org.glassfish.json.JsonStringImpl;
import org.glassfish.json.JsonWriterImpl;
import org.glassfish.json.MapUtil;
import org.glassfish.json.api.BufferPool;

class JsonArrayBuilderImpl
implements JsonArrayBuilder {
    private ArrayList<JsonValue> valueList;
    private final BufferPool bufferPool;

    JsonArrayBuilderImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    JsonArrayBuilderImpl(JsonArray array, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        this.valueList = new ArrayList();
        this.valueList.addAll(array);
    }

    JsonArrayBuilderImpl(Collection<?> collection, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        this.valueList = new ArrayList();
        this.populate(collection);
    }

    @Override
    public JsonArrayBuilder add(JsonValue value) {
        this.validateValue(value);
        this.addValueList(value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(String value) {
        this.validateValue(value);
        this.addValueList(new JsonStringImpl(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(BigDecimal value) {
        this.validateValue(value);
        this.addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(BigInteger value) {
        this.validateValue(value);
        this.addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int value) {
        this.addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(long value) {
        this.addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(double value) {
        this.addValueList(JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(boolean value) {
        this.addValueList(value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    @Override
    public JsonArrayBuilder addNull() {
        this.addValueList(JsonValue.NULL);
        return this;
    }

    @Override
    public JsonArrayBuilder add(JsonObjectBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
        }
        this.addValueList(builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder add(JsonArrayBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_ARRAY_BUILDER_NULL());
        }
        this.addValueList(builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder addAll(JsonArrayBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_ARRAY_BUILDER_NULL());
        }
        if (this.valueList == null) {
            this.valueList = new ArrayList();
        }
        this.valueList.addAll(builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, JsonValue value) {
        this.validateValue(value);
        this.addValueList(index, value);
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, String value) {
        this.validateValue(value);
        this.addValueList(index, new JsonStringImpl(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, BigDecimal value) {
        this.validateValue(value);
        this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, BigInteger value) {
        this.validateValue(value);
        this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, int value) {
        this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, long value) {
        this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, double value) {
        this.addValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, boolean value) {
        this.addValueList(index, value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    @Override
    public JsonArrayBuilder addNull(int index) {
        this.addValueList(index, JsonValue.NULL);
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, JsonObjectBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
        }
        this.addValueList(index, builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, JsonArrayBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
        }
        this.addValueList(index, builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, JsonValue value) {
        this.validateValue(value);
        this.setValueList(index, value);
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, String value) {
        this.validateValue(value);
        this.setValueList(index, new JsonStringImpl(value));
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, BigDecimal value) {
        this.validateValue(value);
        this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, BigInteger value) {
        this.validateValue(value);
        this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, int value) {
        this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, long value) {
        this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, double value) {
        this.setValueList(index, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, boolean value) {
        this.setValueList(index, value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    @Override
    public JsonArrayBuilder setNull(int index) {
        this.setValueList(index, JsonValue.NULL);
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, JsonObjectBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
        }
        this.setValueList(index, builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder set(int index, JsonArrayBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_OBJECT_BUILDER_NULL());
        }
        this.setValueList(index, builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder remove(int index) {
        if (this.valueList == null) {
            throw new IndexOutOfBoundsException(JsonMessages.ARRBUILDER_VALUELIST_NULL(index, 0));
        }
        this.valueList.remove(index);
        return this;
    }

    @Override
    public JsonArray build() {
        List<Object> snapshot = this.valueList == null ? Collections.emptyList() : Collections.unmodifiableList(this.valueList);
        this.valueList = null;
        return new JsonArrayImpl(snapshot, this.bufferPool);
    }

    private void populate(Collection<?> collection) {
        for (Object value : collection) {
            if (value != null && value instanceof Optional) {
                ((Optional)value).ifPresent(v -> this.valueList.add(MapUtil.handle(v, this.bufferPool)));
                continue;
            }
            this.valueList.add(MapUtil.handle(value, this.bufferPool));
        }
    }

    private void addValueList(JsonValue value) {
        if (this.valueList == null) {
            this.valueList = new ArrayList();
        }
        this.valueList.add(value);
    }

    private void addValueList(int index, JsonValue value) {
        if (this.valueList == null) {
            this.valueList = new ArrayList();
        }
        this.valueList.add(index, value);
    }

    private void setValueList(int index, JsonValue value) {
        if (this.valueList == null) {
            throw new IndexOutOfBoundsException(JsonMessages.ARRBUILDER_VALUELIST_NULL(index, 0));
        }
        this.valueList.set(index, value);
    }

    private void validateValue(Object value) {
        if (value == null) {
            throw new NullPointerException(JsonMessages.ARRBUILDER_VALUE_NULL());
        }
    }

    private static final class JsonArrayImpl
    extends AbstractList<JsonValue>
    implements JsonArray {
        private final List<JsonValue> valueList;
        private final BufferPool bufferPool;

        JsonArrayImpl(List<JsonValue> valueList, BufferPool bufferPool) {
            this.valueList = valueList;
            this.bufferPool = bufferPool;
        }

        @Override
        public int size() {
            return this.valueList.size();
        }

        @Override
        public JsonObject getJsonObject(int index) {
            return (JsonObject)this.valueList.get(index);
        }

        @Override
        public JsonArray getJsonArray(int index) {
            return (JsonArray)this.valueList.get(index);
        }

        @Override
        public JsonNumber getJsonNumber(int index) {
            return (JsonNumber)this.valueList.get(index);
        }

        @Override
        public JsonString getJsonString(int index) {
            return (JsonString)this.valueList.get(index);
        }

        @Override
        public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
            return this.valueList;
        }

        @Override
        public String getString(int index) {
            return this.getJsonString(index).getString();
        }

        @Override
        public String getString(int index, String defaultValue) {
            try {
                return this.getString(index);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public int getInt(int index) {
            return this.getJsonNumber(index).intValue();
        }

        @Override
        public int getInt(int index, int defaultValue) {
            try {
                return this.getInt(index);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public boolean getBoolean(int index) {
            JsonValue jsonValue = this.get(index);
            if (jsonValue == JsonValue.TRUE) {
                return true;
            }
            if (jsonValue == JsonValue.FALSE) {
                return false;
            }
            throw new ClassCastException();
        }

        @Override
        public boolean getBoolean(int index, boolean defaultValue) {
            try {
                return this.getBoolean(index);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public boolean isNull(int index) {
            return this.valueList.get(index).equals(JsonValue.NULL);
        }

        @Override
        public JsonValue.ValueType getValueType() {
            return JsonValue.ValueType.ARRAY;
        }

        @Override
        public JsonValue get(int index) {
            return this.valueList.get(index);
        }

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            try (JsonWriterImpl jw = new JsonWriterImpl(sw, this.bufferPool);){
                jw.write(this);
            }
            return sw.toString();
        }

        @Override
        public JsonArray asJsonArray() {
            return this;
        }
    }
}

