/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

public class MutableInteger {
    public int value;

    public int getAndInc() {
        return this.value++;
    }
}

