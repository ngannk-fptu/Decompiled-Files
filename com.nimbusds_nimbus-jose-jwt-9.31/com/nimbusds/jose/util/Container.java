/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.NotThreadSafe
 */
package com.nimbusds.jose.util;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Container<T> {
    private T item;

    public Container() {
    }

    public Container(T item) {
        this.item = item;
    }

    public T get() {
        return this.item;
    }

    public void set(T item) {
        this.item = item;
    }
}

