/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Context<T> {
    private T o;

    public void set(T o) {
        this.o = o;
    }

    public T get() {
        return this.o;
    }
}

