/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.Nullable;

public class Association<P extends PersistentProperty<P>> {
    private final P inverse;
    @Nullable
    private final P obverse;

    public Association(P inverse, @Nullable P obverse) {
        this.inverse = inverse;
        this.obverse = obverse;
    }

    public P getInverse() {
        return this.inverse;
    }

    @Nullable
    public P getObverse() {
        return this.obverse;
    }
}

