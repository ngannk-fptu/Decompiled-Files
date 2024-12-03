/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

public final class ThreadInterruptedException
extends RuntimeException {
    public ThreadInterruptedException(InterruptedException ie) {
        super(ie);
    }
}

