/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.prettyurls.internal.util;

import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;

public class LogUtils {
    private LogUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void logExceptionEvent(Logger logger, Throwable exception, String message) {
        Optional<Logger> optionalLogger = Optional.ofNullable(logger);
        optionalLogger.ifPresent(log -> log.error(String.valueOf(message).concat(" ").concat(String.valueOf(exception))));
        optionalLogger.filter(Objects::nonNull).filter(Logger::isDebugEnabled).ifPresent(log -> log.debug(String.valueOf(message), exception));
    }
}

