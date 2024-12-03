/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

public class TimeConversions {

    public static class LocalTimestampMicrosConversion
    extends Conversion<LocalDateTime> {
        private final TimestampMicrosConversion timestampMicrosConversion = new TimestampMicrosConversion();

        @Override
        public Class<LocalDateTime> getConvertedType() {
            return LocalDateTime.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "local-timestamp-micros";
        }

        @Override
        public LocalDateTime fromLong(Long microsFromEpoch, Schema schema, LogicalType type) {
            Instant instant = this.timestampMicrosConversion.fromLong(microsFromEpoch, schema, type);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        }

        @Override
        public Long toLong(LocalDateTime timestamp, Schema schema, LogicalType type) {
            Instant instant = timestamp.toInstant(ZoneOffset.UTC);
            return this.timestampMicrosConversion.toLong(instant, schema, type);
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.localTimestampMicros().addToSchema(Schema.create(Schema.Type.LONG));
        }
    }

    public static class LocalTimestampMillisConversion
    extends Conversion<LocalDateTime> {
        private final TimestampMillisConversion timestampMillisConversion = new TimestampMillisConversion();

        @Override
        public Class<LocalDateTime> getConvertedType() {
            return LocalDateTime.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "local-timestamp-millis";
        }

        @Override
        public LocalDateTime fromLong(Long millisFromEpoch, Schema schema, LogicalType type) {
            Instant instant = this.timestampMillisConversion.fromLong(millisFromEpoch, schema, type);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        }

        @Override
        public Long toLong(LocalDateTime timestamp, Schema schema, LogicalType type) {
            Instant instant = timestamp.toInstant(ZoneOffset.UTC);
            return this.timestampMillisConversion.toLong(instant, schema, type);
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.localTimestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
        }
    }

    public static class TimestampMicrosConversion
    extends Conversion<Instant> {
        @Override
        public Class<Instant> getConvertedType() {
            return Instant.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "timestamp-micros";
        }

        @Override
        public String adjustAndSetValue(String varName, String valParamName) {
            return varName + " = " + valParamName + ".truncatedTo(java.time.temporal.ChronoUnit.MICROS);";
        }

        @Override
        public Instant fromLong(Long microsFromEpoch, Schema schema, LogicalType type) {
            long epochSeconds = microsFromEpoch / 1000000L;
            long nanoAdjustment = microsFromEpoch % 1000000L * 1000L;
            return Instant.ofEpochSecond(epochSeconds, nanoAdjustment);
        }

        @Override
        public Long toLong(Instant instant, Schema schema, LogicalType type) {
            long seconds = instant.getEpochSecond();
            int nanos = instant.getNano();
            if (seconds < 0L && nanos > 0) {
                long micros = Math.multiplyExact(seconds + 1L, 1000000L);
                long adjustment = (long)nanos / 1000L - 1000000L;
                return Math.addExact(micros, adjustment);
            }
            long micros = Math.multiplyExact(seconds, 1000000L);
            return Math.addExact(micros, (long)nanos / 1000L);
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.timestampMicros().addToSchema(Schema.create(Schema.Type.LONG));
        }
    }

    public static class TimestampMillisConversion
    extends Conversion<Instant> {
        @Override
        public Class<Instant> getConvertedType() {
            return Instant.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "timestamp-millis";
        }

        @Override
        public String adjustAndSetValue(String varName, String valParamName) {
            return varName + " = " + valParamName + ".truncatedTo(java.time.temporal.ChronoUnit.MILLIS);";
        }

        @Override
        public Instant fromLong(Long millisFromEpoch, Schema schema, LogicalType type) {
            return Instant.ofEpochMilli(millisFromEpoch);
        }

        @Override
        public Long toLong(Instant timestamp, Schema schema, LogicalType type) {
            return timestamp.toEpochMilli();
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
        }
    }

    public static class TimeMicrosConversion
    extends Conversion<LocalTime> {
        @Override
        public Class<LocalTime> getConvertedType() {
            return LocalTime.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "time-micros";
        }

        @Override
        public String adjustAndSetValue(String varName, String valParamName) {
            return varName + " = " + valParamName + ".truncatedTo(java.time.temporal.ChronoUnit.MICROS);";
        }

        @Override
        public LocalTime fromLong(Long microsFromMidnight, Schema schema, LogicalType type) {
            return LocalTime.ofNanoOfDay(TimeUnit.MICROSECONDS.toNanos(microsFromMidnight));
        }

        @Override
        public Long toLong(LocalTime time, Schema schema, LogicalType type) {
            return TimeUnit.NANOSECONDS.toMicros(time.toNanoOfDay());
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG));
        }
    }

    public static class TimeMillisConversion
    extends Conversion<LocalTime> {
        @Override
        public Class<LocalTime> getConvertedType() {
            return LocalTime.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "time-millis";
        }

        @Override
        public String adjustAndSetValue(String varName, String valParamName) {
            return varName + " = " + valParamName + ".truncatedTo(java.time.temporal.ChronoUnit.MILLIS);";
        }

        @Override
        public LocalTime fromInt(Integer millisFromMidnight, Schema schema, LogicalType type) {
            return LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.toNanos(millisFromMidnight.intValue()));
        }

        @Override
        public Integer toInt(LocalTime time, Schema schema, LogicalType type) {
            return (int)TimeUnit.NANOSECONDS.toMillis(time.toNanoOfDay());
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT));
        }
    }

    public static class DateConversion
    extends Conversion<LocalDate> {
        @Override
        public Class<LocalDate> getConvertedType() {
            return LocalDate.class;
        }

        @Override
        public String getLogicalTypeName() {
            return "date";
        }

        @Override
        public LocalDate fromInt(Integer daysFromEpoch, Schema schema, LogicalType type) {
            return LocalDate.ofEpochDay(daysFromEpoch.intValue());
        }

        @Override
        public Integer toInt(LocalDate date, Schema schema, LogicalType type) {
            long epochDays = date.toEpochDay();
            return (int)epochDays;
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.date().addToSchema(Schema.create(Schema.Type.INT));
        }
    }
}

