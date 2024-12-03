/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Supplier
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Functions;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

public interface ExceptionPolicy {
    public <T> Function<Supplier<T>, Supplier<T>> handler();

    public static enum Policies implements ExceptionPolicy
    {
        IGNORE_EXCEPTIONS{

            @Override
            public <T> Function<Supplier<T>, Supplier<T>> handler() {
                return Functions.ignoreExceptions();
            }
        }
        ,
        THROW{

            @Override
            public <T> Function<Supplier<T>, Supplier<T>> handler() {
                return com.google.common.base.Functions.identity();
            }
        };

    }
}

