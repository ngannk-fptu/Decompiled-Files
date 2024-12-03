/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.util.StringUtils;

public class DateTimeFormatAnnotationFormatterFactory
extends EmbeddedValueResolutionSupport
implements AnnotationFormatterFactory<DateTimeFormat> {
    private static final Set<Class<?>> FIELD_TYPES;

    @Override
    public Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    @Override
    public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
        return this.getFormatter(annotation, fieldType);
    }

    @Override
    public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
        return this.getFormatter(annotation, fieldType);
    }

    protected Formatter<Date> getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        String pattern;
        DateFormatter formatter = new DateFormatter();
        formatter.setSource(annotation);
        formatter.setIso(annotation.iso());
        String style = this.resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            formatter.setStylePattern(style);
        }
        if (StringUtils.hasLength(pattern = this.resolveEmbeddedValue(annotation.pattern()))) {
            formatter.setPattern(pattern);
        }
        ArrayList<String> resolvedFallbackPatterns = new ArrayList<String>();
        for (String fallbackPattern : annotation.fallbackPatterns()) {
            String resolvedFallbackPattern = this.resolveEmbeddedValue(fallbackPattern);
            if (!StringUtils.hasLength(resolvedFallbackPattern)) continue;
            resolvedFallbackPatterns.add(resolvedFallbackPattern);
        }
        if (!resolvedFallbackPatterns.isEmpty()) {
            formatter.setFallbackPatterns(resolvedFallbackPatterns.toArray(new String[0]));
        }
        return formatter;
    }

    static {
        HashSet<Class<Long>> fieldTypes = new HashSet<Class<Long>>(4);
        fieldTypes.add(Date.class);
        fieldTypes.add(Calendar.class);
        fieldTypes.add(Long.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }
}

