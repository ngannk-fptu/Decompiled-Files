/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.util.HashUtil;

class LockStripe {
    private static final int STRIPE_LENGTH = 20;
    private final Object[] stripe = new Object[20];

    LockStripe() {
        for (int k = 0; k < this.stripe.length; ++k) {
            this.stripe[k] = new Object();
        }
    }

    Object getLock(Object source) {
        int hash = System.identityHashCode(source);
        return this.stripe[HashUtil.hashToIndex(hash, this.stripe.length)];
    }
}

