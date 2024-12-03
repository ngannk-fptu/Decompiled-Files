/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ContainerDeserializerBase<T>
extends StdDeserializer<T> {
    protected ContainerDeserializerBase(Class<?> selfType) {
        super(selfType);
    }

    public abstract JavaType getContentType();

    public abstract JsonDeserializer<Object> getContentDeserializer();
}

