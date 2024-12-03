/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import net.sourceforge.jtds.jdbc.CachedResultSet;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbc.JtdsResultSet;
import net.sourceforge.jtds.jdbc.MSCursorResultSet;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.SQLDiagnostic;
import net.sourceforge.jtds.jdbc.SQLParser;
import net.sourceforge.jtds.jdbc.TdsCore;

public class JtdsStatement
implements Statement {
    static final String GENKEYCOL = "_JTDS_GENE_R_ATED_KEYS_";
    static final int RETURN_GENERATED_KEYS = 1;
    static final int NO_GENERATED_KEYS = 2;
    static final int CLOSE_CURRENT_RESULT = 1;
    static final int KEEP_CURRENT_RESULT = 2;
    static final int CLOSE_ALL_RESULTS = 3;
    static final int BOOLEAN = 16;
    static final int DATALINK = 70;
    static final Integer SUCCESS_NO_INFO = new Integer(-2);
    static final Integer EXECUTE_FAILED = new Integer(-3);
    static final int DEFAULT_FETCH_SIZE = 100;
    protected JtdsConnection connection;
    protected TdsCore tds;
    protected int queryTimeout;
    protected JtdsResultSet currentResult;
    private int updateCount = -1;
    protected int fetchDirection = 1000;
    protected int resultSetType = 1003;
    protected int resultSetConcurrency = 1007;
    protected int fetchSize = 100;
    protected String cursorName;
    protected int maxFieldSize;
    protected int maxRows;
    protected boolean escapeProcessing = true;
    protected final SQLDiagnostic messages;
    protected ArrayList batchValues;
    protected CachedResultSet genKeyResultSet;
    protected final LinkedList resultQueue = new LinkedList();
    protected ArrayList openResultSets;
    protected ColInfo[] colMetaData;
    private final AtomicInteger _Closed = new AtomicInteger();

    JtdsStatement(JtdsConnection connection, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (resultSetType < 1003 || resultSetType > 1006) {
            String method = this instanceof JtdsCallableStatement ? "prepareCall" : (this instanceof JtdsPreparedStatement ? "prepareStatement" : "createStatement");
            throw new SQLException(Messages.get("error.generic.badparam", "resultSetType", method), "HY092");
        }
        if (resultSetConcurrency < 1007 || resultSetConcurrency > 1010) {
            String method = this instanceof JtdsCallableStatement ? "prepareCall" : (this instanceof JtdsPreparedStatement ? "prepareStatement" : "createStatement");
            throw new SQLException(Messages.get("error.generic.badparam", "resultSetConcurrency", method), "HY092");
        }
        this.connection = connection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.tds = connection.getCachedTds();
        if (this.tds == null) {
            this.messages = new SQLDiagnostic(connection.getServerType());
            this.tds = new TdsCore(this.connection, this.messages);
        } else {
            this.messages = this.tds.getMessages();
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        try {
            this.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    TdsCore getTds() {
        return this.tds;
    }

    SQLDiagnostic getMessages() {
        return this.messages;
    }

    protected void checkOpen() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", "Statement"), "HY010");
        }
    }

    protected void checkCursorException(SQLException e) throws SQLException {
        if (this.connection == null || this.connection.isClosed() || "HYT00".equals(e.getSQLState()) || "HY008".equals(e.getSQLState())) {
            throw e;
        }
        if (this.connection.getServerType() == 2) {
            return;
        }
        int error = e.getErrorCode();
        if (error >= 16900 && error <= 16999) {
            return;
        }
        if (error == 6819) {
            return;
        }
        if (error == 8654) {
            return;
        }
        if (error == 8162) {
            return;
        }
        throw e;
    }

    static void notImplemented(String method) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", method), "HYC00");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void closeCurrentResultSet() throws SQLException {
        try {
            if (this.currentResult != null) {
                this.currentResult.close();
            }
        }
        finally {
            this.currentResult = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void closeAllResultSets() throws SQLException {
        try {
            if (this.openResultSets != null) {
                for (int i = 0; i < this.openResultSets.size(); ++i) {
                    JtdsResultSet rs = (JtdsResultSet)this.openResultSets.get(i);
                    if (rs == null) continue;
                    rs.close();
                }
            }
            this.closeCurrentResultSet();
        }
        finally {
            this.openResultSets = null;
        }
    }

    void addWarning(SQLWarning w) {
        this.messages.addWarning(w);
    }

    protected SQLException executeMSBatch(int size, int executeSize, ArrayList counts) throws SQLException {
        SQLException sqlEx = null;
        int i = 0;
        while (i < size) {
            Object value = this.batchValues.get(i);
            boolean executeNow = ++i % executeSize == 0 || i == size;
            this.tds.startBatch();
            this.tds.executeSQL((String)value, null, null, false, 0, -1, -1, executeNow);
            if (!executeNow || (sqlEx = this.tds.getBatchCounts(counts, sqlEx)) == null || counts.size() == i) continue;
            break;
        }
        return sqlEx;
    }

    protected SQLException executeSybaseBatch(int size, int executeSize, ArrayList counts) throws SQLException {
        StringBuilder sql = new StringBuilder(size * 32);
        SQLException sqlEx = null;
        int i = 0;
        while (i < size) {
            Object value = this.batchValues.get(i);
            boolean executeNow = ++i % executeSize == 0 || i == size;
            sql.append((String)value).append(' ');
            if (!executeNow) continue;
            this.tds.executeSQL(sql.toString(), null, null, false, 0, -1, -1, true);
            sql.setLength(0);
            if ((sqlEx = this.tds.getBatchCounts(counts, sqlEx)) == null || counts.size() == i) continue;
            break;
        }
        return sqlEx;
    }

    protected ResultSet executeSQLQuery(String sql, String spName, ParamInfo[] params, boolean useCursor) throws SQLException {
        String warningMessage = null;
        if (useCursor) {
            try {
                if (this.connection.getServerType() == 1) {
                    this.currentResult = new MSCursorResultSet(this, sql, spName, params, this.resultSetType, this.resultSetConcurrency);
                    return this.currentResult;
                }
                this.currentResult = new CachedResultSet(this, sql, spName, params, this.resultSetType, this.resultSetConcurrency);
                return this.currentResult;
            }
            catch (SQLException e) {
                this.checkCursorException(e);
                warningMessage = '[' + e.getSQLState() + "] " + e.getMessage();
            }
        }
        if (spName != null && this.connection.getUseMetadataCache() && this.connection.getPrepareSql() == 3 && this.colMetaData != null && this.connection.getServerType() == 1) {
            this.tds.setColumns(this.colMetaData);
            this.tds.executeSQL(sql, spName, params, true, this.queryTimeout, this.maxRows, this.maxFieldSize, true);
        } else {
            this.tds.executeSQL(sql, spName, params, false, this.queryTimeout, this.maxRows, this.maxFieldSize, true);
        }
        if (warningMessage != null) {
            this.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", warningMessage), "01000"));
        }
        while (!this.tds.getMoreResults() && !this.tds.isEndOfResponse()) {
        }
        this.messages.checkErrors();
        if (!this.tds.isResultSet()) {
            throw new SQLException(Messages.get("error.statement.noresult"), "24000");
        }
        this.currentResult = new JtdsResultSet(this, 1003, 1007, this.tds.getColumns());
        return this.currentResult;
    }

    protected boolean executeSQL(String sql, String spName, ParamInfo[] params, boolean update, boolean useCursor) throws SQLException {
        String warningMessage = null;
        if (this.connection.getServerType() == 1 && !update && useCursor) {
            try {
                this.currentResult = new MSCursorResultSet(this, sql, spName, params, this.resultSetType, this.resultSetConcurrency);
                return true;
            }
            catch (SQLException e) {
                this.checkCursorException(e);
                warningMessage = '[' + e.getSQLState() + "] " + e.getMessage();
            }
        }
        this.tds.executeSQL(sql, spName, params, false, this.queryTimeout, this.maxRows, this.maxFieldSize, true);
        if (warningMessage != null) {
            this.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", warningMessage), "01000"));
        }
        if (this.processResults(update)) {
            Object nextResult = this.resultQueue.removeFirst();
            if (nextResult instanceof Integer) {
                this.updateCount = (Integer)nextResult;
                return false;
            }
            this.currentResult = (JtdsResultSet)nextResult;
            return true;
        }
        return false;
    }

    private boolean processResults(boolean update) throws SQLException {
        if (!this.resultQueue.isEmpty()) {
            throw new IllegalStateException("There should be no queued results.");
        }
        while (!this.tds.isEndOfResponse()) {
            if (!this.tds.getMoreResults()) {
                if (!this.tds.isUpdateCount()) continue;
                if (update && this.connection.getLastUpdateCount()) {
                    this.resultQueue.clear();
                }
                this.resultQueue.addLast(new Integer(this.tds.getUpdateCount()));
                continue;
            }
            ColInfo[] columns = this.tds.getColumns();
            if (columns.length == 1 && columns[0].name.equals(GENKEYCOL)) {
                columns[0].name = "ID";
                this.genKeyResultSet = null;
                while (this.tds.getNextRow()) {
                    if (this.genKeyResultSet == null) {
                        this.genKeyResultSet = new CachedResultSet(this, this.tds.getColumns(), this.tds.getRowData());
                        continue;
                    }
                    this.genKeyResultSet.addRow(this.tds.getRowData());
                }
                continue;
            }
            if (update && this.resultQueue.isEmpty()) {
                SQLException ex = new SQLException(Messages.get("error.statement.nocount"), "07000");
                ex.setNextException(this.messages.exceptions);
                throw ex;
            }
            Object[] computed = this.tds.getComputedRowData();
            if (computed != null) {
                this.resultQueue.add(new CachedResultSet(this, this.tds.getComputedColumns(), computed));
                break;
            }
            this.resultQueue.add(new JtdsResultSet(this, 1003, 1007, this.tds.getColumns()));
            break;
        }
        this.getMessages().checkErrors();
        return !this.resultQueue.isEmpty();
    }

    protected void cacheResults() throws SQLException {
        this.processResults(false);
    }

    protected void reset() throws SQLException {
        this.updateCount = -1;
        this.resultQueue.clear();
        this.genKeyResultSet = null;
        this.tds.clearResponseQueue();
        this.messages.clearWarnings();
        this.messages.exceptions = null;
        this.closeAllResultSets();
    }

    private boolean executeImpl(String sql, int autoGeneratedKeys, boolean update) throws SQLException {
        boolean returnKeys;
        this.reset();
        if (sql == null || sql.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        String sqlWord = "";
        if (this.escapeProcessing) {
            String[] tmp = SQLParser.parse(sql, null, this.connection, false);
            if (tmp[1].length() != 0) {
                throw new SQLException(Messages.get("error.statement.badsql"), "07000");
            }
            sql = tmp[0];
            sqlWord = tmp[2];
        } else if ((sql = sql.trim()).length() > 5) {
            sqlWord = sql.substring(0, 6).toLowerCase();
        }
        if (autoGeneratedKeys == 1) {
            returnKeys = true;
        } else if (autoGeneratedKeys == 2) {
            returnKeys = false;
        } else {
            throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(autoGeneratedKeys), "autoGeneratedKeys"), "HY092");
        }
        if (returnKeys) {
            sql = this.connection.getServerType() == 1 && this.connection.getDatabaseMajorVersion() >= 8 ? sql + " SELECT SCOPE_IDENTITY() AS _JTDS_GENE_R_ATED_KEYS_" : sql + " SELECT @@IDENTITY AS _JTDS_GENE_R_ATED_KEYS_";
        }
        return this.executeSQL(sql, null, null, update, !update && this.useCursor(returnKeys, sqlWord));
    }

    protected boolean useCursor(boolean returnKeys, String sqlWord) {
        return !(this.resultSetType == 1003 && this.resultSetConcurrency == 1007 && !this.connection.getUseCursors() && this.cursorName == null || returnKeys || sqlWord != null && !"select".equals(sqlWord) && !sqlWord.startsWith("exec"));
    }

    int getDefaultFetchSize() {
        return 0 < this.maxRows && this.maxRows < 100 ? this.maxRows : 100;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        this.checkOpen();
        return this.fetchDirection;
    }

    @Override
    public int getFetchSize() throws SQLException {
        this.checkOpen();
        return this.fetchSize;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        this.checkOpen();
        return this.maxFieldSize;
    }

    @Override
    public int getMaxRows() throws SQLException {
        this.checkOpen();
        return this.maxRows;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        this.checkOpen();
        return this.queryTimeout;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        this.checkOpen();
        return this.resultSetConcurrency;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        this.checkOpen();
        return 1;
    }

    @Override
    public int getResultSetType() throws SQLException {
        this.checkOpen();
        return this.resultSetType;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        this.checkOpen();
        return this.updateCount;
    }

    @Override
    public void cancel() throws SQLException {
        this.checkOpen();
        if (this.tds != null) {
            this.tds.cancel(false);
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        this.checkOpen();
        if (this.batchValues != null) {
            this.batchValues.clear();
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.checkOpen();
        this.messages.clearWarnings();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        if (this._Closed.compareAndSet(0, 1)) {
            SQLException releaseEx = null;
            try {
                this.reset();
                try {
                    if (!this.connection.isClosed()) {
                        this.connection.releaseTds(this.tds);
                    }
                    this.tds.getMessages().checkErrors();
                }
                catch (SQLException ex) {
                    releaseEx = ex;
                }
                finally {
                    this._Closed.set(2);
                    this.tds = null;
                    this.connection.removeStatement(this);
                    this.connection = null;
                }
            }
            finally {
                if (releaseEx != null) {
                    throw releaseEx;
                }
            }
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        this.checkOpen();
        return this.getMoreResults(3);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] executeBatch() throws SQLException, BatchUpdateException {
        this.checkOpen();
        this.reset();
        if (this.batchValues == null || this.batchValues.size() == 0) {
            return new int[0];
        }
        int size = this.batchValues.size();
        int executeSize = this.connection.getBatchSize();
        executeSize = executeSize == 0 ? Integer.MAX_VALUE : executeSize;
        ArrayList counts = new ArrayList(size);
        try {
            int i;
            SQLException sqlEx;
            JtdsConnection jtdsConnection = this.connection;
            synchronized (jtdsConnection) {
                sqlEx = this.connection.getServerType() == 2 && this.connection.getTdsVersion() == 2 ? this.executeSybaseBatch(size, executeSize, counts) : this.executeMSBatch(size, executeSize, counts);
            }
            int[] updateCounts = new int[size];
            int results = counts.size();
            for (i = 0; i < results && i < size; ++i) {
                updateCounts[i] = (Integer)counts.get(i);
            }
            for (i = results; i < updateCounts.length; ++i) {
                updateCounts[i] = EXECUTE_FAILED;
            }
            if (sqlEx != null) {
                BatchUpdateException batchEx = new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
                batchEx.setNextException(sqlEx.getNextException());
                throw batchEx;
            }
            int[] nArray = updateCounts;
            return nArray;
        }
        catch (BatchUpdateException ex) {
            throw ex;
        }
        catch (SQLException ex) {
            throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), new int[0]);
        }
        finally {
            this.clearBatch();
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.checkOpen();
        switch (direction) {
            case 1000: 
            case 1001: 
            case 1002: {
                this.fetchDirection = direction;
                break;
            }
            default: {
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(direction), "direction"), "24000");
            }
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.checkOpen();
        if (rows < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", "setFetchSize"), "HY092");
        }
        if (this.maxRows > 0 && rows > this.maxRows) {
            throw new SQLException(Messages.get("error.statement.gtmaxrows"), "HY092");
        }
        if (rows == 0) {
            rows = this.getDefaultFetchSize();
        }
        this.fetchSize = rows;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.checkOpen();
        if (max < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", "setMaxFieldSize"), "HY092");
        }
        this.maxFieldSize = max;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.checkOpen();
        if (max < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", "setMaxRows"), "HY092");
        }
        if (max > 0 && max < this.fetchSize) {
            this.fetchSize = max;
        }
        this.maxRows = max;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.checkOpen();
        if (seconds < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", "setQueryTimeout"), "HY092");
        }
        this.queryTimeout = seconds;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        this.checkOpen();
        switch (current) {
            case 3: {
                this.updateCount = -1;
                this.closeAllResultSets();
                break;
            }
            case 1: {
                this.updateCount = -1;
                this.closeCurrentResultSet();
                break;
            }
            case 2: {
                this.updateCount = -1;
                if (this.openResultSets == null) {
                    this.openResultSets = new ArrayList();
                }
                if (this.currentResult instanceof MSCursorResultSet || this.currentResult instanceof CachedResultSet) {
                    this.openResultSets.add(this.currentResult);
                } else if (this.currentResult != null) {
                    this.currentResult.cacheResultSetRows();
                    this.openResultSets.add(this.currentResult);
                }
                this.currentResult = null;
                break;
            }
            default: {
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(current), "current"), "HY092");
            }
        }
        this.messages.checkErrors();
        if (!this.resultQueue.isEmpty() || this.processResults(false)) {
            Object nextResult = this.resultQueue.removeFirst();
            if (nextResult instanceof Integer) {
                this.updateCount = (Integer)nextResult;
                return false;
            }
            this.currentResult = (JtdsResultSet)nextResult;
            return true;
        }
        return false;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.checkOpen();
        this.escapeProcessing = enable;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return this.executeUpdate(sql, 2);
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        this.checkOpen();
        if (sql == null) {
            throw new NullPointerException();
        }
        if (this.batchValues == null) {
            this.batchValues = new ArrayList();
        }
        if (this.escapeProcessing) {
            String[] tmp = SQLParser.parse(sql, null, this.connection, false);
            if (tmp[1].length() != 0) {
                throw new SQLException(Messages.get("error.statement.badsql"), "07000");
            }
            sql = tmp[0];
        }
        this.batchValues.add(sql);
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        this.checkOpen();
        this.cursorName = name;
        if (name != null) {
            this.resultSetType = 1003;
            this.fetchSize = 1;
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        this.checkOpen();
        return this.executeImpl(sql, 2, false);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        this.executeImpl(sql, autoGeneratedKeys, true);
        int res = this.getUpdateCount();
        return res == -1 ? 0 : res;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        return this.executeImpl(sql, autoGeneratedKeys, false);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        this.checkOpen();
        if (columnIndexes == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", "executeUpdate"), "HY092");
        }
        if (columnIndexes.length != 1) {
            throw new SQLException(Messages.get("error.generic.needcolindex", "executeUpdate"), "HY092");
        }
        return this.executeUpdate(sql, 1);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        this.checkOpen();
        if (columnIndexes == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", "execute"), "HY092");
        }
        if (columnIndexes.length != 1) {
            throw new SQLException(Messages.get("error.generic.needcolindex", "execute"), "HY092");
        }
        return this.executeImpl(sql, 1, false);
    }

    @Override
    public Connection getConnection() throws SQLException {
        this.checkOpen();
        return this.connection;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        this.checkOpen();
        if (this.genKeyResultSet == null) {
            this.genKeyResultSet = new CachedResultSet(this, new String[]{"ID"}, new int[]{4});
        }
        this.genKeyResultSet.setConcurrency(1007);
        return this.genKeyResultSet;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        this.checkOpen();
        if (this.currentResult instanceof MSCursorResultSet || this.currentResult instanceof CachedResultSet) {
            return this.currentResult;
        }
        if (this.currentResult == null || this.resultSetType == 1003 && this.resultSetConcurrency == 1007) {
            return this.currentResult;
        }
        this.currentResult = new CachedResultSet(this.currentResult, true);
        return this.currentResult;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkOpen();
        return this.messages.getWarnings();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        this.checkOpen();
        if (columnNames == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", "executeUpdate"), "HY092");
        }
        if (columnNames.length != 1) {
            throw new SQLException(Messages.get("error.generic.needcolname", "executeUpdate"), "HY092");
        }
        return this.executeUpdate(sql, 1);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        this.checkOpen();
        if (columnNames == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", "execute"), "HY092");
        }
        if (columnNames.length != 1) {
            throw new SQLException(Messages.get("error.generic.needcolname", "execute"), "HY092");
        }
        return this.executeImpl(sql, 1, false);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        this.checkOpen();
        this.reset();
        if (sql == null || sql.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        if (this.escapeProcessing) {
            String[] tmp = SQLParser.parse(sql, null, this.connection, false);
            if (tmp[1].length() != 0) {
                throw new SQLException(Messages.get("error.statement.badsql"), "07000");
            }
            sql = tmp[0];
        }
        return this.executeSQLQuery(sql, null, null, this.useCursor(false, null));
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this._Closed.get() == 2;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }
}

