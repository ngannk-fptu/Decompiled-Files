/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.index.qual.NonNegative
 *  org.checkerframework.checker.lock.qual.GuardedBy
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.checkerframework.checker.nullness.qual.RequiresNonNull
 */
package org.postgresql.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.postgresql.Driver;
import org.postgresql.core.BaseConnection;
import org.postgresql.core.BaseStatement;
import org.postgresql.core.CachedQuery;
import org.postgresql.core.Field;
import org.postgresql.core.ParameterList;
import org.postgresql.core.Query;
import org.postgresql.core.QueryExecutor;
import org.postgresql.core.ResultCursor;
import org.postgresql.core.ResultHandlerBase;
import org.postgresql.core.SqlCommand;
import org.postgresql.core.Tuple;
import org.postgresql.jdbc.BatchResultHandler;
import org.postgresql.jdbc.PSQLWarningWrapper;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.jdbc.PgResultSet;
import org.postgresql.jdbc.PreferQueryMode;
import org.postgresql.jdbc.QueryExecutorTimeZoneProvider;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.jdbc.ResultWrapper;
import org.postgresql.jdbc.StatementCancelState;
import org.postgresql.jdbc.StatementCancelTimerTask;
import org.postgresql.jdbc.TimestampUtils;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.util.internal.Nullness;

public class PgStatement
implements Statement,
BaseStatement {
    private static final String[] NO_RETURNING_COLUMNS = new String[0];
    private static final boolean DEFAULT_FORCE_BINARY_TRANSFERS = Boolean.getBoolean("org.postgresql.forceBinary");
    private boolean forceBinaryTransfers = DEFAULT_FORCE_BINARY_TRANSFERS;
    protected final ResourceLock lock = new ResourceLock();
    protected @Nullable ArrayList<Query> batchStatements = null;
    protected @Nullable ArrayList<@Nullable ParameterList> batchParameters = null;
    protected final int resultsettype;
    protected final int concurrency;
    private final int rsHoldability;
    private boolean poolable;
    private boolean closeOnCompletion = false;
    protected int fetchdirection = 1000;
    private volatile @Nullable TimerTask cancelTimerTask = null;
    private static final AtomicReferenceFieldUpdater<PgStatement, @Nullable TimerTask> CANCEL_TIMER_UPDATER = AtomicReferenceFieldUpdater.newUpdater(PgStatement.class, TimerTask.class, "cancelTimerTask");
    private volatile StatementCancelState statementState = StatementCancelState.IDLE;
    private static final AtomicReferenceFieldUpdater<PgStatement, StatementCancelState> STATE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(PgStatement.class, StatementCancelState.class, "statementState");
    protected boolean wantsGeneratedKeysOnce = false;
    public boolean wantsGeneratedKeysAlways = false;
    protected final PgConnection connection;
    protected volatile @Nullable PSQLWarningWrapper warnings = null;
    protected int maxrows = 0;
    protected int fetchSize = 0;
    protected long timeout = 0L;
    protected boolean replaceProcessingEnabled = true;
    protected @Nullable ResultWrapper result = null;
    protected @Nullable @GuardedBy(value={"<self>"}) ResultWrapper firstUnclosedResult = null;
    protected @Nullable ResultWrapper generatedKeys = null;
    protected int mPrepareThreshold;
    protected int maxFieldSize = 0;
    protected boolean adaptiveFetch = false;
    private @Nullable TimestampUtils timestampUtils;
    private volatile int isClosed = 0;
    private static final AtomicIntegerFieldUpdater<PgStatement> IS_CLOSED_UPDATER = AtomicIntegerFieldUpdater.newUpdater(PgStatement.class, "isClosed");

    PgStatement(PgConnection c, int rsType, int rsConcurrency, int rsHoldability) throws SQLException {
        this.connection = c;
        this.forceBinaryTransfers |= c.getForceBinary();
        this.resultsettype = rsType;
        this.concurrency = rsConcurrency;
        this.setFetchSize(c.getDefaultFetchSize());
        this.setPrepareThreshold(c.getPrepareThreshold());
        this.setAdaptiveFetch(c.getAdaptiveFetch());
        this.rsHoldability = rsHoldability;
    }

    @Override
    public ResultSet createResultSet(@Nullable Query originalQuery, Field[] fields, List<Tuple> tuples, @Nullable ResultCursor cursor) throws SQLException {
        PgResultSet newResult = new PgResultSet(originalQuery, this, fields, tuples, cursor, this.getMaxRows(), this.getMaxFieldSize(), this.getResultSetType(), this.getResultSetConcurrency(), this.getResultSetHoldability(), this.getAdaptiveFetch());
        newResult.setFetchSize(this.getFetchSize());
        newResult.setFetchDirection(this.getFetchDirection());
        return newResult;
    }

    public BaseConnection getPGConnection() {
        return this.connection;
    }

    public @Nullable String getFetchingCursorName() {
        return null;
    }

    @Override
    public @NonNegative int getFetchSize() {
        return this.fetchSize;
    }

    protected boolean wantsScrollableResultSet() {
        return this.resultsettype != 1003;
    }

    protected boolean wantsHoldableResultSet() {
        return this.rsHoldability == 1;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            if (!this.executeWithFlags(sql, 0)) {
                throw new PSQLException(GT.tr("No results were returned by the query.", new Object[0]), PSQLState.NO_DATA);
            }
            ResultSet resultSet = this.getSingleResultSet();
            return resultSet;
        }
    }

    protected ResultSet getSingleResultSet() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            ResultWrapper result = Nullness.castNonNull(this.result);
            if (result.getNext() != null) {
                throw new PSQLException(GT.tr("Multiple ResultSets were returned by the query.", new Object[0]), PSQLState.TOO_MANY_RESULTS);
            }
            ResultSet resultSet = Nullness.castNonNull(result.getResultSet(), "result.getResultSet()");
            return resultSet;
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.executeWithFlags(sql, 4);
            this.checkNoResultUpdate();
            int n = this.getUpdateCount();
            return n;
        }
    }

    protected final void checkNoResultUpdate() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            for (ResultWrapper iter = this.result; iter != null; iter = iter.getNext()) {
                if (iter.getResultSet() == null) continue;
                throw new PSQLException(GT.tr("A result was returned when none was expected.", new Object[0]), PSQLState.TOO_MANY_RESULTS);
            }
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return this.executeWithFlags(sql, 0);
    }

    @Override
    public boolean executeWithFlags(String sql, int flags) throws SQLException {
        return this.executeCachedSql(sql, flags, NO_RETURNING_COLUMNS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean executeCachedSql(String sql, int flags, String @Nullable [] columnNames) throws SQLException {
        boolean res;
        PreferQueryMode preferQueryMode = this.connection.getPreferQueryMode();
        boolean shouldUseParameterized = false;
        QueryExecutor queryExecutor = this.connection.getQueryExecutor();
        Object key = queryExecutor.createQueryKey(sql, this.replaceProcessingEnabled, shouldUseParameterized, columnNames);
        boolean shouldCache = preferQueryMode == PreferQueryMode.EXTENDED_CACHE_EVERYTHING;
        CachedQuery cachedQuery = shouldCache ? queryExecutor.borrowQueryByKey(key) : queryExecutor.createQueryByKey(key);
        if (this.wantsGeneratedKeysOnce) {
            SqlCommand sqlCommand = cachedQuery.query.getSqlCommand();
            this.wantsGeneratedKeysOnce = sqlCommand != null && sqlCommand.isReturningKeywordPresent();
        }
        try {
            res = this.executeWithFlags(cachedQuery, flags);
        }
        finally {
            if (shouldCache) {
                queryExecutor.releaseQuery(cachedQuery);
            }
        }
        return res;
    }

    @Override
    public boolean executeWithFlags(CachedQuery simpleQuery, int flags) throws SQLException {
        this.checkClosed();
        if (this.connection.getPreferQueryMode().compareTo(PreferQueryMode.EXTENDED) < 0) {
            flags |= 0x400;
        }
        this.execute(simpleQuery, null, flags);
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            boolean bl = this.result != null && this.result.getResultSet() != null;
            return bl;
        }
    }

    @Override
    public boolean executeWithFlags(int flags) throws SQLException {
        this.checkClosed();
        throw new PSQLException(GT.tr("Can''t use executeWithFlags(int) on a Statement.", new Object[0]), PSQLState.WRONG_OBJECT_TYPE);
    }

    private void closeUnclosedProcessedResults() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            ResultWrapper resultWrapper;
            ResultWrapper currentResult = this.result;
            for (resultWrapper = this.firstUnclosedResult; resultWrapper != currentResult && resultWrapper != null; resultWrapper = resultWrapper.getNext()) {
                PgResultSet rs = (PgResultSet)resultWrapper.getResultSet();
                if (rs == null) continue;
                rs.closeInternally();
            }
            this.firstUnclosedResult = resultWrapper;
        }
    }

    protected void closeForNextExecution() throws SQLException {
        this.clearWarnings();
        try (ResourceLock ignore = this.lock.obtain();){
            this.closeUnclosedProcessedResults();
            if (this.result != null && this.result.getResultSet() != null) {
                this.result.getResultSet().close();
            }
            this.result = null;
            ResultWrapper generatedKeys = this.generatedKeys;
            if (generatedKeys != null) {
                ResultSet resultSet = generatedKeys.getResultSet();
                if (resultSet != null) {
                    resultSet.close();
                }
                this.generatedKeys = null;
            }
        }
    }

    protected boolean isOneShotQuery(@Nullable CachedQuery cachedQuery) {
        if (cachedQuery == null) {
            return true;
        }
        cachedQuery.increaseExecuteCount();
        return (this.mPrepareThreshold == 0 || cachedQuery.getExecuteCount() < this.mPrepareThreshold) && !this.getForceBinaryTransfer();
    }

    protected final void execute(CachedQuery cachedQuery, @Nullable ParameterList queryParameters, int flags) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            try {
                this.executeInternal(cachedQuery, queryParameters, flags);
            }
            catch (SQLException e) {
                if (cachedQuery.query.getSubqueries() != null || !this.connection.getQueryExecutor().willHealOnRetry(e)) {
                    throw e;
                }
                cachedQuery.query.close();
                this.executeInternal(cachedQuery, queryParameters, flags);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeInternal(CachedQuery cachedQuery, @Nullable ParameterList queryParameters, int flags) throws SQLException {
        Query queryToExecute;
        this.closeForNextExecution();
        if (!(this.fetchSize <= 0 || this.wantsScrollableResultSet() || this.connection.getAutoCommit() || this.wantsHoldableResultSet())) {
            flags |= 8;
        }
        if ((this.wantsGeneratedKeysOnce || this.wantsGeneratedKeysAlways) && ((flags |= 0x40) & 4) != 0) {
            flags &= 0xFFFFFFFB;
        }
        if (this.isOneShotQuery(cachedQuery)) {
            flags |= 1;
        }
        if (this.connection.getAutoCommit()) {
            flags |= 0x10;
        }
        if (this.connection.hintReadOnly()) {
            flags |= 0x800;
        }
        if (this.concurrency != 1007) {
            flags |= 0x100;
        }
        if ((queryToExecute = cachedQuery.query).isEmpty()) {
            flags |= 0x10;
        }
        if (!queryToExecute.isStatementDescribed() && this.forceBinaryTransfers && (flags & 0x400) == 0) {
            int flags2 = flags | 0x20;
            StatementResultHandler handler2 = new StatementResultHandler();
            this.connection.getQueryExecutor().execute(queryToExecute, queryParameters, handler2, 0, 0, flags2);
            ResultWrapper result2 = handler2.getResults();
            if (result2 != null) {
                Nullness.castNonNull(result2.getResultSet(), "result2.getResultSet()").close();
            }
        }
        StatementResultHandler handler = new StatementResultHandler();
        try (ResourceLock ignore = this.lock.obtain();){
            this.result = null;
        }
        try {
            this.startTimer();
            this.connection.getQueryExecutor().execute(queryToExecute, queryParameters, handler, this.maxrows, this.fetchSize, flags, this.adaptiveFetch);
        }
        finally {
            this.killTimerTask();
        }
        ignore = this.lock.obtain();
        var7_8 = null;
        try {
            ResultWrapper currentResult;
            this.checkClosed();
            this.result = this.firstUnclosedResult = (currentResult = handler.getResults());
            if (this.wantsGeneratedKeysOnce || this.wantsGeneratedKeysAlways) {
                this.generatedKeys = currentResult;
                this.result = Nullness.castNonNull(currentResult, "handler.getResults()").getNext();
                if (this.wantsGeneratedKeysOnce) {
                    this.wantsGeneratedKeysOnce = false;
                }
            }
        }
        catch (Throwable throwable) {
            var7_8 = throwable;
            throw throwable;
        }
        finally {
            if (ignore != null) {
                if (var7_8 != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable) {
                        var7_8.addSuppressed(throwable);
                    }
                } else {
                    ignore.close();
                }
            }
        }
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        this.checkClosed();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            if (this.result == null || this.result.getResultSet() != null) {
                int n = -1;
                return n;
            }
            long count = this.result.getUpdateCount();
            int n = count > Integer.MAX_VALUE ? -2 : (int)count;
            return n;
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return this.getMoreResults(3);
    }

    @Override
    public int getMaxRows() throws SQLException {
        this.checkClosed();
        return this.maxrows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.checkClosed();
        if (max < 0) {
            throw new PSQLException(GT.tr("Maximum number of rows must be a value greater than or equal to 0.", new Object[0]), PSQLState.INVALID_PARAMETER_VALUE);
        }
        this.maxrows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.checkClosed();
        this.replaceProcessingEnabled = enable;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        this.checkClosed();
        long seconds = this.timeout / 1000L;
        if (seconds >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)seconds;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.setQueryTimeoutMs((long)seconds * 1000L);
    }

    public long getQueryTimeoutMs() throws SQLException {
        this.checkClosed();
        return this.timeout;
    }

    public void setQueryTimeoutMs(long millis) throws SQLException {
        this.checkClosed();
        if (millis < 0L) {
            throw new PSQLException(GT.tr("Query timeout must be a value greater than or equals to 0.", new Object[0]), PSQLState.INVALID_PARAMETER_VALUE);
        }
        this.timeout = millis;
    }

    public void addWarning(SQLWarning warn) {
        PSQLWarningWrapper warnWrap = this.warnings;
        if (warnWrap == null) {
            this.warnings = new PSQLWarningWrapper(warn);
        } else {
            warnWrap.addWarning(warn);
        }
    }

    @Override
    public @Nullable SQLWarning getWarnings() throws SQLException {
        this.checkClosed();
        PSQLWarningWrapper warnWrap = this.warnings;
        return warnWrap != null ? warnWrap.getFirstWarning() : null;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.checkClosed();
        if (max < 0) {
            throw new PSQLException(GT.tr("The maximum field size must be a value greater than or equal to 0.", new Object[0]), PSQLState.INVALID_PARAMETER_VALUE);
        }
        this.maxFieldSize = max;
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.warnings = null;
    }

    @Override
    public @Nullable ResultSet getResultSet() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            if (this.result == null) {
                ResultSet resultSet = null;
                return resultSet;
            }
            ResultSet resultSet = this.result.getResultSet();
            return resultSet;
        }
    }

    @Override
    public final void close() throws SQLException {
        if (!IS_CLOSED_UPDATER.compareAndSet(this, 0, 1)) {
            return;
        }
        this.cancel();
        this.closeForNextExecution();
        this.closeImpl();
    }

    protected void closeImpl() throws SQLException {
    }

    @Override
    public long getLastOID() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            if (this.result == null) {
                long l = 0L;
                return l;
            }
            long l = this.result.getInsertOID();
            return l;
        }
    }

    @Override
    public void setPrepareThreshold(int newThreshold) throws SQLException {
        this.checkClosed();
        if (newThreshold < 0) {
            this.forceBinaryTransfers = true;
            newThreshold = 1;
        }
        this.mPrepareThreshold = newThreshold;
    }

    @Override
    public int getPrepareThreshold() {
        return this.mPrepareThreshold;
    }

    @Override
    public void setUseServerPrepare(boolean flag) throws SQLException {
        this.setPrepareThreshold(flag ? 1 : 0);
    }

    @Override
    public boolean isUseServerPrepare() {
        return false;
    }

    protected void checkClosed() throws SQLException {
        if (this.isClosed()) {
            throw new PSQLException(GT.tr("This statement has been closed.", new Object[0]), PSQLState.OBJECT_NOT_IN_STATE);
        }
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        ArrayList<ParameterList> batchParameters;
        this.checkClosed();
        ArrayList<Query> batchStatements = this.batchStatements;
        if (batchStatements == null) {
            batchStatements = new ArrayList();
            this.batchStatements = batchStatements;
        }
        if ((batchParameters = this.batchParameters) == null) {
            batchParameters = new ArrayList();
            this.batchParameters = batchParameters;
        }
        boolean shouldUseParameterized = false;
        CachedQuery cachedQuery = this.connection.createQuery(sql, this.replaceProcessingEnabled, shouldUseParameterized, new String[0]);
        batchStatements.add(cachedQuery.query);
        batchParameters.add(null);
    }

    @Override
    public void clearBatch() throws SQLException {
        if (this.batchStatements != null) {
            this.batchStatements.clear();
        }
        if (this.batchParameters != null) {
            this.batchParameters.clear();
        }
    }

    protected BatchResultHandler createBatchHandler(Query[] queries, @Nullable ParameterList[] parameterLists) {
        return new BatchResultHandler(this, queries, parameterLists, this.wantsGeneratedKeysAlways);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @RequiresNonNull(value={"batchStatements", "batchParameters"})
    private BatchResultHandler internalExecuteBatch() throws SQLException {
        boolean sameQueryAhead;
        this.transformQueriesAndParameters();
        ArrayList<Query> batchStatements = Nullness.castNonNull(this.batchStatements);
        ArrayList<@Nullable ParameterList> batchParameters = Nullness.castNonNull(this.batchParameters);
        Query[] queries = batchStatements.toArray(new Query[0]);
        @Nullable ParameterList[] parameterLists = batchParameters.toArray(new ParameterList[0]);
        batchStatements.clear();
        batchParameters.clear();
        boolean preDescribe = false;
        int flags = this.wantsGeneratedKeysAlways ? 320 : 4;
        PreferQueryMode preferQueryMode = this.connection.getPreferQueryMode();
        if (preferQueryMode == PreferQueryMode.SIMPLE || preferQueryMode == PreferQueryMode.EXTENDED_FOR_PREPARED && parameterLists[0] == null) {
            flags |= 0x400;
        }
        boolean bl = sameQueryAhead = queries.length > 1 && queries[0] == queries[1];
        if (!sameQueryAhead || this.isOneShotQuery(null)) {
            flags |= 1;
        } else {
            preDescribe = (this.wantsGeneratedKeysAlways || sameQueryAhead) && !queries[0].isStatementDescribed();
            flags |= 0x200;
        }
        if (this.connection.getAutoCommit()) {
            flags |= 0x10;
        }
        if (this.connection.hintReadOnly()) {
            flags |= 0x800;
        }
        BatchResultHandler handler = this.createBatchHandler(queries, parameterLists);
        if ((preDescribe || this.forceBinaryTransfers) && (flags & 0x400) == 0) {
            int flags2 = flags | 0x20;
            StatementResultHandler handler2 = new StatementResultHandler();
            try {
                this.connection.getQueryExecutor().execute(queries[0], parameterLists[0], handler2, 0, 0, flags2);
            }
            catch (SQLException e) {
                handler.handleError(e);
                handler.handleCompletion();
            }
            ResultWrapper result2 = handler2.getResults();
            if (result2 != null) {
                Nullness.castNonNull(result2.getResultSet(), "result2.getResultSet()").close();
            }
        }
        try (ResourceLock ignore = this.lock.obtain();){
            this.result = null;
        }
        try {
            this.startTimer();
            this.connection.getQueryExecutor().execute(queries, parameterLists, handler, this.maxrows, this.fetchSize, flags, this.adaptiveFetch);
            return handler;
        }
        finally {
            this.killTimerTask();
            ignore = this.lock.obtain();
            var11_12 = null;
            try {
                this.checkClosed();
                if (this.wantsGeneratedKeysAlways) {
                    this.generatedKeys = new ResultWrapper(handler.getGeneratedKeys());
                }
            }
            catch (Throwable throwable) {
                var11_12 = throwable;
                throw throwable;
            }
            finally {
                if (ignore != null) {
                    if (var11_12 != null) {
                        try {
                            ignore.close();
                        }
                        catch (Throwable throwable) {
                            var11_12.addSuppressed(throwable);
                        }
                    } else {
                        ignore.close();
                    }
                }
            }
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        this.checkClosed();
        this.closeForNextExecution();
        if (this.batchStatements == null || this.batchStatements.isEmpty() || this.batchParameters == null) {
            return new int[0];
        }
        return this.internalExecuteBatch().getUpdateCount();
    }

    @Override
    public void cancel() throws SQLException {
        if (this.statementState == StatementCancelState.IDLE) {
            return;
        }
        if (!STATE_UPDATER.compareAndSet(this, StatementCancelState.IN_QUERY, StatementCancelState.CANCELING)) {
            return;
        }
        try (ResourceLock connectionLock = this.connection.obtainLock();){
            try {
                this.connection.cancelQuery();
            }
            finally {
                STATE_UPDATER.set(this, StatementCancelState.CANCELLED);
                this.connection.lockCondition().signalAll();
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public int getFetchDirection() {
        return this.fetchdirection;
    }

    @Override
    public int getResultSetConcurrency() {
        return this.concurrency;
    }

    @Override
    public int getResultSetType() {
        return this.resultsettype;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        switch (direction) {
            case 1000: 
            case 1001: 
            case 1002: {
                this.fetchdirection = direction;
                break;
            }
            default: {
                throw new PSQLException(GT.tr("Invalid fetch direction constant: {0}.", direction), PSQLState.INVALID_PARAMETER_VALUE);
            }
        }
    }

    @Override
    public void setFetchSize(@NonNegative int rows) throws SQLException {
        this.checkClosed();
        if (rows < 0) {
            throw new PSQLException(GT.tr("Fetch size must be a value greater to or equal to 0.", new Object[0]), PSQLState.INVALID_PARAMETER_VALUE);
        }
        this.fetchSize = rows;
    }

    private void startTimer() {
        this.cleanupTimer();
        STATE_UPDATER.set(this, StatementCancelState.IN_QUERY);
        if (this.timeout == 0L) {
            return;
        }
        StatementCancelTimerTask cancelTask = new StatementCancelTimerTask(this);
        CANCEL_TIMER_UPDATER.set(this, cancelTask);
        this.connection.addTimerTask(cancelTask, this.timeout);
    }

    void cancelIfStillNeeded(TimerTask timerTask) {
        try {
            if (!CANCEL_TIMER_UPDATER.compareAndSet(this, timerTask, null)) {
                return;
            }
            this.cancel();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    private boolean cleanupTimer() {
        TimerTask timerTask = CANCEL_TIMER_UPDATER.get(this);
        if (timerTask == null) {
            return this.timeout == 0L;
        }
        if (!CANCEL_TIMER_UPDATER.compareAndSet(this, timerTask, null)) {
            return false;
        }
        timerTask.cancel();
        this.connection.purgeTimerTasks();
        return true;
    }

    private void killTimerTask() {
        boolean timerTaskIsClear = this.cleanupTimer();
        if (timerTaskIsClear && STATE_UPDATER.compareAndSet(this, StatementCancelState.IN_QUERY, StatementCancelState.IDLE)) {
            return;
        }
        boolean interrupted = false;
        try (ResourceLock connectionLock = this.connection.obtainLock();){
            while (!STATE_UPDATER.compareAndSet(this, StatementCancelState.CANCELLED, StatementCancelState.IDLE)) {
                try {
                    this.connection.lockCondition().await(10L, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    protected boolean getForceBinaryTransfer() {
        return this.forceBinaryTransfers;
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            if (this.result == null || this.result.getResultSet() != null) {
                long l = -1L;
                return l;
            }
            long l = this.result.getUpdateCount();
            return l;
        }
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setLargeMaxRows");
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getLargeMaxRows");
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        this.checkClosed();
        this.closeForNextExecution();
        if (this.batchStatements == null || this.batchStatements.isEmpty() || this.batchParameters == null) {
            return new long[0];
        }
        return this.internalExecuteBatch().getLargeUpdateCount();
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.executeWithFlags(sql, 4);
            this.checkNoResultUpdate();
            long l = this.getLargeUpdateCount();
            return l;
        }
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        if (autoGeneratedKeys == 2) {
            return this.executeLargeUpdate(sql);
        }
        return this.executeLargeUpdate(sql, (String[])null);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        if (columnIndexes == null || columnIndexes.length == 0) {
            return this.executeLargeUpdate(sql);
        }
        throw new PSQLException(GT.tr("Returning autogenerated keys by column index is not supported.", new Object[0]), PSQLState.NOT_IMPLEMENTED);
    }

    @Override
    public long executeLargeUpdate(String sql, String @Nullable [] columnNames) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            if (columnNames != null && columnNames.length == 0) {
                long l = this.executeLargeUpdate(sql);
                return l;
            }
            this.wantsGeneratedKeysOnce = true;
            if (!this.executeCachedSql(sql, 0, columnNames)) {
                // empty if block
            }
            long l = this.getLargeUpdateCount();
            return l;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.isClosed == 1;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.checkClosed();
        this.poolable = poolable;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        this.checkClosed();
        return this.poolable;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.closeOnCompletion = true;
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.closeOnCompletion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void checkCompletion() throws SQLException {
        if (!this.closeOnCompletion) {
            return;
        }
        try (ResourceLock ignore = this.lock.obtain();){
            for (ResultWrapper result = this.firstUnclosedResult; result != null; result = result.getNext()) {
                ResultSet resultSet = result.getResultSet();
                if (resultSet == null || resultSet.isClosed()) continue;
                return;
            }
        }
        this.closeOnCompletion = false;
        try {
            this.close();
        }
        finally {
            this.closeOnCompletion = true;
        }
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            if (current == 1 && this.result != null && this.result.getResultSet() != null) {
                this.result.getResultSet().close();
            }
            if (this.result != null) {
                this.result = this.result.getNext();
            }
            if (current == 3) {
                this.closeUnclosedProcessedResults();
            }
            boolean bl = this.result != null && this.result.getResultSet() != null;
            return bl;
        }
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkClosed();
            if (this.generatedKeys == null || this.generatedKeys.getResultSet() == null) {
                ResultSet resultSet = this.createDriverResultSet(new Field[0], new ArrayList<Tuple>());
                return resultSet;
            }
            ResultSet resultSet = this.generatedKeys.getResultSet();
            return resultSet;
        }
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        if (autoGeneratedKeys == 2) {
            return this.executeUpdate(sql);
        }
        return this.executeUpdate(sql, (String[])null);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        if (columnIndexes == null || columnIndexes.length == 0) {
            return this.executeUpdate(sql);
        }
        throw new PSQLException(GT.tr("Returning autogenerated keys by column index is not supported.", new Object[0]), PSQLState.NOT_IMPLEMENTED);
    }

    @Override
    public int executeUpdate(String sql, String @Nullable [] columnNames) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            if (columnNames != null && columnNames.length == 0) {
                int n = this.executeUpdate(sql);
                return n;
            }
            this.wantsGeneratedKeysOnce = true;
            if (!this.executeCachedSql(sql, 0, columnNames)) {
                // empty if block
            }
            int n = this.getUpdateCount();
            return n;
        }
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        if (autoGeneratedKeys == 2) {
            return this.execute(sql);
        }
        return this.execute(sql, (String[])null);
    }

    @Override
    public boolean execute(String sql, int @Nullable [] columnIndexes) throws SQLException {
        if (columnIndexes != null && columnIndexes.length == 0) {
            return this.execute(sql);
        }
        throw new PSQLException(GT.tr("Returning autogenerated keys by column index is not supported.", new Object[0]), PSQLState.NOT_IMPLEMENTED);
    }

    @Override
    public boolean execute(String sql, String @Nullable [] columnNames) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            if (columnNames != null && columnNames.length == 0) {
                boolean bl = this.execute(sql);
                return bl;
            }
            this.wantsGeneratedKeysOnce = true;
            boolean bl = this.executeCachedSql(sql, 0, columnNames);
            return bl;
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.rsHoldability;
    }

    @Override
    public ResultSet createDriverResultSet(Field[] fields, List<Tuple> tuples) throws SQLException {
        return this.createResultSet(null, fields, tuples, null);
    }

    protected void transformQueriesAndParameters() throws SQLException {
    }

    @Override
    public void setAdaptiveFetch(boolean adaptiveFetch) {
        this.adaptiveFetch = adaptiveFetch;
    }

    @Override
    public boolean getAdaptiveFetch() {
        return this.adaptiveFetch;
    }

    protected TimestampUtils getTimestampUtils() {
        if (this.timestampUtils == null) {
            this.timestampUtils = new TimestampUtils(!this.connection.getQueryExecutor().getIntegerDateTimes(), new QueryExecutorTimeZoneProvider(this.connection.getQueryExecutor()));
        }
        return this.timestampUtils;
    }

    public class StatementResultHandler
    extends ResultHandlerBase {
        private @Nullable ResultWrapper results;
        private @Nullable ResultWrapper lastResult;

        @Nullable ResultWrapper getResults() {
            return this.results;
        }

        private void append(ResultWrapper newResult) {
            if (this.results == null) {
                this.lastResult = this.results = newResult;
            } else {
                Nullness.castNonNull(this.lastResult).append(newResult);
            }
        }

        @Override
        public void handleResultRows(Query fromQuery, Field[] fields, List<Tuple> tuples, @Nullable ResultCursor cursor) {
            try {
                ResultSet rs = PgStatement.this.createResultSet(fromQuery, fields, tuples, cursor);
                this.append(new ResultWrapper(rs));
            }
            catch (SQLException e) {
                this.handleError(e);
            }
        }

        @Override
        public void handleCommandStatus(String status, long updateCount, long insertOID) {
            this.append(new ResultWrapper(updateCount, insertOID));
        }

        @Override
        public void handleWarning(SQLWarning warning) {
            PgStatement.this.addWarning(warning);
        }
    }
}

