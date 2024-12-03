/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.logging.formatter;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ArrayOfClassesObjectFormatter {
    private final String stringRepresentation;

    public ArrayOfClassesObjectFormatter(Class<?>[] classes) {
        this.stringRepresentation = Arrays.stream(classes).map(c -> c.getName()).collect(Collectors.joining(", "));
    }

    public String toString() {
        return this.stringRepresentation;
    }
}

