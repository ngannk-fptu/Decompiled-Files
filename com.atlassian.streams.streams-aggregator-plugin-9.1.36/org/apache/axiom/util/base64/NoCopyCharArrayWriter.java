/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.CharArrayWriter;

class NoCopyCharArrayWriter
extends CharArrayWriter {
    NoCopyCharArrayWriter(int expectedSize) {
        super(expectedSize);
    }

    public char[] toCharArray() {
        return this.count == this.buf.length ? this.buf : super.toCharArray();
    }
}

