/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import java.util.Optional;
import org.hibernate.LockOptions;

public interface SimpleNaturalIdLoadAccess<T> {
    public SimpleNaturalIdLoadAccess<T> with(LockOptions var1);

    public SimpleNaturalIdLoadAccess<T> setSynchronizationEnabled(boolean var1);

    public T getReference(Object var1);

    public T load(Object var1);

    public Optional<T> loadOptional(Serializable var1);
}

