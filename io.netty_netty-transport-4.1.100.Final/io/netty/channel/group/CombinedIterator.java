/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.channel.group;

import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator<E>
implements Iterator<E> {
    private final Iterator<E> i1;
    private final Iterator<E> i2;
    private Iterator<E> currentIterator;

    CombinedIterator(Iterator<E> i1, Iterator<E> i2) {
        this.i1 = (Iterator)ObjectUtil.checkNotNull(i1, (String)"i1");
        this.i2 = (Iterator)ObjectUtil.checkNotNull(i2, (String)"i2");
        this.currentIterator = i1;
    }

    @Override
    public boolean hasNext() {
        while (true) {
            if (this.currentIterator.hasNext()) {
                return true;
            }
            if (this.currentIterator != this.i1) break;
            this.currentIterator = this.i2;
        }
        return false;
    }

    @Override
    public E next() {
        while (true) {
            try {
                return this.currentIterator.next();
            }
            catch (NoSuchElementException e) {
                if (this.currentIterator == this.i1) {
                    this.currentIterator = this.i2;
                    continue;
                }
                throw e;
            }
            break;
        }
    }

    @Override
    public void remove() {
        this.currentIterator.remove();
    }
}

