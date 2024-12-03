/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DDC;
import com.microsoft.sqlserver.jdbc.DTV;
import com.microsoft.sqlserver.jdbc.DTVExecuteOp;
import com.microsoft.sqlserver.jdbc.DTVImpl;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.ParameterUtils;
import com.microsoft.sqlserver.jdbc.ReaderInputStream;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TVP;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import microsoft.sql.DateTimeOffset;

final class AppDTVImpl
extends DTVImpl {
    private JDBCType jdbcType = JDBCType.UNKNOWN;
    private Object value;
    private JavaType javaType;
    private StreamSetterArgs streamSetterArgs;
    private Calendar cal;
    private Integer scale;
    private boolean forceEncrypt;
    private SqlVariant internalVariant;

    AppDTVImpl() {
    }

    @Override
    final void skipValue(TypeInfo typeInfo, TDSReader tdsReader, boolean isDiscard) throws SQLServerException {
        assert (false);
    }

    @Override
    final void initFromCompressedNull() {
        assert (false);
    }

    @Override
    void setValue(DTV dtv, SQLCollation collation, JDBCType jdbcType, Object value, JavaType javaType, StreamSetterArgs streamSetterArgs, Calendar cal, Integer scale, SQLServerConnection con, boolean forceEncrypt) throws SQLServerException {
        dtv.setValue(value, javaType);
        dtv.setJdbcType(jdbcType);
        dtv.setStreamSetterArgs(streamSetterArgs);
        dtv.setCalendar(cal);
        dtv.setScale(scale);
        dtv.setForceEncrypt(forceEncrypt);
        dtv.executeOp(new SetValueOp(collation, con));
    }

    @Override
    void setValue(Object value, JavaType javaType) {
        this.value = value;
        this.javaType = javaType;
    }

    @Override
    void setStreamSetterArgs(StreamSetterArgs streamSetterArgs) {
        this.streamSetterArgs = streamSetterArgs;
    }

    @Override
    void setCalendar(Calendar cal) {
        this.cal = cal;
    }

    @Override
    void setScale(Integer scale) {
        this.scale = scale;
    }

    @Override
    void setForceEncrypt(boolean forceEncrypt) {
        this.forceEncrypt = forceEncrypt;
    }

    @Override
    StreamSetterArgs getStreamSetterArgs() {
        return this.streamSetterArgs;
    }

    @Override
    Calendar getCalendar() {
        return this.cal;
    }

    @Override
    Integer getScale() {
        return this.scale;
    }

    @Override
    boolean isNull() {
        return null == this.value;
    }

    @Override
    void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    JDBCType getJdbcType() {
        return this.jdbcType;
    }

    @Override
    JavaType getJavaType() {
        return this.javaType;
    }

    @Override
    Object getValue(DTV dtv, JDBCType jdbcType, int scale, InputStreamGetterArgs streamGetterArgs, Calendar cal, TypeInfo typeInfo, CryptoMetadata cryptoMetadata, TDSReader tdsReader, SQLServerStatement statement) throws SQLServerException {
        if (this.jdbcType != jdbcType) {
            DataTypes.throwConversionError(this.jdbcType.toString(), jdbcType.toString());
        }
        return this.value;
    }

    @Override
    Object getSetterValue() {
        return this.value;
    }

    @Override
    SqlVariant getInternalVariant() {
        return this.internalVariant;
    }

    void setInternalVariant(SqlVariant type) {
        this.internalVariant = type;
    }

    final class SetValueOp
    extends DTVExecuteOp {
        private final SQLCollation collation;
        private final SQLServerConnection con;

        SetValueOp(SQLCollation collation, SQLServerConnection con) {
            this.collation = collation;
            this.con = con;
        }

        @Override
        void execute(DTV dtv, String strValue) throws SQLServerException {
            JDBCType type = dtv.getJdbcType();
            if (JDBCType.DECIMAL == type || JDBCType.NUMERIC == type || JDBCType.MONEY == type || JDBCType.SMALLMONEY == type) {
                assert (null != strValue);
                try {
                    dtv.setValue(new BigDecimal(strValue), JavaType.BIGDECIMAL);
                }
                catch (NumberFormatException e) {
                    DataTypes.throwConversionError("String", type.toString());
                }
            } else if (type.isBinary()) {
                assert (null != strValue);
                dtv.setValue(ParameterUtils.hexToBin(strValue), JavaType.BYTEARRAY);
            } else if (null != this.collation && (JDBCType.CHAR == type || JDBCType.VARCHAR == type || JDBCType.LONGVARCHAR == type || JDBCType.CLOB == type)) {
                byte[] nativeEncoding = null;
                if (null != strValue) {
                    nativeEncoding = strValue.getBytes(this.collation.getCharset());
                }
                dtv.setValue(nativeEncoding, JavaType.BYTEARRAY);
            }
        }

        @Override
        void execute(DTV dtv, Clob clobValue) throws SQLServerException {
            assert (null != clobValue);
            try {
                DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), clobValue.length(), false);
            }
            catch (SQLException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
        }

        @Override
        void execute(DTV dtv, SQLServerSQLXML xmlValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Byte byteValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Integer intValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Time timeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (timeValue != null) : "value is null";
                dtv.setValue(timeValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, Date dateValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (dateValue != null) : "value is null";
                dtv.setValue(dateValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, Timestamp timestampValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (timestampValue != null) : "value is null";
                dtv.setValue(timestampValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, java.util.Date utilDateValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (utilDateValue != null) : "value is null";
                dtv.setValue(utilDateValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, LocalDate localDateValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (localDateValue != null) : "value is null";
                dtv.setValue(localDateValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, LocalTime localTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (localTimeValue != null) : "value is null";
                dtv.setValue(localTimeValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, LocalDateTime localDateTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (localDateTimeValue != null) : "value is null";
                dtv.setValue(localDateTimeValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, OffsetTime offsetTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (offsetTimeValue != null) : "value is null";
                dtv.setValue(offsetTimeValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, OffsetDateTime offsetDateTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (offsetDateTimeValue != null) : "value is null";
                dtv.setValue(offsetDateTimeValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, Calendar calendarValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (calendarValue != null) : "value is null";
                dtv.setValue(calendarValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, DateTimeOffset dtoValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert (dtoValue != null) : "value is null";
                dtv.setValue(dtoValue.toString(), JavaType.STRING);
            }
        }

        @Override
        void execute(DTV dtv, TVP tvpValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Float floatValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Double doubleValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, BigDecimal bigDecimalValue) throws SQLServerException {
            if (null != bigDecimalValue) {
                Integer dtvScale;
                Integer biScale = bigDecimalValue.scale();
                if (null == dtv.getScale() && JDBCType.DECIMAL == dtv.getJdbcType()) {
                    dtvScale = bigDecimalValue.precision() > 38 ? 38 - (bigDecimalValue.precision() - biScale) : biScale;
                    if (dtvScale > 38) {
                        dtv.setScale(38);
                        dtvScale = 38;
                    } else {
                        dtv.setScale(dtvScale);
                    }
                } else {
                    dtvScale = dtv.getScale();
                }
                if (null != dtvScale && 0 != Integer.compare(dtvScale, biScale)) {
                    bigDecimalValue = bigDecimalValue.setScale((int)dtvScale, RoundingMode.DOWN);
                }
            }
            dtv.setValue(bigDecimalValue, JavaType.BIGDECIMAL);
        }

        @Override
        void execute(DTV dtv, Long longValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, BigInteger bigIntegerValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Short shortValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Boolean booleanValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, byte[] byteArrayValue) throws SQLServerException {
        }

        @Override
        void execute(DTV dtv, Blob blobValue) throws SQLServerException {
            assert (null != blobValue);
            try {
                DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), blobValue.length(), false);
            }
            catch (SQLException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
        }

        @Override
        void execute(DTV dtv, InputStream inputStreamValue) throws SQLServerException {
            DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), dtv.getStreamSetterArgs().getLength(), true);
            if (JDBCType.NCHAR == AppDTVImpl.this.jdbcType || JDBCType.NVARCHAR == AppDTVImpl.this.jdbcType || JDBCType.LONGNVARCHAR == AppDTVImpl.this.jdbcType) {
                InputStreamReader readerValue = null;
                readerValue = new InputStreamReader(inputStreamValue, StandardCharsets.US_ASCII);
                dtv.setValue(readerValue, JavaType.READER);
                this.execute(dtv, readerValue);
            }
        }

        @Override
        void execute(DTV dtv, Reader readerValue) throws SQLServerException {
            assert (null != readerValue);
            JDBCType type = dtv.getJdbcType();
            long readerLength = DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), dtv.getStreamSetterArgs().getLength(), true);
            if (type.isBinary()) {
                String stringValue = DDC.convertReaderToString(readerValue, (int)readerLength);
                if (-1L != readerLength && (long)stringValue.length() != readerLength) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
                    Object[] msgArgs = new Object[]{readerLength, stringValue.length()};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                }
                dtv.setValue(stringValue, JavaType.STRING);
                this.execute(dtv, stringValue);
            } else if (null != this.collation && (JDBCType.CHAR == type || JDBCType.VARCHAR == type || JDBCType.LONGVARCHAR == type || JDBCType.CLOB == type)) {
                ReaderInputStream streamValue = new ReaderInputStream(readerValue, this.collation.getCharset(), readerLength);
                dtv.setValue(streamValue, JavaType.INPUTSTREAM);
                dtv.setStreamSetterArgs(new StreamSetterArgs(StreamType.CHARACTER, -1L));
                this.execute(dtv, streamValue);
            }
        }

        @Override
        void execute(DTV dtv, SqlVariant sqlVariantValue) throws SQLServerException {
        }
    }
}

