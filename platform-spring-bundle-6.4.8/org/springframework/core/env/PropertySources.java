/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

public interface PropertySources
extends Iterable<PropertySource<?>> {
    default public Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public boolean contains(String var1);

    @Nullable
    public PropertySource<?> get(String var1);
}

