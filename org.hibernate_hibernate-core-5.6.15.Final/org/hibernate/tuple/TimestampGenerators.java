/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.tuple.ValueGenerator;

final class TimestampGenerators {
    private static final Map<Class<?>, ValueGenerator<?>> generators = new HashMap();

    private TimestampGenerators() {
    }

    public static <T> ValueGenerator<T> get(Class<T> type) {
        ValueGenerator<?> valueGeneratorSupplier = generators.get(type);
        if (Objects.isNull(valueGeneratorSupplier)) {
            throw new HibernateException("Unsupported property type [" + type.getName() + "] for @CreationTimestamp or @UpdateTimestamp generator annotation");
        }
        return valueGeneratorSupplier;
    }

    static {
        generators.put(Date.class, (session, owner) -> new Date());
        generators.put(Calendar.class, (session, owner) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            return calendar;
        });
        generators.put(java.sql.Date.class, (session, owner) -> new java.sql.Date(System.currentTimeMillis()));
        generators.put(Time.class, (session, owner) -> new Time(System.currentTimeMillis()));
        generators.put(Timestamp.class, (session, owner) -> new Timestamp(System.currentTimeMillis()));
        generators.put(Instant.class, (session, owner) -> Instant.now());
        generators.put(LocalDate.class, (session, owner) -> LocalDate.now());
        generators.put(LocalDateTime.class, (session, owner) -> LocalDateTime.now());
        generators.put(LocalTime.class, (session, owner) -> LocalTime.now());
        generators.put(MonthDay.class, (session, owner) -> MonthDay.now());
        generators.put(OffsetDateTime.class, (session, owner) -> OffsetDateTime.now());
        generators.put(OffsetTime.class, (session, owner) -> OffsetTime.now());
        generators.put(Year.class, (session, owner) -> Year.now());
        generators.put(YearMonth.class, (session, owner) -> YearMonth.now());
        generators.put(ZonedDateTime.class, (session, owner) -> ZonedDateTime.now());
    }
}

