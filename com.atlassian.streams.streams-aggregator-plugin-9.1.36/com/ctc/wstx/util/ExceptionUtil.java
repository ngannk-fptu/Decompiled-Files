/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

public final class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static void throwRuntimeException(Throwable t) {
        ExceptionUtil.throwIfUnchecked(t);
        RuntimeException rex = new RuntimeException("[was " + t.getClass() + "] " + t.getMessage());
        ExceptionUtil.setInitCause(rex, t);
        throw rex;
    }

    public static void throwAsIllegalArgument(Throwable t) {
        ExceptionUtil.throwIfUnchecked(t);
        IllegalArgumentException rex = new IllegalArgumentException("[was " + t.getClass() + "] " + t.getMessage());
        ExceptionUtil.setInitCause(rex, t);
        throw rex;
    }

    public static void throwIfUnchecked(Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
    }

    public static void throwGenericInternal() {
        ExceptionUtil.throwInternal(null);
    }

    public static void throwInternal(String msg) {
        if (msg == null) {
            msg = "[no description]";
        }
        throw new RuntimeException("Internal error: " + msg);
    }

    public static void setInitCause(Throwable newT, Throwable rootT) {
        if (newT.getCause() == null) {
            newT.initCause(rootT);
        }
    }
}

