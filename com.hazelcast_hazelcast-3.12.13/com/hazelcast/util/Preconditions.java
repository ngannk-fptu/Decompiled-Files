/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Preconditions {
    private Preconditions() {
    }

    public static String checkHasText(String argument, String errorMessage) {
        if (argument == null || argument.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return argument;
    }

    public static <T> T checkNotNull(T argument, String errorMessage) {
        if (argument == null) {
            throw new NullPointerException(errorMessage);
        }
        return argument;
    }

    public static <T> Iterable<T> checkNoNullInside(Iterable<T> argument, String errorMessage) {
        if (argument == null) {
            return argument;
        }
        for (T element : argument) {
            Preconditions.checkNotNull(element, errorMessage);
        }
        return argument;
    }

    public static <T> T checkNotNull(T argument) {
        if (argument == null) {
            throw new NullPointerException();
        }
        return argument;
    }

    public static <E> E isNotNull(E argument, String argName) {
        if (argument == null) {
            throw new IllegalArgumentException(String.format("argument '%s' can't be null", argName));
        }
        return argument;
    }

    public static long checkNotNegative(long value, String errorMessage) {
        if (value < 0L) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static int checkNotNegative(int value, String errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static long checkNegative(long value, String errorMessage) {
        if (value >= 0L) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static long checkPositive(long value, String errorMessage) {
        if (value <= 0L) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static double checkPositive(double value, String errorMessage) {
        if (value <= 0.0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static int checkPositive(int value, String errorMessage) {
        if (value <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    public static int checkBackupCount(int newBackupCount, int currentAsyncBackupCount) {
        if (newBackupCount < 0) {
            throw new IllegalArgumentException("backup-count can't be smaller than 0");
        }
        if (currentAsyncBackupCount < 0) {
            throw new IllegalArgumentException("async-backup-count can't be smaller than 0");
        }
        if (newBackupCount > 6) {
            throw new IllegalArgumentException("backup-count can't be larger than than 6");
        }
        if (newBackupCount + currentAsyncBackupCount > 6) {
            throw new IllegalArgumentException("the sum of backup-count and async-backup-count can't be larger than than 6");
        }
        return newBackupCount;
    }

    public static int checkAsyncBackupCount(int currentBackupCount, int newAsyncBackupCount) {
        if (currentBackupCount < 0) {
            throw new IllegalArgumentException("backup-count can't be smaller than 0");
        }
        if (newAsyncBackupCount < 0) {
            throw new IllegalArgumentException("async-backup-count can't be smaller than 0");
        }
        if (newAsyncBackupCount > 6) {
            throw new IllegalArgumentException("async-backup-count can't be larger than than 6");
        }
        if (currentBackupCount + newAsyncBackupCount > 6) {
            throw new IllegalArgumentException("the sum of backup-count and async-backup-count can't be larger than than 6");
        }
        return newAsyncBackupCount;
    }

    public static <E> E checkInstanceOf(Class<E> type, Object object, String errorMessage) {
        Preconditions.isNotNull(type, "type");
        if (!type.isInstance(object)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return (E)object;
    }

    public static <E> E checkInstanceOf(Class<E> type, Object object) {
        Preconditions.isNotNull(type, "type");
        if (!type.isInstance(object)) {
            throw new IllegalArgumentException(object + " should be instanceof " + type.getName());
        }
        return (E)object;
    }

    public static <E> E checkNotInstanceOf(Class type, E object, String errorMessage) {
        Preconditions.isNotNull(type, "type");
        if (type.isInstance(object)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return object;
    }

    public static void checkFalse(boolean expression, String errorMessage) {
        if (expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkTrue(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static <T> Iterator<T> checkHasNext(Iterator<T> iterator, String message) throws NoSuchElementException {
        if (!iterator.hasNext()) {
            throw new NoSuchElementException(message);
        }
        return iterator;
    }

    public static void checkState(boolean condition, String message) throws IllegalStateException {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}

