/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Encoding;
import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.ParameterUtils;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerDriverIntProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SSType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

final class Util {
    static final String SYSTEM_SPEC_VERSION = System.getProperty("java.specification.version");
    static final char[] HEXCHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final String WSID_NOT_AVAILABLE = "";
    static final String ACTIVITY_ID_TRACE_PROPERTY = "com.microsoft.sqlserver.jdbc.traceactivity";
    static final String SYSTEM_JRE = System.getProperty("java.vendor") + " " + System.getProperty("java.version");
    private static final Lock LOCK = new ReentrantLock();

    private Util() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static boolean isIBM() {
        String vmName = System.getProperty("java.vm.name");
        return SYSTEM_JRE.startsWith("IBM") && vmName.startsWith("IBM");
    }

    static String getJVMArchOnWindows() {
        return System.getProperty("os.arch").contains("64") ? "x64" : "x86";
    }

    static final boolean isCharType(int jdbcType) {
        switch (jdbcType) {
            case -16: 
            case -15: 
            case -9: 
            case -1: 
            case 1: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    static final Boolean isCharType(SSType ssType) {
        switch (ssType) {
            case CHAR: 
            case NCHAR: 
            case VARCHAR: 
            case NVARCHAR: 
            case VARCHARMAX: 
            case NVARCHARMAX: {
                return true;
            }
        }
        return false;
    }

    static final Boolean isBinaryType(SSType ssType) {
        switch (ssType) {
            case BINARY: 
            case VARBINARY: 
            case VARBINARYMAX: 
            case IMAGE: {
                return true;
            }
        }
        return false;
    }

    static final Boolean isBinaryType(int jdbcType) {
        switch (jdbcType) {
            case -4: 
            case -3: 
            case -2: {
                return true;
            }
        }
        return false;
    }

    static short readShort(byte[] data, int nOffset) {
        return (short)(data[nOffset] & 0xFF | (data[nOffset + 1] & 0xFF) << 8);
    }

    static int readUnsignedShort(byte[] data, int nOffset) {
        return data[nOffset] & 0xFF | (data[nOffset + 1] & 0xFF) << 8;
    }

    static int readUnsignedShortBigEndian(byte[] data, int nOffset) {
        return (data[nOffset] & 0xFF) << 8 | data[nOffset + 1] & 0xFF;
    }

    static void writeShort(short value, byte[] valueBytes, int offset) {
        valueBytes[offset + 0] = (byte)(value >> 0 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 8 & 0xFF);
    }

    static void writeShortBigEndian(short value, byte[] valueBytes, int offset) {
        valueBytes[offset + 0] = (byte)(value >> 8 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 0 & 0xFF);
    }

    static int readInt(byte[] data, int nOffset) {
        int b1 = data[nOffset + 0] & 0xFF;
        int b2 = (data[nOffset + 1] & 0xFF) << 8;
        int b3 = (data[nOffset + 2] & 0xFF) << 16;
        int b4 = (data[nOffset + 3] & 0xFF) << 24;
        return b4 | b3 | b2 | b1;
    }

    static int readIntBigEndian(byte[] data, int nOffset) {
        return (data[nOffset + 3] & 0xFF) << 0 | (data[nOffset + 2] & 0xFF) << 8 | (data[nOffset + 1] & 0xFF) << 16 | (data[nOffset + 0] & 0xFF) << 24;
    }

    static void writeInt(int value, byte[] valueBytes, int offset) {
        valueBytes[offset + 0] = (byte)(value >> 0 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 8 & 0xFF);
        valueBytes[offset + 2] = (byte)(value >> 16 & 0xFF);
        valueBytes[offset + 3] = (byte)(value >> 24 & 0xFF);
    }

    static void writeIntBigEndian(int value, byte[] valueBytes, int offset) {
        valueBytes[offset + 0] = (byte)(value >> 24 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 16 & 0xFF);
        valueBytes[offset + 2] = (byte)(value >> 8 & 0xFF);
        valueBytes[offset + 3] = (byte)(value >> 0 & 0xFF);
    }

    static void writeLongBigEndian(long value, byte[] valueBytes, int offset) {
        valueBytes[offset + 0] = (byte)(value >> 56 & 0xFFL);
        valueBytes[offset + 1] = (byte)(value >> 48 & 0xFFL);
        valueBytes[offset + 2] = (byte)(value >> 40 & 0xFFL);
        valueBytes[offset + 3] = (byte)(value >> 32 & 0xFFL);
        valueBytes[offset + 4] = (byte)(value >> 24 & 0xFFL);
        valueBytes[offset + 5] = (byte)(value >> 16 & 0xFFL);
        valueBytes[offset + 6] = (byte)(value >> 8 & 0xFFL);
        valueBytes[offset + 7] = (byte)(value >> 0 & 0xFFL);
    }

    static BigDecimal readBigDecimal(byte[] valueBytes, int valueLength, int scale) {
        int sign = 0 == valueBytes[0] ? -1 : 1;
        byte[] magnitude = new byte[valueLength - 1];
        for (int i = 1; i <= magnitude.length; ++i) {
            magnitude[magnitude.length - i] = valueBytes[i];
        }
        return new BigDecimal(new BigInteger(sign, magnitude), scale);
    }

    static long readLong(byte[] data, int nOffset) {
        return (long)(data[nOffset + 7] & 0xFF) << 56 | (long)(data[nOffset + 6] & 0xFF) << 48 | (long)(data[nOffset + 5] & 0xFF) << 40 | (long)(data[nOffset + 4] & 0xFF) << 32 | (long)(data[nOffset + 3] & 0xFF) << 24 | (long)(data[nOffset + 2] & 0xFF) << 16 | (long)(data[nOffset + 1] & 0xFF) << 8 | (long)(data[nOffset] & 0xFF);
    }

    static void writeLong(long value, byte[] valueBytes, int offset) {
        valueBytes[offset++] = (byte)(value & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 8 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 16 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 24 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 32 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 40 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 48 & 0xFFL);
        valueBytes[offset] = (byte)(value >> 56 & 0xFFL);
    }

    static Properties parseUrl(String url, Logger logger) throws SQLServerException {
        String property;
        Properties p = new Properties();
        String tmpUrl = url;
        String sPrefix = "jdbc:sqlserver://";
        StringBuilder result = new StringBuilder();
        String name = WSID_NOT_AVAILABLE;
        String value = WSID_NOT_AVAILABLE;
        if (!tmpUrl.startsWith(sPrefix)) {
            return null;
        }
        tmpUrl = tmpUrl.substring(sPrefix.length());
        boolean inStart = false;
        boolean inServerName = true;
        int inPort = 2;
        int inInstanceName = 3;
        int inEscapedValueStart = 4;
        int inEscapedValueEnd = 5;
        int inValue = 6;
        int inName = 7;
        int state = 0;
        block18: for (int i = 0; i < tmpUrl.length(); ++i) {
            char ch = tmpUrl.charAt(i);
            switch (state) {
                case 0: {
                    if (ch == ';') {
                        state = 7;
                        continue block18;
                    }
                    result.append(ch);
                    state = 1;
                    continue block18;
                }
                case 1: {
                    if (ch == ';' || ch == ':' || ch == '\\') {
                        property = result.toString().trim();
                        if (property.contains("=")) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorServerName"));
                            Object[] msgArgs = new Object[]{property};
                            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                        }
                        if (property.length() > 0) {
                            p.put(SQLServerDriverStringProperty.SERVER_NAME.toString(), property);
                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine("Property:serverName Value:" + property);
                            }
                        }
                        result.setLength(0);
                        if (ch == ';') {
                            state = 7;
                            continue block18;
                        }
                        if (ch == ':') {
                            state = 2;
                            continue block18;
                        }
                        state = 3;
                        continue block18;
                    }
                    result.append(ch);
                    continue block18;
                }
                case 2: {
                    if (ch == ';') {
                        property = result.toString().trim();
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Property:portNumber Value:" + property);
                        }
                        p.put(SQLServerDriverIntProperty.PORT_NUMBER.toString(), property);
                        result.setLength(0);
                        state = 7;
                        continue block18;
                    }
                    result.append(ch);
                    continue block18;
                }
                case 3: {
                    if (ch == ';' || ch == ':') {
                        property = result.toString().trim();
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Property:instanceName Value:" + property);
                        }
                        p.put(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), property.toLowerCase(Locale.US));
                        result.setLength(0);
                        if (ch == ';') {
                            state = 7;
                            continue block18;
                        }
                        state = 2;
                        continue block18;
                    }
                    result.append(ch);
                    continue block18;
                }
                case 7: {
                    if (ch == '=') {
                        if ((name = name.trim()).length() <= 0) {
                            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        }
                        state = 6;
                        continue block18;
                    }
                    if (ch == ';') {
                        if ((name = name.trim()).length() <= 0) continue block18;
                        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        continue block18;
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(name);
                    builder.append(ch);
                    name = builder.toString();
                    continue block18;
                }
                case 6: {
                    if (ch == ';') {
                        value = value.trim();
                        if (null != (name = SQLServerDriver.getNormalizedPropertyName(name, logger))) {
                            if (logger.isLoggable(Level.FINE) && !name.equals(SQLServerDriverStringProperty.USER.toString())) {
                                if (!name.toLowerCase(Locale.ENGLISH).contains("password") && !name.toLowerCase(Locale.ENGLISH).contains("keystoresecret")) {
                                    logger.fine("Property:" + name + " Value:" + value);
                                } else {
                                    logger.fine("Property:" + name);
                                }
                            }
                            p.put(name, value);
                        }
                        name = WSID_NOT_AVAILABLE;
                        value = WSID_NOT_AVAILABLE;
                        state = 7;
                        continue block18;
                    }
                    if (ch == '{') {
                        state = 4;
                        if ((value = value.trim()).length() <= 0) continue block18;
                        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        continue block18;
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(value);
                    builder.append(ch);
                    value = builder.toString();
                    continue block18;
                }
                case 4: {
                    StringBuilder builder;
                    if (ch == '}' && i + 1 < tmpUrl.length() && tmpUrl.charAt(i + 1) == '}') {
                        builder = new StringBuilder();
                        builder.append(value);
                        builder.append(ch);
                        value = builder.toString();
                        ++i;
                        continue block18;
                    }
                    if (ch == '}') {
                        if (null != (name = SQLServerDriver.getNormalizedPropertyName(name, logger))) {
                            if (logger.isLoggable(Level.FINE) && !name.equals(SQLServerDriverStringProperty.USER.toString()) && !name.equals(SQLServerDriverStringProperty.PASSWORD.toString())) {
                                logger.fine("Property:" + name + " Value:" + value);
                            }
                            p.put(name, value);
                        }
                        name = WSID_NOT_AVAILABLE;
                        value = WSID_NOT_AVAILABLE;
                        state = 5;
                        continue block18;
                    }
                    builder = new StringBuilder();
                    builder.append(value);
                    builder.append(ch);
                    value = builder.toString();
                    continue block18;
                }
                case 5: {
                    if (ch == ';') {
                        state = 7;
                        continue block18;
                    }
                    if (ch == ' ') continue block18;
                    SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                    continue block18;
                }
                default: {
                    assert (false) : "parseURL: Invalid state " + state;
                    continue block18;
                }
            }
        }
        switch (state) {
            case 1: {
                property = result.toString().trim();
                if (property.length() <= 0) break;
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Property:serverName Value:" + property);
                }
                p.put(SQLServerDriverStringProperty.SERVER_NAME.toString(), property);
                break;
            }
            case 2: {
                property = result.toString().trim();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Property:portNumber Value:" + property);
                }
                p.put(SQLServerDriverIntProperty.PORT_NUMBER.toString(), property);
                break;
            }
            case 3: {
                property = result.toString().trim();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Property:instanceName Value:" + property);
                }
                p.put(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), property);
                break;
            }
            case 6: {
                value = value.trim();
                name = SQLServerDriver.getNormalizedPropertyName(name, logger);
                if (null == name) break;
                if (logger.isLoggable(Level.FINE) && !name.equals(SQLServerDriverStringProperty.USER.toString()) && !name.equals(SQLServerDriverStringProperty.PASSWORD.toString()) && !name.equals(SQLServerDriverStringProperty.KEY_STORE_SECRET.toString())) {
                    logger.fine("Property:" + name + " Value:" + value);
                }
                p.put(name, value);
                break;
            }
            case 0: 
            case 5: {
                break;
            }
            case 7: {
                name = name.trim();
                if (name.length() <= 0) break;
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                break;
            }
            default: {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
            }
        }
        return p;
    }

    static String escapeSQLId(String inID) {
        StringBuilder outID = new StringBuilder(inID.length() + 2);
        outID.append('[');
        for (int i = 0; i < inID.length(); ++i) {
            char ch = inID.charAt(i);
            if (']' == ch) {
                outID.append("]]");
                continue;
            }
            outID.append(ch);
        }
        outID.append(']');
        return outID.toString();
    }

    static void checkDuplicateColumnName(String columnName, Set<String> columnNames) throws SQLServerException {
        if (!columnNames.add(columnName)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPDuplicateColumnName"));
            Object[] msgArgs = new Object[]{columnName};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
    }

    static String readUnicodeString(byte[] b, int offset, int byteLength, SQLServerConnection conn) throws SQLServerException {
        try {
            return new String(b, offset, byteLength, Encoding.UNICODE.charset());
        }
        catch (IndexOutOfBoundsException ex) {
            String txtMsg = SQLServerException.checkAndAppendClientConnId(SQLServerException.getErrString("R_stringReadError"), conn);
            MessageFormat form = new MessageFormat(txtMsg);
            Object[] msgArgs = new Object[]{offset};
            throw new SQLServerException(form.format(msgArgs), null, 0, (Throwable)ex);
        }
    }

    static String byteToHexDisplayString(byte[] b) {
        if (null == b) {
            return "(null)";
        }
        StringBuilder sb = new StringBuilder(b.length * 2 + 2);
        sb.append("0x");
        for (byte aB : b) {
            int hexVal = aB & 0xFF;
            sb.append(HEXCHARS[(hexVal & 0xF0) >> 4]);
            sb.append(HEXCHARS[hexVal & 0xF]);
        }
        return sb.toString();
    }

    static String bytesToHexString(byte[] b, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; ++i) {
            int hexVal = b[i] & 0xFF;
            sb.append(HEXCHARS[(hexVal & 0xF0) >> 4]);
            sb.append(HEXCHARS[hexVal & 0xF]);
        }
        return sb.toString();
    }

    static String lookupHostName() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (null != localAddress) {
                String value = localAddress.getHostName();
                if (null != value && value.length() > 0) {
                    return value;
                }
                value = localAddress.getHostAddress();
                if (null != value && value.length() > 0) {
                    return value;
                }
            }
        }
        catch (UnknownHostException e) {
            return WSID_NOT_AVAILABLE;
        }
        return WSID_NOT_AVAILABLE;
    }

    static final byte[] asGuidByteArray(UUID aId) {
        long msb = aId.getMostSignificantBits();
        long lsb = aId.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        Util.writeLongBigEndian(msb, buffer, 0);
        Util.writeLongBigEndian(lsb, buffer, 8);
        byte tmpByte = buffer[0];
        buffer[0] = buffer[3];
        buffer[3] = tmpByte;
        tmpByte = buffer[1];
        buffer[1] = buffer[2];
        buffer[2] = tmpByte;
        tmpByte = buffer[4];
        buffer[4] = buffer[5];
        buffer[5] = tmpByte;
        tmpByte = buffer[6];
        buffer[6] = buffer[7];
        buffer[7] = tmpByte;
        return buffer;
    }

    static final UUID readGUIDtoUUID(byte[] inputGUID) throws SQLServerException {
        if (inputGUID.length != 16) {
            throw new SQLServerException("guid length must be 16", null);
        }
        byte tmpByte = inputGUID[0];
        inputGUID[0] = inputGUID[3];
        inputGUID[3] = tmpByte;
        tmpByte = inputGUID[1];
        inputGUID[1] = inputGUID[2];
        inputGUID[2] = tmpByte;
        tmpByte = inputGUID[4];
        inputGUID[4] = inputGUID[5];
        inputGUID[5] = tmpByte;
        tmpByte = inputGUID[6];
        inputGUID[6] = inputGUID[7];
        inputGUID[7] = tmpByte;
        long msb = 0L;
        for (int i = 0; i < 8; ++i) {
            msb = msb << 8 | (long)inputGUID[i] & 0xFFL;
        }
        long lsb = 0L;
        for (int i = 8; i < 16; ++i) {
            lsb = lsb << 8 | (long)inputGUID[i] & 0xFFL;
        }
        return new UUID(msb, lsb);
    }

    static final String readGUID(byte[] inputGUID) {
        int i;
        String guidTemplate = "NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN";
        byte[] guid = inputGUID;
        StringBuilder sb = new StringBuilder(guidTemplate.length());
        for (i = 0; i < 4; ++i) {
            sb.append(HEXCHARS[(guid[3 - i] & 0xF0) >> 4]);
            sb.append(HEXCHARS[guid[3 - i] & 0xF]);
        }
        sb.append('-');
        for (i = 0; i < 2; ++i) {
            sb.append(HEXCHARS[(guid[5 - i] & 0xF0) >> 4]);
            sb.append(HEXCHARS[guid[5 - i] & 0xF]);
        }
        sb.append('-');
        for (i = 0; i < 2; ++i) {
            sb.append(HEXCHARS[(guid[7 - i] & 0xF0) >> 4]);
            sb.append(HEXCHARS[guid[7 - i] & 0xF]);
        }
        sb.append('-');
        for (i = 0; i < 2; ++i) {
            sb.append(HEXCHARS[(guid[8 + i] & 0xF0) >> 4]);
            sb.append(HEXCHARS[guid[8 + i] & 0xF]);
        }
        sb.append('-');
        for (i = 0; i < 6; ++i) {
            sb.append(HEXCHARS[(guid[10 + i] & 0xF0) >> 4]);
            sb.append(HEXCHARS[guid[10 + i] & 0xF]);
        }
        return sb.toString();
    }

    static boolean isActivityTraceOn() {
        LogManager lm = LogManager.getLogManager();
        String activityTrace = lm.getProperty(ACTIVITY_ID_TRACE_PROPERTY);
        return "on".equalsIgnoreCase(activityTrace);
    }

    static boolean shouldHonorAEForRead(SQLServerStatementColumnEncryptionSetting stmtColumnEncryptionSetting, SQLServerConnection connection) {
        switch (stmtColumnEncryptionSetting) {
            case DISABLED: {
                return false;
            }
            case ENABLED: 
            case RESULTSET_ONLY: {
                return true;
            }
        }
        assert (SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING == stmtColumnEncryptionSetting) : "Unexpected value for command level override";
        return connection != null && connection.isColumnEncryptionSettingEnabled();
    }

    static boolean shouldHonorAEForParameters(SQLServerStatementColumnEncryptionSetting stmtColumnEncryptionSetting, SQLServerConnection connection) {
        switch (stmtColumnEncryptionSetting) {
            case DISABLED: 
            case RESULTSET_ONLY: {
                return false;
            }
            case ENABLED: {
                return true;
            }
        }
        assert (SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING == stmtColumnEncryptionSetting) : "Unexpected value for command level override";
        return connection != null && connection.isColumnEncryptionSettingEnabled();
    }

    static void validateMoneyRange(BigDecimal bd, JDBCType jdbcType) throws SQLServerException {
        if (null == bd) {
            return;
        }
        switch (jdbcType) {
            case MONEY: {
                if (bd.compareTo(SSType.MAX_VALUE_MONEY) > 0 || bd.compareTo(SSType.MIN_VALUE_MONEY) < 0) break;
                return;
            }
            case SMALLMONEY: {
                if (bd.compareTo(SSType.MAX_VALUE_SMALLMONEY) > 0 || bd.compareTo(SSType.MIN_VALUE_SMALLMONEY) < 0) break;
                return;
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
        Object[] msgArgs = new Object[]{jdbcType};
        throw new SQLServerException(form.format(msgArgs), null);
    }

    static int getValueLengthBaseOnJavaType(Object value, JavaType javaType, Integer precision, Integer scale, JDBCType jdbcType) throws SQLServerException {
        block0 : switch (javaType) {
            case OBJECT: {
                switch (jdbcType) {
                    case DECIMAL: 
                    case NUMERIC: {
                        javaType = JavaType.BIGDECIMAL;
                        break block0;
                    }
                    case TIME: {
                        javaType = JavaType.TIME;
                        break block0;
                    }
                    case TIMESTAMP: {
                        javaType = JavaType.TIMESTAMP;
                        break block0;
                    }
                    case DATETIMEOFFSET: {
                        javaType = JavaType.DATETIMEOFFSET;
                        break block0;
                    }
                }
                break;
            }
        }
        switch (javaType) {
            case STRING: {
                if (JDBCType.GUID == jdbcType) {
                    String guidTemplate = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX";
                    return null == value ? 0 : guidTemplate.length();
                }
                if (JDBCType.TIMESTAMP == jdbcType || JDBCType.TIME == jdbcType || JDBCType.DATETIMEOFFSET == jdbcType) {
                    return null == scale ? 7 : scale;
                }
                if (JDBCType.BINARY == jdbcType || JDBCType.VARBINARY == jdbcType) {
                    return null == value ? 0 : ParameterUtils.hexToBin((String)value).length;
                }
                if (JDBCType.GEOMETRY == jdbcType) {
                    return null == value ? 0 : ((Geometry)value).serialize().length;
                }
                if (JDBCType.GEOGRAPHY == jdbcType) {
                    return null == value ? 0 : ((Geography)value).serialize().length;
                }
                return null == value ? 0 : ((String)value).length();
            }
            case BYTEARRAY: {
                return null == value ? 0 : ((byte[])value).length;
            }
            case BIGDECIMAL: {
                int length;
                if (null == precision) {
                    if (null == value) {
                        length = 0;
                    } else if (0 == ((BigDecimal)value).intValue()) {
                        Object s = WSID_NOT_AVAILABLE + value;
                        s = ((String)(s = ((String)s).replaceAll("\\-", WSID_NOT_AVAILABLE))).startsWith("0.") ? ((String)s).replaceAll("0\\.", WSID_NOT_AVAILABLE) : ((String)s).replaceAll("\\.", WSID_NOT_AVAILABLE);
                        length = ((String)s).length();
                    } else if ((WSID_NOT_AVAILABLE + value).contains("E")) {
                        DecimalFormat dform = new DecimalFormat("###.#####");
                        String s = dform.format(value);
                        s = s.replaceAll("\\.", WSID_NOT_AVAILABLE);
                        s = s.replaceAll("\\-", WSID_NOT_AVAILABLE);
                        length = s.length();
                    } else {
                        length = ((BigDecimal)value).precision();
                    }
                } else {
                    length = precision;
                }
                return length;
            }
            case TIMESTAMP: 
            case TIME: 
            case DATETIMEOFFSET: {
                return null == scale ? 7 : scale;
            }
            case CLOB: {
                return null == value ? 0 : 0x7FFFFFFE;
            }
            case NCLOB: 
            case READER: {
                return null == value ? 0 : 0x3FFFFFFF;
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static boolean checkIfNeedNewAccessToken(SQLServerConnection connection, Date accessTokenExpireDate) {
        LOCK.lock();
        try {
            Date now = new Date();
            if (accessTokenExpireDate.getTime() - now.getTime() < 2700000L) {
                if (accessTokenExpireDate.getTime() - now.getTime() < 600000L) {
                    boolean bl = true;
                    return bl;
                }
                if (connection.attemptRefreshTokenLocked) {
                    boolean bl = false;
                    return bl;
                }
                connection.attemptRefreshTokenLocked = true;
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            LOCK.unlock();
        }
    }

    static <T> T newInstance(Class<?> returnType, String className, String constructorArg, Object[] msgArgs) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        if (!returnType.isAssignableFrom(clazz)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unassignableError"));
            throw new IllegalArgumentException(form.format(msgArgs));
        }
        if (constructorArg == null) {
            return (T)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        return (T)clazz.getDeclaredConstructor(String.class).newInstance(constructorArg);
    }

    static String escapeSingleQuotes(String name) {
        return name.replace("'", "''");
    }

    static String convertInputStreamToString(InputStream is) throws IOException {
        int length;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString();
    }

    static String zeroOneToYesNo(int i) {
        return 0 == i ? "NO" : "YES";
    }

    static byte[] charsToBytes(char[] chars) {
        if (chars == null) {
            return null;
        }
        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; ++i) {
            bytes[i * 2] = (byte)(0xFF & chars[i] >> 8);
            bytes[i * 2 + 1] = (byte)(0xFF & chars[i]);
        }
        return bytes;
    }

    static char[] bytesToChars(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] chars = new char[bytes.length / 2];
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char)((0xFF & bytes[i * 2]) << 8 | 0xFF & bytes[i * 2 + 1]);
        }
        return chars;
    }

    static Throwable getRootCause(Throwable throwable) {
        ArrayList<Throwable> list = new ArrayList<Throwable>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list.isEmpty() ? null : (Throwable)list.get(list.size() - 1);
    }
}

