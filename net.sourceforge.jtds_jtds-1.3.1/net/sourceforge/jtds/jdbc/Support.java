/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.BlobImpl;
import net.sourceforge.jtds.jdbc.ClobImpl;
import net.sourceforge.jtds.jdbc.DateTime;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.UniqueIdentifier;
import net.sourceforge.jtds.util.Logger;

public class Support {
    private static final Integer INTEGER_ZERO = new Integer(0);
    private static final Integer INTEGER_ONE = new Integer(1);
    private static final Long LONG_ZERO = new Long(0L);
    private static final Long LONG_ONE = new Long(1L);
    private static final Float FLOAT_ZERO = new Float(0.0);
    private static final Float FLOAT_ONE = new Float(1.0);
    private static final Double DOUBLE_ZERO = new Double(0.0);
    private static final Double DOUBLE_ONE = new Double(1.0);
    private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal(0.0);
    private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal(1.0);
    private static final Date DATE_ZERO = new Date(0L);
    private static final Time TIME_ZERO = new Time(0L);
    private static final BigInteger MIN_VALUE_LONG_BI = new BigInteger(String.valueOf(Long.MIN_VALUE));
    private static final BigInteger MAX_VALUE_LONG_BI = new BigInteger(String.valueOf(Long.MAX_VALUE));
    private static final BigDecimal MIN_VALUE_LONG_BD = new BigDecimal(String.valueOf(Long.MIN_VALUE));
    private static final BigDecimal MAX_VALUE_LONG_BD = new BigDecimal(String.valueOf(Long.MAX_VALUE));
    private static final BigInteger MAX_VALUE_28 = new BigInteger("9999999999999999999999999999");
    private static final BigInteger MAX_VALUE_38 = new BigInteger("99999999999999999999999999999999999999");
    private static final HashMap typeMap = new HashMap();
    private static final char[] hex;

    public static String toHex(byte[] bytes) {
        int len = bytes.length;
        if (len > 0) {
            StringBuilder buf = new StringBuilder(len * 2);
            for (int i = 0; i < len; ++i) {
                int b1 = bytes[i] & 0xFF;
                buf.append(hex[b1 >> 4]);
                buf.append(hex[b1 & 0xF]);
            }
            return buf.toString();
        }
        return "";
    }

    static BigDecimal normalizeBigDecimal(BigDecimal value, int maxPrecision) throws SQLException {
        BigInteger max;
        if (value == null) {
            return null;
        }
        if (value.scale() < 0) {
            value = value.setScale(0);
        }
        if (value.scale() > maxPrecision) {
            value = value.setScale(maxPrecision, 4);
        }
        BigInteger bigInteger = max = maxPrecision == 28 ? MAX_VALUE_28 : MAX_VALUE_38;
        while (value.abs().unscaledValue().compareTo(max) > 0) {
            int scale = value.scale() - 1;
            if (scale < 0) {
                throw new SQLException(Messages.get("error.normalize.numtoobig", String.valueOf(maxPrecision)), "22000");
            }
            value = value.setScale(scale, 4);
        }
        return value;
    }

    static Object castNumeric(Object orig, int sourceType, int targetType) {
        return null;
    }

    static Object convert(Object callerReference, Object x, int jdbcType, String charSet) throws SQLException {
        if (x == null) {
            switch (jdbcType) {
                case -7: 
                case 16: {
                    return Boolean.FALSE;
                }
                case -6: 
                case 4: 
                case 5: {
                    return INTEGER_ZERO;
                }
                case -5: {
                    return LONG_ZERO;
                }
                case 7: {
                    return FLOAT_ZERO;
                }
                case 6: 
                case 8: {
                    return DOUBLE_ZERO;
                }
            }
            return null;
        }
        try {
            switch (jdbcType) {
                case -6: {
                    long val;
                    if (x instanceof Boolean) {
                        return (Boolean)x != false ? INTEGER_ONE : INTEGER_ZERO;
                    }
                    if (x instanceof Byte) {
                        return new Integer((int)((Byte)x & 0xFF));
                    }
                    if (x instanceof Number) {
                        val = ((Number)x).longValue();
                    } else {
                        if (!(x instanceof String)) break;
                        val = new Long(((String)x).trim());
                    }
                    if (val < -128L || val > 127L) {
                        throw new SQLException(Messages.get("error.convert.numericoverflow", x, Support.getJdbcTypeName(jdbcType)), "22003");
                    }
                    return new Integer(new Long(val).intValue());
                }
                case 5: {
                    long val;
                    if (x instanceof Boolean) {
                        return (Boolean)x != false ? INTEGER_ONE : INTEGER_ZERO;
                    }
                    if (x instanceof Short) {
                        return new Integer(((Short)x).shortValue());
                    }
                    if (x instanceof Byte) {
                        return new Integer((int)((Byte)x & 0xFF));
                    }
                    if (x instanceof Number) {
                        val = ((Number)x).longValue();
                    } else {
                        if (!(x instanceof String)) break;
                        val = new Long(((String)x).trim());
                    }
                    if (val < -32768L || val > 32767L) {
                        throw new SQLException(Messages.get("error.convert.numericoverflow", x, Support.getJdbcTypeName(jdbcType)), "22003");
                    }
                    return new Integer(new Long(val).intValue());
                }
                case 4: {
                    long val;
                    if (x instanceof Integer) {
                        return x;
                    }
                    if (x instanceof Boolean) {
                        return (Boolean)x != false ? INTEGER_ONE : INTEGER_ZERO;
                    }
                    if (x instanceof Short) {
                        return new Integer(((Short)x).shortValue());
                    }
                    if (x instanceof Byte) {
                        return new Integer((int)((Byte)x & 0xFF));
                    }
                    if (x instanceof Number) {
                        val = ((Number)x).longValue();
                    } else {
                        if (!(x instanceof String)) break;
                        val = new Long(((String)x).trim());
                    }
                    if (val < Integer.MIN_VALUE || val > Integer.MAX_VALUE) {
                        throw new SQLException(Messages.get("error.convert.numericoverflow", x, Support.getJdbcTypeName(jdbcType)), "22003");
                    }
                    return new Integer(new Long(val).intValue());
                }
                case -5: {
                    if (x instanceof BigDecimal) {
                        BigDecimal val = (BigDecimal)x;
                        if (val.compareTo(MIN_VALUE_LONG_BD) < 0 || val.compareTo(MAX_VALUE_LONG_BD) > 0) {
                            throw new SQLException(Messages.get("error.convert.numericoverflow", x, Support.getJdbcTypeName(jdbcType)), "22003");
                        }
                        return new Long(val.longValue());
                    }
                    if (x instanceof Long) {
                        return x;
                    }
                    if (x instanceof Boolean) {
                        return (Boolean)x != false ? LONG_ONE : LONG_ZERO;
                    }
                    if (x instanceof Byte) {
                        return new Long((long)((Byte)x & 0xFF));
                    }
                    if (x instanceof BigInteger) {
                        BigInteger val = (BigInteger)x;
                        if (val.compareTo(MIN_VALUE_LONG_BI) < 0 || val.compareTo(MAX_VALUE_LONG_BI) > 0) {
                            throw new SQLException(Messages.get("error.convert.numericoverflow", x, Support.getJdbcTypeName(jdbcType)), "22003");
                        }
                        return new Long(val.longValue());
                    }
                    if (x instanceof Number) {
                        return new Long(((Number)x).longValue());
                    }
                    if (!(x instanceof String)) break;
                    return new Long(((String)x).trim());
                }
                case 7: {
                    if (x instanceof Float) {
                        return x;
                    }
                    if (x instanceof Byte) {
                        return new Float((float)((Byte)x & 0xFF));
                    }
                    if (x instanceof Number) {
                        return new Float(((Number)x).floatValue());
                    }
                    if (x instanceof String) {
                        return new Float(((String)x).trim());
                    }
                    if (!(x instanceof Boolean)) break;
                    return (Boolean)x != false ? FLOAT_ONE : FLOAT_ZERO;
                }
                case 6: 
                case 8: {
                    if (x instanceof Double) {
                        return x;
                    }
                    if (x instanceof Byte) {
                        return new Double((double)((Byte)x & 0xFF));
                    }
                    if (x instanceof Number) {
                        return new Double(((Number)x).doubleValue());
                    }
                    if (x instanceof String) {
                        return new Double(((String)x).trim());
                    }
                    if (!(x instanceof Boolean)) break;
                    return (Boolean)x != false ? DOUBLE_ONE : DOUBLE_ZERO;
                }
                case 2: 
                case 3: {
                    if (x instanceof BigDecimal) {
                        return x;
                    }
                    if (x instanceof Number) {
                        return new BigDecimal(x.toString());
                    }
                    if (x instanceof String) {
                        return new BigDecimal((String)x);
                    }
                    if (!(x instanceof Boolean)) break;
                    return (Boolean)x != false ? BIG_DECIMAL_ONE : BIG_DECIMAL_ZERO;
                }
                case 1: 
                case 12: {
                    if (x instanceof String) {
                        return x;
                    }
                    if (x instanceof Number) {
                        return x.toString();
                    }
                    if (x instanceof Boolean) {
                        return (Boolean)x != false ? "1" : "0";
                    }
                    if (x instanceof Clob) {
                        Clob clob = (Clob)x;
                        long length = clob.length();
                        if (length > Integer.MAX_VALUE) {
                            throw new SQLException(Messages.get("error.normalize.lobtoobig"), "22000");
                        }
                        return clob.getSubString(1L, (int)length);
                    }
                    if (x instanceof Blob) {
                        Blob blob = (Blob)x;
                        long length = blob.length();
                        if (length > Integer.MAX_VALUE) {
                            throw new SQLException(Messages.get("error.normalize.lobtoobig"), "22000");
                        }
                        x = blob.getBytes(1L, (int)length);
                    }
                    if (x instanceof byte[]) {
                        return Support.toHex((byte[])x);
                    }
                    return x.toString();
                }
                case -7: 
                case 16: {
                    if (x instanceof Boolean) {
                        return x;
                    }
                    if (x instanceof Number) {
                        return ((Number)x).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
                    }
                    if (!(x instanceof String)) break;
                    String tmp = ((String)x).trim();
                    return "1".equals(tmp) || "true".equalsIgnoreCase(tmp) ? Boolean.TRUE : Boolean.FALSE;
                }
                case -3: 
                case -2: {
                    if (x instanceof byte[]) {
                        return x;
                    }
                    if (x instanceof Blob) {
                        Blob blob = (Blob)x;
                        return blob.getBytes(1L, (int)blob.length());
                    }
                    if (x instanceof Clob) {
                        Clob clob = (Clob)x;
                        long length = clob.length();
                        if (length > Integer.MAX_VALUE) {
                            throw new SQLException(Messages.get("error.normalize.lobtoobig"), "22000");
                        }
                        x = clob.getSubString(1L, (int)length);
                    }
                    if (x instanceof String) {
                        if (charSet == null) {
                            charSet = "ISO-8859-1";
                        }
                        try {
                            return ((String)x).getBytes(charSet);
                        }
                        catch (UnsupportedEncodingException e) {
                            return ((String)x).getBytes();
                        }
                    }
                    if (!(x instanceof UniqueIdentifier)) break;
                    return ((UniqueIdentifier)x).getBytes();
                }
                case 93: {
                    if (x instanceof DateTime) {
                        return ((DateTime)x).toTimestamp();
                    }
                    if (x instanceof Timestamp) {
                        return x;
                    }
                    if (x instanceof Date) {
                        return new Timestamp(((Date)x).getTime());
                    }
                    if (x instanceof Time) {
                        return new Timestamp(((Time)x).getTime());
                    }
                    if (!(x instanceof String)) break;
                    String val = ((String)x).trim();
                    int len = val.length();
                    try {
                        if (len > 10 && val.charAt(4) == '-') {
                            return Timestamp.valueOf(val);
                        }
                        if (len > 7 && val.charAt(4) == '-') {
                            return new Timestamp(Date.valueOf(val).getTime());
                        }
                        if (len > 7 && val.charAt(2) == ':') {
                            return new Timestamp(Time.valueOf(val.split("\\.")[0].trim()).getTime());
                        }
                    }
                    catch (IllegalArgumentException ie) {
                        // empty catch block
                    }
                    throw new SQLException(Messages.get("error.convert.badnumber", val, Support.getJdbcTypeName(jdbcType)), "22000");
                }
                case 91: {
                    if (x instanceof DateTime) {
                        return ((DateTime)x).toDate();
                    }
                    if (x instanceof Date) {
                        return x;
                    }
                    if (x instanceof Time) {
                        return DATE_ZERO;
                    }
                    if (x instanceof Timestamp) {
                        GregorianCalendar cal = new GregorianCalendar();
                        cal.setTime((java.util.Date)x);
                        cal.set(11, 0);
                        cal.set(12, 0);
                        cal.set(13, 0);
                        cal.set(14, 0);
                        return new Date(cal.getTime().getTime());
                    }
                    if (!(x instanceof String)) break;
                    String val = ((String)x).trim();
                    int len = val.length();
                    try {
                        if (len > 7 && len < 11 && val.charAt(4) == '-') {
                            return Date.valueOf(val);
                        }
                        if (len > 10 && val.charAt(4) == '-') {
                            return Date.valueOf(val.split(" ")[0].trim());
                        }
                        if (len > 7 && val.charAt(2) == ':') {
                            Time.valueOf(val.split("\\.")[0].trim());
                            return DATE_ZERO;
                        }
                    }
                    catch (IllegalArgumentException ie) {
                        // empty catch block
                    }
                    throw new SQLException(Messages.get("error.convert.badnumber", val, Support.getJdbcTypeName(jdbcType)), "22000");
                }
                case 92: {
                    if (x instanceof DateTime) {
                        return ((DateTime)x).toTime();
                    }
                    if (x instanceof Time) {
                        return x;
                    }
                    if (x instanceof Date) {
                        return TIME_ZERO;
                    }
                    if (x instanceof Timestamp) {
                        GregorianCalendar cal = new GregorianCalendar();
                        cal.setTime((java.util.Date)x);
                        cal.set(1, 1970);
                        cal.set(2, 0);
                        cal.set(5, 1);
                        return new Time(cal.getTime().getTime());
                    }
                    if (!(x instanceof String)) break;
                    String val = ((String)x).trim().split("\\.")[0].trim();
                    int len = val.length();
                    try {
                        if (len == 8 && val.charAt(2) == ':') {
                            return Time.valueOf(val);
                        }
                        if (len > 10 && val.charAt(4) == '-') {
                            String[] lines = val.split(" ");
                            if (lines.length > 1) {
                                return Time.valueOf(lines[1].trim());
                            }
                        } else if (len > 7 && val.charAt(4) == '-') {
                            Date.valueOf(val);
                            return TIME_ZERO;
                        }
                    }
                    catch (IllegalArgumentException ie) {
                        // empty catch block
                    }
                    throw new SQLException(Messages.get("error.convert.badnumber", val, Support.getJdbcTypeName(jdbcType)), "22000");
                }
                case 1111: {
                    return x;
                }
                case 2000: {
                    throw new SQLException(Messages.get("error.convert.badtypes", x.getClass().getName(), Support.getJdbcTypeName(jdbcType)), "22005");
                }
                case -4: 
                case 2004: {
                    if (x instanceof Blob) {
                        return x;
                    }
                    if (x instanceof byte[]) {
                        return new BlobImpl(Support.getConnection(callerReference), (byte[])x);
                    }
                    if (x instanceof Clob) {
                        Clob clob = (Clob)x;
                        try {
                            int c;
                            if (charSet == null) {
                                charSet = "ISO-8859-1";
                            }
                            Reader rdr = clob.getCharacterStream();
                            BlobImpl blob = new BlobImpl(Support.getConnection(callerReference));
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(blob.setBinaryStream(1L), charSet));
                            while ((c = rdr.read()) >= 0) {
                                out.write(c);
                            }
                            out.close();
                            rdr.close();
                            return blob;
                        }
                        catch (UnsupportedEncodingException e) {
                            x = clob.getSubString(1L, (int)clob.length());
                        }
                        catch (IOException e) {
                            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                        }
                    }
                    if (!(x instanceof String)) break;
                    BlobImpl blob = new BlobImpl(Support.getConnection(callerReference));
                    String data = (String)x;
                    if (charSet == null) {
                        charSet = "ISO-8859-1";
                    }
                    try {
                        blob.setBytes(1L, data.getBytes(charSet));
                    }
                    catch (UnsupportedEncodingException e) {
                        blob.setBytes(1L, data.getBytes());
                    }
                    return blob;
                }
                case -1: 
                case 2005: {
                    if (x instanceof Clob) {
                        return x;
                    }
                    if (x instanceof Blob) {
                        Blob blob = (Blob)x;
                        try {
                            int b;
                            InputStream is = blob.getBinaryStream();
                            ClobImpl clob = new ClobImpl(Support.getConnection(callerReference));
                            Writer out = clob.setCharacterStream(1L);
                            while ((b = is.read()) >= 0) {
                                out.write(hex[b >> 4]);
                                out.write(hex[b & 0xF]);
                            }
                            out.close();
                            is.close();
                            return clob;
                        }
                        catch (IOException e) {
                            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                        }
                    }
                    if (x instanceof Boolean) {
                        x = (Boolean)x != false ? "1" : "0";
                    } else if (!(x instanceof byte[])) {
                        x = x.toString();
                    }
                    if (x instanceof byte[]) {
                        ClobImpl clob = new ClobImpl(Support.getConnection(callerReference));
                        clob.setString(1L, Support.toHex((byte[])x));
                        return clob;
                    }
                    if (!(x instanceof String)) break;
                    return new ClobImpl(Support.getConnection(callerReference), (String)x);
                }
                default: {
                    throw new SQLException(Messages.get("error.convert.badtypeconst", String.valueOf(x), Support.getJdbcTypeName(jdbcType)), "HY004");
                }
            }
            throw new SQLException(Messages.get("error.convert.badtypes", x.getClass().getName(), Support.getJdbcTypeName(jdbcType)), "22005");
        }
        catch (NumberFormatException nfe) {
            throw new SQLException(Messages.get("error.convert.badnumber", String.valueOf(x), Support.getJdbcTypeName(jdbcType)), "22000");
        }
    }

    static int getJdbcType(Object value) {
        if (value == null) {
            return 0;
        }
        return Support.getJdbcType(value.getClass());
    }

    static int getJdbcType(Class typeClass) {
        if (typeClass == null) {
            return 2000;
        }
        Object type = typeMap.get(typeClass);
        if (type == null) {
            return Support.getJdbcType(typeClass.getSuperclass());
        }
        return (Integer)type;
    }

    static String getJdbcTypeName(int jdbcType) {
        switch (jdbcType) {
            case 2003: {
                return "ARRAY";
            }
            case -5: {
                return "BIGINT";
            }
            case -2: {
                return "BINARY";
            }
            case -7: {
                return "BIT";
            }
            case 2004: {
                return "BLOB";
            }
            case 16: {
                return "BOOLEAN";
            }
            case 1: {
                return "CHAR";
            }
            case 2005: {
                return "CLOB";
            }
            case 70: {
                return "DATALINK";
            }
            case 91: {
                return "DATE";
            }
            case 3: {
                return "DECIMAL";
            }
            case 2001: {
                return "DISTINCT";
            }
            case 8: {
                return "DOUBLE";
            }
            case 6: {
                return "FLOAT";
            }
            case 4: {
                return "INTEGER";
            }
            case 2000: {
                return "JAVA_OBJECT";
            }
            case -4: {
                return "LONGVARBINARY";
            }
            case -1: {
                return "LONGVARCHAR";
            }
            case 0: {
                return "NULL";
            }
            case 2: {
                return "NUMERIC";
            }
            case 1111: {
                return "OTHER";
            }
            case 7: {
                return "REAL";
            }
            case 2006: {
                return "REF";
            }
            case 5: {
                return "SMALLINT";
            }
            case 2002: {
                return "STRUCT";
            }
            case 92: {
                return "TIME";
            }
            case 93: {
                return "TIMESTAMP";
            }
            case -6: {
                return "TINYINT";
            }
            case -3: {
                return "VARBINARY";
            }
            case 12: {
                return "VARCHAR";
            }
            case 2009: {
                return "XML";
            }
        }
        return "ERROR";
    }

    static String getClassName(int jdbcType) {
        switch (jdbcType) {
            case -7: 
            case 16: {
                return "java.lang.Boolean";
            }
            case -6: 
            case 4: 
            case 5: {
                return "java.lang.Integer";
            }
            case -5: {
                return "java.lang.Long";
            }
            case 2: 
            case 3: {
                return "java.math.BigDecimal";
            }
            case 7: {
                return "java.lang.Float";
            }
            case 6: 
            case 8: {
                return "java.lang.Double";
            }
            case 1: 
            case 12: {
                return "java.lang.String";
            }
            case -3: 
            case -2: {
                return "[B";
            }
            case -4: 
            case 2004: {
                return "java.sql.Blob";
            }
            case -1: 
            case 2005: {
                return "java.sql.Clob";
            }
            case 91: {
                return "java.sql.Date";
            }
            case 92: {
                return "java.sql.Time";
            }
            case 93: {
                return "java.sql.Timestamp";
            }
        }
        return "java.lang.Object";
    }

    static void embedData(StringBuilder buf, Object value, boolean isUnicode, JtdsConnection connection) throws SQLException {
        DateTime dt;
        String tmp;
        buf.append(' ');
        if (value == null) {
            buf.append("NULL ");
            return;
        }
        if (value instanceof Blob) {
            Blob blob = (Blob)value;
            value = blob.getBytes(1L, (int)blob.length());
        } else if (value instanceof Clob) {
            Clob clob = (Clob)value;
            value = clob.getSubString(1L, (int)clob.length());
        }
        if (value instanceof DateTime) {
            buf.append('\'');
            buf.append(value);
            buf.append('\'');
        } else if (value instanceof byte[]) {
            byte[] bytes = (byte[])value;
            int len = bytes.length;
            if (len >= 0) {
                buf.append('0').append('x');
                if (len == 0 && connection.getTdsVersion() < 3) {
                    buf.append('0').append('0');
                } else {
                    for (int i = 0; i < len; ++i) {
                        int b1 = bytes[i] & 0xFF;
                        buf.append(hex[b1 >> 4]);
                        buf.append(hex[b1 & 0xF]);
                    }
                }
            }
        } else if (value instanceof String) {
            tmp = (String)value;
            int len = tmp.length();
            if (isUnicode) {
                buf.append('N');
            }
            buf.append('\'');
            for (int i = 0; i < len; ++i) {
                char c = tmp.charAt(i);
                if (c == '\'') {
                    buf.append('\'');
                }
                buf.append(c);
            }
            buf.append('\'');
        } else if (value instanceof Date) {
            dt = new DateTime((Date)value);
            buf.append('\'');
            buf.append(dt);
            buf.append('\'');
        } else if (value instanceof Time) {
            dt = new DateTime((Time)value);
            buf.append('\'');
            buf.append(dt);
            buf.append('\'');
        } else if (value instanceof Timestamp) {
            dt = new DateTime((Timestamp)value);
            buf.append('\'');
            buf.append(dt);
            buf.append('\'');
        } else if (value instanceof Boolean) {
            buf.append((Boolean)value != false ? (char)'1' : '0');
        } else if (value instanceof BigDecimal) {
            tmp = value.toString();
            int maxlen = connection.getMaxPrecision();
            if (tmp.charAt(0) == '-') {
                ++maxlen;
            }
            if (tmp.indexOf(46) >= 0) {
                ++maxlen;
            }
            if (tmp.length() > maxlen) {
                buf.append(tmp.substring(0, maxlen));
            } else {
                buf.append(tmp);
            }
        } else {
            buf.append(value.toString());
        }
        buf.append(' ');
    }

    static String getStatementKey(String sql, ParamInfo[] params, int serverType, String catalog, boolean autoCommit, boolean cursor) {
        StringBuilder key;
        if (serverType == 1) {
            key = new StringBuilder(1 + catalog.length() + sql.length() + 11 * params.length);
            key.append(cursor ? (char)'C' : 'X');
            key.append(catalog);
            key.append(sql);
            for (int i = 0; i < params.length; ++i) {
                key.append(params[i].sqlType);
            }
        } else {
            key = new StringBuilder(sql.length() + 2);
            key.append(autoCommit ? (char)'T' : 'F');
            key.append(sql);
        }
        return key.toString();
    }

    static String getParameterDefinitions(ParamInfo[] parameters) {
        StringBuilder sql = new StringBuilder(parameters.length * 15);
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i].name == null) {
                sql.append("@P");
                sql.append(i);
            } else {
                sql.append(parameters[i].name);
            }
            sql.append(' ');
            sql.append(parameters[i].sqlType);
            if (i + 1 >= parameters.length) continue;
            sql.append(',');
        }
        return sql.toString();
    }

    static String substituteParamMarkers(String sql, ParamInfo[] list) {
        char[] buf = new char[sql.length() + list.length * 7];
        int bufferPtr = 0;
        int start = 0;
        StringBuilder number = new StringBuilder(4);
        for (int i = 0; i < list.length; ++i) {
            int pos = list[i].markerPos;
            if (pos <= 0) continue;
            sql.getChars(start, pos, buf, bufferPtr);
            bufferPtr += pos - start;
            start = pos + 1;
            buf[bufferPtr++] = 32;
            buf[bufferPtr++] = 64;
            buf[bufferPtr++] = 80;
            number.setLength(0);
            number.append(i);
            number.getChars(0, number.length(), buf, bufferPtr);
            bufferPtr += number.length();
            buf[bufferPtr++] = 32;
        }
        if (start < sql.length()) {
            sql.getChars(start, sql.length(), buf, bufferPtr);
            bufferPtr += sql.length() - start;
        }
        return new String(buf, 0, bufferPtr);
    }

    static String substituteParameters(String sql, ParamInfo[] list, JtdsConnection connection) throws SQLException {
        int len = sql.length();
        for (int i = 0; i < list.length; ++i) {
            if (!(list[i].isRetVal || list[i].isSet || list[i].isOutput)) {
                throw new SQLException(Messages.get("error.prepare.paramnotset", Integer.toString(i + 1)), "07000");
            }
            Object value = list[i].value;
            if (value instanceof InputStream || value instanceof Reader) {
                try {
                    value = list[i].jdbcType == -1 || list[i].jdbcType == 2005 || list[i].jdbcType == 12 ? list[i].getString("US-ASCII") : (Object)list[i].getBytes("US-ASCII");
                    list[i].value = value;
                }
                catch (IOException e) {
                    throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                }
            }
            if (value instanceof String) {
                len += ((String)value).length() + 5;
                continue;
            }
            if (value instanceof byte[]) {
                len += ((byte[])value).length * 2 + 4;
                continue;
            }
            len += 32;
        }
        StringBuilder buf = new StringBuilder(len + 16);
        int start = 0;
        for (int i = 0; i < list.length; ++i) {
            int pos = list[i].markerPos;
            if (pos <= 0) continue;
            buf.append(sql.substring(start, list[i].markerPos));
            start = pos + 1;
            boolean isUnicode = connection.getTdsVersion() >= 3 && list[i].isUnicode;
            Support.embedData(buf, list[i].value, isUnicode, connection);
        }
        if (start < sql.length()) {
            buf.append(sql.substring(start));
        }
        return buf.toString();
    }

    static byte[] encodeString(String cs, String value) {
        try {
            return value.getBytes(cs);
        }
        catch (UnsupportedEncodingException e) {
            return value.getBytes();
        }
    }

    public static SQLWarning linkException(SQLWarning sqle, Throwable cause) {
        return (SQLWarning)Support.linkException((Exception)sqle, cause);
    }

    public static SQLException linkException(SQLException sqle, Throwable cause) {
        return (SQLException)Support.linkException((Exception)sqle, cause);
    }

    public static Throwable linkException(Exception exception, Throwable cause) {
        Class<?> exceptionClass = exception.getClass();
        Class[] parameterTypes = new Class[]{Throwable.class};
        Object[] arguments = new Object[]{cause};
        try {
            Method initCauseMethod = exceptionClass.getMethod("initCause", parameterTypes);
            initCauseMethod.invoke((Object)exception, arguments);
        }
        catch (NoSuchMethodException e) {
            if (Logger.isActive()) {
                Logger.logException((Exception)cause);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        return exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long timeToZone(java.util.Date value, Calendar target) {
        java.util.Date tmp = target.getTime();
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(value);
            target.set(11, cal.get(11));
            target.set(12, cal.get(12));
            target.set(13, cal.get(13));
            target.set(14, cal.get(14));
            target.set(1, cal.get(1));
            target.set(2, cal.get(2));
            target.set(5, cal.get(5));
            long l = target.getTime().getTime();
            return l;
        }
        finally {
            target.setTime(tmp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long timeFromZone(java.util.Date value, Calendar target) {
        java.util.Date tmp = target.getTime();
        try {
            GregorianCalendar cal = new GregorianCalendar();
            target.setTime(value);
            cal.set(11, target.get(11));
            cal.set(12, target.get(12));
            cal.set(13, target.get(13));
            cal.set(14, target.get(14));
            cal.set(1, target.get(1));
            cal.set(2, target.get(2));
            cal.set(5, target.get(5));
            long l = cal.getTime().getTime();
            return l;
        }
        finally {
            target.setTime(tmp);
        }
    }

    public static Object convertLOB(Object value) throws SQLException {
        if (value instanceof Clob) {
            Clob c = (Clob)value;
            return c.getSubString(1L, (int)c.length());
        }
        if (value instanceof Blob) {
            Blob b = (Blob)value;
            return b.getBytes(1L, (int)b.length());
        }
        return value;
    }

    public static int convertLOBType(int type) {
        switch (type) {
            case 2004: {
                return -4;
            }
            case 2005: {
                return -1;
            }
        }
        return type;
    }

    public static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    private static JtdsConnection getConnection(Object callerReference) {
        Connection connection;
        block6: {
            if (callerReference == null) {
                throw new IllegalArgumentException("callerReference cannot be null.");
            }
            try {
                if (callerReference instanceof Connection) {
                    connection = (Connection)callerReference;
                    break block6;
                }
                if (callerReference instanceof Statement) {
                    connection = ((Statement)callerReference).getConnection();
                    break block6;
                }
                if (callerReference instanceof ResultSet) {
                    connection = ((ResultSet)callerReference).getStatement().getConnection();
                    break block6;
                }
                throw new IllegalArgumentException("callerReference is invalid.");
            }
            catch (SQLException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return (JtdsConnection)connection;
    }

    private Support() {
    }

    static int calculateNamedPipeBufferSize(int tdsVersion, int packetSize) {
        if (packetSize == 0) {
            if (tdsVersion >= 3) {
                return 4096;
            }
            return 512;
        }
        return packetSize;
    }

    static {
        typeMap.put(Byte.class, new Integer(-6));
        typeMap.put(Short.class, new Integer(5));
        typeMap.put(Integer.class, new Integer(4));
        typeMap.put(Long.class, new Integer(-5));
        typeMap.put(Float.class, new Integer(7));
        typeMap.put(Double.class, new Integer(8));
        typeMap.put(BigDecimal.class, new Integer(3));
        typeMap.put(Boolean.class, new Integer(16));
        typeMap.put(byte[].class, new Integer(-3));
        typeMap.put(Date.class, new Integer(91));
        typeMap.put(Time.class, new Integer(92));
        typeMap.put(Timestamp.class, new Integer(93));
        typeMap.put(BlobImpl.class, new Integer(-4));
        typeMap.put(ClobImpl.class, new Integer(-1));
        typeMap.put(String.class, new Integer(12));
        typeMap.put(Blob.class, new Integer(-4));
        typeMap.put(Clob.class, new Integer(-1));
        typeMap.put(BigInteger.class, new Integer(-5));
        hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }
}

