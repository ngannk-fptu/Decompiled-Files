/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

public class GOST3410ValidationParameters {
    private int x0;
    private int c;
    private long x0L;
    private long cL;

    public GOST3410ValidationParameters(int x0, int c) {
        this.x0 = x0;
        this.c = c;
    }

    public GOST3410ValidationParameters(long x0L, long cL) {
        this.x0L = x0L;
        this.cL = cL;
    }

    public int getC() {
        return this.c;
    }

    public int getX0() {
        return this.x0;
    }

    public long getCL() {
        return this.cL;
    }

    public long getX0L() {
        return this.x0L;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GOST3410ValidationParameters)) {
            return false;
        }
        GOST3410ValidationParameters other = (GOST3410ValidationParameters)o;
        if (other.c != this.c) {
            return false;
        }
        if (other.x0 != this.x0) {
            return false;
        }
        if (other.cL != this.cL) {
            return false;
        }
        return other.x0L == this.x0L;
    }

    public int hashCode() {
        return this.x0 ^ this.c ^ (int)this.x0L ^ (int)(this.x0L >> 32) ^ (int)this.cL ^ (int)(this.cL >> 32);
    }
}

