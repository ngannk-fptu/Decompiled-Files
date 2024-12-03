/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateMidnight
 *  org.joda.time.DateTime
 *  org.joda.time.Instant
 *  org.joda.time.LocalDate
 *  org.joda.time.LocalDateTime
 *  org.joda.time.LocalTime
 *  org.joda.time.MutableDateTime
 *  org.joda.time.ReadableInstant
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterRegistry
 */
package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableInstant;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;

final class JodaTimeConverters {
    private JodaTimeConverters() {
    }

    public static void registerConverters(ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter((Converter)new DateTimeToLocalDateConverter());
        registry.addConverter((Converter)new DateTimeToLocalTimeConverter());
        registry.addConverter((Converter)new DateTimeToLocalDateTimeConverter());
        registry.addConverter((Converter)new DateTimeToDateMidnightConverter());
        registry.addConverter((Converter)new DateTimeToMutableDateTimeConverter());
        registry.addConverter((Converter)new DateTimeToInstantConverter());
        registry.addConverter((Converter)new DateTimeToDateConverter());
        registry.addConverter((Converter)new DateTimeToCalendarConverter());
        registry.addConverter((Converter)new DateTimeToLongConverter());
        registry.addConverter((Converter)new DateToReadableInstantConverter());
        registry.addConverter((Converter)new CalendarToReadableInstantConverter());
        registry.addConverter((Converter)new LongToReadableInstantConverter());
        registry.addConverter((Converter)new LocalDateTimeToLocalDateConverter());
        registry.addConverter((Converter)new LocalDateTimeToLocalTimeConverter());
    }

    private static class LocalDateTimeToLocalTimeConverter
    implements Converter<LocalDateTime, LocalTime> {
        private LocalDateTimeToLocalTimeConverter() {
        }

        public LocalTime convert(LocalDateTime source) {
            return source.toLocalTime();
        }
    }

    private static class LocalDateTimeToLocalDateConverter
    implements Converter<LocalDateTime, LocalDate> {
        private LocalDateTimeToLocalDateConverter() {
        }

        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    private static class LongToReadableInstantConverter
    implements Converter<Long, ReadableInstant> {
        private LongToReadableInstantConverter() {
        }

        public ReadableInstant convert(Long source) {
            return new DateTime(source.longValue());
        }
    }

    private static class CalendarToReadableInstantConverter
    implements Converter<Calendar, ReadableInstant> {
        private CalendarToReadableInstantConverter() {
        }

        public ReadableInstant convert(Calendar source) {
            return new DateTime((Object)source);
        }
    }

    private static class DateToReadableInstantConverter
    implements Converter<Date, ReadableInstant> {
        private DateToReadableInstantConverter() {
        }

        public ReadableInstant convert(Date source) {
            return new DateTime((Object)source);
        }
    }

    private static class DateTimeToLongConverter
    implements Converter<DateTime, Long> {
        private DateTimeToLongConverter() {
        }

        public Long convert(DateTime source) {
            return source.getMillis();
        }
    }

    private static class DateTimeToCalendarConverter
    implements Converter<DateTime, Calendar> {
        private DateTimeToCalendarConverter() {
        }

        public Calendar convert(DateTime source) {
            return source.toGregorianCalendar();
        }
    }

    private static class DateTimeToDateConverter
    implements Converter<DateTime, Date> {
        private DateTimeToDateConverter() {
        }

        public Date convert(DateTime source) {
            return source.toDate();
        }
    }

    private static class DateTimeToInstantConverter
    implements Converter<DateTime, Instant> {
        private DateTimeToInstantConverter() {
        }

        public Instant convert(DateTime source) {
            return source.toInstant();
        }
    }

    private static class DateTimeToMutableDateTimeConverter
    implements Converter<DateTime, MutableDateTime> {
        private DateTimeToMutableDateTimeConverter() {
        }

        public MutableDateTime convert(DateTime source) {
            return source.toMutableDateTime();
        }
    }

    @Deprecated
    private static class DateTimeToDateMidnightConverter
    implements Converter<DateTime, DateMidnight> {
        private DateTimeToDateMidnightConverter() {
        }

        public DateMidnight convert(DateTime source) {
            return source.toDateMidnight();
        }
    }

    private static class DateTimeToLocalDateTimeConverter
    implements Converter<DateTime, LocalDateTime> {
        private DateTimeToLocalDateTimeConverter() {
        }

        public LocalDateTime convert(DateTime source) {
            return source.toLocalDateTime();
        }
    }

    private static class DateTimeToLocalTimeConverter
    implements Converter<DateTime, LocalTime> {
        private DateTimeToLocalTimeConverter() {
        }

        public LocalTime convert(DateTime source) {
            return source.toLocalTime();
        }
    }

    private static class DateTimeToLocalDateConverter
    implements Converter<DateTime, LocalDate> {
        private DateTimeToLocalDateConverter() {
        }

        public LocalDate convert(DateTime source) {
            return source.toLocalDate();
        }
    }
}

