/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.util.Comparator;
import org.jheaps.AddressableHeap;
import org.jheaps.AddressableHeapFactory;
import org.jheaps.tree.FibonacciHeap;
import org.jheaps.tree.ReflectedHeap;

public class ReflectedFibonacciHeap<K, V>
extends ReflectedHeap<K, V> {
    private static final long serialVersionUID = 651281438828109106L;

    public ReflectedFibonacciHeap() {
        this((Comparator<K>)null);
    }

    public ReflectedFibonacciHeap(Comparator<? super K> comparator) {
        super(new Factory(), comparator);
    }

    private static class Factory<K, V>
    implements AddressableHeapFactory<K, V> {
        private Factory() {
        }

        @Override
        public AddressableHeap<K, V> get(Comparator<? super K> comparator) {
            return new FibonacciHeap(comparator);
        }
    }
}

