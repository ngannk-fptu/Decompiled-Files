/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

import com.amazonaws.event.DeliveryMode;
import com.amazonaws.event.ProgressListener;

public abstract class SyncProgressListener
implements ProgressListener,
DeliveryMode {
    @Override
    public boolean isSyncCallSafe() {
        return true;
    }
}

