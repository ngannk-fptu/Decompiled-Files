/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

public class CMStateSet {
    int fBitCount;
    int fByteCount;
    int fBits1;
    int fBits2;
    byte[] fByteArray;

    public CMStateSet(int n) {
        this.fBitCount = n;
        if (this.fBitCount < 0) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount > 64) {
            this.fByteCount = this.fBitCount / 8;
            if (this.fBitCount % 8 != 0) {
                ++this.fByteCount;
            }
            this.fByteArray = new byte[this.fByteCount];
        }
        this.zeroBits();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            stringBuffer.append('{');
            for (int i = 0; i < this.fBitCount; ++i) {
                if (!this.getBit(i)) continue;
                stringBuffer.append(' ').append(i);
            }
            stringBuffer.append(" }");
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return stringBuffer.toString();
    }

    public final void intersection(CMStateSet cMStateSet) {
        if (this.fBitCount < 65) {
            this.fBits1 &= cMStateSet.fBits1;
            this.fBits2 &= cMStateSet.fBits2;
        } else {
            for (int i = this.fByteCount - 1; i >= 0; --i) {
                int n = i;
                this.fByteArray[n] = (byte)(this.fByteArray[n] & cMStateSet.fByteArray[i]);
            }
        }
    }

    public final boolean getBit(int n) {
        if (n >= this.fBitCount) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount < 65) {
            int n2 = 1 << n % 32;
            if (n < 32) {
                return (this.fBits1 & n2) != 0;
            }
            return (this.fBits2 & n2) != 0;
        }
        int n3 = n >> 3;
        byte by = (byte)(1 << n % 8);
        return (this.fByteArray[n3] & by) != 0;
    }

    public final boolean isEmpty() {
        if (this.fBitCount < 65) {
            return this.fBits1 == 0 && this.fBits2 == 0;
        }
        for (int i = this.fByteCount - 1; i >= 0; --i) {
            if (this.fByteArray[i] == 0) continue;
            return false;
        }
        return true;
    }

    final boolean isSameSet(CMStateSet cMStateSet) {
        if (this.fBitCount != cMStateSet.fBitCount) {
            return false;
        }
        if (this.fBitCount < 65) {
            return this.fBits1 == cMStateSet.fBits1 && this.fBits2 == cMStateSet.fBits2;
        }
        for (int i = this.fByteCount - 1; i >= 0; --i) {
            if (this.fByteArray[i] == cMStateSet.fByteArray[i]) continue;
            return false;
        }
        return true;
    }

    public final void union(CMStateSet cMStateSet) {
        if (this.fBitCount < 65) {
            this.fBits1 |= cMStateSet.fBits1;
            this.fBits2 |= cMStateSet.fBits2;
        } else {
            for (int i = this.fByteCount - 1; i >= 0; --i) {
                int n = i;
                this.fByteArray[n] = (byte)(this.fByteArray[n] | cMStateSet.fByteArray[i]);
            }
        }
    }

    public final void setBit(int n) {
        if (n >= this.fBitCount) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount < 65) {
            int n2 = 1 << n % 32;
            if (n < 32) {
                this.fBits1 &= ~n2;
                this.fBits1 |= n2;
            } else {
                this.fBits2 &= ~n2;
                this.fBits2 |= n2;
            }
        } else {
            int n3;
            byte by = (byte)(1 << n % 8);
            int n4 = n3 = n >> 3;
            this.fByteArray[n4] = (byte)(this.fByteArray[n4] & ~by);
            int n5 = n3;
            this.fByteArray[n5] = (byte)(this.fByteArray[n5] | by);
        }
    }

    public final void setTo(CMStateSet cMStateSet) {
        if (this.fBitCount != cMStateSet.fBitCount) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount < 65) {
            this.fBits1 = cMStateSet.fBits1;
            this.fBits2 = cMStateSet.fBits2;
        } else {
            for (int i = this.fByteCount - 1; i >= 0; --i) {
                this.fByteArray[i] = cMStateSet.fByteArray[i];
            }
        }
    }

    public final void zeroBits() {
        if (this.fBitCount < 65) {
            this.fBits1 = 0;
            this.fBits2 = 0;
        } else {
            for (int i = this.fByteCount - 1; i >= 0; --i) {
                this.fByteArray[i] = 0;
            }
        }
    }

    public boolean equals(Object object) {
        if (!(object instanceof CMStateSet)) {
            return false;
        }
        return this.isSameSet((CMStateSet)object);
    }

    public int hashCode() {
        if (this.fBitCount < 65) {
            return this.fBits1 + this.fBits2 * 31;
        }
        int n = 0;
        for (int i = this.fByteCount - 1; i >= 0; --i) {
            n = this.fByteArray[i] + n * 31;
        }
        return n;
    }
}

