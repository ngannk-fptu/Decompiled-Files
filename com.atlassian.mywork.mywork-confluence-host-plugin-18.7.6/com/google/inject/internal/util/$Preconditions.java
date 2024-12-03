/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Preconditions {
    private $Preconditions() {
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object ... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException($Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static void checkState(boolean expression, String errorMessageTemplate, Object ... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException($Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object ... errorMessageArgs) {
        if (reference == null) {
            throw new NullPointerException($Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static <T extends Iterable<?>> T checkContentsNotNull(T iterable) {
        if ($Preconditions.containsOrIsNull(iterable)) {
            throw new NullPointerException();
        }
        return iterable;
    }

    public static <T extends Iterable<?>> T checkContentsNotNull(T iterable, Object errorMessage) {
        if ($Preconditions.containsOrIsNull(iterable)) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return iterable;
    }

    public static <T extends Iterable<?>> T checkContentsNotNull(T iterable, String errorMessageTemplate, Object ... errorMessageArgs) {
        if ($Preconditions.containsOrIsNull(iterable)) {
            throw new NullPointerException($Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
        return iterable;
    }

    private static boolean containsOrIsNull(Iterable<?> iterable) {
        if (iterable == null) {
            return true;
        }
        if (iterable instanceof Collection) {
            Collection collection = (Collection)iterable;
            try {
                return collection.contains(null);
            }
            catch (NullPointerException e) {
                return false;
            }
        }
        for (Object element : iterable) {
            if (element != null) continue;
            return true;
        }
        return false;
    }

    public static void checkElementIndex(int index, int size) {
        $Preconditions.checkElementIndex(index, size, "index");
    }

    public static void checkElementIndex(int index, int size, String desc) {
        $Preconditions.checkArgument(size >= 0, "negative size: %s", size);
        if (index < 0) {
            throw new IndexOutOfBoundsException($Preconditions.format("%s (%s) must not be negative", desc, index));
        }
        if (index >= size) {
            throw new IndexOutOfBoundsException($Preconditions.format("%s (%s) must be less than size (%s)", desc, index, size));
        }
    }

    public static void checkPositionIndex(int index, int size) {
        $Preconditions.checkPositionIndex(index, size, "index");
    }

    public static void checkPositionIndex(int index, int size, String desc) {
        $Preconditions.checkArgument(size >= 0, "negative size: %s", size);
        if (index < 0) {
            throw new IndexOutOfBoundsException($Preconditions.format("%s (%s) must not be negative", desc, index));
        }
        if (index > size) {
            throw new IndexOutOfBoundsException($Preconditions.format("%s (%s) must not be greater than size (%s)", desc, index, size));
        }
    }

    public static void checkPositionIndexes(int start, int end, int size) {
        $Preconditions.checkPositionIndex(start, size, "start index");
        $Preconditions.checkPositionIndex(end, size, "end index");
        if (end < start) {
            throw new IndexOutOfBoundsException($Preconditions.format("end index (%s) must not be less than start index (%s)", end, start));
        }
    }

    static String format(String template, Object ... args) {
        int placeholderStart;
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length && (placeholderStart = template.indexOf("%s", templateStart)) != -1) {
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append("]");
        }
        return builder.toString();
    }
}

