/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.log;

import java.util.Arrays;

public class PadMessage {
    private final Object text;
    private final int length;

    public PadMessage(Object message, int length) {
        this.text = message;
        this.length = length;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(this.text);
        if (b.length() < this.length) {
            char[] pad = new char[this.length - b.length()];
            Arrays.fill(pad, ' ');
            b.append(pad);
        }
        return b.toString();
    }
}

