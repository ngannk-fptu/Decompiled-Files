/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.io.SerializedString
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerFactory;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.KeyDeserializers;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DeserializerProvider {
    protected DeserializerProvider() {
    }

    public abstract DeserializerProvider withFactory(DeserializerFactory var1);

    public abstract DeserializerProvider withAdditionalDeserializers(Deserializers var1);

    public abstract DeserializerProvider withAdditionalKeyDeserializers(KeyDeserializers var1);

    public abstract DeserializerProvider withDeserializerModifier(BeanDeserializerModifier var1);

    public abstract DeserializerProvider withAbstractTypeResolver(AbstractTypeResolver var1);

    public abstract DeserializerProvider withValueInstantiators(ValueInstantiators var1);

    public abstract JsonDeserializer<Object> findValueDeserializer(DeserializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    public abstract JsonDeserializer<Object> findTypedValueDeserializer(DeserializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    public abstract KeyDeserializer findKeyDeserializer(DeserializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    public abstract boolean hasValueDeserializerFor(DeserializationConfig var1, JavaType var2);

    public abstract JavaType mapAbstractType(DeserializationConfig var1, JavaType var2) throws JsonMappingException;

    public abstract SerializedString findExpectedRootName(DeserializationConfig var1, JavaType var2) throws JsonMappingException;

    public abstract int cachedDeserializersCount();

    public abstract void flushCachedDeserializers();
}

