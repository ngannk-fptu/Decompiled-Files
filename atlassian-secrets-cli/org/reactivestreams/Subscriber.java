/*
 * Decompiled with CFR 0.152.
 */
package org.reactivestreams;

import org.reactivestreams.Subscription;

public interface Subscriber<T> {
    public void onSubscribe(Subscription var1);

    public void onNext(T var1);

    public void onError(Throwable var1);

    public void onComplete();
}

