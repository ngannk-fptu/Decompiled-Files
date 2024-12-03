/*
 * Decompiled with CFR 0.152.
 */
package antlr;

public class CharQueue {
    protected char[] buffer;
    private int sizeLessOne;
    private int offset;
    protected int nbrEntries;

    public CharQueue(int n) {
        int n2;
        if (n < 0) {
            this.init(16);
            return;
        }
        if (n >= 0x3FFFFFFF) {
            this.init(Integer.MAX_VALUE);
            return;
        }
        for (n2 = 2; n2 < n; n2 *= 2) {
        }
        this.init(n2);
    }

    public final void append(char c) {
        if (this.nbrEntries == this.buffer.length) {
            this.expand();
        }
        this.buffer[this.offset + this.nbrEntries & this.sizeLessOne] = c;
        ++this.nbrEntries;
    }

    public final char elementAt(int n) {
        return this.buffer[this.offset + n & this.sizeLessOne];
    }

    private final void expand() {
        char[] cArray = new char[this.buffer.length * 2];
        for (int i = 0; i < this.buffer.length; ++i) {
            cArray[i] = this.elementAt(i);
        }
        this.buffer = cArray;
        this.sizeLessOne = this.buffer.length - 1;
        this.offset = 0;
    }

    public void init(int n) {
        this.buffer = new char[n];
        this.sizeLessOne = n - 1;
        this.offset = 0;
        this.nbrEntries = 0;
    }

    public final void reset() {
        this.offset = 0;
        this.nbrEntries = 0;
    }

    public final void removeFirst() {
        this.offset = this.offset + 1 & this.sizeLessOne;
        --this.nbrEntries;
    }
}

