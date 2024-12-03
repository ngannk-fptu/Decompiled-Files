/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import org.apache.batik.util.io.CharDecoder;

public class StringDecoder
implements CharDecoder {
    protected String string;
    protected int length;
    protected int next;

    public StringDecoder(String s) {
        this.string = s;
        this.length = s.length();
    }

    @Override
    public int readChar() throws IOException {
        if (this.next == this.length) {
            return -1;
        }
        return this.string.charAt(this.next++);
    }

    @Override
    public void dispose() throws IOException {
        this.string = null;
    }
}

