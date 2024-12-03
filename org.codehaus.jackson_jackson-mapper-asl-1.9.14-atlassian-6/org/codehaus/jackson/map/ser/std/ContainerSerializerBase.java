/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.std.SerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ContainerSerializerBase<T>
extends SerializerBase<T> {
    protected ContainerSerializerBase(Class<T> t) {
        super(t);
    }

    protected ContainerSerializerBase(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    public ContainerSerializerBase<?> withValueTypeSerializer(TypeSerializer vts) {
        if (vts == null) {
            return this;
        }
        return this._withValueTypeSerializer(vts);
    }

    public abstract ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer var1);
}

