/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.FromStringDeserializer;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.map.util.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CoreXMLDeserializers
implements Provider<StdDeserializer<?>> {
    static final DatatypeFactory _dataTypeFactory;

    @Override
    public Collection<StdDeserializer<?>> provide() {
        return Arrays.asList(new DurationDeserializer(), new GregorianCalendarDeserializer(), new QNameDeserializer());
    }

    static {
        try {
            _dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class QNameDeserializer
    extends FromStringDeserializer<QName> {
        public QNameDeserializer() {
            super(QName.class);
        }

        @Override
        protected QName _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return QName.valueOf(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class GregorianCalendarDeserializer
    extends StdScalarDeserializer<XMLGregorianCalendar> {
        public GregorianCalendarDeserializer() {
            super(XMLGregorianCalendar.class);
        }

        @Override
        public XMLGregorianCalendar deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Date d = this._parseDate(jp, ctxt);
            if (d == null) {
                return null;
            }
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            return _dataTypeFactory.newXMLGregorianCalendar(calendar);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DurationDeserializer
    extends FromStringDeserializer<Duration> {
        public DurationDeserializer() {
            super(Duration.class);
        }

        @Override
        protected Duration _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return _dataTypeFactory.newDuration(value);
        }
    }
}

