/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

import com.amazonaws.SdkClientException;
import com.amazonaws.event.DeliveryMode;
import com.amazonaws.event.ProgressEvent;

public interface ProgressListener {
    public static final ProgressListener NOOP = new NoOpProgressListener();

    public void progressChanged(ProgressEvent var1);

    public static class ExceptionReporter
    implements ProgressListener,
    DeliveryMode {
        private final ProgressListener listener;
        private final boolean syncCallSafe;
        private volatile Throwable cause;

        public ExceptionReporter(ProgressListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException();
            }
            this.listener = listener;
            if (listener instanceof DeliveryMode) {
                DeliveryMode cs = (DeliveryMode)((Object)listener);
                this.syncCallSafe = cs.isSyncCallSafe();
            } else {
                this.syncCallSafe = false;
            }
        }

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            if (this.cause != null) {
                return;
            }
            try {
                this.listener.progressChanged(progressEvent);
            }
            catch (Throwable t) {
                this.cause = t;
            }
        }

        public void throwExceptionIfAny() {
            if (this.cause != null) {
                throw new SdkClientException(this.cause);
            }
        }

        public Throwable getCause() {
            return this.cause;
        }

        public static ExceptionReporter wrap(ProgressListener listener) {
            return new ExceptionReporter(listener);
        }

        @Override
        public boolean isSyncCallSafe() {
            return this.syncCallSafe;
        }
    }

    public static class NoOpProgressListener
    implements ProgressListener,
    DeliveryMode {
        @Override
        public boolean isSyncCallSafe() {
            return true;
        }

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
        }
    }
}

