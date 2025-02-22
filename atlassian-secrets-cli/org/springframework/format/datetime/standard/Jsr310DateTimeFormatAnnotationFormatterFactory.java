/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.format.datetime.standard.TemporalAccessorParser;
import org.springframework.format.datetime.standard.TemporalAccessorPrinter;
import org.springframework.util.StringUtils;

public class Jsr310DateTimeFormatAnnotationFormatterFactory
extends EmbeddedValueResolutionSupport
implements AnnotationFormatterFactory<DateTimeFormat> {
    private static final Set<Class<?>> FIELD_TYPES;

    @Override
    public final Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    @Override
    public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = this.getFormatter(annotation, fieldType);
        if (formatter == DateTimeFormatter.ISO_DATE) {
            if (this.isLocal(fieldType)) {
                formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            }
        } else if (formatter == DateTimeFormatter.ISO_TIME) {
            if (this.isLocal(fieldType)) {
                formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            }
        } else if (formatter == DateTimeFormatter.ISO_DATE_TIME && this.isLocal(fieldType)) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        return new TemporalAccessorPrinter(formatter);
    }

    @Override
    public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = this.getFormatter(annotation, fieldType);
        return new TemporalAccessorParser(fieldType, formatter);
    }

    protected DateTimeFormatter getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        String style = this.resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            factory.setStylePattern(style);
        }
        factory.setIso(annotation.iso());
        String pattern = this.resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            factory.setPattern(pattern);
        }
        return factory.createDateTimeFormatter();
    }

    private boolean isLocal(Class<?> fieldType) {
        return fieldType.getSimpleName().startsWith("Local");
    }

    static {
        HashSet<Class> fieldTypes = new HashSet<Class>(8);
        fieldTypes.add(LocalDate.class);
        fieldTypes.add(LocalTime.class);
        fieldTypes.add(LocalDateTime.class);
        fieldTypes.add(ZonedDateTime.class);
        fieldTypes.add(OffsetDateTime.class);
        fieldTypes.add(OffsetTime.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }
}

