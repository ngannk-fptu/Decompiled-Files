/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ColumnFilter;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DTV;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.SQLIdentifier;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.Util;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.UUID;

final class Column {
    private TypeInfo typeInfo;
    private CryptoMetadata cryptoMetadata;
    private SqlVariant internalVariant;
    private DTV updaterDTV;
    private final DTV getterDTV = new DTV();
    private JDBCType jdbcTypeSetByUser = null;
    private int valueLength = 0;
    private String columnName;
    private String baseColumnName;
    private int tableNum;
    private int infoStatus;
    private SQLIdentifier tableName;
    ColumnFilter filter;

    final void setInternalVariant(SqlVariant type) {
        this.internalVariant = type;
    }

    final SqlVariant getInternalVariant() {
        return this.internalVariant;
    }

    final TypeInfo getTypeInfo() {
        return this.typeInfo;
    }

    final void setColumnName(String name) {
        this.columnName = name;
    }

    final String getColumnName() {
        return this.columnName;
    }

    final void setBaseColumnName(String name) {
        this.baseColumnName = name;
    }

    final String getBaseColumnName() {
        return this.baseColumnName;
    }

    final void setTableNum(int num) {
        this.tableNum = num;
    }

    final int getTableNum() {
        return this.tableNum;
    }

    final void setInfoStatus(int status) {
        this.infoStatus = status;
    }

    final boolean hasDifferentName() {
        return 0 != (this.infoStatus & 0x20);
    }

    final boolean isHidden() {
        return 0 != (this.infoStatus & 0x10);
    }

    final boolean isKey() {
        return 0 != (this.infoStatus & 8);
    }

    final boolean isExpression() {
        return 0 != (this.infoStatus & 4);
    }

    final boolean isUpdatable() {
        return !this.isExpression() && !this.isHidden() && this.tableName.getObjectName().length() > 0;
    }

    final void setTableName(SQLIdentifier name) {
        this.tableName = name;
    }

    final SQLIdentifier getTableName() {
        return this.tableName;
    }

    Column(TypeInfo typeInfo, String columnName, SQLIdentifier tableName, CryptoMetadata cryptoMeta) {
        this.typeInfo = typeInfo;
        this.columnName = columnName;
        this.baseColumnName = columnName;
        this.tableName = tableName;
        this.cryptoMetadata = cryptoMeta;
    }

    CryptoMetadata getCryptoMetadata() {
        return this.cryptoMetadata;
    }

    final void clear() {
        this.getterDTV.clear();
    }

    final void skipValue(TDSReader tdsReader, boolean isDiscard) throws SQLServerException {
        this.getterDTV.skipValue(this.typeInfo, tdsReader, isDiscard);
    }

    final void initFromCompressedNull() {
        this.getterDTV.initFromCompressedNull();
    }

    void setFilter(ColumnFilter filter) {
        this.filter = filter;
    }

    final boolean isNull() {
        return this.getterDTV.isNull();
    }

    final boolean isInitialized() {
        return this.getterDTV.isInitialized();
    }

    Object getValue(JDBCType jdbcType, InputStreamGetterArgs getterArgs, Calendar cal, TDSReader tdsReader, SQLServerStatement statement) throws SQLServerException {
        Object value = this.getterDTV.getValue(jdbcType, this.typeInfo.getScale(), getterArgs, cal, this.typeInfo, this.cryptoMetadata, tdsReader, statement);
        this.setInternalVariant(this.getterDTV.getInternalVariant());
        return null != this.filter ? this.filter.apply(value, jdbcType) : value;
    }

    int getInt(TDSReader tdsReader, SQLServerStatement statement) throws SQLServerException {
        return (Integer)this.getValue(JDBCType.INTEGER, null, null, tdsReader, statement);
    }

    void updateValue(JDBCType jdbcType, Object value, JavaType javaType, StreamSetterArgs streamSetterArgs, Calendar cal, Integer scale, SQLServerConnection con, SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting, Integer precision, boolean forceEncrypt, int parameterIndex) throws SQLServerException {
        Object[] msgArgs;
        MessageFormat form;
        SSType ssType = this.typeInfo.getSSType();
        if (null != this.cryptoMetadata) {
            if (SSType.VARBINARYMAX == this.cryptoMetadata.baseTypeInfo.getSSType() && JDBCType.BINARY == jdbcType) {
                jdbcType = this.cryptoMetadata.baseTypeInfo.getSSType().getJDBCType();
            }
            if (null != value) {
                if (JDBCType.TINYINT == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() && javaType == JavaType.SHORT) {
                    String stringValue;
                    Short shortValue;
                    if (value instanceof Boolean) {
                        value = (Boolean)value != false ? Integer.valueOf(1) : Integer.valueOf(0);
                    }
                    if ((shortValue = Short.valueOf(stringValue = "" + value)) >= 0 && shortValue <= 255) {
                        value = shortValue.byteValue();
                        javaType = JavaType.BYTE;
                        jdbcType = JDBCType.TINYINT;
                    }
                }
            } else if (jdbcType.isBinary()) {
                jdbcType = this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType();
            }
        }
        if (null == scale && null != this.cryptoMetadata) {
            scale = this.cryptoMetadata.getBaseTypeInfo().getScale();
        }
        if (!(null == this.cryptoMetadata || JDBCType.CHAR != jdbcType && JDBCType.VARCHAR != jdbcType || JDBCType.NVARCHAR != this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() && JDBCType.NCHAR != this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() && JDBCType.LONGNVARCHAR != this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType())) {
            jdbcType = this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType();
        }
        if (Util.shouldHonorAEForParameters(stmtColumnEncriptionSetting, con)) {
            if (null == this.cryptoMetadata && forceEncrypt) {
                form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAETrue_UnencryptedColumnRS"));
                msgArgs = new Object[]{parameterIndex};
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            this.setJdbcTypeSetByUser(jdbcType);
            this.valueLength = Util.getValueLengthBaseOnJavaType(value, javaType, precision, scale, jdbcType);
            if (null != this.cryptoMetadata && (JDBCType.NCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() || JDBCType.NVARCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() || JDBCType.LONGNVARCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType())) {
                this.valueLength *= 2;
            }
        } else if (forceEncrypt) {
            form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAEFalseRS"));
            msgArgs = new Object[]{parameterIndex};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        if (null != streamSetterArgs) {
            if (!streamSetterArgs.streamType.convertsTo(this.typeInfo)) {
                DataTypes.throwConversionError(streamSetterArgs.streamType.toString(), ssType.toString());
            }
        } else if (null != this.cryptoMetadata) {
            JDBCType jdbcTypeFromSSType;
            SSType basicSSType;
            if (JDBCType.UNKNOWN == jdbcType && value instanceof UUID) {
                javaType = JavaType.STRING;
                jdbcType = JDBCType.GUID;
                this.setJdbcTypeSetByUser(jdbcType);
            }
            if (!jdbcType.convertsTo(basicSSType = this.cryptoMetadata.baseTypeInfo.getSSType())) {
                DataTypes.throwConversionError(jdbcType.toString(), ssType.toString());
            }
            if ((jdbcTypeFromSSType = Column.getJDBCTypeFromBaseSSType(basicSSType, jdbcType)) != jdbcType) {
                this.setJdbcTypeSetByUser(jdbcTypeFromSSType);
                jdbcType = jdbcTypeFromSSType;
                this.valueLength = Util.getValueLengthBaseOnJavaType(value, javaType, precision, scale, jdbcType);
            }
        } else if (!jdbcType.convertsTo(ssType)) {
            DataTypes.throwConversionError(jdbcType.toString(), ssType.toString());
        }
        if (!(JDBCType.DATETIMEOFFSET != jdbcType && JavaType.DATETIMEOFFSET != javaType || con.isKatmaiOrLater())) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        if (null != this.cryptoMetadata && con.sendStringParametersAsUnicode() && (JavaType.STRING == javaType || JavaType.READER == javaType || JavaType.CLOB == javaType || JavaType.OBJECT == javaType)) {
            jdbcType = Column.getSSPAUJDBCType(jdbcType);
        }
        if (!(SSType.NCHAR != ssType && SSType.NVARCHAR != ssType && SSType.NVARCHARMAX != ssType && SSType.NTEXT != ssType && SSType.XML != ssType || JDBCType.CHAR != jdbcType && JDBCType.VARCHAR != jdbcType && JDBCType.LONGVARCHAR != jdbcType && JDBCType.CLOB != jdbcType)) {
            jdbcType = JDBCType.CLOB == jdbcType ? JDBCType.NCLOB : JDBCType.NVARCHAR;
        } else if (!(SSType.BINARY != ssType && SSType.VARBINARY != ssType && SSType.VARBINARYMAX != ssType && SSType.IMAGE != ssType && SSType.UDT != ssType || JDBCType.CHAR != jdbcType && JDBCType.VARCHAR != jdbcType && JDBCType.LONGVARCHAR != jdbcType)) {
            jdbcType = JDBCType.VARBINARY;
        } else if (!(JDBCType.TIMESTAMP != jdbcType && JDBCType.DATE != jdbcType && JDBCType.TIME != jdbcType && JDBCType.DATETIMEOFFSET != jdbcType || SSType.CHAR != ssType && SSType.VARCHAR != ssType && SSType.VARCHARMAX != ssType && SSType.TEXT != ssType && SSType.NCHAR != ssType && SSType.NVARCHAR != ssType && SSType.NVARCHARMAX != ssType && SSType.NTEXT != ssType)) {
            jdbcType = JDBCType.NCHAR;
        }
        if (null == this.updaterDTV) {
            this.updaterDTV = new DTV();
        }
        this.updaterDTV.setValue(this.typeInfo.getSQLCollation(), jdbcType, value, javaType, streamSetterArgs, cal, scale, con, false);
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

    private static JDBCType getJDBCTypeFromBaseSSType(SSType basicSSType, JDBCType jdbcType) {
        switch (jdbcType) {
            case TIMESTAMP: {
                if (SSType.DATETIME == basicSSType) {
                    return JDBCType.DATETIME;
                }
                if (SSType.SMALLDATETIME == basicSSType) {
                    return JDBCType.SMALLDATETIME;
                }
                return jdbcType;
            }
            case NUMERIC: 
            case DECIMAL: {
                if (SSType.MONEY == basicSSType) {
                    return JDBCType.MONEY;
                }
                if (SSType.SMALLMONEY == basicSSType) {
                    return JDBCType.SMALLMONEY;
                }
                return jdbcType;
            }
            case CHAR: {
                if (SSType.GUID == basicSSType) {
                    return JDBCType.GUID;
                }
                if (SSType.VARCHARMAX == basicSSType) {
                    return JDBCType.LONGVARCHAR;
                }
                return jdbcType;
            }
        }
        return jdbcType;
    }

    boolean hasUpdates() {
        return null != this.updaterDTV;
    }

    void cancelUpdates() {
        this.updaterDTV = null;
    }

    void sendByRPC(TDSWriter tdsWriter, SQLServerStatement statement) throws SQLServerException {
        if (null == this.updaterDTV) {
            return;
        }
        try {
            this.updaterDTV.sendCryptoMetaData(this.cryptoMetadata, tdsWriter);
            this.updaterDTV.setJdbcTypeSetByUser(this.getJdbcTypeSetByUser(), this.getValueLength());
            this.updaterDTV.sendByRPC(this.baseColumnName, this.typeInfo, null != this.cryptoMetadata ? this.cryptoMetadata.getBaseTypeInfo().getSQLCollation() : this.typeInfo.getSQLCollation(), null != this.cryptoMetadata ? this.cryptoMetadata.getBaseTypeInfo().getPrecision() : this.typeInfo.getPrecision(), null != this.cryptoMetadata ? this.cryptoMetadata.getBaseTypeInfo().getScale() : this.typeInfo.getScale(), false, tdsWriter, statement);
        }
        finally {
            this.updaterDTV.sendCryptoMetaData(null, tdsWriter);
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
}

