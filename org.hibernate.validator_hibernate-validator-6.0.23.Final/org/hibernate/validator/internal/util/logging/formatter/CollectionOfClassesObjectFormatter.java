/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.logging.formatter;

import java.util.Collection;
import java.util.stream.Collectors;

public class CollectionOfClassesObjectFormatter {
    private final String stringRepresentation;

    public CollectionOfClassesObjectFormatter(Collection<? extends Class<?>> classes) {
        this.stringRepresentation = classes.stream().map(c -> c.getName()).collect(Collectors.joining(", "));
    }

    public String toString() {
        return this.stringRepresentation;
    }
}

