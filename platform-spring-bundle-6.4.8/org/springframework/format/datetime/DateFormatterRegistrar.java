/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime;

import java.util.Calendar;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateTimeFormatAnnotationFormatterFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DateFormatterRegistrar
implements FormatterRegistrar {
    @Nullable
    private DateFormatter dateFormatter;

    public void setFormatter(DateFormatter dateFormatter) {
        Assert.notNull((Object)dateFormatter, "DateFormatter must not be null");
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        if (this.dateFormatter != null) {
            registry.addFormatter(this.dateFormatter);
            registry.addFormatterForFieldType(Calendar.class, this.dateFormatter);
        }
        registry.addFormatterForFieldAnnotation(new DateTimeFormatAnnotationFormatterFactory());
    }

    public static void addDateConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverter(new DateToLongConverter());
        converterRegistry.addConverter(new DateToCalendarConverter());
        converterRegistry.addConverter(new CalendarToDateConverter());
        converterRegistry.addConverter(new CalendarToLongConverter());
        converterRegistry.addConverter(new LongToDateConverter());
        converterRegistry.addConverter(new LongToCalendarConverter());
    }

    private static class LongToCalendarConverter
    implements Converter<Long, Calendar> {
        private LongToCalendarConverter() {
        }

        @Override
        public Calendar convert(Long source) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(source);
            return calendar;
        }
    }

    private static class LongToDateConverter
    implements Converter<Long, Date> {
        private LongToDateConverter() {
        }

        @Override
        public Date convert(Long source) {
            return new Date(source);
        }
    }

    private static class CalendarToLongConverter
    implements Converter<Calendar, Long> {
        private CalendarToLongConverter() {
        }

        @Override
        public Long convert(Calendar source) {
            return source.getTimeInMillis();
        }
    }

    private static class CalendarToDateConverter
    implements Converter<Calendar, Date> {
        private CalendarToDateConverter() {
        }

        @Override
        public Date convert(Calendar source) {
            return source.getTime();
        }
    }

    private static class DateToCalendarConverter
    implements Converter<Date, Calendar> {
        private DateToCalendarConverter() {
        }

        @Override
        public Calendar convert(Date source) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(source);
            return calendar;
        }
    }

    private static class DateToLongConverter
    implements Converter<Date, Long> {
        private DateToLongConverter() {
        }

        @Override
        public Long convert(Date source) {
            return source.getTime();
        }
    }
}

