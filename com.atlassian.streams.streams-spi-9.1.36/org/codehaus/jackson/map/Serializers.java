/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Serializers {
    public JsonSerializer<?> findSerializer(SerializationConfig var1, JavaType var2, BeanDescription var3, BeanProperty var4);

    public JsonSerializer<?> findArraySerializer(SerializationConfig var1, ArrayType var2, BeanDescription var3, BeanProperty var4, TypeSerializer var5, JsonSerializer<Object> var6);

    public JsonSerializer<?> findCollectionSerializer(SerializationConfig var1, CollectionType var2, BeanDescription var3, BeanProperty var4, TypeSerializer var5, JsonSerializer<Object> var6);

    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig var1, CollectionLikeType var2, BeanDescription var3, BeanProperty var4, TypeSerializer var5, JsonSerializer<Object> var6);

    public JsonSerializer<?> findMapSerializer(SerializationConfig var1, MapType var2, BeanDescription var3, BeanProperty var4, JsonSerializer<Object> var5, TypeSerializer var6, JsonSerializer<Object> var7);

    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig var1, MapLikeType var2, BeanDescription var3, BeanProperty var4, JsonSerializer<Object> var5, TypeSerializer var6, JsonSerializer<Object> var7);

    @Deprecated
    public static class None
    extends Base {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Base
    implements Serializers {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, BeanProperty property) {
            return null;
        }

        @Override
        public JsonSerializer<?> findArraySerializer(SerializationConfig config, ArrayType type, BeanDescription beanDesc, BeanProperty property, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            return null;
        }

        @Override
        public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, BeanProperty property, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            return null;
        }

        @Override
        public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BeanDescription beanDesc, BeanProperty property, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            return null;
        }

        @Override
        public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, BeanProperty property, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            return null;
        }

        @Override
        public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, BeanProperty property, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            return null;
        }
    }
}

