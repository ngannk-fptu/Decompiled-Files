/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.hazelcast.util;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.util.EmptyStatement;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

public final class ExceptionUtil {
    private static final String EXCEPTION_SEPARATOR = "------ submitted from ------";
    private static final String EXCEPTION_MESSAGE_SEPARATOR = "------ %MSG% ------";
    private static final ExceptionWrapper<RuntimeException> HAZELCAST_EXCEPTION_WRAPPER = new ExceptionWrapper<RuntimeException>(){

        @Override
        public RuntimeException create(Throwable throwable, String message) {
            if (message != null) {
                return new HazelcastException(message, throwable);
            }
            return new HazelcastException(throwable);
        }
    };

    private ExceptionUtil() {
    }

    public static String toString(Throwable cause) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cause.printStackTrace(pw);
        return sw.toString();
    }

    public static RuntimeException peel(Throwable t) {
        return (RuntimeException)ExceptionUtil.peel(t, null, null, HAZELCAST_EXCEPTION_WRAPPER);
    }

    public static <T extends Throwable> Throwable peel(Throwable t, Class<T> allowedType, String message) {
        return ExceptionUtil.peel(t, allowedType, message, HAZELCAST_EXCEPTION_WRAPPER);
    }

    public static <T, W extends Throwable> Throwable peel(Throwable t, Class<T> allowedType, String message, ExceptionWrapper<W> exceptionWrapper) {
        if (t instanceof RuntimeException) {
            return t;
        }
        if (t instanceof ExecutionException || t instanceof InvocationTargetException) {
            Throwable cause = t.getCause();
            if (cause != null) {
                return ExceptionUtil.peel(cause, allowedType, message, exceptionWrapper);
            }
            return exceptionWrapper.create(t, message);
        }
        if (allowedType != null && allowedType.isAssignableFrom(t.getClass())) {
            return t;
        }
        return exceptionWrapper.create(t, message);
    }

    public static RuntimeException rethrow(Throwable t) {
        ExceptionUtil.rethrowIfError(t);
        throw ExceptionUtil.peel(t);
    }

    public static RuntimeException rethrow(Throwable t, ExceptionWrapper<RuntimeException> exceptionWrapper) {
        ExceptionUtil.rethrowIfError(t);
        throw (RuntimeException)ExceptionUtil.peel(t, null, null, exceptionWrapper);
    }

    public static <T extends Throwable> RuntimeException rethrow(Throwable t, Class<T> allowedType) throws T {
        ExceptionUtil.rethrowIfError(t);
        throw ExceptionUtil.peel(t, allowedType, null);
    }

    public static <T extends Throwable> RuntimeException rethrowAllowedTypeFirst(Throwable t, Class<T> allowedType) throws T {
        ExceptionUtil.rethrowIfError(t);
        if (allowedType.isAssignableFrom(t.getClass())) {
            throw t;
        }
        throw ExceptionUtil.peel(t);
    }

    private static void rethrowIfError(Throwable t) {
        if (t instanceof Error) {
            if (t instanceof OutOfMemoryError) {
                OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)t);
            }
            throw (Error)t;
        }
    }

    public static RuntimeException rethrowAllowInterrupted(Throwable t) throws InterruptedException {
        return ExceptionUtil.rethrow(t, InterruptedException.class);
    }

    public static <T> T sneakyThrow(Throwable t) {
        ExceptionUtil.sneakyThrowInternal(t);
        return (T)t;
    }

    private static <T extends Throwable> void sneakyThrowInternal(Throwable t) throws T {
        throw t;
    }

    public static void fixAsyncStackTrace(Throwable asyncCause, StackTraceElement[] localSideStackTrace) {
        Throwable throwable = asyncCause;
        if (asyncCause instanceof ExecutionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        StackTraceElement[] remoteStackTrace = throwable.getStackTrace();
        StackTraceElement[] newStackTrace = new StackTraceElement[localSideStackTrace.length + remoteStackTrace.length];
        System.arraycopy(remoteStackTrace, 0, newStackTrace, 0, remoteStackTrace.length);
        newStackTrace[remoteStackTrace.length] = new StackTraceElement(EXCEPTION_SEPARATOR, "", null, -1);
        System.arraycopy(localSideStackTrace, 1, newStackTrace, remoteStackTrace.length + 1, localSideStackTrace.length - 1);
        throwable.setStackTrace(newStackTrace);
    }

    public static void fixAsyncStackTrace(Throwable asyncCause, StackTraceElement[] localSideStackTrace, String localExceptionMessage) {
        Throwable throwable = asyncCause;
        if (asyncCause instanceof ExecutionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        String msg = EXCEPTION_MESSAGE_SEPARATOR.replace("%MSG%", localExceptionMessage);
        StackTraceElement[] remoteStackTrace = throwable.getStackTrace();
        StackTraceElement[] newStackTrace = new StackTraceElement[localSideStackTrace.length + remoteStackTrace.length + 1];
        System.arraycopy(remoteStackTrace, 0, newStackTrace, 0, remoteStackTrace.length);
        newStackTrace[remoteStackTrace.length] = new StackTraceElement(EXCEPTION_SEPARATOR, "", null, -1);
        StackTraceElement nextElement = localSideStackTrace[1];
        newStackTrace[remoteStackTrace.length + 1] = new StackTraceElement(msg, nextElement.getMethodName(), nextElement.getFileName(), nextElement.getLineNumber());
        System.arraycopy(localSideStackTrace, 1, newStackTrace, remoteStackTrace.length + 2, localSideStackTrace.length - 1);
        throwable.setStackTrace(newStackTrace);
    }

    public static <T extends Throwable> T tryCreateExceptionWithMessageAndCause(Class<? extends Throwable> exceptionClass, String message, @Nullable Throwable cause) {
        try {
            Constructor<? extends Throwable> constructor = exceptionClass.getConstructor(String.class, Throwable.class);
            Throwable clone = constructor.newInstance(message, cause);
            return (T)clone;
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
            try {
                Constructor<? extends Throwable> constructor = exceptionClass.getConstructor(Throwable.class);
                Throwable clone = constructor.newInstance(cause);
                return (T)clone;
            }
            catch (Throwable ignored2) {
                EmptyStatement.ignore(ignored2);
                try {
                    Constructor<? extends Throwable> constructor = exceptionClass.getConstructor(String.class);
                    Throwable clone = constructor.newInstance(message);
                    clone.initCause(cause);
                    return (T)clone;
                }
                catch (Throwable ignored3) {
                    EmptyStatement.ignore(ignored3);
                    try {
                        Constructor<? extends Throwable> constructor = exceptionClass.getConstructor(new Class[0]);
                        Throwable clone = constructor.newInstance(new Object[0]);
                        clone.initCause(cause);
                        return (T)clone;
                    }
                    catch (Throwable ignored4) {
                        EmptyStatement.ignore(ignored4);
                        return null;
                    }
                }
            }
        }
    }

    public static interface ExceptionWrapper<T extends Throwable> {
        public T create(Throwable var1, String var2);
    }
}

