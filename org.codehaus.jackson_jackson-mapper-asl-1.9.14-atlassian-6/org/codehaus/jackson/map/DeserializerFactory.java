/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.KeyDeserializers;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DeserializerFactory {
    protected static final Deserializers[] NO_DESERIALIZERS = new Deserializers[0];

    public abstract Config getConfig();

    public abstract DeserializerFactory withConfig(Config var1);

    public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
        return this.withConfig(this.getConfig().withAdditionalDeserializers(additional));
    }

    public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
        return this.withConfig(this.getConfig().withAdditionalKeyDeserializers(additional));
    }

    public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
        return this.withConfig(this.getConfig().withDeserializerModifier(modifier));
    }

    public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
        return this.withConfig(this.getConfig().withAbstractTypeResolver(resolver));
    }

    public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
        return this.withConfig(this.getConfig().withValueInstantiators(instantiators));
    }

    public abstract JavaType mapAbstractType(DeserializationConfig var1, JavaType var2) throws JsonMappingException;

    public abstract ValueInstantiator findValueInstantiator(DeserializationConfig var1, BasicBeanDescription var2) throws JsonMappingException;

    public abstract JsonDeserializer<Object> createBeanDeserializer(DeserializationConfig var1, DeserializerProvider var2, JavaType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createArrayDeserializer(DeserializationConfig var1, DeserializerProvider var2, ArrayType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createCollectionDeserializer(DeserializationConfig var1, DeserializerProvider var2, CollectionType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationConfig var1, DeserializerProvider var2, CollectionLikeType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createEnumDeserializer(DeserializationConfig var1, DeserializerProvider var2, JavaType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createMapDeserializer(DeserializationConfig var1, DeserializerProvider var2, MapType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createMapLikeDeserializer(DeserializationConfig var1, DeserializerProvider var2, MapLikeType var3, BeanProperty var4) throws JsonMappingException;

    public abstract JsonDeserializer<?> createTreeDeserializer(DeserializationConfig var1, DeserializerProvider var2, JavaType var3, BeanProperty var4) throws JsonMappingException;

    public KeyDeserializer createKeyDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        return null;
    }

    public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType, BeanProperty property) throws JsonMappingException {
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class Config {
        public abstract Config withAdditionalDeserializers(Deserializers var1);

        public abstract Config withAdditionalKeyDeserializers(KeyDeserializers var1);

        public abstract Config withDeserializerModifier(BeanDeserializerModifier var1);

        public abstract Config withAbstractTypeResolver(AbstractTypeResolver var1);

        public abstract Config withValueInstantiators(ValueInstantiators var1);

        public abstract Iterable<Deserializers> deserializers();

        public abstract Iterable<KeyDeserializers> keyDeserializers();

        public abstract Iterable<BeanDeserializerModifier> deserializerModifiers();

        public abstract Iterable<AbstractTypeResolver> abstractTypeResolvers();

        public abstract Iterable<ValueInstantiators> valueInstantiators();

        public abstract boolean hasDeserializers();

        public abstract boolean hasKeyDeserializers();

        public abstract boolean hasDeserializerModifiers();

        public abstract boolean hasAbstractTypeResolvers();

        public abstract boolean hasValueInstantiators();
    }
}

