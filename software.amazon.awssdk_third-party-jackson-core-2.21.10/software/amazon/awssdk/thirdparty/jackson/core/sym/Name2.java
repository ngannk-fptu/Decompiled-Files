/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.sym;

import software.amazon.awssdk.thirdparty.jackson.core.sym.Name;

public final class Name2
extends Name {
    private final int q1;
    private final int q2;

    Name2(String name, int hash, int quad1, int quad2) {
        super(name, hash);
        this.q1 = quad1;
        this.q2 = quad2;
    }

    @Override
    public boolean equals(int quad) {
        return false;
    }

    @Override
    public boolean equals(int quad1, int quad2) {
        return quad1 == this.q1 && quad2 == this.q2;
    }

    @Override
    public boolean equals(int quad1, int quad2, int q3) {
        return false;
    }

    @Override
    public boolean equals(int[] quads, int qlen) {
        return qlen == 2 && quads[0] == this.q1 && quads[1] == this.q2;
    }
}

