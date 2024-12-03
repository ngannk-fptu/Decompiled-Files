/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.function;

@FunctionalInterface
public interface Resolver<I, O> {
    public O resolve(I var1);
}

