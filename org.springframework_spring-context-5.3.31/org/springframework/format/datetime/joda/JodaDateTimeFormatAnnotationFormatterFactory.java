/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.LocalDate
 *  org.joda.time.LocalDateTime
 *  org.joda.time.LocalTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 *  org.joda.time.format.DateTimeFormatter
 *  org.springframework.util.StringUtils
 */
package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.format.datetime.joda.DateTimeParser;
import org.springframework.format.datetime.joda.LocalDateParser;
import org.springframework.format.datetime.joda.LocalDateTimeParser;
import org.springframework.format.datetime.joda.LocalTimeParser;
import org.springframework.format.datetime.joda.MillisecondInstantPrinter;
import org.springframework.format.datetime.joda.ReadableInstantPrinter;
import org.springframework.format.datetime.joda.ReadablePartialPrinter;
import org.springframework.util.StringUtils;

@Deprecated
public class JodaDateTimeFormatAnnotationFormatterFactory
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
        if (ReadablePartial.class.isAssignableFrom(fieldType)) {
            return new ReadablePartialPrinter(formatter);
        }
        if (ReadableInstant.class.isAssignableFrom(fieldType) || Calendar.class.isAssignableFrom(fieldType)) {
            return new ReadableInstantPrinter(formatter);
        }
        return new MillisecondInstantPrinter(formatter);
    }

    @Override
    public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
        if (LocalDate.class == fieldType) {
            return new LocalDateParser(this.getFormatter(annotation, fieldType));
        }
        if (LocalTime.class == fieldType) {
            return new LocalTimeParser(this.getFormatter(annotation, fieldType));
        }
        if (LocalDateTime.class == fieldType) {
            return new LocalDateTimeParser(this.getFormatter(annotation, fieldType));
        }
        return new DateTimeParser(this.getFormatter(annotation, fieldType));
    }

    protected DateTimeFormatter getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        String style = this.resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength((String)style)) {
            factory.setStyle(style);
        }
        factory.setIso(annotation.iso());
        String pattern = this.resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength((String)pattern)) {
            factory.setPattern(pattern);
        }
        return factory.createDateTimeFormatter();
    }

    static {
        HashSet<Class<Long>> fieldTypes = new HashSet<Class<Long>>(8);
        fieldTypes.add(ReadableInstant.class);
        fieldTypes.add(LocalDate.class);
        fieldTypes.add(LocalTime.class);
        fieldTypes.add(LocalDateTime.class);
        fieldTypes.add(Date.class);
        fieldTypes.add(Calendar.class);
        fieldTypes.add(Long.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }
}

