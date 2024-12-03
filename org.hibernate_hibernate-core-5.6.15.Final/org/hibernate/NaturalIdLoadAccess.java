/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Optional;
import org.hibernate.LockOptions;

public interface NaturalIdLoadAccess<T> {
    public NaturalIdLoadAccess<T> with(LockOptions var1);

    public NaturalIdLoadAccess<T> using(String var1, Object var2);

    public NaturalIdLoadAccess<T> setSynchronizationEnabled(boolean var1);

    public T getReference();

    public T load();

    public Optional<T> loadOptional();
}

