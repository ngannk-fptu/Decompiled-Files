/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.avro;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicalTypes {
    private static final Logger LOG = LoggerFactory.getLogger(LogicalTypes.class);
    private static final Map<String, LogicalTypeFactory> REGISTERED_TYPES = new ConcurrentHashMap<String, LogicalTypeFactory>();
    private static final String DECIMAL = "decimal";
    private static final String UUID = "uuid";
    private static final String DATE = "date";
    private static final String TIME_MILLIS = "time-millis";
    private static final String TIME_MICROS = "time-micros";
    private static final String TIMESTAMP_MILLIS = "timestamp-millis";
    private static final String TIMESTAMP_MICROS = "timestamp-micros";
    private static final String LOCAL_TIMESTAMP_MILLIS = "local-timestamp-millis";
    private static final String LOCAL_TIMESTAMP_MICROS = "local-timestamp-micros";
    private static final LogicalType UUID_TYPE;
    private static final Date DATE_TYPE;
    private static final TimeMillis TIME_MILLIS_TYPE;
    private static final TimeMicros TIME_MICROS_TYPE;
    private static final TimestampMillis TIMESTAMP_MILLIS_TYPE;
    private static final TimestampMicros TIMESTAMP_MICROS_TYPE;
    private static final LocalTimestampMillis LOCAL_TIMESTAMP_MILLIS_TYPE;
    private static final LocalTimestampMicros LOCAL_TIMESTAMP_MICROS_TYPE;

    public static void register(LogicalTypeFactory factory) {
        Objects.requireNonNull(factory, "Logical type factory cannot be null");
        LogicalTypes.register(factory.getTypeName(), factory);
    }

    public static void register(String logicalTypeName, LogicalTypeFactory factory) {
        Objects.requireNonNull(logicalTypeName, "Logical type name cannot be null");
        Objects.requireNonNull(factory, "Logical type factory cannot be null");
        try {
            String factoryTypeName = factory.getTypeName();
            if (!logicalTypeName.equals(factoryTypeName)) {
                LOG.debug("Provided logicalTypeName '{}' does not match factory typeName '{}'", (Object)logicalTypeName, (Object)factoryTypeName);
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        REGISTERED_TYPES.put(logicalTypeName, factory);
    }

    public static Map<String, LogicalTypeFactory> getCustomRegisteredTypes() {
        return Collections.unmodifiableMap(REGISTERED_TYPES);
    }

    public static LogicalType fromSchema(Schema schema) {
        return LogicalTypes.fromSchemaImpl(schema, true);
    }

    public static LogicalType fromSchemaIgnoreInvalid(Schema schema) {
        return LogicalTypes.fromSchemaImpl(schema, false);
    }

    private static LogicalType fromSchemaImpl(Schema schema, boolean throwErrors) {
        LogicalType logicalType;
        String typeName = schema.getProp("logicalType");
        if (typeName == null) {
            return null;
        }
        try {
            switch (typeName) {
                case "timestamp-millis": {
                    logicalType = TIMESTAMP_MILLIS_TYPE;
                    break;
                }
                case "decimal": {
                    logicalType = new Decimal(schema);
                    break;
                }
                case "uuid": {
                    logicalType = UUID_TYPE;
                    break;
                }
                case "date": {
                    logicalType = DATE_TYPE;
                    break;
                }
                case "timestamp-micros": {
                    logicalType = TIMESTAMP_MICROS_TYPE;
                    break;
                }
                case "time-millis": {
                    logicalType = TIME_MILLIS_TYPE;
                    break;
                }
                case "time-micros": {
                    logicalType = TIME_MICROS_TYPE;
                    break;
                }
                case "local-timestamp-micros": {
                    logicalType = LOCAL_TIMESTAMP_MICROS_TYPE;
                    break;
                }
                case "local-timestamp-millis": {
                    logicalType = LOCAL_TIMESTAMP_MILLIS_TYPE;
                    break;
                }
                default: {
                    LogicalTypeFactory typeFactory = REGISTERED_TYPES.get(typeName);
                    LogicalType logicalType2 = logicalType = typeFactory == null ? null : typeFactory.fromSchema(schema);
                }
            }
            if (logicalType != null) {
                ((LogicalType)logicalType).validate(schema);
            }
        }
        catch (RuntimeException e) {
            LOG.debug("Invalid logical type found", (Throwable)e);
            if (throwErrors) {
                throw e;
            }
            LOG.warn("Ignoring invalid logical type for name: {}", (Object)typeName);
            return null;
        }
        return logicalType;
    }

    public static Decimal decimal(int precision) {
        return LogicalTypes.decimal(precision, 0);
    }

    public static Decimal decimal(int precision, int scale) {
        return new Decimal(precision, scale);
    }

    public static LogicalType uuid() {
        return UUID_TYPE;
    }

    public static Date date() {
        return DATE_TYPE;
    }

    public static TimeMillis timeMillis() {
        return TIME_MILLIS_TYPE;
    }

    public static TimeMicros timeMicros() {
        return TIME_MICROS_TYPE;
    }

    public static TimestampMillis timestampMillis() {
        return TIMESTAMP_MILLIS_TYPE;
    }

    public static TimestampMicros timestampMicros() {
        return TIMESTAMP_MICROS_TYPE;
    }

    public static LocalTimestampMillis localTimestampMillis() {
        return LOCAL_TIMESTAMP_MILLIS_TYPE;
    }

    public static LocalTimestampMicros localTimestampMicros() {
        return LOCAL_TIMESTAMP_MICROS_TYPE;
    }

    static {
        for (LogicalTypeFactory logicalTypeFactory : ServiceLoader.load(LogicalTypeFactory.class)) {
            LogicalTypes.register(logicalTypeFactory);
        }
        UUID_TYPE = new LogicalType(UUID);
        DATE_TYPE = new Date();
        TIME_MILLIS_TYPE = new TimeMillis();
        TIME_MICROS_TYPE = new TimeMicros();
        TIMESTAMP_MILLIS_TYPE = new TimestampMillis();
        TIMESTAMP_MICROS_TYPE = new TimestampMicros();
        LOCAL_TIMESTAMP_MILLIS_TYPE = new LocalTimestampMillis();
        LOCAL_TIMESTAMP_MICROS_TYPE = new LocalTimestampMicros();
    }

    public static class LocalTimestampMicros
    extends LogicalType {
        private LocalTimestampMicros() {
            super(LogicalTypes.LOCAL_TIMESTAMP_MICROS);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.LONG) {
                throw new IllegalArgumentException("Local timestamp (micros) can only be used with an underlying long type");
            }
        }
    }

    public static class LocalTimestampMillis
    extends LogicalType {
        private LocalTimestampMillis() {
            super(LogicalTypes.LOCAL_TIMESTAMP_MILLIS);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.LONG) {
                throw new IllegalArgumentException("Local timestamp (millis) can only be used with an underlying long type");
            }
        }
    }

    public static class TimestampMicros
    extends LogicalType {
        private TimestampMicros() {
            super(LogicalTypes.TIMESTAMP_MICROS);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.LONG) {
                throw new IllegalArgumentException("Timestamp (micros) can only be used with an underlying long type");
            }
        }
    }

    public static class TimestampMillis
    extends LogicalType {
        private TimestampMillis() {
            super(LogicalTypes.TIMESTAMP_MILLIS);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.LONG) {
                throw new IllegalArgumentException("Timestamp (millis) can only be used with an underlying long type");
            }
        }
    }

    public static class TimeMicros
    extends LogicalType {
        private TimeMicros() {
            super(LogicalTypes.TIME_MICROS);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.LONG) {
                throw new IllegalArgumentException("Time (micros) can only be used with an underlying long type");
            }
        }
    }

    public static class TimeMillis
    extends LogicalType {
        private TimeMillis() {
            super(LogicalTypes.TIME_MILLIS);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.INT) {
                throw new IllegalArgumentException("Time (millis) can only be used with an underlying int type");
            }
        }
    }

    public static class Date
    extends LogicalType {
        private Date() {
            super(LogicalTypes.DATE);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.INT) {
                throw new IllegalArgumentException("Date can only be used with an underlying int type");
            }
        }
    }

    public static class Decimal
    extends LogicalType {
        private static final String PRECISION_PROP = "precision";
        private static final String SCALE_PROP = "scale";
        private final int precision;
        private final int scale;

        private Decimal(int precision, int scale) {
            super(LogicalTypes.DECIMAL);
            this.precision = precision;
            this.scale = scale;
        }

        private Decimal(Schema schema) {
            super(LogicalTypes.DECIMAL);
            if (!this.hasProperty(schema, PRECISION_PROP)) {
                throw new IllegalArgumentException("Invalid decimal: missing precision");
            }
            this.precision = this.getInt(schema, PRECISION_PROP);
            this.scale = this.hasProperty(schema, SCALE_PROP) ? this.getInt(schema, SCALE_PROP) : 0;
        }

        @Override
        public Schema addToSchema(Schema schema) {
            super.addToSchema(schema);
            schema.addProp(PRECISION_PROP, this.precision);
            schema.addProp(SCALE_PROP, this.scale);
            return schema;
        }

        public int getPrecision() {
            return this.precision;
        }

        public int getScale() {
            return this.scale;
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.FIXED && schema.getType() != Schema.Type.BYTES) {
                throw new IllegalArgumentException("Logical type decimal must be backed by fixed or bytes");
            }
            if (this.precision <= 0) {
                throw new IllegalArgumentException("Invalid decimal precision: " + this.precision + " (must be positive)");
            }
            if ((long)this.precision > this.maxPrecision(schema)) {
                throw new IllegalArgumentException("fixed(" + schema.getFixedSize() + ") cannot store " + this.precision + " digits (max " + this.maxPrecision(schema) + ")");
            }
            if (this.scale < 0) {
                throw new IllegalArgumentException("Invalid decimal scale: " + this.scale + " (must be positive)");
            }
            if (this.scale > this.precision) {
                throw new IllegalArgumentException("Invalid decimal scale: " + this.scale + " (greater than precision: " + this.precision + ")");
            }
        }

        private long maxPrecision(Schema schema) {
            if (schema.getType() == Schema.Type.BYTES) {
                return Integer.MAX_VALUE;
            }
            if (schema.getType() == Schema.Type.FIXED) {
                int size = schema.getFixedSize();
                return Math.round(Math.floor(Math.log10(2.0) * (double)(8 * size - 1)));
            }
            return 0L;
        }

        private boolean hasProperty(Schema schema, String name) {
            return schema.getObjectProp(name) != null;
        }

        private int getInt(Schema schema, String name) {
            Object obj = schema.getObjectProp(name);
            if (obj instanceof Integer) {
                return (Integer)obj;
            }
            throw new IllegalArgumentException("Expected int " + name + ": " + (obj == null ? "null" : obj + ":" + obj.getClass().getSimpleName()));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Decimal decimal = (Decimal)o;
            if (this.precision != decimal.precision) {
                return false;
            }
            return this.scale == decimal.scale;
        }

        public int hashCode() {
            int result = this.precision;
            result = 31 * result + this.scale;
            return result;
        }
    }

    public static interface LogicalTypeFactory {
        public LogicalType fromSchema(Schema var1);

        default public String getTypeName() {
            throw new UnsupportedOperationException("LogicalTypeFactory TypeName has not been provided");
        }
    }
}

