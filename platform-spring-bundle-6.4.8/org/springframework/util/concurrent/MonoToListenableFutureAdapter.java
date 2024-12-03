/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.util.concurrent;

import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import reactor.core.publisher.Mono;

public class MonoToListenableFutureAdapter<T>
extends CompletableToListenableFutureAdapter<T> {
    public MonoToListenableFutureAdapter(Mono<T> mono) {
        super(mono.toFuture());
    }
}

