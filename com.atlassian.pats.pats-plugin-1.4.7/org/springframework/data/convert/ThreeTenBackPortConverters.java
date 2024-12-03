/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.util.ClassUtils
 *  org.threeten.bp.DateTimeUtils
 *  org.threeten.bp.Instant
 *  org.threeten.bp.LocalDate
 *  org.threeten.bp.LocalDateTime
 *  org.threeten.bp.LocalTime
 *  org.threeten.bp.ZoneId
 *  org.threeten.bp.ZoneOffset
 */
package org.springframework.data.convert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import javax.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.util.ClassUtils;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

@Deprecated
public abstract class ThreeTenBackPortConverters {
    private static final boolean THREE_TEN_BACK_PORT_IS_PRESENT = ClassUtils.isPresent((String)"org.threeten.bp.LocalDateTime", (ClassLoader)ThreeTenBackPortConverters.class.getClassLoader());
    private static final Collection<Class<?>> SUPPORTED_TYPES = THREE_TEN_BACK_PORT_IS_PRESENT ? Arrays.asList(LocalDateTime.class, LocalDate.class, LocalTime.class, org.threeten.bp.Instant.class, Instant.class) : Collections.emptySet();

    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        if (!THREE_TEN_BACK_PORT_IS_PRESENT) {
            return Collections.emptySet();
        }
        ArrayList converters = new ArrayList();
        converters.add(DateToLocalDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToDateConverter.INSTANCE);
        converters.add(DateToLocalDateConverter.INSTANCE);
        converters.add(LocalDateToDateConverter.INSTANCE);
        converters.add(DateToLocalTimeConverter.INSTANCE);
        converters.add(LocalTimeToDateConverter.INSTANCE);
        converters.add(DateToInstantConverter.INSTANCE);
        converters.add(InstantToDateConverter.INSTANCE);
        converters.add(ZoneIdToStringConverter.INSTANCE);
        converters.add(StringToZoneIdConverter.INSTANCE);
        converters.add(LocalDateTimeToJsr310LocalDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToJavaTimeInstantConverter.INSTANCE);
        converters.add(JavaTimeInstantToLocalDateTimeConverter.INSTANCE);
        return converters;
    }

    public static boolean supports(Class<?> type) {
        return SUPPORTED_TYPES.contains(type);
    }

    @ReadingConverter
    @Deprecated
    public static enum StringToZoneIdConverter implements Converter<String, ZoneId>
    {
        INSTANCE;


        @Nonnull
        public ZoneId convert(String source) {
            return ZoneId.of((String)source);
        }
    }

    @WritingConverter
    @Deprecated
    public static enum ZoneIdToStringConverter implements Converter<ZoneId, String>
    {
        INSTANCE;


        @Nonnull
        public String convert(ZoneId source) {
            return source.toString();
        }
    }

    @Deprecated
    public static enum JavaTimeInstantToLocalDateTimeConverter implements Converter<Instant, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(Instant source) {
            return LocalDateTime.ofInstant((org.threeten.bp.Instant)org.threeten.bp.Instant.ofEpochMilli((long)source.toEpochMilli()), (ZoneId)ZoneOffset.systemDefault());
        }
    }

    @Deprecated
    public static enum LocalDateTimeToJavaTimeInstantConverter implements Converter<LocalDateTime, Instant>
    {
        INSTANCE;


        @Nonnull
        public Instant convert(LocalDateTime source) {
            return Instant.ofEpochMilli(source.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
        }
    }

    @Deprecated
    public static enum InstantToDateConverter implements Converter<org.threeten.bp.Instant, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(org.threeten.bp.Instant source) {
            return DateTimeUtils.toDate((org.threeten.bp.Instant)source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @Deprecated
    public static enum DateToInstantConverter implements Converter<Date, org.threeten.bp.Instant>
    {
        INSTANCE;


        @Nonnull
        public org.threeten.bp.Instant convert(Date source) {
            return DateTimeUtils.toInstant((Date)source);
        }
    }

    @Deprecated
    public static enum LocalTimeToDateConverter implements Converter<LocalTime, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalTime source) {
            return DateTimeUtils.toDate((org.threeten.bp.Instant)source.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @Deprecated
    public static enum DateToLocalTimeConverter implements Converter<Date, LocalTime>
    {
        INSTANCE;


        @Nonnull
        public LocalTime convert(Date source) {
            return LocalDateTime.ofInstant((org.threeten.bp.Instant)org.threeten.bp.Instant.ofEpochMilli((long)source.getTime()), (ZoneId)ZoneId.systemDefault()).toLocalTime();
        }
    }

    @Deprecated
    public static enum LocalDateToDateConverter implements Converter<LocalDate, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalDate source) {
            return DateTimeUtils.toDate((org.threeten.bp.Instant)source.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    @Deprecated
    public static enum DateToLocalDateConverter implements Converter<Date, LocalDate>
    {
        INSTANCE;


        @Nonnull
        public LocalDate convert(Date source) {
            return LocalDateTime.ofInstant((org.threeten.bp.Instant)org.threeten.bp.Instant.ofEpochMilli((long)source.getTime()), (ZoneId)ZoneId.systemDefault()).toLocalDate();
        }
    }

    @Deprecated
    public static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date>
    {
        INSTANCE;


        @Nonnull
        public Date convert(LocalDateTime source) {
            return DateTimeUtils.toDate((org.threeten.bp.Instant)source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @Deprecated
    public static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public LocalDateTime convert(Date source) {
            return LocalDateTime.ofInstant((org.threeten.bp.Instant)DateTimeUtils.toInstant((Date)source), (ZoneId)ZoneId.systemDefault());
        }
    }

    @Deprecated
    public static enum LocalDateTimeToJsr310LocalDateTimeConverter implements Converter<LocalDateTime, java.time.LocalDateTime>
    {
        INSTANCE;


        @Nonnull
        public java.time.LocalDateTime convert(LocalDateTime source) {
            Date date = DateTimeUtils.toDate((org.threeten.bp.Instant)source.atZone(ZoneId.systemDefault()).toInstant());
            return Jsr310Converters.DateToLocalDateTimeConverter.INSTANCE.convert(date);
        }
    }
}

