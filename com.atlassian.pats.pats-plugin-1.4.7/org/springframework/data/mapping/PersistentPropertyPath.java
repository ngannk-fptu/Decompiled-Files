/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;

public interface PersistentPropertyPath<P extends PersistentProperty<P>>
extends Streamable<P> {
    @Nullable
    public String toDotPath();

    @Nullable
    public String toDotPath(Converter<? super P, String> var1);

    @Nullable
    public String toPath(String var1);

    @Nullable
    public String toPath(String var1, Converter<? super P, String> var2);

    @Nullable
    public P getLeafProperty();

    default public P getRequiredLeafProperty() {
        P property = this.getLeafProperty();
        if (property == null) {
            throw new IllegalStateException("No leaf property found!");
        }
        return property;
    }

    @Nullable
    public P getBaseProperty();

    public boolean isBasePathOf(PersistentPropertyPath<P> var1);

    public PersistentPropertyPath<P> getExtensionForBaseOf(PersistentPropertyPath<P> var1);

    public PersistentPropertyPath<P> getParentPath();

    public int getLength();
}

