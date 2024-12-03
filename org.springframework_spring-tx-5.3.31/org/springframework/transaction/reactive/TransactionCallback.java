/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 */
package org.springframework.transaction.reactive;

import org.reactivestreams.Publisher;
import org.springframework.transaction.ReactiveTransaction;

@FunctionalInterface
public interface TransactionCallback<T> {
    public Publisher<T> doInTransaction(ReactiveTransaction var1);
}

