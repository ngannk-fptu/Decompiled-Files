/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.streams.spi;

import com.google.common.base.Function;

@Deprecated
public interface Evictor<T>
extends Function<T, Void>,
java.util.function.Function<T, Void> {
}

