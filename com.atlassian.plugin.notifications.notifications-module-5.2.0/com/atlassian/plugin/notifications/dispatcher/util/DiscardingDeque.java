/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.dispatcher.util;

import java.util.concurrent.LinkedBlockingDeque;

public class DiscardingDeque<E>
extends LinkedBlockingDeque<E> {
    public DiscardingDeque(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized boolean offerFirst(E e) {
        if (this.remainingCapacity() == 0) {
            this.removeLast();
        }
        super.offerFirst(e);
        return true;
    }
}

