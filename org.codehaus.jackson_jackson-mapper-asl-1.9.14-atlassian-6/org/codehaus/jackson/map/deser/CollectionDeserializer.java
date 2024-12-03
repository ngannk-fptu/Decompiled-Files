/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser;

import java.lang.reflect.Constructor;
import java.util.Collection;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class CollectionDeserializer
extends org.codehaus.jackson.map.deser.std.CollectionDeserializer {
    @Deprecated
    public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, Constructor<Collection<Object>> defCtor) {
        super(collectionType, valueDeser, valueTypeDeser, defCtor);
    }

    public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
        super(collectionType, valueDeser, valueTypeDeser, valueInstantiator);
    }

    protected CollectionDeserializer(CollectionDeserializer src) {
        super(src);
    }
}

