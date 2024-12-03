/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public class ImmutableMutabilityPlan<T>
implements MutabilityPlan<T> {
    public static final ImmutableMutabilityPlan INSTANCE = new ImmutableMutabilityPlan();

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public Serializable disassemble(T value) {
        return (Serializable)value;
    }

    @Override
    public T assemble(Serializable cached) {
        return (T)cached;
    }
}

