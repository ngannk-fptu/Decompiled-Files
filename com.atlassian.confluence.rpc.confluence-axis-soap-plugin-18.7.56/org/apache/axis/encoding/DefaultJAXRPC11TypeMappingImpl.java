/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
import org.apache.axis.encoding.ser.CalendarSerializerFactory;
import org.apache.axis.encoding.ser.DateDeserializerFactory;
import org.apache.axis.encoding.ser.DateSerializerFactory;
import org.apache.axis.encoding.ser.TimeDeserializerFactory;
import org.apache.axis.encoding.ser.TimeSerializerFactory;

public class DefaultJAXRPC11TypeMappingImpl
extends DefaultTypeMappingImpl {
    private static DefaultJAXRPC11TypeMappingImpl tm = null;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$util$Calendar;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$math$BigInteger;

    public static synchronized TypeMappingImpl getSingleton() {
        if (tm == null) {
            tm = new DefaultJAXRPC11TypeMappingImpl();
        }
        return tm;
    }

    protected DefaultJAXRPC11TypeMappingImpl() {
        this.registerXSDTypes();
    }

    private void registerXSDTypes() {
        this.myRegisterSimple(Constants.XSD_UNSIGNEDINT, class$java$lang$Long == null ? (class$java$lang$Long = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.Long")) : class$java$lang$Long);
        this.myRegisterSimple(Constants.XSD_UNSIGNEDINT, Long.TYPE);
        this.myRegisterSimple(Constants.XSD_UNSIGNEDSHORT, class$java$lang$Integer == null ? (class$java$lang$Integer = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.Integer")) : class$java$lang$Integer);
        this.myRegisterSimple(Constants.XSD_UNSIGNEDSHORT, Integer.TYPE);
        this.myRegisterSimple(Constants.XSD_UNSIGNEDBYTE, class$java$lang$Short == null ? (class$java$lang$Short = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.Short")) : class$java$lang$Short);
        this.myRegisterSimple(Constants.XSD_UNSIGNEDBYTE, Short.TYPE);
        this.myRegister(Constants.XSD_DATETIME, class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, new CalendarSerializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATETIME), new CalendarDeserializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATETIME));
        this.myRegister(Constants.XSD_DATE, class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, new DateSerializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATE), new DateDeserializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_DATE));
        this.myRegister(Constants.XSD_TIME, class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, new TimeSerializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_TIME), new TimeDeserializerFactory(class$java$util$Calendar == null ? (class$java$util$Calendar = DefaultJAXRPC11TypeMappingImpl.class$("java.util.Calendar")) : class$java$util$Calendar, Constants.XSD_TIME));
        try {
            this.myRegisterSimple(Constants.XSD_ANYURI, Class.forName("java.net.URI"));
        }
        catch (ClassNotFoundException e) {
            this.myRegisterSimple(Constants.XSD_ANYURI, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        }
        this.myRegisterSimple(Constants.XSD_DURATION, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_YEARMONTH, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_YEAR, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_MONTHDAY, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_DAY, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_MONTH, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_NORMALIZEDSTRING, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_TOKEN, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_LANGUAGE, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_NAME, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_NCNAME, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_ID, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_NMTOKEN, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_NMTOKENS, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_STRING, class$java$lang$String == null ? (class$java$lang$String = DefaultJAXRPC11TypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_NONPOSITIVEINTEGER, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultJAXRPC11TypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
        this.myRegisterSimple(Constants.XSD_NEGATIVEINTEGER, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultJAXRPC11TypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
        this.myRegisterSimple(Constants.XSD_NONNEGATIVEINTEGER, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultJAXRPC11TypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
        this.myRegisterSimple(Constants.XSD_UNSIGNEDLONG, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultJAXRPC11TypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
        this.myRegisterSimple(Constants.XSD_POSITIVEINTEGER, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultJAXRPC11TypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
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

