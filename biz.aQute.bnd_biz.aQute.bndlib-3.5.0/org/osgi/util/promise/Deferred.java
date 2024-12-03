/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.util.promise;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Deferred<T> {
    private final PromiseImpl<T> promise = new PromiseImpl();

    public Promise<T> getPromise() {
        return this.promise;
    }

    public void resolve(T value) {
        this.promise.resolve(value, null);
    }

    public void fail(Throwable failure) {
        this.promise.resolve(null, PromiseImpl.requireNonNull(failure));
    }

    public Promise<Void> resolveWith(Promise<? extends T> with) {
        return this.promise.resolveWith(with);
    }
}

