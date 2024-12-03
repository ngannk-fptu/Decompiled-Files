/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import java.lang.reflect.InvocationTargetException;

class Exceptions {
    Exceptions() {
    }

    public static RuntimeException throwCleanly(InvocationTargetException exception) {
        Throwable cause = exception;
        if (((Throwable)cause).getCause() != null) {
            cause = ((Throwable)cause).getCause();
        }
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new UnhandledCheckedUserException(cause);
    }

    static class UnhandledCheckedUserException
    extends RuntimeException {
        public UnhandledCheckedUserException(Throwable cause) {
            super(cause);
        }
    }
}

