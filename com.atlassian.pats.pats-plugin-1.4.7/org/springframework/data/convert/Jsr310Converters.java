/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.lang.NonNull
 */
package org.springframework.data.convert;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

public abstract class Jsr310Converters {
    private static final List<Class<?>> CLASSES = Arrays.asList(LocalDateTime.class, LocalDate.class, LocalTime.class, Instant.class, ZoneId.class, Duration.class, Period.class);

    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        ArrayList converters = new ArrayList();
        converters.add(DateToLocalDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToDateConverter.INSTANCE);
        converters.add(DateToLocalDateConverter.INSTANCE);
        converters.add(LocalDateToDateConverter.INSTANCE);
        converters.add(DateToLocalTimeConverter.INSTANCE);
        converters.add(LocalTimeToDateConverter.INSTANCE);
        converters.add(DateToInstantConverter.INSTANCE);
        converters.add(InstantToDateConverter.INSTANCE);
        converters.add(LocalDateTimeToInstantConverter.INSTANCE);
        converters.add(InstantToLocalDateTimeConverter.INSTANCE);
        converters.add(ZoneIdToStringConverter.INSTANCE);
        converters.add(StringToZoneIdConverter.INSTANCE);
        converters.add(DurationToStringConverter.INSTANCE);
        converters.add(StringToDurationConverter.INSTANCE);
        converters.add(PeriodToStringConverter.INSTANCE);
        converters.add(StringToPeriodConverter.INSTANCE);
        converters.add(StringToLocalDateConverter.INSTANCE);
        converters.add(StringToLocalDateTimeConverter.INSTANCE);
        converters.add(StringToInstantConverter.INSTANCE);
        return converters;
    }

    public static boolean supports(Class<?> type) {
        return CLASSES.contains(type);
    }

    @ReadingConverter
    public static enum StringToInstantConverter implements Converter<String, Instant>
    {
        INSTANCE;


        @NonNull
        public Instant convert(String source) {
            return Instant.parse(source);
        }
    }

    @ReadingConverter
    public static enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime>
    {
        INSTANCE;


        @NonNull
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    @ReadingConverter
    public static enum StringToLocalDateConverter implements Converter<String, LocalDate>
    {
        INSTANCE;


        @NonNull
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ISO_DATE);
        }
    }

    @ReadingConverter
    public static enum StringToPeriodConverter implements Converter<String, Period>
    {
        INSTANCE;


        @Nonnull
        public Period convert(String s) {
            return Period.parse(s);
        }
    }

    @WritingConverter
    public static enum PeriodToStringConverter implements Converter<Period, String>
    {
        INSTANCE;


        @Nonnull
        public String convert(Period period) {
            return period.toString();
        }
    }

    @ReadingConverter
    public static enum StringToDurationConverter implements Converter<String, Duration>
    {
        INSTANCE;


        @Nonnull
        public Duration convert(String s) {
            return Duration.parse(s);
        }
    }

    @WritingConverter
    public static enum DurationToStringConverter implements Converter<Duration, String>
    {
        INSTANCE;


        @Nonnull
        public String convert(Duration duration) {
            return duration.toString();
        }
    }

    @ReadingConverter
    public static enum StringToZoneIdConverter implements Converter<String, ZoneId>
    {
        INSTANCE;


        @Nonnull
        public ZoneId convert(String source) {
            return ZoneId.of(source);
        }
    }

    @WritingConverter
    public static enum ZoneIdToStringConverter implements Converter<ZoneId, String>
    {
        INSTANCE;


        @Nonnull
        public String convert(ZoneId source) {
            return source.toString();
        }
    }

    @ReadingConverter
    public static enum InstantToLocalDateTimeConverter implements Converter<Instant, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(Instant source) {
            return LocalDateTime.ofInstant(source, ZoneId.systemDefault());
        }
    }

    @ReadingConverter
    public static enum LocalDateTimeToInstantConverter implements Converter<LocalDateTime, Instant>
    {
        INSTANCE;


        @Nonnull
        public Instant convert(LocalDateTime source) {
            return source.atZone(ZoneId.systemDefault()).toInstant();
        }
    }

    @WritingConverter
    public static enum InstantToDateConverter implements Converter<Instant, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(Instant source) {
            return Date.from(source);
        }
    }

    @ReadingConverter
    public static enum DateToInstantConverter implements Converter<Date, Instant>
    {
        INSTANCE;


        @Nonnull
        public Instant convert(Date source) {
            return source.toInstant();
        }
    }

    @WritingConverter
    public static enum LocalTimeToDateConverter implements Converter<LocalTime, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalTime source) {
            return Date.from(source.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @ReadingConverter
    public static enum DateToLocalTimeConverter implements Converter<Date, LocalTime>
    {
        INSTANCE;


        @Nonnull
        public LocalTime convert(Date source) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(source.getTime()), ZoneId.systemDefault()).toLocalTime();
        }
    }

    @WritingConverter
    public static enum LocalDateToDateConverter implements Converter<LocalDate, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalDate source) {
            return Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    @ReadingConverter
    public static enum DateToLocalDateConverter implements Converter<Date, LocalDate>
    {
        INSTANCE;


        @Nonnull
        public LocalDate convert(Date source) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(source.getTime()), ZoneId.systemDefault()).toLocalDate();
        }
    }

    @WritingConverter
    public static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalDateTime source) {
            return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @ReadingConverter
    public static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(Date source) {
            return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }
}

