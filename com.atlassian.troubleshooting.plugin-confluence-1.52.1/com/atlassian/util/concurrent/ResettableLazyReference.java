/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import net.jcip.annotations.ThreadSafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ThreadSafe
public abstract class ResettableLazyReference<T>
implements Supplier<T> {
    private volatile InternalReference referrent = new InternalReference();

    protected abstract T create() throws Exception;

    @Override
    public T get() {
        return this.referrent.get();
    }

    public final T getInterruptibly() throws InterruptedException {
        return this.referrent.getInterruptibly();
    }

    public void reset() {
        this.referrent = new InternalReference();
    }

    public boolean isInitialized() {
        return this.referrent.isInitialized();
    }

    public void cancel() {
        this.referrent.cancel();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class InternalReference
    extends LazyReference<T> {
        InternalReference() {
        }

        @Override
        protected T create() throws Exception {
            return ResettableLazyReference.this.create();
        }
    }
}

