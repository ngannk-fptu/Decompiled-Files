/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.AsciiFilteredInputStream;
import com.microsoft.sqlserver.jdbc.AsciiFilteredUnicodeInputStream;
import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.GregorianChange;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerBlob;
import com.microsoft.sqlserver.jdbc.SQLServerClob;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerNClob;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.UTC;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import microsoft.sql.DateTimeOffset;

final class DDC {
    private static final BigInteger maxRPCDecimalValue = new BigInteger("99999999999999999999999999999999999999");

    private DDC() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static final Object convertIntegerToObject(int intValue, int valueLength, JDBCType jdbcType, StreamType streamType) {
        switch (jdbcType) {
            case INTEGER: {
                return intValue;
            }
            case SMALLINT: 
            case TINYINT: {
                return (short)intValue;
            }
            case BIT: 
            case BOOLEAN: {
                return 0 != intValue;
            }
            case BIGINT: {
                return (long)intValue;
            }
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: {
                return new BigDecimal(Integer.toString(intValue));
            }
            case FLOAT: 
            case DOUBLE: {
                return (double)intValue;
            }
            case REAL: {
                return Float.valueOf(intValue);
            }
            case BINARY: {
                return DDC.convertIntToBytes(intValue, valueLength);
            }
            case SQL_VARIANT: {
                if (valueLength == 1) {
                    return 0 != intValue;
                }
                if (valueLength == 3 || valueLength == 4) {
                    return (short)intValue;
                }
                return intValue;
            }
        }
        return Integer.toString(intValue);
    }

    static final Object convertLongToObject(long longVal, JDBCType jdbcType, SSType baseSSType, StreamType streamType) {
        switch (jdbcType) {
            case BIGINT: 
            case SQL_VARIANT: {
                return longVal;
            }
            case INTEGER: {
                return (int)longVal;
            }
            case SMALLINT: 
            case TINYINT: {
                return (short)longVal;
            }
            case BIT: 
            case BOOLEAN: {
                return 0L != longVal;
            }
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: {
                return new BigDecimal(Long.toString(longVal));
            }
            case FLOAT: 
            case DOUBLE: {
                return (double)longVal;
            }
            case REAL: {
                return Float.valueOf(longVal);
            }
            case BINARY: {
                byte[] convertedBytes = DDC.convertLongToBytes(longVal);
                switch (baseSSType) {
                    case BIT: 
                    case TINYINT: {
                        int bytesToReturnLength = 1;
                        byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    case SMALLINT: {
                        int bytesToReturnLength = 2;
                        byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    case INTEGER: {
                        int bytesToReturnLength = 4;
                        byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    case BIGINT: {
                        int bytesToReturnLength = 8;
                        byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                }
                return convertedBytes;
            }
            case VARBINARY: {
                switch (baseSSType) {
                    case BIGINT: {
                        return longVal;
                    }
                    case INTEGER: {
                        return (int)longVal;
                    }
                    case TINYINT: 
                    case SMALLINT: {
                        return (short)longVal;
                    }
                    case BIT: {
                        return 0L != longVal;
                    }
                    case DECIMAL: 
                    case NUMERIC: 
                    case MONEY: 
                    case SMALLMONEY: {
                        return new BigDecimal(Long.toString(longVal));
                    }
                    case FLOAT: {
                        return (double)longVal;
                    }
                    case REAL: {
                        return Float.valueOf(longVal);
                    }
                    case BINARY: {
                        return DDC.convertLongToBytes(longVal);
                    }
                }
                return Long.toString(longVal);
            }
        }
        return Long.toString(longVal);
    }

    static final byte[] convertIntToBytes(int intValue, int valueLength) {
        byte[] bytes = new byte[valueLength];
        int i = valueLength;
        while (i-- > 0) {
            bytes[i] = (byte)(intValue & 0xFF);
            intValue >>= 8;
        }
        return bytes;
    }

    static final Object convertFloatToObject(float floatVal, JDBCType jdbcType, StreamType streamType) {
        switch (jdbcType) {
            case REAL: 
            case SQL_VARIANT: {
                return Float.valueOf(floatVal);
            }
            case INTEGER: {
                return (int)floatVal;
            }
            case SMALLINT: 
            case TINYINT: {
                return (short)floatVal;
            }
            case BIT: 
            case BOOLEAN: {
                return 0 != Float.compare(0.0f, floatVal);
            }
            case BIGINT: {
                return (long)floatVal;
            }
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: {
                return new BigDecimal(Float.toString(floatVal));
            }
            case FLOAT: 
            case DOUBLE: {
                return Float.valueOf(floatVal).doubleValue();
            }
            case BINARY: {
                return DDC.convertIntToBytes(Float.floatToRawIntBits(floatVal), 4);
            }
        }
        return Float.toString(floatVal);
    }

    static final byte[] convertLongToBytes(long longValue) {
        byte[] bytes = new byte[8];
        int i = 8;
        while (i-- > 0) {
            bytes[i] = (byte)(longValue & 0xFFL);
            longValue >>= 8;
        }
        return bytes;
    }

    static final Object convertDoubleToObject(double doubleVal, JDBCType jdbcType, StreamType streamType) {
        switch (jdbcType) {
            case FLOAT: 
            case DOUBLE: 
            case SQL_VARIANT: {
                return doubleVal;
            }
            case REAL: {
                return Float.valueOf(Double.valueOf(doubleVal).floatValue());
            }
            case INTEGER: {
                return (int)doubleVal;
            }
            case SMALLINT: 
            case TINYINT: {
                return (short)doubleVal;
            }
            case BIT: 
            case BOOLEAN: {
                return 0 != Double.compare(0.0, doubleVal);
            }
            case BIGINT: {
                return (long)doubleVal;
            }
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: {
                return new BigDecimal(Double.toString(doubleVal));
            }
            case BINARY: {
                return DDC.convertLongToBytes(Double.doubleToRawLongBits(doubleVal));
            }
        }
        return Double.toString(doubleVal);
    }

    static final byte[] convertBigDecimalToBytes(BigDecimal bigDecimalVal, int scale) {
        byte[] valueBytes;
        if (bigDecimalVal == null) {
            valueBytes = new byte[]{(byte)scale, 0};
        } else {
            boolean isNegative;
            boolean bl = isNegative = bigDecimalVal.signum() < 0;
            if (bigDecimalVal.scale() < 0) {
                bigDecimalVal = bigDecimalVal.setScale(0);
            }
            BigInteger bi = bigDecimalVal.unscaledValue();
            if (isNegative) {
                bi = bi.negate();
            }
            byte[] unscaledBytes = bi.toByteArray();
            valueBytes = new byte[unscaledBytes.length + 3];
            int j = 0;
            valueBytes[j++] = (byte)bigDecimalVal.scale();
            valueBytes[j++] = (byte)(unscaledBytes.length + 1);
            valueBytes[j++] = (byte)(!isNegative ? 1 : 0);
            for (int i = unscaledBytes.length - 1; i >= 0; --i) {
                valueBytes[j++] = unscaledBytes[i];
            }
        }
        return valueBytes;
    }

    static final byte[] convertMoneyToBytes(BigDecimal bigDecimalVal, int bLength) {
        byte[] valueBytes = new byte[bLength];
        BigInteger bi = bigDecimalVal.unscaledValue();
        if (bLength == 8) {
            byte[] longbArray = new byte[bLength];
            Util.writeLong(bi.longValue(), longbArray, 0);
            System.arraycopy(longbArray, 0, valueBytes, 4, 4);
            System.arraycopy(longbArray, 4, valueBytes, 0, 4);
        } else {
            Util.writeInt(bi.intValue(), valueBytes, 0);
        }
        return valueBytes;
    }

    static final Object convertBigDecimalToObject(BigDecimal bigDecimalVal, JDBCType jdbcType, StreamType streamType) {
        switch (jdbcType) {
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: 
            case SQL_VARIANT: {
                return bigDecimalVal;
            }
            case FLOAT: 
            case DOUBLE: {
                return bigDecimalVal.doubleValue();
            }
            case REAL: {
                return Float.valueOf(bigDecimalVal.floatValue());
            }
            case INTEGER: {
                return bigDecimalVal.intValue();
            }
            case SMALLINT: 
            case TINYINT: {
                return bigDecimalVal.shortValue();
            }
            case BIT: 
            case BOOLEAN: {
                return 0 != bigDecimalVal.compareTo(BigDecimal.valueOf(0L));
            }
            case BIGINT: {
                return bigDecimalVal.longValue();
            }
            case BINARY: {
                return DDC.convertBigDecimalToBytes(bigDecimalVal, bigDecimalVal.scale());
            }
        }
        return bigDecimalVal.toString();
    }

    static final Object convertMoneyToObject(BigDecimal bigDecimalVal, JDBCType jdbcType, StreamType streamType, int numberOfBytes) {
        switch (jdbcType) {
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: {
                return bigDecimalVal;
            }
            case FLOAT: 
            case DOUBLE: {
                return bigDecimalVal.doubleValue();
            }
            case REAL: {
                return Float.valueOf(bigDecimalVal.floatValue());
            }
            case INTEGER: {
                return bigDecimalVal.intValue();
            }
            case SMALLINT: 
            case TINYINT: {
                return bigDecimalVal.shortValue();
            }
            case BIT: 
            case BOOLEAN: {
                return 0 != bigDecimalVal.compareTo(BigDecimal.valueOf(0L));
            }
            case BIGINT: {
                return bigDecimalVal.longValue();
            }
            case BINARY: {
                return DDC.convertToBytes(bigDecimalVal, bigDecimalVal.scale(), numberOfBytes);
            }
        }
        return bigDecimalVal.toString();
    }

    private static byte[] convertToBytes(BigDecimal value, int scale, int numBytes) {
        boolean isNeg = value.signum() < 0;
        value = value.setScale(scale);
        BigInteger bigInt = value.unscaledValue();
        byte[] unscaledBytes = bigInt.toByteArray();
        byte[] ret = new byte[numBytes];
        if (unscaledBytes.length < numBytes) {
            for (int i = 0; i < numBytes - unscaledBytes.length; ++i) {
                ret[i] = (byte)(isNeg ? -1 : 0);
            }
        }
        int offset = numBytes - unscaledBytes.length;
        System.arraycopy(unscaledBytes, 0, ret, offset, numBytes - offset);
        return ret;
    }

    static final Object convertBytesToObject(byte[] bytesValue, JDBCType jdbcType, TypeInfo baseTypeInfo) throws SQLServerException {
        switch (jdbcType) {
            case CHAR: {
                String str = Util.bytesToHexString(bytesValue, bytesValue.length);
                if (SSType.BINARY == baseTypeInfo.getSSType() && str.length() < baseTypeInfo.getPrecision() * 2) {
                    StringBuilder strbuf = new StringBuilder(str);
                    while (strbuf.length() < baseTypeInfo.getPrecision() * 2) {
                        strbuf.append('0');
                    }
                    return strbuf.toString();
                }
                return str;
            }
            case BINARY: 
            case VARBINARY: 
            case LONGVARBINARY: {
                if (SSType.BINARY == baseTypeInfo.getSSType() && bytesValue.length < baseTypeInfo.getPrecision()) {
                    byte[] newBytes = new byte[baseTypeInfo.getPrecision()];
                    System.arraycopy(bytesValue, 0, newBytes, 0, bytesValue.length);
                    return newBytes;
                }
                return bytesValue;
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
        throw new SQLServerException(form.format(new Object[]{baseTypeInfo.getSSType().name(), jdbcType}), null, 0, null);
    }

    static final Object convertStringToObject(String stringVal, Charset charset, JDBCType jdbcType, StreamType streamType) throws UnsupportedEncodingException {
        switch (jdbcType) {
            case DECIMAL: 
            case NUMERIC: 
            case MONEY: 
            case SMALLMONEY: {
                return new BigDecimal(stringVal.trim());
            }
            case FLOAT: 
            case DOUBLE: {
                return Double.valueOf(stringVal.trim());
            }
            case REAL: {
                return Float.valueOf(stringVal.trim());
            }
            case INTEGER: {
                return Integer.valueOf(stringVal.trim());
            }
            case SMALLINT: 
            case TINYINT: {
                return Short.valueOf(stringVal.trim());
            }
            case BIT: 
            case BOOLEAN: {
                String trimmedString = stringVal.trim();
                return 1 == trimmedString.length() ? Boolean.valueOf('1' == trimmedString.charAt(0)) : Boolean.valueOf(trimmedString);
            }
            case BIGINT: {
                return Long.valueOf(stringVal.trim());
            }
            case TIMESTAMP: {
                return Timestamp.valueOf(stringVal.trim());
            }
            case LOCALDATETIME: {
                return DDC.parseStringIntoLDT(stringVal.trim());
            }
            case DATE: {
                return Date.valueOf(DDC.getDatePart(stringVal.trim()));
            }
            case TIME: {
                Timestamp ts = Timestamp.valueOf("1970-01-01 " + DDC.getTimePart(stringVal.trim()));
                GregorianCalendar cal = new GregorianCalendar(Locale.US);
                cal.clear();
                cal.setTimeInMillis(ts.getTime());
                if (ts.getNanos() % 1000000 >= 500000) {
                    cal.add(14, 1);
                }
                cal.set(1970, 0, 1);
                return new Time(cal.getTimeInMillis());
            }
            case BINARY: {
                return stringVal.getBytes(charset);
            }
        }
        switch (streamType) {
            case CHARACTER: {
                return new StringReader(stringVal);
            }
            case ASCII: {
                return new ByteArrayInputStream(stringVal.getBytes(StandardCharsets.US_ASCII));
            }
            case BINARY: {
                return new ByteArrayInputStream(stringVal.getBytes());
            }
        }
        return stringVal;
    }

    private static LocalDateTime parseStringIntoLDT(String s) {
        int second;
        int minute;
        int hour;
        int YEAR_LENGTH = 4;
        int MONTH_LENGTH = 2;
        int DAY_LENGTH = 2;
        int MAX_MONTH = 12;
        int MAX_DAY = 31;
        int year = 0;
        int month = 0;
        int day = 0;
        int nanos = 0;
        String formatError = "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]";
        if (s == null) {
            throw new IllegalArgumentException("null string");
        }
        int dividingSpace = (s = s.trim()).indexOf(32);
        if (dividingSpace < 0) {
            throw new IllegalArgumentException(formatError);
        }
        int firstDash = s.indexOf(45);
        int secondDash = s.indexOf(45, firstDash + 1);
        int firstColon = s.indexOf(58, dividingSpace + 1);
        int secondColon = s.indexOf(58, firstColon + 1);
        int period = s.indexOf(46, secondColon + 1);
        boolean parsedDate = false;
        if (firstDash > 0 && secondDash > 0 && secondDash < dividingSpace - 1 && firstDash == 4 && secondDash - firstDash > 1 && secondDash - firstDash <= 3 && dividingSpace - secondDash > 1 && dividingSpace - secondDash <= 3) {
            year = Integer.parseInt(s.substring(0, firstDash));
            month = Integer.parseInt(s.substring(firstDash + 1, secondDash));
            day = Integer.parseInt(s.substring(secondDash + 1, dividingSpace));
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                parsedDate = true;
            }
        }
        if (!parsedDate) {
            throw new IllegalArgumentException(formatError);
        }
        int len = s.length();
        if (firstColon > 0 && secondColon > 0 && secondColon < len - 1) {
            hour = Integer.parseInt(s.substring(dividingSpace + 1, firstColon));
            minute = Integer.parseInt(s.substring(firstColon + 1, secondColon));
            if (period > 0 && period < len - 1) {
                second = Integer.parseInt(s.substring(secondColon + 1, period));
                int nanoPrecision = len - (period + 1);
                if (nanoPrecision > 9) {
                    throw new IllegalArgumentException(formatError);
                }
                if (!Character.isDigit(s.charAt(period + 1))) {
                    throw new IllegalArgumentException(formatError);
                }
                int tmpNanos = Integer.parseInt(s.substring(period + 1, len));
                while (nanoPrecision < 9) {
                    tmpNanos *= 10;
                    ++nanoPrecision;
                }
                nanos = tmpNanos;
            } else {
                if (period > 0) {
                    throw new IllegalArgumentException(formatError);
                }
                second = Integer.parseInt(s.substring(secondColon + 1, len));
            }
        } else {
            throw new IllegalArgumentException(formatError);
        }
        return LocalDateTime.of(year, month, day, hour, minute, second, nanos);
    }

    static final Object convertStreamToObject(BaseInputStream stream, TypeInfo typeInfo, JDBCType jdbcType, InputStreamGetterArgs getterArgs) throws SQLServerException {
        if (null == stream) {
            return null;
        }
        assert (null != typeInfo);
        assert (null != getterArgs);
        SSType ssType = typeInfo.getSSType();
        try {
            switch (jdbcType) {
                case CLOB: {
                    return new SQLServerClob(stream, typeInfo);
                }
                case NCLOB: {
                    return new SQLServerNClob(stream, typeInfo);
                }
                case SQLXML: {
                    return new SQLServerSQLXML(stream, getterArgs, typeInfo);
                }
                case BINARY: 
                case VARBINARY: 
                case LONGVARBINARY: 
                case BLOB: {
                    if (StreamType.BINARY == getterArgs.streamType) {
                        return stream;
                    }
                    if (JDBCType.BLOB == jdbcType) {
                        return new SQLServerBlob(stream);
                    }
                    return stream.getBytes();
                }
            }
            if (SSType.BINARY == ssType || SSType.VARBINARY == ssType || SSType.VARBINARYMAX == ssType || SSType.TIMESTAMP == ssType || SSType.IMAGE == ssType || SSType.UDT == ssType) {
                if (StreamType.ASCII == getterArgs.streamType) {
                    return stream;
                }
                assert (StreamType.CHARACTER == getterArgs.streamType || StreamType.NONE == getterArgs.streamType);
                byte[] byteValue = stream.getBytes();
                if (JDBCType.GUID == jdbcType) {
                    return Util.readGUID(byteValue);
                }
                if (JDBCType.GEOMETRY == jdbcType) {
                    if (!typeInfo.getSSTypeName().equalsIgnoreCase(jdbcType.toString())) {
                        DataTypes.throwConversionError(typeInfo.getSSTypeName().toUpperCase(), jdbcType.toString());
                    }
                    return Geometry.STGeomFromWKB(byteValue);
                }
                if (JDBCType.GEOGRAPHY == jdbcType) {
                    if (!typeInfo.getSSTypeName().equalsIgnoreCase(jdbcType.toString())) {
                        DataTypes.throwConversionError(typeInfo.getSSTypeName().toUpperCase(), jdbcType.toString());
                    }
                    return Geography.STGeomFromWKB(byteValue);
                }
                String hexString = Util.bytesToHexString(byteValue, byteValue.length);
                if (StreamType.NONE == getterArgs.streamType) {
                    return hexString;
                }
                return new StringReader(hexString);
            }
            if (StreamType.ASCII == getterArgs.streamType) {
                if (typeInfo.supportsFastAsciiConversion()) {
                    return new AsciiFilteredInputStream(stream);
                }
                if (getterArgs.isAdaptive) {
                    return AsciiFilteredUnicodeInputStream.makeAsciiFilteredUnicodeInputStream(stream, new BufferedReader(new InputStreamReader((InputStream)stream, typeInfo.getCharset())));
                }
                return new ByteArrayInputStream(new String(stream.getBytes(), typeInfo.getCharset()).getBytes(StandardCharsets.US_ASCII));
            }
            if (StreamType.CHARACTER == getterArgs.streamType || StreamType.NCHARACTER == getterArgs.streamType) {
                if (getterArgs.isAdaptive) {
                    return new BufferedReader(new InputStreamReader((InputStream)stream, typeInfo.getCharset()));
                }
                return new StringReader(new String(stream.getBytes(), typeInfo.getCharset()));
            }
            return DDC.convertStringToObject(new String(stream.getBytes(), typeInfo.getCharset()), typeInfo.getCharset(), jdbcType, getterArgs.streamType);
        }
        catch (UnsupportedEncodingException | IllegalArgumentException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
            throw new SQLServerException(form.format(new Object[]{typeInfo.getSSType(), jdbcType}), null, 0, (Throwable)e);
        }
    }

    private static String getDatePart(String s) {
        int sp = s.indexOf(32);
        if (-1 == sp) {
            return s;
        }
        return s.substring(0, sp);
    }

    private static String getTimePart(String s) {
        int sp = s.indexOf(32);
        if (-1 == sp) {
            return s;
        }
        return s.substring(sp + 1);
    }

    private static String fractionalSecondsString(long subSecondNanos, int scale) {
        assert (0L <= subSecondNanos && subSecondNanos < 1000000000L);
        assert (0 <= scale && scale <= 7);
        if (0 == scale) {
            return "";
        }
        return BigDecimal.valueOf(subSecondNanos % 1000000000L, 9).setScale(scale).toPlainString().substring(1);
    }

    static final Object convertTemporalToObject(JDBCType jdbcType, SSType ssType, Calendar timeZoneCalendar, int daysSinceBaseDate, long ticksSinceMidnight, int fractionalSecondsScale) throws SQLServerException {
        int subSecondNanos;
        if (null == timeZoneCalendar) {
            return DDC.convertTemporalToObject(jdbcType, ssType, daysSinceBaseDate, ticksSinceMidnight, fractionalSecondsScale);
        }
        TimeZone localTimeZone = timeZoneCalendar.getTimeZone();
        TimeZone componentTimeZone = SSType.DATETIMEOFFSET == ssType ? UTC.timeZone : localTimeZone;
        GregorianCalendar cal = new GregorianCalendar(componentTimeZone, Locale.US);
        cal.setLenient(true);
        cal.clear();
        switch (ssType) {
            case TIME: {
                cal.set(1900, 0, 1, 0, 0, 0);
                cal.set(14, (int)(ticksSinceMidnight / 1000000L));
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATE: 
            case DATETIME2: 
            case DATETIMEOFFSET: {
                if (daysSinceBaseDate >= GregorianChange.DAYS_SINCE_BASE_DATE_HINT) {
                    cal.set(1, 0, 1 + daysSinceBaseDate + GregorianChange.EXTRA_DAYS_TO_BE_ADDED, 0, 0, 0);
                    cal.set(14, (int)(ticksSinceMidnight / 1000000L));
                } else {
                    cal.setGregorianChange(GregorianChange.PURE_CHANGE_DATE);
                    cal.set(1, 0, 1 + daysSinceBaseDate, 0, 0, 0);
                    cal.set(14, (int)(ticksSinceMidnight / 1000000L));
                    int year = cal.get(1);
                    int month = cal.get(2);
                    int date = cal.get(5);
                    int hour = cal.get(11);
                    int minute = cal.get(12);
                    int second = cal.get(13);
                    int millis = cal.get(14);
                    cal.setGregorianChange(GregorianChange.STANDARD_CHANGE_DATE);
                    cal.set(year, month, date, hour, minute, second);
                    cal.set(14, millis);
                }
                if (SSType.DATETIMEOFFSET == ssType && !componentTimeZone.hasSameRules(localTimeZone)) {
                    GregorianCalendar localCalendar = new GregorianCalendar(localTimeZone, Locale.US);
                    localCalendar.clear();
                    localCalendar.setTimeInMillis(cal.getTimeInMillis());
                    cal = localCalendar;
                }
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATETIME: {
                cal.set(1900, 0, 1 + daysSinceBaseDate, 0, 0, 0);
                cal.set(14, (int)ticksSinceMidnight);
                subSecondNanos = (int)(ticksSinceMidnight * 1000000L % 1000000000L);
                break;
            }
            default: {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
            }
        }
        int localMillisOffset = timeZoneCalendar.get(15);
        switch (jdbcType.category) {
            case BINARY: 
            case SQL_VARIANT: {
                switch (ssType) {
                    case DATE: {
                        cal.set(11, 0);
                        cal.set(12, 0);
                        cal.set(13, 0);
                        cal.set(14, 0);
                        return new Date(cal.getTimeInMillis());
                    }
                    case DATETIME2: 
                    case DATETIME: {
                        Timestamp ts = new Timestamp(cal.getTimeInMillis());
                        ts.setNanos(subSecondNanos);
                        return ts;
                    }
                    case DATETIMEOFFSET: {
                        assert (SSType.DATETIMEOFFSET == ssType);
                        assert (0 == localMillisOffset % 60000);
                        Timestamp ts1 = new Timestamp(cal.getTimeInMillis());
                        ts1.setNanos(subSecondNanos);
                        return DateTimeOffset.valueOf(ts1, localMillisOffset / 60000);
                    }
                    case TIME: {
                        if (subSecondNanos % 1000000 >= 500000) {
                            cal.add(14, 1);
                        }
                        cal.set(1970, 0, 1);
                        return new Time(cal.getTimeInMillis());
                    }
                }
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
            }
            case DATE: {
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                return new Date(cal.getTimeInMillis());
            }
            case TIME: {
                if (subSecondNanos % 1000000 >= 500000) {
                    cal.add(14, 1);
                }
                cal.set(1970, 0, 1);
                return new Time(cal.getTimeInMillis());
            }
            case TIMESTAMP: {
                Timestamp ts2 = new Timestamp(cal.getTimeInMillis());
                ts2.setNanos(subSecondNanos);
                if (jdbcType == JDBCType.LOCALDATETIME) {
                    return ts2.toLocalDateTime();
                }
                return ts2;
            }
            case DATETIMEOFFSET: {
                assert (SSType.DATETIMEOFFSET == ssType);
                assert (0 == localMillisOffset % 60000);
                Timestamp ts = new Timestamp(cal.getTimeInMillis());
                ts.setNanos(subSecondNanos);
                return DateTimeOffset.valueOf(ts, localMillisOffset / 60000);
            }
            case CHARACTER: {
                switch (ssType) {
                    case DATE: {
                        return String.format(Locale.US, "%1$tF", cal);
                    }
                    case TIME: {
                        return String.format(Locale.US, "%1$tT%2$s", cal, DDC.fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIME2: {
                        return String.format(Locale.US, "%1$tF %1$tT%2$s", cal, DDC.fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIMEOFFSET: {
                        assert (0 == localMillisOffset % 60000);
                        int unsignedMinutesOffset = Math.abs(localMillisOffset / 60000);
                        return String.format(Locale.US, "%1$tF %1$tT%2$s %3$c%4$02d:%5$02d", cal, DDC.fractionalSecondsString(subSecondNanos, fractionalSecondsScale), Character.valueOf(localMillisOffset >= 0 ? (char)'+' : '-'), unsignedMinutesOffset / 60, unsignedMinutesOffset % 60);
                    }
                    case DATETIME: {
                        return new Timestamp(cal.getTimeInMillis()).toString();
                    }
                }
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
        throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
    }

    private static Object convertTemporalToObject(JDBCType jdbcType, SSType ssType, int daysSinceBaseDate, long ticksSinceMidnight, int fractionalSecondsScale) throws SQLServerException {
        int subSecondNanos;
        LocalDateTime ldt;
        switch (ssType) {
            case TIME: {
                ldt = LocalDateTime.of(TDS.BASE_LOCAL_DATE_1900, LocalTime.ofNanoOfDay(ticksSinceMidnight));
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATE: 
            case DATETIME2: 
            case DATETIMEOFFSET: {
                LocalDate ld1 = TDS.BASE_LOCAL_DATE.plusDays(daysSinceBaseDate);
                if (ticksSinceMidnight == 0L) {
                    ldt = LocalDateTime.of(ld1, LocalTime.MIN);
                    subSecondNanos = 0;
                    break;
                }
                ldt = LocalDateTime.of(ld1, LocalTime.ofNanoOfDay(ticksSinceMidnight));
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATETIME: {
                LocalDate ld2 = TDS.BASE_LOCAL_DATE_1900.plusDays(daysSinceBaseDate);
                if (ticksSinceMidnight == 0L) {
                    ldt = LocalDateTime.of(ld2, LocalTime.MIN);
                    subSecondNanos = 0;
                    break;
                }
                long nanoOfDay = ticksSinceMidnight * 1000000L;
                ldt = nanoOfDay > LocalTime.MAX.toNanoOfDay() ? LocalDateTime.of(ld2, LocalTime.MIN).plusNanos(nanoOfDay) : LocalDateTime.of(ld2, LocalTime.ofNanoOfDay(nanoOfDay));
                subSecondNanos = (int)(nanoOfDay % 1000000000L);
                break;
            }
            default: {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
            }
        }
        switch (jdbcType.category) {
            case BINARY: 
            case SQL_VARIANT: {
                switch (ssType) {
                    case DATE: {
                        return Date.valueOf(ldt.toLocalDate());
                    }
                    case DATETIME2: 
                    case DATETIME: {
                        Timestamp ts = Timestamp.valueOf(ldt);
                        ts.setNanos(subSecondNanos);
                        return ts;
                    }
                    case TIME: {
                        if (subSecondNanos % 1000000 >= 500000) {
                            ldt = ldt.plusNanos(1000000L);
                        }
                        Time t = Time.valueOf(ldt.toLocalTime());
                        t.setTime(t.getTime() + (long)(ldt.getNano() / 1000000));
                        return t;
                    }
                }
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
            }
            case DATE: {
                return Date.valueOf(ldt.toLocalDate());
            }
            case TIME: {
                if (subSecondNanos % 1000000 >= 500000) {
                    ldt = ldt.plusNanos(1000000L);
                }
                Time t = Time.valueOf(ldt.toLocalTime());
                t.setTime(t.getTime() + (long)(ldt.getNano() / 1000000));
                return t;
            }
            case TIMESTAMP: {
                if (jdbcType == JDBCType.LOCALDATETIME) {
                    return ldt;
                }
                Timestamp ts = Timestamp.valueOf(ldt);
                ts.setNanos(subSecondNanos);
                return ts;
            }
            case CHARACTER: {
                switch (ssType) {
                    case DATE: {
                        return String.format(Locale.US, "%1$tF", Timestamp.valueOf(ldt));
                    }
                    case TIME: {
                        return String.format(Locale.US, "%1$tT%2$s", ldt, DDC.fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIME2: {
                        return String.format(Locale.US, "%1$tF %1$tT%2$s", Timestamp.valueOf(ldt), DDC.fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIME: {
                        return Timestamp.valueOf(ldt).toString();
                    }
                }
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
        throw new SQLServerException(form.format(new Object[]{ssType.name(), jdbcType}), null, 0, null);
    }

    static int daysSinceBaseDate(int year, int dayOfYear, int baseYear) {
        assert (year >= 1);
        assert (baseYear >= 1);
        assert (dayOfYear >= 1);
        return dayOfYear - 1 + (year - baseYear) * 365 + DDC.leapDaysBeforeYear(year) - DDC.leapDaysBeforeYear(baseYear);
    }

    private static int leapDaysBeforeYear(int year) {
        assert (year >= 1);
        return (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400;
    }

    static final boolean exceedsMaxRPCDecimalPrecisionOrScale(BigDecimal bigDecimalValue) {
        BigInteger bi;
        if (null == bigDecimalValue) {
            return false;
        }
        if (bigDecimalValue.scale() > 38) {
            return true;
        }
        BigInteger bigInteger = bi = bigDecimalValue.scale() < 0 ? bigDecimalValue.setScale(0).unscaledValue() : bigDecimalValue.unscaledValue();
        if (bigDecimalValue.signum() < 0) {
            bi = bi.negate();
        }
        return bi.compareTo(maxRPCDecimalValue) > 0;
    }

    static String convertReaderToString(Reader reader, int readerLength) throws SQLServerException {
        assert (-1 == readerLength || readerLength >= 0);
        if (null == reader) {
            return null;
        }
        if (0 == readerLength) {
            return "";
        }
        try {
            int readChars;
            StringBuilder sb = new StringBuilder(-1 != readerLength ? readerLength : 4000);
            char[] charArray = new char[-1 != readerLength && readerLength < 4000 ? readerLength : 4000];
            while ((readChars = reader.read(charArray, 0, charArray.length)) > 0) {
                if (readChars > charArray.length) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    Object[] msgArgs = new Object[]{SQLServerException.getErrString("R_streamReadReturnedInvalidValue")};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                }
                sb.append(charArray, 0, readChars);
            }
            return sb.toString();
        }
        catch (IOException ioEx) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
            Object[] msgArgs = new Object[]{ioEx.toString()};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
            return null;
        }
    }
}

