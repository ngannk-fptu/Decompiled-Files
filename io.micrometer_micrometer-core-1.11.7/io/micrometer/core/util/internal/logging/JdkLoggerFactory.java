/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.util.internal.logging;

import io.micrometer.core.util.internal.logging.InternalLogger;
import io.micrometer.core.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.util.internal.logging.JdkLogger;
import java.util.logging.Logger;

@Deprecated
public class JdkLoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new JdkLoggerFactory();

    private JdkLoggerFactory() {
    }

    @Override
    public InternalLogger newInstance(String name) {
        return new JdkLogger(Logger.getLogger(name));
    }
}

