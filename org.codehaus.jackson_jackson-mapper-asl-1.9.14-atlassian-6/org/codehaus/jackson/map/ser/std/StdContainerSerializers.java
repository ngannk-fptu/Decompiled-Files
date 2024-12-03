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
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;
import org.codehaus.jackson.map.ser.std.AsArraySerializerBase;
import org.codehaus.jackson.map.ser.std.CollectionSerializer;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.map.ser.std.EnumSetSerializer;
import org.codehaus.jackson.map.ser.std.IterableSerializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdContainerSerializers {
    protected StdContainerSerializers() {
    }

    public static ContainerSerializerBase<?> indexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer) {
        return new IndexedListSerializer(elemType, staticTyping, vts, property, valueSerializer);
    }

    public static ContainerSerializerBase<?> collectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer) {
        return new CollectionSerializer(elemType, staticTyping, vts, property, valueSerializer);
    }

    public static ContainerSerializerBase<?> iteratorSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property) {
        return new IteratorSerializer(elemType, staticTyping, vts, property);
    }

    public static ContainerSerializerBase<?> iterableSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property) {
        return new IterableSerializer(elemType, staticTyping, vts, property);
    }

    public static JsonSerializer<?> enumSetSerializer(JavaType enumType, BeanProperty property) {
        return new EnumSetSerializer(enumType, property);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static class IteratorSerializer
    extends AsArraySerializerBase<Iterator<?>> {
        public IteratorSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property) {
            super(Iterator.class, elemType, staticTyping, vts, property, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new IteratorSerializer(this._elementType, this._staticTyping, vts, this._property);
        }

        @Override
        public void serializeContents(Iterator<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (value.hasNext()) {
                TypeSerializer typeSer = this._valueTypeSerializer;
                JsonSerializer<Object> prevSerializer = null;
                Class<?> prevClass = null;
                do {
                    JsonSerializer<Object> currSerializer;
                    Object elem;
                    if ((elem = value.next()) == null) {
                        provider.defaultSerializeNull(jgen);
                        continue;
                    }
                    Class<?> cc = elem.getClass();
                    if (cc == prevClass) {
                        currSerializer = prevSerializer;
                    } else {
                        prevSerializer = currSerializer = provider.findValueSerializer(cc, this._property);
                        prevClass = cc;
                    }
                    if (typeSer == null) {
                        currSerializer.serialize(elem, jgen, provider);
                        continue;
                    }
                    currSerializer.serializeWithType(elem, jgen, provider, typeSer);
                } while (value.hasNext());
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static class IndexedListSerializer
    extends AsArraySerializerBase<List<?>> {
        public IndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer) {
            super(List.class, elemType, staticTyping, vts, property, valueSerializer);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new IndexedListSerializer(this._elementType, this._staticTyping, vts, this._property, this._elementSerializer);
        }

        @Override
        public void serializeContents(List<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int i;
            if (this._elementSerializer != null) {
                this.serializeContentsUsing(value, jgen, provider, this._elementSerializer);
                return;
            }
            if (this._valueTypeSerializer != null) {
                this.serializeTypedContents(value, jgen, provider);
                return;
            }
            int len = value.size();
            if (len == 0) {
                return;
            }
            try {
                PropertySerializerMap serializers = this._dynamicSerializers;
                for (i = 0; i < len; ++i) {
                    Object elem = value.get(i);
                    if (elem == null) {
                        provider.defaultSerializeNull(jgen);
                        continue;
                    }
                    Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        serializer = this._elementType.hasGenericTypes() ? this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider) : this._findAndAddDynamic(serializers, cc, provider);
                        serializers = this._dynamicSerializers;
                    }
                    serializer.serialize(elem, jgen, provider);
                }
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, (Throwable)e, value, i);
            }
        }

        public void serializeContentsUsing(List<?> value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
            int len = value.size();
            if (len == 0) {
                return;
            }
            TypeSerializer typeSer = this._valueTypeSerializer;
            for (int i = 0; i < len; ++i) {
                Object elem = value.get(i);
                try {
                    if (elem == null) {
                        provider.defaultSerializeNull(jgen);
                        continue;
                    }
                    if (typeSer == null) {
                        ser.serialize(elem, jgen, provider);
                        continue;
                    }
                    ser.serializeWithType(elem, jgen, provider, typeSer);
                    continue;
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, (Throwable)e, value, i);
                }
            }
        }

        public void serializeTypedContents(List<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int i;
            int len = value.size();
            if (len == 0) {
                return;
            }
            try {
                TypeSerializer typeSer = this._valueTypeSerializer;
                PropertySerializerMap serializers = this._dynamicSerializers;
                for (i = 0; i < len; ++i) {
                    Object elem = value.get(i);
                    if (elem == null) {
                        provider.defaultSerializeNull(jgen);
                        continue;
                    }
                    Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        serializer = this._elementType.hasGenericTypes() ? this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider) : this._findAndAddDynamic(serializers, cc, provider);
                        serializers = this._dynamicSerializers;
                    }
                    serializer.serializeWithType(elem, jgen, provider, typeSer);
                }
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, (Throwable)e, value, i);
            }
        }
    }
}

