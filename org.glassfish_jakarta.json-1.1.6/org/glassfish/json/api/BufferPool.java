/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json.api;

public interface BufferPool {
    public char[] take();

    public void recycle(char[] var1);
}

