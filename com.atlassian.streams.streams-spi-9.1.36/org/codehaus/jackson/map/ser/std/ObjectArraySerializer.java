/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.map.ser.std.StdArraySerializers;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class ObjectArraySerializer
extends StdArraySerializers.ArraySerializerBase<Object[]>
implements ResolvableSerializer {
    protected final boolean _staticTyping;
    protected final JavaType _elementType;
    protected JsonSerializer<Object> _elementSerializer;
    protected PropertySerializerMap _dynamicSerializers;

    @Deprecated
    public ObjectArraySerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property) {
        this(elemType, staticTyping, vts, property, null);
    }

    public ObjectArraySerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> elementSerializer) {
        super(Object[].class, vts, property);
        this._elementType = elemType;
        this._staticTyping = staticTyping;
        this._dynamicSerializers = PropertySerializerMap.emptyMap();
        this._elementSerializer = elementSerializer;
    }

    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new ObjectArraySerializer(this._elementType, this._staticTyping, vts, this._property, this._elementSerializer);
    }

    @Override
    public void serializeContents(Object[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        int i;
        int len = value.length;
        if (len == 0) {
            return;
        }
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, jgen, provider, this._elementSerializer);
            return;
        }
        if (this._valueTypeSerializer != null) {
            this.serializeTypedContents(value, jgen, provider);
            return;
        }
        Object elem = null;
        try {
            PropertySerializerMap serializers = this._dynamicSerializers;
            for (i = 0; i < len; ++i) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                Class<?> cc = elem.getClass();
                JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                if (serializer == null) {
                    serializer = this._elementType.hasGenericTypes() ? this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider) : this._findAndAddDynamic(serializers, cc, provider);
                }
                serializer.serialize(elem, jgen, provider);
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            Throwable t = e;
            while (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw JsonMappingException.wrapWithPath(t, elem, i);
        }
    }

    public void serializeContentsUsing(Object[] value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        int i;
        int len = value.length;
        TypeSerializer typeSer = this._valueTypeSerializer;
        Object elem = null;
        try {
            for (i = 0; i < len; ++i) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                if (typeSer == null) {
                    ser.serialize(elem, jgen, provider);
                    continue;
                }
                ser.serializeWithType(elem, jgen, provider, typeSer);
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            Throwable t = e;
            while (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw JsonMappingException.wrapWithPath(t, elem, i);
        }
    }

    public void serializeTypedContents(Object[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        int i;
        int len = value.length;
        TypeSerializer typeSer = this._valueTypeSerializer;
        Object elem = null;
        try {
            PropertySerializerMap serializers = this._dynamicSerializers;
            for (i = 0; i < len; ++i) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                Class<?> cc = elem.getClass();
                JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                if (serializer == null) {
                    serializer = this._findAndAddDynamic(serializers, cc, provider);
                }
                serializer.serializeWithType(elem, jgen, provider, typeSer);
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            Throwable t = e;
            while (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw JsonMappingException.wrapWithPath(t, elem, i);
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        JavaType javaType;
        ObjectNode o = this.createSchemaNode("array", true);
        if (typeHint != null && (javaType = provider.constructType(typeHint)).isArrayType()) {
            Class<?> componentType = ((ArrayType)javaType).getContentType().getRawClass();
            if (componentType == Object.class) {
                o.put("items", JsonSchema.getDefaultSchemaNode());
            } else {
                JsonSerializer<Object> ser = provider.findValueSerializer(componentType, this._property);
                JsonNode schemaNode = ser instanceof SchemaAware ? ((SchemaAware)((Object)ser)).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
                o.put("items", schemaNode);
            }
        }
        return o;
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (this._staticTyping && this._elementSerializer == null) {
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

