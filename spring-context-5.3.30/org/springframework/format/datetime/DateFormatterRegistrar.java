/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
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
        Assert.notNull((Object)dateFormatter, (String)"DateFormatter must not be null");
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
        converterRegistry.addConverter((Converter)new DateToLongConverter());
        converterRegistry.addConverter((Converter)new DateToCalendarConverter());
        converterRegistry.addConverter((Converter)new CalendarToDateConverter());
        converterRegistry.addConverter((Converter)new CalendarToLongConverter());
        converterRegistry.addConverter((Converter)new LongToDateConverter());
        converterRegistry.addConverter((Converter)new LongToCalendarConverter());
    }

    private static class LongToCalendarConverter
    implements Converter<Long, Calendar> {
        private LongToCalendarConverter() {
        }

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

        public Date convert(Long source) {
            return new Date(source);
        }
    }

    private static class CalendarToLongConverter
    implements Converter<Calendar, Long> {
        private CalendarToLongConverter() {
        }

        public Long convert(Calendar source) {
            return source.getTimeInMillis();
        }
    }

    private static class CalendarToDateConverter
    implements Converter<Calendar, Date> {
        private CalendarToDateConverter() {
        }

        public Date convert(Calendar source) {
            return source.getTime();
        }
    }

    private static class DateToCalendarConverter
    implements Converter<Date, Calendar> {
        private DateToCalendarConverter() {
        }

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

        public Long convert(Date source) {
            return source.getTime();
        }
    }
}

