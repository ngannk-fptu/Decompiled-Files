/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

public class FastInfosetException
extends Exception {
    public FastInfosetException(String message) {
        super(message);
    }

    public FastInfosetException(String message, Exception e) {
        super(message, e);
    }

    public FastInfosetException(Exception e) {
        super(e);
    }
}

