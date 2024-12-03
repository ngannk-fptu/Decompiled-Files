/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;

public interface MutabilityPlan<T>
extends Serializable {
    public boolean isMutable();

    public T deepCopy(T var1);

    public Serializable disassemble(T var1);

    public T assemble(Serializable var1);
}

