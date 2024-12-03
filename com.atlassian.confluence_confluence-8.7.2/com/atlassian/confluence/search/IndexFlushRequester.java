/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

public interface IndexFlushRequester {
    public void requestFlush();

    public void resume();

    public Resumer pause();

    @FunctionalInterface
    public static interface Resumer
    extends AutoCloseable {
        @Override
        public void close();
    }
}

