/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.sync;

public interface Synchronizer {
    public void beginRead();

    public void endRead();

    public void beginWrite();

    public void endWrite();
}

