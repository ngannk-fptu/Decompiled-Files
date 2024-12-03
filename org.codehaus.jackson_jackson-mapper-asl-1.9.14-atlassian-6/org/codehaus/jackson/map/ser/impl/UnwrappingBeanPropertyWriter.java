/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser.impl;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnwrappingBeanPropertyWriter
extends BeanPropertyWriter {
    public UnwrappingBeanPropertyWriter(BeanPropertyWriter base) {
        super(base);
    }

    public UnwrappingBeanPropertyWriter(BeanPropertyWriter base, JsonSerializer<Object> ser) {
        super(base, ser);
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        if (this.getClass() != UnwrappingBeanPropertyWriter.class) {
            throw new IllegalStateException("UnwrappingBeanPropertyWriter sub-class does not override 'withSerializer()'; needs to!");
        }
        if (!ser.isUnwrappingSerializer()) {
            ser = ser.unwrappingSerializer();
        }
        return new UnwrappingBeanPropertyWriter(this, ser);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
        Class<?> cls;
        PropertySerializerMap map;
        Object value = this.get(bean);
        if (value == null) {
            return;
        }
        if (value == bean) {
            this._reportSelfReference(bean);
        }
        if (this._suppressableValue != null && this._suppressableValue.equals(value)) {
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null && (ser = (map = this._dynamicSerializers).serializerFor(cls = value.getClass())) == null) {
            ser = this._findAndAddDynamic(map, cls, prov);
        }
        if (!ser.isUnwrappingSerializer()) {
            jgen.writeFieldName(this._name);
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        } else {
            ser.serializeWithType(value, jgen, prov, this._typeSerializer);
        }
    }

    @Override
    protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer;
        if (this._nonTrivialBaseType != null) {
            JavaType subtype = provider.constructSpecializedType(this._nonTrivialBaseType, type);
            serializer = provider.findValueSerializer(subtype, (BeanProperty)this);
        } else {
            serializer = provider.findValueSerializer(type, (BeanProperty)this);
        }
        if (!serializer.isUnwrappingSerializer()) {
            serializer = serializer.unwrappingSerializer();
        }
        this._dynamicSerializers = this._dynamicSerializers.newWith(type, serializer);
        return serializer;
    }
}

