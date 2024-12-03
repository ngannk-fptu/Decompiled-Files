/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.jose.util;

import net.jcip.annotations.Immutable;

@Immutable
public class Pair<L, R> {
    private final L left;
    private final R right;

    protected Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }
}

