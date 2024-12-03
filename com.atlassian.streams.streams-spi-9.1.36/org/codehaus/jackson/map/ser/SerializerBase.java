/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public abstract class SerializerBase<T>
extends org.codehaus.jackson.map.ser.std.SerializerBase<T> {
    protected SerializerBase(Class<T> t) {
        super(t);
    }

    protected SerializerBase(JavaType type) {
        super(type);
    }

    protected SerializerBase(Class<?> t, boolean dummy) {
        super(t, dummy);
    }
}

