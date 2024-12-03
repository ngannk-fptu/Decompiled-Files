/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.hibernate.EntityMode;
import org.hibernate.internal.util.ValueHolder;
import org.hibernate.type.Type;

public final class TypedValue
implements Serializable {
    private final Type type;
    private final Object value;
    private transient ValueHolder<Integer> hashcode;

    public TypedValue(Type type, Object value) {
        this.type = type;
        this.value = value;
        this.initTransients();
    }

    @Deprecated
    public TypedValue(Type type, Object value, EntityMode entityMode) {
        this(type, value);
    }

    public Object getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }

    public String toString() {
        return this.value == null ? "null" : this.value.toString();
    }

    public int hashCode() {
        return this.hashcode.getValue();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        TypedValue that = (TypedValue)other;
        return this.type.getReturnedClass() == that.type.getReturnedClass() && this.type.isEqual(that.value, this.value);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.initTransients();
    }

    private void initTransients() {
        this.hashcode = new ValueHolder<1>(new ValueHolder.DeferredInitializer<Integer>(){

            @Override
            public Integer initialize() {
                return TypedValue.this.value == null ? 0 : TypedValue.this.type.getHashCode(TypedValue.this.value);
            }
        });
    }
}

