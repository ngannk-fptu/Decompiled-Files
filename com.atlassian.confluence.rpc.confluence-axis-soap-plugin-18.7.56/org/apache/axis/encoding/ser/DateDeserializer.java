/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.utils.Messages;

public class DateDeserializer
extends SimpleDeserializer {
    private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd");
    private static Calendar calendar = Calendar.getInstance();
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$sql$Date;

    public DateDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object makeValue(String source) {
        Comparable<Date> result;
        boolean bc = false;
        if (source != null) {
            if (source.charAt(0) == '+') {
                source = source.substring(1);
            }
            if (source.charAt(0) == '-') {
                source = source.substring(1);
                bc = true;
            }
            if (source.length() < 10) {
                throw new NumberFormatException(Messages.getMessage("badDate00"));
            }
            if (source.charAt(4) != '-' || source.charAt(7) != '-') {
                throw new NumberFormatException(Messages.getMessage("badDate00"));
            }
        }
        Calendar calendar = DateDeserializer.calendar;
        synchronized (calendar) {
            try {
                result = zulu.parse(source == null ? null : source.substring(0, 10));
            }
            catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }
            if (bc) {
                DateDeserializer.calendar.setTime((Date)result);
                DateDeserializer.calendar.set(0, 0);
                result = DateDeserializer.calendar.getTime();
            }
            if (this.javaType == (class$java$util$Date == null ? (class$java$util$Date = DateDeserializer.class$("java.util.Date")) : class$java$util$Date)) {
                return result;
            }
            if (this.javaType == (class$java$sql$Date == null ? (class$java$sql$Date = DateDeserializer.class$("java.sql.Date")) : class$java$sql$Date)) {
                result = new java.sql.Date(result.getTime());
            } else {
                DateDeserializer.calendar.setTime((Date)result);
                result = DateDeserializer.calendar;
            }
        }
        return result;
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

