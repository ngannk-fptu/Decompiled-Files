/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class TimeSerializer
implements SimpleValueSerializer {
    private static SimpleDateFormat zulu = new SimpleDateFormat("HH:mm:ss.SSS'Z'");

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        context.writeString(this.getValueAsString(value, context));
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        StringBuffer buf = new StringBuffer();
        ((Calendar)value).set(0, 0, 0);
        buf.append(zulu.format(((Calendar)value).getTime()));
        return buf.toString();
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

