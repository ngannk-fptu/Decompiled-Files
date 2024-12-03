/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Deserializers {
    public JsonDeserializer<?> findArrayDeserializer(ArrayType var1, DeserializationConfig var2, DeserializerProvider var3, BeanProperty var4, TypeDeserializer var5, JsonDeserializer<?> var6) throws JsonMappingException;

    public JsonDeserializer<?> findCollectionDeserializer(CollectionType var1, DeserializationConfig var2, DeserializerProvider var3, BeanDescription var4, BeanProperty var5, TypeDeserializer var6, JsonDeserializer<?> var7) throws JsonMappingException;

    public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType var1, DeserializationConfig var2, DeserializerProvider var3, BeanDescription var4, BeanProperty var5, TypeDeserializer var6, JsonDeserializer<?> var7) throws JsonMappingException;

    public JsonDeserializer<?> findEnumDeserializer(Class<?> var1, DeserializationConfig var2, BeanDescription var3, BeanProperty var4) throws JsonMappingException;

    public JsonDeserializer<?> findMapDeserializer(MapType var1, DeserializationConfig var2, DeserializerProvider var3, BeanDescription var4, BeanProperty var5, KeyDeserializer var6, TypeDeserializer var7, JsonDeserializer<?> var8) throws JsonMappingException;

    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType var1, DeserializationConfig var2, DeserializerProvider var3, BeanDescription var4, BeanProperty var5, KeyDeserializer var6, TypeDeserializer var7, JsonDeserializer<?> var8) throws JsonMappingException;

    public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> var1, DeserializationConfig var2, BeanProperty var3) throws JsonMappingException;

    public JsonDeserializer<?> findBeanDeserializer(JavaType var1, DeserializationConfig var2, DeserializerProvider var3, BeanDescription var4, BeanProperty var5) throws JsonMappingException;

    @Deprecated
    public static class None
    extends Base {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Base
    implements Deserializers {
        @Override
        public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, DeserializerProvider provider, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanProperty property) throws JsonMappingException {
            return null;
        }

        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
            return null;
        }
    }
}

