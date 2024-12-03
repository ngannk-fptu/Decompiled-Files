/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.mapping.callback;

import java.util.function.BiFunction;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.data.mapping.callback.EntityCallbackInvoker;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

interface ReactiveEntityCallbackInvoker
extends EntityCallbackInvoker {
    @NonNull
    public <T> Mono<T> invokeCallback(EntityCallback<T> var1, @Nullable T var2, BiFunction<EntityCallback<T>, T, Object> var3);
}

