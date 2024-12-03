/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.IntSupplier
 */
package io.netty.channel;

import io.netty.util.IntSupplier;

public interface SelectStrategy {
    public static final int SELECT = -1;
    public static final int CONTINUE = -2;
    public static final int BUSY_WAIT = -3;

    public int calculateStrategy(IntSupplier var1, boolean var2) throws Exception;
}

