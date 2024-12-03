/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.BlobImpl;
import net.sourceforge.jtds.jdbc.CharsetInfo;
import net.sourceforge.jtds.jdbc.ClobImpl;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.DateTime;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.ProtocolException;
import net.sourceforge.jtds.jdbc.RequestStream;
import net.sourceforge.jtds.jdbc.ResponseStream;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.UniqueIdentifier;
import net.sourceforge.jtds.util.BlobBuffer;

public class TdsData {
    private static final int SYBCHAR = 47;
    private static final int SYBVARCHAR = 39;
    private static final int SYBINTN = 38;
    private static final int SYBINT1 = 48;
    private static final int SYBDATE = 49;
    private static final int SYBTIME = 51;
    private static final int SYBINT2 = 52;
    private static final int SYBINT4 = 56;
    private static final int SYBINT8 = 127;
    private static final int SYBFLT8 = 62;
    private static final int SYBDATETIME = 61;
    private static final int SYBBIT = 50;
    private static final int SYBTEXT = 35;
    private static final int SYBNTEXT = 99;
    private static final int SYBIMAGE = 34;
    private static final int SYBMONEY4 = 122;
    private static final int SYBMONEY = 60;
    private static final int SYBDATETIME4 = 58;
    private static final int SYBREAL = 59;
    private static final int SYBBINARY = 45;
    private static final int SYBVOID = 31;
    private static final int SYBVARBINARY = 37;
    private static final int SYBNVARCHAR = 103;
    private static final int SYBBITN = 104;
    private static final int SYBNUMERIC = 108;
    private static final int SYBDECIMAL = 106;
    private static final int SYBFLTN = 109;
    private static final int SYBMONEYN = 110;
    private static final int SYBDATETIMN = 111;
    private static final int SYBDATEN = 123;
    private static final int SYBTIMEN = 147;
    private static final int XSYBCHAR = 175;
    private static final int XSYBVARCHAR = 167;
    private static final int XSYBNVARCHAR = 231;
    private static final int XSYBNCHAR = 239;
    private static final int XSYBVARBINARY = 165;
    private static final int XSYBBINARY = 173;
    private static final int SYBUNITEXT = 174;
    private static final int SYBLONGBINARY = 225;
    private static final int SYBSINT1 = 64;
    private static final int SYBUINT2 = 65;
    private static final int SYBUINT4 = 66;
    private static final int SYBUINT8 = 67;
    private static final int SYBUINTN = 68;
    private static final int SYBUNIQUE = 36;
    private static final int SYBVARIANT = 98;
    private static final int SYBSINT8 = 191;
    private static final int XML = 241;
    private static final int DATEN = 40;
    private static final int TIMEN = 41;
    private static final int DATETIME2N = 42;
    private static final int DATETIMEOFFSETN = 43;
    static final int SYBLONGDATA = 36;
    private static final int UDT_CHAR = 1;
    private static final int UDT_VARCHAR = 2;
    private static final int UDT_BINARY = 3;
    private static final int UDT_VARBINARY = 4;
    private static final int UDT_SYSNAME = 18;
    private static final int UDT_NCHAR = 24;
    private static final int UDT_NVARCHAR = 25;
    private static final int UDT_UNICHAR = 34;
    private static final int UDT_UNIVARCHAR = 35;
    private static final int UDT_UNITEXT = 36;
    private static final int UDT_LONGSYSNAME = 42;
    private static final int UDT_TIMESTAMP = 80;
    private static final int UDT_NEWSYSNAME = 256;
    private static final int VAR_MAX = 255;
    private static final int SYB_LONGVAR_MAX = 16384;
    private static final int MS_LONGVAR_MAX = 8000;
    private static final int SYB_CHUNK_SIZE = 8192;
    private static final TypeInfo[] types = new TypeInfo[256];
    static final int DEFAULT_SCALE = 10;
    static final int DEFAULT_PRECISION_28 = 28;
    static final int DEFAULT_PRECISION_38 = 38;

    static int getCollation(ResponseStream in, ColInfo ci) throws IOException {
        if (TdsData.isCollation(ci)) {
            ci.collation = new byte[5];
            in.read(ci.collation);
            return 5;
        }
        return 0;
    }

    static void setColumnCharset(ColInfo ci, JtdsConnection connection) throws SQLException {
        if (connection.isCharsetSpecified()) {
            ci.charsetInfo = connection.getCharsetInfo();
        } else if (ci.collation != null) {
            int i;
            byte[] collation = ci.collation;
            byte[] defaultCollation = connection.getCollation();
            for (i = 0; i < 5 && collation[i] == defaultCollation[i]; ++i) {
            }
            ci.charsetInfo = i == 5 ? connection.getCharsetInfo() : CharsetInfo.getCharset(collation);
        }
    }

    static int readType(ResponseStream in, ColInfo ci) throws IOException, ProtocolException {
        int tdsVersion = in.getTdsVersion();
        boolean isTds8 = tdsVersion >= 4;
        boolean isTds7 = tdsVersion >= 3;
        boolean isTds5 = tdsVersion == 2;
        boolean isTds42 = tdsVersion == 1;
        int bytesRead = 1;
        int type = in.read();
        if (types[type] == null || isTds5 && type == 36) {
            throw new ProtocolException("Invalid TDS data type 0x" + Integer.toHexString(type & 0xFF));
        }
        ci.tdsType = type;
        ci.jdbcType = TdsData.types[type].jdbcType;
        ci.bufferSize = TdsData.types[type].size;
        if (ci.bufferSize == -5) {
            ci.bufferSize = in.readInt();
            bytesRead += 4;
        } else if (ci.bufferSize == -4) {
            ci.bufferSize = in.readInt();
            if (isTds8) {
                bytesRead += TdsData.getCollation(in, ci);
            }
            int lenName = in.readShort();
            ci.tableName = in.readString(lenName);
            bytesRead += 6 + (in.getTdsVersion() >= 3 ? lenName * 2 : lenName);
        } else if (ci.bufferSize == -2) {
            if (isTds5 && ci.tdsType == 175) {
                ci.bufferSize = in.readInt();
                bytesRead += 4;
            } else {
                ci.bufferSize = in.readShort();
                bytesRead += 2;
            }
            if (isTds8) {
                bytesRead += TdsData.getCollation(in, ci);
            }
        } else if (ci.bufferSize == -1) {
            ++bytesRead;
            ci.bufferSize = in.read();
        }
        ci.displaySize = TdsData.types[type].displaySize;
        ci.precision = TdsData.types[type].precision;
        ci.sqlType = TdsData.types[type].sqlType;
        switch (type) {
            case 61: {
                ci.scale = 3;
                break;
            }
            case 111: {
                if (ci.bufferSize == 8) {
                    ci.displaySize = TdsData.types[61].displaySize;
                    ci.precision = TdsData.types[61].precision;
                    ci.scale = 3;
                    break;
                }
                ci.displaySize = TdsData.types[58].displaySize;
                ci.precision = TdsData.types[58].precision;
                ci.sqlType = TdsData.types[58].sqlType;
                ci.scale = 0;
                break;
            }
            case 109: {
                if (ci.bufferSize == 8) {
                    ci.displaySize = TdsData.types[62].displaySize;
                    ci.precision = TdsData.types[62].precision;
                    break;
                }
                ci.displaySize = TdsData.types[59].displaySize;
                ci.precision = TdsData.types[59].precision;
                ci.jdbcType = 7;
                ci.sqlType = TdsData.types[59].sqlType;
                break;
            }
            case 38: {
                if (ci.bufferSize == 8) {
                    ci.displaySize = TdsData.types[127].displaySize;
                    ci.precision = TdsData.types[127].precision;
                    ci.jdbcType = -5;
                    ci.sqlType = TdsData.types[127].sqlType;
                    break;
                }
                if (ci.bufferSize == 4) {
                    ci.displaySize = TdsData.types[56].displaySize;
                    ci.precision = TdsData.types[56].precision;
                    break;
                }
                if (ci.bufferSize == 2) {
                    ci.displaySize = TdsData.types[52].displaySize;
                    ci.precision = TdsData.types[52].precision;
                    ci.jdbcType = 5;
                    ci.sqlType = TdsData.types[52].sqlType;
                    break;
                }
                ci.displaySize = TdsData.types[48].displaySize;
                ci.precision = TdsData.types[48].precision;
                ci.jdbcType = -6;
                ci.sqlType = TdsData.types[48].sqlType;
                break;
            }
            case 68: {
                if (ci.bufferSize == 8) {
                    ci.displaySize = TdsData.types[67].displaySize;
                    ci.precision = TdsData.types[67].precision;
                    ci.jdbcType = TdsData.types[67].jdbcType;
                    ci.sqlType = TdsData.types[67].sqlType;
                    break;
                }
                if (ci.bufferSize == 4) {
                    ci.displaySize = TdsData.types[66].displaySize;
                    ci.precision = TdsData.types[66].precision;
                    break;
                }
                if (ci.bufferSize == 2) {
                    ci.displaySize = TdsData.types[65].displaySize;
                    ci.precision = TdsData.types[65].precision;
                    ci.jdbcType = TdsData.types[65].jdbcType;
                    ci.sqlType = TdsData.types[65].sqlType;
                    break;
                }
                throw new ProtocolException("unsigned int null (size 1) not supported");
            }
            case 60: 
            case 122: {
                ci.scale = 4;
                break;
            }
            case 110: {
                if (ci.bufferSize == 8) {
                    ci.displaySize = TdsData.types[60].displaySize;
                    ci.precision = TdsData.types[60].precision;
                } else {
                    ci.displaySize = TdsData.types[122].displaySize;
                    ci.precision = TdsData.types[122].precision;
                    ci.sqlType = TdsData.types[122].sqlType;
                }
                ci.scale = 4;
                break;
            }
            case 106: 
            case 108: {
                ci.precision = in.read();
                ci.scale = in.read();
                ci.displaySize = (ci.scale > 0 ? 2 : 1) + ci.precision;
                bytesRead += 2;
                ci.sqlType = TdsData.types[type].sqlType;
                break;
            }
            case 34: {
                ci.precision = Integer.MAX_VALUE;
                ci.displaySize = Integer.MAX_VALUE;
                break;
            }
            case 37: 
            case 45: 
            case 165: 
            case 173: 
            case 225: {
                ci.precision = ci.bufferSize;
                ci.displaySize = ci.precision * 2;
                break;
            }
            case 99: {
                ci.precision = 0x3FFFFFFF;
                ci.displaySize = 0x3FFFFFFF;
                break;
            }
            case 174: {
                ci.precision = 0x3FFFFFFF;
                ci.displaySize = 0x3FFFFFFF;
                break;
            }
            case 231: 
            case 239: {
                ci.precision = ci.displaySize = ci.bufferSize / 2;
                break;
            }
            case 35: 
            case 39: 
            case 47: 
            case 103: 
            case 167: 
            case 175: {
                ci.displaySize = ci.precision = ci.bufferSize;
            }
        }
        if (ci.isIdentity) {
            ci.sqlType = ci.sqlType + " identity";
        }
        if (isTds42 || isTds5) {
            switch (ci.userType) {
                case 1: {
                    ci.sqlType = "char";
                    ci.displaySize = ci.bufferSize;
                    ci.jdbcType = 1;
                    break;
                }
                case 2: {
                    ci.sqlType = "varchar";
                    ci.displaySize = ci.bufferSize;
                    ci.jdbcType = 12;
                    break;
                }
                case 3: {
                    ci.sqlType = "binary";
                    ci.displaySize = ci.bufferSize * 2;
                    ci.jdbcType = -2;
                    break;
                }
                case 4: {
                    ci.sqlType = "varbinary";
                    ci.displaySize = ci.bufferSize * 2;
                    ci.jdbcType = -3;
                    break;
                }
                case 18: {
                    ci.sqlType = "sysname";
                    ci.displaySize = ci.bufferSize;
                    ci.jdbcType = 12;
                    break;
                }
                case 80: {
                    ci.sqlType = "timestamp";
                    ci.displaySize = ci.bufferSize * 2;
                    ci.jdbcType = -3;
                }
            }
        }
        if (isTds5) {
            switch (ci.userType) {
                case 24: {
                    ci.sqlType = "nchar";
                    ci.displaySize = ci.bufferSize;
                    ci.jdbcType = 1;
                    break;
                }
                case 25: {
                    ci.sqlType = "nvarchar";
                    ci.displaySize = ci.bufferSize;
                    ci.jdbcType = 12;
                    break;
                }
                case 34: {
                    ci.sqlType = "unichar";
                    ci.precision = ci.displaySize = ci.bufferSize / 2;
                    ci.jdbcType = 1;
                    break;
                }
                case 35: {
                    ci.sqlType = "univarchar";
                    ci.precision = ci.displaySize = ci.bufferSize / 2;
                    ci.jdbcType = 12;
                    break;
                }
                case 42: {
                    ci.sqlType = "longsysname";
                    ci.jdbcType = 12;
                    ci.displaySize = ci.bufferSize;
                }
            }
        }
        if (isTds7) {
            switch (ci.userType) {
                case 80: {
                    ci.sqlType = "timestamp";
                    ci.jdbcType = -2;
                    break;
                }
                case 256: {
                    ci.sqlType = "sysname";
                    ci.jdbcType = 12;
                }
            }
        }
        return bytesRead;
    }

    static Object readData(JtdsConnection connection, ResponseStream in, ColInfo ci) throws IOException, ProtocolException {
        switch (ci.tdsType) {
            case 38: {
                switch (in.read()) {
                    case 1: {
                        return new Integer(in.read() & 0xFF);
                    }
                    case 2: {
                        return new Integer(in.readShort());
                    }
                    case 4: {
                        return new Integer(in.readInt());
                    }
                    case 8: {
                        return new Long(in.readLong());
                    }
                }
                break;
            }
            case 68: {
                switch (in.read()) {
                    case 1: {
                        return new Integer(in.read() & 0xFF);
                    }
                    case 2: {
                        return new Integer(in.readShort() & 0xFFFF);
                    }
                    case 4: {
                        return new Long((long)in.readInt() & 0xFFFFFFFFL);
                    }
                    case 8: {
                        return in.readUnsignedLong();
                    }
                }
                break;
            }
            case 48: {
                return new Integer(in.read() & 0xFF);
            }
            case 52: {
                return new Integer(in.readShort());
            }
            case 56: {
                return new Integer(in.readInt());
            }
            case 127: {
                return new Long(in.readLong());
            }
            case 191: {
                return new Long(in.readLong());
            }
            case 65: {
                return new Integer(in.readShort() & 0xFFFF);
            }
            case 66: {
                return new Long((long)in.readInt() & 0xFFFFFFFFL);
            }
            case 67: {
                return in.readUnsignedLong();
            }
            case 34: {
                BlobImpl blob;
                int dataLen;
                int len = in.read();
                if (len <= 0) break;
                in.skip(24);
                if (dataLen == 0 && in.getTdsVersion() <= 2) break;
                if ((long)dataLen <= connection.getLobBuffer()) {
                    byte[] data = new byte[dataLen];
                    in.read(data);
                    blob = new BlobImpl(connection, data);
                } else {
                    try {
                        int result;
                        blob = new BlobImpl(connection);
                        OutputStream out = blob.setBinaryStream(1L);
                        byte[] buffer = new byte[1024];
                        for (dataLen = in.readInt(); (result = in.read(buffer, 0, Math.min(dataLen, buffer.length))) != -1 && dataLen != 0; dataLen -= result) {
                            out.write(buffer, 0, result);
                        }
                        out.close();
                    }
                    catch (SQLException e) {
                        throw new IOException(e.getMessage());
                    }
                }
                return blob;
            }
            case 35: {
                int len = in.read();
                if (len <= 0) break;
                String charset = ci.charsetInfo != null ? ci.charsetInfo.getCharset() : connection.getCharset();
                in.skip(24);
                int dataLen = in.readInt();
                if (dataLen == 0 && in.getTdsVersion() <= 2) break;
                ClobImpl clob = new ClobImpl(connection);
                BlobBuffer blobBuffer = clob.getBlobBuffer();
                if ((long)dataLen <= connection.getLobBuffer()) {
                    int c;
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(in.getInputStream(dataLen), charset), 1024);
                    byte[] data = new byte[dataLen * 2];
                    int p = 0;
                    while ((c = rdr.read()) >= 0) {
                        data[p++] = (byte)c;
                        data[p++] = (byte)(c >> 8);
                    }
                    rdr.close();
                    blobBuffer.setBuffer(data, false);
                    if (p == 2 && data[0] == 32 && data[1] == 0 && in.getTdsVersion() < 3) {
                        p = 0;
                    }
                    blobBuffer.setLength(p);
                } else {
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(in.getInputStream(dataLen), charset), 1024);
                    try {
                        int c;
                        OutputStream out = blobBuffer.setBinaryStream(1L, false);
                        while ((c = rdr.read()) >= 0) {
                            out.write(c);
                            out.write(c >> 8);
                        }
                        out.close();
                        rdr.close();
                    }
                    catch (SQLException e) {
                        throw new IOException(e.getMessage());
                    }
                }
                return clob;
            }
            case 99: 
            case 174: {
                int dataLen;
                int len = in.read();
                if (len <= 0) break;
                in.skip(24);
                if (dataLen == 0 && in.getTdsVersion() <= 2) break;
                ClobImpl clob = new ClobImpl(connection);
                BlobBuffer blobBuffer = clob.getBlobBuffer();
                if ((long)dataLen <= connection.getLobBuffer()) {
                    byte[] data = new byte[dataLen];
                    in.read(data);
                    blobBuffer.setBuffer(data, false);
                    if (dataLen == 2 && data[0] == 32 && data[1] == 0 && in.getTdsVersion() == 2) {
                        dataLen = 0;
                    }
                    blobBuffer.setLength(dataLen);
                } else {
                    try {
                        int result;
                        OutputStream out = blobBuffer.setBinaryStream(1L, false);
                        byte[] buffer = new byte[1024];
                        for (dataLen = in.readInt(); (result = in.read(buffer, 0, Math.min(dataLen, buffer.length))) != -1 && dataLen != 0; dataLen -= result) {
                            out.write(buffer, 0, result);
                        }
                        out.close();
                    }
                    catch (SQLException e) {
                        throw new IOException(e.getMessage());
                    }
                }
                return clob;
            }
            case 39: 
            case 47: {
                int len = in.read();
                if (len <= 0) break;
                String value = in.readNonUnicodeString(len, ci.charsetInfo == null ? connection.getCharsetInfo() : ci.charsetInfo);
                if (len == 1 && ci.tdsType == 39 && in.getTdsVersion() < 3) {
                    return " ".equals(value) ? "" : value;
                }
                return value;
            }
            case 103: {
                int len = in.read();
                if (len <= 0) break;
                return in.readUnicodeString(len / 2);
            }
            case 167: 
            case 175: {
                if (in.getTdsVersion() == 2) {
                    int len = in.readInt();
                    if (len <= 0) break;
                    String tmp = in.readNonUnicodeString(len);
                    if (" ".equals(tmp) && !"char".equals(ci.sqlType)) {
                        tmp = "";
                    }
                    return tmp;
                }
                short len = in.readShort();
                if (len == -1) break;
                return in.readNonUnicodeString(len, ci.charsetInfo == null ? connection.getCharsetInfo() : ci.charsetInfo);
            }
            case 231: 
            case 239: {
                short len = in.readShort();
                if (len == -1) break;
                return in.readUnicodeString(len / 2);
            }
            case 37: 
            case 45: {
                int len = in.read();
                if (len <= 0) break;
                byte[] bytes = new byte[len];
                in.read(bytes);
                return bytes;
            }
            case 165: 
            case 173: {
                short len = in.readShort();
                if (len == -1) break;
                byte[] bytes = new byte[len];
                in.read(bytes);
                return bytes;
            }
            case 225: {
                int len = in.readInt();
                if (len == 0) break;
                if ("unichar".equals(ci.sqlType) || "univarchar".equals(ci.sqlType)) {
                    char[] buf = new char[len / 2];
                    in.read(buf);
                    if ((len & 1) != 0) {
                        in.skip(1);
                    }
                    if (len == 2 && buf[0] == ' ') {
                        return "";
                    }
                    return new String(buf);
                }
                byte[] bytes = new byte[len];
                in.read(bytes);
                return bytes;
            }
            case 60: 
            case 110: 
            case 122: {
                return TdsData.getMoneyValue(in, ci.tdsType);
            }
            case 58: 
            case 61: 
            case 111: {
                return TdsData.getDatetimeValue(in, ci.tdsType);
            }
            case 49: 
            case 123: {
                int len;
                int n = len = ci.tdsType == 123 ? in.read() : 4;
                if (len == 4) {
                    return new DateTime(in.readInt(), Integer.MIN_VALUE);
                }
                in.skip(len);
                break;
            }
            case 51: 
            case 147: {
                int len;
                int n = len = ci.tdsType == 147 ? in.read() : 4;
                if (len == 4) {
                    return new DateTime(Integer.MIN_VALUE, in.readInt());
                }
                in.skip(len);
                break;
            }
            case 50: {
                return in.read() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            case 104: {
                int len = in.read();
                if (len <= 0) break;
                return in.read() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            case 59: {
                return new Float(Float.intBitsToFloat(in.readInt()));
            }
            case 62: {
                return new Double(Double.longBitsToDouble(in.readLong()));
            }
            case 109: {
                int len = in.read();
                if (len == 4) {
                    return new Float(Float.intBitsToFloat(in.readInt()));
                }
                if (len != 8) break;
                return new Double(Double.longBitsToDouble(in.readLong()));
            }
            case 36: {
                int len = in.read();
                if (len <= 0) break;
                byte[] bytes = new byte[len];
                in.read(bytes);
                return new UniqueIdentifier(bytes);
            }
            case 106: 
            case 108: {
                BigInteger bi;
                int len = in.read();
                if (len <= 0) break;
                int sign = in.read();
                byte[] bytes = new byte[--len];
                if (in.getServerType() == 2) {
                    for (int i = 0; i < len; ++i) {
                        bytes[i] = (byte)in.read();
                    }
                    bi = new BigInteger(sign == 0 ? 1 : -1, bytes);
                } else {
                    while (len-- > 0) {
                        bytes[len] = (byte)in.read();
                    }
                    bi = new BigInteger(sign == 0 ? -1 : 1, bytes);
                }
                return new BigDecimal(bi, ci.scale);
            }
            case 98: {
                return TdsData.getVariant(connection, in);
            }
            default: {
                throw new ProtocolException("Unsupported TDS data type 0x" + Integer.toHexString(ci.tdsType & 0xFF));
            }
        }
        return null;
    }

    static boolean isSigned(ColInfo ci) {
        int type = ci.tdsType;
        if (type < 0 || type > 255 || types[type] == null) {
            throw new IllegalArgumentException("TDS data type " + type + " invalid");
        }
        if (type == 38 && ci.bufferSize == 1) {
            type = 48;
        }
        return TdsData.types[type].isSigned;
    }

    static boolean isCollation(ColInfo ci) {
        int type = ci.tdsType;
        if (type < 0 || type > 255 || types[type] == null) {
            throw new IllegalArgumentException("TDS data type " + type + " invalid");
        }
        return TdsData.types[type].isCollation;
    }

    static boolean isCurrency(ColInfo ci) {
        int type = ci.tdsType;
        if (type < 0 || type > 255 || types[type] == null) {
            throw new IllegalArgumentException("TDS data type " + type + " invalid");
        }
        return type == 60 || type == 122 || type == 110;
    }

    static boolean isSearchable(ColInfo ci) {
        int type = ci.tdsType;
        if (type < 0 || type > 255 || types[type] == null) {
            throw new IllegalArgumentException("TDS data type " + type + " invalid");
        }
        return TdsData.types[type].size != -4;
    }

    static boolean isUnicode(ColInfo ci) {
        int type = ci.tdsType;
        if (type < 0 || type > 255 || types[type] == null) {
            throw new IllegalArgumentException("TDS data type " + type + " invalid");
        }
        switch (type) {
            case 98: 
            case 99: 
            case 103: 
            case 175: 
            case 231: 
            case 239: {
                return true;
            }
        }
        return false;
    }

    static void fillInType(ColInfo ci) throws SQLException {
        switch (ci.jdbcType) {
            case 12: {
                ci.tdsType = 39;
                ci.bufferSize = 8000;
                ci.displaySize = 8000;
                ci.precision = 8000;
                break;
            }
            case 4: {
                ci.tdsType = 56;
                ci.bufferSize = 4;
                ci.displaySize = 11;
                ci.precision = 10;
                break;
            }
            case 5: {
                ci.tdsType = 52;
                ci.bufferSize = 2;
                ci.displaySize = 6;
                ci.precision = 5;
                break;
            }
            case -7: {
                ci.tdsType = 50;
                ci.bufferSize = 1;
                ci.displaySize = 1;
                ci.precision = 1;
                break;
            }
            default: {
                throw new SQLException(Messages.get("error.baddatatype", Integer.toString(ci.jdbcType)), "HY000");
            }
        }
        ci.sqlType = TdsData.types[ci.tdsType].sqlType;
        ci.scale = 0;
    }

    static void getNativeType(JtdsConnection connection, ParamInfo pi) throws SQLException {
        int jdbcType = pi.jdbcType;
        if (jdbcType == 1111) {
            jdbcType = Support.getJdbcType(pi.value);
        }
        switch (jdbcType) {
            case -1: 
            case 1: 
            case 12: 
            case 2005: {
                int len = pi.value == null ? 0 : pi.length;
                if (connection.getTdsVersion() < 3) {
                    Object tmp;
                    String charset = connection.getCharset();
                    if (len > 0 && (len <= 8192 || connection.getSybaseInfo(32)) && connection.getSybaseInfo(16) && connection.getUseUnicode() && !"UTF-8".equals(charset)) {
                        try {
                            tmp = pi.getString(charset);
                            if (!TdsData.canEncode((String)tmp, charset)) {
                                pi.length = ((String)tmp).length();
                                if (pi.length > 8192) {
                                    pi.sqlType = "unitext";
                                    pi.tdsType = 36;
                                    break;
                                }
                                pi.sqlType = "univarchar(" + pi.length + ')';
                                pi.tdsType = 225;
                                break;
                            }
                        }
                        catch (IOException e) {
                            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                        }
                    }
                    if (connection.isWideChar() && len <= 16384) {
                        try {
                            tmp = pi.getBytes(charset);
                            len = tmp == null ? 0 : ((byte[])tmp).length;
                        }
                        catch (IOException e) {
                            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                        }
                    }
                    if (len <= 255) {
                        pi.tdsType = 39;
                        pi.sqlType = "varchar(255)";
                        break;
                    }
                    if (connection.getSybaseInfo(1)) {
                        if (len > 16384) {
                            pi.tdsType = 36;
                            pi.sqlType = "text";
                            break;
                        }
                        pi.tdsType = 175;
                        pi.sqlType = "varchar(" + len + ')';
                        break;
                    }
                    pi.tdsType = 35;
                    pi.sqlType = "text";
                    break;
                }
                if (pi.isUnicode && len <= 4000) {
                    pi.tdsType = 231;
                    pi.sqlType = "nvarchar(4000)";
                    break;
                }
                if (!pi.isUnicode && len <= 8000) {
                    CharsetInfo csi = connection.getCharsetInfo();
                    try {
                        if (len > 0 && csi.isWideChars() && pi.getBytes(csi.getCharset()).length > 8000) {
                            pi.tdsType = 35;
                            pi.sqlType = "text";
                            break;
                        }
                        pi.tdsType = 167;
                        pi.sqlType = "varchar(8000)";
                        break;
                    }
                    catch (IOException e) {
                        throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                    }
                }
                if (pi.isOutput) {
                    throw new SQLException(Messages.get("error.textoutparam"), "HY000");
                }
                if (pi.isUnicode) {
                    pi.tdsType = 99;
                    pi.sqlType = "ntext";
                    break;
                }
                pi.tdsType = 35;
                pi.sqlType = "text";
                break;
            }
            case -6: 
            case 4: 
            case 5: {
                pi.tdsType = 38;
                pi.sqlType = "int";
                break;
            }
            case -7: 
            case 16: {
                pi.tdsType = connection.getTdsVersion() >= 3 || connection.getSybaseInfo(4) ? 104 : 50;
                pi.sqlType = "bit";
                break;
            }
            case 7: {
                pi.tdsType = 109;
                pi.sqlType = "real";
                break;
            }
            case 6: 
            case 8: {
                pi.tdsType = 109;
                pi.sqlType = "float";
                break;
            }
            case 91: {
                if (connection.getSybaseInfo(2)) {
                    pi.tdsType = 123;
                    pi.sqlType = "date";
                    break;
                }
                pi.tdsType = 111;
                pi.sqlType = "datetime";
                break;
            }
            case 92: {
                if (connection.getSybaseInfo(2)) {
                    pi.tdsType = 147;
                    pi.sqlType = "time";
                    break;
                }
                pi.tdsType = 111;
                pi.sqlType = "datetime";
                break;
            }
            case 93: {
                pi.tdsType = 111;
                pi.sqlType = "datetime";
                break;
            }
            case -4: 
            case -3: 
            case -2: 
            case 2004: {
                int len = pi.value == null ? 0 : pi.length;
                if (connection.getTdsVersion() < 3) {
                    if (len <= 255) {
                        pi.tdsType = 37;
                        pi.sqlType = "varbinary(255)";
                        break;
                    }
                    if (connection.getSybaseInfo(1)) {
                        if (len > 16384) {
                            pi.tdsType = 36;
                            pi.sqlType = "image";
                            break;
                        }
                        pi.tdsType = 225;
                        pi.sqlType = "varbinary(" + len + ")";
                        break;
                    }
                    pi.tdsType = 34;
                    pi.sqlType = "image";
                    break;
                }
                if (len <= 8000) {
                    pi.tdsType = 165;
                    pi.sqlType = "varbinary(8000)";
                    break;
                }
                if (pi.isOutput) {
                    throw new SQLException(Messages.get("error.textoutparam"), "HY000");
                }
                pi.tdsType = 34;
                pi.sqlType = TdsData.isMSSQL2005Plus(connection) ? "varbinary(max)" : "image";
                break;
            }
            case -5: {
                if (connection.getTdsVersion() >= 4 || connection.getSybaseInfo(64)) {
                    pi.tdsType = 38;
                    pi.sqlType = "bigint";
                    break;
                }
                pi.tdsType = 106;
                pi.sqlType = "decimal(" + connection.getMaxPrecision() + ')';
                pi.scale = 0;
                break;
            }
            case 2: 
            case 3: {
                pi.tdsType = 106;
                int prec = connection.getMaxPrecision();
                int scale = 10;
                if (pi.value instanceof BigDecimal) {
                    scale = ((BigDecimal)pi.value).scale();
                } else if (pi.scale >= 0 && pi.scale <= prec) {
                    scale = pi.scale;
                }
                pi.sqlType = "decimal(" + prec + ',' + scale + ')';
                break;
            }
            case 0: 
            case 1111: {
                pi.tdsType = 39;
                pi.sqlType = "varchar(255)";
                break;
            }
            case 2009: {
                int len;
                int n = len = pi.value == null ? 0 : pi.length;
                if (connection.getTdsVersion() >= 4) {
                    pi.tdsType = 241;
                    pi.sqlType = "xml";
                    break;
                }
                if (connection.getTdsVersion() < 3) {
                    if (len <= 255) {
                        pi.tdsType = 37;
                        pi.sqlType = "varbinary(255)";
                        break;
                    }
                    if (connection.getSybaseInfo(1)) {
                        if (len > 16384) {
                            pi.tdsType = 36;
                            pi.sqlType = "image";
                            break;
                        }
                        pi.tdsType = 225;
                        pi.sqlType = "varbinary(" + len + ')';
                        break;
                    }
                    pi.tdsType = 34;
                    pi.sqlType = "image";
                    break;
                }
                if (len <= 8000) {
                    pi.tdsType = 165;
                    pi.sqlType = "varbinary(8000)";
                    break;
                }
                if (pi.isOutput) {
                    throw new SQLException(Messages.get("error.textoutparam"), "HY000");
                }
                pi.tdsType = 34;
                pi.sqlType = TdsData.isMSSQL2005Plus(connection) ? "varbinary(max)" : "image";
                break;
            }
            default: {
                throw new SQLException(Messages.get("error.baddatatype", Integer.toString(pi.jdbcType)), "HY000");
            }
        }
    }

    static int getTds5ParamSize(String charset, boolean isWideChar, ParamInfo pi, boolean useParamNames) {
        int size = 8;
        if (pi.name != null && useParamNames) {
            if (isWideChar) {
                byte[] buf = Support.encodeString(charset, pi.name);
                size += buf.length;
            } else {
                size += pi.name.length();
            }
        }
        switch (pi.tdsType) {
            case 37: 
            case 38: 
            case 39: 
            case 109: 
            case 111: 
            case 123: 
            case 147: {
                ++size;
                break;
            }
            case 36: 
            case 106: {
                size += 3;
                break;
            }
            case 175: 
            case 225: {
                size += 4;
                break;
            }
            case 50: {
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported output TDS type 0x" + Integer.toHexString(pi.tdsType));
            }
        }
        return size;
    }

    static void writeTds5ParamFmt(RequestStream out, String charset, boolean isWideChar, ParamInfo pi, boolean useParamNames) throws IOException {
        if (pi.name != null && useParamNames) {
            if (isWideChar) {
                byte[] buf = Support.encodeString(charset, pi.name);
                out.write((byte)buf.length);
                out.write(buf);
            } else {
                out.write((byte)pi.name.length());
                out.write(pi.name);
            }
        } else {
            out.write((byte)0);
        }
        out.write((byte)(pi.isOutput ? 1 : 0));
        if (pi.sqlType.startsWith("univarchar")) {
            out.write(35);
        } else if ("unitext".equals(pi.sqlType)) {
            out.write(36);
        } else {
            out.write(0);
        }
        out.write((byte)pi.tdsType);
        switch (pi.tdsType) {
            case 37: 
            case 39: {
                out.write((byte)-1);
                break;
            }
            case 175: {
                out.write(Integer.MAX_VALUE);
                break;
            }
            case 36: {
                out.write("text".equals(pi.sqlType) ? (byte)3 : 4);
                out.write((byte)0);
                out.write((byte)0);
                break;
            }
            case 225: {
                out.write(Integer.MAX_VALUE);
                break;
            }
            case 50: {
                break;
            }
            case 38: {
                out.write("bigint".equals(pi.sqlType) ? (byte)8 : 4);
                break;
            }
            case 109: {
                if (pi.value instanceof Float) {
                    out.write((byte)4);
                    break;
                }
                out.write((byte)8);
                break;
            }
            case 111: {
                out.write((byte)8);
                break;
            }
            case 123: 
            case 147: {
                out.write((byte)4);
                break;
            }
            case 106: {
                out.write((byte)17);
                out.write((byte)38);
                if (pi.jdbcType == -5) {
                    out.write((byte)0);
                    break;
                }
                if (pi.value instanceof BigDecimal) {
                    out.write((byte)((BigDecimal)pi.value).scale());
                    break;
                }
                if (pi.scale >= 0 && pi.scale <= 38) {
                    out.write((byte)pi.scale);
                    break;
                }
                out.write((byte)10);
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported output TDS type " + Integer.toHexString(pi.tdsType));
            }
        }
        out.write((byte)0);
    }

    static void writeTds5Param(RequestStream out, CharsetInfo charsetInfo, ParamInfo pi) throws IOException, SQLException {
        if (pi.charsetInfo == null) {
            pi.charsetInfo = charsetInfo;
        }
        switch (pi.tdsType) {
            case 39: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (buf.length == 0) {
                    buf = new byte[]{32};
                }
                if (buf.length > 255) {
                    throw new SQLException(Messages.get("error.generic.truncmbcs"), "HY000");
                }
                out.write((byte)buf.length);
                out.write(buf);
                break;
            }
            case 37: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (out.getTdsVersion() < 3 && buf.length == 0) {
                    out.write((byte)1);
                    out.write((byte)0);
                    break;
                }
                out.write((byte)buf.length);
                out.write(buf);
                break;
            }
            case 175: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (buf.length == 0) {
                    buf = new byte[]{32};
                }
                out.write(buf.length);
                out.write(buf);
                break;
            }
            case 36: {
                out.write((byte)0);
                out.write((byte)0);
                out.write((byte)0);
                if (pi.value instanceof InputStream) {
                    byte[] buffer = new byte[8192];
                    int len = ((InputStream)pi.value).read(buffer);
                    while (len > 0) {
                        out.write((byte)len);
                        out.write((byte)(len >> 8));
                        out.write((byte)(len >> 16));
                        out.write((byte)(len >> 24 | 0x80));
                        out.write(buffer, 0, len);
                        len = ((InputStream)pi.value).read(buffer);
                    }
                } else if (pi.value instanceof Reader && !pi.charsetInfo.isWideChars()) {
                    char[] buffer = new char[8192];
                    int len = ((Reader)pi.value).read(buffer);
                    while (len > 0) {
                        out.write((byte)len);
                        out.write((byte)(len >> 8));
                        out.write((byte)(len >> 16));
                        out.write((byte)(len >> 24 | 0x80));
                        out.write(Support.encodeString(pi.charsetInfo.getCharset(), new String(buffer, 0, len)));
                        len = ((Reader)pi.value).read(buffer);
                    }
                } else if (pi.value != null) {
                    if ("unitext".equals(pi.sqlType)) {
                        int clen;
                        String buf = pi.getString(pi.charsetInfo.getCharset());
                        for (int pos = 0; pos < buf.length(); pos += clen) {
                            clen = buf.length() - pos >= 4096 ? 4096 : buf.length() - pos;
                            int len = clen * 2;
                            out.write((byte)len);
                            out.write((byte)(len >> 8));
                            out.write((byte)(len >> 16));
                            out.write((byte)(len >> 24 | 0x80));
                            out.write(buf.substring(pos, pos + clen).toCharArray(), 0, clen);
                        }
                    } else {
                        byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                        int pos = 0;
                        while (pos < buf.length) {
                            int len = buf.length - pos >= 8192 ? 8192 : buf.length - pos;
                            out.write((byte)len);
                            out.write((byte)(len >> 8));
                            out.write((byte)(len >> 16));
                            out.write((byte)(len >> 24 | 0x80));
                            for (int i = 0; i < len; ++i) {
                                out.write(buf[pos++]);
                            }
                        }
                    }
                }
                out.write(0);
                break;
            }
            case 225: {
                if (pi.value == null) {
                    out.write(0);
                    break;
                }
                if (pi.sqlType.startsWith("univarchar")) {
                    String tmp = pi.getString(pi.charsetInfo.getCharset());
                    if (tmp.length() == 0) {
                        tmp = " ";
                    }
                    out.write(tmp.length() * 2);
                    out.write(tmp.toCharArray(), 0, tmp.length());
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (buf.length > 0) {
                    out.write(buf.length);
                    out.write(buf);
                    break;
                }
                out.write(1);
                out.write((byte)0);
                break;
            }
            case 38: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                if ("bigint".equals(pi.sqlType)) {
                    out.write((byte)8);
                    out.write(((Number)pi.value).longValue());
                    break;
                }
                out.write((byte)4);
                out.write(((Number)pi.value).intValue());
                break;
            }
            case 109: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                if (pi.value instanceof Float) {
                    out.write((byte)4);
                    out.write(((Number)pi.value).floatValue());
                    break;
                }
                out.write((byte)8);
                out.write(((Number)pi.value).doubleValue());
                break;
            }
            case 111: {
                TdsData.putDateTimeValue(out, (DateTime)pi.value);
                break;
            }
            case 123: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                out.write((byte)4);
                out.write(((DateTime)pi.value).getDate());
                break;
            }
            case 147: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                out.write((byte)4);
                out.write(((DateTime)pi.value).getTime());
                break;
            }
            case 50: {
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                out.write((byte)((Boolean)pi.value != false ? 1 : 0));
                break;
            }
            case 106: 
            case 108: {
                BigDecimal value = null;
                if (pi.value != null) {
                    value = pi.value instanceof Long ? new BigDecimal(pi.value.toString()) : (BigDecimal)pi.value;
                }
                out.write(value);
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported output TDS type " + Integer.toHexString(pi.tdsType));
            }
        }
    }

    static void putCollation(RequestStream out, ParamInfo pi) throws IOException {
        if (TdsData.types[pi.tdsType].isCollation) {
            if (pi.collation != null) {
                out.write(pi.collation);
            } else {
                byte[] collation = new byte[]{0, 0, 0, 0, 0};
                out.write(collation);
            }
        }
    }

    static void writeParam(RequestStream out, CharsetInfo charsetInfo, byte[] collation, ParamInfo pi) throws IOException {
        boolean isTds8;
        boolean bl = isTds8 = out.getTdsVersion() >= 4;
        if (isTds8 && pi.collation == null) {
            pi.collation = collation;
        }
        if (pi.charsetInfo == null) {
            pi.charsetInfo = charsetInfo;
        }
        switch (pi.tdsType) {
            case 167: {
                if (pi.value == null) {
                    out.write((byte)pi.tdsType);
                    out.write((short)8000);
                    if (isTds8) {
                        TdsData.putCollation(out, pi);
                    }
                    out.write((short)-1);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (buf.length > 8000) {
                    out.write((byte)35);
                    out.write(buf.length);
                    if (isTds8) {
                        TdsData.putCollation(out, pi);
                    }
                    out.write(buf.length);
                    out.write(buf);
                    break;
                }
                out.write((byte)pi.tdsType);
                out.write((short)8000);
                if (isTds8) {
                    TdsData.putCollation(out, pi);
                }
                out.write((short)buf.length);
                out.write(buf);
                break;
            }
            case 39: {
                if (pi.value == null) {
                    out.write((byte)pi.tdsType);
                    out.write((byte)-1);
                    out.write((byte)0);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (buf.length > 255) {
                    if (buf.length <= 8000 && out.getTdsVersion() >= 3) {
                        out.write((byte)-89);
                        out.write((short)8000);
                        if (isTds8) {
                            TdsData.putCollation(out, pi);
                        }
                        out.write((short)buf.length);
                        out.write(buf);
                        break;
                    }
                    out.write((byte)35);
                    out.write(buf.length);
                    if (isTds8) {
                        TdsData.putCollation(out, pi);
                    }
                    out.write(buf.length);
                    out.write(buf);
                    break;
                }
                if (buf.length == 0) {
                    buf = new byte[]{32};
                }
                out.write((byte)pi.tdsType);
                out.write((byte)-1);
                out.write((byte)buf.length);
                out.write(buf);
                break;
            }
            case 231: {
                out.write((byte)pi.tdsType);
                out.write((short)8000);
                if (isTds8) {
                    TdsData.putCollation(out, pi);
                }
                if (pi.value == null) {
                    out.write((short)-1);
                    break;
                }
                String tmp = pi.getString(pi.charsetInfo.getCharset());
                out.write((short)(tmp.length() * 2));
                out.write(tmp);
                break;
            }
            case 35: {
                int len;
                if (pi.value == null) {
                    len = 0;
                } else {
                    len = pi.length;
                    if (len == 0 && out.getTdsVersion() < 3) {
                        pi.value = " ";
                        len = 1;
                    }
                }
                out.write((byte)pi.tdsType);
                if (len > 0) {
                    if (pi.value instanceof InputStream) {
                        out.write(len);
                        if (isTds8) {
                            TdsData.putCollation(out, pi);
                        }
                        out.write(len);
                        out.writeStreamBytes((InputStream)pi.value, len);
                        break;
                    }
                    if (pi.value instanceof Reader && !pi.charsetInfo.isWideChars()) {
                        out.write(len);
                        if (isTds8) {
                            TdsData.putCollation(out, pi);
                        }
                        out.write(len);
                        out.writeReaderBytes((Reader)pi.value, len);
                        break;
                    }
                    byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                    out.write(buf.length);
                    if (isTds8) {
                        TdsData.putCollation(out, pi);
                    }
                    out.write(buf.length);
                    out.write(buf);
                    break;
                }
                out.write(len);
                if (isTds8) {
                    TdsData.putCollation(out, pi);
                }
                out.write(len);
                break;
            }
            case 99: {
                int len = pi.value == null ? 0 : pi.length;
                out.write((byte)pi.tdsType);
                if (len > 0) {
                    if (pi.value instanceof Reader) {
                        out.write(len);
                        if (isTds8) {
                            TdsData.putCollation(out, pi);
                        }
                        out.write(len * 2);
                        out.writeReaderChars((Reader)pi.value, len);
                        break;
                    }
                    if (pi.value instanceof InputStream && !pi.charsetInfo.isWideChars()) {
                        out.write(len);
                        if (isTds8) {
                            TdsData.putCollation(out, pi);
                        }
                        out.write(len * 2);
                        out.writeReaderChars(new InputStreamReader((InputStream)pi.value, pi.charsetInfo.getCharset()), len);
                        break;
                    }
                    String tmp = pi.getString(pi.charsetInfo.getCharset());
                    len = tmp.length();
                    out.write(len);
                    if (isTds8) {
                        TdsData.putCollation(out, pi);
                    }
                    out.write(len * 2);
                    out.write(tmp);
                    break;
                }
                out.write(len);
                if (isTds8) {
                    TdsData.putCollation(out, pi);
                }
                out.write(len);
                break;
            }
            case 165: {
                out.write((byte)pi.tdsType);
                out.write((short)8000);
                if (pi.value == null) {
                    out.write((short)-1);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                out.write((short)buf.length);
                out.write(buf);
                break;
            }
            case 37: {
                out.write((byte)pi.tdsType);
                out.write((byte)-1);
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                if (out.getTdsVersion() < 3 && buf.length == 0) {
                    out.write((byte)1);
                    out.write((byte)0);
                    break;
                }
                out.write((byte)buf.length);
                out.write(buf);
                break;
            }
            case 34: {
                int len = pi.value == null ? 0 : pi.length;
                out.write((byte)pi.tdsType);
                if (len > 0) {
                    if (pi.value instanceof InputStream) {
                        out.write(len);
                        out.write(len);
                        out.writeStreamBytes((InputStream)pi.value, len);
                        break;
                    }
                    byte[] buf = pi.getBytes(pi.charsetInfo.getCharset());
                    out.write(buf.length);
                    out.write(buf.length);
                    out.write(buf);
                    break;
                }
                if (out.getTdsVersion() < 3) {
                    out.write(1);
                    out.write(1);
                    out.write((byte)0);
                    break;
                }
                out.write(len);
                out.write(len);
                break;
            }
            case 38: {
                out.write((byte)pi.tdsType);
                if (pi.value == null) {
                    out.write("bigint".equals(pi.sqlType) ? (byte)8 : 4);
                    out.write((byte)0);
                    break;
                }
                if ("bigint".equals(pi.sqlType)) {
                    out.write((byte)8);
                    out.write((byte)8);
                    out.write(((Number)pi.value).longValue());
                    break;
                }
                out.write((byte)4);
                out.write((byte)4);
                out.write(((Number)pi.value).intValue());
                break;
            }
            case 109: {
                out.write((byte)pi.tdsType);
                if (pi.value instanceof Float) {
                    out.write((byte)4);
                    out.write((byte)4);
                    out.write(((Number)pi.value).floatValue());
                    break;
                }
                out.write((byte)8);
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                out.write((byte)8);
                out.write(((Number)pi.value).doubleValue());
                break;
            }
            case 111: {
                out.write((byte)111);
                out.write((byte)8);
                TdsData.putDateTimeValue(out, (DateTime)pi.value);
                break;
            }
            case 50: {
                out.write((byte)pi.tdsType);
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                out.write((byte)((Boolean)pi.value != false ? 1 : 0));
                break;
            }
            case 104: {
                out.write((byte)104);
                out.write((byte)1);
                if (pi.value == null) {
                    out.write((byte)0);
                    break;
                }
                out.write((byte)1);
                out.write((byte)((Boolean)pi.value != false ? 1 : 0));
                break;
            }
            case 106: 
            case 108: {
                int scale;
                out.write((byte)pi.tdsType);
                BigDecimal value = null;
                int prec = out.getMaxPrecision();
                if (pi.value == null) {
                    scale = pi.jdbcType == -5 ? 0 : (pi.scale >= 0 && pi.scale <= prec ? pi.scale : 10);
                } else if (pi.value instanceof Long) {
                    value = new BigDecimal(((Long)pi.value).toString());
                    scale = 0;
                } else {
                    value = (BigDecimal)pi.value;
                    scale = value.scale();
                }
                out.write(out.getMaxDecimalBytes());
                out.write((byte)prec);
                out.write((byte)scale);
                out.write(value);
                break;
            }
            case 241: {
                int len;
                out.write((byte)pi.tdsType);
                out.write((byte)0);
                if (pi.value == null) {
                    out.write(-1L);
                    break;
                }
                out.write((long)len);
                out.write(len);
                if (pi.value instanceof byte[]) {
                    out.write((byte[])pi.value);
                } else if (pi.value instanceof InputStream) {
                    int res;
                    byte[] buffer = new byte[1024];
                    for (len = pi.length; len > 0; len -= res) {
                        res = ((InputStream)pi.value).read(buffer);
                        if (res < 0) {
                            throw new IOException(Messages.get("error.io.outofdata"));
                        }
                        out.write(buffer, 0, res);
                    }
                }
                out.write(0);
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported output TDS type " + Integer.toHexString(pi.tdsType));
            }
        }
    }

    private TdsData() {
    }

    private static Object getDatetimeValue(ResponseStream in, int type) throws IOException, ProtocolException {
        int len = type == 111 ? in.read() : (type == 58 ? 4 : 8);
        switch (len) {
            case 0: {
                return null;
            }
            case 8: {
                int daysSince1900 = in.readInt();
                int time = in.readInt();
                return new DateTime(daysSince1900, time);
            }
            case 4: {
                int daysSince1900 = in.readShort() & 0xFFFF;
                short minutes = in.readShort();
                return new DateTime((short)daysSince1900, minutes);
            }
        }
        throw new ProtocolException("Invalid DATETIME value with size of " + len + " bytes.");
    }

    private static void putDateTimeValue(RequestStream out, DateTime value) throws IOException {
        if (value == null) {
            out.write((byte)0);
            return;
        }
        out.write((byte)8);
        out.write(value.getDate());
        out.write(value.getTime());
    }

    private static Object getMoneyValue(ResponseStream in, int type) throws IOException, ProtocolException {
        int len = type == 60 ? 8 : (type == 110 ? in.read() : 4);
        BigInteger x = null;
        if (len == 4) {
            x = BigInteger.valueOf(in.readInt());
        } else if (len == 8) {
            byte b4 = (byte)in.read();
            byte b5 = (byte)in.read();
            byte b6 = (byte)in.read();
            byte b7 = (byte)in.read();
            byte b0 = (byte)in.read();
            byte b1 = (byte)in.read();
            byte b2 = (byte)in.read();
            byte b3 = (byte)in.read();
            long l = (long)(b0 & 0xFF) + ((long)(b1 & 0xFF) << 8) + ((long)(b2 & 0xFF) << 16) + ((long)(b3 & 0xFF) << 24) + ((long)(b4 & 0xFF) << 32) + ((long)(b5 & 0xFF) << 40) + ((long)(b6 & 0xFF) << 48) + ((long)(b7 & 0xFF) << 56);
            x = BigInteger.valueOf(l);
        } else if (len != 0) {
            throw new ProtocolException("Invalid money value.");
        }
        return x == null ? null : new BigDecimal(x, 4);
    }

    private static Object getVariant(JtdsConnection connection, ResponseStream in) throws IOException, ProtocolException {
        int len = in.readInt();
        if (len == 0) {
            return null;
        }
        ColInfo ci = new ColInfo();
        len -= 2;
        ci.tdsType = in.read();
        len -= in.read();
        switch (ci.tdsType) {
            case 48: {
                return new Integer(in.read() & 0xFF);
            }
            case 52: {
                return new Integer(in.readShort());
            }
            case 56: {
                return new Integer(in.readInt());
            }
            case 127: {
                return new Long(in.readLong());
            }
            case 167: 
            case 175: {
                TdsData.getCollation(in, ci);
                try {
                    TdsData.setColumnCharset(ci, connection);
                }
                catch (SQLException ex) {
                    in.skip(2 + len);
                    throw new ProtocolException(ex.toString() + " [SQLState: " + ex.getSQLState() + ']');
                }
                in.skip(2);
                return in.readNonUnicodeString(len);
            }
            case 231: 
            case 239: {
                in.skip(7);
                return in.readUnicodeString(len / 2);
            }
            case 165: 
            case 173: {
                in.skip(2);
                byte[] bytes = new byte[len];
                in.read(bytes);
                return bytes;
            }
            case 60: 
            case 122: {
                return TdsData.getMoneyValue(in, ci.tdsType);
            }
            case 58: 
            case 61: {
                return TdsData.getDatetimeValue(in, ci.tdsType);
            }
            case 50: {
                return in.read() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            case 59: {
                return new Float(Float.intBitsToFloat(in.readInt()));
            }
            case 62: {
                return new Double(Double.longBitsToDouble(in.readLong()));
            }
            case 36: {
                byte[] bytes = new byte[len];
                in.read(bytes);
                return new UniqueIdentifier(bytes);
            }
            case 106: 
            case 108: {
                ci.precision = in.read();
                ci.scale = in.read();
                int sign = in.read();
                byte[] bytes = new byte[--len];
                while (len-- > 0) {
                    bytes[len] = (byte)in.read();
                }
                BigInteger bi = new BigInteger(sign == 0 ? -1 : 1, bytes);
                return new BigDecimal(bi, ci.scale);
            }
        }
        throw new ProtocolException("Unsupported TDS data type 0x" + Integer.toHexString(ci.tdsType) + " in sql_variant");
    }

    public static String getMSTypeName(String typeName, int tdsType) {
        if (typeName.equalsIgnoreCase("text") && tdsType != 35) {
            return "varchar";
        }
        if (typeName.equalsIgnoreCase("ntext") && tdsType != 35) {
            return "nvarchar";
        }
        if (typeName.equalsIgnoreCase("image") && tdsType != 34) {
            return "varbinary";
        }
        return typeName;
    }

    public static int getTdsVersion(int rawTdsVersion) {
        if (rawTdsVersion >= 0x71000001) {
            return 5;
        }
        if (rawTdsVersion >= 0x7010000) {
            return 4;
        }
        if (rawTdsVersion >= 0x7000000) {
            return 3;
        }
        if (rawTdsVersion >= 0x5000000) {
            return 2;
        }
        return 1;
    }

    private static boolean canEncode(String value, String charset) {
        if (value == null) {
            return true;
        }
        if ("UTF-8".equals(charset)) {
            return true;
        }
        if ("ISO-8859-1".equals(charset)) {
            for (int i = value.length() - 1; i >= 0; --i) {
                if (value.charAt(i) <= '\u00ff') continue;
                return false;
            }
            return true;
        }
        if ("ISO-8859-15".equals(charset) || "Cp1252".equals(charset)) {
            for (int i = value.length() - 1; i >= 0; --i) {
                char c = value.charAt(i);
                if (c <= '\u00ff' || c == '\u20ac') continue;
                return false;
            }
            return true;
        }
        if ("US-ASCII".equals(charset)) {
            for (int i = value.length() - 1; i >= 0; --i) {
                if (value.charAt(i) <= '\u007f') continue;
                return false;
            }
            return true;
        }
        try {
            return new String(value.getBytes(charset), charset).equals(value);
        }
        catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    static boolean isMSSQL2005Plus(JtdsConnection connection) {
        return connection.getServerType() == 1 && connection.getDatabaseMajorVersion() > 8;
    }

    static {
        TdsData.types[47] = new TypeInfo("char", -1, -1, 1, false, false, 1);
        TdsData.types[39] = new TypeInfo("varchar", -1, -1, 1, false, false, 12);
        TdsData.types[38] = new TypeInfo("int", -1, 10, 11, true, false, 4);
        TdsData.types[48] = new TypeInfo("tinyint", 1, 3, 4, false, false, -6);
        TdsData.types[52] = new TypeInfo("smallint", 2, 5, 6, true, false, 5);
        TdsData.types[56] = new TypeInfo("int", 4, 10, 11, true, false, 4);
        TdsData.types[127] = new TypeInfo("bigint", 8, 19, 20, true, false, -5);
        TdsData.types[62] = new TypeInfo("float", 8, 15, 24, true, false, 8);
        TdsData.types[61] = new TypeInfo("datetime", 8, 23, 23, false, false, 93);
        TdsData.types[50] = new TypeInfo("bit", 1, 1, 1, false, false, -7);
        TdsData.types[35] = new TypeInfo("text", -4, -1, -1, false, true, 2005);
        TdsData.types[99] = new TypeInfo("ntext", -4, -1, -1, false, true, 2005);
        TdsData.types[174] = new TypeInfo("unitext", -4, -1, -1, false, true, 2005);
        TdsData.types[34] = new TypeInfo("image", -4, -1, -1, false, false, 2004);
        TdsData.types[122] = new TypeInfo("smallmoney", 4, 10, 12, true, false, 3);
        TdsData.types[60] = new TypeInfo("money", 8, 19, 21, true, false, 3);
        TdsData.types[58] = new TypeInfo("smalldatetime", 4, 16, 19, false, false, 93);
        TdsData.types[59] = new TypeInfo("real", 4, 7, 14, true, false, 7);
        TdsData.types[45] = new TypeInfo("binary", -1, -1, 2, false, false, -2);
        TdsData.types[31] = new TypeInfo("void", -1, 1, 1, false, false, 0);
        TdsData.types[37] = new TypeInfo("varbinary", -1, -1, -1, false, false, -3);
        TdsData.types[103] = new TypeInfo("nvarchar", -1, -1, -1, false, false, 12);
        TdsData.types[104] = new TypeInfo("bit", -1, 1, 1, false, false, -7);
        TdsData.types[108] = new TypeInfo("numeric", -1, -1, -1, true, false, 2);
        TdsData.types[106] = new TypeInfo("decimal", -1, -1, -1, true, false, 3);
        TdsData.types[109] = new TypeInfo("float", -1, 15, 24, true, false, 8);
        TdsData.types[110] = new TypeInfo("money", -1, 19, 21, true, false, 3);
        TdsData.types[111] = new TypeInfo("datetime", -1, 23, 23, false, false, 93);
        TdsData.types[49] = new TypeInfo("date", 4, 10, 10, false, false, 91);
        TdsData.types[51] = new TypeInfo("time", 4, 8, 8, false, false, 92);
        TdsData.types[123] = new TypeInfo("date", -1, 10, 10, false, false, 91);
        TdsData.types[147] = new TypeInfo("time", -1, 8, 8, false, false, 92);
        TdsData.types[175] = new TypeInfo("char", -2, -1, -1, false, true, 1);
        TdsData.types[167] = new TypeInfo("varchar", -2, -1, -1, false, true, 12);
        TdsData.types[231] = new TypeInfo("nvarchar", -2, -1, -1, false, true, 12);
        TdsData.types[239] = new TypeInfo("nchar", -2, -1, -1, false, true, 1);
        TdsData.types[165] = new TypeInfo("varbinary", -2, -1, -1, false, false, -3);
        TdsData.types[173] = new TypeInfo("binary", -2, -1, -1, false, false, -2);
        TdsData.types[225] = new TypeInfo("varbinary", -5, -1, 2, false, false, -2);
        TdsData.types[64] = new TypeInfo("tinyint", 1, 2, 3, false, false, -6);
        TdsData.types[65] = new TypeInfo("unsigned smallint", 2, 5, 6, false, false, 4);
        TdsData.types[66] = new TypeInfo("unsigned int", 4, 10, 11, false, false, -5);
        TdsData.types[67] = new TypeInfo("unsigned bigint", 8, 20, 20, false, false, 3);
        TdsData.types[68] = new TypeInfo("unsigned int", -1, 10, 11, true, false, -5);
        TdsData.types[36] = new TypeInfo("uniqueidentifier", -1, 36, 36, false, false, 1);
        TdsData.types[98] = new TypeInfo("sql_variant", -5, 0, 8000, false, false, 12);
        TdsData.types[191] = new TypeInfo("bigint", 8, 19, 20, true, false, -5);
        TdsData.types[241] = new TypeInfo("xml", -4, -1, -1, false, true, 2009);
        TdsData.types[40] = new TypeInfo("date", 3, 10, 10, false, false, 91);
        TdsData.types[41] = new TypeInfo("time", -1, -1, -1, false, false, 92);
        TdsData.types[42] = new TypeInfo("datetime2", -1, -1, -1, false, false, 93);
        TdsData.types[43] = new TypeInfo("datetimeoffset", -1, -1, -1, false, false, 93);
    }

    private static class TypeInfo {
        public final String sqlType;
        public final int size;
        public final int precision;
        public final int displaySize;
        public final boolean isSigned;
        public final boolean isCollation;
        public final int jdbcType;

        TypeInfo(String sqlType, int size, int precision, int displaySize, boolean isSigned, boolean isCollation, int jdbcType) {
            this.sqlType = sqlType;
            this.size = size;
            this.precision = precision;
            this.displaySize = displaySize;
            this.isSigned = isSigned;
            this.isCollation = isCollation;
            this.jdbcType = jdbcType;
        }
    }
}

