/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.map.ser.std.EnumSerializer;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.util.EnumValues;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class EnumMapSerializer
extends ContainerSerializerBase<EnumMap<? extends Enum<?>, ?>>
implements ResolvableSerializer {
    protected final boolean _staticTyping;
    protected final EnumValues _keyEnums;
    protected final JavaType _valueType;
    protected final BeanProperty _property;
    protected JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;

    @Deprecated
    public EnumMapSerializer(JavaType valueType, boolean staticTyping, EnumValues keyEnums, TypeSerializer vts, BeanProperty property) {
        this(valueType, staticTyping, keyEnums, vts, property, null);
    }

    public EnumMapSerializer(JavaType valueType, boolean staticTyping, EnumValues keyEnums, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer) {
        super(EnumMap.class, false);
        this._staticTyping = staticTyping || valueType != null && valueType.isFinal();
        this._valueType = valueType;
        this._keyEnums = keyEnums;
        this._valueTypeSerializer = vts;
        this._property = property;
        this._valueSerializer = valueSerializer;
    }

    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new EnumMapSerializer(this._valueType, this._staticTyping, this._keyEnums, vts, this._property, this._valueSerializer);
    }

    @Override
    public void serialize(EnumMap<? extends Enum<?>, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        if (!value.isEmpty()) {
            this.serializeContents(value, jgen, provider);
        }
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(EnumMap<? extends Enum<?>, ?> value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForObject(value, jgen);
        if (!value.isEmpty()) {
            this.serializeContents(value, jgen, provider);
        }
        typeSer.writeTypeSuffixForObject(value, jgen);
    }

    protected void serializeContents(EnumMap<? extends Enum<?>, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._valueSerializer != null) {
            this.serializeContentsUsing(value, jgen, provider, this._valueSerializer);
            return;
        }
        JsonSerializer<Object> prevSerializer = null;
        Class<?> prevClass = null;
        EnumValues keyEnums = this._keyEnums;
        for (Map.Entry<Enum<?>, ?> entry : value.entrySet()) {
            JsonSerializer<Object> currSerializer;
            Enum<?> key = entry.getKey();
            if (keyEnums == null) {
                SerializerBase ser = (SerializerBase)provider.findValueSerializer(key.getDeclaringClass(), this._property);
                keyEnums = ((EnumSerializer)ser).getEnumValues();
            }
            jgen.writeFieldName(keyEnums.serializedValueFor(key));
            Object valueElem = entry.getValue();
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
                continue;
            }
            Class<?> cc = valueElem.getClass();
            if (cc == prevClass) {
                currSerializer = prevSerializer;
            } else {
                prevSerializer = currSerializer = provider.findValueSerializer(cc, this._property);
                prevClass = cc;
            }
            try {
                currSerializer.serialize(valueElem, jgen, provider);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, (Throwable)e, value, entry.getKey().name());
            }
        }
    }

    protected void serializeContentsUsing(EnumMap<? extends Enum<?>, ?> value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> valueSer) throws IOException, JsonGenerationException {
        EnumValues keyEnums = this._keyEnums;
        for (Map.Entry<Enum<?>, ?> entry : value.entrySet()) {
            Enum<?> key = entry.getKey();
            if (keyEnums == null) {
                SerializerBase ser = (SerializerBase)provider.findValueSerializer(key.getDeclaringClass(), this._property);
                keyEnums = ((EnumSerializer)ser).getEnumValues();
            }
            jgen.writeFieldName(keyEnums.serializedValueFor(key));
            Object valueElem = entry.getValue();
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
                continue;
            }
            try {
                valueSer.serialize(valueElem, jgen, provider);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, (Throwable)e, value, entry.getKey().name());
            }
        }
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (this._staticTyping && this._valueSerializer == null) {
            this._valueSerializer = provider.findValueSerializer(this._valueType, this._property);
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        Type[] typeArgs;
        ObjectNode o = this.createSchemaNode("object", true);
        if (typeHint instanceof ParameterizedType && (typeArgs = ((ParameterizedType)typeHint).getActualTypeArguments()).length == 2) {
            JavaType enumType = provider.constructType(typeArgs[0]);
            JavaType valueType = provider.constructType(typeArgs[1]);
            ObjectNode propsNode = JsonNodeFactory.instance.objectNode();
            Class<?> enumClass = enumType.getRawClass();
            for (Enum enumValue : (Enum[])enumClass.getEnumConstants()) {
                JsonSerializer<Object> ser = provider.findValueSerializer(valueType.getRawClass(), this._property);
                JsonNode schemaNode = ser instanceof SchemaAware ? ((SchemaAware)((Object)ser)).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
                propsNode.put(provider.getConfig().getAnnotationIntrospector().findEnumValue(enumValue), schemaNode);
            }
            o.put("properties", propsNode);
        }
        return o;
    }
}

