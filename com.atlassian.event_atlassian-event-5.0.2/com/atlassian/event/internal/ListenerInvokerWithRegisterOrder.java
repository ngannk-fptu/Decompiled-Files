/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.internal;

import com.atlassian.event.spi.ListenerInvoker;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class ListenerInvokerWithRegisterOrder {
    private static final AtomicInteger registerOrderSequence = new AtomicInteger();
    private final Object listener;
    private final ListenerInvoker invoker;
    private final Optional<String> scope;
    private final int registerOrder;

    ListenerInvokerWithRegisterOrder(Object listener, ListenerInvoker invoker, Optional<String> scope) {
        this.invoker = invoker;
        this.listener = listener;
        this.scope = scope;
        this.registerOrder = registerOrderSequence.incrementAndGet();
    }

    ListenerInvoker getInvoker() {
        return this.invoker;
    }

    Optional<String> getScope() {
        return this.scope;
    }

    int getOrder() {
        return this.invoker.getOrder();
    }

    public int getRegisterOrder() {
        return this.registerOrder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListenerInvokerWithRegisterOrder)) {
            return false;
        }
        ListenerInvokerWithRegisterOrder that = (ListenerInvokerWithRegisterOrder)o;
        return this.listener == that.listener && this.invoker.equals(that.invoker) && this.scope.equals(that.scope);
    }

    public int hashCode() {
        return Objects.hash(System.identityHashCode(this.listener), this.invoker);
    }

    boolean isFor(Object someListener) {
        return someListener == this.listener;
    }
}

