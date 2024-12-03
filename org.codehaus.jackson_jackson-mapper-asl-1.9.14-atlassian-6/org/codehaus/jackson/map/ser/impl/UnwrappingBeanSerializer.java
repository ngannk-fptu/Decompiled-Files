/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 */
package org.codehaus.jackson.map.ser.impl;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.BeanSerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnwrappingBeanSerializer
extends BeanSerializerBase {
    public UnwrappingBeanSerializer(BeanSerializerBase src) {
        super(src);
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer() {
        return this;
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return true;
    }

    @Override
    public final void serialize(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        } else {
            this.serializeFields(bean, jgen, provider);
        }
    }

    public String toString() {
        return "UnwrappingBeanSerializer for " + this.handledType().getName();
    }
}

