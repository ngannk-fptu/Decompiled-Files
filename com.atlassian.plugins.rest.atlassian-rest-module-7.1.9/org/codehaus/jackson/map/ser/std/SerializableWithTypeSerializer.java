/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.JsonSerializableSchema;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class SerializableWithTypeSerializer
extends SerializerBase<JsonSerializableWithType> {
    public static final SerializableWithTypeSerializer instance = new SerializableWithTypeSerializer();

    protected SerializableWithTypeSerializer() {
        super(JsonSerializableWithType.class);
    }

    @Override
    public void serialize(JsonSerializableWithType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        value.serialize(jgen, provider);
    }

    @Override
    public final void serializeWithType(JsonSerializableWithType value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        value.serializeWithType(jgen, provider, typeSer);
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        Class<?> rawClass;
        ObjectNode objectNode = this.createObjectNode();
        String schemaType = "any";
        String objectProperties = null;
        String itemDefinition = null;
        if (typeHint != null && (rawClass = TypeFactory.rawClass(typeHint)).isAnnotationPresent(JsonSerializableSchema.class)) {
            JsonSerializableSchema schemaInfo = rawClass.getAnnotation(JsonSerializableSchema.class);
            schemaType = schemaInfo.schemaType();
            if (!"##irrelevant".equals(schemaInfo.schemaObjectPropertiesDefinition())) {
                objectProperties = schemaInfo.schemaObjectPropertiesDefinition();
            }
            if (!"##irrelevant".equals(schemaInfo.schemaItemDefinition())) {
                itemDefinition = schemaInfo.schemaItemDefinition();
            }
        }
        objectNode.put("type", schemaType);
        if (objectProperties != null) {
            try {
                objectNode.put("properties", new ObjectMapper().readValue(objectProperties, JsonNode.class));
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        if (itemDefinition != null) {
            try {
                objectNode.put("items", new ObjectMapper().readValue(itemDefinition, JsonNode.class));
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return objectNode;
    }
}

