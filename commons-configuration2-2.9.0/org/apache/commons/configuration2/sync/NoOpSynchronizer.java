/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.sync;

import org.apache.commons.configuration2.sync.Synchronizer;

public enum NoOpSynchronizer implements Synchronizer
{
    INSTANCE;


    @Override
    public void beginRead() {
    }

    @Override
    public void endRead() {
    }

    @Override
    public void beginWrite() {
    }

    @Override
    public void endWrite() {
    }
}

