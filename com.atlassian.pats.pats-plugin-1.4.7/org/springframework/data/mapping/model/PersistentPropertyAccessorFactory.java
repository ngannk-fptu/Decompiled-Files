/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyAccessor;

public interface PersistentPropertyAccessorFactory {
    public <T> PersistentPropertyAccessor<T> getPropertyAccessor(PersistentEntity<?, ?> var1, T var2);

    public boolean isSupported(PersistentEntity<?, ?> var1);
}

