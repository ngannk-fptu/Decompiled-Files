/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

class Exceptions {
    Exceptions() {
    }

    public static RuntimeException rethrowCause(Throwable throwable) {
        Throwable cause = throwable;
        if (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return Exceptions.rethrow(cause);
    }

    public static RuntimeException rethrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        throw new UnhandledCheckedUserException(throwable);
    }

    static class UnhandledCheckedUserException
    extends RuntimeException {
        public UnhandledCheckedUserException(Throwable cause) {
            super(cause);
        }
    }
}

