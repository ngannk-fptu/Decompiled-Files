/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util.internal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;

public class JacksonUtils {
    private JacksonUtils() {
    }

    public static JsonNode toJsonNode(Object datum) {
        if (datum == null) {
            return null;
        }
        try {
            TokenBuffer generator = new TokenBuffer(new ObjectMapper(), false);
            JacksonUtils.toJson(datum, generator);
            return (JsonNode)new ObjectMapper().readTree(generator.asParser());
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    static void toJson(Object datum, JsonGenerator generator) throws IOException {
        if (datum == JsonProperties.NULL_VALUE) {
            generator.writeNull();
        } else if (datum instanceof Map) {
            generator.writeStartObject();
            for (Map.Entry entry : ((Map)datum).entrySet()) {
                generator.writeFieldName(entry.getKey().toString());
                JacksonUtils.toJson(entry.getValue(), generator);
            }
            generator.writeEndObject();
        } else if (datum instanceof Collection) {
            generator.writeStartArray();
            for (Object element : (Collection)datum) {
                JacksonUtils.toJson(element, generator);
            }
            generator.writeEndArray();
        } else if (datum instanceof byte[]) {
            generator.writeString(new String((byte[])datum, StandardCharsets.ISO_8859_1));
        } else if (datum instanceof CharSequence || datum instanceof Enum) {
            generator.writeString(datum.toString());
        } else if (datum instanceof Double) {
            generator.writeNumber((Double)datum);
        } else if (datum instanceof Float) {
            generator.writeNumber(((Float)datum).floatValue());
        } else if (datum instanceof Long) {
            generator.writeNumber((Long)datum);
        } else if (datum instanceof Integer) {
            generator.writeNumber((Integer)datum);
        } else if (datum instanceof Boolean) {
            generator.writeBoolean((Boolean)datum);
        } else if (datum instanceof BigInteger) {
            generator.writeNumber((BigInteger)datum);
        } else if (datum instanceof BigDecimal) {
            generator.writeNumber((BigDecimal)datum);
        } else {
            throw new AvroRuntimeException("Unknown datum class: " + datum.getClass());
        }
    }

    public static Object toObject(JsonNode jsonNode) {
        return JacksonUtils.toObject(jsonNode, null);
    }

    public static Object toObject(JsonNode jsonNode, Schema schema) {
        if (schema != null && schema.getType().equals((Object)Schema.Type.UNION)) {
            return JacksonUtils.toObject(jsonNode, schema.getTypes().get(0));
        }
        if (jsonNode == null) {
            return null;
        }
        if (jsonNode.isNull()) {
            return JsonProperties.NULL_VALUE;
        }
        if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        }
        if (jsonNode.isInt()) {
            if (schema == null || schema.getType().equals((Object)Schema.Type.INT)) {
                return jsonNode.asInt();
            }
            if (schema.getType().equals((Object)Schema.Type.LONG)) {
                return jsonNode.asLong();
            }
            if (schema.getType().equals((Object)Schema.Type.FLOAT)) {
                return Float.valueOf((float)jsonNode.asDouble());
            }
            if (schema.getType().equals((Object)Schema.Type.DOUBLE)) {
                return jsonNode.asDouble();
            }
        } else if (jsonNode.isLong()) {
            if (schema == null || schema.getType().equals((Object)Schema.Type.LONG)) {
                return jsonNode.asLong();
            }
            if (schema.getType().equals((Object)Schema.Type.INT)) {
                if (jsonNode.canConvertToInt()) {
                    return jsonNode.asInt();
                }
                return jsonNode.asLong();
            }
            if (schema.getType().equals((Object)Schema.Type.FLOAT)) {
                return Float.valueOf((float)jsonNode.asDouble());
            }
            if (schema.getType().equals((Object)Schema.Type.DOUBLE)) {
                return jsonNode.asDouble();
            }
        } else if (jsonNode.isDouble() || jsonNode.isFloat()) {
            if (schema == null || schema.getType().equals((Object)Schema.Type.DOUBLE)) {
                return jsonNode.asDouble();
            }
            if (schema.getType().equals((Object)Schema.Type.FLOAT)) {
                return Float.valueOf((float)jsonNode.asDouble());
            }
        } else if (jsonNode.isTextual()) {
            if (schema == null || schema.getType().equals((Object)Schema.Type.STRING) || schema.getType().equals((Object)Schema.Type.ENUM)) {
                return jsonNode.asText();
            }
            if (schema.getType().equals((Object)Schema.Type.BYTES) || schema.getType().equals((Object)Schema.Type.FIXED)) {
                return jsonNode.textValue().getBytes(StandardCharsets.ISO_8859_1);
            }
        } else {
            if (jsonNode.isArray()) {
                ArrayList<Object> l = new ArrayList<Object>();
                for (JsonNode node : jsonNode) {
                    l.add(JacksonUtils.toObject(node, schema == null ? null : schema.getElementType()));
                }
                return l;
            }
            if (jsonNode.isObject()) {
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                Iterator<String> it = jsonNode.fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    Schema s = schema != null && schema.getType().equals((Object)Schema.Type.MAP) ? schema.getValueType() : (schema != null && schema.getType().equals((Object)Schema.Type.RECORD) ? schema.getField(key).schema() : null);
                    Object value = JacksonUtils.toObject(jsonNode.get(key), s);
                    m.put(key, value);
                }
                return m;
            }
        }
        return null;
    }

    public static Map objectToMap(Object datum) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.convertValue(datum, Map.class);
    }
}

