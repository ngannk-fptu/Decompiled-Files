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
    JodaTimeConverters() {
    }

    public static void registerConverters(ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter(new DateTimeToLocalDateConverter());
        registry.addConverter(new DateTimeToLocalTimeConverter());
        registry.addConverter(new DateTimeToLocalDateTimeConverter());
        registry.addConverter(new DateTimeToDateMidnightConverter());
        registry.addConverter(new DateTimeToMutableDateTimeConverter());
        registry.addConverter(new DateTimeToInstantConverter());
        registry.addConverter(new DateTimeToDateConverter());
        registry.addConverter(new DateTimeToCalendarConverter());
        registry.addConverter(new DateTimeToLongConverter());
        registry.addConverter(new DateToReadableInstantConverter());
        registry.addConverter(new CalendarToReadableInstantConverter());
        registry.addConverter(new LongToReadableInstantConverter());
        registry.addConverter(new LocalDateTimeToLocalDateConverter());
        registry.addConverter(new LocalDateTimeToLocalTimeConverter());
    }

    private static class LocalDateTimeToLocalTimeConverter
    implements Converter<LocalDateTime, LocalTime> {
        private LocalDateTimeToLocalTimeConverter() {
        }

        @Override
        public LocalTime convert(LocalDateTime source) {
            return source.toLocalTime();
        }
    }

    private static class LocalDateTimeToLocalDateConverter
    implements Converter<LocalDateTime, LocalDate> {
        private LocalDateTimeToLocalDateConverter() {
        }

        @Override
        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    private static class LongToReadableInstantConverter
    implements Converter<Long, ReadableInstant> {
        private LongToReadableInstantConverter() {
        }

        @Override
        public ReadableInstant convert(Long source) {
            return new DateTime(source.longValue());
        }
    }

    private static class CalendarToReadableInstantConverter
    implements Converter<Calendar, ReadableInstant> {
        private CalendarToReadableInstantConverter() {
        }

        @Override
        public ReadableInstant convert(Calendar source) {
            return new DateTime((Object)source);
        }
    }

    private static class DateToReadableInstantConverter
    implements Converter<Date, ReadableInstant> {
        private DateToReadableInstantConverter() {
        }

        @Override
        public ReadableInstant convert(Date source) {
            return new DateTime((Object)source);
        }
    }

    private static class DateTimeToLongConverter
    implements Converter<DateTime, Long> {
        private DateTimeToLongConverter() {
        }

        @Override
        public Long convert(DateTime source) {
            return source.getMillis();
        }
    }

    private static class DateTimeToCalendarConverter
    implements Converter<DateTime, Calendar> {
        private DateTimeToCalendarConverter() {
        }

        @Override
        public Calendar convert(DateTime source) {
            return source.toGregorianCalendar();
        }
    }

    private static class DateTimeToDateConverter
    implements Converter<DateTime, Date> {
        private DateTimeToDateConverter() {
        }

        @Override
        public Date convert(DateTime source) {
            return source.toDate();
        }
    }

    private static class DateTimeToInstantConverter
    implements Converter<DateTime, Instant> {
        private DateTimeToInstantConverter() {
        }

        @Override
        public Instant convert(DateTime source) {
            return source.toInstant();
        }
    }

    private static class DateTimeToMutableDateTimeConverter
    implements Converter<DateTime, MutableDateTime> {
        private DateTimeToMutableDateTimeConverter() {
        }

        @Override
        public MutableDateTime convert(DateTime source) {
            return source.toMutableDateTime();
        }
    }

    @Deprecated
    private static class DateTimeToDateMidnightConverter
    implements Converter<DateTime, DateMidnight> {
        private DateTimeToDateMidnightConverter() {
        }

        @Override
        public DateMidnight convert(DateTime source) {
            return source.toDateMidnight();
        }
    }

    private static class DateTimeToLocalDateTimeConverter
    implements Converter<DateTime, LocalDateTime> {
        private DateTimeToLocalDateTimeConverter() {
        }

        @Override
        public LocalDateTime convert(DateTime source) {
            return source.toLocalDateTime();
        }
    }

    private static class DateTimeToLocalTimeConverter
    implements Converter<DateTime, LocalTime> {
        private DateTimeToLocalTimeConverter() {
        }

        @Override
        public LocalTime convert(DateTime source) {
            return source.toLocalTime();
        }
    }

    private static class DateTimeToLocalDateConverter
    implements Converter<DateTime, LocalDate> {
        private DateTimeToLocalDateConverter() {
        }

        @Override
        public LocalDate convert(DateTime source) {
            return source.toLocalDate();
        }
    }
}

