/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.IOException;
import java.io.InputStream;
import org.apache.regexp.CharacterIterator;

public final class StreamCharacterIterator
implements CharacterIterator {
    private final InputStream is;
    private final StringBuffer buff;
    private boolean closed;

    public StreamCharacterIterator(InputStream inputStream) {
        this.is = inputStream;
        this.buff = new StringBuffer(512);
        this.closed = false;
    }

    public char charAt(int n) {
        try {
            this.ensure(n);
            return this.buff.charAt(n);
        }
        catch (IOException iOException) {
            throw new StringIndexOutOfBoundsException(iOException.getMessage());
        }
    }

    private void ensure(int n) throws IOException {
        if (this.closed) {
            return;
        }
        if (n < this.buff.length()) {
            return;
        }
        this.read(n + 1 - this.buff.length());
    }

    public boolean isEnd(int n) {
        if (this.buff.length() > n) {
            return false;
        }
        try {
            this.ensure(n);
            return this.buff.length() <= n;
        }
        catch (IOException iOException) {
            throw new StringIndexOutOfBoundsException(iOException.getMessage());
        }
    }

    private int read(int n) throws IOException {
        if (this.closed) {
            return 0;
        }
        int n2 = n;
        while (--n2 >= 0) {
            int n3 = this.is.read();
            if (n3 < 0) {
                this.closed = true;
                break;
            }
            this.buff.append((char)n3);
        }
        return n - n2;
    }

    private void readAll() throws IOException {
        while (!this.closed) {
            this.read(1000);
        }
    }

    public String substring(int n) {
        try {
            this.readAll();
            return this.buff.toString().substring(n);
        }
        catch (IOException iOException) {
            throw new StringIndexOutOfBoundsException(iOException.getMessage());
        }
    }

    public String substring(int n, int n2) {
        try {
            this.ensure(n + n2);
            return this.buff.toString().substring(n, n2);
        }
        catch (IOException iOException) {
            throw new StringIndexOutOfBoundsException(iOException.getMessage());
        }
    }
}

