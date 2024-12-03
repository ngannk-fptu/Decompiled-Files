/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.sym;

import org.codehaus.jackson.sym.Name;

public final class NameN
extends Name {
    final int[] mQuads;
    final int mQuadLen;

    NameN(String name, int hash, int[] quads, int quadLen) {
        super(name, hash);
        if (quadLen < 3) {
            throw new IllegalArgumentException("Qlen must >= 3");
        }
        this.mQuads = quads;
        this.mQuadLen = quadLen;
    }

    public boolean equals(int quad) {
        return false;
    }

    public boolean equals(int quad1, int quad2) {
        return false;
    }

    public boolean equals(int[] quads, int qlen) {
        if (qlen != this.mQuadLen) {
            return false;
        }
        for (int i = 0; i < qlen; ++i) {
            if (quads[i] == this.mQuads[i]) continue;
            return false;
        }
        return true;
    }
}

