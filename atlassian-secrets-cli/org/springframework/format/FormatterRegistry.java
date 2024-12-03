/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format;

import java.lang.annotation.Annotation;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

public interface FormatterRegistry
extends ConverterRegistry {
    public void addFormatter(Formatter<?> var1);

    public void addFormatterForFieldType(Class<?> var1, Formatter<?> var2);

    public void addFormatterForFieldType(Class<?> var1, Printer<?> var2, Parser<?> var3);

    public void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> var1);
}

