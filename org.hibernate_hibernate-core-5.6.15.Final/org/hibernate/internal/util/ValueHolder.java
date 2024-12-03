/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

public class ValueHolder<T> {
    private final DeferredInitializer<T> valueInitializer;
    private T value;
    private static final DeferredInitializer NO_DEFERRED_INITIALIZER = new DeferredInitializer(){

        public Void initialize() {
            return null;
        }
    };

    public ValueHolder(DeferredInitializer<T> valueInitializer) {
        this.valueInitializer = valueInitializer;
    }

    public ValueHolder(T value) {
        this(NO_DEFERRED_INITIALIZER);
        this.value = value;
    }

    public T getValue() {
        if (this.value == null) {
            this.value = this.valueInitializer.initialize();
        }
        return this.value;
    }

    public static interface DeferredInitializer<T> {
        public T initialize();
    }
}

