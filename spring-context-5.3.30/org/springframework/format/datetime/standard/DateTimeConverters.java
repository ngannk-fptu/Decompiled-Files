/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterRegistry
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
    private DateTimeConverters() {
    }

    public static void registerConverters(ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter((Converter)new LocalDateTimeToLocalDateConverter());
        registry.addConverter((Converter)new LocalDateTimeToLocalTimeConverter());
        registry.addConverter((Converter)new ZonedDateTimeToLocalDateConverter());
        registry.addConverter((Converter)new ZonedDateTimeToLocalTimeConverter());
        registry.addConverter((Converter)new ZonedDateTimeToLocalDateTimeConverter());
        registry.addConverter((Converter)new ZonedDateTimeToOffsetDateTimeConverter());
        registry.addConverter((Converter)new ZonedDateTimeToInstantConverter());
        registry.addConverter((Converter)new OffsetDateTimeToLocalDateConverter());
        registry.addConverter((Converter)new OffsetDateTimeToLocalTimeConverter());
        registry.addConverter((Converter)new OffsetDateTimeToLocalDateTimeConverter());
        registry.addConverter((Converter)new OffsetDateTimeToZonedDateTimeConverter());
        registry.addConverter((Converter)new OffsetDateTimeToInstantConverter());
        registry.addConverter((Converter)new CalendarToZonedDateTimeConverter());
        registry.addConverter((Converter)new CalendarToOffsetDateTimeConverter());
        registry.addConverter((Converter)new CalendarToLocalDateConverter());
        registry.addConverter((Converter)new CalendarToLocalTimeConverter());
        registry.addConverter((Converter)new CalendarToLocalDateTimeConverter());
        registry.addConverter((Converter)new CalendarToInstantConverter());
        registry.addConverter((Converter)new LongToInstantConverter());
        registry.addConverter((Converter)new InstantToLongConverter());
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

        public Long convert(Instant source) {
            return source.toEpochMilli();
        }
    }

    private static class LongToInstantConverter
    implements Converter<Long, Instant> {
        private LongToInstantConverter() {
        }

        public Instant convert(Long source) {
            return Instant.ofEpochMilli(source);
        }
    }

    private static class CalendarToInstantConverter
    implements Converter<Calendar, Instant> {
        private CalendarToInstantConverter() {
        }

        public Instant convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toInstant();
        }
    }

    private static class CalendarToLocalDateTimeConverter
    implements Converter<Calendar, LocalDateTime> {
        private CalendarToLocalDateTimeConverter() {
        }

        public LocalDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalDateTime();
        }
    }

    private static class CalendarToLocalTimeConverter
    implements Converter<Calendar, LocalTime> {
        private CalendarToLocalTimeConverter() {
        }

        public LocalTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalTime();
        }
    }

    private static class CalendarToLocalDateConverter
    implements Converter<Calendar, LocalDate> {
        private CalendarToLocalDateConverter() {
        }

        public LocalDate convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalDate();
        }
    }

    private static class CalendarToOffsetDateTimeConverter
    implements Converter<Calendar, OffsetDateTime> {
        private CalendarToOffsetDateTimeConverter() {
        }

        public OffsetDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toOffsetDateTime();
        }
    }

    private static class CalendarToZonedDateTimeConverter
    implements Converter<Calendar, ZonedDateTime> {
        private CalendarToZonedDateTimeConverter() {
        }

        public ZonedDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source);
        }
    }

    private static class OffsetDateTimeToInstantConverter
    implements Converter<OffsetDateTime, Instant> {
        private OffsetDateTimeToInstantConverter() {
        }

        public Instant convert(OffsetDateTime source) {
            return source.toInstant();
        }
    }

    private static class OffsetDateTimeToZonedDateTimeConverter
    implements Converter<OffsetDateTime, ZonedDateTime> {
        private OffsetDateTimeToZonedDateTimeConverter() {
        }

        public ZonedDateTime convert(OffsetDateTime source) {
            return source.toZonedDateTime();
        }
    }

    private static class OffsetDateTimeToLocalDateTimeConverter
    implements Converter<OffsetDateTime, LocalDateTime> {
        private OffsetDateTimeToLocalDateTimeConverter() {
        }

        public LocalDateTime convert(OffsetDateTime source) {
            return source.toLocalDateTime();
        }
    }

    private static class OffsetDateTimeToLocalTimeConverter
    implements Converter<OffsetDateTime, LocalTime> {
        private OffsetDateTimeToLocalTimeConverter() {
        }

        public LocalTime convert(OffsetDateTime source) {
            return source.toLocalTime();
        }
    }

    private static class OffsetDateTimeToLocalDateConverter
    implements Converter<OffsetDateTime, LocalDate> {
        private OffsetDateTimeToLocalDateConverter() {
        }

        public LocalDate convert(OffsetDateTime source) {
            return source.toLocalDate();
        }
    }

    private static class ZonedDateTimeToInstantConverter
    implements Converter<ZonedDateTime, Instant> {
        private ZonedDateTimeToInstantConverter() {
        }

        public Instant convert(ZonedDateTime source) {
            return source.toInstant();
        }
    }

    private static class ZonedDateTimeToOffsetDateTimeConverter
    implements Converter<ZonedDateTime, OffsetDateTime> {
        private ZonedDateTimeToOffsetDateTimeConverter() {
        }

        public OffsetDateTime convert(ZonedDateTime source) {
            return source.toOffsetDateTime();
        }
    }

    private static class ZonedDateTimeToLocalDateTimeConverter
    implements Converter<ZonedDateTime, LocalDateTime> {
        private ZonedDateTimeToLocalDateTimeConverter() {
        }

        public LocalDateTime convert(ZonedDateTime source) {
            return source.toLocalDateTime();
        }
    }

    private static class ZonedDateTimeToLocalTimeConverter
    implements Converter<ZonedDateTime, LocalTime> {
        private ZonedDateTimeToLocalTimeConverter() {
        }

        public LocalTime convert(ZonedDateTime source) {
            return source.toLocalTime();
        }
    }

    private static class ZonedDateTimeToLocalDateConverter
    implements Converter<ZonedDateTime, LocalDate> {
        private ZonedDateTimeToLocalDateConverter() {
        }

        public LocalDate convert(ZonedDateTime source) {
            return source.toLocalDate();
        }
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
}

