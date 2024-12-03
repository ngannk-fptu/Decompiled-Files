/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECPoint;

public class ECPair {
    private final ECPoint x;
    private final ECPoint y;

    public ECPair(ECPoint x, ECPoint y) {
        this.x = x;
        this.y = y;
    }

    public ECPoint getX() {
        return this.x;
    }

    public ECPoint getY() {
        return this.y;
    }

    public boolean equals(ECPair other) {
        return other.getX().equals(this.getX()) && other.getY().equals(this.getY());
    }

    public boolean equals(Object other) {
        return other instanceof ECPair ? this.equals((ECPair)other) : false;
    }

    public int hashCode() {
        return this.x.hashCode() + 37 * this.y.hashCode();
    }
}

