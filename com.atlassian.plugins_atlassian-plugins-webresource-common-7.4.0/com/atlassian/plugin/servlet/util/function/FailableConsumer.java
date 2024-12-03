/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.servlet.util.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface FailableConsumer<T, E extends Exception> {
    public void accept(T var1) throws E;

    public static <T, E extends Exception> Consumer<T> wrapper(FailableConsumer<T, E> consumer) {
        return argument -> {
            try {
                consumer.accept(argument);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException(exception);
            }
        };
    }
}

