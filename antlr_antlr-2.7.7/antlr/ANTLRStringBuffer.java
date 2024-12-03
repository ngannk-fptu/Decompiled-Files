/*
 * Decompiled with CFR 0.152.
 */
package antlr;

public class ANTLRStringBuffer {
    protected char[] buffer = null;
    protected int length = 0;

    public ANTLRStringBuffer() {
        this.buffer = new char[50];
    }

    public ANTLRStringBuffer(int n) {
        this.buffer = new char[n];
    }

    public final void append(char c) {
        if (this.length >= this.buffer.length) {
            int n;
            for (n = this.buffer.length; this.length >= n; n *= 2) {
            }
            char[] cArray = new char[n];
            for (int i = 0; i < this.length; ++i) {
                cArray[i] = this.buffer[i];
            }
            this.buffer = cArray;
        }
        this.buffer[this.length] = c;
        ++this.length;
    }

    public final void append(String string) {
        for (int i = 0; i < string.length(); ++i) {
            this.append(string.charAt(i));
        }
    }

    public final char charAt(int n) {
        return this.buffer[n];
    }

    public final char[] getBuffer() {
        return this.buffer;
    }

    public final int length() {
        return this.length;
    }

    public final void setCharAt(int n, char c) {
        this.buffer[n] = c;
    }

    public final void setLength(int n) {
        if (n < this.length) {
            this.length = n;
        } else {
            while (n > this.length) {
                this.append('\u0000');
            }
        }
    }

    public final String toString() {
        return new String(this.buffer, 0, this.length);
    }
}

