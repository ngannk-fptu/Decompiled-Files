/*
 * Decompiled with CFR 0.152.
 */
package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Pair<A, B> {
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public final A getA() {
        return this.a;
    }

    public final B getB() {
        return this.b;
    }
}

