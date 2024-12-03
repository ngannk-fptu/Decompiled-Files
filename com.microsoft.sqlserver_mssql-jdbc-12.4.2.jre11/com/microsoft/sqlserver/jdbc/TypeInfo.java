/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Encoding;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSLenType;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSType;
import com.microsoft.sqlserver.jdbc.UDTTDSHeader;
import com.microsoft.sqlserver.jdbc.XMLTDSHeader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.Map;

final class TypeInfo
implements Serializable {
    private static final long serialVersionUID = 6641910171379986768L;
    private int maxLength;
    private SSLenType ssLenType;
    private int precision;
    private int displaySize;
    private int scale;
    private short flags;
    private SSType ssType;
    private int userType;
    private String udtTypeName;
    private SQLCollation collation;
    private transient Charset charset;
    static final int UPDATABLE_READ_ONLY = 0;
    static final int UPDATABLE_READ_WRITE = 1;
    static final int UPDATABLE_UNKNOWN = 2;
    private static final Map<TDSType, Builder> builderMap = new EnumMap<TDSType, Builder>(TDSType.class);

    SSType getSSType() {
        return this.ssType;
    }

    void setSSType(SSType ssType) {
        this.ssType = ssType;
    }

    SSLenType getSSLenType() {
        return this.ssLenType;
    }

    void setSSLenType(SSLenType ssLenType) {
        this.ssLenType = ssLenType;
    }

    String getSSTypeName() {
        return SSType.UDT == this.ssType ? this.udtTypeName : this.ssType.toString();
    }

    int getMaxLength() {
        return this.maxLength;
    }

    void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    int getPrecision() {
        return this.precision;
    }

    void setPrecision(int precision) {
        this.precision = precision;
    }

    int getDisplaySize() {
        return this.displaySize;
    }

    void setDisplaySize(int displaySize) {
        this.displaySize = displaySize;
    }

    int getScale() {
        return this.scale;
    }

    SQLCollation getSQLCollation() {
        return this.collation;
    }

    void setSQLCollation(SQLCollation collation) {
        this.collation = collation;
    }

    Charset getCharset() {
        return this.charset;
    }

    void setCharset(Charset charset) {
        this.charset = charset;
    }

    boolean isNullable() {
        return 1 == (this.flags & 1);
    }

    boolean isCaseSensitive() {
        return 2 == (this.flags & 2);
    }

    boolean isSparseColumnSet() {
        return 1024 == (this.flags & 0x400);
    }

    boolean isEncrypted() {
        return 2048 == (this.flags & 0x800);
    }

    int getUpdatability() {
        return this.flags >> 2 & 3;
    }

    boolean isIdentity() {
        return 16 == (this.flags & 0x10);
    }

    byte[] getFlags() {
        byte[] f = new byte[]{(byte)(this.flags & 0xFF), (byte)(this.flags >> 8 & 0xFF)};
        return f;
    }

    short getFlagsAsShort() {
        return this.flags;
    }

    void setFlags(Short flags) {
        this.flags = flags;
    }

    void setScale(int scale) {
        this.scale = scale;
    }

    boolean supportsFastAsciiConversion() {
        switch (this.ssType) {
            case CHAR: 
            case VARCHAR: 
            case VARCHARMAX: 
            case TEXT: {
                return this.collation.hasAsciiCompatibleSBCS();
            }
        }
        return false;
    }

    private TypeInfo() {
    }

    static TypeInfo getInstance(TDSReader tdsReader, boolean readFlags) throws SQLServerException {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.userType = tdsReader.readInt();
        if (readFlags) {
            typeInfo.flags = tdsReader.readShort();
        }
        TDSType tdsType = null;
        try {
            tdsType = TDSType.valueOf(tdsReader.readUnsignedByte());
        }
        catch (IllegalArgumentException e) {
            tdsReader.getConnection().terminate(4, e.getMessage(), e);
        }
        assert (null != builderMap.get((Object)tdsType)) : "Missing TypeInfo builder for TDSType " + tdsType;
        return builderMap.get((Object)tdsType).build(typeInfo, tdsReader);
    }

    static {
        for (Builder builder : Builder.values()) {
            builderMap.put(builder.getTDSType(), builder);
        }
    }

    static final class Builder
    extends Enum<Builder> {
        public static final /* enum */ Builder BIT = new Builder(TDSType.BIT1, new FixedLenStrategy(SSType.BIT, 1, 1, "1".length(), 0));
        public static final /* enum */ Builder BIGINT = new Builder(TDSType.INT8, new FixedLenStrategy(SSType.BIGINT, 8, Long.toString(Long.MAX_VALUE).length(), ("-" + Long.toString(Long.MAX_VALUE)).length(), 0));
        public static final /* enum */ Builder INTEGER = new Builder(TDSType.INT4, new FixedLenStrategy(SSType.INTEGER, 4, Integer.toString(Integer.MAX_VALUE).length(), ("-" + Integer.toString(Integer.MAX_VALUE)).length(), 0));
        public static final /* enum */ Builder SMALLINT = new Builder(TDSType.INT2, new FixedLenStrategy(SSType.SMALLINT, 2, Short.toString((short)Short.MAX_VALUE).length(), ("-" + Short.toString((short)Short.MAX_VALUE)).length(), 0));
        public static final /* enum */ Builder TINYINT = new Builder(TDSType.INT1, new FixedLenStrategy(SSType.TINYINT, 1, Byte.toString((byte)127).length(), Byte.toString((byte)127).length(), 0));
        public static final /* enum */ Builder REAL = new Builder(TDSType.FLOAT4, new FixedLenStrategy(SSType.REAL, 4, 7, 13, 0));
        public static final /* enum */ Builder FLOAT = new Builder(TDSType.FLOAT8, new FixedLenStrategy(SSType.FLOAT, 8, 15, 22, 0));
        public static final /* enum */ Builder SMALLDATETIME = new Builder(TDSType.DATETIME4, new FixedLenStrategy(SSType.SMALLDATETIME, 4, "yyyy-mm-dd hh:mm".length(), "yyyy-mm-dd hh:mm".length(), 0));
        public static final /* enum */ Builder DATETIME = new Builder(TDSType.DATETIME8, new FixedLenStrategy(SSType.DATETIME, 8, "yyyy-mm-dd hh:mm:ss.fff".length(), "yyyy-mm-dd hh:mm:ss.fff".length(), 3));
        public static final /* enum */ Builder SMALLMONEY = new Builder(TDSType.MONEY4, new FixedLenStrategy(SSType.SMALLMONEY, 4, Integer.toString(Integer.MAX_VALUE).length(), ("-." + Integer.toString(Integer.MAX_VALUE)).length(), 4));
        public static final /* enum */ Builder MONEY = new Builder(TDSType.MONEY8, new FixedLenStrategy(SSType.MONEY, 8, Long.toString(Long.MAX_VALUE).length(), ("-." + Long.toString(Long.MAX_VALUE)).length(), 4));
        public static final /* enum */ Builder BITN = new Builder(TDSType.BITN, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                if (1 != tdsReader.readUnsignedByte()) {
                    tdsReader.throwInvalidTDS();
                }
                BIT.build(typeInfo, tdsReader);
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
            }
        });
        public static final /* enum */ Builder INTN = new Builder(TDSType.INTN, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                switch (tdsReader.readUnsignedByte()) {
                    case 8: {
                        BIGINT.build(typeInfo, tdsReader);
                        break;
                    }
                    case 4: {
                        INTEGER.build(typeInfo, tdsReader);
                        break;
                    }
                    case 2: {
                        SMALLINT.build(typeInfo, tdsReader);
                        break;
                    }
                    case 1: {
                        TINYINT.build(typeInfo, tdsReader);
                        break;
                    }
                    default: {
                        tdsReader.throwInvalidTDS();
                    }
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
            }
        });
        public static final /* enum */ Builder DECIMAL = new Builder(TDSType.DECIMALN, new DecimalNumericStrategy(SSType.DECIMAL));
        public static final /* enum */ Builder NUMERIC = new Builder(TDSType.NUMERICN, new DecimalNumericStrategy(SSType.NUMERIC));
        public static final /* enum */ Builder FLOATN = new Builder(TDSType.FLOATN, new BigOrSmallByteLenStrategy(FLOAT, REAL));
        public static final /* enum */ Builder MONEYN = new Builder(TDSType.MONEYN, new BigOrSmallByteLenStrategy(MONEY, SMALLMONEY));
        public static final /* enum */ Builder DATETIMEN = new Builder(TDSType.DATETIMEN, new BigOrSmallByteLenStrategy(DATETIME, SMALLDATETIME));
        public static final /* enum */ Builder TIME = new Builder(TDSType.TIMEN, new KatmaiScaledTemporalStrategy(SSType.TIME));
        public static final /* enum */ Builder DATETIME2 = new Builder(TDSType.DATETIME2N, new KatmaiScaledTemporalStrategy(SSType.DATETIME2));
        public static final /* enum */ Builder DATETIMEOFFSET = new Builder(TDSType.DATETIMEOFFSETN, new KatmaiScaledTemporalStrategy(SSType.DATETIMEOFFSET));
        public static final /* enum */ Builder DATE = new Builder(TDSType.DATEN, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssType = SSType.DATE;
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.maxLength = 3;
                typeInfo.displaySize = typeInfo.precision = "yyyy-mm-dd".length();
            }
        });
        public static final /* enum */ Builder BIGBINARY = new Builder(TDSType.BIGBINARY, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (typeInfo.maxLength > 8000) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.precision = typeInfo.maxLength;
                typeInfo.displaySize = 2 * typeInfo.maxLength;
                typeInfo.ssType = 80 == typeInfo.userType ? SSType.TIMESTAMP : SSType.BINARY;
            }
        });
        public static final /* enum */ Builder BIGVARBINARY = new Builder(TDSType.BIGVARBINARY, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                    typeInfo.ssType = SSType.VARBINARYMAX;
                    typeInfo.precision = Integer.MAX_VALUE;
                    typeInfo.displaySize = Integer.MAX_VALUE;
                } else if (typeInfo.maxLength <= 8000) {
                    typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                    typeInfo.ssType = SSType.VARBINARY;
                    typeInfo.precision = typeInfo.maxLength;
                    typeInfo.displaySize = 2 * typeInfo.maxLength;
                } else {
                    tdsReader.throwInvalidTDS();
                }
            }
        });
        public static final /* enum */ Builder IMAGE = new Builder(TDSType.IMAGE, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                if (typeInfo.maxLength < 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssType = SSType.IMAGE;
                typeInfo.precision = Integer.MAX_VALUE;
                typeInfo.displaySize = Integer.MAX_VALUE;
            }
        });
        public static final /* enum */ Builder BIGCHAR = new Builder(TDSType.BIGCHAR, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (typeInfo.maxLength > 8000) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.displaySize = typeInfo.precision = typeInfo.maxLength;
                typeInfo.ssType = SSType.CHAR;
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = typeInfo.collation.getCharset();
            }
        });
        public static final /* enum */ Builder BIGVARCHAR = new Builder(TDSType.BIGVARCHAR, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                    typeInfo.ssType = SSType.VARCHARMAX;
                    typeInfo.precision = Integer.MAX_VALUE;
                    typeInfo.displaySize = Integer.MAX_VALUE;
                } else if (typeInfo.maxLength <= 8000) {
                    typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                    typeInfo.ssType = SSType.VARCHAR;
                    typeInfo.displaySize = typeInfo.precision = typeInfo.maxLength;
                } else {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = typeInfo.collation.getCharset();
            }
        });
        public static final /* enum */ Builder TEXT = new Builder(TDSType.TEXT, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                if (typeInfo.maxLength < 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssType = SSType.TEXT;
                typeInfo.precision = Integer.MAX_VALUE;
                typeInfo.displaySize = Integer.MAX_VALUE;
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = typeInfo.collation.getCharset();
            }
        });
        public static final /* enum */ Builder NCHAR = new Builder(TDSType.NCHAR, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (typeInfo.maxLength > 8000 || 0 != typeInfo.maxLength % 2) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.displaySize = typeInfo.precision = typeInfo.maxLength / 2;
                typeInfo.ssType = SSType.NCHAR;
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        });
        public static final /* enum */ Builder NVARCHAR = new Builder(TDSType.NVARCHAR, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                    typeInfo.ssType = SSType.NVARCHARMAX;
                    typeInfo.precision = 0x3FFFFFFF;
                    typeInfo.displaySize = 0x3FFFFFFF;
                } else if (typeInfo.maxLength <= 8000 && 0 == typeInfo.maxLength % 2) {
                    typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                    typeInfo.ssType = SSType.NVARCHAR;
                    typeInfo.displaySize = typeInfo.precision = typeInfo.maxLength / 2;
                } else {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        });
        public static final /* enum */ Builder NTEXT = new Builder(TDSType.NTEXT, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                if (typeInfo.maxLength < 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssType = SSType.NTEXT;
                typeInfo.precision = 0x3FFFFFFF;
                typeInfo.displaySize = 0x3FFFFFFF;
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        });
        public static final /* enum */ Builder GUID = new Builder(TDSType.GUID, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                int maxLength = tdsReader.readUnsignedByte();
                if (maxLength != 16 && maxLength != 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.ssType = SSType.GUID;
                typeInfo.maxLength = maxLength;
                typeInfo.displaySize = typeInfo.precision = "NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN".length();
            }
        });
        public static final /* enum */ Builder UDT = new Builder(TDSType.UDT, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                UDTTDSHeader udtTDSHeader = new UDTTDSHeader(tdsReader);
                typeInfo.maxLength = udtTDSHeader.getMaxLen();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.precision = Integer.MAX_VALUE;
                    typeInfo.displaySize = Integer.MAX_VALUE;
                } else if (typeInfo.maxLength <= 8000) {
                    typeInfo.precision = typeInfo.maxLength;
                    typeInfo.displaySize = 2 * typeInfo.maxLength;
                } else {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                typeInfo.ssType = SSType.UDT;
                typeInfo.udtTypeName = udtTDSHeader.getTypeName();
            }
        });
        public static final /* enum */ Builder XML = new Builder(TDSType.XML, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                new XMLTDSHeader(tdsReader);
                typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                typeInfo.ssType = SSType.XML;
                typeInfo.precision = 0x3FFFFFFF;
                typeInfo.displaySize = 0x3FFFFFFF;
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        });
        public static final /* enum */ Builder SQL_VARIANT = new Builder(TDSType.SQL_VARIANT, new Strategy(){

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                typeInfo.ssType = SSType.SQL_VARIANT;
            }
        });
        private final TDSType tdsType;
        private final Strategy strategy;
        private static final /* synthetic */ Builder[] $VALUES;

        public static Builder[] values() {
            return (Builder[])$VALUES.clone();
        }

        public static Builder valueOf(String name) {
            return Enum.valueOf(Builder.class, name);
        }

        private Builder(TDSType tdsType, Strategy strategy) {
            this.tdsType = tdsType;
            this.strategy = strategy;
        }

        final TDSType getTDSType() {
            return this.tdsType;
        }

        final TypeInfo build(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
            this.strategy.apply(typeInfo, tdsReader);
            assert (null != typeInfo.ssType);
            assert (null != typeInfo.ssLenType);
            return typeInfo;
        }

        static {
            $VALUES = new Builder[]{BIT, BIGINT, INTEGER, SMALLINT, TINYINT, REAL, FLOAT, SMALLDATETIME, DATETIME, SMALLMONEY, MONEY, BITN, INTN, DECIMAL, NUMERIC, FLOATN, MONEYN, DATETIMEN, TIME, DATETIME2, DATETIMEOFFSET, DATE, BIGBINARY, BIGVARBINARY, IMAGE, BIGCHAR, BIGVARCHAR, TEXT, NCHAR, NVARCHAR, NTEXT, GUID, UDT, XML, SQL_VARIANT};
        }

        private static final class KatmaiScaledTemporalStrategy
        implements Strategy {
            private final SSType ssType;

            KatmaiScaledTemporalStrategy(SSType ssType) {
                this.ssType = ssType;
            }

            private int getPrecision(String baseFormat, int scale) {
                return baseFormat.length() + (scale > 0 ? 1 + scale : 0);
            }

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                typeInfo.scale = tdsReader.readUnsignedByte();
                if (typeInfo.scale > 7) {
                    tdsReader.throwInvalidTDS();
                }
                switch (this.ssType) {
                    case TIME: {
                        typeInfo.precision = this.getPrecision("hh:mm:ss", typeInfo.scale);
                        typeInfo.maxLength = TDS.timeValueLength(typeInfo.scale);
                        break;
                    }
                    case DATETIME2: {
                        typeInfo.precision = this.getPrecision("yyyy-mm-dd hh:mm:ss", typeInfo.scale);
                        typeInfo.maxLength = TDS.datetime2ValueLength(typeInfo.scale);
                        break;
                    }
                    case DATETIMEOFFSET: {
                        typeInfo.precision = this.getPrecision("yyyy-mm-dd hh:mm:ss +HH:MM", typeInfo.scale);
                        typeInfo.maxLength = TDS.datetimeoffsetValueLength(typeInfo.scale);
                        break;
                    }
                    default: {
                        assert (false) : "Unexpected SSType: " + this.ssType;
                        break;
                    }
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.ssType = this.ssType;
                typeInfo.displaySize = typeInfo.precision;
            }
        }

        private static final class BigOrSmallByteLenStrategy
        implements Strategy {
            private final Builder bigBuilder;
            private final Builder smallBuilder;

            BigOrSmallByteLenStrategy(Builder bigBuilder, Builder smallBuilder) {
                this.bigBuilder = bigBuilder;
                this.smallBuilder = smallBuilder;
            }

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                switch (tdsReader.readUnsignedByte()) {
                    case 8: {
                        this.bigBuilder.build(typeInfo, tdsReader);
                        break;
                    }
                    case 4: {
                        this.smallBuilder.build(typeInfo, tdsReader);
                        break;
                    }
                    default: {
                        tdsReader.throwInvalidTDS();
                    }
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
            }
        }

        private static final class DecimalNumericStrategy
        implements Strategy {
            private final SSType ssType;

            DecimalNumericStrategy(SSType ssType) {
                this.ssType = ssType;
            }

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) throws SQLServerException {
                int maxLength = tdsReader.readUnsignedByte();
                int precision = tdsReader.readUnsignedByte();
                int scale = tdsReader.readUnsignedByte();
                if (maxLength > 17) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.ssType = this.ssType;
                typeInfo.maxLength = maxLength;
                typeInfo.precision = precision;
                typeInfo.displaySize = precision + 2;
                typeInfo.scale = scale;
            }
        }

        private static final class FixedLenStrategy
        implements Strategy {
            private final SSType ssType;
            private final int maxLength;
            private final int precision;
            private final int displaySize;
            private final int scale;

            FixedLenStrategy(SSType ssType, int maxLength, int precision, int displaySize, int scale) {
                this.ssType = ssType;
                this.maxLength = maxLength;
                this.precision = precision;
                this.displaySize = displaySize;
                this.scale = scale;
            }

            @Override
            public void apply(TypeInfo typeInfo, TDSReader tdsReader) {
                typeInfo.ssLenType = SSLenType.FIXEDLENTYPE;
                typeInfo.ssType = this.ssType;
                typeInfo.maxLength = this.maxLength;
                typeInfo.precision = this.precision;
                typeInfo.displaySize = this.displaySize;
                typeInfo.scale = this.scale;
            }
        }

        private static interface Strategy {
            public void apply(TypeInfo var1, TDSReader var2) throws SQLServerException;
        }
    }
}

