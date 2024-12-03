/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateMidnight
 *  org.joda.time.DateTime
 *  org.joda.time.LocalDate
 *  org.joda.time.LocalDateTime
 *  org.joda.time.Period
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 */
package org.codehaus.jackson.map.ext;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.codehaus.jackson.map.util.Provider;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JodaSerializers
implements Provider<Map.Entry<Class<?>, JsonSerializer<?>>> {
    static final HashMap<Class<?>, JsonSerializer<?>> _serializers = new HashMap();

    @Override
    public Collection<Map.Entry<Class<?>, JsonSerializer<?>>> provide() {
        return _serializers.entrySet();
    }

    static {
        _serializers.put(DateTime.class, new DateTimeSerializer());
        _serializers.put(LocalDateTime.class, new LocalDateTimeSerializer());
        _serializers.put(LocalDate.class, new LocalDateSerializer());
        _serializers.put(DateMidnight.class, new DateMidnightSerializer());
        _serializers.put(Period.class, ToStringSerializer.instance);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class DateMidnightSerializer
    extends JodaSerializer<DateMidnight> {
        public DateMidnightSerializer() {
            super(DateMidnight.class);
        }

        @Override
        public void serialize(DateMidnight dt, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS)) {
                jgen.writeStartArray();
                jgen.writeNumber(dt.year().get());
                jgen.writeNumber(dt.monthOfYear().get());
                jgen.writeNumber(dt.dayOfMonth().get());
                jgen.writeEndArray();
            } else {
                jgen.writeString(this.printLocalDate((ReadableInstant)dt));
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode(provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS) ? "array" : "string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class LocalDateSerializer
    extends JodaSerializer<LocalDate> {
        public LocalDateSerializer() {
            super(LocalDate.class);
        }

        @Override
        public void serialize(LocalDate dt, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS)) {
                jgen.writeStartArray();
                jgen.writeNumber(dt.year().get());
                jgen.writeNumber(dt.monthOfYear().get());
                jgen.writeNumber(dt.dayOfMonth().get());
                jgen.writeEndArray();
            } else {
                jgen.writeString(this.printLocalDate((ReadablePartial)dt));
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode(provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS) ? "array" : "string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class LocalDateTimeSerializer
    extends JodaSerializer<LocalDateTime> {
        public LocalDateTimeSerializer() {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(LocalDateTime dt, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS)) {
                jgen.writeStartArray();
                jgen.writeNumber(dt.year().get());
                jgen.writeNumber(dt.monthOfYear().get());
                jgen.writeNumber(dt.dayOfMonth().get());
                jgen.writeNumber(dt.hourOfDay().get());
                jgen.writeNumber(dt.minuteOfHour().get());
                jgen.writeNumber(dt.secondOfMinute().get());
                jgen.writeNumber(dt.millisOfSecond().get());
                jgen.writeEndArray();
            } else {
                jgen.writeString(this.printLocalDateTime((ReadablePartial)dt));
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode(provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS) ? "array" : "string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class DateTimeSerializer
    extends JodaSerializer<DateTime> {
        public DateTimeSerializer() {
            super(DateTime.class);
        }

        @Override
        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS)) {
                jgen.writeNumber(value.getMillis());
            } else {
                jgen.writeString(value.toString());
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode(provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS) ? "number" : "string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static abstract class JodaSerializer<T>
    extends SerializerBase<T> {
        static final DateTimeFormatter _localDateTimeFormat = ISODateTimeFormat.dateTime();
        static final DateTimeFormatter _localDateFormat = ISODateTimeFormat.date();

        protected JodaSerializer(Class<T> cls) {
            super(cls);
        }

        protected String printLocalDateTime(ReadablePartial dateValue) throws IOException, JsonProcessingException {
            return _localDateTimeFormat.print(dateValue);
        }

        protected String printLocalDate(ReadablePartial dateValue) throws IOException, JsonProcessingException {
            return _localDateFormat.print(dateValue);
        }

        protected String printLocalDate(ReadableInstant dateValue) throws IOException, JsonProcessingException {
            return _localDateFormat.print(dateValue);
        }
    }
}

