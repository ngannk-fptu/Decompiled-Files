/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import org.apache.fontbox.cff.CFFCIDFont;

public abstract class FDSelect {
    protected final CFFCIDFont owner;

    public FDSelect(CFFCIDFont owner) {
        this.owner = owner;
    }

    public abstract int getFDIndex(int var1);
}

