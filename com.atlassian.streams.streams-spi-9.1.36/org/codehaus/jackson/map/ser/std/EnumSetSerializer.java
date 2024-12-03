/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.util.EnumSet;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.std.AsArraySerializerBase;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumSetSerializer
extends AsArraySerializerBase<EnumSet<? extends Enum<?>>> {
    public EnumSetSerializer(JavaType elemType, BeanProperty property) {
        super(EnumSet.class, elemType, true, null, property, null);
    }

    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
        return this;
    }

    @Override
    public void serializeContents(EnumSet<? extends Enum<?>> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        JsonSerializer<Object> enumSer = this._elementSerializer;
        for (Enum enum_ : value) {
            if (enumSer == null) {
                enumSer = provider.findValueSerializer(enum_.getDeclaringClass(), this._property);
            }
            enumSer.serialize(enum_, jgen, provider);
        }
    }
}

