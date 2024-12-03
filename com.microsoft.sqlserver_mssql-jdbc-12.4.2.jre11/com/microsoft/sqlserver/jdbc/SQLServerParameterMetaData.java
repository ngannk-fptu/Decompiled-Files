/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DataTypeFilter;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerFMTQuery;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.ThreePartName;
import com.microsoft.sqlserver.jdbc.ZeroFixupFilter;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SQLServerParameterMetaData
implements ParameterMetaData {
    private static final int SQL_SERVER_2012_VERSION = 11;
    private static final String DATA_TYPE = "DATA_TYPE";
    private static final String COLUMN_TYPE = "COLUMN_TYPE";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String PRECISION = "PRECISION";
    private static final String SCALE = "SCALE";
    private static final String NULLABLE = "NULLABLE";
    private static final String SS_TYPE_SCHEMA_NAME = "SS_TYPE_SCHEMA_NAME";
    private final SQLServerPreparedStatement stmtParent;
    private SQLServerConnection con;
    private List<Map<String, Object>> procMetadata;
    boolean procedureIsFound;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerParameterMetaData");
    private static final AtomicInteger baseID = new AtomicInteger(0);
    private final String traceID;
    boolean isTVP;
    Map<Integer, QueryMeta> queryMetaMap;

    private static int nextInstanceID() {
        return baseID.incrementAndGet();
    }

    public final String toString() {
        return this.traceID;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void parseQueryMeta(ResultSet rsQueryMeta) throws SQLServerException {
        Pattern datatypePattern = Pattern.compile("(.*)\\((.*)(\\)|,(.*)\\))");
        try {
            if (null == rsQueryMeta) return;
            while (rsQueryMeta.next()) {
                int paramOrdinal;
                SSType ssType;
                QueryMeta qm;
                block38: {
                    block41: {
                        String typename;
                        block39: {
                            Matcher matcher;
                            block40: {
                                qm = new QueryMeta();
                                ssType = null;
                                paramOrdinal = rsQueryMeta.getInt("parameter_ordinal");
                                typename = rsQueryMeta.getString("suggested_system_type_name");
                                if (null == typename) {
                                    typename = rsQueryMeta.getString("suggested_user_type_name");
                                    try (SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement)this.con.prepareStatement("select max_length, precision, scale, is_nullable from sys.assembly_types where name = ?");){
                                        pstmt.setNString(1, typename);
                                        try (ResultSet assemblyRs = pstmt.executeQuery();){
                                            if (assemblyRs.next()) {
                                                qm.parameterTypeName = typename;
                                                qm.precision = assemblyRs.getInt("max_length");
                                                qm.scale = assemblyRs.getInt("scale");
                                                ssType = SSType.UDT;
                                            }
                                            break block38;
                                        }
                                    }
                                }
                                qm.precision = rsQueryMeta.getInt("suggested_precision");
                                qm.scale = rsQueryMeta.getInt("suggested_scale");
                                matcher = datatypePattern.matcher(typename);
                                if (!matcher.matches()) break block39;
                                ssType = SSType.of(matcher.group(1));
                                if (!"varchar(max)".equalsIgnoreCase(typename) && !"varbinary(max)".equalsIgnoreCase(typename)) break block40;
                                qm.precision = Integer.MAX_VALUE;
                                break block41;
                            }
                            if ("nvarchar(max)".equalsIgnoreCase(typename)) {
                                qm.precision = 0x3FFFFFFF;
                                break block41;
                            } else if (SSType.Category.CHARACTER == ssType.category || SSType.Category.BINARY == ssType.category || SSType.Category.NCHARACTER == ssType.category) {
                                try {
                                    qm.precision = Integer.parseInt(matcher.group(2));
                                }
                                catch (NumberFormatException e) {
                                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_metaDataErrorForParameter"));
                                    Object[] msgArgs = new Object[]{paramOrdinal};
                                    SQLServerException.makeFromDriverError(this.con, this.stmtParent, form.format(msgArgs) + " " + e.getMessage(), null, false);
                                }
                            }
                            break block41;
                        }
                        ssType = SSType.of(typename);
                    }
                    if (SSType.FLOAT == ssType) {
                        qm.precision = 15;
                    } else if (SSType.REAL == ssType) {
                        qm.precision = 7;
                    } else if (SSType.TEXT == ssType) {
                        qm.precision = Integer.MAX_VALUE;
                    } else if (SSType.NTEXT == ssType) {
                        qm.precision = 0x3FFFFFFF;
                    } else if (SSType.IMAGE == ssType) {
                        qm.precision = Integer.MAX_VALUE;
                    } else if (SSType.GUID == ssType) {
                        qm.precision = 36;
                    } else if (SSType.TIMESTAMP == ssType) {
                        qm.precision = 8;
                    } else if (SSType.XML == ssType) {
                        qm.precision = 0x3FFFFFFF;
                    }
                    qm.parameterTypeName = ssType.toString();
                }
                if (null == ssType) {
                    throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), null);
                }
                JDBCType jdbcType = ssType.getJDBCType();
                qm.parameterClassName = jdbcType.className();
                qm.parameterType = jdbcType.getIntValue();
                qm.isSigned = SSType.Category.NUMERIC == ssType.category && SSType.BIT != ssType && SSType.TINYINT != ssType;
                this.queryMetaMap.put(paramOrdinal, qm);
            }
            return;
        }
        catch (SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), e);
        }
    }

    private void parseFMTQueryMeta(ResultSetMetaData md, SQLServerFMTQuery f) throws SQLServerException {
        try {
            List<String> columns = f.getColumns();
            List<List<String>> params = f.getValuesList();
            int valueListOffset = 0;
            int mdIndex = 1;
            int mapIndex = 1;
            for (int i = 0; i < columns.size(); ++i) {
                if ("*".equals(columns.get(i))) {
                    for (int j = 0; j < params.get(valueListOffset).size(); ++j) {
                        if (!"?".equals(params.get(valueListOffset).get(j)) || md.isAutoIncrement(mdIndex + j)) continue;
                        QueryMeta qm = this.getQueryMetaFromResultSetMetaData(md, mdIndex + j);
                        this.queryMetaMap.put(mapIndex++, qm);
                        ++i;
                    }
                    mdIndex += params.get(valueListOffset).size();
                    ++valueListOffset;
                    continue;
                }
                QueryMeta qm = this.getQueryMetaFromResultSetMetaData(md, mdIndex);
                this.queryMetaMap.put(mapIndex++, qm);
                ++mdIndex;
            }
        }
        catch (SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), e);
        }
    }

    private QueryMeta getQueryMetaFromResultSetMetaData(ResultSetMetaData md, int index) throws SQLException {
        QueryMeta qm = new QueryMeta();
        qm.parameterClassName = md.getColumnClassName(index);
        qm.parameterType = md.getColumnType(index);
        qm.parameterTypeName = md.getColumnTypeName(index);
        qm.precision = md.getPrecision(index);
        qm.scale = md.getScale(index);
        qm.isNullable = md.isNullable(index);
        qm.isSigned = md.isSigned(index);
        return qm;
    }

    String parseProcIdentifier(String procIdentifier) throws SQLServerException {
        ThreePartName threePartName = ThreePartName.parse(procIdentifier);
        StringBuilder sb = new StringBuilder();
        if (threePartName.getDatabasePart() != null) {
            sb.append("@procedure_qualifier=");
            sb.append(threePartName.getDatabasePart());
            sb.append(", ");
        }
        if (threePartName.getOwnerPart() != null) {
            sb.append("@procedure_owner=");
            sb.append(threePartName.getOwnerPart());
            sb.append(", ");
        }
        if (threePartName.getProcedurePart() != null) {
            sb.append("@procedure_name=");
            sb.append(threePartName.getProcedurePart());
        } else {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, SQLServerException.getErrString("R_noMetadata"), null, false);
        }
        return sb.toString();
    }

    private void checkClosed() throws SQLServerException {
        this.con.checkClosed();
    }

    SQLServerParameterMetaData(SQLServerPreparedStatement st, String sProcString) throws SQLServerException {
        block39: {
            this.procedureIsFound = false;
            this.traceID = " SQLServerParameterMetaData:" + SQLServerParameterMetaData.nextInstanceID();
            this.isTVP = false;
            this.queryMetaMap = null;
            assert (null != st);
            this.stmtParent = st;
            this.con = st.connection;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(this.toString() + " created by (" + st.toString() + ")");
            }
            try {
                if (null != st.procedureName) {
                    String sProc = this.parseProcIdentifier(st.procedureName);
                    try (SQLServerStatement s = (SQLServerStatement)this.con.createStatement(1004, 1007);
                         SQLServerResultSet rsProcedureMeta = s.executeQueryInternal(this.con.isKatmaiOrLater() ? "exec sp_sproc_columns_100 " + sProc + ", @ODBCVer=3, @fUsePattern=0" : "exec sp_sproc_columns " + sProc + ", @ODBCVer=3, @fUsePattern=0");){
                        this.procedureIsFound = rsProcedureMeta.next();
                        rsProcedureMeta.beforeFirst();
                        rsProcedureMeta.getColumn(6).setFilter(new DataTypeFilter());
                        if (this.con.isKatmaiOrLater()) {
                            rsProcedureMeta.getColumn(8).setFilter(new ZeroFixupFilter());
                            rsProcedureMeta.getColumn(9).setFilter(new ZeroFixupFilter());
                            rsProcedureMeta.getColumn(17).setFilter(new ZeroFixupFilter());
                        }
                        this.procMetadata = new ArrayList<Map<String, Object>>();
                        while (rsProcedureMeta.next()) {
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put(DATA_TYPE, rsProcedureMeta.getShort(DATA_TYPE));
                            map.put(COLUMN_TYPE, rsProcedureMeta.getInt(COLUMN_TYPE));
                            map.put(TYPE_NAME, rsProcedureMeta.getString(TYPE_NAME));
                            map.put(PRECISION, rsProcedureMeta.getInt(PRECISION));
                            map.put(SCALE, rsProcedureMeta.getInt(SCALE));
                            map.put(NULLABLE, rsProcedureMeta.getInt(NULLABLE));
                            map.put(SS_TYPE_SCHEMA_NAME, rsProcedureMeta.getString(SS_TYPE_SCHEMA_NAME));
                            this.procMetadata.add(map);
                        }
                        break block39;
                    }
                }
                this.queryMetaMap = new HashMap<Integer, QueryMeta>();
                if (this.con.getServerMajorVersion() >= 11 && !st.getUseFmtOnly()) {
                    String preparedSQL = this.con.replaceParameterMarkers(this.stmtParent.userSQL, this.stmtParent.userSQLParamPositions, this.stmtParent.inOutParam, this.stmtParent.bReturnValueSyntax);
                    try (SQLServerCallableStatement cstmt = (SQLServerCallableStatement)this.con.prepareCall("exec sp_describe_undeclared_parameters ?");){
                        cstmt.setNString(1, preparedSQL);
                        this.parseQueryMeta(cstmt.executeQueryInternal());
                        break block39;
                    }
                }
                SQLServerFMTQuery f = new SQLServerFMTQuery(sProcString);
                try (SQLServerStatement stmt = (SQLServerStatement)this.con.createStatement();
                     ResultSet rs = stmt.executeQuery(f.getFMTQuery());){
                    this.parseFMTQueryMeta(rs.getMetaData(), f);
                }
            }
            catch (SQLServerException e) {
                throw e;
            }
            catch (StringIndexOutOfBoundsException | SQLException e) {
                SQLServerException.makeFromDriverError(this.con, this.stmtParent, e.getMessage(), null, false);
            }
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T t;
        try {
            t = iface.cast(this);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        return t;
    }

    private Map<String, Object> getParameterInfo(int param) {
        if (this.stmtParent.bReturnValueSyntax && this.isTVP) {
            return this.procMetadata.get(param - 1);
        }
        return this.procMetadata.get(param);
    }

    private boolean isValidParamProc(int n) {
        return this.stmtParent.bReturnValueSyntax && this.isTVP && this.procMetadata.size() >= n || this.procMetadata.size() > n;
    }

    private boolean isValidParamQuery(int n) {
        return null != this.queryMetaMap && this.queryMetaMap.containsKey(n);
    }

    private void checkParam(int param) throws SQLServerException {
        if (null == this.procMetadata) {
            if (!this.isValidParamQuery(param)) {
                SQLServerException.makeFromDriverError(this.con, this.stmtParent, SQLServerException.getErrString("R_noMetadata"), null, false);
            }
        } else if (!this.isValidParamProc(param)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidParameterNumber"));
            Object[] msgArgs = new Object[]{param};
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, form.format(msgArgs), null, false);
        }
    }

    @Override
    public String getParameterClassName(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        try {
            if (null == this.procMetadata) {
                return this.queryMetaMap.get((Object)Integer.valueOf((int)param)).parameterClassName;
            }
            return JDBCType.of(((Short)this.getParameterInfo(param).get(DATA_TYPE)).shortValue()).className();
        }
        catch (SQLServerException e) {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, e.getMessage(), null, false);
            return null;
        }
    }

    @Override
    public int getParameterCount() throws SQLServerException {
        this.checkClosed();
        if (null == this.procMetadata) {
            return this.queryMetaMap.size();
        }
        return this.procMetadata.isEmpty() ? 0 : this.procMetadata.size() - 1;
    }

    @Override
    public int getParameterMode(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return 1;
        }
        int n = (Integer)this.getParameterInfo(param).get(COLUMN_TYPE);
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 4;
        }
        return 0;
    }

    @Override
    public int getParameterType(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        int parameterType = 0;
        parameterType = null == this.procMetadata ? this.queryMetaMap.get((Object)Integer.valueOf((int)param)).parameterType : (int)((Short)this.getParameterInfo(param).get(DATA_TYPE)).shortValue();
        if (0 != parameterType) {
            switch (parameterType) {
                case -151: 
                case -150: {
                    parameterType = SSType.DATETIME2.getJDBCType().asJavaSqlType();
                    break;
                }
                case -148: 
                case -146: {
                    parameterType = SSType.DECIMAL.getJDBCType().asJavaSqlType();
                    break;
                }
                case -145: {
                    parameterType = SSType.CHAR.getJDBCType().asJavaSqlType();
                    break;
                }
            }
        }
        return parameterType;
    }

    @Override
    public String getParameterTypeName(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return this.queryMetaMap.get((Object)Integer.valueOf((int)param)).parameterTypeName;
        }
        return this.getParameterInfo(param).get(TYPE_NAME).toString();
    }

    @Override
    public int getPrecision(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return this.queryMetaMap.get((Object)Integer.valueOf((int)param)).precision;
        }
        return (Integer)this.getParameterInfo(param).get(PRECISION);
    }

    @Override
    public int getScale(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return this.queryMetaMap.get((Object)Integer.valueOf((int)param)).scale;
        }
        return (Integer)this.getParameterInfo(param).get(SCALE);
    }

    @Override
    public int isNullable(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (this.procMetadata == null) {
            return this.queryMetaMap.get((Object)Integer.valueOf((int)param)).isNullable;
        }
        return (Integer)this.getParameterInfo(param).get(NULLABLE);
    }

    @Override
    public boolean isSigned(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        try {
            if (null == this.procMetadata) {
                return this.queryMetaMap.get((Object)Integer.valueOf((int)param)).isSigned;
            }
            return JDBCType.of(((Short)this.getParameterInfo(param).get(DATA_TYPE)).shortValue()).isSigned();
        }
        catch (SQLException e) {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, e.getMessage(), null, false);
            return false;
        }
    }

    String getTVPSchemaFromStoredProcedure(int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        return (String)this.getParameterInfo(param).get(SS_TYPE_SCHEMA_NAME);
    }

    class QueryMeta {
        String parameterClassName = null;
        int parameterType = 0;
        String parameterTypeName = null;
        int precision = 0;
        int scale = 0;
        int isNullable = 2;
        boolean isSigned = false;

        QueryMeta() {
        }
    }
}

