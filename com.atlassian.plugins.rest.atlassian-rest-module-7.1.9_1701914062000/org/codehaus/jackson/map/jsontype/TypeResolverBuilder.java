/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype;

import java.util.Collection;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeResolverBuilder<T extends TypeResolverBuilder<T>> {
    public Class<?> getDefaultImpl();

    public TypeSerializer buildTypeSerializer(SerializationConfig var1, JavaType var2, Collection<NamedType> var3, BeanProperty var4);

    public TypeDeserializer buildTypeDeserializer(DeserializationConfig var1, JavaType var2, Collection<NamedType> var3, BeanProperty var4);

    public T init(JsonTypeInfo.Id var1, TypeIdResolver var2);

    public T inclusion(JsonTypeInfo.As var1);

    public T typeProperty(String var1);

    public T defaultImpl(Class<?> var1);
}

