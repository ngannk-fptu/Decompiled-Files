/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import java.util.Queue;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class ConsumingQueueIterator<T>
extends AbstractIterator<T> {
    private final Queue<T> queue;

    ConsumingQueueIterator(Queue<T> queue) {
        this.queue = Preconditions.checkNotNull(queue);
    }

    @Override
    @CheckForNull
    protected T computeNext() {
        if (this.queue.isEmpty()) {
            return this.endOfData();
        }
        return this.queue.remove();
    }
}

