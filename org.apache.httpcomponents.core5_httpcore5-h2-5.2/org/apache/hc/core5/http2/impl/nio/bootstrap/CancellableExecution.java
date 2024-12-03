/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.Cancellable
 *  org.apache.hc.core5.concurrent.CancellableDependency
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.concurrent.CancellableDependency;

final class CancellableExecution
implements CancellableDependency {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<Cancellable> dependencyRef = new AtomicReference();

    CancellableExecution() {
    }

    public void setDependency(Cancellable cancellable) {
        Cancellable dependency;
        this.dependencyRef.set(cancellable);
        if (this.cancelled.get() && (dependency = (Cancellable)this.dependencyRef.getAndSet(null)) != null) {
            dependency.cancel();
        }
    }

    public boolean isCancelled() {
        return this.cancelled.get();
    }

    public boolean cancel() {
        if (this.cancelled.compareAndSet(false, true)) {
            Cancellable dependency = this.dependencyRef.getAndSet(null);
            if (dependency != null) {
                dependency.cancel();
            }
            return true;
        }
        return false;
    }
}

