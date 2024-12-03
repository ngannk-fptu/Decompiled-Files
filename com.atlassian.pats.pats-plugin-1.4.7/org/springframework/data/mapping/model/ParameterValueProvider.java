/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.lang.Nullable;

public interface ParameterValueProvider<P extends PersistentProperty<P>> {
    @Nullable
    public <T> T getParameterValue(PreferredConstructor.Parameter<T, P> var1);
}

