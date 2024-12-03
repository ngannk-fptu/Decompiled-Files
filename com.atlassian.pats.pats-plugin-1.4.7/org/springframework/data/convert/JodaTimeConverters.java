/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.convert;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import javax.annotation.Nonnull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.ClassUtils;

@Deprecated
public abstract class JodaTimeConverters {
    private static final boolean JODA_TIME_IS_PRESENT = ClassUtils.isPresent((String)"org.joda.time.LocalDate", null);

    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        if (!JODA_TIME_IS_PRESENT) {
            return Collections.emptySet();
        }
        ArrayList converters = new ArrayList();
        converters.add(LocalDateToDateConverter.INSTANCE);
        converters.add(LocalDateTimeToDateConverter.INSTANCE);
        converters.add(DateTimeToDateConverter.INSTANCE);
        converters.add(DateToLocalDateConverter.INSTANCE);
        converters.add(DateToLocalDateTimeConverter.INSTANCE);
        converters.add(DateToDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToJodaLocalDateTime.INSTANCE);
        converters.add(LocalDateTimeToJodaDateTime.INSTANCE);
        converters.add(InstantToJodaLocalDateTime.INSTANCE);
        converters.add(JodaLocalDateTimeToInstant.INSTANCE);
        converters.add(LocalDateTimeToJsr310Converter.INSTANCE);
        return converters;
    }

    @Deprecated
    public static enum LocalDateTimeToJodaDateTime implements Converter<java.time.LocalDateTime, DateTime>
    {
        INSTANCE;


        @Nonnull
        public DateTime convert(java.time.LocalDateTime source) {
            return new DateTime(Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(source));
        }
    }

    @Deprecated
    public static enum JodaLocalDateTimeToInstant implements Converter<LocalDateTime, Instant>
    {
        INSTANCE;


        @Nonnull
        public Instant convert(LocalDateTime source) {
            return Instant.ofEpochMilli(source.toDateTime().getMillis());
        }
    }

    @Deprecated
    public static enum InstantToJodaLocalDateTime implements Converter<Instant, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(Instant source) {
            return LocalDateTime.fromDateFields(new Date(source.toEpochMilli()));
        }
    }

    @ReadingConverter
    @Deprecated
    public static enum LocalDateTimeToJodaLocalDateTime implements Converter<java.time.LocalDateTime, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(java.time.LocalDateTime source) {
            return LocalDateTime.fromDateFields(Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(source));
        }
    }

    @Deprecated
    public static enum DateToDateTimeConverter implements Converter<Date, DateTime>
    {
        INSTANCE;


        @Nonnull
        public DateTime convert(Date source) {
            return new DateTime(source.getTime());
        }
    }

    @Deprecated
    public static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(Date source) {
            return new LocalDateTime(source.getTime());
        }
    }

    @Deprecated
    public static enum DateToLocalDateConverter implements Converter<Date, LocalDate>
    {
        INSTANCE;


        @Nonnull
        public LocalDate convert(Date source) {
            return new LocalDate(source.getTime());
        }
    }

    @Deprecated
    public static enum DateTimeToDateConverter implements Converter<DateTime, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(DateTime source) {
            return source.toDate();
        }
    }

    @Deprecated
    public static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalDateTime source) {
            return source.toDate();
        }
    }

    @Deprecated
    public static enum LocalDateToDateConverter implements Converter<LocalDate, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalDate source) {
            return source.toDate();
        }
    }

    @Deprecated
    public static enum LocalDateTimeToJsr310Converter implements Converter<LocalDateTime, java.time.LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public java.time.LocalDateTime convert(LocalDateTime source) {
            return java.time.LocalDateTime.ofInstant(source.toDate().toInstant(), ZoneId.systemDefault());
        }
    }
}

