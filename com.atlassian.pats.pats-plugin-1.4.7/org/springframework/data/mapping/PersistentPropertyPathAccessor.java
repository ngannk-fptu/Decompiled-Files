/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.AccessOptions;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.lang.Nullable;

public interface PersistentPropertyPathAccessor<T>
extends PersistentPropertyAccessor<T> {
    @Override
    @Nullable
    default public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path) {
        return PersistentPropertyAccessor.super.getProperty(path);
    }

    @Nullable
    public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> var1, AccessOptions.GetOptions var2);

    @Override
    public void setProperty(PersistentPropertyPath<? extends PersistentProperty<?>> var1, @Nullable Object var2);

    public void setProperty(PersistentPropertyPath<? extends PersistentProperty<?>> var1, @Nullable Object var2, AccessOptions.SetOptions var3);
}

