/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import org.codehaus.jackson.map.ser.std.SerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public abstract class ScalarSerializerBase<T>
extends SerializerBase<T> {
    protected ScalarSerializerBase(Class<T> t) {
        super(t);
    }

    protected ScalarSerializerBase(Class<?> t, boolean dummy) {
        super(t);
    }
}

