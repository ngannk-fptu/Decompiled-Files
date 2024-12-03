/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import org.codehaus.jackson.map.ser.std.AsArraySerializerBase;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CollectionSerializer
extends AsArraySerializerBase<Collection<?>> {
    public CollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer) {
        super(Collection.class, elemType, staticTyping, vts, property, valueSerializer);
    }

    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new CollectionSerializer(this._elementType, this._staticTyping, vts, this._property, this._elementSerializer);
    }

    @Override
    public void serializeContents(Collection<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, jgen, provider, this._elementSerializer);
            return;
        }
        Iterator<?> it = value.iterator();
        if (!it.hasNext()) {
            return;
        }
        PropertySerializerMap serializers = this._dynamicSerializers;
        TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        try {
            do {
                Object elem;
                if ((elem = it.next()) == null) {
                    provider.defaultSerializeNull(jgen);
                } else {
                    Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        serializer = this._elementType.hasGenericTypes() ? this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider) : this._findAndAddDynamic(serializers, cc, provider);
                        serializers = this._dynamicSerializers;
                    }
                    if (typeSer == null) {
                        serializer.serialize(elem, jgen, provider);
                    } else {
                        serializer.serializeWithType(elem, jgen, provider, typeSer);
                    }
                }
                ++i;
            } while (it.hasNext());
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, (Throwable)e, value, i);
        }
    }

    public void serializeContentsUsing(Collection<?> value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        Iterator<?> it = value.iterator();
        if (it.hasNext()) {
            TypeSerializer typeSer = this._valueTypeSerializer;
            int i = 0;
            do {
                Object elem = it.next();
                try {
                    if (elem == null) {
                        provider.defaultSerializeNull(jgen);
                    } else if (typeSer == null) {
                        ser.serialize(elem, jgen, provider);
                    } else {
                        ser.serializeWithType(elem, jgen, provider, typeSer);
                    }
                    ++i;
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, (Throwable)e, value, i);
                }
            } while (it.hasNext());
        }
    }
}

