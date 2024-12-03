/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.LazyReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class ResettableLazyReference<T>
implements Supplier<T> {
    private static final AtomicReferenceFieldUpdater<ResettableLazyReference, InternalReference> updater = AtomicReferenceFieldUpdater.newUpdater(ResettableLazyReference.class, InternalReference.class, "referrent");
    private volatile InternalReference<T> referrent = new InternalReference(this);

    protected abstract T create() throws Exception;

    @Override
    public final T get() {
        return this.referrent.get();
    }

    public final T getInterruptibly() throws InterruptedException {
        return this.referrent.getInterruptibly();
    }

    public final void reset() {
        this.resets();
    }

    public final LazyReference<T> resets() {
        LazyReference result = updater.getAndSet(this, new InternalReference(this));
        this.onReset(result);
        return result;
    }

    protected void onReset(LazyReference<T> oldValue) {
    }

    public final boolean isInitialized() {
        return this.referrent.isInitialized();
    }

    public final void cancel() {
        this.referrent.cancel();
    }

    static class InternalReference<T>
    extends LazyReference<T> {
        private final ResettableLazyReference<T> ref;

        InternalReference(ResettableLazyReference<T> ref) {
            this.ref = ref;
        }

        @Override
        protected T create() throws Exception {
            return this.ref.create();
        }
    }
}

