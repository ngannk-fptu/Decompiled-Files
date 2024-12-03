/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

public interface AnnotationFormatterFactory<A extends Annotation> {
    public Set<Class<?>> getFieldTypes();

    public Printer<?> getPrinter(A var1, Class<?> var2);

    public Parser<?> getParser(A var1, Class<?> var2);
}

