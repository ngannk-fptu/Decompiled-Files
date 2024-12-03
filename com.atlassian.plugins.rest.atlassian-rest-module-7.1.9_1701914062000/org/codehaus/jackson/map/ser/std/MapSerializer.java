/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class MapSerializer
extends ContainerSerializerBase<Map<?, ?>>
implements ResolvableSerializer {
    protected static final JavaType UNSPECIFIED_TYPE = TypeFactory.unknownType();
    protected final BeanProperty _property;
    protected final HashSet<String> _ignoredEntries;
    protected final boolean _valueTypeIsStatic;
    protected final JavaType _keyType;
    protected final JavaType _valueType;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;
    protected PropertySerializerMap _dynamicValueSerializers;

    protected MapSerializer() {
        this(null, null, null, false, null, null, null, null);
    }

    protected MapSerializer(HashSet<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, BeanProperty property) {
        super(Map.class, false);
        this._property = property;
        this._ignoredEntries = ignoredEntries;
        this._keyType = keyType;
        this._valueType = valueType;
        this._valueTypeIsStatic = valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = keySerializer;
        this._valueSerializer = valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyMap();
    }

    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
        MapSerializer ms = new MapSerializer(this._ignoredEntries, this._keyType, this._valueType, this._valueTypeIsStatic, vts, this._keySerializer, this._valueSerializer, this._property);
        if (this._valueSerializer != null) {
            ms._valueSerializer = this._valueSerializer;
        }
        return ms;
    }

    @Deprecated
    public static MapSerializer construct(String[] ignoredList, JavaType mapType, boolean staticValueType, TypeSerializer vts, BeanProperty property) {
        return MapSerializer.construct(ignoredList, mapType, staticValueType, vts, property, null, null);
    }

    public static MapSerializer construct(String[] ignoredList, JavaType mapType, boolean staticValueType, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer) {
        JavaType keyType;
        JavaType valueType;
        HashSet<String> ignoredEntries = MapSerializer.toSet(ignoredList);
        if (mapType == null) {
            keyType = valueType = UNSPECIFIED_TYPE;
        } else {
            keyType = mapType.getKeyType();
            valueType = mapType.getContentType();
        }
        if (!staticValueType) {
            staticValueType = valueType != null && valueType.isFinal();
        }
        return new MapSerializer(ignoredEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer, property);
    }

    private static HashSet<String> toSet(String[] ignoredEntries) {
        if (ignoredEntries == null || ignoredEntries.length == 0) {
            return null;
        }
        HashSet<String> result = new HashSet<String>(ignoredEntries.length);
        for (String prop : ignoredEntries) {
            result.add(prop);
        }
        return result;
    }

    @Override
    public void serialize(Map<?, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        if (!value.isEmpty()) {
            if (this._valueSerializer != null) {
                this.serializeFieldsUsing(value, jgen, provider, this._valueSerializer);
            } else {
                this.serializeFields(value, jgen, provider);
            }
        }
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(Map<?, ?> value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForObject(value, jgen);
        if (!value.isEmpty()) {
            if (this._valueSerializer != null) {
                this.serializeFieldsUsing(value, jgen, provider, this._valueSerializer);
            } else {
                this.serializeFields(value, jgen, provider);
            }
        }
        typeSer.writeTypeSuffixForObject(value, jgen);
    }

    public void serializeFields(Map<?, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._valueTypeSerializer != null) {
            this.serializeTypedFields(value, jgen, provider);
            return;
        }
        JsonSerializer<Object> keySerializer = this._keySerializer;
        HashSet<String> ignored = this._ignoredEntries;
        boolean skipNulls = !provider.isEnabled(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
        PropertySerializerMap serializers = this._dynamicValueSerializers;
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object valueElem = entry.getValue();
            Object keyElem = entry.getKey();
            if (keyElem == null) {
                provider.getNullKeySerializer().serialize(null, jgen, provider);
            } else {
                if (skipNulls && valueElem == null || ignored != null && ignored.contains(keyElem)) continue;
                keySerializer.serialize(keyElem, jgen, provider);
            }
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
                continue;
            }
            Class<?> cc = valueElem.getClass();
            JsonSerializer<Object> serializer = serializers.serializerFor(cc);
            if (serializer == null) {
                serializer = this._valueType.hasGenericTypes() ? this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._valueType, cc), provider) : this._findAndAddDynamic(serializers, cc, provider);
                serializers = this._dynamicValueSerializers;
            }
            try {
                serializer.serialize(valueElem, jgen, provider);
            }
            catch (Exception e) {
                String keyDesc = "" + keyElem;
                this.wrapAndThrow(provider, (Throwable)e, value, keyDesc);
            }
        }
    }

    protected void serializeFieldsUsing(Map<?, ?> value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        JsonSerializer<Object> keySerializer = this._keySerializer;
        HashSet<String> ignored = this._ignoredEntries;
        TypeSerializer typeSer = this._valueTypeSerializer;
        boolean skipNulls = !provider.isEnabled(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object valueElem = entry.getValue();
            Object keyElem = entry.getKey();
            if (keyElem == null) {
                provider.getNullKeySerializer().serialize(null, jgen, provider);
            } else {
                if (skipNulls && valueElem == null || ignored != null && ignored.contains(keyElem)) continue;
                keySerializer.serialize(keyElem, jgen, provider);
            }
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
                continue;
            }
            try {
                if (typeSer == null) {
                    ser.serialize(valueElem, jgen, provider);
                    continue;
                }
                ser.serializeWithType(valueElem, jgen, provider, typeSer);
            }
            catch (Exception e) {
                String keyDesc = "" + keyElem;
                this.wrapAndThrow(provider, (Throwable)e, value, keyDesc);
            }
        }
    }

    protected void serializeTypedFields(Map<?, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        JsonSerializer<Object> keySerializer = this._keySerializer;
        JsonSerializer<Object> prevValueSerializer = null;
        Class<?> prevValueClass = null;
        HashSet<String> ignored = this._ignoredEntries;
        boolean skipNulls = !provider.isEnabled(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            JsonSerializer<Object> currSerializer;
            Object valueElem = entry.getValue();
            Object keyElem = entry.getKey();
            if (keyElem == null) {
                provider.getNullKeySerializer().serialize(null, jgen, provider);
            } else {
                if (skipNulls && valueElem == null || ignored != null && ignored.contains(keyElem)) continue;
                keySerializer.serialize(keyElem, jgen, provider);
            }
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
                continue;
            }
            Class<?> cc = valueElem.getClass();
            if (cc == prevValueClass) {
                currSerializer = prevValueSerializer;
            } else {
                currSerializer = this._valueType.hasGenericTypes() ? provider.findValueSerializer(provider.constructSpecializedType(this._valueType, cc), this._property) : provider.findValueSerializer(cc, this._property);
                prevValueSerializer = currSerializer;
                prevValueClass = cc;
            }
            try {
                currSerializer.serializeWithType(valueElem, jgen, provider, this._valueTypeSerializer);
            }
            catch (Exception e) {
                String keyDesc = "" + keyElem;
                this.wrapAndThrow(provider, (Throwable)e, value, keyDesc);
            }
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        ObjectNode o = this.createSchemaNode("object", true);
        return o;
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (this._valueTypeIsStatic && this._valueSerializer == null) {
            this._valueSerializer = provider.findValueSerializer(this._valueType, this._property);
        }
        if (this._keySerializer == null) {
            this._keySerializer = provider.findKeySerializer(this._keyType, this._property);
        }
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
}

