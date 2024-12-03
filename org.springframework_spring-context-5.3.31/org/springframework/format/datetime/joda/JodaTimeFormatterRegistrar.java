/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 *  org.joda.time.LocalDate
 *  org.joda.time.LocalDateTime
 *  org.joda.time.LocalTime
 *  org.joda.time.MonthDay
 *  org.joda.time.Period
 *  org.joda.time.ReadableInstant
 *  org.joda.time.YearMonth
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.format.datetime.joda.DateTimeParser;
import org.springframework.format.datetime.joda.DurationFormatter;
import org.springframework.format.datetime.joda.JodaDateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.datetime.joda.JodaTimeConverters;
import org.springframework.format.datetime.joda.LocalDateParser;
import org.springframework.format.datetime.joda.LocalDateTimeParser;
import org.springframework.format.datetime.joda.LocalTimeParser;
import org.springframework.format.datetime.joda.MonthDayFormatter;
import org.springframework.format.datetime.joda.PeriodFormatter;
import org.springframework.format.datetime.joda.ReadableInstantPrinter;
import org.springframework.format.datetime.joda.ReadablePartialPrinter;
import org.springframework.format.datetime.joda.YearMonthFormatter;

@Deprecated
public class JodaTimeFormatterRegistrar
implements FormatterRegistrar {
    private final Map<Type, DateTimeFormatter> formatters = new EnumMap<Type, DateTimeFormatter>(Type.class);
    private final Map<Type, DateTimeFormatterFactory> factories = new EnumMap<Type, DateTimeFormatterFactory>(Type.class);

    public JodaTimeFormatterRegistrar() {
        for (Type type : Type.values()) {
            this.factories.put(type, new DateTimeFormatterFactory());
        }
    }

    public void setUseIsoFormat(boolean useIsoFormat) {
        this.factories.get((Object)Type.DATE).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE : DateTimeFormat.ISO.NONE);
        this.factories.get((Object)Type.TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.TIME : DateTimeFormat.ISO.NONE);
        this.factories.get((Object)Type.DATE_TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE_TIME : DateTimeFormat.ISO.NONE);
    }

    public void setDateStyle(String dateStyle) {
        this.factories.get((Object)Type.DATE).setStyle(dateStyle + "-");
    }

    public void setTimeStyle(String timeStyle) {
        this.factories.get((Object)Type.TIME).setStyle("-" + timeStyle);
    }

    public void setDateTimeStyle(String dateTimeStyle) {
        this.factories.get((Object)Type.DATE_TIME).setStyle(dateTimeStyle);
    }

    public void setDateFormatter(DateTimeFormatter formatter) {
        this.formatters.put(Type.DATE, formatter);
    }

    public void setTimeFormatter(DateTimeFormatter formatter) {
        this.formatters.put(Type.TIME, formatter);
    }

    public void setDateTimeFormatter(DateTimeFormatter formatter) {
        this.formatters.put(Type.DATE_TIME, formatter);
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        JodaTimeConverters.registerConverters(registry);
        DateTimeFormatter dateFormatter = this.getFormatter(Type.DATE);
        DateTimeFormatter timeFormatter = this.getFormatter(Type.TIME);
        DateTimeFormatter dateTimeFormatter = this.getFormatter(Type.DATE_TIME);
        this.addFormatterForFields(registry, new ReadablePartialPrinter(dateFormatter), new LocalDateParser(dateFormatter), LocalDate.class);
        this.addFormatterForFields(registry, new ReadablePartialPrinter(timeFormatter), new LocalTimeParser(timeFormatter), LocalTime.class);
        this.addFormatterForFields(registry, new ReadablePartialPrinter(dateTimeFormatter), new LocalDateTimeParser(dateTimeFormatter), LocalDateTime.class);
        this.addFormatterForFields(registry, new ReadableInstantPrinter(dateTimeFormatter), new DateTimeParser(dateTimeFormatter), ReadableInstant.class);
        if (this.formatters.containsKey((Object)Type.DATE_TIME)) {
            this.addFormatterForFields(registry, new ReadableInstantPrinter(dateTimeFormatter), new DateTimeParser(dateTimeFormatter), Date.class, Calendar.class);
        }
        registry.addFormatterForFieldType(Period.class, new PeriodFormatter());
        registry.addFormatterForFieldType(Duration.class, new DurationFormatter());
        registry.addFormatterForFieldType(YearMonth.class, new YearMonthFormatter());
        registry.addFormatterForFieldType(MonthDay.class, new MonthDayFormatter());
        registry.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
    }

    private DateTimeFormatter getFormatter(Type type) {
        DateTimeFormatter formatter = this.formatters.get((Object)type);
        if (formatter != null) {
            return formatter;
        }
        DateTimeFormatter fallbackFormatter = this.getFallbackFormatter(type);
        return this.factories.get((Object)type).createDateTimeFormatter(fallbackFormatter);
    }

    private DateTimeFormatter getFallbackFormatter(Type type) {
        switch (type) {
            case DATE: {
                return DateTimeFormat.shortDate();
            }
            case TIME: {
                return DateTimeFormat.shortTime();
            }
        }
        return DateTimeFormat.shortDateTime();
    }

    private void addFormatterForFields(FormatterRegistry registry, Printer<?> printer, Parser<?> parser, Class<?> ... fieldTypes) {
        for (Class<?> fieldType : fieldTypes) {
            registry.addFormatterForFieldType(fieldType, printer, parser);
        }
    }

    private static enum Type {
        DATE,
        TIME,
        DATE_TIME;

    }
}

