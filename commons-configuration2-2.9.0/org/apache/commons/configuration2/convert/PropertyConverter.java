/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.convert;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public final class PropertyConverter {
    private static final String HEX_PREFIX = "0x";
    private static final int HEX_RADIX = 16;
    private static final String BIN_PREFIX = "0b";
    private static final int BIN_RADIX = 2;
    private static final Class<?>[] CONSTR_ARGS = new Class[]{String.class};
    private static final String INTERNET_ADDRESS_CLASSNAME_JAVAX = "javax.mail.internet.InternetAddress";
    private static final String INTERNET_ADDRESS_CLASSNAME_JAKARTA = "jakarta.mail.internet.InternetAddress";

    private PropertyConverter() {
    }

    public static Object to(Class<?> cls, Object value, DefaultConversionHandler convHandler) throws ConversionException {
        if (cls.isInstance(value)) {
            return value;
        }
        if (String.class.equals(cls)) {
            return String.valueOf(value);
        }
        if (Boolean.class.equals(cls) || Boolean.TYPE.equals(cls)) {
            return PropertyConverter.toBoolean(value);
        }
        if (Character.class.equals(cls) || Character.TYPE.equals(cls)) {
            return PropertyConverter.toCharacter(value);
        }
        if (Number.class.isAssignableFrom(cls) || cls.isPrimitive()) {
            if (Integer.class.equals(cls) || Integer.TYPE.equals(cls)) {
                return PropertyConverter.toInteger(value);
            }
            if (Long.class.equals(cls) || Long.TYPE.equals(cls)) {
                return PropertyConverter.toLong(value);
            }
            if (Byte.class.equals(cls) || Byte.TYPE.equals(cls)) {
                return PropertyConverter.toByte(value);
            }
            if (Short.class.equals(cls) || Short.TYPE.equals(cls)) {
                return PropertyConverter.toShort(value);
            }
            if (Float.class.equals(cls) || Float.TYPE.equals(cls)) {
                return PropertyConverter.toFloat(value);
            }
            if (Double.class.equals(cls) || Double.TYPE.equals(cls)) {
                return PropertyConverter.toDouble(value);
            }
            if (BigInteger.class.equals(cls)) {
                return PropertyConverter.toBigInteger(value);
            }
            if (BigDecimal.class.equals(cls)) {
                return PropertyConverter.toBigDecimal(value);
            }
        } else {
            if (Date.class.equals(cls)) {
                return PropertyConverter.toDate(value, convHandler.getDateFormat());
            }
            if (Calendar.class.equals(cls)) {
                return PropertyConverter.toCalendar(value, convHandler.getDateFormat());
            }
            if (File.class.equals(cls)) {
                return PropertyConverter.toFile(value);
            }
            if (Path.class.equals(cls)) {
                return PropertyConverter.toPath(value);
            }
            if (URI.class.equals(cls)) {
                return PropertyConverter.toURI(value);
            }
            if (URL.class.equals(cls)) {
                return PropertyConverter.toURL(value);
            }
            if (Pattern.class.equals(cls)) {
                return PropertyConverter.toPattern(value);
            }
            if (Locale.class.equals(cls)) {
                return PropertyConverter.toLocale(value);
            }
            if (PropertyConverter.isEnum(cls)) {
                return PropertyConverter.convertToEnum(cls, value);
            }
            if (Color.class.equals(cls)) {
                return PropertyConverter.toColor(value);
            }
            if (cls.getName().equals(INTERNET_ADDRESS_CLASSNAME_JAVAX)) {
                return PropertyConverter.toInternetAddress(value, INTERNET_ADDRESS_CLASSNAME_JAVAX);
            }
            if (cls.getName().equals(INTERNET_ADDRESS_CLASSNAME_JAKARTA)) {
                return PropertyConverter.toInternetAddress(value, INTERNET_ADDRESS_CLASSNAME_JAKARTA);
            }
            if (InetAddress.class.isAssignableFrom(cls)) {
                return PropertyConverter.toInetAddress(value);
            }
            if (Duration.class.equals(cls)) {
                return PropertyConverter.toDuration(value);
            }
        }
        throw new ConversionException("The value '" + value + "' (" + value.getClass() + ") can't be converted to a " + cls.getName() + " object");
    }

    public static Boolean toBoolean(Object value) throws ConversionException {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
        }
        Boolean b = BooleanUtils.toBooleanObject((String)((String)value));
        if (b == null) {
            throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
        }
        return b;
    }

    public static Character toCharacter(Object value) throws ConversionException {
        String strValue = String.valueOf(value);
        if (strValue.length() == 1) {
            return Character.valueOf(strValue.charAt(0));
        }
        throw new ConversionException(String.format("The value '%s' cannot be converted to a Character object!", strValue));
    }

    public static Byte toByte(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, Byte.class);
        if (n instanceof Byte) {
            return (Byte)n;
        }
        return n.byteValue();
    }

    public static Short toShort(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, Short.class);
        if (n instanceof Short) {
            return (Short)n;
        }
        return n.shortValue();
    }

    public static Integer toInteger(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, Integer.class);
        if (n instanceof Integer) {
            return (Integer)n;
        }
        return n.intValue();
    }

    public static Long toLong(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, Long.class);
        if (n instanceof Long) {
            return (Long)n;
        }
        return n.longValue();
    }

    public static Float toFloat(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, Float.class);
        if (n instanceof Float) {
            return (Float)n;
        }
        return Float.valueOf(n.floatValue());
    }

    public static Double toDouble(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, Double.class);
        if (n instanceof Double) {
            return (Double)n;
        }
        return n.doubleValue();
    }

    public static Duration toDuration(Object value) throws ConversionException {
        if (value instanceof Duration) {
            return (Duration)value;
        }
        if (value instanceof CharSequence) {
            try {
                return Duration.parse((CharSequence)value);
            }
            catch (DateTimeParseException e) {
                throw new ConversionException("Could not convert " + value + " to Duration", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a Duration");
    }

    public static BigInteger toBigInteger(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, BigInteger.class);
        if (n instanceof BigInteger) {
            return (BigInteger)n;
        }
        return BigInteger.valueOf(n.longValue());
    }

    public static BigDecimal toBigDecimal(Object value) throws ConversionException {
        Number n = PropertyConverter.toNumber(value, BigDecimal.class);
        if (n instanceof BigDecimal) {
            return (BigDecimal)n;
        }
        return BigDecimal.valueOf(n.doubleValue());
    }

    static Number toNumber(Object value, Class<?> targetClass) throws ConversionException {
        if (value instanceof Number) {
            return (Number)value;
        }
        String str = value.toString();
        if (str.startsWith(HEX_PREFIX)) {
            try {
                return new BigInteger(str.substring(HEX_PREFIX.length()), 16);
            }
            catch (NumberFormatException nex) {
                throw new ConversionException("Could not convert " + str + " to " + targetClass.getName() + "! Invalid hex number.", nex);
            }
        }
        if (str.startsWith(BIN_PREFIX)) {
            try {
                return new BigInteger(str.substring(BIN_PREFIX.length()), 2);
            }
            catch (NumberFormatException nex) {
                throw new ConversionException("Could not convert " + str + " to " + targetClass.getName() + "! Invalid binary number.", nex);
            }
        }
        try {
            Constructor<?> constr = targetClass.getConstructor(CONSTR_ARGS);
            return (Number)constr.newInstance(str);
        }
        catch (InvocationTargetException itex) {
            throw new ConversionException("Could not convert " + str + " to " + targetClass.getName(), itex.getTargetException());
        }
        catch (Exception ex) {
            throw new ConversionException("Conversion error when trying to convert " + str + " to " + targetClass.getName(), ex);
        }
    }

    public static File toFile(Object value) throws ConversionException {
        if (value instanceof File) {
            return (File)value;
        }
        if (value instanceof Path) {
            return ((Path)value).toFile();
        }
        if (value instanceof String) {
            return new File((String)value);
        }
        throw new ConversionException("The value " + value + " can't be converted to a File");
    }

    public static Path toPath(Object value) throws ConversionException {
        if (value instanceof File) {
            return ((File)value).toPath();
        }
        if (value instanceof Path) {
            return (Path)value;
        }
        if (value instanceof String) {
            return Paths.get((String)value, new String[0]);
        }
        throw new ConversionException("The value " + value + " can't be converted to a Path");
    }

    public static URI toURI(Object value) throws ConversionException {
        if (value instanceof URI) {
            return (URI)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to an URI");
        }
        try {
            return new URI((String)value);
        }
        catch (URISyntaxException e) {
            throw new ConversionException("The value " + value + " can't be converted to an URI", e);
        }
    }

    public static URL toURL(Object value) throws ConversionException {
        if (value instanceof URL) {
            return (URL)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to an URL");
        }
        try {
            return new URL((String)value);
        }
        catch (MalformedURLException e) {
            throw new ConversionException("The value " + value + " can't be converted to an URL", e);
        }
    }

    public static Pattern toPattern(Object value) throws ConversionException {
        if (value instanceof Pattern) {
            return (Pattern)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Pattern");
        }
        try {
            return Pattern.compile((String)value);
        }
        catch (PatternSyntaxException e) {
            throw new ConversionException("The value " + value + " can't be converted to a Pattern", e);
        }
    }

    public static Locale toLocale(Object value) throws ConversionException {
        if (value instanceof Locale) {
            return (Locale)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Locale");
        }
        String[] elements = ((String)value).split("_");
        int size = elements.length;
        if (size >= 1 && (elements[0].length() == 2 || elements[0].isEmpty())) {
            String language = elements[0];
            String country = size >= 2 ? elements[1] : "";
            String variant = size >= 3 ? elements[2] : "";
            return new Locale(language, country, variant);
        }
        throw new ConversionException("The value " + value + " can't be converted to a Locale");
    }

    public static Color toColor(Object value) throws ConversionException {
        if (value instanceof Color) {
            return (Color)value;
        }
        if (!(value instanceof String) || StringUtils.isBlank((CharSequence)((String)value))) {
            throw new ConversionException("The value " + value + " can't be converted to a Color");
        }
        String color = ((String)value).trim();
        int[] components = new int[3];
        int minlength = components.length * 2;
        if (color.length() < minlength) {
            throw new ConversionException("The value " + value + " can't be converted to a Color");
        }
        if (color.startsWith("#")) {
            color = color.substring(1);
        }
        try {
            for (int i = 0; i < components.length; ++i) {
                components[i] = Integer.parseInt(color.substring(2 * i, 2 * i + 2), 16);
            }
            int alpha = color.length() >= minlength + 2 ? Integer.parseInt(color.substring(minlength, minlength + 2), 16) : Color.black.getAlpha();
            return new Color(components[0], components[1], components[2], alpha);
        }
        catch (Exception e) {
            throw new ConversionException("The value " + value + " can't be converted to a Color", e);
        }
    }

    static InetAddress toInetAddress(Object value) throws ConversionException {
        if (value instanceof InetAddress) {
            return (InetAddress)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a InetAddress");
        }
        try {
            return InetAddress.getByName((String)value);
        }
        catch (UnknownHostException e) {
            throw new ConversionException("The value " + value + " can't be converted to a InetAddress", e);
        }
    }

    static Object toInternetAddress(Object value, String targetClassName) throws ConversionException {
        if (value.getClass().getName().equals(targetClassName)) {
            return value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to an InternetAddress");
        }
        try {
            Constructor<?> ctor = Class.forName(targetClassName).getConstructor(String.class);
            return ctor.newInstance(value);
        }
        catch (Exception e) {
            throw new ConversionException("The value " + value + " can't be converted to an InternetAddress", e);
        }
    }

    static boolean isEnum(Class<?> cls) {
        return cls.isEnum();
    }

    static <E extends Enum<E>> E toEnum(Object value, Class<E> cls) throws ConversionException {
        if (value.getClass().equals(cls)) {
            return (E)((Enum)cls.cast(value));
        }
        if (value instanceof String) {
            try {
                return Enum.valueOf(cls, (String)value);
            }
            catch (Exception e) {
                throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
            }
        }
        if (!(value instanceof Number)) {
            throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
        }
        try {
            Enum[] enumConstants = (Enum[])cls.getEnumConstants();
            return (E)enumConstants[((Number)value).intValue()];
        }
        catch (Exception e) {
            throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
        }
    }

    public static Date toDate(Object value, String format) throws ConversionException {
        if (value instanceof Date) {
            return (Date)value;
        }
        if (value instanceof Calendar) {
            return ((Calendar)value).getTime();
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Date");
        }
        try {
            return new SimpleDateFormat(format).parse((String)value);
        }
        catch (ParseException e) {
            throw new ConversionException("The value " + value + " can't be converted to a Date", e);
        }
    }

    public static Calendar toCalendar(Object value, String format) throws ConversionException {
        if (value instanceof Calendar) {
            return (Calendar)value;
        }
        if (value instanceof Date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date)value);
            return calendar;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Calendar");
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat(format).parse((String)value));
            return calendar;
        }
        catch (ParseException e) {
            throw new ConversionException("The value " + value + " can't be converted to a Calendar", e);
        }
    }

    private static Object convertToEnum(Class<?> enumClass, Object value) {
        return PropertyConverter.toEnum(value, enumClass.asSubclass(Enum.class));
    }
}

