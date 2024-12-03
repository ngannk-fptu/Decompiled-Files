/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class DateSerializer
implements SimpleValueSerializer {
    private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd");
    private static Calendar calendar = Calendar.getInstance();

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        context.writeString(this.getValueAsString(value, context));
        context.endElement();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getValueAsString(Object value, SerializationContext context) {
        StringBuffer buf = new StringBuffer();
        Calendar calendar = DateSerializer.calendar;
        synchronized (calendar) {
            if (value instanceof Calendar) {
                value = ((Calendar)value).getTime();
            }
            if (DateSerializer.calendar.get(0) == 0) {
                buf.append("-");
                DateSerializer.calendar.setTime((Date)value);
                DateSerializer.calendar.set(0, 1);
                value = DateSerializer.calendar.getTime();
            }
            buf.append(zulu.format((Date)value));
        }
        return buf.toString();
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}

