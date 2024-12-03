/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.SerializableString
 *  org.codehaus.jackson.io.SerializedString
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.SerializableString;
import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.std.ScalarSerializerBase;
import org.codehaus.jackson.map.util.EnumValues;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class EnumSerializer
extends ScalarSerializerBase<Enum<?>> {
    protected final EnumValues _values;

    public EnumSerializer(EnumValues v) {
        super(Enum.class, false);
        this._values = v;
    }

    public static EnumSerializer construct(Class<Enum<?>> enumClass, SerializationConfig config, BasicBeanDescription beanDesc) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        EnumValues v = config.isEnabled(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING) ? EnumValues.constructFromToString(enumClass, intr) : EnumValues.constructFromName(enumClass, intr);
        return new EnumSerializer(v);
    }

    @Override
    public final void serialize(Enum<?> en, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (provider.isEnabled(SerializationConfig.Feature.WRITE_ENUMS_USING_INDEX)) {
            jgen.writeNumber(en.ordinal());
            return;
        }
        jgen.writeString((SerializableString)this._values.serializedValueFor(en));
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        JavaType type;
        if (provider.isEnabled(SerializationConfig.Feature.WRITE_ENUMS_USING_INDEX)) {
            return this.createSchemaNode("integer", true);
        }
        ObjectNode objectNode = this.createSchemaNode("string", true);
        if (typeHint != null && (type = provider.constructType(typeHint)).isEnumType()) {
            ArrayNode enumNode = objectNode.putArray("enum");
            for (SerializedString value : this._values.values()) {
                enumNode.add(value.getValue());
            }
        }
        return objectNode;
    }

    public EnumValues getEnumValues() {
        return this._values;
    }
}

