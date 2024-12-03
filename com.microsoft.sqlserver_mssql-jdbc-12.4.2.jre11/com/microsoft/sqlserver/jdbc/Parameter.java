/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CekTableEntry;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DTV;
import com.microsoft.sqlserver.jdbc.DTVExecuteOp;
import com.microsoft.sqlserver.jdbc.DatetimeType;
import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.StreamRetValue;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.TVP;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.Locale;
import microsoft.sql.DateTimeOffset;

final class Parameter {
    private TypeInfo typeInfo;
    CryptoMetadata cryptoMeta = null;
    private boolean shouldHonorAEForParameter = false;
    private boolean userProvidesPrecision = false;
    private boolean userProvidesScale = false;
    private String typeDefinition = null;
    boolean renewDefinition = false;
    private JDBCType jdbcTypeSetByUser = null;
    private int valueLength = 0;
    private boolean forceEncryption = false;
    int scale = 0;
    private int outScale = 4;
    private String name;
    private String schemaName;
    private DTV getterDTV;
    private DTV registeredOutDTV = null;
    private DTV setterDTV = null;
    private DTV inputDTV = null;

    TypeInfo getTypeInfo() {
        return this.typeInfo;
    }

    final CryptoMetadata getCryptoMetadata() {
        return this.cryptoMeta;
    }

    Parameter(boolean honorAE) {
        this.shouldHonorAEForParameter = honorAE;
    }

    boolean isOutput() {
        return null != this.registeredOutDTV;
    }

    JDBCType getJdbcType() {
        return null != this.inputDTV ? this.inputDTV.getJdbcType() : JDBCType.UNKNOWN;
    }

    private static JDBCType getSSPAUJDBCType(JDBCType jdbcType) {
        switch (jdbcType) {
            case CHAR: {
                return JDBCType.NCHAR;
            }
            case VARCHAR: {
                return JDBCType.NVARCHAR;
            }
            case LONGVARCHAR: {
                return JDBCType.LONGNVARCHAR;
            }
            case CLOB: {
                return JDBCType.NCLOB;
            }
        }
        return jdbcType;
    }

    void registerForOutput(JDBCType jdbcType, SQLServerConnection con) throws SQLServerException {
        if (JDBCType.DATETIMEOFFSET == jdbcType && !con.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        if (con.sendStringParametersAsUnicode()) {
            if (this.shouldHonorAEForParameter) {
                this.setJdbcTypeSetByUser(jdbcType);
            }
            jdbcType = Parameter.getSSPAUJDBCType(jdbcType);
        }
        this.registeredOutDTV = new DTV();
        this.registeredOutDTV.setJdbcType(jdbcType);
        if (null == this.setterDTV) {
            this.inputDTV = this.registeredOutDTV;
        }
        this.resetOutputValue();
    }

    int getOutScale() {
        return this.outScale;
    }

    void setOutScale(int outScale) {
        this.outScale = outScale;
        this.userProvidesScale = true;
    }

    final Parameter cloneForBatch() {
        Parameter clonedParam = new Parameter(this.shouldHonorAEForParameter);
        clonedParam.typeInfo = this.typeInfo;
        clonedParam.typeDefinition = this.typeDefinition;
        clonedParam.outScale = this.outScale;
        clonedParam.name = this.name;
        clonedParam.getterDTV = this.getterDTV;
        clonedParam.registeredOutDTV = this.registeredOutDTV;
        clonedParam.setterDTV = this.setterDTV;
        clonedParam.inputDTV = this.inputDTV;
        clonedParam.cryptoMeta = this.cryptoMeta;
        clonedParam.jdbcTypeSetByUser = this.jdbcTypeSetByUser;
        clonedParam.valueLength = this.valueLength;
        clonedParam.userProvidesPrecision = this.userProvidesPrecision;
        clonedParam.userProvidesScale = this.userProvidesScale;
        return clonedParam;
    }

    final void skipValue(TDSReader tdsReader, boolean isDiscard) throws SQLServerException {
        if (null == this.getterDTV) {
            this.getterDTV = new DTV();
        }
        this.deriveTypeInfo(tdsReader);
        this.getterDTV.skipValue(this.typeInfo, tdsReader, isDiscard);
    }

    final void skipRetValStatus(TDSReader tdsReader) throws SQLServerException {
        StreamRetValue srv = new StreamRetValue();
        srv.setFromTDS(tdsReader);
    }

    void clearInputValue() {
        this.setterDTV = null;
        this.inputDTV = this.registeredOutDTV;
    }

    void resetOutputValue() {
        this.getterDTV = null;
        this.typeInfo = null;
    }

    void deriveTypeInfo(TDSReader tdsReader) throws SQLServerException {
        if (null == this.typeInfo) {
            this.typeInfo = TypeInfo.getInstance(tdsReader, true);
            if (this.shouldHonorAEForParameter && this.typeInfo.isEncrypted()) {
                CekTableEntry cekEntry = this.cryptoMeta.getCekTableEntry();
                this.cryptoMeta = new StreamRetValue().getCryptoMetadata(tdsReader);
                this.cryptoMeta.setCekTableEntry(cekEntry);
            }
        }
    }

    void setFromReturnStatus(int returnStatus, SQLServerConnection con) throws SQLServerException {
        if (null == this.getterDTV) {
            this.getterDTV = new DTV();
        }
        this.getterDTV.setValue(null, JDBCType.INTEGER, returnStatus, JavaType.INTEGER, null, null, null, con, this.getForceEncryption());
    }

    void setValue(JDBCType jdbcType, Object value, JavaType javaType, StreamSetterArgs streamSetterArgs, Calendar calendar, Integer precision, Integer scale, SQLServerConnection con, boolean forceEncrypt, SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting, int parameterIndex, String userSQL, String tvpName) throws SQLServerException {
        if (this.shouldHonorAEForParameter) {
            this.userProvidesPrecision = false;
            this.userProvidesScale = false;
            if (null != precision) {
                this.userProvidesPrecision = true;
            }
            if (null != scale) {
                this.userProvidesScale = true;
            }
            if (!(this.isOutput() || JavaType.SHORT != javaType || JDBCType.TINYINT != jdbcType && JDBCType.SMALLINT != jdbcType)) {
                if ((Short)value >= 0 && (Short)value <= 255) {
                    value = ((Short)value).byteValue();
                    javaType = JavaType.of(value);
                    jdbcType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
                } else if (JDBCType.TINYINT == jdbcType) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                    Object[] msgArgs = new Object[]{javaType.toString().toLowerCase(Locale.ENGLISH), jdbcType.toString().toLowerCase(Locale.ENGLISH)};
                    throw new SQLServerException(form.format(msgArgs), null);
                }
            }
        }
        if (forceEncrypt && !Util.shouldHonorAEForParameters(stmtColumnEncriptionSetting, con)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAEFalse"));
            Object[] msgArgs = new Object[]{parameterIndex, userSQL};
            SQLServerException.makeFromDriverError(con, this, form.format(msgArgs), null, true);
        }
        if (!(JDBCType.DATETIMEOFFSET != jdbcType && JavaType.DATETIMEOFFSET != javaType || con.isKatmaiOrLater())) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        if (JavaType.TVP == javaType) {
            TVP tvpValue;
            if (null == value) {
                tvpValue = new TVP(tvpName);
            } else if (value instanceof SQLServerDataTable) {
                tvpValue = new TVP(tvpName, (SQLServerDataTable)value);
            } else if (value instanceof ResultSet) {
                tvpValue = new TVP(tvpName, (ResultSet)value);
            } else if (value instanceof ISQLServerDataRecord) {
                tvpValue = new TVP(tvpName, (ISQLServerDataRecord)value);
            } else {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPInvalidValue"));
                Object[] msgArgs = new Object[]{parameterIndex};
                throw new SQLServerException(form.format(msgArgs), null);
            }
            if (!tvpValue.isNull() && 0 == tvpValue.getTVPColumnCount()) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPEmptyMetadata"), null);
            }
            this.name = tvpValue.getTVPName();
            this.schemaName = tvpValue.getOwningSchemaNameTVP();
            value = tvpValue;
        }
        if (this.shouldHonorAEForParameter) {
            this.setForceEncryption(forceEncrypt);
            if (!this.isOutput() || this.jdbcTypeSetByUser == null) {
                this.setJdbcTypeSetByUser(jdbcType);
            }
            if (!jdbcType.isTextual() && !jdbcType.isBinary() || !this.isOutput() || this.valueLength == 0) {
                this.valueLength = Util.getValueLengthBaseOnJavaType(value, javaType, precision, scale, jdbcType);
            }
            if (null != scale) {
                this.outScale = scale;
            }
        }
        if (con.sendStringParametersAsUnicode() && (JavaType.STRING == javaType || JavaType.READER == javaType || JavaType.CLOB == javaType || JavaType.OBJECT == javaType)) {
            jdbcType = Parameter.getSSPAUJDBCType(jdbcType);
        }
        DTV newDTV = new DTV();
        newDTV.setValue(con.getDatabaseCollation(), jdbcType, value, javaType, streamSetterArgs, calendar, scale, con, forceEncrypt);
        if (!con.sendStringParametersAsUnicode()) {
            newDTV.sendStringParametersAsUnicode = false;
        }
        this.inputDTV = this.setterDTV = newDTV;
    }

    boolean isNull() {
        if (null != this.getterDTV) {
            return this.getterDTV.isNull();
        }
        return false;
    }

    boolean isValueGotten() {
        return null != this.getterDTV;
    }

    Object getValue(JDBCType jdbcType, InputStreamGetterArgs getterArgs, Calendar cal, TDSReader tdsReader, SQLServerStatement statement) throws SQLServerException {
        if (null == this.getterDTV) {
            this.getterDTV = new DTV();
        }
        this.deriveTypeInfo(tdsReader);
        return this.getterDTV.getValue(jdbcType, this.outScale, getterArgs, cal, this.typeInfo, this.cryptoMeta, tdsReader, statement);
    }

    Object getSetterValue() {
        return this.setterDTV.getSetterValue();
    }

    int getInt(TDSReader tdsReader, SQLServerStatement statement) throws SQLServerException {
        Integer value = (Integer)this.getValue(JDBCType.INTEGER, null, null, tdsReader, statement);
        return null != value ? value : 0;
    }

    String getTypeDefinition(SQLServerConnection con, TDSReader tdsReader) throws SQLServerException {
        if (null == this.inputDTV) {
            return null;
        }
        this.inputDTV.executeOp(new GetTypeDefinitionOp(this, con));
        return this.typeDefinition;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void sendByRPC(TDSWriter tdsWriter, SQLServerStatement statement) throws SQLServerException {
        assert (null != this.inputDTV) : "Parameter was neither set nor registered";
        SQLServerConnection conn = statement.connection;
        try {
            this.inputDTV.sendCryptoMetaData(this.cryptoMeta, tdsWriter);
            this.inputDTV.setJdbcTypeSetByUser(this.getJdbcTypeSetByUser(), this.getValueLength());
            this.inputDTV.sendByRPC(this.name, null, conn.getDatabaseCollation(), this.valueLength, this.isOutput() ? this.outScale : this.scale, this.isOutput(), tdsWriter, statement);
        }
        finally {
            this.inputDTV.sendCryptoMetaData(null, tdsWriter);
        }
        if (JavaType.INPUTSTREAM == this.inputDTV.getJavaType() || JavaType.READER == this.inputDTV.getJavaType()) {
            this.setterDTV = null;
            this.inputDTV = null;
        }
    }

    JDBCType getJdbcTypeSetByUser() {
        return this.jdbcTypeSetByUser;
    }

    void setJdbcTypeSetByUser(JDBCType jdbcTypeSetByUser) {
        this.jdbcTypeSetByUser = jdbcTypeSetByUser;
    }

    int getValueLength() {
        return this.valueLength;
    }

    void setValueLength(int valueLength) {
        this.valueLength = valueLength;
        this.userProvidesPrecision = true;
    }

    boolean getForceEncryption() {
        return this.forceEncryption;
    }

    void setForceEncryption(boolean forceEncryption) {
        this.forceEncryption = forceEncryption;
    }

    final class GetTypeDefinitionOp
    extends DTVExecuteOp {
        private static final String NVARCHAR_MAX = "nvarchar(max)";
        private static final String NVARCHAR_4K = "nvarchar(4000)";
        private static final String NTEXT = "ntext";
        private static final String VARCHAR_MAX = "varchar(max)";
        private static final String VARCHAR_8K = "varchar(8000)";
        private static final String TEXT = "text";
        private static final String VARBINARY_MAX = "varbinary(max)";
        private static final String VARBINARY_8K = "varbinary(8000)";
        private static final String IMAGE = "image";
        private final Parameter param;
        private final SQLServerConnection con;

        GetTypeDefinitionOp(Parameter param, SQLServerConnection con) {
            this.param = param;
            this.con = con;
        }

        private void setTypeDefinition(DTV dtv) {
            switch (dtv.getJdbcType()) {
                case TINYINT: {
                    this.param.typeDefinition = SSType.TINYINT.toString();
                    break;
                }
                case SMALLINT: {
                    this.param.typeDefinition = SSType.SMALLINT.toString();
                    break;
                }
                case INTEGER: {
                    this.param.typeDefinition = SSType.INTEGER.toString();
                    break;
                }
                case BIGINT: {
                    this.param.typeDefinition = SSType.BIGINT.toString();
                    break;
                }
                case REAL: {
                    if (this.param.shouldHonorAEForParameter && null != Parameter.this.jdbcTypeSetByUser && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.REAL.toString();
                        break;
                    }
                    this.param.typeDefinition = SSType.FLOAT.toString();
                    break;
                }
                case FLOAT: 
                case DOUBLE: {
                    this.param.typeDefinition = SSType.FLOAT.toString();
                    break;
                }
                case DECIMAL: 
                case NUMERIC: {
                    Integer inScale;
                    if (Parameter.this.scale > 38) {
                        Parameter.this.scale = 38;
                    }
                    if (null != (inScale = dtv.getScale()) && Parameter.this.scale < inScale) {
                        Parameter.this.scale = inScale;
                    }
                    if (this.param.isOutput() && Parameter.this.scale < this.param.getOutScale()) {
                        Parameter.this.scale = this.param.getOutScale();
                    }
                    if (this.param.shouldHonorAEForParameter && null != Parameter.this.jdbcTypeSetByUser && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (0 == Parameter.this.valueLength) {
                            if (!Parameter.this.isOutput()) {
                                this.param.typeDefinition = SSType.DECIMAL.toString() + "(18," + Parameter.this.scale + ")";
                            }
                        } else if (18 >= Parameter.this.valueLength) {
                            this.param.typeDefinition = SSType.DECIMAL.toString() + "(18," + Parameter.this.scale + ")";
                            if (18 < Parameter.this.valueLength + Parameter.this.scale) {
                                this.param.typeDefinition = SSType.DECIMAL.toString() + "(" + (18 + Parameter.this.scale) + "," + Parameter.this.scale + ")";
                            }
                        } else {
                            this.param.typeDefinition = SSType.DECIMAL.toString() + "(38," + Parameter.this.scale + ")";
                        }
                        if (Parameter.this.isOutput()) {
                            this.param.typeDefinition = SSType.DECIMAL.toString() + "(38, " + Parameter.this.scale + ")";
                        }
                        if (!Parameter.this.userProvidesPrecision) break;
                        this.param.typeDefinition = SSType.DECIMAL.toString() + "(" + Parameter.this.valueLength + "," + Parameter.this.scale + ")";
                        break;
                    }
                    this.param.typeDefinition = SSType.DECIMAL.toString() + "(38," + Parameter.this.scale + ")";
                    break;
                }
                case MONEY: {
                    this.param.typeDefinition = SSType.MONEY.toString();
                    break;
                }
                case SMALLMONEY: {
                    this.param.typeDefinition = SSType.MONEY.toString();
                    if (!this.param.shouldHonorAEForParameter || null == this.param.getCryptoMetadata() && this.param.renewDefinition) break;
                    this.param.typeDefinition = SSType.SMALLMONEY.toString();
                    break;
                }
                case BIT: 
                case BOOLEAN: {
                    this.param.typeDefinition = SSType.BIT.toString();
                    break;
                }
                case LONGVARBINARY: 
                case BLOB: {
                    this.param.typeDefinition = VARBINARY_MAX;
                    break;
                }
                case BINARY: 
                case VARBINARY: {
                    if (VARBINARY_MAX.equals(this.param.typeDefinition) || IMAGE.equals(this.param.typeDefinition)) break;
                    if (this.param.shouldHonorAEForParameter && null != Parameter.this.jdbcTypeSetByUser && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "varbinary(1)";
                            ++Parameter.this.valueLength;
                        } else {
                            this.param.typeDefinition = "varbinary(" + Parameter.this.valueLength + ")";
                        }
                        if (JDBCType.LONGVARBINARY != Parameter.this.jdbcTypeSetByUser) break;
                        this.param.typeDefinition = VARBINARY_MAX;
                        break;
                    }
                    this.param.typeDefinition = VARBINARY_8K;
                    break;
                }
                case DATE: {
                    this.param.typeDefinition = this.con.isKatmaiOrLater() ? SSType.DATE.toString() : SSType.DATETIME.toString();
                    break;
                }
                case TIME: {
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (Parameter.this.userProvidesScale) {
                            this.param.typeDefinition = SSType.TIME.toString() + "(" + Parameter.this.outScale + ")";
                            break;
                        }
                        this.param.typeDefinition = SSType.TIME.toString() + "(" + Parameter.this.valueLength + ")";
                        break;
                    }
                    this.param.typeDefinition = this.con.getSendTimeAsDatetime() ? SSType.DATETIME.toString() : SSType.TIME.toString();
                    break;
                }
                case TIMESTAMP: {
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (Parameter.this.userProvidesScale) {
                            this.param.typeDefinition = this.getDatetimeDataType(this.con, Parameter.this.outScale);
                            break;
                        }
                        this.param.typeDefinition = this.getDatetimeDataType(this.con, Parameter.this.valueLength);
                        break;
                    }
                    this.param.typeDefinition = this.getDatetimeDataType(this.con, null);
                    break;
                }
                case DATETIME: {
                    this.param.typeDefinition = this.getDatetimeDataType(this.con, null);
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.DATETIME.toString();
                    }
                    if (!this.param.shouldHonorAEForParameter) {
                        if (!this.param.isOutput()) break;
                        this.param.typeDefinition = this.getDatetimeDataType(this.con, Parameter.this.outScale);
                        break;
                    }
                    if (null != this.param.getCryptoMetadata() || !this.param.renewDefinition || !this.param.isOutput()) break;
                    this.param.typeDefinition = this.getDatetimeDataType(this.con, Parameter.this.outScale);
                    break;
                }
                case SMALLDATETIME: {
                    this.param.typeDefinition = this.getDatetimeDataType(this.con, null);
                    if (!this.param.shouldHonorAEForParameter || null == this.param.getCryptoMetadata() && this.param.renewDefinition) break;
                    this.param.typeDefinition = SSType.SMALLDATETIME.toString();
                    break;
                }
                case TIME_WITH_TIMEZONE: 
                case TIMESTAMP_WITH_TIMEZONE: 
                case DATETIMEOFFSET: {
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (Parameter.this.userProvidesScale) {
                            this.param.typeDefinition = SSType.DATETIMEOFFSET.toString() + "(" + Parameter.this.outScale + ")";
                            break;
                        }
                        this.param.typeDefinition = SSType.DATETIMEOFFSET.toString() + "(" + Parameter.this.valueLength + ")";
                        break;
                    }
                    this.param.typeDefinition = SSType.DATETIMEOFFSET.toString();
                    break;
                }
                case LONGVARCHAR: 
                case CLOB: {
                    this.param.typeDefinition = VARCHAR_MAX;
                    break;
                }
                case CHAR: 
                case VARCHAR: {
                    if (VARCHAR_MAX.equals(this.param.typeDefinition) || TEXT.equals(this.param.typeDefinition)) break;
                    if (this.param.shouldHonorAEForParameter && null != Parameter.this.jdbcTypeSetByUser && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = SSType.VARCHAR.toString() + "(1)";
                            ++Parameter.this.valueLength;
                            break;
                        }
                        this.param.typeDefinition = SSType.VARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                        if (8000 > Parameter.this.valueLength) break;
                        this.param.typeDefinition = VARCHAR_MAX;
                        break;
                    }
                    this.param.typeDefinition = VARCHAR_8K;
                    break;
                }
                case LONGNVARCHAR: {
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.VARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.CHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.LONGVARCHAR)) {
                            if (0 == Parameter.this.valueLength) {
                                this.param.typeDefinition = SSType.VARCHAR.toString() + "(1)";
                                ++Parameter.this.valueLength;
                            } else {
                                this.param.typeDefinition = 8000 < Parameter.this.valueLength ? VARCHAR_MAX : SSType.VARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                            }
                            if (Parameter.this.jdbcTypeSetByUser != JDBCType.LONGVARCHAR) break;
                            this.param.typeDefinition = VARCHAR_MAX;
                            break;
                        }
                        if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.NVARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.LONGNVARCHAR)) {
                            if (0 == Parameter.this.valueLength) {
                                this.param.typeDefinition = SSType.NVARCHAR.toString() + "(1)";
                                ++Parameter.this.valueLength;
                            } else {
                                this.param.typeDefinition = 4000 < Parameter.this.valueLength ? NVARCHAR_MAX : SSType.NVARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                            }
                            if (Parameter.this.jdbcTypeSetByUser != JDBCType.LONGNVARCHAR) break;
                            this.param.typeDefinition = NVARCHAR_MAX;
                            break;
                        }
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = SSType.NVARCHAR.toString() + "(1)";
                            ++Parameter.this.valueLength;
                            break;
                        }
                        this.param.typeDefinition = SSType.NVARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                        if (8000 > Parameter.this.valueLength) break;
                        this.param.typeDefinition = NVARCHAR_MAX;
                        break;
                    }
                    this.param.typeDefinition = NVARCHAR_MAX;
                    break;
                }
                case NCLOB: {
                    this.param.typeDefinition = NVARCHAR_MAX;
                    break;
                }
                case NCHAR: 
                case NVARCHAR: {
                    if (NVARCHAR_MAX.equals(this.param.typeDefinition) || NTEXT.equals(this.param.typeDefinition)) break;
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.VARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.CHAR || JDBCType.LONGVARCHAR == Parameter.this.jdbcTypeSetByUser)) {
                            if (0 == Parameter.this.valueLength) {
                                this.param.typeDefinition = SSType.VARCHAR.toString() + "(1)";
                                ++Parameter.this.valueLength;
                            } else {
                                this.param.typeDefinition = SSType.VARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                                if (8000 < Parameter.this.valueLength) {
                                    this.param.typeDefinition = VARCHAR_MAX;
                                }
                            }
                            if (JDBCType.LONGVARCHAR != Parameter.this.jdbcTypeSetByUser) break;
                            this.param.typeDefinition = VARCHAR_MAX;
                            break;
                        }
                        if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.NVARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.NCHAR || JDBCType.LONGNVARCHAR == Parameter.this.jdbcTypeSetByUser)) {
                            if (0 == Parameter.this.valueLength) {
                                this.param.typeDefinition = SSType.NVARCHAR.toString() + "(1)";
                                ++Parameter.this.valueLength;
                            } else {
                                this.param.typeDefinition = SSType.NVARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                                if (8000 <= Parameter.this.valueLength) {
                                    this.param.typeDefinition = NVARCHAR_MAX;
                                }
                            }
                            if (JDBCType.LONGNVARCHAR != Parameter.this.jdbcTypeSetByUser) break;
                            this.param.typeDefinition = NVARCHAR_MAX;
                            break;
                        }
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = SSType.NVARCHAR.toString() + "(1)";
                            ++Parameter.this.valueLength;
                            break;
                        }
                        this.param.typeDefinition = SSType.NVARCHAR.toString() + "(" + Parameter.this.valueLength + ")";
                        if (8000 > Parameter.this.valueLength) break;
                        this.param.typeDefinition = NVARCHAR_MAX;
                        break;
                    }
                    this.param.typeDefinition = NVARCHAR_4K;
                    break;
                }
                case SQLXML: {
                    this.param.typeDefinition = SSType.XML.toString();
                    break;
                }
                case TVP: {
                    String schema = this.param.schemaName;
                    if (null != schema) {
                        this.param.typeDefinition = "[" + schema + "].[" + this.param.name + "] READONLY";
                        break;
                    }
                    this.param.typeDefinition = "[" + this.param.name + "] READONLY";
                    break;
                }
                case GUID: {
                    this.param.typeDefinition = SSType.GUID.toString();
                    break;
                }
                case SQL_VARIANT: {
                    this.param.typeDefinition = SSType.SQL_VARIANT.toString();
                    break;
                }
                case GEOMETRY: {
                    this.param.typeDefinition = SSType.GEOMETRY.toString();
                    break;
                }
                case GEOGRAPHY: {
                    this.param.typeDefinition = SSType.GEOGRAPHY.toString();
                    break;
                }
                default: {
                    assert (false) : "Unexpected JDBC type " + dtv.getJdbcType();
                    break;
                }
            }
        }

        String getDatetimeDataType(SQLServerConnection con, Integer scale) {
            if (con.isKatmaiOrLater()) {
                String paramType = con.getDatetimeParameterType();
                if (paramType.equalsIgnoreCase(DatetimeType.DATETIME2.toString())) {
                    Object datatype = SSType.DATETIME2.toString();
                    if (scale != null) {
                        datatype = (String)datatype + "(" + scale + ")";
                    }
                    return datatype;
                }
                if (paramType.equalsIgnoreCase(DatetimeType.DATETIMEOFFSET.toString())) {
                    Object datatype = SSType.DATETIMEOFFSET.toString();
                    if (scale != null) {
                        datatype = (String)datatype + "(" + scale + ")";
                    }
                    return datatype;
                }
                return SSType.DATETIME.toString();
            }
            return SSType.DATETIME.toString();
        }

        @Override
        void execute(DTV dtv, String strValue) throws SQLServerException {
            if (null != strValue && strValue.length() > 4000) {
                dtv.setJdbcType(JDBCType.LONGNVARCHAR);
            }
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Clob clobValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Byte byteValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Integer intValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Time timeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Date dateValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Timestamp timestampValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, java.util.Date utildateValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Calendar calendarValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, LocalDate localDateValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, LocalTime localTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, LocalDateTime localDateTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, OffsetTime offsetTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, OffsetDateTime offsetDateTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, DateTimeOffset dtoValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Float floatValue) throws SQLServerException {
            Parameter.this.scale = 4;
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Double doubleValue) throws SQLServerException {
            Parameter.this.scale = 4;
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, BigDecimal bigDecimalValue) throws SQLServerException {
            if (null != bigDecimalValue) {
                Parameter.this.scale = bigDecimalValue.scale();
                if (Parameter.this.scale < 0) {
                    Parameter.this.scale = 0;
                }
            }
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Long longValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, BigInteger bigIntegerValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Short shortValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Boolean booleanValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, byte[] byteArrayValue) throws SQLServerException {
            if (null != byteArrayValue && byteArrayValue.length > 8000 && dtv.getJdbcType() != JDBCType.GEOMETRY && dtv.getJdbcType() != JDBCType.GEOGRAPHY) {
                dtv.setJdbcType(dtv.getJdbcType().isBinary() ? JDBCType.LONGVARBINARY : JDBCType.LONGVARCHAR);
            }
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Blob blobValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, InputStream inputStreamValue) throws SQLServerException {
            StreamSetterArgs streamSetterArgs = dtv.getStreamSetterArgs();
            JDBCType jdbcType = dtv.getJdbcType();
            if (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.BINARY == jdbcType || JDBCType.VARBINARY == jdbcType) {
                if (streamSetterArgs.getLength() > 8000L) {
                    dtv.setJdbcType(jdbcType.isBinary() ? JDBCType.LONGVARBINARY : JDBCType.LONGVARCHAR);
                } else if (-1L == streamSetterArgs.getLength()) {
                    byte[] vartypeBytes = new byte[8001];
                    BufferedInputStream bufferedStream = new BufferedInputStream(inputStreamValue, vartypeBytes.length);
                    int bytesRead = 0;
                    try {
                        bufferedStream.mark(vartypeBytes.length);
                        bytesRead = bufferedStream.read(vartypeBytes, 0, vartypeBytes.length);
                        if (-1 == bytesRead) {
                            bytesRead = 0;
                        }
                        bufferedStream.reset();
                    }
                    catch (IOException e) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                        Object[] msgArgs = new Object[]{e.toString()};
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                    }
                    dtv.setValue(bufferedStream, JavaType.INPUTSTREAM);
                    if (bytesRead > 8000) {
                        dtv.setJdbcType(jdbcType.isBinary() ? JDBCType.LONGVARBINARY : JDBCType.LONGVARCHAR);
                    } else {
                        streamSetterArgs.setLength(bytesRead);
                    }
                }
            }
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, Reader readerValue) throws SQLServerException {
            if (JDBCType.NCHAR == dtv.getJdbcType() || JDBCType.NVARCHAR == dtv.getJdbcType()) {
                StreamSetterArgs streamSetterArgs = dtv.getStreamSetterArgs();
                if (streamSetterArgs.getLength() > 4000L) {
                    dtv.setJdbcType(JDBCType.LONGNVARCHAR);
                } else if (-1L == streamSetterArgs.getLength()) {
                    char[] vartypeChars = new char[4001];
                    BufferedReader bufferedReader = new BufferedReader(readerValue, vartypeChars.length);
                    int charsRead = 0;
                    try {
                        bufferedReader.mark(vartypeChars.length);
                        charsRead = bufferedReader.read(vartypeChars, 0, vartypeChars.length);
                        if (-1 == charsRead) {
                            charsRead = 0;
                        }
                        bufferedReader.reset();
                    }
                    catch (IOException e) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                        Object[] msgArgs = new Object[]{e.toString()};
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                    }
                    dtv.setValue(bufferedReader, JavaType.READER);
                    if (charsRead > 4000) {
                        dtv.setJdbcType(JDBCType.LONGNVARCHAR);
                    } else {
                        streamSetterArgs.setLength(charsRead);
                    }
                }
            }
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, SQLServerSQLXML xmlValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, TVP tvpValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }

        @Override
        void execute(DTV dtv, SqlVariant sqlVariantValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
    }
}

