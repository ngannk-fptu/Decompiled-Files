/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public abstract class MutableMutabilityPlan<T>
implements MutabilityPlan<T> {
    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(T value) {
        return (Serializable)this.deepCopy(value);
    }

    @Override
    public T assemble(Serializable cached) {
        return (T)this.deepCopy(cached);
    }

    @Override
    public final T deepCopy(T value) {
        return value == null ? null : (T)this.deepCopyNotNull(value);
    }

    protected abstract T deepCopyNotNull(T var1);
}

