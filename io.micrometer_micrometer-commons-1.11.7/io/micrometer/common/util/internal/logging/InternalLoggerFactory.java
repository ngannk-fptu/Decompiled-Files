/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common.util.internal.logging;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.JdkLoggerFactory;
import io.micrometer.common.util.internal.logging.Slf4JLoggerFactory;
import java.util.Objects;

public abstract class InternalLoggerFactory {
    private static volatile InternalLoggerFactory defaultFactory;

    private static InternalLoggerFactory newDefaultFactory(String name) {
        InternalLoggerFactory f;
        try {
            f = Slf4JLoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
        }
        catch (Throwable ignored) {
            f = JdkLoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using java.util.logging as the default logging framework");
        }
        return f;
    }

    public static InternalLoggerFactory getDefaultFactory() {
        if (defaultFactory == null) {
            defaultFactory = InternalLoggerFactory.newDefaultFactory(InternalLoggerFactory.class.getName());
        }
        return defaultFactory;
    }

    public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
        Objects.requireNonNull(defaultFactory, "defaultFactory");
        InternalLoggerFactory.defaultFactory = defaultFactory;
    }

    public static InternalLogger getInstance(Class<?> clazz) {
        return InternalLoggerFactory.getInstance(clazz.getName());
    }

    public static InternalLogger getInstance(String name) {
        return InternalLoggerFactory.getDefaultFactory().newInstance(name);
    }

    protected abstract InternalLogger newInstance(String var1);
}

