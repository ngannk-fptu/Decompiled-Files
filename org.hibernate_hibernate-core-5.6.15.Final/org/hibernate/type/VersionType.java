/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Comparator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

public interface VersionType<T>
extends Type {
    public T seed(SharedSessionContractImplementor var1);

    public T next(T var1, SharedSessionContractImplementor var2);

    public Comparator<T> getComparator();
}

