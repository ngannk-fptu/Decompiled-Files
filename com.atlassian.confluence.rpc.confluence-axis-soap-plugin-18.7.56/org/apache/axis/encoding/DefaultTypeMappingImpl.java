/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.DeserializerFactory;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMappingDelegate;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.Base64DeserializerFactory;
import org.apache.axis.encoding.ser.Base64SerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.DateDeserializerFactory;
import org.apache.axis.encoding.ser.DateSerializerFactory;
import org.apache.axis.encoding.ser.DocumentDeserializerFactory;
import org.apache.axis.encoding.ser.DocumentSerializerFactory;
import org.apache.axis.encoding.ser.ElementDeserializerFactory;
import org.apache.axis.encoding.ser.ElementSerializerFactory;
import org.apache.axis.encoding.ser.HexDeserializerFactory;
import org.apache.axis.encoding.ser.HexSerializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.axis.encoding.ser.MapDeserializerFactory;
import org.apache.axis.encoding.ser.MapSerializerFactory;
import org.apache.axis.encoding.ser.QNameDeserializerFactory;
import org.apache.axis.encoding.ser.QNameSerializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleSerializerFactory;
import org.apache.axis.encoding.ser.VectorDeserializerFactory;
import org.apache.axis.encoding.ser.VectorSerializerFactory;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

public class DefaultTypeMappingImpl
extends TypeMappingImpl {
    private static DefaultTypeMappingImpl tm = null;
    private boolean inInitMappings = false;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$types$HexBinary;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$math$BigInteger;
    static /* synthetic */ Class class$java$math$BigDecimal;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$sql$Date;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$org$apache$axis$types$Time;
    static /* synthetic */ Class class$org$apache$axis$types$YearMonth;
    static /* synthetic */ Class class$org$apache$axis$types$Year;
    static /* synthetic */ Class class$org$apache$axis$types$Month;
    static /* synthetic */ Class class$org$apache$axis$types$Day;
    static /* synthetic */ Class class$org$apache$axis$types$MonthDay;
    static /* synthetic */ Class class$java$util$Hashtable;
    static /* synthetic */ Class class$java$util$Map;
    static /* synthetic */ Class class$java$util$HashMap;
    static /* synthetic */ Class class$org$w3c$dom$Element;
    static /* synthetic */ Class class$org$w3c$dom$Document;
    static /* synthetic */ Class class$java$util$Vector;
    static /* synthetic */ Class class$java$awt$Image;
    static /* synthetic */ Class class$javax$mail$internet$MimeMultipart;
    static /* synthetic */ Class class$javax$xml$transform$Source;
    static /* synthetic */ Class class$org$apache$axis$attachments$OctetStream;
    static /* synthetic */ Class class$javax$activation$DataHandler;
    static /* synthetic */ Class class$org$apache$axis$types$Token;
    static /* synthetic */ Class class$org$apache$axis$types$NormalizedString;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedLong;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedInt;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedShort;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedByte;
    static /* synthetic */ Class class$org$apache$axis$types$NonNegativeInteger;
    static /* synthetic */ Class class$org$apache$axis$types$NegativeInteger;
    static /* synthetic */ Class class$org$apache$axis$types$PositiveInteger;
    static /* synthetic */ Class class$org$apache$axis$types$NonPositiveInteger;
    static /* synthetic */ Class class$org$apache$axis$types$Name;
    static /* synthetic */ Class class$org$apache$axis$types$NCName;
    static /* synthetic */ Class class$org$apache$axis$types$Id;
    static /* synthetic */ Class class$org$apache$axis$types$Language;
    static /* synthetic */ Class class$org$apache$axis$types$NMToken;
    static /* synthetic */ Class class$org$apache$axis$types$NMTokens;
    static /* synthetic */ Class class$org$apache$axis$types$Notation;
    static /* synthetic */ Class class$org$apache$axis$types$Entity;
    static /* synthetic */ Class class$org$apache$axis$types$Entities;
    static /* synthetic */ Class class$org$apache$axis$types$IDRef;
    static /* synthetic */ Class class$org$apache$axis$types$IDRefs;
    static /* synthetic */ Class class$org$apache$axis$types$Duration;
    static /* synthetic */ Class class$org$apache$axis$types$URI;
    static /* synthetic */ Class class$org$apache$axis$types$Schema;
    static /* synthetic */ Class class$java$util$ArrayList;

    public static synchronized TypeMappingDelegate getSingletonDelegate() {
        if (tm == null) {
            tm = new DefaultTypeMappingImpl();
        }
        return new TypeMappingDelegate(tm);
    }

    protected DefaultTypeMappingImpl() {
        this.initMappings();
    }

    protected DefaultTypeMappingImpl(boolean noMappings) {
        if (!noMappings) {
            this.initMappings();
        }
    }

    protected void initMappings() {
        this.inInitMappings = true;
        if (JavaUtils.isAttachmentSupported()) {
            this.myRegister(Constants.MIME_PLAINTEXT, class$java$lang$String == null ? (class$java$lang$String = DefaultTypeMappingImpl.class$("java.lang.String")) : class$java$lang$String, new JAFDataHandlerSerializerFactory(class$java$lang$String == null ? (class$java$lang$String = DefaultTypeMappingImpl.class$("java.lang.String")) : class$java$lang$String, Constants.MIME_PLAINTEXT), new JAFDataHandlerDeserializerFactory(class$java$lang$String == null ? (class$java$lang$String = DefaultTypeMappingImpl.class$("java.lang.String")) : class$java$lang$String, Constants.MIME_PLAINTEXT));
        }
        this.myRegister(Constants.XSD_HEXBIN, class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = DefaultTypeMappingImpl.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary, new HexSerializerFactory(class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = DefaultTypeMappingImpl.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary, Constants.XSD_HEXBIN), new HexDeserializerFactory(class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = DefaultTypeMappingImpl.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary, Constants.XSD_HEXBIN));
        this.myRegister(Constants.XSD_HEXBIN, array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, new HexSerializerFactory(array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, Constants.XSD_HEXBIN), new HexDeserializerFactory(array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, Constants.XSD_HEXBIN));
        this.myRegister(Constants.XSD_BYTE, array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, new ArraySerializerFactory(), null);
        this.myRegister(Constants.XSD_BASE64, array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, new Base64SerializerFactory(array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, Constants.XSD_BASE64), new Base64DeserializerFactory(array$B == null ? (array$B = DefaultTypeMappingImpl.class$("[B")) : array$B, Constants.XSD_BASE64));
        this.myRegisterSimple(Constants.XSD_ANYSIMPLETYPE, class$java$lang$String == null ? (class$java$lang$String = DefaultTypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_STRING, class$java$lang$String == null ? (class$java$lang$String = DefaultTypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.XSD_BOOLEAN, class$java$lang$Boolean == null ? (class$java$lang$Boolean = DefaultTypeMappingImpl.class$("java.lang.Boolean")) : class$java$lang$Boolean);
        this.myRegisterSimple(Constants.XSD_DOUBLE, class$java$lang$Double == null ? (class$java$lang$Double = DefaultTypeMappingImpl.class$("java.lang.Double")) : class$java$lang$Double);
        this.myRegisterSimple(Constants.XSD_FLOAT, class$java$lang$Float == null ? (class$java$lang$Float = DefaultTypeMappingImpl.class$("java.lang.Float")) : class$java$lang$Float);
        this.myRegisterSimple(Constants.XSD_INT, class$java$lang$Integer == null ? (class$java$lang$Integer = DefaultTypeMappingImpl.class$("java.lang.Integer")) : class$java$lang$Integer);
        this.myRegisterSimple(Constants.XSD_INTEGER, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultTypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
        this.myRegisterSimple(Constants.XSD_DECIMAL, class$java$math$BigDecimal == null ? (class$java$math$BigDecimal = DefaultTypeMappingImpl.class$("java.math.BigDecimal")) : class$java$math$BigDecimal);
        this.myRegisterSimple(Constants.XSD_LONG, class$java$lang$Long == null ? (class$java$lang$Long = DefaultTypeMappingImpl.class$("java.lang.Long")) : class$java$lang$Long);
        this.myRegisterSimple(Constants.XSD_SHORT, class$java$lang$Short == null ? (class$java$lang$Short = DefaultTypeMappingImpl.class$("java.lang.Short")) : class$java$lang$Short);
        this.myRegisterSimple(Constants.XSD_BYTE, class$java$lang$Byte == null ? (class$java$lang$Byte = DefaultTypeMappingImpl.class$("java.lang.Byte")) : class$java$lang$Byte);
        this.myRegisterSimple(Constants.XSD_BOOLEAN, Boolean.TYPE);
        this.myRegisterSimple(Constants.XSD_DOUBLE, Double.TYPE);
        this.myRegisterSimple(Constants.XSD_FLOAT, Float.TYPE);
        this.myRegisterSimple(Constants.XSD_INT, Integer.TYPE);
        this.myRegisterSimple(Constants.XSD_LONG, Long.TYPE);
        this.myRegisterSimple(Constants.XSD_SHORT, Short.TYPE);
        this.myRegisterSimple(Constants.XSD_BYTE, Byte.TYPE);
        this.myRegister(Constants.XSD_QNAME, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = DefaultTypeMappingImpl.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName, new QNameSerializerFactory(class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = DefaultTypeMappingImpl.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName, Constants.XSD_QNAME), new QNameDeserializerFactory(class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = DefaultTypeMappingImpl.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName, Constants.XSD_QNAME));
        this.myRegister(Constants.XSD_ANYTYPE, class$java$lang$Object == null ? (class$java$lang$Object = DefaultTypeMappingImpl.class$("java.lang.Object")) : class$java$lang$Object, null, null);
        this.myRegister(Constants.XSD_DATE, class$java$sql$Date == null ? (class$java$sql$Date = DefaultTypeMappingImpl.class$("java.sql.Date")) : class$java$sql$Date, new DateSerializerFactory(class$java$sql$Date == null ? (class$java$sql$Date = DefaultTypeMappingImpl.class$("java.sql.Date")) : class$java$sql$Date, Constants.XSD_DATE), new DateDeserializerFactory(class$java$sql$Date == null ? (class$java$sql$Date = DefaultTypeMappingImpl.class$("java.sql.Date")) : class$java$sql$Date, Constants.XSD_DATE));
        this.myRegister(Constants.XSD_DATE, class$java$util$Date == null ? (class$java$util$Date = DefaultTypeMappingImpl.class$("java.util.Date")) : class$java$util$Date, new DateSerializerFactory(class$java$util$Date == null ? (class$java$util$Date = DefaultTypeMappingImpl.class$("java.util.Date")) : class$java$util$Date, Constants.XSD_DATE), new DateDeserializerFactory(class$java$util$Date == null ? (class$java$util$Date = DefaultTypeMappingImpl.class$("java.util.Date")) : class$java$util$Date, Constants.XSD_DATE));
        this.myRegister(Constants.XSD_TIME, class$org$apache$axis$types$Time == null ? (class$org$apache$axis$types$Time = DefaultTypeMappingImpl.class$("org.apache.axis.types.Time")) : class$org$apache$axis$types$Time, new SimpleSerializerFactory(class$org$apache$axis$types$Time == null ? (class$org$apache$axis$types$Time = DefaultTypeMappingImpl.class$("org.apache.axis.types.Time")) : class$org$apache$axis$types$Time, Constants.XSD_TIME), new SimpleDeserializerFactory(class$org$apache$axis$types$Time == null ? (class$org$apache$axis$types$Time = DefaultTypeMappingImpl.class$("org.apache.axis.types.Time")) : class$org$apache$axis$types$Time, Constants.XSD_TIME));
        this.myRegister(Constants.XSD_YEARMONTH, class$org$apache$axis$types$YearMonth == null ? (class$org$apache$axis$types$YearMonth = DefaultTypeMappingImpl.class$("org.apache.axis.types.YearMonth")) : class$org$apache$axis$types$YearMonth, new SimpleSerializerFactory(class$org$apache$axis$types$YearMonth == null ? (class$org$apache$axis$types$YearMonth = DefaultTypeMappingImpl.class$("org.apache.axis.types.YearMonth")) : class$org$apache$axis$types$YearMonth, Constants.XSD_YEARMONTH), new SimpleDeserializerFactory(class$org$apache$axis$types$YearMonth == null ? (class$org$apache$axis$types$YearMonth = DefaultTypeMappingImpl.class$("org.apache.axis.types.YearMonth")) : class$org$apache$axis$types$YearMonth, Constants.XSD_YEARMONTH));
        this.myRegister(Constants.XSD_YEAR, class$org$apache$axis$types$Year == null ? (class$org$apache$axis$types$Year = DefaultTypeMappingImpl.class$("org.apache.axis.types.Year")) : class$org$apache$axis$types$Year, new SimpleSerializerFactory(class$org$apache$axis$types$Year == null ? (class$org$apache$axis$types$Year = DefaultTypeMappingImpl.class$("org.apache.axis.types.Year")) : class$org$apache$axis$types$Year, Constants.XSD_YEAR), new SimpleDeserializerFactory(class$org$apache$axis$types$Year == null ? (class$org$apache$axis$types$Year = DefaultTypeMappingImpl.class$("org.apache.axis.types.Year")) : class$org$apache$axis$types$Year, Constants.XSD_YEAR));
        this.myRegister(Constants.XSD_MONTH, class$org$apache$axis$types$Month == null ? (class$org$apache$axis$types$Month = DefaultTypeMappingImpl.class$("org.apache.axis.types.Month")) : class$org$apache$axis$types$Month, new SimpleSerializerFactory(class$org$apache$axis$types$Month == null ? (class$org$apache$axis$types$Month = DefaultTypeMappingImpl.class$("org.apache.axis.types.Month")) : class$org$apache$axis$types$Month, Constants.XSD_MONTH), new SimpleDeserializerFactory(class$org$apache$axis$types$Month == null ? (class$org$apache$axis$types$Month = DefaultTypeMappingImpl.class$("org.apache.axis.types.Month")) : class$org$apache$axis$types$Month, Constants.XSD_MONTH));
        this.myRegister(Constants.XSD_DAY, class$org$apache$axis$types$Day == null ? (class$org$apache$axis$types$Day = DefaultTypeMappingImpl.class$("org.apache.axis.types.Day")) : class$org$apache$axis$types$Day, new SimpleSerializerFactory(class$org$apache$axis$types$Day == null ? (class$org$apache$axis$types$Day = DefaultTypeMappingImpl.class$("org.apache.axis.types.Day")) : class$org$apache$axis$types$Day, Constants.XSD_DAY), new SimpleDeserializerFactory(class$org$apache$axis$types$Day == null ? (class$org$apache$axis$types$Day = DefaultTypeMappingImpl.class$("org.apache.axis.types.Day")) : class$org$apache$axis$types$Day, Constants.XSD_DAY));
        this.myRegister(Constants.XSD_MONTHDAY, class$org$apache$axis$types$MonthDay == null ? (class$org$apache$axis$types$MonthDay = DefaultTypeMappingImpl.class$("org.apache.axis.types.MonthDay")) : class$org$apache$axis$types$MonthDay, new SimpleSerializerFactory(class$org$apache$axis$types$MonthDay == null ? (class$org$apache$axis$types$MonthDay = DefaultTypeMappingImpl.class$("org.apache.axis.types.MonthDay")) : class$org$apache$axis$types$MonthDay, Constants.XSD_MONTHDAY), new SimpleDeserializerFactory(class$org$apache$axis$types$MonthDay == null ? (class$org$apache$axis$types$MonthDay = DefaultTypeMappingImpl.class$("org.apache.axis.types.MonthDay")) : class$org$apache$axis$types$MonthDay, Constants.XSD_MONTHDAY));
        this.myRegister(Constants.SOAP_MAP, class$java$util$Hashtable == null ? (class$java$util$Hashtable = DefaultTypeMappingImpl.class$("java.util.Hashtable")) : class$java$util$Hashtable, new MapSerializerFactory(class$java$util$Hashtable == null ? (class$java$util$Hashtable = DefaultTypeMappingImpl.class$("java.util.Hashtable")) : class$java$util$Hashtable, Constants.SOAP_MAP), null);
        this.myRegister(Constants.SOAP_MAP, class$java$util$Map == null ? (class$java$util$Map = DefaultTypeMappingImpl.class$("java.util.Map")) : class$java$util$Map, new MapSerializerFactory(class$java$util$Map == null ? (class$java$util$Map = DefaultTypeMappingImpl.class$("java.util.Map")) : class$java$util$Map, Constants.SOAP_MAP), null);
        this.myRegister(Constants.SOAP_MAP, class$java$util$HashMap == null ? (class$java$util$HashMap = DefaultTypeMappingImpl.class$("java.util.HashMap")) : class$java$util$HashMap, new MapSerializerFactory(class$java$util$Map == null ? (class$java$util$Map = DefaultTypeMappingImpl.class$("java.util.Map")) : class$java$util$Map, Constants.SOAP_MAP), new MapDeserializerFactory(class$java$util$HashMap == null ? (class$java$util$HashMap = DefaultTypeMappingImpl.class$("java.util.HashMap")) : class$java$util$HashMap, Constants.SOAP_MAP));
        this.myRegister(Constants.SOAP_ELEMENT, class$org$w3c$dom$Element == null ? (class$org$w3c$dom$Element = DefaultTypeMappingImpl.class$("org.w3c.dom.Element")) : class$org$w3c$dom$Element, new ElementSerializerFactory(), new ElementDeserializerFactory());
        this.myRegister(Constants.SOAP_DOCUMENT, class$org$w3c$dom$Document == null ? (class$org$w3c$dom$Document = DefaultTypeMappingImpl.class$("org.w3c.dom.Document")) : class$org$w3c$dom$Document, new DocumentSerializerFactory(), new DocumentDeserializerFactory());
        this.myRegister(Constants.SOAP_VECTOR, class$java$util$Vector == null ? (class$java$util$Vector = DefaultTypeMappingImpl.class$("java.util.Vector")) : class$java$util$Vector, new VectorSerializerFactory(class$java$util$Vector == null ? (class$java$util$Vector = DefaultTypeMappingImpl.class$("java.util.Vector")) : class$java$util$Vector, Constants.SOAP_VECTOR), new VectorDeserializerFactory(class$java$util$Vector == null ? (class$java$util$Vector = DefaultTypeMappingImpl.class$("java.util.Vector")) : class$java$util$Vector, Constants.SOAP_VECTOR));
        if (JavaUtils.isAttachmentSupported()) {
            this.myRegister(Constants.MIME_IMAGE, class$java$awt$Image == null ? (class$java$awt$Image = DefaultTypeMappingImpl.class$("java.awt.Image")) : class$java$awt$Image, new JAFDataHandlerSerializerFactory(class$java$awt$Image == null ? (class$java$awt$Image = DefaultTypeMappingImpl.class$("java.awt.Image")) : class$java$awt$Image, Constants.MIME_IMAGE), new JAFDataHandlerDeserializerFactory(class$java$awt$Image == null ? (class$java$awt$Image = DefaultTypeMappingImpl.class$("java.awt.Image")) : class$java$awt$Image, Constants.MIME_IMAGE));
            this.myRegister(Constants.MIME_MULTIPART, class$javax$mail$internet$MimeMultipart == null ? (class$javax$mail$internet$MimeMultipart = DefaultTypeMappingImpl.class$("javax.mail.internet.MimeMultipart")) : class$javax$mail$internet$MimeMultipart, new JAFDataHandlerSerializerFactory(class$javax$mail$internet$MimeMultipart == null ? (class$javax$mail$internet$MimeMultipart = DefaultTypeMappingImpl.class$("javax.mail.internet.MimeMultipart")) : class$javax$mail$internet$MimeMultipart, Constants.MIME_MULTIPART), new JAFDataHandlerDeserializerFactory(class$javax$mail$internet$MimeMultipart == null ? (class$javax$mail$internet$MimeMultipart = DefaultTypeMappingImpl.class$("javax.mail.internet.MimeMultipart")) : class$javax$mail$internet$MimeMultipart, Constants.MIME_MULTIPART));
            this.myRegister(Constants.MIME_SOURCE, class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = DefaultTypeMappingImpl.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source, new JAFDataHandlerSerializerFactory(class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = DefaultTypeMappingImpl.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source, Constants.MIME_SOURCE), new JAFDataHandlerDeserializerFactory(class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = DefaultTypeMappingImpl.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source, Constants.MIME_SOURCE));
            this.myRegister(Constants.MIME_OCTETSTREAM, class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = DefaultTypeMappingImpl.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream, new JAFDataHandlerSerializerFactory(class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = DefaultTypeMappingImpl.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream, Constants.MIME_OCTETSTREAM), new JAFDataHandlerDeserializerFactory(class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = DefaultTypeMappingImpl.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream, Constants.MIME_OCTETSTREAM));
            this.myRegister(Constants.MIME_DATA_HANDLER, class$javax$activation$DataHandler == null ? (class$javax$activation$DataHandler = DefaultTypeMappingImpl.class$("javax.activation.DataHandler")) : class$javax$activation$DataHandler, new JAFDataHandlerSerializerFactory(), new JAFDataHandlerDeserializerFactory());
        }
        this.myRegister(Constants.XSD_TOKEN, class$org$apache$axis$types$Token == null ? (class$org$apache$axis$types$Token = DefaultTypeMappingImpl.class$("org.apache.axis.types.Token")) : class$org$apache$axis$types$Token, new SimpleSerializerFactory(class$org$apache$axis$types$Token == null ? (class$org$apache$axis$types$Token = DefaultTypeMappingImpl.class$("org.apache.axis.types.Token")) : class$org$apache$axis$types$Token, Constants.XSD_TOKEN), new SimpleDeserializerFactory(class$org$apache$axis$types$Token == null ? (class$org$apache$axis$types$Token = DefaultTypeMappingImpl.class$("org.apache.axis.types.Token")) : class$org$apache$axis$types$Token, Constants.XSD_TOKEN));
        this.myRegister(Constants.XSD_NORMALIZEDSTRING, class$org$apache$axis$types$NormalizedString == null ? (class$org$apache$axis$types$NormalizedString = DefaultTypeMappingImpl.class$("org.apache.axis.types.NormalizedString")) : class$org$apache$axis$types$NormalizedString, new SimpleSerializerFactory(class$org$apache$axis$types$NormalizedString == null ? (class$org$apache$axis$types$NormalizedString = DefaultTypeMappingImpl.class$("org.apache.axis.types.NormalizedString")) : class$org$apache$axis$types$NormalizedString, Constants.XSD_NORMALIZEDSTRING), new SimpleDeserializerFactory(class$org$apache$axis$types$NormalizedString == null ? (class$org$apache$axis$types$NormalizedString = DefaultTypeMappingImpl.class$("org.apache.axis.types.NormalizedString")) : class$org$apache$axis$types$NormalizedString, Constants.XSD_NORMALIZEDSTRING));
        this.myRegister(Constants.XSD_UNSIGNEDLONG, class$org$apache$axis$types$UnsignedLong == null ? (class$org$apache$axis$types$UnsignedLong = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedLong")) : class$org$apache$axis$types$UnsignedLong, new SimpleSerializerFactory(class$org$apache$axis$types$UnsignedLong == null ? (class$org$apache$axis$types$UnsignedLong = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedLong")) : class$org$apache$axis$types$UnsignedLong, Constants.XSD_UNSIGNEDLONG), new SimpleDeserializerFactory(class$org$apache$axis$types$UnsignedLong == null ? (class$org$apache$axis$types$UnsignedLong = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedLong")) : class$org$apache$axis$types$UnsignedLong, Constants.XSD_UNSIGNEDLONG));
        this.myRegister(Constants.XSD_UNSIGNEDINT, class$org$apache$axis$types$UnsignedInt == null ? (class$org$apache$axis$types$UnsignedInt = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedInt")) : class$org$apache$axis$types$UnsignedInt, new SimpleSerializerFactory(class$org$apache$axis$types$UnsignedInt == null ? (class$org$apache$axis$types$UnsignedInt = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedInt")) : class$org$apache$axis$types$UnsignedInt, Constants.XSD_UNSIGNEDINT), new SimpleDeserializerFactory(class$org$apache$axis$types$UnsignedInt == null ? (class$org$apache$axis$types$UnsignedInt = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedInt")) : class$org$apache$axis$types$UnsignedInt, Constants.XSD_UNSIGNEDINT));
        this.myRegister(Constants.XSD_UNSIGNEDSHORT, class$org$apache$axis$types$UnsignedShort == null ? (class$org$apache$axis$types$UnsignedShort = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedShort")) : class$org$apache$axis$types$UnsignedShort, new SimpleSerializerFactory(class$org$apache$axis$types$UnsignedShort == null ? (class$org$apache$axis$types$UnsignedShort = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedShort")) : class$org$apache$axis$types$UnsignedShort, Constants.XSD_UNSIGNEDSHORT), new SimpleDeserializerFactory(class$org$apache$axis$types$UnsignedShort == null ? (class$org$apache$axis$types$UnsignedShort = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedShort")) : class$org$apache$axis$types$UnsignedShort, Constants.XSD_UNSIGNEDSHORT));
        this.myRegister(Constants.XSD_UNSIGNEDBYTE, class$org$apache$axis$types$UnsignedByte == null ? (class$org$apache$axis$types$UnsignedByte = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedByte")) : class$org$apache$axis$types$UnsignedByte, new SimpleSerializerFactory(class$org$apache$axis$types$UnsignedByte == null ? (class$org$apache$axis$types$UnsignedByte = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedByte")) : class$org$apache$axis$types$UnsignedByte, Constants.XSD_UNSIGNEDBYTE), new SimpleDeserializerFactory(class$org$apache$axis$types$UnsignedByte == null ? (class$org$apache$axis$types$UnsignedByte = DefaultTypeMappingImpl.class$("org.apache.axis.types.UnsignedByte")) : class$org$apache$axis$types$UnsignedByte, Constants.XSD_UNSIGNEDBYTE));
        this.myRegister(Constants.XSD_NONNEGATIVEINTEGER, class$org$apache$axis$types$NonNegativeInteger == null ? (class$org$apache$axis$types$NonNegativeInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NonNegativeInteger")) : class$org$apache$axis$types$NonNegativeInteger, new SimpleSerializerFactory(class$org$apache$axis$types$NonNegativeInteger == null ? (class$org$apache$axis$types$NonNegativeInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NonNegativeInteger")) : class$org$apache$axis$types$NonNegativeInteger, Constants.XSD_NONNEGATIVEINTEGER), new SimpleDeserializerFactory(class$org$apache$axis$types$NonNegativeInteger == null ? (class$org$apache$axis$types$NonNegativeInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NonNegativeInteger")) : class$org$apache$axis$types$NonNegativeInteger, Constants.XSD_NONNEGATIVEINTEGER));
        this.myRegister(Constants.XSD_NEGATIVEINTEGER, class$org$apache$axis$types$NegativeInteger == null ? (class$org$apache$axis$types$NegativeInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NegativeInteger")) : class$org$apache$axis$types$NegativeInteger, new SimpleSerializerFactory(class$org$apache$axis$types$NegativeInteger == null ? (class$org$apache$axis$types$NegativeInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NegativeInteger")) : class$org$apache$axis$types$NegativeInteger, Constants.XSD_NEGATIVEINTEGER), new SimpleDeserializerFactory(class$org$apache$axis$types$NegativeInteger == null ? (class$org$apache$axis$types$NegativeInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NegativeInteger")) : class$org$apache$axis$types$NegativeInteger, Constants.XSD_NEGATIVEINTEGER));
        this.myRegister(Constants.XSD_POSITIVEINTEGER, class$org$apache$axis$types$PositiveInteger == null ? (class$org$apache$axis$types$PositiveInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.PositiveInteger")) : class$org$apache$axis$types$PositiveInteger, new SimpleSerializerFactory(class$org$apache$axis$types$PositiveInteger == null ? (class$org$apache$axis$types$PositiveInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.PositiveInteger")) : class$org$apache$axis$types$PositiveInteger, Constants.XSD_POSITIVEINTEGER), new SimpleDeserializerFactory(class$org$apache$axis$types$PositiveInteger == null ? (class$org$apache$axis$types$PositiveInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.PositiveInteger")) : class$org$apache$axis$types$PositiveInteger, Constants.XSD_POSITIVEINTEGER));
        this.myRegister(Constants.XSD_NONPOSITIVEINTEGER, class$org$apache$axis$types$NonPositiveInteger == null ? (class$org$apache$axis$types$NonPositiveInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NonPositiveInteger")) : class$org$apache$axis$types$NonPositiveInteger, new SimpleSerializerFactory(class$org$apache$axis$types$NonPositiveInteger == null ? (class$org$apache$axis$types$NonPositiveInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NonPositiveInteger")) : class$org$apache$axis$types$NonPositiveInteger, Constants.XSD_NONPOSITIVEINTEGER), new SimpleDeserializerFactory(class$org$apache$axis$types$NonPositiveInteger == null ? (class$org$apache$axis$types$NonPositiveInteger = DefaultTypeMappingImpl.class$("org.apache.axis.types.NonPositiveInteger")) : class$org$apache$axis$types$NonPositiveInteger, Constants.XSD_NONPOSITIVEINTEGER));
        this.myRegister(Constants.XSD_NAME, class$org$apache$axis$types$Name == null ? (class$org$apache$axis$types$Name = DefaultTypeMappingImpl.class$("org.apache.axis.types.Name")) : class$org$apache$axis$types$Name, new SimpleSerializerFactory(class$org$apache$axis$types$Name == null ? (class$org$apache$axis$types$Name = DefaultTypeMappingImpl.class$("org.apache.axis.types.Name")) : class$org$apache$axis$types$Name, Constants.XSD_NAME), new SimpleDeserializerFactory(class$org$apache$axis$types$Name == null ? (class$org$apache$axis$types$Name = DefaultTypeMappingImpl.class$("org.apache.axis.types.Name")) : class$org$apache$axis$types$Name, Constants.XSD_NAME));
        this.myRegister(Constants.XSD_NCNAME, class$org$apache$axis$types$NCName == null ? (class$org$apache$axis$types$NCName = DefaultTypeMappingImpl.class$("org.apache.axis.types.NCName")) : class$org$apache$axis$types$NCName, new SimpleSerializerFactory(class$org$apache$axis$types$NCName == null ? (class$org$apache$axis$types$NCName = DefaultTypeMappingImpl.class$("org.apache.axis.types.NCName")) : class$org$apache$axis$types$NCName, Constants.XSD_NCNAME), new SimpleDeserializerFactory(class$org$apache$axis$types$NCName == null ? (class$org$apache$axis$types$NCName = DefaultTypeMappingImpl.class$("org.apache.axis.types.NCName")) : class$org$apache$axis$types$NCName, Constants.XSD_NCNAME));
        this.myRegister(Constants.XSD_ID, class$org$apache$axis$types$Id == null ? (class$org$apache$axis$types$Id = DefaultTypeMappingImpl.class$("org.apache.axis.types.Id")) : class$org$apache$axis$types$Id, new SimpleSerializerFactory(class$org$apache$axis$types$Id == null ? (class$org$apache$axis$types$Id = DefaultTypeMappingImpl.class$("org.apache.axis.types.Id")) : class$org$apache$axis$types$Id, Constants.XSD_ID), new SimpleDeserializerFactory(class$org$apache$axis$types$Id == null ? (class$org$apache$axis$types$Id = DefaultTypeMappingImpl.class$("org.apache.axis.types.Id")) : class$org$apache$axis$types$Id, Constants.XSD_ID));
        this.myRegister(Constants.XML_LANG, class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = DefaultTypeMappingImpl.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language, new SimpleSerializerFactory(class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = DefaultTypeMappingImpl.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language, Constants.XML_LANG), new SimpleDeserializerFactory(class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = DefaultTypeMappingImpl.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language, Constants.XML_LANG));
        this.myRegister(Constants.XSD_LANGUAGE, class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = DefaultTypeMappingImpl.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language, new SimpleSerializerFactory(class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = DefaultTypeMappingImpl.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language, Constants.XSD_LANGUAGE), new SimpleDeserializerFactory(class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = DefaultTypeMappingImpl.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language, Constants.XSD_LANGUAGE));
        this.myRegister(Constants.XSD_NMTOKEN, class$org$apache$axis$types$NMToken == null ? (class$org$apache$axis$types$NMToken = DefaultTypeMappingImpl.class$("org.apache.axis.types.NMToken")) : class$org$apache$axis$types$NMToken, new SimpleSerializerFactory(class$org$apache$axis$types$NMToken == null ? (class$org$apache$axis$types$NMToken = DefaultTypeMappingImpl.class$("org.apache.axis.types.NMToken")) : class$org$apache$axis$types$NMToken, Constants.XSD_NMTOKEN), new SimpleDeserializerFactory(class$org$apache$axis$types$NMToken == null ? (class$org$apache$axis$types$NMToken = DefaultTypeMappingImpl.class$("org.apache.axis.types.NMToken")) : class$org$apache$axis$types$NMToken, Constants.XSD_NMTOKEN));
        this.myRegister(Constants.XSD_NMTOKENS, class$org$apache$axis$types$NMTokens == null ? (class$org$apache$axis$types$NMTokens = DefaultTypeMappingImpl.class$("org.apache.axis.types.NMTokens")) : class$org$apache$axis$types$NMTokens, new SimpleSerializerFactory(class$org$apache$axis$types$NMTokens == null ? (class$org$apache$axis$types$NMTokens = DefaultTypeMappingImpl.class$("org.apache.axis.types.NMTokens")) : class$org$apache$axis$types$NMTokens, Constants.XSD_NMTOKENS), new SimpleDeserializerFactory(class$org$apache$axis$types$NMTokens == null ? (class$org$apache$axis$types$NMTokens = DefaultTypeMappingImpl.class$("org.apache.axis.types.NMTokens")) : class$org$apache$axis$types$NMTokens, Constants.XSD_NMTOKENS));
        this.myRegister(Constants.XSD_NOTATION, class$org$apache$axis$types$Notation == null ? (class$org$apache$axis$types$Notation = DefaultTypeMappingImpl.class$("org.apache.axis.types.Notation")) : class$org$apache$axis$types$Notation, new BeanSerializerFactory(class$org$apache$axis$types$Notation == null ? (class$org$apache$axis$types$Notation = DefaultTypeMappingImpl.class$("org.apache.axis.types.Notation")) : class$org$apache$axis$types$Notation, Constants.XSD_NOTATION), new BeanDeserializerFactory(class$org$apache$axis$types$Notation == null ? (class$org$apache$axis$types$Notation = DefaultTypeMappingImpl.class$("org.apache.axis.types.Notation")) : class$org$apache$axis$types$Notation, Constants.XSD_NOTATION));
        this.myRegister(Constants.XSD_ENTITY, class$org$apache$axis$types$Entity == null ? (class$org$apache$axis$types$Entity = DefaultTypeMappingImpl.class$("org.apache.axis.types.Entity")) : class$org$apache$axis$types$Entity, new SimpleSerializerFactory(class$org$apache$axis$types$Entity == null ? (class$org$apache$axis$types$Entity = DefaultTypeMappingImpl.class$("org.apache.axis.types.Entity")) : class$org$apache$axis$types$Entity, Constants.XSD_ENTITY), new SimpleDeserializerFactory(class$org$apache$axis$types$Entity == null ? (class$org$apache$axis$types$Entity = DefaultTypeMappingImpl.class$("org.apache.axis.types.Entity")) : class$org$apache$axis$types$Entity, Constants.XSD_ENTITY));
        this.myRegister(Constants.XSD_ENTITIES, class$org$apache$axis$types$Entities == null ? (class$org$apache$axis$types$Entities = DefaultTypeMappingImpl.class$("org.apache.axis.types.Entities")) : class$org$apache$axis$types$Entities, new SimpleSerializerFactory(class$org$apache$axis$types$Entities == null ? (class$org$apache$axis$types$Entities = DefaultTypeMappingImpl.class$("org.apache.axis.types.Entities")) : class$org$apache$axis$types$Entities, Constants.XSD_ENTITIES), new SimpleDeserializerFactory(class$org$apache$axis$types$Entities == null ? (class$org$apache$axis$types$Entities = DefaultTypeMappingImpl.class$("org.apache.axis.types.Entities")) : class$org$apache$axis$types$Entities, Constants.XSD_ENTITIES));
        this.myRegister(Constants.XSD_IDREF, class$org$apache$axis$types$IDRef == null ? (class$org$apache$axis$types$IDRef = DefaultTypeMappingImpl.class$("org.apache.axis.types.IDRef")) : class$org$apache$axis$types$IDRef, new SimpleSerializerFactory(class$org$apache$axis$types$IDRef == null ? (class$org$apache$axis$types$IDRef = DefaultTypeMappingImpl.class$("org.apache.axis.types.IDRef")) : class$org$apache$axis$types$IDRef, Constants.XSD_IDREF), new SimpleDeserializerFactory(class$org$apache$axis$types$IDRef == null ? (class$org$apache$axis$types$IDRef = DefaultTypeMappingImpl.class$("org.apache.axis.types.IDRef")) : class$org$apache$axis$types$IDRef, Constants.XSD_IDREF));
        this.myRegister(Constants.XSD_IDREFS, class$org$apache$axis$types$IDRefs == null ? (class$org$apache$axis$types$IDRefs = DefaultTypeMappingImpl.class$("org.apache.axis.types.IDRefs")) : class$org$apache$axis$types$IDRefs, new SimpleSerializerFactory(class$org$apache$axis$types$IDRefs == null ? (class$org$apache$axis$types$IDRefs = DefaultTypeMappingImpl.class$("org.apache.axis.types.IDRefs")) : class$org$apache$axis$types$IDRefs, Constants.XSD_IDREFS), new SimpleDeserializerFactory(class$org$apache$axis$types$IDRefs == null ? (class$org$apache$axis$types$IDRefs = DefaultTypeMappingImpl.class$("org.apache.axis.types.IDRefs")) : class$org$apache$axis$types$IDRefs, Constants.XSD_IDREFS));
        this.myRegister(Constants.XSD_DURATION, class$org$apache$axis$types$Duration == null ? (class$org$apache$axis$types$Duration = DefaultTypeMappingImpl.class$("org.apache.axis.types.Duration")) : class$org$apache$axis$types$Duration, new SimpleSerializerFactory(class$org$apache$axis$types$Duration == null ? (class$org$apache$axis$types$Duration = DefaultTypeMappingImpl.class$("org.apache.axis.types.Duration")) : class$org$apache$axis$types$Duration, Constants.XSD_DURATION), new SimpleDeserializerFactory(class$org$apache$axis$types$Duration == null ? (class$org$apache$axis$types$Duration = DefaultTypeMappingImpl.class$("org.apache.axis.types.Duration")) : class$org$apache$axis$types$Duration, Constants.XSD_DURATION));
        this.myRegister(Constants.XSD_ANYURI, class$org$apache$axis$types$URI == null ? (class$org$apache$axis$types$URI = DefaultTypeMappingImpl.class$("org.apache.axis.types.URI")) : class$org$apache$axis$types$URI, new SimpleSerializerFactory(class$org$apache$axis$types$URI == null ? (class$org$apache$axis$types$URI = DefaultTypeMappingImpl.class$("org.apache.axis.types.URI")) : class$org$apache$axis$types$URI, Constants.XSD_ANYURI), new SimpleDeserializerFactory(class$org$apache$axis$types$URI == null ? (class$org$apache$axis$types$URI = DefaultTypeMappingImpl.class$("org.apache.axis.types.URI")) : class$org$apache$axis$types$URI, Constants.XSD_ANYURI));
        this.myRegister(Constants.XSD_SCHEMA, class$org$apache$axis$types$Schema == null ? (class$org$apache$axis$types$Schema = DefaultTypeMappingImpl.class$("org.apache.axis.types.Schema")) : class$org$apache$axis$types$Schema, new BeanSerializerFactory(class$org$apache$axis$types$Schema == null ? (class$org$apache$axis$types$Schema = DefaultTypeMappingImpl.class$("org.apache.axis.types.Schema")) : class$org$apache$axis$types$Schema, Constants.XSD_SCHEMA), new BeanDeserializerFactory(class$org$apache$axis$types$Schema == null ? (class$org$apache$axis$types$Schema = DefaultTypeMappingImpl.class$("org.apache.axis.types.Schema")) : class$org$apache$axis$types$Schema, Constants.XSD_SCHEMA));
        this.myRegister(Constants.SOAP_ARRAY, class$java$util$ArrayList == null ? (class$java$util$ArrayList = DefaultTypeMappingImpl.class$("java.util.ArrayList")) : class$java$util$ArrayList, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        SchemaVersion.SCHEMA_1999.registerSchemaSpecificTypes(this);
        SchemaVersion.SCHEMA_2000.registerSchemaSpecificTypes(this);
        SchemaVersion.SCHEMA_2001.registerSchemaSpecificTypes(this);
        this.inInitMappings = false;
    }

    protected void myRegisterSimple(QName xmlType, Class javaType) {
        SimpleSerializerFactory sf = new SimpleSerializerFactory(javaType, xmlType);
        SimpleDeserializerFactory df = null;
        if (javaType != (class$java$lang$Object == null ? (class$java$lang$Object = DefaultTypeMappingImpl.class$("java.lang.Object")) : class$java$lang$Object)) {
            df = new SimpleDeserializerFactory(javaType, xmlType);
        }
        this.myRegister(xmlType, javaType, sf, df);
    }

    protected void myRegister(QName xmlType, Class javaType, SerializerFactory sf, DeserializerFactory df) {
        try {
            if (xmlType.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
                for (int i = 0; i < Constants.URIS_SCHEMA_XSD.length; ++i) {
                    QName qName = new QName(Constants.URIS_SCHEMA_XSD[i], xmlType.getLocalPart());
                    super.internalRegister(javaType, qName, sf, df);
                }
            } else if (xmlType.getNamespaceURI().equals(Constants.URI_DEFAULT_SOAP_ENC)) {
                for (int i = 0; i < Constants.URIS_SOAP_ENC.length; ++i) {
                    QName qName = new QName(Constants.URIS_SOAP_ENC[i], xmlType.getLocalPart());
                    super.internalRegister(javaType, qName, sf, df);
                }
            } else {
                super.internalRegister(javaType, xmlType, sf, df);
            }
        }
        catch (JAXRPCException e) {
            // empty catch block
        }
    }

    public void register(Class javaType, QName xmlType, javax.xml.rpc.encoding.SerializerFactory sf, DeserializerFactory dsf) throws JAXRPCException {
        super.register(javaType, xmlType, sf, dsf);
    }

    public void removeSerializer(Class javaType, QName xmlType) throws JAXRPCException {
        throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
    }

    public void removeDeserializer(Class javaType, QName xmlType) throws JAXRPCException {
        throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
    }

    public void setSupportedEncodings(String[] namespaceURIs) {
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

