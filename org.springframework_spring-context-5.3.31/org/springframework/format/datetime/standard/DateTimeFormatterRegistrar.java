/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.DateTimeConverters;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.format.datetime.standard.DurationFormatter;
import org.springframework.format.datetime.standard.InstantFormatter;
import org.springframework.format.datetime.standard.Jsr310DateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.datetime.standard.MonthDayFormatter;
import org.springframework.format.datetime.standard.MonthFormatter;
import org.springframework.format.datetime.standard.PeriodFormatter;
import org.springframework.format.datetime.standard.TemporalAccessorParser;
import org.springframework.format.datetime.standard.TemporalAccessorPrinter;
import org.springframework.format.datetime.standard.YearFormatter;
import org.springframework.format.datetime.standard.YearMonthFormatter;

public class DateTimeFormatterRegistrar
implements FormatterRegistrar {
    private final Map<Type, DateTimeFormatter> formatters = new EnumMap<Type, DateTimeFormatter>(Type.class);
    private final Map<Type, DateTimeFormatterFactory> factories = new EnumMap<Type, DateTimeFormatterFactory>(Type.class);

    public DateTimeFormatterRegistrar() {
        for (Type type : Type.values()) {
            this.factories.put(type, new DateTimeFormatterFactory());
        }
    }

    public void setUseIsoFormat(boolean useIsoFormat) {
        this.factories.get((Object)Type.DATE).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE : DateTimeFormat.ISO.NONE);
        this.factories.get((Object)Type.TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.TIME : DateTimeFormat.ISO.NONE);
        this.factories.get((Object)Type.DATE_TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE_TIME : DateTimeFormat.ISO.NONE);
    }

    public void setDateStyle(FormatStyle dateStyle) {
        this.factories.get((Object)Type.DATE).setDateStyle(dateStyle);
    }

    public void setTimeStyle(FormatStyle timeStyle) {
        this.factories.get((Object)Type.TIME).setTimeStyle(timeStyle);
    }

    public void setDateTimeStyle(FormatStyle dateTimeStyle) {
        this.factories.get((Object)Type.DATE_TIME).setDateTimeStyle(dateTimeStyle);
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
        DateTimeConverters.registerConverters(registry);
        DateTimeFormatter df = this.getFormatter(Type.DATE);
        DateTimeFormatter tf = this.getFormatter(Type.TIME);
        DateTimeFormatter dtf = this.getFormatter(Type.DATE_TIME);
        registry.addFormatterForFieldType(LocalDate.class, new TemporalAccessorPrinter(df == DateTimeFormatter.ISO_DATE ? DateTimeFormatter.ISO_LOCAL_DATE : df), new TemporalAccessorParser(LocalDate.class, df));
        registry.addFormatterForFieldType(LocalTime.class, new TemporalAccessorPrinter(tf == DateTimeFormatter.ISO_TIME ? DateTimeFormatter.ISO_LOCAL_TIME : tf), new TemporalAccessorParser(LocalTime.class, tf));
        registry.addFormatterForFieldType(LocalDateTime.class, new TemporalAccessorPrinter(dtf == DateTimeFormatter.ISO_DATE_TIME ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : dtf), new TemporalAccessorParser(LocalDateTime.class, dtf));
        registry.addFormatterForFieldType(ZonedDateTime.class, new TemporalAccessorPrinter(dtf), new TemporalAccessorParser(ZonedDateTime.class, dtf));
        registry.addFormatterForFieldType(OffsetDateTime.class, new TemporalAccessorPrinter(dtf), new TemporalAccessorParser(OffsetDateTime.class, dtf));
        registry.addFormatterForFieldType(OffsetTime.class, new TemporalAccessorPrinter(tf), new TemporalAccessorParser(OffsetTime.class, tf));
        registry.addFormatterForFieldType(Instant.class, new InstantFormatter());
        registry.addFormatterForFieldType(Period.class, new PeriodFormatter());
        registry.addFormatterForFieldType(Duration.class, new DurationFormatter());
        registry.addFormatterForFieldType(Year.class, new YearFormatter());
        registry.addFormatterForFieldType(Month.class, new MonthFormatter());
        registry.addFormatterForFieldType(YearMonth.class, new YearMonthFormatter());
        registry.addFormatterForFieldType(MonthDay.class, new MonthDayFormatter());
        registry.addFormatterForFieldAnnotation(new Jsr310DateTimeFormatAnnotationFormatterFactory());
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
                return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
            }
            case TIME: {
                return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
            }
        }
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    }

    private static enum Type {
        DATE,
        TIME,
        DATE_TIME;

    }
}

