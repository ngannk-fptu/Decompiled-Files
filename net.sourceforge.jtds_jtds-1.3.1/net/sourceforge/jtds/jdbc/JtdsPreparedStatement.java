/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.DateTime;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsResultSet;
import net.sourceforge.jtds.jdbc.JtdsResultSetMetaData;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.SQLParser;
import net.sourceforge.jtds.jdbc.Support;

public class JtdsPreparedStatement
extends JtdsStatement
implements PreparedStatement {
    protected final String sql;
    private final String originalSql;
    protected String sqlWord;
    protected String procName;
    protected ParamInfo[] parameters;
    private boolean returnKeys;
    protected ParamInfo[] paramMetaData;
    private static final NumberFormat f = NumberFormat.getInstance();
    Collection handles;

    JtdsPreparedStatement(JtdsConnection connection, String sql, int resultSetType, int concurrency, boolean returnKeys) throws SQLException {
        super(connection, resultSetType, concurrency);
        ArrayList params;
        String[] parsedSql;
        this.originalSql = sql;
        if (this instanceof JtdsCallableStatement) {
            sql = JtdsPreparedStatement.normalizeCall(sql);
        }
        if ((parsedSql = SQLParser.parse(sql, params = new ArrayList(), connection, false))[0].length() == 0) {
            throw new SQLException(Messages.get("error.prepare.nosql"), "07000");
        }
        if (parsedSql[1].length() > 1 && this instanceof JtdsCallableStatement) {
            this.procName = parsedSql[1];
        }
        this.sqlWord = parsedSql[2];
        if (returnKeys) {
            this.sql = connection.getServerType() == 1 && connection.getDatabaseMajorVersion() >= 8 ? parsedSql[0] + " SELECT SCOPE_IDENTITY() AS " + "_JTDS_GENE_R_ATED_KEYS_" : parsedSql[0] + " SELECT @@IDENTITY AS " + "_JTDS_GENE_R_ATED_KEYS_";
            this.returnKeys = true;
        } else {
            this.sql = parsedSql[0];
            this.returnKeys = false;
        }
        this.parameters = params.toArray(new ParamInfo[params.size()]);
    }

    public String toString() {
        return this.originalSql;
    }

    protected static String normalizeCall(String sql) throws SQLException {
        try {
            return JtdsPreparedStatement.normalize(sql, 0);
        }
        catch (SQLException sqle) {
            if (sqle.getSQLState() != null) {
                throw sqle;
            }
            return sql;
        }
    }

    private static String normalize(String sql, int level) throws SQLException {
        String sub;
        if (level > 1) {
            throw new SQLException();
        }
        int len = sql.length();
        int qmark = -1;
        int equal = -1;
        int call = -1;
        block7: for (int i = 0; i < len && call < 0; ++i) {
            while (Character.isWhitespace(sql.charAt(i))) {
                ++i;
            }
            switch (sql.charAt(i)) {
                case '{': {
                    return sql;
                }
                case '?': {
                    if (qmark == -1) {
                        qmark = i;
                        continue block7;
                    }
                    throw new SQLException();
                }
                case '=': {
                    if (equal == -1 && qmark >= 0) {
                        equal = i;
                        continue block7;
                    }
                    throw new SQLException();
                }
                case '-': {
                    if (i + 1 >= len || sql.charAt(i + 1) != '-') continue block7;
                    i += 2;
                    while (i < len && sql.charAt(i) != '\n' && sql.charAt(i) != '\r') {
                        ++i;
                    }
                    continue block7;
                }
                case '/': {
                    if (i + 1 >= len || sql.charAt(i + 1) != '*') continue block7;
                    ++i;
                    int block = 1;
                    do {
                        if (i >= len - 1) {
                            throw new SQLException(Messages.get("error.parsesql.missing", "*/"), "22025");
                        }
                        if (sql.charAt(++i) == '/' && sql.charAt(i + 1) == '*') {
                            ++i;
                            ++block;
                            continue;
                        }
                        if (sql.charAt(i) != '*' || sql.charAt(i + 1) != '/') continue;
                        ++i;
                        --block;
                    } while (block > 0);
                    continue block7;
                }
                default: {
                    if (len - i > 4 && sql.substring(i, i + 5).equalsIgnoreCase("exec ") || sql.substring(i, i + 5).equalsIgnoreCase("call ")) {
                        return JtdsPreparedStatement.normalize(sql.substring(0, i) + sql.substring(i + 4, sql.length()), level++);
                    }
                    if (len - i > 7 && sql.substring(i, i + 8).equalsIgnoreCase("execute ")) {
                        return JtdsPreparedStatement.normalize(sql.substring(0, i) + sql.substring(i + 7, sql.length()), level++);
                    }
                    call = i;
                }
            }
        }
        if (equal == -1 && qmark != -1) {
            throw new SQLException();
        }
        if (call + 7 < len && (sub = sql.substring(call, call + 7)) != null && (sub.equalsIgnoreCase("insert ") || sub.equalsIgnoreCase("update ") || sub.equalsIgnoreCase("delete "))) {
            throw new SQLException(Messages.get("error.parsesql.noprocedurecall"), "07000");
        }
        return "{" + sql.substring(0, call) + "call " + sql.substring(call) + (JtdsPreparedStatement.openComment(sql, call) ? "\n" : "") + "}";
    }

    private static boolean openComment(String sql, int offset) throws SQLException {
        int len = sql.length();
        block4: for (int i = offset; i < len; ++i) {
            switch (sql.charAt(i)) {
                case '-': {
                    if (i + 1 >= len || sql.charAt(i + 1) != '-') continue block4;
                    i += 2;
                    while (i < len && sql.charAt(i) != '\n' && sql.charAt(i) != '\r') {
                        ++i;
                    }
                    if (i != len) continue block4;
                    return true;
                }
                case '/': {
                    if (i + 1 >= len || sql.charAt(i + 1) != '*') continue block4;
                    ++i;
                    int block = 1;
                    do {
                        if (i >= len - 1) {
                            throw new SQLException(Messages.get("error.parsesql.missing", "*/"), "22025");
                        }
                        if (sql.charAt(++i) == '/' && sql.charAt(i + 1) == '*') {
                            ++i;
                            ++block;
                            continue;
                        }
                        if (sql.charAt(i) != '*' || sql.charAt(i + 1) != '/') continue;
                        ++i;
                        --block;
                    } while (block > 0);
                }
            }
        }
        return false;
    }

    @Override
    protected void checkOpen() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", "PreparedStatement"), "HY010");
        }
    }

    protected void notSupported(String method) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notsup", method), "HYC00");
    }

    @Override
    protected SQLException executeMSBatch(int size, int executeSize, ArrayList counts) throws SQLException {
        int i;
        if (this.parameters.length == 0) {
            return super.executeMSBatch(size, executeSize, counts);
        }
        SQLException sqlEx = null;
        String[] procHandle = null;
        if (this.connection.getPrepareSql() == 1 || this.connection.getPrepareSql() == 3) {
            procHandle = new String[size];
            for (i = 0; i < size; ++i) {
                procHandle[i] = this.connection.prepareSQL(this, this.sql, (ParamInfo[])this.batchValues.get(i), false, false);
            }
        }
        i = 0;
        while (i < size) {
            Object value = this.batchValues.get(i);
            String proc = procHandle == null ? this.procName : procHandle[i];
            boolean executeNow = ++i % executeSize == 0 || i == size;
            this.tds.startBatch();
            this.tds.executeSQL(this.sql, proc, (ParamInfo[])value, false, 0, -1, -1, executeNow);
            if (!executeNow || (sqlEx = this.tds.getBatchCounts(counts, sqlEx)) == null || counts.size() == i) continue;
            break;
        }
        return sqlEx;
    }

    @Override
    protected SQLException executeSybaseBatch(int size, int executeSize, ArrayList counts) throws SQLException {
        if (this.parameters.length == 0) {
            return super.executeSybaseBatch(size, executeSize, counts);
        }
        int maxParams = this.connection.getDatabaseMajorVersion() < 12 || this.connection.getDatabaseMajorVersion() == 12 && this.connection.getDatabaseMinorVersion() < 50 ? 200 : 1000;
        StringBuilder sqlBuf = new StringBuilder(size * 32);
        SQLException sqlEx = null;
        if (this.parameters.length * executeSize > maxParams && (executeSize = maxParams / this.parameters.length) == 0) {
            executeSize = 1;
        }
        ArrayList<ParamInfo> paramList = new ArrayList<ParamInfo>();
        int i = 0;
        while (i < size) {
            Object value = this.batchValues.get(i);
            boolean executeNow = ++i % executeSize == 0 || i == size;
            int offset = sqlBuf.length();
            sqlBuf.append(this.sql).append(' ');
            for (int n = 0; n < this.parameters.length; ++n) {
                ParamInfo p = ((ParamInfo[])value)[n];
                p.markerPos += offset;
                paramList.add(p);
            }
            if (!executeNow) continue;
            ParamInfo[] args = paramList.toArray(new ParamInfo[paramList.size()]);
            this.tds.executeSQL(sqlBuf.toString(), null, args, false, 0, -1, -1, true);
            sqlBuf.setLength(0);
            paramList.clear();
            if ((sqlEx = this.tds.getBatchCounts(counts, sqlEx)) == null || counts.size() == i) continue;
            break;
        }
        return sqlEx;
    }

    protected ParamInfo getParameter(int parameterIndex) throws SQLException {
        this.checkOpen();
        if (parameterIndex < 1 || parameterIndex > this.parameters.length) {
            throw new SQLException(Messages.get("error.prepare.paramindex", Integer.toString(parameterIndex)), "07009");
        }
        return this.parameters[parameterIndex - 1];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setObjectBase(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        this.checkOpen();
        int length = 0;
        if (targetSqlType == 2005) {
            targetSqlType = -1;
        } else if (targetSqlType == 2004) {
            targetSqlType = -4;
        }
        if (x != null) {
            x = Support.convert(this, x, targetSqlType, this.connection.getCharset());
            if (scale >= 0) {
                if (x instanceof BigDecimal) {
                    x = ((BigDecimal)x).setScale(scale, 4);
                } else if (x instanceof Number) {
                    NumberFormat numberFormat = f;
                    synchronized (numberFormat) {
                        f.setGroupingUsed(false);
                        f.setMaximumFractionDigits(scale);
                        x = Support.convert(this, f.format(x), targetSqlType, this.connection.getCharset());
                    }
                }
            }
            if (x instanceof Blob) {
                Blob blob = (Blob)x;
                length = (int)blob.length();
                x = blob.getBinaryStream();
            } else if (x instanceof Clob) {
                Clob clob = (Clob)x;
                length = (int)clob.length();
                x = clob.getCharacterStream();
            }
        }
        this.setParameter(parameterIndex, x, targetSqlType, scale, length);
    }

    protected void setParameter(int parameterIndex, Object x, int targetSqlType, int scale, int length) throws SQLException {
        ParamInfo pi = this.getParameter(parameterIndex);
        if ("ERROR".equals(Support.getJdbcTypeName(targetSqlType))) {
            throw new SQLException(Messages.get("error.generic.badtype", Integer.toString(targetSqlType)), "HY092");
        }
        if (targetSqlType == 3 || targetSqlType == 2) {
            pi.precision = this.connection.getMaxPrecision();
            if (x instanceof BigDecimal) {
                x = Support.normalizeBigDecimal((BigDecimal)x, pi.precision);
                pi.scale = ((BigDecimal)x).scale();
            } else {
                pi.scale = scale < 0 ? 10 : scale;
            }
        } else {
            int n = pi.scale = scale < 0 ? 0 : scale;
        }
        pi.length = x instanceof String ? ((String)x).length() : (x instanceof byte[] ? ((byte[])x).length : length);
        if (x instanceof Date) {
            x = new DateTime((Date)x);
        } else if (x instanceof Time) {
            x = new DateTime((Time)x);
        } else if (x instanceof Timestamp) {
            x = new DateTime((Timestamp)x);
        }
        pi.value = x;
        pi.jdbcType = targetSqlType;
        pi.isSet = true;
        pi.isUnicode = this.connection.getUseUnicode();
    }

    void setColMetaData(ColInfo[] value) {
        this.colMetaData = value;
    }

    void setParamMetaData(ParamInfo[] value) {
        for (int i = 0; i < value.length && i < this.parameters.length; ++i) {
            if (this.parameters[i].isSet) continue;
            this.parameters[i].jdbcType = value[i].jdbcType;
            this.parameters[i].isOutput = value[i].isOutput;
            this.parameters[i].precision = value[i].precision;
            this.parameters[i].scale = value[i].scale;
            this.parameters[i].sqlType = value[i].sqlType;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        try {
            super.close();
        }
        finally {
            this.handles = null;
            this.parameters = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int executeUpdate() throws SQLException {
        this.checkOpen();
        this.reset();
        if (this.procName == null && !(this instanceof JtdsCallableStatement)) {
            JtdsConnection jtdsConnection = this.connection;
            synchronized (jtdsConnection) {
                String spName = this.connection.prepareSQL(this, this.sql, this.parameters, this.returnKeys, false);
                this.executeSQL(this.sql, spName, this.parameters, true, false);
            }
        } else {
            this.executeSQL(this.sql, this.procName, this.parameters, true, false);
        }
        int res = this.getUpdateCount();
        return res == -1 ? 0 : res;
    }

    @Override
    public void addBatch() throws SQLException {
        this.checkOpen();
        if (this.batchValues == null) {
            this.batchValues = new ArrayList();
        }
        if (this.parameters.length == 0) {
            this.batchValues.add(this.sql);
        } else {
            this.batchValues.add(this.parameters);
            ParamInfo[] tmp = new ParamInfo[this.parameters.length];
            for (int i = 0; i < this.parameters.length; ++i) {
                tmp[i] = (ParamInfo)this.parameters[i].clone();
            }
            this.parameters = tmp;
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        this.checkOpen();
        for (int i = 0; i < this.parameters.length; ++i) {
            this.parameters[i].clearInValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean execute() throws SQLException {
        this.checkOpen();
        this.reset();
        boolean useCursor = this.useCursor(this.returnKeys, this.sqlWord);
        if (this.procName == null && !(this instanceof JtdsCallableStatement)) {
            JtdsConnection jtdsConnection = this.connection;
            synchronized (jtdsConnection) {
                String spName = this.connection.prepareSQL(this, this.sql, this.parameters, this.returnKeys, useCursor);
                return this.executeSQL(this.sql, spName, this.parameters, false, useCursor);
            }
        }
        return this.executeSQL(this.sql, this.procName, this.parameters, false, useCursor);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.setParameter(parameterIndex, new Integer(x & 0xFF), -6, 0, 0);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.setParameter(parameterIndex, new Double(x), 8, 0, 0);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.setParameter(parameterIndex, new Float(x), 7, 0, 0);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.setParameter(parameterIndex, new Integer(x), 4, 0, 0);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        if (sqlType == 2005) {
            sqlType = -1;
        } else if (sqlType == 2004) {
            sqlType = -4;
        }
        this.setParameter(parameterIndex, null, sqlType, -1, 0);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.setParameter(parameterIndex, new Long(x), -5, 0, 0);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.setParameter(parameterIndex, new Integer(x), 5, 0, 0);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.setParameter(parameterIndex, x ? Boolean.TRUE : Boolean.FALSE, 16, 0, 0);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.setParameter(parameterIndex, x, -2, 0, 0);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream inputStream, int length) throws SQLException {
        if (inputStream == null || length < 0) {
            this.setParameter(parameterIndex, null, -1, 0, 0);
        } else {
            try {
                this.setCharacterStream(parameterIndex, (Reader)new InputStreamReader(inputStream, "US-ASCII"), length);
            }
            catch (UnsupportedEncodingException e) {
                // empty catch block
            }
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.checkOpen();
        if (x == null || length < 0) {
            this.setBytes(parameterIndex, null);
        } else {
            this.setParameter(parameterIndex, x, -4, 0, length);
        }
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream inputStream, int length) throws SQLException {
        this.checkOpen();
        if (inputStream == null || length < 0) {
            this.setString(parameterIndex, null);
        } else {
            try {
                char[] tmp = new char[length /= 2];
                int pos = 0;
                int b1 = inputStream.read();
                int b2 = inputStream.read();
                while (b1 >= 0 && b2 >= 0 && pos < length) {
                    tmp[pos++] = (char)(b1 << 8 & 0xFF00 | b2 & 0xFF);
                    b1 = inputStream.read();
                    b2 = inputStream.read();
                }
                this.setString(parameterIndex, new String(tmp, 0, pos));
            }
            catch (IOException e) {
                throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
            }
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        if (reader == null || length < 0) {
            this.setParameter(parameterIndex, null, -1, 0, 0);
        } else {
            this.setParameter(parameterIndex, reader, -1, 0, length);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.setObjectBase(parameterIndex, x, Support.getJdbcType(x), -1);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.setObjectBase(parameterIndex, x, targetSqlType, -1);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        this.checkOpen();
        if (scale < 0 || scale > this.connection.getMaxPrecision()) {
            throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
        }
        this.setObjectBase(parameterIndex, x, targetSqlType, scale);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        JtdsPreparedStatement.notImplemented("PreparedStatement.setNull(int, int, String)");
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        this.setParameter(parameterIndex, x, 12, 0, 0);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.setParameter(parameterIndex, x, 3, -1, 0);
    }

    @Override
    public void setURL(int parameterIndex, URL url) throws SQLException {
        this.setString(parameterIndex, url == null ? null : url.toString());
    }

    @Override
    public void setArray(int arg0, Array arg1) throws SQLException {
        JtdsPreparedStatement.notImplemented("PreparedStatement.setArray");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        if (x == null) {
            this.setBytes(parameterIndex, null);
        } else {
            long length = x.length();
            if (length > Integer.MAX_VALUE) {
                throw new SQLException(Messages.get("error.resultset.longblob"), "24000");
            }
            this.setBinaryStream(parameterIndex, x.getBinaryStream(), (int)x.length());
        }
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        if (x == null) {
            this.setString(parameterIndex, null);
        } else {
            long length = x.length();
            if (length > Integer.MAX_VALUE) {
                throw new SQLException(Messages.get("error.resultset.longclob"), "24000");
            }
            this.setCharacterStream(parameterIndex, x.getCharacterStream(), (int)x.length());
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.setParameter(parameterIndex, x, 91, 0, 0);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkOpen();
        if (this.connection.getServerType() == 2) {
            this.connection.prepareSQL(this, this.sql, new ParamInfo[0], false, false);
        }
        try {
            Class<?> pmdClass = Class.forName("net.sourceforge.jtds.jdbc.ParameterMetaDataImpl");
            Class[] parameterTypes = new Class[]{ParamInfo[].class, JtdsConnection.class};
            Object[] arguments = new Object[]{this.parameters, this.connection};
            Constructor<?> pmdConstructor = pmdClass.getConstructor(parameterTypes);
            return (ParameterMetaData)pmdConstructor.newInstance(arguments);
        }
        catch (Exception e) {
            JtdsPreparedStatement.notImplemented("PreparedStatement.getParameterMetaData");
            return null;
        }
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        JtdsPreparedStatement.notImplemented("PreparedStatement.setRef");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet executeQuery() throws SQLException {
        this.checkOpen();
        this.reset();
        boolean useCursor = this.useCursor(false, null);
        if (this.procName == null && !(this instanceof JtdsCallableStatement)) {
            JtdsConnection jtdsConnection = this.connection;
            synchronized (jtdsConnection) {
                String spName = this.connection.prepareSQL(this, this.sql, this.parameters, false, useCursor);
                return this.executeSQLQuery(this.sql, spName, this.parameters, useCursor);
            }
        }
        return this.executeSQLQuery(this.sql, this.procName, this.parameters, useCursor);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        if (this.colMetaData == null) {
            if (this.currentResult != null) {
                this.colMetaData = this.currentResult.columns;
            } else if (this.connection.getServerType() == 2) {
                this.connection.prepareSQL(this, this.sql, new ParamInfo[0], false, false);
            } else if ("select".equals(this.sqlWord) || "with".equals(this.sqlWord)) {
                ParamInfo[] params = new ParamInfo[this.parameters.length];
                for (int i = 0; i < params.length; ++i) {
                    params[i] = new ParamInfo(this.parameters[i].markerPos, false);
                    params[i].isSet = true;
                }
                StringBuilder testSql = new StringBuilder(this.sql.length() + 128);
                testSql.append("SET FMTONLY ON; ");
                testSql.append(Support.substituteParameters(this.sql, params, this.connection));
                testSql.append("\r\n; SET FMTONLY OFF");
                try {
                    this.tds.submitSQL(testSql.toString());
                    this.colMetaData = this.tds.getColumns();
                }
                catch (SQLException e) {
                    this.tds.submitSQL("SET FMTONLY OFF");
                }
            }
        }
        return this.colMetaData == null ? null : new JtdsResultSetMetaData(this.colMetaData, JtdsResultSet.getColumnCount(this.colMetaData), this.connection.getUseLOBs());
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.setParameter(parameterIndex, x, 92, 0, 0);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.setParameter(parameterIndex, x, 93, 0, 0);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        if (x != null && cal != null) {
            x = new Date(Support.timeFromZone(x, cal));
        }
        this.setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        if (x != null && cal != null) {
            x = new Time(Support.timeFromZone(x, cal));
        }
        this.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        if (x != null && cal != null) {
            x = new Timestamp(Support.timeFromZone(x, cal));
        }
        this.setTimestamp(parameterIndex, x);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        this.notSupported("executeUpdate(String)");
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        this.notSupported("executeBatch(String)");
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        this.notSupported("execute(String)");
        return false;
    }

    @Override
    public int executeUpdate(String sql, int getKeys) throws SQLException {
        this.notSupported("executeUpdate(String, int)");
        return 0;
    }

    @Override
    public boolean execute(String arg0, int arg1) throws SQLException {
        this.notSupported("execute(String, int)");
        return false;
    }

    @Override
    public int executeUpdate(String arg0, int[] arg1) throws SQLException {
        this.notSupported("executeUpdate(String, int[])");
        return 0;
    }

    @Override
    public boolean execute(String arg0, int[] arg1) throws SQLException {
        this.notSupported("execute(String, int[])");
        return false;
    }

    @Override
    public int executeUpdate(String arg0, String[] arg1) throws SQLException {
        this.notSupported("executeUpdate(String, String[])");
        return 0;
    }

    @Override
    public boolean execute(String arg0, String[] arg1) throws SQLException {
        this.notSupported("execute(String, String[])");
        return false;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        this.notSupported("executeQuery(String)");
        return null;
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new AbstractMethodError();
    }
}

