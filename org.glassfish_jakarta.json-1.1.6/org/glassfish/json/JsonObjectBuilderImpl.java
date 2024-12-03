/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

class JsonObjectBuilderImpl
implements JsonObjectBuilder {
    private Map<String, JsonValue> valueMap;
    private final BufferPool bufferPool;

    JsonObjectBuilderImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    JsonObjectBuilderImpl(JsonObject object, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        this.valueMap = new LinkedHashMap<String, JsonValue>();
        this.valueMap.putAll(object);
    }

    JsonObjectBuilderImpl(Map<String, Object> map, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        this.valueMap = new LinkedHashMap<String, JsonValue>();
        this.populate(map);
    }

    @Override
    public JsonObjectBuilder add(String name, JsonValue value) {
        this.validateName(name);
        this.validateValue(value);
        this.putValueMap(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, String value) {
        this.validateName(name);
        this.validateValue(value);
        this.putValueMap(name, new JsonStringImpl(value));
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigInteger value) {
        this.validateName(name);
        this.validateValue(value);
        this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigDecimal value) {
        this.validateName(name);
        this.validateValue(value);
        this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, int value) {
        this.validateName(name);
        this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, long value) {
        this.validateName(name);
        this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, double value) {
        this.validateName(name);
        this.putValueMap(name, JsonNumberImpl.getJsonNumber(value));
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, boolean value) {
        this.validateName(name);
        this.putValueMap(name, value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    @Override
    public JsonObjectBuilder addNull(String name) {
        this.validateName(name);
        this.putValueMap(name, JsonValue.NULL);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        this.validateName(name);
        if (builder == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_OBJECT_BUILDER_NULL());
        }
        this.putValueMap(name, builder.build());
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        this.validateName(name);
        if (builder == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_ARRAY_BUILDER_NULL());
        }
        this.putValueMap(name, builder.build());
        return this;
    }

    @Override
    public JsonObjectBuilder addAll(JsonObjectBuilder builder) {
        if (builder == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_OBJECT_BUILDER_NULL());
        }
        if (this.valueMap == null) {
            this.valueMap = new LinkedHashMap<String, JsonValue>();
        }
        this.valueMap.putAll(builder.build());
        return this;
    }

    @Override
    public JsonObjectBuilder remove(String name) {
        this.validateName(name);
        this.valueMap.remove(name);
        return this;
    }

    @Override
    public JsonObject build() {
        Map<String, JsonValue> snapshot = this.valueMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.valueMap);
        this.valueMap = null;
        return new JsonObjectImpl(snapshot, this.bufferPool);
    }

    private void populate(Map<String, Object> map) {
        Set<String> fields = map.keySet();
        for (String field : fields) {
            Object value = map.get(field);
            if (value != null && value instanceof Optional) {
                ((Optional)value).ifPresent(v -> this.valueMap.put(field, MapUtil.handle(v, this.bufferPool)));
                continue;
            }
            this.valueMap.put(field, MapUtil.handle(value, this.bufferPool));
        }
    }

    private void putValueMap(String name, JsonValue value) {
        if (this.valueMap == null) {
            this.valueMap = new LinkedHashMap<String, JsonValue>();
        }
        this.valueMap.put(name, value);
    }

    private void validateName(String name) {
        if (name == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_NAME_NULL());
        }
    }

    private void validateValue(Object value) {
        if (value == null) {
            throw new NullPointerException(JsonMessages.OBJBUILDER_VALUE_NULL());
        }
    }

    private static final class JsonObjectImpl
    extends AbstractMap<String, JsonValue>
    implements JsonObject {
        private final Map<String, JsonValue> valueMap;
        private final BufferPool bufferPool;

        JsonObjectImpl(Map<String, JsonValue> valueMap, BufferPool bufferPool) {
            this.valueMap = valueMap;
            this.bufferPool = bufferPool;
        }

        @Override
        public JsonArray getJsonArray(String name) {
            return (JsonArray)this.get(name);
        }

        @Override
        public JsonObject getJsonObject(String name) {
            return (JsonObject)this.get(name);
        }

        @Override
        public JsonNumber getJsonNumber(String name) {
            return (JsonNumber)this.get(name);
        }

        @Override
        public JsonString getJsonString(String name) {
            return (JsonString)this.get(name);
        }

        @Override
        public String getString(String name) {
            return this.getJsonString(name).getString();
        }

        @Override
        public String getString(String name, String defaultValue) {
            try {
                return this.getString(name);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public int getInt(String name) {
            return this.getJsonNumber(name).intValue();
        }

        @Override
        public int getInt(String name, int defaultValue) {
            try {
                return this.getInt(name);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public boolean getBoolean(String name) {
            JsonValue value = this.get(name);
            if (value == null) {
                throw new NullPointerException();
            }
            if (value == JsonValue.TRUE) {
                return true;
            }
            if (value == JsonValue.FALSE) {
                return false;
            }
            throw new ClassCastException();
        }

        @Override
        public boolean getBoolean(String name, boolean defaultValue) {
            try {
                return this.getBoolean(name);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public boolean isNull(String name) {
            return this.get(name).equals(JsonValue.NULL);
        }

        @Override
        public JsonValue.ValueType getValueType() {
            return JsonValue.ValueType.OBJECT;
        }

        @Override
        public Set<Map.Entry<String, JsonValue>> entrySet() {
            return this.valueMap.entrySet();
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
        public JsonObject asJsonObject() {
            return this;
        }

        @Override
        public int size() {
            return this.valueMap.size();
        }

        @Override
        public JsonValue get(Object key) {
            return this.valueMap.get(key);
        }

        @Override
        public boolean containsKey(Object key) {
            return this.valueMap.containsKey(key);
        }
    }
}

