/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

public final class ThreadInterruptedException
extends RuntimeException {
    public ThreadInterruptedException(InterruptedException ie) {
        super(ie);
    }
}

