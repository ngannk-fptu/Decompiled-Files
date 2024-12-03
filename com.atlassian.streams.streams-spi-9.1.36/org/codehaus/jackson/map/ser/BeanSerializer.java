/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.AnyGetterWriter;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.impl.UnwrappingBeanSerializer;
import org.codehaus.jackson.map.ser.std.BeanSerializerBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BeanSerializer
extends BeanSerializerBase {
    public BeanSerializer(JavaType type, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties, AnyGetterWriter anyGetterWriter, Object filterId) {
        super(type, properties, filteredProperties, anyGetterWriter, filterId);
    }

    public BeanSerializer(Class<?> rawType, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties, AnyGetterWriter anyGetterWriter, Object filterId) {
        super(rawType, properties, filteredProperties, anyGetterWriter, filterId);
    }

    protected BeanSerializer(BeanSerializer src) {
        super(src);
    }

    protected BeanSerializer(BeanSerializerBase src) {
        super(src);
    }

    public static BeanSerializer createDummy(Class<?> forType) {
        return new BeanSerializer(forType, NO_PROPS, null, null, null);
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer() {
        return new UnwrappingBeanSerializer(this);
    }

    @Override
    public final void serialize(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        } else {
            this.serializeFields(bean, jgen, provider);
        }
        jgen.writeEndObject();
    }

    public String toString() {
        return "BeanSerializer for " + this.handledType().getName();
    }
}

