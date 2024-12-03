/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

public interface Ordered {
    public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    public int getOrder();
}

