/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.awt.Image;
import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.xml.rpc.holders.Holder;
import javax.xml.soap.SOAPException;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.OctetStream;
import org.apache.axis.components.image.ImageIO;
import org.apache.axis.components.image.ImageIOFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.types.HexBinary;
import org.apache.axis.utils.ArrayUtil;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class JavaUtils {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$JavaUtils == null ? (class$org$apache$axis$utils$JavaUtils = JavaUtils.class$("org.apache.axis.utils.JavaUtils")) : class$org$apache$axis$utils$JavaUtils).getName());
    public static final char NL = '\n';
    public static final char CR = '\r';
    public static final String LS = System.getProperty("line.separator", new Character('\n').toString());
    static final String[] keywords = new String[]{"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"};
    static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);
    static final char keywordPrefix = '_';
    private static HashMap enumMap = new HashMap();
    private static boolean checkForAttachmentSupport = true;
    private static boolean attachmentSupportEnabled = false;
    static /* synthetic */ Class class$org$apache$axis$utils$JavaUtils;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Character;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Number;
    static /* synthetic */ Class class$org$apache$axis$types$Day;
    static /* synthetic */ Class class$org$apache$axis$types$Duration;
    static /* synthetic */ Class class$org$apache$axis$types$Entities;
    static /* synthetic */ Class class$org$apache$axis$types$Entity;
    static /* synthetic */ Class class$org$apache$axis$types$HexBinary;
    static /* synthetic */ Class class$org$apache$axis$types$Id;
    static /* synthetic */ Class class$org$apache$axis$types$IDRef;
    static /* synthetic */ Class class$org$apache$axis$types$IDRefs;
    static /* synthetic */ Class class$org$apache$axis$types$Language;
    static /* synthetic */ Class class$org$apache$axis$types$Month;
    static /* synthetic */ Class class$org$apache$axis$types$MonthDay;
    static /* synthetic */ Class class$org$apache$axis$types$Name;
    static /* synthetic */ Class class$org$apache$axis$types$NCName;
    static /* synthetic */ Class class$org$apache$axis$types$NegativeInteger;
    static /* synthetic */ Class class$org$apache$axis$types$NMToken;
    static /* synthetic */ Class class$org$apache$axis$types$NMTokens;
    static /* synthetic */ Class class$org$apache$axis$types$NonNegativeInteger;
    static /* synthetic */ Class class$org$apache$axis$types$NonPositiveInteger;
    static /* synthetic */ Class class$org$apache$axis$types$NormalizedString;
    static /* synthetic */ Class class$org$apache$axis$types$PositiveInteger;
    static /* synthetic */ Class class$org$apache$axis$types$Time;
    static /* synthetic */ Class class$org$apache$axis$types$Token;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedByte;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedInt;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedLong;
    static /* synthetic */ Class class$org$apache$axis$types$UnsignedShort;
    static /* synthetic */ Class class$org$apache$axis$types$URI;
    static /* synthetic */ Class class$org$apache$axis$types$Year;
    static /* synthetic */ Class class$org$apache$axis$types$YearMonth;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$util$Calendar;
    static /* synthetic */ Class class$java$sql$Date;
    static /* synthetic */ Class class$java$util$Hashtable;
    static /* synthetic */ Class class$org$apache$axis$attachments$OctetStream;
    static /* synthetic */ Class class$java$awt$Image;
    static /* synthetic */ Class class$javax$xml$transform$Source;
    static /* synthetic */ Class class$javax$activation$DataHandler;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$util$Collection;
    static /* synthetic */ Class class$java$util$List;
    static /* synthetic */ Class class$java$util$Set;
    static /* synthetic */ Class class$java$util$Map;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$javax$xml$rpc$holders$Holder;

    private JavaUtils() {
    }

    public static Class getWrapperClass(Class primitive) {
        if (primitive == Integer.TYPE) {
            return class$java$lang$Integer == null ? (class$java$lang$Integer = JavaUtils.class$("java.lang.Integer")) : class$java$lang$Integer;
        }
        if (primitive == Short.TYPE) {
            return class$java$lang$Short == null ? (class$java$lang$Short = JavaUtils.class$("java.lang.Short")) : class$java$lang$Short;
        }
        if (primitive == Boolean.TYPE) {
            return class$java$lang$Boolean == null ? (class$java$lang$Boolean = JavaUtils.class$("java.lang.Boolean")) : class$java$lang$Boolean;
        }
        if (primitive == Byte.TYPE) {
            return class$java$lang$Byte == null ? (class$java$lang$Byte = JavaUtils.class$("java.lang.Byte")) : class$java$lang$Byte;
        }
        if (primitive == Long.TYPE) {
            return class$java$lang$Long == null ? (class$java$lang$Long = JavaUtils.class$("java.lang.Long")) : class$java$lang$Long;
        }
        if (primitive == Double.TYPE) {
            return class$java$lang$Double == null ? (class$java$lang$Double = JavaUtils.class$("java.lang.Double")) : class$java$lang$Double;
        }
        if (primitive == Float.TYPE) {
            return class$java$lang$Float == null ? (class$java$lang$Float = JavaUtils.class$("java.lang.Float")) : class$java$lang$Float;
        }
        if (primitive == Character.TYPE) {
            return class$java$lang$Character == null ? (class$java$lang$Character = JavaUtils.class$("java.lang.Character")) : class$java$lang$Character;
        }
        return null;
    }

    public static String getWrapper(String primitive) {
        if (primitive.equals("int")) {
            return "Integer";
        }
        if (primitive.equals("short")) {
            return "Short";
        }
        if (primitive.equals("boolean")) {
            return "Boolean";
        }
        if (primitive.equals("byte")) {
            return "Byte";
        }
        if (primitive.equals("long")) {
            return "Long";
        }
        if (primitive.equals("double")) {
            return "Double";
        }
        if (primitive.equals("float")) {
            return "Float";
        }
        if (primitive.equals("char")) {
            return "Character";
        }
        return null;
    }

    public static Class getPrimitiveClass(Class wrapper) {
        if (wrapper == (class$java$lang$Integer == null ? (class$java$lang$Integer = JavaUtils.class$("java.lang.Integer")) : class$java$lang$Integer)) {
            return Integer.TYPE;
        }
        if (wrapper == (class$java$lang$Short == null ? (class$java$lang$Short = JavaUtils.class$("java.lang.Short")) : class$java$lang$Short)) {
            return Short.TYPE;
        }
        if (wrapper == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = JavaUtils.class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
            return Boolean.TYPE;
        }
        if (wrapper == (class$java$lang$Byte == null ? (class$java$lang$Byte = JavaUtils.class$("java.lang.Byte")) : class$java$lang$Byte)) {
            return Byte.TYPE;
        }
        if (wrapper == (class$java$lang$Long == null ? (class$java$lang$Long = JavaUtils.class$("java.lang.Long")) : class$java$lang$Long)) {
            return Long.TYPE;
        }
        if (wrapper == (class$java$lang$Double == null ? (class$java$lang$Double = JavaUtils.class$("java.lang.Double")) : class$java$lang$Double)) {
            return Double.TYPE;
        }
        if (wrapper == (class$java$lang$Float == null ? (class$java$lang$Float = JavaUtils.class$("java.lang.Float")) : class$java$lang$Float)) {
            return Float.TYPE;
        }
        if (wrapper == (class$java$lang$Character == null ? (class$java$lang$Character = JavaUtils.class$("java.lang.Character")) : class$java$lang$Character)) {
            return Character.TYPE;
        }
        return null;
    }

    public static boolean isBasic(Class javaType) {
        return javaType.isPrimitive() || javaType == (class$java$lang$String == null ? (class$java$lang$String = JavaUtils.class$("java.lang.String")) : class$java$lang$String) || javaType == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = JavaUtils.class$("java.lang.Boolean")) : class$java$lang$Boolean) || javaType == (class$java$lang$Float == null ? (class$java$lang$Float = JavaUtils.class$("java.lang.Float")) : class$java$lang$Float) || javaType == (class$java$lang$Double == null ? (class$java$lang$Double = JavaUtils.class$("java.lang.Double")) : class$java$lang$Double) || (class$java$lang$Number == null ? (class$java$lang$Number = JavaUtils.class$("java.lang.Number")) : class$java$lang$Number).isAssignableFrom(javaType) || javaType == (class$org$apache$axis$types$Day == null ? (class$org$apache$axis$types$Day = JavaUtils.class$("org.apache.axis.types.Day")) : class$org$apache$axis$types$Day) || javaType == (class$org$apache$axis$types$Duration == null ? (class$org$apache$axis$types$Duration = JavaUtils.class$("org.apache.axis.types.Duration")) : class$org$apache$axis$types$Duration) || javaType == (class$org$apache$axis$types$Entities == null ? (class$org$apache$axis$types$Entities = JavaUtils.class$("org.apache.axis.types.Entities")) : class$org$apache$axis$types$Entities) || javaType == (class$org$apache$axis$types$Entity == null ? (class$org$apache$axis$types$Entity = JavaUtils.class$("org.apache.axis.types.Entity")) : class$org$apache$axis$types$Entity) || javaType == (class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = JavaUtils.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary) || javaType == (class$org$apache$axis$types$Id == null ? (class$org$apache$axis$types$Id = JavaUtils.class$("org.apache.axis.types.Id")) : class$org$apache$axis$types$Id) || javaType == (class$org$apache$axis$types$IDRef == null ? (class$org$apache$axis$types$IDRef = JavaUtils.class$("org.apache.axis.types.IDRef")) : class$org$apache$axis$types$IDRef) || javaType == (class$org$apache$axis$types$IDRefs == null ? (class$org$apache$axis$types$IDRefs = JavaUtils.class$("org.apache.axis.types.IDRefs")) : class$org$apache$axis$types$IDRefs) || javaType == (class$org$apache$axis$types$Language == null ? (class$org$apache$axis$types$Language = JavaUtils.class$("org.apache.axis.types.Language")) : class$org$apache$axis$types$Language) || javaType == (class$org$apache$axis$types$Month == null ? (class$org$apache$axis$types$Month = JavaUtils.class$("org.apache.axis.types.Month")) : class$org$apache$axis$types$Month) || javaType == (class$org$apache$axis$types$MonthDay == null ? (class$org$apache$axis$types$MonthDay = JavaUtils.class$("org.apache.axis.types.MonthDay")) : class$org$apache$axis$types$MonthDay) || javaType == (class$org$apache$axis$types$Name == null ? (class$org$apache$axis$types$Name = JavaUtils.class$("org.apache.axis.types.Name")) : class$org$apache$axis$types$Name) || javaType == (class$org$apache$axis$types$NCName == null ? (class$org$apache$axis$types$NCName = JavaUtils.class$("org.apache.axis.types.NCName")) : class$org$apache$axis$types$NCName) || javaType == (class$org$apache$axis$types$NegativeInteger == null ? (class$org$apache$axis$types$NegativeInteger = JavaUtils.class$("org.apache.axis.types.NegativeInteger")) : class$org$apache$axis$types$NegativeInteger) || javaType == (class$org$apache$axis$types$NMToken == null ? (class$org$apache$axis$types$NMToken = JavaUtils.class$("org.apache.axis.types.NMToken")) : class$org$apache$axis$types$NMToken) || javaType == (class$org$apache$axis$types$NMTokens == null ? (class$org$apache$axis$types$NMTokens = JavaUtils.class$("org.apache.axis.types.NMTokens")) : class$org$apache$axis$types$NMTokens) || javaType == (class$org$apache$axis$types$NonNegativeInteger == null ? (class$org$apache$axis$types$NonNegativeInteger = JavaUtils.class$("org.apache.axis.types.NonNegativeInteger")) : class$org$apache$axis$types$NonNegativeInteger) || javaType == (class$org$apache$axis$types$NonPositiveInteger == null ? (class$org$apache$axis$types$NonPositiveInteger = JavaUtils.class$("org.apache.axis.types.NonPositiveInteger")) : class$org$apache$axis$types$NonPositiveInteger) || javaType == (class$org$apache$axis$types$NormalizedString == null ? (class$org$apache$axis$types$NormalizedString = JavaUtils.class$("org.apache.axis.types.NormalizedString")) : class$org$apache$axis$types$NormalizedString) || javaType == (class$org$apache$axis$types$PositiveInteger == null ? (class$org$apache$axis$types$PositiveInteger = JavaUtils.class$("org.apache.axis.types.PositiveInteger")) : class$org$apache$axis$types$PositiveInteger) || javaType == (class$org$apache$axis$types$Time == null ? (class$org$apache$axis$types$Time = JavaUtils.class$("org.apache.axis.types.Time")) : class$org$apache$axis$types$Time) || javaType == (class$org$apache$axis$types$Token == null ? (class$org$apache$axis$types$Token = JavaUtils.class$("org.apache.axis.types.Token")) : class$org$apache$axis$types$Token) || javaType == (class$org$apache$axis$types$UnsignedByte == null ? (class$org$apache$axis$types$UnsignedByte = JavaUtils.class$("org.apache.axis.types.UnsignedByte")) : class$org$apache$axis$types$UnsignedByte) || javaType == (class$org$apache$axis$types$UnsignedInt == null ? (class$org$apache$axis$types$UnsignedInt = JavaUtils.class$("org.apache.axis.types.UnsignedInt")) : class$org$apache$axis$types$UnsignedInt) || javaType == (class$org$apache$axis$types$UnsignedLong == null ? (class$org$apache$axis$types$UnsignedLong = JavaUtils.class$("org.apache.axis.types.UnsignedLong")) : class$org$apache$axis$types$UnsignedLong) || javaType == (class$org$apache$axis$types$UnsignedShort == null ? (class$org$apache$axis$types$UnsignedShort = JavaUtils.class$("org.apache.axis.types.UnsignedShort")) : class$org$apache$axis$types$UnsignedShort) || javaType == (class$org$apache$axis$types$URI == null ? (class$org$apache$axis$types$URI = JavaUtils.class$("org.apache.axis.types.URI")) : class$org$apache$axis$types$URI) || javaType == (class$org$apache$axis$types$Year == null ? (class$org$apache$axis$types$Year = JavaUtils.class$("org.apache.axis.types.Year")) : class$org$apache$axis$types$Year) || javaType == (class$org$apache$axis$types$YearMonth == null ? (class$org$apache$axis$types$YearMonth = JavaUtils.class$("org.apache.axis.types.YearMonth")) : class$org$apache$axis$types$YearMonth);
    }

    public static Object convert(Object arg, Class destClass) {
        Class hintClass;
        Object newArg;
        if (destClass == null) {
            return arg;
        }
        Class argHeldType = null;
        if (arg != null) {
            argHeldType = JavaUtils.getHolderValueType(arg.getClass());
        }
        if (arg != null && argHeldType == null && destClass.isAssignableFrom(arg.getClass())) {
            return arg;
        }
        if (log.isDebugEnabled()) {
            String clsName = "null";
            if (arg != null) {
                clsName = arg.getClass().getName();
            }
            log.debug((Object)Messages.getMessage("convert00", clsName, destClass.getName()));
        }
        Object destValue = null;
        if (arg instanceof ConvertCache && (destValue = ((ConvertCache)arg).getConvertedValue(destClass)) != null) {
            return destValue;
        }
        Class destHeldType = JavaUtils.getHolderValueType(destClass);
        if (arg instanceof HexBinary && destClass == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B)) {
            return ((HexBinary)arg).getBytes();
        }
        if (arg instanceof byte[] && destClass == (class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = JavaUtils.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary)) {
            return new HexBinary((byte[])arg);
        }
        if (arg instanceof Calendar && destClass == (class$java$util$Date == null ? (class$java$util$Date = JavaUtils.class$("java.util.Date")) : class$java$util$Date)) {
            return ((Calendar)arg).getTime();
        }
        if (arg instanceof java.util.Date && destClass == (class$java$util$Calendar == null ? (class$java$util$Calendar = JavaUtils.class$("java.util.Calendar")) : class$java$util$Calendar)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((java.util.Date)arg);
            return calendar;
        }
        if (arg instanceof Calendar && destClass == (class$java$sql$Date == null ? (class$java$sql$Date = JavaUtils.class$("java.sql.Date")) : class$java$sql$Date)) {
            return new Date(((Calendar)arg).getTime().getTime());
        }
        if (arg instanceof HashMap && destClass == (class$java$util$Hashtable == null ? (class$java$util$Hashtable = JavaUtils.class$("java.util.Hashtable")) : class$java$util$Hashtable)) {
            return new Hashtable((HashMap)arg);
        }
        if (JavaUtils.isAttachmentSupported() && (arg instanceof InputStream || arg instanceof AttachmentPart || arg instanceof DataHandler)) {
            try {
                String destName = destClass.getName();
                if (destClass == (class$java$lang$String == null ? (class$java$lang$String = JavaUtils.class$("java.lang.String")) : class$java$lang$String) || destClass == (class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = JavaUtils.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream) || destClass == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B) || destClass == (class$java$awt$Image == null ? (class$java$awt$Image = JavaUtils.class$("java.awt.Image")) : class$java$awt$Image) || destClass == (class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = JavaUtils.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source) || destClass == (class$javax$activation$DataHandler == null ? (class$javax$activation$DataHandler = JavaUtils.class$("javax.activation.DataHandler")) : class$javax$activation$DataHandler) || destName.equals("javax.mail.internet.MimeMultipart")) {
                    DataHandler handler = null;
                    if (arg instanceof AttachmentPart) {
                        handler = ((AttachmentPart)arg).getDataHandler();
                    } else if (arg instanceof DataHandler) {
                        handler = (DataHandler)arg;
                    }
                    if (destClass == (class$java$awt$Image == null ? (class$java$awt$Image = JavaUtils.class$("java.awt.Image")) : class$java$awt$Image)) {
                        InputStream is = (InputStream)handler.getContent();
                        if (is.available() == 0) {
                            return null;
                        }
                        ImageIO imageIO = ImageIOFactory.getImageIO();
                        if (imageIO != null) {
                            return JavaUtils.getImageFromStream(is);
                        }
                        log.info((Object)Messages.getMessage("needImageIO"));
                        return arg;
                    }
                    if (destClass == (class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = JavaUtils.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source)) {
                        return new StreamSource(new StringReader((String)handler.getContent()));
                    }
                    if (destClass == (class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = JavaUtils.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream) || destClass == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B)) {
                        InputStream in = null;
                        in = arg instanceof InputStream ? (InputStream)arg : (InputStream)handler.getContent();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int byte1 = -1;
                        while ((byte1 = in.read()) != -1) {
                            baos.write(byte1);
                        }
                        return new OctetStream(baos.toByteArray());
                    }
                    if (destClass == (class$javax$activation$DataHandler == null ? (class$javax$activation$DataHandler = JavaUtils.class$("javax.activation.DataHandler")) : class$javax$activation$DataHandler)) {
                        return handler;
                    }
                    return handler.getContent();
                }
            }
            catch (IOException ioe) {
            }
            catch (SOAPException se) {
                // empty catch block
            }
        }
        if (arg != null && destClass.isArray() && !destClass.getComponentType().equals(class$java$lang$Object == null ? (class$java$lang$Object = JavaUtils.class$("java.lang.Object")) : class$java$lang$Object) && destClass.getComponentType().isAssignableFrom(arg.getClass())) {
            Object array = Array.newInstance(destClass.getComponentType(), 1);
            Array.set(array, 0, arg);
            return array;
        }
        if (arg != null && destClass.isArray() && ((newArg = ArrayUtil.convertObjectToArray(arg, destClass)) == null || newArg != ArrayUtil.NON_CONVERTABLE && newArg != arg)) {
            return newArg;
        }
        if (arg != null && arg.getClass().isArray() && (newArg = ArrayUtil.convertArrayToObject(arg, destClass)) != null) {
            return newArg;
        }
        if (!(arg instanceof Collection) && (arg == null || !arg.getClass().isArray()) && (destHeldType == null && argHeldType == null || destHeldType != null && argHeldType != null)) {
            return arg;
        }
        if (destHeldType != null) {
            newArg = JavaUtils.convert(arg, destHeldType);
            Object argHolder = null;
            try {
                argHolder = destClass.newInstance();
                JavaUtils.setHolderValue(argHolder, newArg);
                return argHolder;
            }
            catch (Exception e) {
                return arg;
            }
        }
        if (argHeldType != null) {
            try {
                newArg = JavaUtils.getHolderValue(arg);
                return JavaUtils.convert(newArg, destClass);
            }
            catch (HolderException e) {
                return arg;
            }
        }
        if (arg instanceof ConvertCache && ((ConvertCache)arg).getDestClass() != destClass && (hintClass = ((ConvertCache)arg).getDestClass()) != null && hintClass.isArray() && destClass.isArray() && destClass.isAssignableFrom(hintClass) && (destValue = ((ConvertCache)arg).getConvertedValue(destClass = hintClass)) != null) {
            return destValue;
        }
        if (arg == null) {
            return arg;
        }
        int length = 0;
        length = arg.getClass().isArray() ? Array.getLength(arg) : ((Collection)arg).size();
        if (destClass.isArray()) {
            if (destClass.getComponentType().isPrimitive()) {
                Object array = Array.newInstance(destClass.getComponentType(), length);
                if (arg.getClass().isArray()) {
                    for (int i = 0; i < length; ++i) {
                        Array.set(array, i, Array.get(arg, i));
                    }
                } else {
                    int idx = 0;
                    Iterator i = ((Collection)arg).iterator();
                    while (i.hasNext()) {
                        Array.set(array, idx++, i.next());
                    }
                }
                destValue = array;
            } else {
                Object[] array;
                try {
                    array = (Object[])Array.newInstance(destClass.getComponentType(), length);
                }
                catch (Exception e) {
                    return arg;
                }
                if (arg.getClass().isArray()) {
                    for (int i = 0; i < length; ++i) {
                        array[i] = JavaUtils.convert(Array.get(arg, i), destClass.getComponentType());
                    }
                } else {
                    int idx = 0;
                    Iterator i = ((Collection)arg).iterator();
                    while (i.hasNext()) {
                        array[idx++] = JavaUtils.convert(i.next(), destClass.getComponentType());
                    }
                }
                destValue = array;
            }
        } else if ((class$java$util$Collection == null ? (class$java$util$Collection = JavaUtils.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(destClass)) {
            Collection newList = null;
            try {
                newList = destClass == (class$java$util$Collection == null ? (class$java$util$Collection = JavaUtils.class$("java.util.Collection")) : class$java$util$Collection) || destClass == (class$java$util$List == null ? (class$java$util$List = JavaUtils.class$("java.util.List")) : class$java$util$List) ? new ArrayList() : (destClass == (class$java$util$Set == null ? (class$java$util$Set = JavaUtils.class$("java.util.Set")) : class$java$util$Set) ? new HashSet() : (Collection)destClass.newInstance());
            }
            catch (Exception e) {
                return arg;
            }
            if (arg.getClass().isArray()) {
                for (int j = 0; j < length; ++j) {
                    newList.add(Array.get(arg, j));
                }
            } else {
                Iterator j = ((Collection)arg).iterator();
                while (j.hasNext()) {
                    newList.add(j.next());
                }
            }
            destValue = newList;
        } else {
            destValue = arg;
        }
        if (arg instanceof ConvertCache) {
            ((ConvertCache)arg).setConvertedValue(destClass, destValue);
        }
        return destValue;
    }

    public static boolean isConvertable(Object obj, Class dest) {
        return JavaUtils.isConvertable(obj, dest, false);
    }

    public static boolean isConvertable(Object obj, Class dest, boolean isEncoded) {
        Class<?> src = null;
        if (obj != null) {
            src = obj instanceof Class ? (Class<?>)obj : obj.getClass();
        } else if (!dest.isPrimitive()) {
            return true;
        }
        if (dest == null) {
            return false;
        }
        if (src != null) {
            if (dest.isAssignableFrom(src)) {
                return true;
            }
            if ((class$java$util$Map == null ? (class$java$util$Map = JavaUtils.class$("java.util.Map")) : class$java$util$Map).isAssignableFrom(dest) && (class$java$util$Map == null ? (class$java$util$Map = JavaUtils.class$("java.util.Map")) : class$java$util$Map).isAssignableFrom(src)) {
                return true;
            }
            if (JavaUtils.getWrapperClass(src) == dest) {
                return true;
            }
            if (JavaUtils.getWrapperClass(dest) == src) {
                return true;
            }
            if (((class$java$util$Collection == null ? (class$java$util$Collection = JavaUtils.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(src) || src.isArray()) && ((class$java$util$Collection == null ? (class$java$util$Collection = JavaUtils.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(dest) || dest.isArray()) && (src.getComponentType() == (class$java$lang$Object == null ? (class$java$lang$Object = JavaUtils.class$("java.lang.Object")) : class$java$lang$Object) || src.getComponentType() == null || dest.getComponentType() == (class$java$lang$Object == null ? (class$java$lang$Object = JavaUtils.class$("java.lang.Object")) : class$java$lang$Object) || dest.getComponentType() == null || JavaUtils.isConvertable(src.getComponentType(), dest.getComponentType()))) {
                return true;
            }
            if (!isEncoded && dest.isArray() && dest.getComponentType().isAssignableFrom(src)) {
                return true;
            }
            if (src == (class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = JavaUtils.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary) && dest == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B) || src == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B) && dest == (class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = JavaUtils.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary)) {
                return true;
            }
            if ((class$java$util$Calendar == null ? (class$java$util$Calendar = JavaUtils.class$("java.util.Calendar")) : class$java$util$Calendar).isAssignableFrom(src) && dest == (class$java$util$Date == null ? (class$java$util$Date = JavaUtils.class$("java.util.Date")) : class$java$util$Date)) {
                return true;
            }
            if ((class$java$util$Date == null ? (class$java$util$Date = JavaUtils.class$("java.util.Date")) : class$java$util$Date).isAssignableFrom(src) && dest == (class$java$util$Calendar == null ? (class$java$util$Calendar = JavaUtils.class$("java.util.Calendar")) : class$java$util$Calendar)) {
                return true;
            }
            if ((class$java$util$Calendar == null ? (class$java$util$Calendar = JavaUtils.class$("java.util.Calendar")) : class$java$util$Calendar).isAssignableFrom(src) && dest == (class$java$sql$Date == null ? (class$java$sql$Date = JavaUtils.class$("java.sql.Date")) : class$java$sql$Date)) {
                return true;
            }
        }
        Class destHeld = JavaUtils.getHolderValueType(dest);
        if (src == null) {
            return destHeld != null;
        }
        if (destHeld != null && (destHeld.isAssignableFrom(src) || JavaUtils.isConvertable(src, destHeld))) {
            return true;
        }
        Class srcHeld = JavaUtils.getHolderValueType(src);
        if (srcHeld != null && (dest.isAssignableFrom(srcHeld) || JavaUtils.isConvertable(srcHeld, dest))) {
            return true;
        }
        if (dest.getName().equals("javax.activation.DataHandler")) {
            String name = src.getName();
            if (src == (class$java$lang$String == null ? (class$java$lang$String = JavaUtils.class$("java.lang.String")) : class$java$lang$String) || src == (class$java$awt$Image == null ? (class$java$awt$Image = JavaUtils.class$("java.awt.Image")) : class$java$awt$Image) || src == (class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = JavaUtils.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream) || name.equals("javax.mail.internet.MimeMultipart") || name.equals("javax.xml.transform.Source")) {
                return true;
            }
        }
        if (src.getName().equals("javax.activation.DataHandler")) {
            if (dest == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B)) {
                return true;
            }
            if (dest.isArray() && dest.getComponentType() == (array$B == null ? (array$B = JavaUtils.class$("[B")) : array$B)) {
                return true;
            }
        }
        if (dest.getName().equals("javax.activation.DataHandler")) {
            if (src == (array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = JavaUtils.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object)) {
                return true;
            }
            if (src.isArray() && src.getComponentType() == (array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = JavaUtils.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object)) {
                return true;
            }
        }
        if (obj instanceof InputStream && dest == (class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = JavaUtils.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream)) {
            return true;
        }
        if (src.isPrimitive()) {
            return JavaUtils.isConvertable(JavaUtils.getWrapperClass(src), dest);
        }
        if (dest.isArray() && ArrayUtil.isConvertable(src, dest)) {
            return true;
        }
        return src.isArray() && ArrayUtil.isConvertable(src, dest);
    }

    public static Image getImageFromStream(InputStream is) {
        try {
            return ImageIOFactory.getImageIO().loadImage(is);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static boolean isJavaId(String id) {
        if (id == null || id.equals("") || JavaUtils.isJavaKeyword(id)) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(id.charAt(0))) {
            return false;
        }
        for (int i = 1; i < id.length(); ++i) {
            if (Character.isJavaIdentifierPart(id.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isJavaKeyword(String keyword) {
        return Arrays.binarySearch(keywords, keyword, englishCollator) >= 0;
    }

    public static String makeNonJavaKeyword(String keyword) {
        return '_' + keyword;
    }

    public static String getLoadableClassName(String text) {
        if (text == null || text.indexOf("[") < 0 || text.charAt(0) == '[') {
            return text;
        }
        String className = text.substring(0, text.indexOf("["));
        className = className.equals("byte") ? "B" : (className.equals("char") ? "C" : (className.equals("double") ? "D" : (className.equals("float") ? "F" : (className.equals("int") ? "I" : (className.equals("long") ? "J" : (className.equals("short") ? "S" : (className.equals("boolean") ? "Z" : "L" + className + ";")))))));
        int i = text.indexOf("]");
        while (i > 0) {
            className = "[" + className;
            i = text.indexOf("]", i + 1);
        }
        return className;
    }

    public static String getTextClassName(String text) {
        int index;
        if (text == null || text.indexOf("[") != 0) {
            return text;
        }
        String className = "";
        for (index = 0; index < text.length() && text.charAt(index) == '['; ++index) {
            className = className + "[]";
        }
        if (index < text.length()) {
            className = text.charAt(index) == 'B' ? "byte" + className : (text.charAt(index) == 'C' ? "char" + className : (text.charAt(index) == 'D' ? "double" + className : (text.charAt(index) == 'F' ? "float" + className : (text.charAt(index) == 'I' ? "int" + className : (text.charAt(index) == 'J' ? "long" + className : (text.charAt(index) == 'S' ? "short" + className : (text.charAt(index) == 'Z' ? "boolean" + className : text.substring(index + 1, text.indexOf(";")) + className)))))));
        }
        return className;
    }

    public static String xmlNameToJava(String name) {
        int i;
        if (name == null || name.equals("")) {
            return name;
        }
        char[] nameArray = name.toCharArray();
        int nameLen = name.length();
        StringBuffer result = new StringBuffer(nameLen);
        boolean wordStart = false;
        for (i = 0; i < nameLen && (JavaUtils.isPunctuation(nameArray[i]) || !Character.isJavaIdentifierStart(nameArray[i])); ++i) {
        }
        if (i < nameLen) {
            result.append(nameArray[i]);
            wordStart = !Character.isLetter(nameArray[i]) && nameArray[i] != "_".charAt(0);
        } else if (Character.isJavaIdentifierPart(nameArray[0])) {
            result.append("_" + nameArray[0]);
        } else {
            result.append("_" + nameArray.length);
        }
        ++i;
        while (i < nameLen) {
            char c = nameArray[i];
            if (JavaUtils.isPunctuation(c) || !Character.isJavaIdentifierPart(c)) {
                wordStart = true;
            } else {
                if (wordStart && Character.isLowerCase(c)) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(c);
                }
                wordStart = !Character.isLetter(c) && c != "_".charAt(0);
            }
            ++i;
        }
        String newName = result.toString();
        if (Character.isUpperCase(newName.charAt(0))) {
            newName = Introspector.decapitalize(newName);
        }
        if (JavaUtils.isJavaKeyword(newName)) {
            newName = JavaUtils.makeNonJavaKeyword(newName);
        }
        return newName;
    }

    private static boolean isPunctuation(char c) {
        return '-' == c || '.' == c || ':' == c || '\u00b7' == c || '\u0387' == c || '\u06dd' == c || '\u06de' == c;
    }

    public static final String replace(String name, String oldT, String newT) {
        if (name == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer(name.length() * 2);
        int len = oldT.length();
        try {
            int start = 0;
            int i = name.indexOf(oldT, start);
            while (i >= 0) {
                sb.append(name.substring(start, i));
                sb.append(newT);
                start = i + len;
                i = name.indexOf(oldT, start);
            }
            if (start < name.length()) {
                sb.append(name.substring(start));
            }
        }
        catch (NullPointerException e) {
            // empty catch block
        }
        return new String(sb);
    }

    public static Class getHolderValueType(Class type) {
        if (type != null) {
            Field field;
            Class<?>[] intf = type.getInterfaces();
            boolean isHolder = false;
            for (int i = 0; i < intf.length && !isHolder; ++i) {
                if (intf[i] != (class$javax$xml$rpc$holders$Holder == null ? JavaUtils.class$("javax.xml.rpc.holders.Holder") : class$javax$xml$rpc$holders$Holder)) continue;
                isHolder = true;
            }
            if (!isHolder) {
                return null;
            }
            try {
                field = type.getField("value");
            }
            catch (Exception e) {
                field = null;
            }
            if (field != null) {
                return field.getType();
            }
        }
        return null;
    }

    public static Object getHolderValue(Object holder) throws HolderException {
        if (!(holder instanceof Holder)) {
            throw new HolderException(Messages.getMessage("badHolder00"));
        }
        try {
            Field valueField = holder.getClass().getField("value");
            return valueField.get(holder);
        }
        catch (Exception e) {
            throw new HolderException(Messages.getMessage("exception01", e.getMessage()));
        }
    }

    public static void setHolderValue(Object holder, Object value) throws HolderException {
        if (!(holder instanceof Holder)) {
            throw new HolderException(Messages.getMessage("badHolder00"));
        }
        try {
            Field valueField = holder.getClass().getField("value");
            if (valueField.getType().isPrimitive()) {
                if (value != null) {
                    valueField.set(holder, value);
                }
            } else {
                valueField.set(holder, value);
            }
        }
        catch (Exception e) {
            throw new HolderException(Messages.getMessage("exception01", e.getMessage()));
        }
    }

    public static boolean isEnumClass(Class cls) {
        Boolean b = (Boolean)enumMap.get(cls);
        if (b == null) {
            b = JavaUtils.isEnumClassSub(cls) ? Boolean.TRUE : Boolean.FALSE;
            enumMap.put(cls, b);
        }
        return b;
    }

    private static boolean isEnumClassSub(Class cls) {
        try {
            Method[] methods = cls.getMethods();
            Method getValueMethod = null;
            Method fromValueMethod = null;
            Method setValueMethod = null;
            Method fromStringMethod = null;
            for (int i = 0; i < methods.length; ++i) {
                String name = methods[i].getName();
                if (name.equals("getValue") && methods[i].getParameterTypes().length == 0) {
                    getValueMethod = methods[i];
                    continue;
                }
                if (name.equals("fromString")) {
                    Class<?>[] params = methods[i].getParameterTypes();
                    if (params.length != 1 || params[0] != (class$java$lang$String == null ? JavaUtils.class$("java.lang.String") : class$java$lang$String)) continue;
                    fromStringMethod = methods[i];
                    continue;
                }
                if (name.equals("fromValue") && methods[i].getParameterTypes().length == 1) {
                    fromValueMethod = methods[i];
                    continue;
                }
                if (!name.equals("setValue") || methods[i].getParameterTypes().length != 1) continue;
                setValueMethod = methods[i];
            }
            if (null != getValueMethod && null != fromStringMethod) {
                return null == setValueMethod || setValueMethod.getParameterTypes().length != 1 || getValueMethod.getReturnType() != setValueMethod.getParameterTypes()[0];
            }
            return false;
        }
        catch (SecurityException e) {
            return false;
        }
    }

    public static String stackToString(Throwable e) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static final boolean isTrue(String value) {
        return !JavaUtils.isFalseExplicitly(value);
    }

    public static final boolean isTrueExplicitly(String value) {
        return value != null && (value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes"));
    }

    public static final boolean isTrueExplicitly(Object value, boolean defaultVal) {
        if (value == null) {
            return defaultVal;
        }
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof Integer) {
            return (Integer)value != 0;
        }
        if (value instanceof String) {
            return JavaUtils.isTrueExplicitly((String)value);
        }
        return true;
    }

    public static final boolean isTrueExplicitly(Object value) {
        return JavaUtils.isTrueExplicitly(value, false);
    }

    public static final boolean isTrue(Object value, boolean defaultVal) {
        return !JavaUtils.isFalseExplicitly(value, !defaultVal);
    }

    public static final boolean isTrue(Object value) {
        return JavaUtils.isTrue(value, false);
    }

    public static final boolean isFalse(String value) {
        return JavaUtils.isFalseExplicitly(value);
    }

    public static final boolean isFalseExplicitly(String value) {
        return value == null || value.equalsIgnoreCase("false") || value.equals("0") || value.equalsIgnoreCase("no");
    }

    public static final boolean isFalseExplicitly(Object value, boolean defaultVal) {
        if (value == null) {
            return defaultVal;
        }
        if (value instanceof Boolean) {
            return (Boolean)value == false;
        }
        if (value instanceof Integer) {
            return (Integer)value == 0;
        }
        if (value instanceof String) {
            return JavaUtils.isFalseExplicitly((String)value);
        }
        return false;
    }

    public static final boolean isFalseExplicitly(Object value) {
        return JavaUtils.isFalseExplicitly(value, true);
    }

    public static final boolean isFalse(Object value, boolean defaultVal) {
        return JavaUtils.isFalseExplicitly(value, defaultVal);
    }

    public static final boolean isFalse(Object value) {
        return JavaUtils.isFalse(value, true);
    }

    public static String mimeToJava(String mime) {
        if ("image/gif".equals(mime) || "image/jpeg".equals(mime)) {
            return "java.awt.Image";
        }
        if ("text/plain".equals(mime)) {
            return "java.lang.String";
        }
        if ("text/xml".equals(mime) || "application/xml".equals(mime)) {
            return "javax.xml.transform.Source";
        }
        if ("application/octet-stream".equals(mime) || "application/octetstream".equals(mime)) {
            return "org.apache.axis.attachments.OctetStream";
        }
        if (mime != null && mime.startsWith("multipart/")) {
            return "javax.mail.internet.MimeMultipart";
        }
        return "javax.activation.DataHandler";
    }

    public static synchronized boolean isAttachmentSupported() {
        if (checkForAttachmentSupport) {
            checkForAttachmentSupport = false;
            try {
                ClassUtils.forName("javax.activation.DataHandler");
                ClassUtils.forName("javax.mail.internet.MimeMultipart");
                attachmentSupportEnabled = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            log.debug((Object)(Messages.getMessage("attachEnabled") + "  " + attachmentSupportEnabled));
            if (!attachmentSupportEnabled) {
                log.warn((Object)Messages.getMessage("attachDisabled"));
            }
        }
        return attachmentSupportEnabled;
    }

    public static String getUniqueValue(Collection values, String initValue) {
        int end;
        if (!values.contains(initValue)) {
            return initValue;
        }
        StringBuffer unqVal = new StringBuffer(initValue);
        int beg = unqVal.length();
        while (Character.isDigit(unqVal.charAt(beg - 1))) {
            --beg;
        }
        if (beg == unqVal.length()) {
            unqVal.append('1');
        }
        int cur = end = unqVal.length() - 1;
        while (values.contains(unqVal.toString())) {
            if (unqVal.charAt(cur) < '9') {
                unqVal.setCharAt(cur, (char)(unqVal.charAt(cur) + '\u0001'));
                continue;
            }
            while (cur-- > beg) {
                if (unqVal.charAt(cur) >= '9') continue;
                unqVal.setCharAt(cur, (char)(unqVal.charAt(cur) + '\u0001'));
                break;
            }
            if (cur < beg) {
                unqVal.insert(++cur, '1');
                ++end;
            }
            while (cur < end) {
                unqVal.setCharAt(++cur, '0');
            }
        }
        return unqVal.toString();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static class HolderException
    extends Exception {
        public HolderException(String msg) {
            super(msg);
        }
    }

    public static interface ConvertCache {
        public void setConvertedValue(Class var1, Object var2);

        public Object getConvertedValue(Class var1);

        public Class getDestClass();
    }
}

