/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AsArraySerializerBase<T>
extends ContainerSerializerBase<T>
implements ResolvableSerializer {
    protected final boolean _staticTyping;
    protected final JavaType _elementType;
    protected final TypeSerializer _valueTypeSerializer;
    protected JsonSerializer<Object> _elementSerializer;
    protected final BeanProperty _property;
    protected PropertySerializerMap _dynamicSerializers;

    @Deprecated
    protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, BeanProperty property) {
        this(cls, et, staticTyping, vts, property, null);
    }

    protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> elementSerializer) {
        super(cls, false);
        this._elementType = et;
        this._staticTyping = staticTyping || et != null && et.isFinal();
        this._valueTypeSerializer = vts;
        this._property = property;
        this._elementSerializer = elementSerializer;
        this._dynamicSerializers = PropertySerializerMap.emptyMap();
    }

    @Override
    public final void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartArray();
        this.serializeContents(value, jgen, provider);
        jgen.writeEndArray();
    }

    @Override
    public final void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForArray(value, jgen);
        this.serializeContents(value, jgen, provider);
        typeSer.writeTypeSuffixForArray(value, jgen);
    }

    protected abstract void serializeContents(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException;

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        Type[] typeArgs;
        JavaType javaType;
        ObjectNode o = this.createSchemaNode("array", true);
        JavaType contentType = null;
        if (typeHint != null && (contentType = (javaType = provider.constructType(typeHint)).getContentType()) == null && typeHint instanceof ParameterizedType && (typeArgs = ((ParameterizedType)typeHint).getActualTypeArguments()).length == 1) {
            contentType = provider.constructType(typeArgs[0]);
        }
        if (contentType == null && this._elementType != null) {
            contentType = this._elementType;
        }
        if (contentType != null) {
            JsonSerializer<Object> ser;
            JsonNode schemaNode = null;
            if (contentType.getRawClass() != Object.class && (ser = provider.findValueSerializer(contentType, this._property)) instanceof SchemaAware) {
                schemaNode = ((SchemaAware)((Object)ser)).getSchema(provider, null);
            }
            if (schemaNode == null) {
                schemaNode = JsonSchema.getDefaultSchemaNode();
            }
            o.put("items", schemaNode);
        }
        return o;
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (this._staticTyping && this._elementType != null && this._elementSerializer == null) {
            this._elementSerializer = provider.findValueSerializer(this._elementType, this._property);
        }
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }
}

