/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

public interface PropertySources
extends Iterable<PropertySource<?>> {
    public boolean contains(String var1);

    @Nullable
    public PropertySource<?> get(String var1);
}

