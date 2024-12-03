/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 */
package com.atlassian.ozymandias.error;

import com.atlassian.ozymandias.error.ThrowableLogger;
import javax.annotation.Nonnull;
import org.slf4j.Logger;

public final class ErrorUtils {
    public static final String LINKAGE_ERROR_MESSAGE = "A LinkageError indicates that plugin code was compiled with outdated versions.";

    public static void handleThrowable(@Nonnull Throwable throwable, @Nonnull String errorMessage, @Nonnull Logger log) {
        if (throwable instanceof Error) {
            ErrorUtils.handleError((Error)throwable, errorMessage, log);
        } else {
            ThrowableLogger.logThrowable(errorMessage, throwable, log);
        }
    }

    private static void handleError(Error error, String errorMessage, Logger log) {
        if (!(error instanceof LinkageError)) {
            throw error;
        }
        ThrowableLogger.logThrowable("A LinkageError indicates that plugin code was compiled with outdated versions. " + errorMessage, error, log);
    }

    private ErrorUtils() {
    }
}

