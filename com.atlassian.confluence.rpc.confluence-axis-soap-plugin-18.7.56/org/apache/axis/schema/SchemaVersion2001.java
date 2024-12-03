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

public class SchemaVersion2001
implements SchemaVersion {
    public static QName QNAME_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil");
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$util$Calendar;

    SchemaVersion2001() {
    }

    public QName getNilQName() {
        return QNAME_NIL;
    }

    public String getXsiURI() {
        return "http://www.w3.org/2001/XMLSchema-instance";
    }

    public String getXsdURI() {
        return "http://www.w3.org/2001/XMLSchema";
    }

    public void registerSchemaSpecificTypes(TypeMappingImpl tm) {
        tm.register(class$java$util$Date == null ? (class$java$util$Date = SchemaVersion2001.class$("java.util.Date")) : class$java$util$Date, Constants.XSD_DATETIME, new CalendarSerializerFactory(class$java$util$Date == null ? (class$java$util$Date = SchemaVersion2001.class$("java.util.Date")) : class$java$util$Date, Constants.XSD_DATETIME), new CalendarDeserializerFactory(class$java$util$Date == null ? (class$java$util$Date = SchemaVersion2001.class$("java.util.Date")) : class$java$util$Date, Constants.XSD_DATETIME));
        tm.register(class$java$util$Calendar == null ? (class$java$util$Calendar = SchemaVersion2001.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATETIME, new CalendarSerializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = SchemaVersion2001.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATETIME), new CalendarDeserializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = SchemaVersion2001.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATETIME));
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

