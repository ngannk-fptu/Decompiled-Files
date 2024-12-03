/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonSerializer
 *  com.fasterxml.jackson.databind.module.SimpleModule
 *  org.joda.time.DateMidnight
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Duration
 *  org.joda.time.Instant
 *  org.joda.time.Interval
 *  org.joda.time.LocalDate
 *  org.joda.time.LocalDateTime
 *  org.joda.time.LocalTime
 *  org.joda.time.MonthDay
 *  org.joda.time.Period
 *  org.joda.time.ReadableDateTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePeriod
 *  org.joda.time.YearMonth
 */
package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.PackageVersion;
import com.fasterxml.jackson.datatype.joda.deser.DateMidnightDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeZoneDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.IntervalDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.MonthDayDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.PeriodDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.key.DateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.key.DurationKeyDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.key.LocalDateKeyDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.key.LocalTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.key.PeriodKeyDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateMidnightSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeZoneSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.joda.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.joda.ser.IntervalSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.MonthDaySerializer;
import com.fasterxml.jackson.datatype.joda.ser.PeriodSerializer;
import com.fasterxml.jackson.datatype.joda.ser.YearMonthSerializer;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.YearMonth;

public class JodaModule
extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public JodaModule() {
        super(PackageVersion.VERSION);
        this.addDeserializer(DateTime.class, DateTimeDeserializer.forType(DateTime.class));
        this.addDeserializer(DateTimeZone.class, (JsonDeserializer)new DateTimeZoneDeserializer());
        this.addDeserializer(Duration.class, (JsonDeserializer)new DurationDeserializer());
        this.addDeserializer(Instant.class, (JsonDeserializer)new InstantDeserializer());
        this.addDeserializer(LocalDateTime.class, (JsonDeserializer)new LocalDateTimeDeserializer());
        this.addDeserializer(LocalDate.class, (JsonDeserializer)new LocalDateDeserializer());
        this.addDeserializer(LocalTime.class, (JsonDeserializer)new LocalTimeDeserializer());
        PeriodDeserializer deser = new PeriodDeserializer(true);
        this.addDeserializer(Period.class, (JsonDeserializer)deser);
        this.addDeserializer(ReadablePeriod.class, (JsonDeserializer)new PeriodDeserializer(false));
        this.addDeserializer(ReadableDateTime.class, DateTimeDeserializer.forType(ReadableDateTime.class));
        this.addDeserializer(ReadableInstant.class, DateTimeDeserializer.forType(ReadableInstant.class));
        this.addDeserializer(Interval.class, (JsonDeserializer)new IntervalDeserializer());
        this.addDeserializer(MonthDay.class, (JsonDeserializer)new MonthDayDeserializer());
        this.addDeserializer(YearMonth.class, (JsonDeserializer)new YearMonthDeserializer());
        this.addSerializer(DateTime.class, (JsonSerializer)new DateTimeSerializer());
        this.addSerializer(DateTimeZone.class, (JsonSerializer)new DateTimeZoneSerializer());
        this.addSerializer(Duration.class, (JsonSerializer)new DurationSerializer());
        this.addSerializer(Instant.class, (JsonSerializer)new InstantSerializer());
        this.addSerializer(LocalDateTime.class, (JsonSerializer)new LocalDateTimeSerializer());
        this.addSerializer(LocalDate.class, (JsonSerializer)new LocalDateSerializer());
        this.addSerializer(LocalTime.class, (JsonSerializer)new LocalTimeSerializer());
        this.addSerializer(Period.class, (JsonSerializer)new PeriodSerializer());
        this.addSerializer(Interval.class, (JsonSerializer)new IntervalSerializer());
        this.addSerializer(MonthDay.class, (JsonSerializer)new MonthDaySerializer());
        this.addSerializer(YearMonth.class, (JsonSerializer)new YearMonthSerializer());
        this.addKeyDeserializer(DateTime.class, new DateTimeKeyDeserializer());
        this.addKeyDeserializer(LocalTime.class, new LocalTimeKeyDeserializer());
        this.addKeyDeserializer(LocalDate.class, new LocalDateKeyDeserializer());
        this.addKeyDeserializer(LocalDateTime.class, new LocalDateTimeKeyDeserializer());
        this.addKeyDeserializer(Duration.class, new DurationKeyDeserializer());
        this.addKeyDeserializer(Period.class, new PeriodKeyDeserializer());
        this.addDeserializer(DateMidnight.class, (JsonDeserializer)new DateMidnightDeserializer());
        this.addSerializer(DateMidnight.class, (JsonSerializer)new DateMidnightSerializer());
    }

    public String getModuleName() {
        return ((Object)((Object)this)).getClass().getSimpleName();
    }

    public int hashCode() {
        return ((Object)((Object)this)).getClass().hashCode();
    }

    public boolean equals(Object o) {
        return this == o;
    }
}

