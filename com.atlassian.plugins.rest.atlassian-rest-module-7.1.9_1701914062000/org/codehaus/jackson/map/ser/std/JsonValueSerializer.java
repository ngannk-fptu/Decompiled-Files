/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class JsonValueSerializer
extends SerializerBase<Object>
implements ResolvableSerializer,
SchemaAware {
    protected final Method _accessorMethod;
    protected JsonSerializer<Object> _valueSerializer;
    protected final BeanProperty _property;
    protected boolean _forceTypeInformation;

    public JsonValueSerializer(Method valueMethod, JsonSerializer<Object> ser, BeanProperty property) {
        super(Object.class);
        this._accessorMethod = valueMethod;
        this._valueSerializer = ser;
        this._property = property;
    }

    @Override
    public void serialize(Object bean, JsonGenerator jgen, SerializerProvider prov) throws IOException, JsonGenerationException {
        try {
            Object value = this._accessorMethod.invoke(bean, new Object[0]);
            if (value == null) {
                prov.defaultSerializeNull(jgen);
                return;
            }
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                Class<?> c = value.getClass();
                ser = prov.findTypedValueSerializer(c, true, this._property);
            }
            ser.serialize(value, jgen, prov);
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
            throw JsonMappingException.wrapWithPath(t, bean, this._accessorMethod.getName() + "()");
        }
    }

    @Override
    public void serializeWithType(Object bean, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        Object value = null;
        try {
            value = this._accessorMethod.invoke(bean, new Object[0]);
            if (value == null) {
                provider.defaultSerializeNull(jgen);
                return;
            }
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser != null) {
                if (this._forceTypeInformation) {
                    typeSer.writeTypePrefixForScalar(bean, jgen);
                }
                ser.serializeWithType(value, jgen, provider, typeSer);
                if (this._forceTypeInformation) {
                    typeSer.writeTypeSuffixForScalar(bean, jgen);
                }
                return;
            }
            Class<?> c = value.getClass();
            ser = provider.findTypedValueSerializer(c, true, this._property);
            ser.serialize(value, jgen, provider);
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
            throw JsonMappingException.wrapWithPath(t, bean, this._accessorMethod.getName() + "()");
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return this._valueSerializer instanceof SchemaAware ? ((SchemaAware)((Object)this._valueSerializer)).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        if (this._valueSerializer == null && (provider.isEnabled(SerializationConfig.Feature.USE_STATIC_TYPING) || Modifier.isFinal(this._accessorMethod.getReturnType().getModifiers()))) {
            JavaType t = provider.constructType(this._accessorMethod.getGenericReturnType());
            this._valueSerializer = provider.findTypedValueSerializer(t, false, this._property);
            this._forceTypeInformation = this.isNaturalTypeWithStdHandling(t, this._valueSerializer);
        }
    }

    protected boolean isNaturalTypeWithStdHandling(JavaType type, JsonSerializer<?> ser) {
        Class<?> cls = type.getRawClass();
        if (type.isPrimitive() ? cls != Integer.TYPE && cls != Boolean.TYPE && cls != Double.TYPE : cls != String.class && cls != Integer.class && cls != Boolean.class && cls != Double.class) {
            return false;
        }
        return ser.getClass().getAnnotation(JacksonStdImpl.class) != null;
    }

    public String toString() {
        return "(@JsonValue serializer for method " + this._accessorMethod.getDeclaringClass() + "#" + this._accessorMethod.getName() + ")";
    }
}

