/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class SelectorOptimizer {
    static final String SELECTOR_IMPL = "sun.nio.ch.SelectorImpl";

    private SelectorOptimizer() {
    }

    static Selector newSelector(ILogger logger) {
        Selector selector;
        Preconditions.checkNotNull(logger, "logger");
        try {
            selector = Selector.open();
        }
        catch (IOException e) {
            throw new HazelcastException("Failed to open a Selector", e);
        }
        boolean optimize = Boolean.parseBoolean(System.getProperty("hazelcast.io.optimizeselector", "true"));
        if (optimize) {
            SelectorOptimizer.optimize(selector, logger);
        }
        return selector;
    }

    static SelectionKeysSet optimize(Selector selector, ILogger logger) {
        Preconditions.checkNotNull(selector, "selector");
        Preconditions.checkNotNull(logger, "logger");
        try {
            SelectionKeysSet set = new SelectionKeysSet();
            Class<?> selectorImplClass = SelectorOptimizer.findOptimizableSelectorClass(selector);
            if (selectorImplClass == null) {
                return null;
            }
            Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
            selectedKeysField.setAccessible(true);
            Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
            publicSelectedKeysField.setAccessible(true);
            selectedKeysField.set(selector, set);
            publicSelectedKeysField.set(selector, set);
            logger.finest("Optimized Selector: " + selector.getClass().getName());
            return set;
        }
        catch (Throwable t) {
            logger.finest("Failed to optimize Selector: " + selector.getClass().getName(), t);
            return null;
        }
    }

    static Class<?> findOptimizableSelectorClass(Selector selector) throws ClassNotFoundException {
        Class<?> selectorImplClass = Class.forName(SELECTOR_IMPL, false, SelectorOptimizer.class.getClassLoader());
        if (!selectorImplClass.isAssignableFrom(selector.getClass())) {
            return null;
        }
        return selectorImplClass;
    }

    static final class IteratorImpl
    implements Iterator<SelectionKey> {
        SelectionKey[] keys;
        int index;

        IteratorImpl() {
        }

        private void init(SelectionKey[] keys) {
            this.keys = keys;
            this.index = -1;
        }

        @Override
        public boolean hasNext() {
            if (this.index >= this.keys.length - 1) {
                return false;
            }
            return this.keys[this.index + 1] != null;
        }

        @Override
        public SelectionKey next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            ++this.index;
            return this.keys[this.index];
        }

        @Override
        public void remove() {
            if (this.index == -1 || this.index >= this.keys.length || this.keys[this.index] == null) {
                throw new IllegalStateException();
            }
            this.keys[this.index] = null;
        }
    }

    static final class SelectionKeys {
        static final int INITIAL_CAPACITY = 32;
        SelectionKey[] keys = new SelectionKey[32];
        int size;

        SelectionKeys() {
        }

        private boolean add(SelectionKey key) {
            if (key == null) {
                return false;
            }
            this.ensureCapacity();
            this.keys[this.size] = key;
            ++this.size;
            return true;
        }

        private void ensureCapacity() {
            if (this.size < this.keys.length) {
                return;
            }
            SelectionKey[] newKeys = new SelectionKey[this.keys.length * 2];
            System.arraycopy(this.keys, 0, newKeys, 0, this.size);
            this.keys = newKeys;
        }
    }

    static class SelectionKeysSet
    extends AbstractSet<SelectionKey> {
        SelectionKeys activeKeys = new SelectionKeys();
        SelectionKeys passiveKeys = new SelectionKeys();
        private final IteratorImpl iterator = new IteratorImpl();

        SelectionKeysSet() {
        }

        @Override
        public boolean add(SelectionKey o) {
            return this.activeKeys.add(o);
        }

        @Override
        public int size() {
            return this.activeKeys.size;
        }

        @Override
        public Iterator<SelectionKey> iterator() {
            this.iterator.init(this.flip());
            return this.iterator;
        }

        private SelectionKey[] flip() {
            SelectionKeys tmp = this.activeKeys;
            this.activeKeys = this.passiveKeys;
            this.passiveKeys = tmp;
            this.activeKeys.size = 0;
            return this.passiveKeys.keys;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }
    }
}

