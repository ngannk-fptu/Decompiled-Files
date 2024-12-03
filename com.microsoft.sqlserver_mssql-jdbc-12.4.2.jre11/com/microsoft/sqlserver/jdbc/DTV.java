/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.AppDTVImpl;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DDC;
import com.microsoft.sqlserver.jdbc.DTVExecuteOp;
import com.microsoft.sqlserver.jdbc.DTVImpl;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.ReaderInputStream;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.ServerDTVImpl;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSType;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.TVP;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.UTC;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.sql.DateTimeOffset;

final class DTV {
    private static final Logger aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.DTV");
    private DTVImpl impl;
    CryptoMetadata cryptoMeta = null;
    JDBCType jdbcTypeSetByUser = null;
    int valueLength = 0;
    boolean sendStringParametersAsUnicode = true;

    DTV() {
    }

    void setValue(SQLCollation collation, JDBCType jdbcType, Object value, JavaType javaType, StreamSetterArgs streamSetterArgs, Calendar calendar, Integer scale, SQLServerConnection con, boolean forceEncrypt) throws SQLServerException {
        if (null == this.impl) {
            this.impl = new AppDTVImpl();
        }
        this.impl.setValue(this, collation, jdbcType, value, javaType, streamSetterArgs, calendar, scale, con, forceEncrypt);
    }

    final void setValue(Object value, JavaType javaType) {
        this.impl.setValue(value, javaType);
    }

    final void clear() {
        this.impl = null;
    }

    final void skipValue(TypeInfo type, TDSReader tdsReader, boolean isDiscard) throws SQLServerException {
        if (null == this.impl) {
            this.impl = new ServerDTVImpl();
        }
        this.impl.skipValue(type, tdsReader, isDiscard);
    }

    final void initFromCompressedNull() {
        if (null == this.impl) {
            this.impl = new ServerDTVImpl();
        }
        this.impl.initFromCompressedNull();
    }

    final void setStreamSetterArgs(StreamSetterArgs streamSetterArgs) {
        this.impl.setStreamSetterArgs(streamSetterArgs);
    }

    final void setCalendar(Calendar calendar) {
        this.impl.setCalendar(calendar);
    }

    final void setScale(Integer scale) {
        this.impl.setScale(scale);
    }

    final void setForceEncrypt(boolean forceEncrypt) {
        this.impl.setForceEncrypt(forceEncrypt);
    }

    StreamSetterArgs getStreamSetterArgs() {
        return this.impl.getStreamSetterArgs();
    }

    Calendar getCalendar() {
        return this.impl.getCalendar();
    }

    Integer getScale() {
        return this.impl.getScale();
    }

    boolean isNull() {
        return null == this.impl || this.impl.isNull();
    }

    final boolean isInitialized() {
        return null != this.impl;
    }

    final void setJdbcType(JDBCType jdbcType) {
        if (null == this.impl) {
            this.impl = new AppDTVImpl();
        }
        this.impl.setJdbcType(jdbcType);
    }

    final JDBCType getJdbcType() {
        assert (null != this.impl);
        return this.impl.getJdbcType();
    }

    final JavaType getJavaType() {
        assert (null != this.impl);
        return this.impl.getJavaType();
    }

    Object getValue(JDBCType jdbcType, int scale, InputStreamGetterArgs streamGetterArgs, Calendar cal, TypeInfo typeInfo, CryptoMetadata cryptoMetadata, TDSReader tdsReader, SQLServerStatement statement) throws SQLServerException {
        if (null == this.impl) {
            this.impl = new ServerDTVImpl();
        }
        return this.impl.getValue(this, jdbcType, scale, streamGetterArgs, cal, typeInfo, cryptoMetadata, tdsReader, statement);
    }

    Object getSetterValue() {
        return this.impl.getSetterValue();
    }

    SqlVariant getInternalVariant() {
        return this.impl.getInternalVariant();
    }

    void setImpl(DTVImpl impl) {
        this.impl = impl;
    }

    final void executeOp(DTVExecuteOp op) throws SQLServerException {
        JDBCType jdbcType = this.getJdbcType();
        Object value = this.getSetterValue();
        JavaType javaType = this.getJavaType();
        boolean unsupportedConversion = false;
        byte[] byteValue = null;
        if (null != this.cryptoMeta && !JavaType.SetterConversionAE.converts(javaType, jdbcType, this.sendStringParametersAsUnicode)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionAE"));
            Object[] msgArgs = new Object[]{javaType.toString().toLowerCase(Locale.ENGLISH), jdbcType.toString().toLowerCase(Locale.ENGLISH)};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        if (null == value) {
            switch (jdbcType) {
                case NCHAR: 
                case NVARCHAR: 
                case LONGNVARCHAR: 
                case NCLOB: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (String)null);
                    break;
                }
                case INTEGER: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Integer)null);
                    break;
                }
                case DATE: {
                    op.execute(this, (Date)null);
                    break;
                }
                case TIME: {
                    op.execute(this, (Time)null);
                    break;
                }
                case DATETIME: 
                case SMALLDATETIME: 
                case TIMESTAMP: {
                    op.execute(this, (Timestamp)null);
                    break;
                }
                case TIMESTAMP_WITH_TIMEZONE: 
                case DATETIMEOFFSET: 
                case TIME_WITH_TIMEZONE: {
                    op.execute(this, (DateTimeOffset)null);
                    break;
                }
                case FLOAT: 
                case REAL: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Float)null);
                    break;
                }
                case NUMERIC: 
                case DECIMAL: 
                case MONEY: 
                case SMALLMONEY: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (BigDecimal)null);
                    break;
                }
                case BINARY: 
                case VARBINARY: 
                case LONGVARBINARY: 
                case BLOB: 
                case CHAR: 
                case VARCHAR: 
                case LONGVARCHAR: 
                case CLOB: 
                case GUID: {
                    op.execute(this, (byte[])null);
                    break;
                }
                case TINYINT: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Byte)null);
                    break;
                }
                case BIGINT: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Long)null);
                    break;
                }
                case DOUBLE: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Double)null);
                    break;
                }
                case SMALLINT: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Short)null);
                    break;
                }
                case BIT: 
                case BOOLEAN: {
                    if (null != this.cryptoMeta) {
                        op.execute(this, (byte[])null);
                        break;
                    }
                    op.execute(this, (Boolean)null);
                    break;
                }
                case SQLXML: {
                    op.execute(this, (SQLServerSQLXML)null);
                    break;
                }
                case ARRAY: 
                case DATALINK: 
                case DISTINCT: 
                case JAVA_OBJECT: 
                case NULL: 
                case OTHER: 
                case REF: 
                case ROWID: 
                case STRUCT: {
                    unsupportedConversion = true;
                    break;
                }
                case SQL_VARIANT: {
                    op.execute(this, (SqlVariant)null);
                    break;
                }
                default: {
                    assert (false) : "Unexpected JDBCType: " + jdbcType;
                    unsupportedConversion = true;
                    break;
                }
            }
        } else {
            if (aeLogger.isLoggable(Level.FINE) && null != this.cryptoMeta) {
                aeLogger.fine("Encrypting java data type: " + javaType);
            }
            switch (javaType) {
                case STRING: {
                    if (JDBCType.GUID == jdbcType) {
                        if (null != this.cryptoMeta) {
                            if (value instanceof String) {
                                value = UUID.fromString((String)value);
                            }
                            byte[] bArray = Util.asGuidByteArray((UUID)value);
                            op.execute(this, bArray);
                            break;
                        }
                        op.execute(this, String.valueOf(value));
                        break;
                    }
                    if (JDBCType.SQL_VARIANT == jdbcType) {
                        op.execute(this, String.valueOf(value));
                        break;
                    }
                    if (JDBCType.GEOMETRY == jdbcType) {
                        op.execute(this, ((Geometry)value).serialize());
                        break;
                    }
                    if (JDBCType.GEOGRAPHY == jdbcType) {
                        op.execute(this, ((Geography)value).serialize());
                        break;
                    }
                    if (null != this.cryptoMeta) {
                        if (jdbcType == JDBCType.LONGNVARCHAR && JDBCType.VARCHAR == this.jdbcTypeSetByUser && Integer.MAX_VALUE < this.valueLength) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_StreamingDataTypeAE"));
                            Object[] msgArgs = new Object[]{Integer.MAX_VALUE, JDBCType.LONGVARCHAR};
                            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                        }
                        if (JDBCType.NVARCHAR == this.jdbcTypeSetByUser && 0x3FFFFFFF < this.valueLength) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_StreamingDataTypeAE"));
                            Object[] msgArgs = new Object[]{0x3FFFFFFF, JDBCType.LONGNVARCHAR};
                            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                        }
                        if (JDBCType.NVARCHAR == this.jdbcTypeSetByUser || JDBCType.NCHAR == this.jdbcTypeSetByUser || JDBCType.LONGNVARCHAR == this.jdbcTypeSetByUser) {
                            byteValue = ((String)value).getBytes(StandardCharsets.UTF_16LE);
                        } else if (JDBCType.VARCHAR == this.jdbcTypeSetByUser || JDBCType.CHAR == this.jdbcTypeSetByUser || JDBCType.LONGVARCHAR == this.jdbcTypeSetByUser) {
                            byteValue = ((String)value).getBytes();
                        }
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (String)value);
                    break;
                }
                case INTEGER: {
                    if (null != this.cryptoMeta) {
                        byteValue = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(((Integer)value).longValue()).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Integer)value);
                    break;
                }
                case DATE: {
                    op.execute(this, (Date)value);
                    break;
                }
                case TIME: {
                    op.execute(this, (Time)value);
                    break;
                }
                case TIMESTAMP: {
                    op.execute(this, (Timestamp)value);
                    break;
                }
                case TVP: {
                    op.execute(this, (TVP)value);
                    break;
                }
                case UTILDATE: {
                    op.execute(this, (java.util.Date)value);
                    break;
                }
                case CALENDAR: {
                    op.execute(this, (Calendar)value);
                    break;
                }
                case LOCALDATE: {
                    op.execute(this, (LocalDate)value);
                    break;
                }
                case LOCALTIME: {
                    op.execute(this, (LocalTime)value);
                    break;
                }
                case LOCALDATETIME: {
                    op.execute(this, (LocalDateTime)value);
                    break;
                }
                case OFFSETTIME: {
                    op.execute(this, (OffsetTime)value);
                    break;
                }
                case OFFSETDATETIME: {
                    op.execute(this, (OffsetDateTime)value);
                    break;
                }
                case DATETIMEOFFSET: {
                    op.execute(this, (DateTimeOffset)value);
                    break;
                }
                case GEOMETRY: {
                    op.execute(this, ((Geometry)value).serialize());
                    break;
                }
                case GEOGRAPHY: {
                    op.execute(this, ((Geography)value).serialize());
                    break;
                }
                case FLOAT: {
                    if (null != this.cryptoMeta) {
                        if (Float.isInfinite(((Float)value).floatValue())) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                            throw new SQLServerException(form.format(new Object[]{jdbcType}), null, 0, null);
                        }
                        byteValue = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(((Float)value).floatValue()).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Float)value);
                    break;
                }
                case BIGDECIMAL: {
                    if (null != this.cryptoMeta) {
                        if (JDBCType.MONEY == jdbcType || JDBCType.SMALLMONEY == jdbcType) {
                            BigDecimal bdValue = (BigDecimal)value;
                            Util.validateMoneyRange(bdValue, jdbcType);
                            int digitCount = Math.max(bdValue.precision() - bdValue.scale(), 0) + 4;
                            long moneyVal = ((BigDecimal)value).multiply(new BigDecimal(10000), new MathContext(digitCount, RoundingMode.HALF_UP)).longValue();
                            ByteBuffer bbuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
                            bbuf.putInt((int)(moneyVal >> 32)).array();
                            bbuf.putInt((int)moneyVal).array();
                            op.execute(this, bbuf.array());
                            break;
                        }
                        BigDecimal bigDecimalVal = (BigDecimal)value;
                        byte[] decimalToByte = DDC.convertBigDecimalToBytes(bigDecimalVal, bigDecimalVal.scale());
                        byteValue = new byte[16];
                        System.arraycopy(decimalToByte, 2, byteValue, 0, decimalToByte.length - 2);
                        this.setScale(bigDecimalVal.scale());
                        if (null != this.cryptoMeta.getBaseTypeInfo()) {
                            if (this.cryptoMeta.getBaseTypeInfo().getPrecision() < Util.getValueLengthBaseOnJavaType(bigDecimalVal, javaType, null, null, jdbcType)) {
                                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                                Object[] msgArgs = new Object[]{this.cryptoMeta.getBaseTypeInfo().getSSTypeName()};
                                throw new SQLServerException(form.format(msgArgs), SQLState.NUMERIC_DATA_OUT_OF_RANGE, DriverError.NOT_SET, null);
                            }
                        } else if (this.valueLength < Util.getValueLengthBaseOnJavaType(bigDecimalVal, javaType, null, null, jdbcType)) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                            Object[] msgArgs = new Object[]{SSType.DECIMAL};
                            throw new SQLServerException(form.format(msgArgs), SQLState.NUMERIC_DATA_OUT_OF_RANGE, DriverError.NOT_SET, null);
                        }
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (BigDecimal)value);
                    break;
                }
                case BYTEARRAY: {
                    if (null != this.cryptoMeta && Integer.MAX_VALUE < this.valueLength) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_StreamingDataTypeAE"));
                        Object[] msgArgs = new Object[]{Integer.MAX_VALUE, JDBCType.BINARY};
                        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                    }
                    op.execute(this, (byte[])value);
                    break;
                }
                case BYTE: {
                    if (null != this.cryptoMeta) {
                        byteValue = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong((long)((Byte)value & 0xFF)).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Byte)value);
                    break;
                }
                case LONG: {
                    if (null != this.cryptoMeta) {
                        byteValue = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong((Long)value).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Long)value);
                    break;
                }
                case BIGINTEGER: {
                    op.execute(this, (BigInteger)value);
                    break;
                }
                case DOUBLE: {
                    if (null != this.cryptoMeta) {
                        if (Double.isInfinite((Double)value)) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                            throw new SQLServerException(form.format(new Object[]{jdbcType}), null, 0, null);
                        }
                        byteValue = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble((Double)value).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Double)value);
                    break;
                }
                case SHORT: {
                    if (null != this.cryptoMeta) {
                        byteValue = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(((Short)value).shortValue()).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Short)value);
                    break;
                }
                case BOOLEAN: {
                    if (null != this.cryptoMeta) {
                        byteValue = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong((Boolean)value != false ? 1L : 0L).array();
                        op.execute(this, byteValue);
                        break;
                    }
                    op.execute(this, (Boolean)value);
                    break;
                }
                case BLOB: {
                    op.execute(this, (Blob)value);
                    break;
                }
                case CLOB: 
                case NCLOB: {
                    op.execute(this, (Clob)value);
                    break;
                }
                case INPUTSTREAM: {
                    op.execute(this, (InputStream)value);
                    break;
                }
                case READER: {
                    op.execute(this, (Reader)value);
                    break;
                }
                case SQLXML: {
                    op.execute(this, (SQLServerSQLXML)value);
                    break;
                }
                default: {
                    assert (false) : "Unexpected JavaType: " + javaType;
                    unsupportedConversion = true;
                }
            }
        }
        if (unsupportedConversion) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
            Object[] msgArgs = new Object[]{javaType, jdbcType};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
    }

    void sendCryptoMetaData(CryptoMetadata cryptoMeta, TDSWriter tdsWriter) {
        this.cryptoMeta = cryptoMeta;
        tdsWriter.setCryptoMetaData(cryptoMeta);
    }

    void setJdbcTypeSetByUser(JDBCType jdbcTypeSetByUser, int valueLength) {
        this.jdbcTypeSetByUser = jdbcTypeSetByUser;
        this.valueLength = valueLength;
    }

    void sendByRPC(String name, TypeInfo typeInfo, SQLCollation collation, int precision, int outScale, boolean isOutParam, TDSWriter tdsWriter, SQLServerStatement statement) throws SQLServerException {
        this.executeOp(new SendByRPCOp(name, typeInfo, collation, precision, outScale, isOutParam, tdsWriter, statement));
    }

    final class SendByRPCOp
    extends DTVExecuteOp {
        private final String name;
        private final TypeInfo typeInfo;
        private final SQLCollation collation;
        private final int precision;
        private final int outScale;
        private final boolean isOutParam;
        private final TDSWriter tdsWriter;
        private final SQLServerConnection conn;
        private final SQLServerStatement statement;

        SendByRPCOp(String name, TypeInfo typeInfo, SQLCollation collation, int precision, int outScale, boolean isOutParam, TDSWriter tdsWriter, SQLServerStatement statement) {
            this.name = name;
            this.typeInfo = typeInfo;
            this.collation = collation;
            this.precision = precision;
            this.outScale = outScale;
            this.isOutParam = isOutParam;
            this.tdsWriter = tdsWriter;
            this.conn = statement.connection;
            this.statement = statement;
        }

        @Override
        void execute(DTV dtv, String strValue) throws SQLServerException {
            this.tdsWriter.writeRPCStringUnicode(this.name, strValue, this.isOutParam, this.collation);
        }

        @Override
        void execute(DTV dtv, Clob clobValue) throws SQLServerException {
            assert (null != clobValue);
            long clobLength = 0L;
            Reader clobReader = null;
            try {
                clobLength = DataTypes.getCheckedLength(this.conn, dtv.getJdbcType(), clobValue.length(), false);
                clobReader = clobValue.getCharacterStream();
            }
            catch (SQLException e) {
                SQLServerException.makeFromDriverError(this.conn, null, e.getMessage(), null, false);
            }
            JDBCType jdbcType = dtv.getJdbcType();
            if (null != this.collation && (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.LONGVARCHAR == jdbcType || JDBCType.CLOB == jdbcType)) {
                if (null == clobReader) {
                    this.tdsWriter.writeRPCByteArray(this.name, null, this.isOutParam, jdbcType, this.collation);
                } else {
                    ReaderInputStream clobStream = new ReaderInputStream(clobReader, this.collation.getCharset(), clobLength);
                    this.tdsWriter.writeRPCInputStream(this.name, clobStream, -1L, this.isOutParam, jdbcType, this.collation);
                }
            } else if (null == clobReader) {
                this.tdsWriter.writeRPCStringUnicode(this.name, null, this.isOutParam, this.collation);
            } else {
                this.tdsWriter.writeRPCReaderUnicode(this.name, clobReader, clobLength, this.isOutParam, this.collation);
            }
        }

        @Override
        void execute(DTV dtv, Byte byteValue) throws SQLServerException {
            this.tdsWriter.writeRPCByte(this.name, byteValue, this.isOutParam);
        }

        @Override
        void execute(DTV dtv, Integer intValue) throws SQLServerException {
            this.tdsWriter.writeRPCInt(this.name, intValue, this.isOutParam);
        }

        @Override
        void execute(DTV dtv, Time timeValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.TIME, timeValue);
        }

        @Override
        void execute(DTV dtv, Date dateValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.DATE, dateValue);
        }

        @Override
        void execute(DTV dtv, Timestamp timestampValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.TIMESTAMP, timestampValue);
        }

        @Override
        void execute(DTV dtv, java.util.Date utilDateValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.UTILDATE, utilDateValue);
        }

        @Override
        void execute(DTV dtv, Calendar calendarValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.CALENDAR, calendarValue);
        }

        @Override
        void execute(DTV dtv, LocalDate localDateValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.LOCALDATE, localDateValue);
        }

        @Override
        void execute(DTV dtv, LocalTime localTimeValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.LOCALTIME, localTimeValue);
        }

        @Override
        void execute(DTV dtv, LocalDateTime localDateTimeValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.LOCALDATETIME, localDateTimeValue);
        }

        @Override
        void execute(DTV dtv, OffsetTime offsetTimeValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.OFFSETTIME, offsetTimeValue);
        }

        @Override
        void execute(DTV dtv, OffsetDateTime offsetDateTimeValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.OFFSETDATETIME, offsetDateTimeValue);
        }

        @Override
        void execute(DTV dtv, DateTimeOffset dtoValue) throws SQLServerException {
            this.sendTemporal(dtv, JavaType.DATETIMEOFFSET, dtoValue);
        }

        @Override
        void execute(DTV dtv, TVP tvpValue) throws SQLServerException {
            this.tdsWriter.writeTVP(tvpValue);
        }

        private void clearSetCalendar(Calendar cal, boolean lenient, Integer year, Integer month, Integer dayOfMonth, Integer hourOfDay, Integer minute, Integer second) {
            cal.clear();
            cal.setLenient(lenient);
            if (null != year) {
                cal.set(1, year);
            }
            if (null != month) {
                cal.set(2, month);
            }
            if (null != dayOfMonth) {
                cal.set(5, dayOfMonth);
            }
            if (null != hourOfDay) {
                cal.set(11, hourOfDay);
            }
            if (null != minute) {
                cal.set(12, minute);
            }
            if (null != second) {
                cal.set(13, second);
            }
        }

        private void sendTemporal(DTV dtv, JavaType javaType, Object value) throws SQLServerException {
            block74: {
                int subSecondNanos;
                GregorianCalendar calendar;
                JDBCType jdbcType;
                block75: {
                    int minutesOffset;
                    block73: {
                        jdbcType = dtv.getJdbcType();
                        calendar = null;
                        subSecondNanos = 0;
                        minutesOffset = 0;
                        if (null != value) {
                            TimeZone timeZone = TimeZone.getDefault();
                            long utcMillis = 0L;
                            switch (javaType) {
                                case TIME: {
                                    timeZone = null != dtv.getCalendar() ? dtv.getCalendar().getTimeZone() : TimeZone.getDefault();
                                    utcMillis = ((Time)value).getTime();
                                    subSecondNanos = 1000000 * (int)(utcMillis % 1000L);
                                    if (subSecondNanos >= 0) break;
                                    subSecondNanos += 1000000000;
                                    break;
                                }
                                case DATE: {
                                    timeZone = null != dtv.getCalendar() ? dtv.getCalendar().getTimeZone() : TimeZone.getDefault();
                                    utcMillis = ((Date)value).getTime();
                                    break;
                                }
                                case TIMESTAMP: {
                                    timeZone = null != dtv.getCalendar() ? dtv.getCalendar().getTimeZone() : TimeZone.getDefault();
                                    Timestamp timestampValue = (Timestamp)value;
                                    utcMillis = timestampValue.getTime();
                                    subSecondNanos = timestampValue.getNanos();
                                    break;
                                }
                                case UTILDATE: {
                                    timeZone = null != dtv.getCalendar() ? dtv.getCalendar().getTimeZone() : TimeZone.getDefault();
                                    utcMillis = ((java.util.Date)value).getTime();
                                    subSecondNanos = 1000000 * (int)(utcMillis % 1000L);
                                    if (subSecondNanos >= 0) break;
                                    subSecondNanos += 1000000000;
                                    break;
                                }
                                case CALENDAR: {
                                    timeZone = null != dtv.getCalendar() ? dtv.getCalendar().getTimeZone() : TimeZone.getDefault();
                                    utcMillis = ((Calendar)value).getTimeInMillis();
                                    subSecondNanos = 1000000 * (int)(utcMillis % 1000L);
                                    if (subSecondNanos >= 0) break;
                                    subSecondNanos += 1000000000;
                                    break;
                                }
                                case LOCALDATE: {
                                    calendar = new GregorianCalendar(UTC.timeZone, Locale.US);
                                    this.clearSetCalendar(calendar, true, ((LocalDate)value).getYear(), ((LocalDate)value).getMonthValue() - 1, ((LocalDate)value).getDayOfMonth(), null, null, null);
                                    break;
                                }
                                case LOCALTIME: {
                                    calendar = new GregorianCalendar(UTC.timeZone, Locale.US);
                                    LocalTime localTimeValue = (LocalTime)value;
                                    this.clearSetCalendar(calendar, true, this.conn.baseYear(), 1, 1, localTimeValue.getHour(), localTimeValue.getMinute(), localTimeValue.getSecond());
                                    subSecondNanos = localTimeValue.getNano();
                                    break;
                                }
                                case LOCALDATETIME: {
                                    calendar = new GregorianCalendar(UTC.timeZone, Locale.US);
                                    LocalDateTime localDateTimeValue = (LocalDateTime)value;
                                    this.clearSetCalendar(calendar, true, localDateTimeValue.getYear(), localDateTimeValue.getMonthValue() - 1, localDateTimeValue.getDayOfMonth(), localDateTimeValue.getHour(), localDateTimeValue.getMinute(), localDateTimeValue.getSecond());
                                    subSecondNanos = localDateTimeValue.getNano();
                                    break;
                                }
                                case OFFSETTIME: {
                                    OffsetTime offsetTimeValue = (OffsetTime)value;
                                    try {
                                        minutesOffset = offsetTimeValue.getOffset().getTotalSeconds() / 60;
                                    }
                                    catch (Exception e) {
                                        throw new SQLServerException(SQLServerException.getErrString("R_zoneOffsetError"), null, 0, (Throwable)e);
                                    }
                                    subSecondNanos = offsetTimeValue.getNano();
                                    timeZone = JDBCType.TIME_WITH_TIMEZONE == jdbcType && (null == this.typeInfo || SSType.DATETIMEOFFSET == this.typeInfo.getSSType()) ? UTC.timeZone : new SimpleTimeZone(minutesOffset * 60 * 1000, "");
                                    LocalDate baseDate = LocalDate.of(this.conn.baseYear(), 1, 1);
                                    utcMillis = offsetTimeValue.atDate(baseDate).toEpochSecond() * 1000L;
                                    break;
                                }
                                case OFFSETDATETIME: {
                                    OffsetDateTime offsetDateTimeValue = (OffsetDateTime)value;
                                    try {
                                        minutesOffset = offsetDateTimeValue.getOffset().getTotalSeconds() / 60;
                                    }
                                    catch (Exception e) {
                                        throw new SQLServerException(SQLServerException.getErrString("R_zoneOffsetError"), null, 0, (Throwable)e);
                                    }
                                    subSecondNanos = offsetDateTimeValue.getNano();
                                    timeZone = !(JDBCType.TIMESTAMP_WITH_TIMEZONE != jdbcType && JDBCType.TIME_WITH_TIMEZONE != jdbcType || null != this.typeInfo && SSType.DATETIMEOFFSET != this.typeInfo.getSSType()) ? UTC.timeZone : new SimpleTimeZone(minutesOffset * 60 * 1000, "");
                                    utcMillis = offsetDateTimeValue.toEpochSecond() * 1000L;
                                    break;
                                }
                                case DATETIMEOFFSET: {
                                    DateTimeOffset dtoValue = (DateTimeOffset)value;
                                    utcMillis = dtoValue.getTimestamp().getTime();
                                    subSecondNanos = dtoValue.getTimestamp().getNanos();
                                    minutesOffset = dtoValue.getMinutesOffset();
                                    assert (null == dtv.getCalendar());
                                    timeZone = JDBCType.DATETIMEOFFSET == jdbcType && (null == this.typeInfo || SSType.DATETIMEOFFSET == this.typeInfo.getSSType() || SSType.VARBINARY == this.typeInfo.getSSType() || SSType.VARBINARYMAX == this.typeInfo.getSSType()) ? UTC.timeZone : new SimpleTimeZone(minutesOffset * 60 * 1000, "");
                                    break;
                                }
                                default: {
                                    throw new AssertionError((Object)("Unexpected JavaType: " + javaType));
                                }
                            }
                            if (null == calendar) {
                                calendar = new GregorianCalendar(timeZone, Locale.US);
                                calendar.setLenient(true);
                                calendar.clear();
                                calendar.setTimeInMillis(utcMillis);
                            }
                        }
                        if (null == this.typeInfo) break block73;
                        switch (this.typeInfo.getSSType()) {
                            case DATETIME: 
                            case DATETIME2: {
                                int scale = this.typeInfo.getSSType() == SSType.DATETIME ? this.typeInfo.getScale() + 4 : this.typeInfo.getScale();
                                this.tdsWriter.writeRPCDateTime2(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, scale, this.isOutParam);
                                break block74;
                            }
                            case DATE: {
                                this.tdsWriter.writeRPCDate(this.name, calendar, this.isOutParam);
                                break block74;
                            }
                            case TIME: {
                                this.tdsWriter.writeRPCTime(this.name, calendar, subSecondNanos, this.typeInfo.getScale(), this.isOutParam);
                                break block74;
                            }
                            case DATETIMEOFFSET: {
                                if (JavaType.DATETIMEOFFSET != javaType) {
                                    calendar = this.timestampNormalizedCalendar(this.localCalendarAsUTC(calendar), javaType, this.conn.baseYear());
                                    minutesOffset = 0;
                                }
                                this.tdsWriter.writeRPCDateTimeOffset(this.name, calendar, minutesOffset, subSecondNanos, this.typeInfo.getScale(), this.isOutParam);
                                break block74;
                            }
                            case SMALLDATETIME: {
                                this.tdsWriter.writeRPCDateTime(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, this.isOutParam);
                                break block74;
                            }
                            case VARBINARY: 
                            case VARBINARYMAX: {
                                switch (jdbcType) {
                                    case DATETIME: 
                                    case SMALLDATETIME: {
                                        this.tdsWriter.writeEncryptedRPCDateTime(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, this.isOutParam, jdbcType, this.statement);
                                        break block74;
                                    }
                                    case TIMESTAMP: {
                                        assert (null != DTV.this.cryptoMeta);
                                        this.tdsWriter.writeEncryptedRPCDateTime2(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, DTV.this.valueLength, this.isOutParam, this.statement);
                                        break block74;
                                    }
                                    case TIME: {
                                        assert (null != DTV.this.cryptoMeta);
                                        this.tdsWriter.writeEncryptedRPCTime(this.name, calendar, subSecondNanos, DTV.this.valueLength, this.isOutParam, this.statement);
                                        break block74;
                                    }
                                    case DATE: {
                                        assert (null != DTV.this.cryptoMeta);
                                        this.tdsWriter.writeEncryptedRPCDate(this.name, calendar, this.isOutParam, this.statement);
                                        break block74;
                                    }
                                    case TIMESTAMP_WITH_TIMEZONE: 
                                    case DATETIMEOFFSET: {
                                        if (JavaType.DATETIMEOFFSET != javaType && JavaType.OFFSETDATETIME != javaType) {
                                            calendar = this.timestampNormalizedCalendar(this.localCalendarAsUTC(calendar), javaType, this.conn.baseYear());
                                            minutesOffset = 0;
                                        }
                                        assert (null != DTV.this.cryptoMeta);
                                        this.tdsWriter.writeEncryptedRPCDateTimeOffset(this.name, calendar, minutesOffset, subSecondNanos, DTV.this.valueLength, this.isOutParam, this.statement);
                                        break block74;
                                    }
                                    default: {
                                        assert (false) : "Unexpected JDBCType: " + jdbcType;
                                        break block74;
                                    }
                                }
                            }
                            default: {
                                assert (false) : "Unexpected SSType: " + this.typeInfo.getSSType();
                                break block74;
                            }
                        }
                    }
                    if (!this.conn.isKatmaiOrLater()) break block75;
                    if (aeLogger.isLoggable(Level.FINE) && null != DTV.this.cryptoMeta) {
                        aeLogger.fine("Encrypting temporal data type.");
                    }
                    switch (jdbcType) {
                        case DATETIME: 
                        case SMALLDATETIME: 
                        case TIMESTAMP: {
                            if (null != DTV.this.cryptoMeta) {
                                if (JDBCType.DATETIME == jdbcType || JDBCType.SMALLDATETIME == jdbcType) {
                                    this.tdsWriter.writeEncryptedRPCDateTime(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, this.isOutParam, jdbcType, this.statement);
                                } else if (0 == DTV.this.valueLength) {
                                    this.tdsWriter.writeEncryptedRPCDateTime2(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, this.outScale, this.isOutParam, this.statement);
                                } else {
                                    this.tdsWriter.writeEncryptedRPCDateTime2(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, DTV.this.valueLength, this.isOutParam, this.statement);
                                }
                            } else {
                                this.tdsWriter.writeRPCDateTime2(this.name, this.timestampNormalizedCalendar(calendar, javaType, this.conn.baseYear()), subSecondNanos, 7, this.isOutParam);
                            }
                            break block74;
                        }
                        case TIME: {
                            if (null != DTV.this.cryptoMeta) {
                                if (0 == DTV.this.valueLength) {
                                    this.tdsWriter.writeEncryptedRPCTime(this.name, calendar, subSecondNanos, this.outScale, this.isOutParam, this.statement);
                                } else {
                                    this.tdsWriter.writeEncryptedRPCTime(this.name, calendar, subSecondNanos, DTV.this.valueLength, this.isOutParam, this.statement);
                                }
                            } else if (this.conn.getSendTimeAsDatetime()) {
                                this.tdsWriter.writeRPCDateTime(this.name, this.timestampNormalizedCalendar(calendar, JavaType.TIME, 1970), subSecondNanos, this.isOutParam);
                            } else {
                                this.tdsWriter.writeRPCTime(this.name, calendar, subSecondNanos, 7, this.isOutParam);
                            }
                            break block74;
                        }
                        case DATE: {
                            if (null != DTV.this.cryptoMeta) {
                                this.tdsWriter.writeEncryptedRPCDate(this.name, calendar, this.isOutParam, this.statement);
                            } else {
                                this.tdsWriter.writeRPCDate(this.name, calendar, this.isOutParam);
                            }
                            break block74;
                        }
                        case TIME_WITH_TIMEZONE: {
                            if (JavaType.OFFSETDATETIME != javaType && JavaType.OFFSETTIME != javaType) {
                                calendar = this.timestampNormalizedCalendar(this.localCalendarAsUTC(calendar), javaType, this.conn.baseYear());
                                minutesOffset = 0;
                            }
                            this.tdsWriter.writeRPCDateTimeOffset(this.name, calendar, minutesOffset, subSecondNanos, 7, this.isOutParam);
                            break block74;
                        }
                        case TIMESTAMP_WITH_TIMEZONE: 
                        case DATETIMEOFFSET: {
                            if (JavaType.DATETIMEOFFSET != javaType && JavaType.OFFSETDATETIME != javaType) {
                                calendar = this.timestampNormalizedCalendar(this.localCalendarAsUTC(calendar), javaType, this.conn.baseYear());
                                minutesOffset = 0;
                            }
                            if (null != DTV.this.cryptoMeta) {
                                if (0 == DTV.this.valueLength) {
                                    this.tdsWriter.writeEncryptedRPCDateTimeOffset(this.name, calendar, minutesOffset, subSecondNanos, this.outScale, this.isOutParam, this.statement);
                                } else {
                                    this.tdsWriter.writeEncryptedRPCDateTimeOffset(this.name, calendar, minutesOffset, subSecondNanos, 0 == DTV.this.valueLength ? 7 : DTV.this.valueLength, this.isOutParam, this.statement);
                                }
                            } else {
                                this.tdsWriter.writeRPCDateTimeOffset(this.name, calendar, minutesOffset, subSecondNanos, 7, this.isOutParam);
                            }
                            break block74;
                        }
                        default: {
                            assert (false) : "Unexpected JDBCType: " + jdbcType;
                            break block74;
                        }
                    }
                }
                assert (JDBCType.TIME == jdbcType || JDBCType.DATE == jdbcType || JDBCType.TIMESTAMP == jdbcType) : "Unexpected JDBCType: " + jdbcType;
                this.tdsWriter.writeRPCDateTime(this.name, this.timestampNormalizedCalendar(calendar, javaType, 1970), subSecondNanos, this.isOutParam);
            }
        }

        private GregorianCalendar timestampNormalizedCalendar(GregorianCalendar calendar, JavaType javaType, int baseYear) {
            if (null != calendar) {
                switch (javaType) {
                    case DATE: 
                    case LOCALDATE: {
                        calendar.set(11, 0);
                        calendar.set(12, 0);
                        calendar.set(13, 0);
                        calendar.set(14, 0);
                        break;
                    }
                    case TIME: 
                    case LOCALTIME: 
                    case OFFSETTIME: {
                        assert (1970 == baseYear || 1900 == baseYear);
                        calendar.set(baseYear, 0, 1);
                        break;
                    }
                }
            }
            return calendar;
        }

        private GregorianCalendar localCalendarAsUTC(GregorianCalendar cal) {
            if (null == cal) {
                return null;
            }
            int year = cal.get(1);
            int month = cal.get(2);
            int date = cal.get(5);
            int hour = cal.get(11);
            int minute = cal.get(12);
            int second = cal.get(13);
            int millis = cal.get(14);
            cal.setTimeZone(UTC.timeZone);
            cal.set(year, month, date, hour, minute, second);
            cal.set(14, millis);
            return cal;
        }

        @Override
        void execute(DTV dtv, Float floatValue) throws SQLServerException {
            if (JDBCType.REAL == dtv.getJdbcType()) {
                this.tdsWriter.writeRPCReal(this.name, floatValue, this.isOutParam);
            } else {
                Double doubleValue = null == floatValue ? null : Double.valueOf(floatValue.floatValue());
                this.tdsWriter.writeRPCDouble(this.name, doubleValue, this.isOutParam);
            }
        }

        @Override
        void execute(DTV dtv, Double doubleValue) throws SQLServerException {
            this.tdsWriter.writeRPCDouble(this.name, doubleValue, this.isOutParam);
        }

        @Override
        void execute(DTV dtv, BigDecimal bigDecimalValue) throws SQLServerException {
            if (DDC.exceedsMaxRPCDecimalPrecisionOrScale(bigDecimalValue)) {
                if (JDBCType.DECIMAL == dtv.getJdbcType() || JDBCType.NUMERIC == dtv.getJdbcType()) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRangeSQLType"));
                    Object[] msgArgs = new Object[]{dtv.getJdbcType()};
                    throw new SQLServerException(form.format(msgArgs), SQLState.NUMERIC_DATA_OUT_OF_RANGE, DriverError.NOT_SET, null);
                }
                String strValue = bigDecimalValue.toString();
                this.tdsWriter.writeRPCStringUnicode(this.name, strValue, this.isOutParam, this.collation);
            } else {
                this.tdsWriter.writeRPCBigDecimal(this.name, bigDecimalValue, this.outScale, this.isOutParam);
            }
        }

        @Override
        void execute(DTV dtv, Long longValue) throws SQLServerException {
            this.tdsWriter.writeRPCLong(this.name, longValue, this.isOutParam);
        }

        @Override
        void execute(DTV dtv, BigInteger bigIntegerValue) throws SQLServerException {
            this.tdsWriter.writeRPCLong(this.name, bigIntegerValue.longValue(), this.isOutParam);
        }

        @Override
        void execute(DTV dtv, Short shortValue) throws SQLServerException {
            this.tdsWriter.writeRPCShort(this.name, shortValue, this.isOutParam);
        }

        @Override
        void execute(DTV dtv, Boolean booleanValue) throws SQLServerException {
            this.tdsWriter.writeRPCBit(this.name, booleanValue, this.isOutParam);
        }

        @Override
        void execute(DTV dtv, byte[] byteArrayValue) throws SQLServerException {
            if (null != DTV.this.cryptoMeta) {
                this.tdsWriter.writeRPCNameValType(this.name, this.isOutParam, TDSType.BIGVARBINARY);
                if (null != byteArrayValue) {
                    byteArrayValue = SQLServerSecurityUtility.encryptWithKey(byteArrayValue, DTV.this.cryptoMeta, this.conn, this.statement);
                    this.tdsWriter.writeEncryptedRPCByteArray(byteArrayValue);
                    this.writeEncryptData(dtv, false);
                } else {
                    if ((JDBCType.LONGVARCHAR == DTV.this.jdbcTypeSetByUser || JDBCType.LONGNVARCHAR == DTV.this.jdbcTypeSetByUser || JDBCType.LONGVARBINARY == DTV.this.jdbcTypeSetByUser || 8000 == this.precision && JDBCType.VARCHAR == DTV.this.jdbcTypeSetByUser || 4000 == this.precision && JDBCType.NVARCHAR == DTV.this.jdbcTypeSetByUser || 8000 == this.precision && JDBCType.VARBINARY == DTV.this.jdbcTypeSetByUser) && null == dtv.getJavaType() && this.isOutParam) {
                        this.tdsWriter.writeEncryptedRPCPLP();
                    } else {
                        this.tdsWriter.writeEncryptedRPCByteArray(byteArrayValue);
                    }
                    this.writeEncryptData(dtv, true);
                }
            } else {
                this.tdsWriter.writeRPCByteArray(this.name, byteArrayValue, this.isOutParam, dtv.getJdbcType(), this.collation);
            }
        }

        void writeEncryptData(DTV dtv, boolean isNull) throws SQLServerException {
            JDBCType destType = null == DTV.this.jdbcTypeSetByUser ? dtv.getJdbcType() : DTV.this.jdbcTypeSetByUser;
            switch (destType.getIntValue()) {
                case 4: {
                    this.tdsWriter.writeByte(TDSType.INTN.byteValue());
                    this.tdsWriter.writeByte((byte)4);
                    break;
                }
                case -5: {
                    this.tdsWriter.writeByte(TDSType.INTN.byteValue());
                    this.tdsWriter.writeByte((byte)8);
                    break;
                }
                case -7: {
                    this.tdsWriter.writeByte(TDSType.BITN.byteValue());
                    this.tdsWriter.writeByte((byte)1);
                    break;
                }
                case 5: {
                    this.tdsWriter.writeByte(TDSType.INTN.byteValue());
                    this.tdsWriter.writeByte((byte)2);
                    break;
                }
                case -6: {
                    this.tdsWriter.writeByte(TDSType.INTN.byteValue());
                    this.tdsWriter.writeByte((byte)1);
                    break;
                }
                case 8: {
                    this.tdsWriter.writeByte(TDSType.FLOATN.byteValue());
                    this.tdsWriter.writeByte((byte)8);
                    break;
                }
                case 7: {
                    this.tdsWriter.writeByte(TDSType.FLOATN.byteValue());
                    this.tdsWriter.writeByte((byte)4);
                    break;
                }
                case -148: 
                case -146: 
                case 2: 
                case 3: {
                    if (JDBCType.MONEY == destType || JDBCType.SMALLMONEY == destType) {
                        this.tdsWriter.writeByte(TDSType.MONEYN.byteValue());
                        this.tdsWriter.writeByte((byte)(JDBCType.MONEY == destType ? 8 : 4));
                        break;
                    }
                    this.tdsWriter.writeByte(TDSType.NUMERICN.byteValue());
                    if (isNull) {
                        this.tdsWriter.writeByte((byte)17);
                        if (null != DTV.this.cryptoMeta && null != DTV.this.cryptoMeta.getBaseTypeInfo()) {
                            this.tdsWriter.writeByte((byte)(0 != DTV.this.valueLength ? DTV.this.valueLength : DTV.this.cryptoMeta.getBaseTypeInfo().getPrecision()));
                        } else {
                            this.tdsWriter.writeByte((byte)(0 != DTV.this.valueLength ? DTV.this.valueLength : 18));
                        }
                        this.tdsWriter.writeByte((byte)this.outScale);
                        break;
                    }
                    this.tdsWriter.writeByte((byte)17);
                    if (null != DTV.this.cryptoMeta && null != DTV.this.cryptoMeta.getBaseTypeInfo()) {
                        this.tdsWriter.writeByte((byte)DTV.this.cryptoMeta.getBaseTypeInfo().getPrecision());
                    } else {
                        this.tdsWriter.writeByte((byte)(0 != DTV.this.valueLength ? DTV.this.valueLength : 18));
                    }
                    if (null != DTV.this.cryptoMeta && null != DTV.this.cryptoMeta.getBaseTypeInfo()) {
                        this.tdsWriter.writeByte((byte)DTV.this.cryptoMeta.getBaseTypeInfo().getScale());
                        break;
                    }
                    this.tdsWriter.writeByte((byte)(null != dtv.getScale() ? dtv.getScale() : 0));
                    break;
                }
                case -145: {
                    this.tdsWriter.writeByte(TDSType.GUID.byteValue());
                    if (isNull) {
                        this.tdsWriter.writeByte((byte)(0 != DTV.this.valueLength ? DTV.this.valueLength : 1));
                        break;
                    }
                    this.tdsWriter.writeByte((byte)16);
                    break;
                }
                case 1: {
                    this.tdsWriter.writeByte(TDSType.BIGCHAR.byteValue());
                    if (isNull) {
                        this.tdsWriter.writeShort((short)(0 != DTV.this.valueLength ? DTV.this.valueLength : 1));
                    } else {
                        this.tdsWriter.writeShort((short)DTV.this.valueLength);
                    }
                    if (null != this.collation) {
                        this.collation.writeCollation(this.tdsWriter);
                        break;
                    }
                    this.conn.getDatabaseCollation().writeCollation(this.tdsWriter);
                    break;
                }
                case -15: {
                    this.tdsWriter.writeByte(TDSType.NCHAR.byteValue());
                    if (isNull) {
                        this.tdsWriter.writeShort((short)(0 != DTV.this.valueLength ? DTV.this.valueLength * 2 : 1));
                    } else if (this.isOutParam) {
                        this.tdsWriter.writeShort((short)(DTV.this.valueLength * 2));
                    } else if (DTV.this.valueLength > 8000) {
                        this.tdsWriter.writeShort((short)-1);
                    } else {
                        this.tdsWriter.writeShort((short)DTV.this.valueLength);
                    }
                    if (null != this.collation) {
                        this.collation.writeCollation(this.tdsWriter);
                        break;
                    }
                    this.conn.getDatabaseCollation().writeCollation(this.tdsWriter);
                    break;
                }
                case -1: 
                case 12: {
                    this.tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                    if (isNull) {
                        if (dtv.jdbcTypeSetByUser.getIntValue() == -1) {
                            this.tdsWriter.writeShort((short)-1);
                        } else {
                            this.tdsWriter.writeShort((short)(0 != DTV.this.valueLength ? DTV.this.valueLength : 1));
                        }
                    } else if (dtv.jdbcTypeSetByUser.getIntValue() == -1) {
                        this.tdsWriter.writeShort((short)-1);
                    } else if (dtv.getJdbcType().getIntValue() == -1 || dtv.getJdbcType().getIntValue() == -16) {
                        this.tdsWriter.writeShort((short)1);
                    } else if (DTV.this.valueLength > 8000) {
                        this.tdsWriter.writeShort((short)-1);
                    } else {
                        this.tdsWriter.writeShort((short)DTV.this.valueLength);
                    }
                    if (null != this.collation) {
                        this.collation.writeCollation(this.tdsWriter);
                        break;
                    }
                    this.conn.getDatabaseCollation().writeCollation(this.tdsWriter);
                    break;
                }
                case -16: 
                case -9: {
                    this.tdsWriter.writeByte(TDSType.NVARCHAR.byteValue());
                    if (isNull) {
                        if (dtv.jdbcTypeSetByUser.getIntValue() == -16) {
                            this.tdsWriter.writeShort((short)-1);
                        } else {
                            this.tdsWriter.writeShort((short)(0 != DTV.this.valueLength ? DTV.this.valueLength * 2 : 1));
                        }
                    } else if (this.isOutParam) {
                        if (dtv.jdbcTypeSetByUser.getIntValue() == -16) {
                            this.tdsWriter.writeShort((short)-1);
                        } else {
                            this.tdsWriter.writeShort((short)(DTV.this.valueLength * 2));
                        }
                    } else if (DTV.this.valueLength > 8000) {
                        this.tdsWriter.writeShort((short)-1);
                    } else {
                        this.tdsWriter.writeShort((short)DTV.this.valueLength);
                    }
                    if (null != this.collation) {
                        this.collation.writeCollation(this.tdsWriter);
                        break;
                    }
                    this.conn.getDatabaseCollation().writeCollation(this.tdsWriter);
                    break;
                }
                case -2: {
                    this.tdsWriter.writeByte(TDSType.BIGBINARY.byteValue());
                    if (isNull) {
                        this.tdsWriter.writeShort((short)(0 != DTV.this.valueLength ? DTV.this.valueLength : 1));
                        break;
                    }
                    this.tdsWriter.writeShort((short)DTV.this.valueLength);
                    break;
                }
                case -4: 
                case -3: {
                    this.tdsWriter.writeByte(TDSType.BIGVARBINARY.byteValue());
                    if (isNull) {
                        if (dtv.jdbcTypeSetByUser.getIntValue() == -4) {
                            this.tdsWriter.writeShort((short)-1);
                            break;
                        }
                        this.tdsWriter.writeShort((short)(0 != DTV.this.valueLength ? DTV.this.valueLength : 1));
                        break;
                    }
                    if (dtv.jdbcTypeSetByUser.getIntValue() == -4) {
                        this.tdsWriter.writeShort((short)-1);
                        break;
                    }
                    this.tdsWriter.writeShort((short)DTV.this.valueLength);
                    break;
                }
                default: {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
                    throw new SQLServerException(form.format(new Object[]{destType}), null, 0, null);
                }
            }
            this.tdsWriter.writeCryptoMetaData();
        }

        @Override
        void execute(DTV dtv, Blob blobValue) throws SQLServerException {
            assert (null != blobValue);
            long blobLength = 0L;
            InputStream blobStream = null;
            try {
                blobLength = DataTypes.getCheckedLength(this.conn, dtv.getJdbcType(), blobValue.length(), false);
                blobStream = blobValue.getBinaryStream();
            }
            catch (SQLException e) {
                SQLServerException.makeFromDriverError(this.conn, null, e.getMessage(), null, false);
            }
            if (null == blobStream) {
                this.tdsWriter.writeRPCByteArray(this.name, null, this.isOutParam, dtv.getJdbcType(), this.collation);
            } else {
                this.tdsWriter.writeRPCInputStream(this.name, blobStream, blobLength, this.isOutParam, dtv.getJdbcType(), this.collation);
            }
        }

        @Override
        void execute(DTV dtv, SQLServerSQLXML xmlValue) throws SQLServerException {
            InputStream o = null == xmlValue ? null : xmlValue.getValue();
            this.tdsWriter.writeRPCXML(this.name, o, null == o ? 0L : dtv.getStreamSetterArgs().getLength(), this.isOutParam);
        }

        @Override
        void execute(DTV dtv, InputStream inputStreamValue) throws SQLServerException {
            this.tdsWriter.writeRPCInputStream(this.name, inputStreamValue, null == inputStreamValue ? 0L : dtv.getStreamSetterArgs().getLength(), this.isOutParam, dtv.getJdbcType(), this.collation);
        }

        @Override
        void execute(DTV dtv, Reader readerValue) throws SQLServerException {
            JDBCType jdbcType = dtv.getJdbcType();
            assert (null != readerValue);
            assert (JDBCType.NCHAR == jdbcType || JDBCType.NVARCHAR == jdbcType || JDBCType.LONGNVARCHAR == jdbcType || JDBCType.NCLOB == jdbcType) : "SendByRPCOp(Reader): Unexpected JDBC type " + jdbcType;
            this.tdsWriter.writeRPCReaderUnicode(this.name, readerValue, dtv.getStreamSetterArgs().getLength(), this.isOutParam, this.collation);
        }

        @Override
        void execute(DTV dtv, SqlVariant sqlVariantValue) throws SQLServerException {
            this.tdsWriter.writeRPCSqlVariant(this.name, sqlVariantValue, this.isOutParam);
        }
    }
}

