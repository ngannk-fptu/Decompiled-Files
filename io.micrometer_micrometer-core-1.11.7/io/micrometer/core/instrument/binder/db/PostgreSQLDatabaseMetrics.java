/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.db;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import javax.sql.DataSource;

@NonNullApi
@NonNullFields
public class PostgreSQLDatabaseMetrics
implements MeterBinder {
    private static final String SELECT = "SELECT ";
    private static final String QUERY_DEAD_TUPLE_COUNT = PostgreSQLDatabaseMetrics.getUserTableQuery("SUM(n_dead_tup)");
    private static final String QUERY_TIMED_CHECKPOINTS_COUNT = PostgreSQLDatabaseMetrics.getBgWriterQuery("checkpoints_timed");
    private static final String QUERY_REQUESTED_CHECKPOINTS_COUNT = PostgreSQLDatabaseMetrics.getBgWriterQuery("checkpoints_req");
    private static final String QUERY_BUFFERS_CLEAN = PostgreSQLDatabaseMetrics.getBgWriterQuery("buffers_clean");
    private static final String QUERY_BUFFERS_BACKEND = PostgreSQLDatabaseMetrics.getBgWriterQuery("buffers_backend");
    private static final String QUERY_BUFFERS_CHECKPOINT = PostgreSQLDatabaseMetrics.getBgWriterQuery("buffers_checkpoint");
    private final String database;
    private final DataSource postgresDataSource;
    private final Iterable<Tag> tags;
    private final Map<String, Double> beforeResetValuesCacheMap;
    private final Map<String, Double> previousValueCacheMap;
    private final String queryConnectionCount;
    private final String queryReadCount;
    private final String queryInsertCount;
    private final String queryTempBytes;
    private final String queryUpdateCount;
    private final String queryDeleteCount;
    private final String queryBlockHits;
    private final String queryBlockReads;
    private final String queryTransactionCount;

    public PostgreSQLDatabaseMetrics(DataSource postgresDataSource, String database) {
        this(postgresDataSource, database, Tags.empty());
    }

    public PostgreSQLDatabaseMetrics(DataSource postgresDataSource, String database, Iterable<Tag> tags) {
        this.postgresDataSource = postgresDataSource;
        this.database = database;
        this.tags = Tags.of(tags).and(PostgreSQLDatabaseMetrics.createDbTag(database));
        this.beforeResetValuesCacheMap = new ConcurrentHashMap<String, Double>();
        this.previousValueCacheMap = new ConcurrentHashMap<String, Double>();
        this.queryConnectionCount = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "numbackends");
        this.queryReadCount = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "tup_fetched");
        this.queryInsertCount = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "tup_inserted");
        this.queryTempBytes = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "temp_bytes");
        this.queryUpdateCount = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "tup_updated");
        this.queryDeleteCount = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "tup_deleted");
        this.queryBlockHits = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "blks_hit");
        this.queryBlockReads = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "blks_read");
        this.queryTransactionCount = PostgreSQLDatabaseMetrics.getDBStatQuery(database, "xact_commit + xact_rollback");
    }

    private static Tag createDbTag(String database) {
        return Tag.of("database", database);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder(Names.SIZE, this.postgresDataSource, dataSource -> this.getDatabaseSize().longValue()).tags(this.tags).description("The database size").register(registry);
        Gauge.builder(Names.CONNECTIONS, this.postgresDataSource, dataSource -> this.getConnectionCount().longValue()).tags(this.tags).description("Number of active connections to the given db").register(registry);
        FunctionCounter.builder(Names.BLOCKS_HITS, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.BLOCKS_HITS, this::getBlockHits)).tags(this.tags).description("Number of times disk blocks were found already in the buffer cache, so that a read was not necessary").register(registry);
        FunctionCounter.builder(Names.BLOCKS_READS, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.BLOCKS_READS, this::getBlockReads)).tags(this.tags).description("Number of disk blocks read in this database").register(registry);
        FunctionCounter.builder(Names.TRANSACTIONS, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.TRANSACTIONS, this::getTransactionCount)).tags(this.tags).description("Total number of transactions executed (commits + rollbacks)").register(registry);
        Gauge.builder(Names.LOCKS, this.postgresDataSource, dataSource -> this.getLockCount().longValue()).tags(this.tags).description("Number of locks on the given db").register(registry);
        FunctionCounter.builder(Names.TEMP_WRITES, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.TEMP_WRITES, this::getTempBytes)).tags(this.tags).description("The total amount of temporary writes to disk to execute queries").baseUnit("bytes").register(registry);
        this.registerRowCountMetrics(registry);
        this.registerCheckpointMetrics(registry);
    }

    private void registerRowCountMetrics(MeterRegistry registry) {
        FunctionCounter.builder(Names.ROWS_FETCHED, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.ROWS_FETCHED, this::getReadCount)).tags(this.tags).description("Number of rows fetched from the db").register(registry);
        FunctionCounter.builder(Names.ROWS_INSERTED, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.ROWS_INSERTED, this::getInsertCount)).tags(this.tags).description("Number of rows inserted from the db").register(registry);
        FunctionCounter.builder(Names.ROWS_UPDATED, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.ROWS_UPDATED, this::getUpdateCount)).tags(this.tags).description("Number of rows updated from the db").register(registry);
        FunctionCounter.builder(Names.ROWS_DELETED, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.ROWS_DELETED, this::getDeleteCount)).tags(this.tags).description("Number of rows deleted from the db").register(registry);
        Gauge.builder(Names.ROWS_DEAD, this.postgresDataSource, dataSource -> this.getDeadTupleCount().longValue()).tags(this.tags).description("Total number of dead rows in the current database").register(registry);
    }

    private void registerCheckpointMetrics(MeterRegistry registry) {
        FunctionCounter.builder(Names.CHECKPOINTS_TIMED, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.CHECKPOINTS_TIMED, this::getTimedCheckpointsCount)).tags(this.tags).description("Number of checkpoints timed").register(registry);
        FunctionCounter.builder(Names.CHECKPOINTS_REQUESTED, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.CHECKPOINTS_REQUESTED, this::getRequestedCheckpointsCount)).tags(this.tags).description("Number of checkpoints requested").register(registry);
        FunctionCounter.builder(Names.BUFFERS_CHECKPOINT, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.BUFFERS_CHECKPOINT, this::getBuffersCheckpoint)).tags(this.tags).description("Number of buffers written during checkpoints").register(registry);
        FunctionCounter.builder(Names.BUFFERS_CLEAN, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.BUFFERS_CLEAN, this::getBuffersClean)).tags(this.tags).description("Number of buffers written by the background writer").register(registry);
        FunctionCounter.builder(Names.BUFFERS_BACKEND, this.postgresDataSource, dataSource -> this.resettableFunctionalCounter(Names.BUFFERS_BACKEND, this::getBuffersBackend)).tags(this.tags).description("Number of buffers written directly by a backend").register(registry);
    }

    private Long getDatabaseSize() {
        return this.runQuery("SELECT pg_database_size('" + this.database + "')");
    }

    private Long getLockCount() {
        return this.runQuery("SELECT count(*) FROM pg_locks l JOIN pg_database d ON l.DATABASE=d.oid WHERE d.datname='" + this.database + "'");
    }

    private Long getConnectionCount() {
        return this.runQuery(this.queryConnectionCount);
    }

    private Long getReadCount() {
        return this.runQuery(this.queryReadCount);
    }

    private Long getInsertCount() {
        return this.runQuery(this.queryInsertCount);
    }

    private Long getTempBytes() {
        return this.runQuery(this.queryTempBytes);
    }

    private Long getUpdateCount() {
        return this.runQuery(this.queryUpdateCount);
    }

    private Long getDeleteCount() {
        return this.runQuery(this.queryDeleteCount);
    }

    private Long getBlockHits() {
        return this.runQuery(this.queryBlockHits);
    }

    private Long getBlockReads() {
        return this.runQuery(this.queryBlockReads);
    }

    private Long getTransactionCount() {
        return this.runQuery(this.queryTransactionCount);
    }

    private Long getDeadTupleCount() {
        return this.runQuery(QUERY_DEAD_TUPLE_COUNT);
    }

    private Long getTimedCheckpointsCount() {
        return this.runQuery(QUERY_TIMED_CHECKPOINTS_COUNT);
    }

    private Long getRequestedCheckpointsCount() {
        return this.runQuery(QUERY_REQUESTED_CHECKPOINTS_COUNT);
    }

    private Long getBuffersClean() {
        return this.runQuery(QUERY_BUFFERS_CLEAN);
    }

    private Long getBuffersBackend() {
        return this.runQuery(QUERY_BUFFERS_BACKEND);
    }

    private Long getBuffersCheckpoint() {
        return this.runQuery(QUERY_BUFFERS_CHECKPOINT);
    }

    Double resettableFunctionalCounter(String functionalCounterKey, DoubleSupplier function) {
        Double result = function.getAsDouble();
        Double previousResult = this.previousValueCacheMap.getOrDefault(functionalCounterKey, 0.0);
        Double beforeResetValue = this.beforeResetValuesCacheMap.getOrDefault(functionalCounterKey, 0.0);
        Double correctedValue = result + beforeResetValue;
        if (correctedValue < previousResult) {
            this.beforeResetValuesCacheMap.put(functionalCounterKey, previousResult);
            correctedValue = previousResult + result;
        }
        this.previousValueCacheMap.put(functionalCounterKey, correctedValue);
        return correctedValue;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Long runQuery(String query) {
        try (Connection connection = this.postgresDataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);){
            if (!resultSet.next()) return 0L;
            Long l = resultSet.getLong(1);
            return l;
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        return 0L;
    }

    private static String getDBStatQuery(String database, String statName) {
        return SELECT + statName + " FROM pg_stat_database WHERE datname = '" + database + "'";
    }

    private static String getUserTableQuery(String statName) {
        return SELECT + statName + " FROM pg_stat_user_tables";
    }

    private static String getBgWriterQuery(String statName) {
        return SELECT + statName + " FROM pg_stat_bgwriter";
    }

    static final class Names {
        static final String SIZE = Names.of("size");
        static final String CONNECTIONS = Names.of("connections");
        static final String BLOCKS_HITS = Names.of("blocks.hits");
        static final String BLOCKS_READS = Names.of("blocks.reads");
        static final String TRANSACTIONS = Names.of("transactions");
        static final String LOCKS = Names.of("locks");
        static final String TEMP_WRITES = Names.of("temp.writes");
        static final String ROWS_FETCHED = Names.of("rows.fetched");
        static final String ROWS_INSERTED = Names.of("rows.inserted");
        static final String ROWS_UPDATED = Names.of("rows.updated");
        static final String ROWS_DELETED = Names.of("rows.deleted");
        static final String ROWS_DEAD = Names.of("rows.dead");
        static final String CHECKPOINTS_TIMED = Names.of("checkpoints.timed");
        static final String CHECKPOINTS_REQUESTED = Names.of("checkpoints.requested");
        static final String BUFFERS_CHECKPOINT = Names.of("buffers.checkpoint");
        static final String BUFFERS_CLEAN = Names.of("buffers.clean");
        static final String BUFFERS_BACKEND = Names.of("buffers.backend");

        private static String of(String name) {
            return "postgres." + name;
        }

        private Names() {
        }
    }
}

