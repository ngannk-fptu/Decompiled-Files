/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.confluence.impl.velocity;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.slf4j.Logger;

interface Slf4jLogSink {
    public void log(String var1);

    public void log(String var1, Throwable var2);

    public boolean isEnabled();

    public static Map<Integer, Slf4jLogSink> sinks(Logger log) {
        return Map.of(2, Slf4jLogSink.of(arg_0 -> ((Logger)log).warn(arg_0), (arg_0, arg_1) -> ((Logger)log).warn(arg_0, arg_1), () -> ((Logger)log).isWarnEnabled()), 1, Slf4jLogSink.of(arg_0 -> ((Logger)log).info(arg_0), (arg_0, arg_1) -> ((Logger)log).info(arg_0, arg_1), () -> ((Logger)log).isInfoEnabled()), -1, Slf4jLogSink.of(arg_0 -> ((Logger)log).trace(arg_0), (arg_0, arg_1) -> ((Logger)log).trace(arg_0, arg_1), () -> ((Logger)log).isTraceEnabled()), 3, Slf4jLogSink.of(arg_0 -> ((Logger)log).error(arg_0), (arg_0, arg_1) -> ((Logger)log).error(arg_0, arg_1), () -> ((Logger)log).isErrorEnabled()), 0, Slf4jLogSink.of(arg_0 -> ((Logger)log).debug(arg_0), (arg_0, arg_1) -> ((Logger)log).debug(arg_0, arg_1), () -> ((Logger)log).isDebugEnabled()));
    }

    private static Slf4jLogSink of(final Consumer<String> log, final BiConsumer<String, Throwable> logWithThrowable, final BooleanSupplier isEnabled) {
        return new Slf4jLogSink(){

            @Override
            public void log(String message) {
                log.accept(message);
            }

            @Override
            public void log(String message, Throwable th) {
                logWithThrowable.accept(message, th);
            }

            @Override
            public boolean isEnabled() {
                return isEnabled.getAsBoolean();
            }
        };
    }
}

