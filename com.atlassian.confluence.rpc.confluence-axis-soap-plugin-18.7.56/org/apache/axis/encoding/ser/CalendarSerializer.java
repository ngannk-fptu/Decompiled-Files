/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class CalendarSerializer
implements SimpleValueSerializer {
    private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        context.writeString(this.getValueAsString(value, context));
        context.endElement();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getValueAsString(Object value, SerializationContext context) {
        Date date = value instanceof Date ? (Date)value : ((Calendar)value).getTime();
        SimpleDateFormat simpleDateFormat = zulu;
        synchronized (simpleDateFormat) {
            return zulu.format(date);
        }
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }

    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}

