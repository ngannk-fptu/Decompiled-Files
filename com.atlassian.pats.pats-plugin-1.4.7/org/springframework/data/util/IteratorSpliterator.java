/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

class IteratorSpliterator<T>
implements Spliterator<T> {
    private static final int BATCH_UNIT = 1024;
    private static final int MAX_BATCH = 0x2000000;
    private final Iterator<? extends T> it;
    private long est;
    private int batch;

    public IteratorSpliterator(Iterator<? extends T> iterator) {
        this.it = iterator;
        this.est = Long.MAX_VALUE;
    }

    @Override
    public Spliterator<T> trySplit() {
        Iterator<T> i = this.it;
        long s = this.est;
        if (s > 1L && i.hasNext()) {
            int n = this.batch + 1024;
            if ((long)n > s) {
                n = (int)s;
            }
            if (n > 0x2000000) {
                n = 0x2000000;
            }
            Object[] a = new Object[n];
            int j = 0;
            do {
                a[j] = i.next();
            } while (++j < n && i.hasNext());
            this.batch = j;
            if (this.est != Long.MAX_VALUE) {
                this.est -= (long)j;
            }
            return Spliterators.spliterator(a, 0, j, 0);
        }
        return null;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        this.it.forEachRemaining(action);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (this.it.hasNext()) {
            action.accept(this.it.next());
            return true;
        }
        return false;
    }

    @Override
    public long estimateSize() {
        return -1L;
    }

    @Override
    public int characteristics() {
        return 0;
    }

    @Override
    public Comparator<? super T> getComparator() {
        if (this.hasCharacteristics(4)) {
            return null;
        }
        throw new IllegalStateException();
    }
}

