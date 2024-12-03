/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class HandlerInstantiator {
    public abstract JsonDeserializer<?> deserializerInstance(DeserializationConfig var1, Annotated var2, Class<? extends JsonDeserializer<?>> var3);

    public abstract KeyDeserializer keyDeserializerInstance(DeserializationConfig var1, Annotated var2, Class<? extends KeyDeserializer> var3);

    public abstract JsonSerializer<?> serializerInstance(SerializationConfig var1, Annotated var2, Class<? extends JsonSerializer<?>> var3);

    public abstract TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> var1, Annotated var2, Class<? extends TypeResolverBuilder<?>> var3);

    public abstract TypeIdResolver typeIdResolverInstance(MapperConfig<?> var1, Annotated var2, Class<? extends TypeIdResolver> var3);

    public ValueInstantiator valueInstantiatorInstance(MapperConfig<?> config, Annotated annotated, Class<? extends ValueInstantiator> resolverClass) {
        return null;
    }
}

