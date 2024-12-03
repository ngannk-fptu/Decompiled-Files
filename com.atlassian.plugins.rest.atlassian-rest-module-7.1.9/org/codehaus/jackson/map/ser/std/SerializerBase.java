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
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SerializerBase<T>
extends JsonSerializer<T>
implements SchemaAware {
    protected final Class<T> _handledType;

    protected SerializerBase(Class<T> t) {
        this._handledType = t;
    }

    protected SerializerBase(JavaType type) {
        this._handledType = type.getRawClass();
    }

    protected SerializerBase(Class<?> t, boolean dummy) {
        this._handledType = t;
    }

    @Override
    public final Class<T> handledType() {
        return this._handledType;
    }

    @Override
    public abstract void serialize(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException;

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return this.createSchemaNode("string");
    }

    protected ObjectNode createObjectNode() {
        return JsonNodeFactory.instance.objectNode();
    }

    protected ObjectNode createSchemaNode(String type) {
        ObjectNode schema = this.createObjectNode();
        schema.put("type", type);
        return schema;
    }

    protected ObjectNode createSchemaNode(String type, boolean isOptional) {
        ObjectNode schema = this.createSchemaNode(type);
        if (!isOptional) {
            schema.put("required", !isOptional);
        }
        return schema;
    }

    protected boolean isDefaultSerializer(JsonSerializer<?> serializer) {
        return serializer != null && serializer.getClass().getAnnotation(JacksonStdImpl.class) != null;
    }

    public void wrapAndThrow(SerializerProvider provider, Throwable t, Object bean, String fieldName) throws IOException {
        boolean wrap;
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        boolean bl = wrap = provider == null || provider.isEnabled(SerializationConfig.Feature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonMappingException)) {
                throw (IOException)t;
            }
        } else if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw JsonMappingException.wrapWithPath(t, bean, fieldName);
    }

    public void wrapAndThrow(SerializerProvider provider, Throwable t, Object bean, int index) throws IOException {
        boolean wrap;
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        boolean bl = wrap = provider == null || provider.isEnabled(SerializationConfig.Feature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonMappingException)) {
                throw (IOException)t;
            }
        } else if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw JsonMappingException.wrapWithPath(t, bean, index);
    }

    @Deprecated
    public void wrapAndThrow(Throwable t, Object bean, String fieldName) throws IOException {
        this.wrapAndThrow(null, t, bean, fieldName);
    }

    @Deprecated
    public void wrapAndThrow(Throwable t, Object bean, int index) throws IOException {
        this.wrapAndThrow(null, t, bean, index);
    }
}

