/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.schema;

import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
import org.apache.axis.encoding.ser.CalendarSerializerFactory;
import org.apache.axis.schema.SchemaVersion;

public class SchemaVersion2000
implements SchemaVersion {
    public static QName QNAME_NIL = new QName("http://www.w3.org/2000/10/XMLSchema-instance", "null");
    static /* synthetic */ Class class$java$util$Calendar;

    SchemaVersion2000() {
    }

    public QName getNilQName() {
        return QNAME_NIL;
    }

    public String getXsiURI() {
        return "http://www.w3.org/2000/10/XMLSchema-instance";
    }

    public String getXsdURI() {
        return "http://www.w3.org/2000/10/XMLSchema";
    }

    public void registerSchemaSpecificTypes(TypeMappingImpl tm) {
        tm.register(class$java$util$Calendar == null ? (class$java$util$Calendar = SchemaVersion2000.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_TIMEINSTANT2000, new CalendarSerializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = SchemaVersion2000.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_TIMEINSTANT2000), new CalendarDeserializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = SchemaVersion2000.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_TIMEINSTANT2000));
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

