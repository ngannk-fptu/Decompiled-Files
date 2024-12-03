/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.convert;

import org.springframework.data.mapping.Alias;

public interface TypeAliasAccessor<S> {
    public Alias readAliasFrom(S var1);

    public void writeTypeTo(S var1, Object var2);
}

