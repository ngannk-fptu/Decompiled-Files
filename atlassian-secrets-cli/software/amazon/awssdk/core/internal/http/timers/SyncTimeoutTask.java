/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.timers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTask;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class SyncTimeoutTask
implements TimeoutTask {
    private final Thread threadToInterrupt;
    private volatile boolean hasExecuted;
    private volatile boolean isCancelled;
    private final Object lock = new Object();
    private Abortable abortable;

    SyncTimeoutTask(Thread threadToInterrupt) {
        this.threadToInterrupt = Validate.paramNotNull(threadToInterrupt, "threadToInterrupt");
    }

    @Override
    public void abortable(Abortable abortable) {
        this.abortable = abortable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Object object = this.lock;
        synchronized (object) {
            if (this.isCancelled) {
                return;
            }
            this.hasExecuted = true;
            this.threadToInterrupt.interrupt();
            if (this.abortable != null) {
                this.abortable.abort();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancel() {
        Object object = this.lock;
        synchronized (object) {
            this.isCancelled = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hasExecuted() {
        Object object = this.lock;
        synchronized (object) {
            return this.hasExecuted;
        }
    }
}

