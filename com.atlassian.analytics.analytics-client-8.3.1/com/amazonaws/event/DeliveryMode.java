/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

import com.amazonaws.event.ProgressListener;

public interface DeliveryMode {
    public boolean isSyncCallSafe();

    public static class Check {
        public static boolean isSyncCallSafe(ProgressListener listener) {
            if (listener instanceof DeliveryMode) {
                DeliveryMode mode = (DeliveryMode)((Object)listener);
                return mode.isSyncCallSafe();
            }
            return listener == null;
        }
    }
}

