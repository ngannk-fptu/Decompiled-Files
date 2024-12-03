/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 *  org.codehaus.jackson.schema.JsonSchema
 *  org.codehaus.jackson.schema.SchemaAware
 */
package org.codehaus.jackson.xc;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XmlAdapterJsonSerializer
extends SerializerBase<Object>
implements SchemaAware {
    private final XmlAdapter<Object, Object> xmlAdapter;

    public XmlAdapterJsonSerializer(XmlAdapter<Object, Object> xmlAdapter) {
        super(Object.class);
        this.xmlAdapter = xmlAdapter;
    }

    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Object adapted;
        try {
            adapted = this.xmlAdapter.marshal(value);
        }
        catch (Exception e) {
            throw new JsonMappingException("Unable to marshal: " + e.getMessage(), (Throwable)e);
        }
        if (adapted == null) {
            provider.getNullValueSerializer().serialize(null, jgen, provider);
        } else {
            Class<?> c = adapted.getClass();
            provider.findTypedValueSerializer(c, true, null).serialize(adapted, jgen, provider);
        }
    }

    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        JsonSerializer ser = provider.findValueSerializer(this.findValueClass(), null);
        JsonNode schemaNode = ser instanceof SchemaAware ? ((SchemaAware)ser).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
        return schemaNode;
    }

    private Class<?> findValueClass() {
        Type superClass = this.xmlAdapter.getClass().getGenericSuperclass();
        while (superClass instanceof ParameterizedType && XmlAdapter.class != ((ParameterizedType)superClass).getRawType()) {
            superClass = ((Class)((ParameterizedType)superClass).getRawType()).getGenericSuperclass();
        }
        return (Class)((ParameterizedType)superClass).getActualTypeArguments()[0];
    }
}

