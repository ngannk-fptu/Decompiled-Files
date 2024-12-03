/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BitArray
implements Externalizable {
    static final long serialVersionUID = -4876019880708377663L;
    private int[] _bits;
    private int _bitSize;
    private int _intSize;
    private int _mask;
    private static final int[] _masks = new int[]{Integer.MIN_VALUE, 0x40000000, 0x20000000, 0x10000000, 0x8000000, 0x4000000, 0x2000000, 0x1000000, 0x800000, 0x400000, 0x200000, 0x100000, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};
    private static final boolean DEBUG_ASSERTIONS = false;
    private int _pos = Integer.MAX_VALUE;
    private int _node = 0;
    private int _int = 0;
    private int _bit = 0;
    int _first = Integer.MAX_VALUE;
    int _last = Integer.MIN_VALUE;

    public BitArray() {
        this(32);
    }

    public BitArray(int size) {
        if (size < 32) {
            size = 32;
        }
        this._bitSize = size;
        this._intSize = (this._bitSize >>> 5) + 1;
        this._bits = new int[this._intSize + 1];
    }

    public BitArray(int size, int[] bits) {
        if (size < 32) {
            size = 32;
        }
        this._bitSize = size;
        this._intSize = (this._bitSize >>> 5) + 1;
        this._bits = bits;
    }

    public void setMask(int mask) {
        this._mask = mask;
    }

    public int getMask() {
        return this._mask;
    }

    public final int size() {
        return this._bitSize;
    }

    public final boolean getBit(int bit) {
        return (this._bits[bit >>> 5] & _masks[bit % 32]) != 0;
    }

    public final int getNextBit(int startBit) {
        for (int i = startBit >>> 5; i <= this._intSize; ++i) {
            int bits = this._bits[i];
            if (bits != 0) {
                for (int b = startBit % 32; b < 32; ++b) {
                    if ((bits & _masks[b]) == 0) continue;
                    return (i << 5) + b;
                }
            }
            startBit = 0;
        }
        return -1;
    }

    public final int getBitNumber(int pos) {
        if (pos == this._pos) {
            return this._node;
        }
        if (pos < this._pos) {
            this._pos = 0;
            this._bit = 0;
            this._int = 0;
        }
        while (this._int <= this._intSize) {
            int bits = this._bits[this._int];
            if (bits != 0) {
                while (this._bit < 32) {
                    if ((bits & _masks[this._bit]) != 0 && ++this._pos == pos) {
                        this._node = (this._int << 5) + this._bit - 1;
                        return this._node;
                    }
                    ++this._bit;
                }
                this._bit = 0;
            }
            ++this._int;
        }
        return 0;
    }

    public final int[] data() {
        return this._bits;
    }

    public final void setBit(int bit) {
        if (bit >= this._bitSize) {
            return;
        }
        int i = bit >>> 5;
        if (i < this._first) {
            this._first = i;
        }
        if (i > this._last) {
            this._last = i;
        }
        int n = i;
        this._bits[n] = this._bits[n] | _masks[bit % 32];
    }

    public final BitArray merge(BitArray other) {
        if (this._last == -1) {
            this._bits = other._bits;
        } else if (other._last != -1) {
            int stop;
            int start = this._first < other._first ? this._first : other._first;
            int n = stop = this._last > other._last ? this._last : other._last;
            if (other._intSize > this._intSize) {
                if (stop > this._intSize) {
                    stop = this._intSize;
                }
                for (int i = start; i <= stop; ++i) {
                    int n2 = i;
                    other._bits[n2] = other._bits[n2] | this._bits[i];
                }
                this._bits = other._bits;
            } else {
                if (stop > other._intSize) {
                    stop = other._intSize;
                }
                for (int i = start; i <= stop; ++i) {
                    int n3 = i;
                    this._bits[n3] = this._bits[n3] | other._bits[i];
                }
            }
        }
        return this;
    }

    public final void resize(int newSize) {
        if (newSize > this._bitSize) {
            this._intSize = (newSize >>> 5) + 1;
            int[] newBits = new int[this._intSize + 1];
            System.arraycopy(this._bits, 0, newBits, 0, (this._bitSize >>> 5) + 1);
            this._bits = newBits;
            this._bitSize = newSize;
        }
    }

    public BitArray cloneArray() {
        return new BitArray(this._intSize, this._bits);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this._bitSize);
        out.writeInt(this._mask);
        out.writeObject(this._bits);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._bitSize = in.readInt();
        this._intSize = (this._bitSize >>> 5) + 1;
        this._mask = in.readInt();
        this._bits = (int[])in.readObject();
    }
}

