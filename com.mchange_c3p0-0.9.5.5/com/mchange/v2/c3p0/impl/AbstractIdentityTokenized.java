/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.impl.IdentityTokenized;

public abstract class AbstractIdentityTokenized
implements IdentityTokenized {
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof IdentityTokenized) {
            return this.getIdentityToken().equals(((IdentityTokenized)o).getIdentityToken());
        }
        return false;
    }

    public int hashCode() {
        return ~this.getIdentityToken().hashCode();
    }
}

