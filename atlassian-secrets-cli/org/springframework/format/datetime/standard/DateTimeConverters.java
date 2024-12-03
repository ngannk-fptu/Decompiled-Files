/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;

final class DateTimeConverters {
    DateTimeConverters() {
    }

    public static void registerConverters(ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter(new LocalDateTimeToLocalDateConverter());
        registry.addConverter(new LocalDateTimeToLocalTimeConverter());
        registry.addConverter(new ZonedDateTimeToLocalDateConverter());
        registry.addConverter(new ZonedDateTimeToLocalTimeConverter());
        registry.addConverter(new ZonedDateTimeToLocalDateTimeConverter());
        registry.addConverter(new ZonedDateTimeToOffsetDateTimeConverter());
        registry.addConverter(new ZonedDateTimeToInstantConverter());
        registry.addConverter(new OffsetDateTimeToLocalDateConverter());
        registry.addConverter(new OffsetDateTimeToLocalTimeConverter());
        registry.addConverter(new OffsetDateTimeToLocalDateTimeConverter());
        registry.addConverter(new OffsetDateTimeToZonedDateTimeConverter());
        registry.addConverter(new OffsetDateTimeToInstantConverter());
        registry.addConverter(new CalendarToZonedDateTimeConverter());
        registry.addConverter(new CalendarToOffsetDateTimeConverter());
        registry.addConverter(new CalendarToLocalDateConverter());
        registry.addConverter(new CalendarToLocalTimeConverter());
        registry.addConverter(new CalendarToLocalDateTimeConverter());
        registry.addConverter(new CalendarToInstantConverter());
        registry.addConverter(new LongToInstantConverter());
        registry.addConverter(new InstantToLongConverter());
    }

    private static ZonedDateTime calendarToZonedDateTime(Calendar source) {
        if (source instanceof GregorianCalendar) {
            return ((GregorianCalendar)source).toZonedDateTime();
        }
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.getTimeInMillis()), source.getTimeZone().toZoneId());
    }

    private static class InstantToLongConverter
    implements Converter<Instant, Long> {
        private InstantToLongConverter() {
        }

        @Override
        public Long convert(Instant source) {
            return source.toEpochMilli();
        }
    }

    private static class LongToInstantConverter
    implements Converter<Long, Instant> {
        private LongToInstantConverter() {
        }

        @Override
        public Instant convert(Long source) {
            return Instant.ofEpochMilli(source);
        }
    }

    private static class CalendarToInstantConverter
    implements Converter<Calendar, Instant> {
        private CalendarToInstantConverter() {
        }

        @Override
        public Instant convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toInstant();
        }
    }

    private static class CalendarToLocalDateTimeConverter
    implements Converter<Calendar, LocalDateTime> {
        private CalendarToLocalDateTimeConverter() {
        }

        @Override
        public LocalDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalDateTime();
        }
    }

    private static class CalendarToLocalTimeConverter
    implements Converter<Calendar, LocalTime> {
        private CalendarToLocalTimeConverter() {
        }

        @Override
        public LocalTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalTime();
        }
    }

    private static class CalendarToLocalDateConverter
    implements Converter<Calendar, LocalDate> {
        private CalendarToLocalDateConverter() {
        }

        @Override
        public LocalDate convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalDate();
        }
    }

    private static class CalendarToOffsetDateTimeConverter
    implements Converter<Calendar, OffsetDateTime> {
        private CalendarToOffsetDateTimeConverter() {
        }

        @Override
        public OffsetDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toOffsetDateTime();
        }
    }

    private static class CalendarToZonedDateTimeConverter
    implements Converter<Calendar, ZonedDateTime> {
        private CalendarToZonedDateTimeConverter() {
        }

        @Override
        public ZonedDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source);
        }
    }

    private static class OffsetDateTimeToInstantConverter
    implements Converter<OffsetDateTime, Instant> {
        private OffsetDateTimeToInstantConverter() {
        }

        @Override
        public Instant convert(OffsetDateTime source) {
            return source.toInstant();
        }
    }

    private static class OffsetDateTimeToZonedDateTimeConverter
    implements Converter<OffsetDateTime, ZonedDateTime> {
        private OffsetDateTimeToZonedDateTimeConverter() {
        }

        @Override
        public ZonedDateTime convert(OffsetDateTime source) {
            return source.toZonedDateTime();
        }
    }

    private static class OffsetDateTimeToLocalDateTimeConverter
    implements Converter<OffsetDateTime, LocalDateTime> {
        private OffsetDateTimeToLocalDateTimeConverter() {
        }

        @Override
        public LocalDateTime convert(OffsetDateTime source) {
            return source.toLocalDateTime();
        }
    }

    private static class OffsetDateTimeToLocalTimeConverter
    implements Converter<OffsetDateTime, LocalTime> {
        private OffsetDateTimeToLocalTimeConverter() {
        }

        @Override
        public LocalTime convert(OffsetDateTime source) {
            return source.toLocalTime();
        }
    }

    private static class OffsetDateTimeToLocalDateConverter
    implements Converter<OffsetDateTime, LocalDate> {
        private OffsetDateTimeToLocalDateConverter() {
        }

        @Override
        public LocalDate convert(OffsetDateTime source) {
            return source.toLocalDate();
        }
    }

    private static class ZonedDateTimeToInstantConverter
    implements Converter<ZonedDateTime, Instant> {
        private ZonedDateTimeToInstantConverter() {
        }

        @Override
        public Instant convert(ZonedDateTime source) {
            return source.toInstant();
        }
    }

    private static class ZonedDateTimeToOffsetDateTimeConverter
    implements Converter<ZonedDateTime, OffsetDateTime> {
        private ZonedDateTimeToOffsetDateTimeConverter() {
        }

        @Override
        public OffsetDateTime convert(ZonedDateTime source) {
            return source.toOffsetDateTime();
        }
    }

    private static class ZonedDateTimeToLocalDateTimeConverter
    implements Converter<ZonedDateTime, LocalDateTime> {
        private ZonedDateTimeToLocalDateTimeConverter() {
        }

        @Override
        public LocalDateTime convert(ZonedDateTime source) {
            return source.toLocalDateTime();
        }
    }

    private static class ZonedDateTimeToLocalTimeConverter
    implements Converter<ZonedDateTime, LocalTime> {
        private ZonedDateTimeToLocalTimeConverter() {
        }

        @Override
        public LocalTime convert(ZonedDateTime source) {
            return source.toLocalTime();
        }
    }

    private static class ZonedDateTimeToLocalDateConverter
    implements Converter<ZonedDateTime, LocalDate> {
        private ZonedDateTimeToLocalDateConverter() {
        }

        @Override
        public LocalDate convert(ZonedDateTime source) {
            return source.toLocalDate();
        }
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
}

