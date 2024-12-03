/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.sync;

import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.sync.Synchronizer;

public interface SynchronizerSupport {
    public Synchronizer getSynchronizer();

    public void setSynchronizer(Synchronizer var1);

    public void lock(LockMode var1);

    public void unlock(LockMode var1);
}

