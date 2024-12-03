/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao;

import com.google.common.base.Function;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.java.ao.ActiveObjectsException;
import net.java.ao.CachingSqlProcessor;
import net.java.ao.Common;
import net.java.ao.DBParam;
import net.java.ao.DelegateConnection;
import net.java.ao.DelegateConnectionHandler;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import net.java.ao.EntityManager;
import net.java.ao.ParameterMetadataCachingPreparedStatement;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import net.java.ao.schema.Case;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.TriggerNameConverter;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLIndexField;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.DDLValue;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DatabaseProvider
implements Disposable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Logger sqlLogger = LoggerFactory.getLogger((String)"net.java.ao.sql");
    private final Set<SqlListener> sqlListeners;
    private final ThreadLocal<Connection> transactionThreadLocal = new ThreadLocal();
    private final DisposableDataSource dataSource;
    protected final TypeManager typeManager;
    private final String schema;
    protected AtomicReference<String> quoteRef = new AtomicReference();
    private static final String ORDER_CLAUSE_STRING = "(?:IDENTIFIER_QUOTE_STRING(\\w+)IDENTIFIER_QUOTE_STRING\\.)?(?:IDENTIFIER_QUOTE_STRING(\\w+)IDENTIFIER_QUOTE_STRING)(?:\\s*(?i:(ASC|DESC)))?";
    private static final String PROP_TRANSACTION_ISOLATION_LEVEL = "ao.transaction.isolation.level";
    private final Pattern ORDER_CLAUSE_PATTERN;
    private static final CachingSqlProcessor SHARED_CACHING_SQL_PROCESSOR = new CachingSqlProcessor();
    private CachingSqlProcessor cachingSqlProcessor = SHARED_CACHING_SQL_PROCESSOR;
    private final java.util.function.Function<String, String> processID = this::processID;

    protected DatabaseProvider(DisposableDataSource dataSource, String schema, TypeManager typeManager) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource can't be null");
        this.typeManager = typeManager;
        this.schema = DatabaseProvider.isBlank(schema) ? null : schema;
        this.sqlListeners = new CopyOnWriteArraySet<SqlListener>();
        this.sqlListeners.add(new LoggingSqlListener(this.sqlLogger));
        this.loadQuoteString();
        String identifierQuoteStringPattern = "";
        String quote = this.quoteRef.get();
        if (quote != null && !quote.isEmpty()) {
            identifierQuoteStringPattern = "(?:" + Pattern.quote(quote) + ")?";
        }
        this.ORDER_CLAUSE_PATTERN = Pattern.compile(ORDER_CLAUSE_STRING.replaceAll("IDENTIFIER_QUOTE_STRING", Matcher.quoteReplacement(identifierQuoteStringPattern)));
    }

    protected DatabaseProvider(DisposableDataSource dataSource, String schema) {
        this(dataSource, schema, new TypeManager.Builder().build());
    }

    public TypeManager getTypeManager() {
        return this.typeManager;
    }

    public String getSchema() {
        return this.schema;
    }

    protected void loadQuoteString() {
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
            if (conn == null) {
                throw new IllegalStateException("Could not get connection to load quote String");
            }
            this.quoteRef.set(conn.getMetaData().getIdentifierQuoteString().trim());
        }
        catch (SQLException e) {
            throw new RuntimeException("Unable to query the database", e);
        }
        finally {
            Common.closeQuietly(conn);
        }
    }

    public String renderMetadataQuery(String tableName) {
        return "SELECT * FROM " + this.withSchema(tableName) + " LIMIT 1";
    }

    protected String renderAutoIncrement() {
        return "AUTO_INCREMENT";
    }

    public final Iterable<SQLAction> renderAction(NameConverters nameConverters, DDLAction action) {
        switch (action.getActionType()) {
            case CREATE: {
                return this.renderCreateTableActions(nameConverters, action.getTable());
            }
            case DROP: {
                return this.renderDropTableActions(nameConverters, action.getTable());
            }
            case ALTER_ADD_COLUMN: {
                return this.renderAddColumnActions(nameConverters, action.getTable(), action.getField());
            }
            case ALTER_CHANGE_COLUMN: {
                return this.renderAlterTableChangeColumn(nameConverters, action.getTable(), action.getOldField(), action.getField());
            }
            case ALTER_DROP_COLUMN: {
                return this.renderDropColumnActions(nameConverters, action.getTable(), action.getField());
            }
            case ALTER_ADD_KEY: {
                return Collections.singletonList(this.renderAlterTableAddKey(action.getKey()).withUndoAction(this.renderAlterTableDropKey(action.getKey())));
            }
            case ALTER_DROP_KEY: {
                return Collections.singletonList(this.renderAlterTableDropKey(action.getKey()));
            }
            case CREATE_INDEX: {
                return Collections.singletonList(this.renderCreateIndex(nameConverters.getIndexNameConverter(), action.getIndex()).withUndoAction(this.renderDropIndex(nameConverters.getIndexNameConverter(), action.getIndex())));
            }
            case DROP_INDEX: {
                return Collections.singletonList(this.renderDropForAoManagedIndex(nameConverters.getIndexNameConverter(), action.getIndex()));
            }
            case INSERT: {
                return Collections.singletonList(this.renderInsert(action.getTable(), action.getValues()));
            }
        }
        throw new IllegalArgumentException("unknown DDLAction type " + (Object)((Object)action.getActionType()));
    }

    private SQLAction renderDropForAoManagedIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String indexNamePrefix = indexNameConverter.getPrefix(this.shorten(index.getTable()));
        if (index.getIndexName().toLowerCase().startsWith(indexNamePrefix)) {
            return Optional.ofNullable(this.renderDropIndex(indexNameConverter, index)).orElse(SQLAction.of(""));
        }
        this.logger.debug("Ignoring Drop index {} as index not managed by AO", (Object)index.getIndexName());
        return SQLAction.of("");
    }

    private Iterable<SQLAction> renderCreateTableActions(NameConverters nameConverters, DDLTable table) {
        ArrayList<SQLAction> ret = new ArrayList<SQLAction>();
        ret.add(this.renderTable(nameConverters, table).withUndoAction(this.renderDropTableStatement(table)));
        this.renderAccessories(nameConverters, table).forEach(ret::add);
        return Collections.unmodifiableList(ret);
    }

    private Iterable<SQLAction> renderDropTableActions(NameConverters nameConverters, DDLTable table) {
        ArrayList<SQLAction> ret = new ArrayList<SQLAction>();
        for (DDLIndex index : table.getIndexes()) {
            SQLAction sqlAction = this.renderDropIndex(nameConverters.getIndexNameConverter(), index);
            if (sqlAction == null) continue;
            ret.add(sqlAction);
        }
        this.renderDropAccessories(nameConverters, table).forEach(ret::add);
        ret.add(this.renderDropTableStatement(table));
        return Collections.unmodifiableList(ret);
    }

    private Iterable<SQLAction> renderAddColumnActions(NameConverters nameConverters, DDLTable table, DDLField field) {
        ArrayList ret = new ArrayList();
        this.renderAlterTableAddColumn(nameConverters, table, field).forEach(ret::add);
        return Collections.unmodifiableList(ret);
    }

    protected Iterable<SQLAction> renderDropColumnActions(NameConverters nameConverters, DDLTable table, DDLField field) {
        ArrayList sqlActions = new ArrayList();
        List dropIndexActions = Stream.of(table.getIndexes()).filter(index -> index.containsFieldWithName(field.getName())).map(index -> DDLAction.builder(DDLActionType.DROP_INDEX).setIndex((DDLIndex)index).build()).map(action -> this.renderAction(nameConverters, (DDLAction)action)).flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false)).collect(Collectors.toList());
        sqlActions.addAll(dropIndexActions);
        this.renderAlterTableDropColumn(nameConverters, table, field).forEach(sqlActions::add);
        return Collections.unmodifiableList(sqlActions);
    }

    public String renderQuery(Query query, TableNameConverter converter, boolean count) {
        StringBuilder sql = new StringBuilder();
        sql.append(this.renderQuerySelect(query, converter, count));
        sql.append(this.renderQueryJoins(query, converter));
        sql.append(this.renderQueryWhere(query));
        sql.append(this.renderQueryGroupBy(query));
        sql.append(this.renderQueryHaving(query));
        sql.append(this.renderQueryOrderBy(query));
        sql.append(this.renderQueryLimit(query));
        return sql.toString();
    }

    /*
     * Exception decompiling
     */
    public Object parseValue(int type, String value) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 7[SWITCH]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void setQueryStatementProperties(Statement stmt, Query query) throws SQLException {
    }

    public void setQueryResultSetProperties(ResultSet res, Query query) throws SQLException {
    }

    public ResultSet getTables(Connection conn) throws SQLException {
        return conn.getMetaData().getTables(conn.getCatalog(), this.schema, "%", new String[]{"TABLE"});
    }

    public ResultSet getSequences(Connection conn) throws SQLException {
        return conn.getMetaData().getTables(conn.getCatalog(), this.schema, "%", new String[]{"SEQUENCE"});
    }

    public ResultSet getIndexes(Connection conn, String tableName) throws SQLException {
        return conn.getMetaData().getIndexInfo(conn.getCatalog(), this.schema, tableName, false, false);
    }

    public ResultSet getImportedKeys(Connection connection, String tableName) throws SQLException {
        return connection.getMetaData().getImportedKeys(connection.getCatalog(), this.schema, tableName);
    }

    protected String renderQuerySelect(Query query, TableNameConverter converter, boolean count) {
        StringBuilder sql = new StringBuilder();
        switch (query.getType()) {
            case SELECT: {
                sql.append("SELECT ");
                if (query.isDistinct()) {
                    sql.append("DISTINCT ");
                }
                if (count) {
                    sql.append("COUNT(*)");
                } else {
                    sql.append(this.querySelectFields(query, converter));
                }
                sql.append(" FROM ").append(this.queryTableName(query, converter));
            }
        }
        return sql.toString();
    }

    protected final String queryTableName(Query query, TableNameConverter converter) {
        String queryTable = query.getTable();
        String tableName = queryTable != null ? queryTable : converter.getName(query.getTableType());
        StringBuilder queryTableName = new StringBuilder().append(this.withSchema(tableName));
        if (query.getAlias(query.getTableType()) != null) {
            queryTableName.append(" ").append(query.getAlias(query.getTableType()));
        }
        return queryTableName.toString();
    }

    protected final String querySelectFields(Query query, TableNameConverter converter) {
        return query.getFieldMetadata().stream().map(fieldName -> this.withAlias(query, (Query.FieldMetadata)fieldName, converter)).collect(Collectors.joining(","));
    }

    private String withAlias(Query query, Query.FieldMetadata field, TableNameConverter converter) {
        StringBuilder withAlias = new StringBuilder();
        if (query.getAlias(query.getTableType()) != null) {
            withAlias.append(query.getAlias(query.getTableType())).append(".");
        } else if (!query.getJoins().isEmpty()) {
            String queryTable = query.getTable();
            String tableName = queryTable != null ? queryTable : converter.getName(query.getTableType());
            withAlias.append(this.processID(tableName)).append(".");
        }
        return withAlias.append(this.processID(field)).toString();
    }

    protected String renderQueryJoins(Query query, TableNameConverter converter) {
        StringBuilder sql = new StringBuilder();
        for (Map.Entry<Class<RawEntity<?>>, String> joinEntry : query.getJoins().entrySet()) {
            sql.append(" JOIN ").append(this.withSchema(converter.getName(joinEntry.getKey())));
            if (query.getAlias(joinEntry.getKey()) != null) {
                sql.append(" ").append(query.getAlias(joinEntry.getKey()));
            }
            if (joinEntry.getValue() == null) continue;
            sql.append(" ON ").append(this.processOnClause(joinEntry.getValue()));
        }
        return sql.toString();
    }

    protected String renderQueryWhere(Query query) {
        StringBuilder sql = new StringBuilder();
        String whereClause = query.getWhereClause();
        if (whereClause != null) {
            sql.append(" WHERE ");
            sql.append(this.processWhereClause(whereClause));
        }
        return sql.toString();
    }

    protected String renderQueryGroupBy(Query query) {
        StringBuilder sql = new StringBuilder();
        String groupClause = query.getGroupClause();
        if (groupClause != null) {
            sql.append(" GROUP BY ");
            sql.append(this.processGroupByClause(groupClause));
        }
        return sql.toString();
    }

    private String processGroupByClause(String groupBy) {
        return SqlUtils.processGroupByClause(groupBy, (Function<String, String>)((Function)this::processID), (Function<String, String>)((Function)this::processTableName));
    }

    protected String renderQueryHaving(Query query) {
        StringBuilder sql = new StringBuilder();
        String havingClause = query.getHavingClause();
        if (havingClause != null) {
            sql.append(" HAVING ");
            sql.append(this.processHavingClause(havingClause));
        }
        return sql.toString();
    }

    private String processHavingClause(String having) {
        return SqlUtils.processHavingClause(having, (Function<String, String>)((Function)this::processID), (Function<String, String>)((Function)this::processTableName));
    }

    protected String renderQueryOrderBy(Query query) {
        StringBuilder sql = new StringBuilder();
        String orderClause = query.getOrderClause();
        if (orderClause != null) {
            sql.append(" ORDER BY ");
            sql.append(this.processOrderClause(orderClause));
        }
        return sql.toString();
    }

    public final String processOrderClause(String order) {
        Matcher matcher = this.ORDER_CLAUSE_PATTERN.matcher(order);
        boolean ORDER_CLAUSE_PATTERN_GROUP_TABLE_NAME = true;
        int ORDER_CLAUSE_PATTERN_GROUP_COL_NAME = 2;
        int ORDER_CLAUSE_PATTERN_GROUP_DIRECTION = 3;
        StringBuffer sql = new StringBuffer();
        while (matcher.find()) {
            StringBuilder repl = new StringBuilder();
            if (matcher.group(1) != null) {
                repl.append(this.processTableName(matcher.group(1)));
                repl.append(".");
            }
            repl.append(this.processID(matcher.group(2)));
            if (matcher.group(3) != null) {
                repl.append(" ").append(matcher.group(3));
            }
            matcher.appendReplacement(sql, Matcher.quoteReplacement(repl.toString()));
        }
        matcher.appendTail(sql);
        return sql.toString();
    }

    protected String renderQueryLimit(Query query) {
        int offset;
        StringBuilder sql = new StringBuilder();
        int limit = query.getLimit();
        if (limit >= 0) {
            sql.append(" LIMIT ");
            sql.append(limit);
        }
        if ((offset = query.getOffset()) > 0) {
            sql.append(" OFFSET ").append(offset);
        }
        return sql.toString();
    }

    public final Connection getConnection() throws SQLException {
        Connection connectionImpl;
        Connection c = this.transactionThreadLocal.get();
        if (c != null) {
            if (!c.isClosed()) {
                return c;
            }
            this.transactionThreadLocal.remove();
        }
        if ((connectionImpl = this.dataSource.getConnection()) == null) {
            throw new SQLException("Unable to create connection");
        }
        c = DelegateConnectionHandler.newInstance(connectionImpl, this.isExtraLoggingEnabled());
        this.setPostConnectionProperties(c);
        return c;
    }

    private boolean isExtraLoggingEnabled() {
        return Boolean.getBoolean("net.java.ao.sql.logging.extra");
    }

    public final Connection startTransaction() throws SQLException {
        Connection c = this.getConnection();
        this.setCloseable(c, false);
        c.setTransactionIsolation(this.getTransactionIsolationLevel().getLevel());
        c.setAutoCommit(false);
        this.transactionThreadLocal.set(c);
        return c;
    }

    private TransactionIsolationLevel getTransactionIsolationLevel() {
        TransactionIsolationLevel defaultLevel = TransactionIsolationLevel.TRANSACTION_SERIALIZABLE;
        String isolationLevelProperty = System.getProperty(PROP_TRANSACTION_ISOLATION_LEVEL, defaultLevel.toString());
        try {
            return TransactionIsolationLevel.valueOf(isolationLevelProperty);
        }
        catch (IllegalArgumentException e) {
            Object[] warningArgs = new Object[]{isolationLevelProperty, PROP_TRANSACTION_ISOLATION_LEVEL, defaultLevel};
            this.logger.warn("Invalid value '{}' for {}, using default value '{}'", warningArgs);
            return defaultLevel;
        }
    }

    public final Connection commitTransaction(Connection c) throws SQLException {
        Validate.validState((c == this.transactionThreadLocal.get() ? 1 : 0) != 0, (String)"There are two concurrently open transactions!", (Object[])new Object[0]);
        Validate.validState((c != null ? 1 : 0) != 0, (String)"Tried to commit a transaction that is not started!", (Object[])new Object[0]);
        c.commit();
        this.transactionThreadLocal.remove();
        return c;
    }

    public final void rollbackTransaction(Connection c) throws SQLException {
        Validate.validState((c == this.transactionThreadLocal.get() ? 1 : 0) != 0, (String)"There are two concurrently open transactions!", (Object[])new Object[0]);
        Validate.validState((c != null ? 1 : 0) != 0, (String)"Tried to rollback a transaction that is not started!", (Object[])new Object[0]);
        c.rollback();
    }

    void setCloseable(Connection connection, boolean closeable) {
        if (connection instanceof DelegateConnection) {
            ((DelegateConnection)connection).setCloseable(closeable);
        }
    }

    @Override
    public void dispose() {
        this.dataSource.dispose();
    }

    protected void setPostConnectionProperties(Connection conn) throws SQLException {
    }

    protected String renderConstraintsForTable(UniqueNameConverter uniqueNameConverter, DDLTable table) {
        StringBuilder back = new StringBuilder();
        for (DDLForeignKey key : table.getForeignKeys()) {
            back.append("    ").append(this.renderForeignKey(key)).append(",\n");
        }
        return back.toString();
    }

    protected String renderConstraints(NameConverters nameConverters, List<String> primaryKeys, DDLTable table) {
        StringBuilder back = new StringBuilder();
        back.append(this.renderConstraintsForTable(nameConverters.getUniqueNameConverter(), table));
        if (primaryKeys.size() > 0) {
            back.append(this.renderPrimaryKey(table.getName(), primaryKeys.get(0)));
        }
        return back.toString();
    }

    protected String renderForeignKey(DDLForeignKey key) {
        StringBuilder back = new StringBuilder();
        back.append("CONSTRAINT ").append(this.processID(key.getFKName()));
        back.append(" FOREIGN KEY (").append(this.processID(key.getField())).append(") REFERENCES ");
        back.append(this.withSchema(key.getTable())).append('(').append(this.processID(key.getForeignField())).append(")");
        return back.toString();
    }

    protected String convertTypeToString(TypeInfo<?> type) {
        return type.getSqlTypeIdentifier();
    }

    protected final SQLAction renderTable(NameConverters nameConverters, DDLTable table) {
        StringBuilder back = new StringBuilder("CREATE TABLE ");
        back.append(this.withSchema(table.getName()));
        back.append(" (\n");
        LinkedList<String> primaryKeys = new LinkedList<String>();
        for (DDLField field : table.getFields()) {
            back.append("    ").append(this.renderField(nameConverters, table, field, new RenderFieldOptions(true, true, true))).append(",\n");
            if (!field.isPrimaryKey()) continue;
            primaryKeys.add(field.getName());
        }
        if (primaryKeys.size() > 1) {
            throw new RuntimeException("Entities may only have one primary key");
        }
        back.append(this.renderConstraints(nameConverters, primaryKeys, table));
        back.append(")");
        String tailAppend = this.renderAppend();
        if (tailAppend != null) {
            back.append(' ');
            back.append(tailAppend);
        }
        return SQLAction.of(back);
    }

    protected String renderPrimaryKey(String tableName, String pkFieldName) {
        StringBuilder b = new StringBuilder();
        b.append("    PRIMARY KEY(");
        b.append(this.processID(pkFieldName));
        b.append(")\n");
        return b.toString();
    }

    protected SQLAction renderInsert(DDLTable ddlTable, DDLValue[] ddlValues) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (DDLValue v : ddlValues) {
            columns.append(this.processID(v.getField().getName())).append(",");
            values.append(this.renderValue(v.getValue())).append(",");
        }
        columns.deleteCharAt(columns.length() - 1);
        values.deleteCharAt(values.length() - 1);
        return SQLAction.of(new StringBuilder().append("INSERT INTO ").append(this.withSchema(ddlTable.getName())).append("(").append((CharSequence)columns).append(")").append(" VALUES (").append((CharSequence)values).append(")"));
    }

    protected SQLAction renderDropTableStatement(DDLTable table) {
        return SQLAction.of("DROP TABLE " + this.withSchema(table.getName()));
    }

    protected final Iterable<SQLAction> renderAccessories(NameConverters nameConverters, DDLTable table) {
        return this.renderFields(table, (com.google.common.base.Predicate<DDLField>)((com.google.common.base.Predicate)ddlField -> true), (Function<DDLField, Iterable<SQLAction>>)((Function)ddlField -> this.renderAccessoriesForField(nameConverters, table, (DDLField)ddlField)));
    }

    protected final Iterable<SQLAction> renderDropAccessories(NameConverters nameConverters, DDLTable table) {
        return this.renderFields(table, (com.google.common.base.Predicate<DDLField>)((com.google.common.base.Predicate)ddlField -> true), (Function<DDLField, Iterable<SQLAction>>)((Function)ddlField -> this.renderDropAccessoriesForField(nameConverters, table, (DDLField)ddlField)));
    }

    protected Iterable<SQLAction> renderAccessoriesForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return Collections.emptyList();
    }

    protected Iterable<SQLAction> renderDropAccessoriesForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return Collections.emptyList();
    }

    @Deprecated
    protected final Iterable<SQLAction> renderFields(DDLTable table, com.google.common.base.Predicate<DDLField> filter, Function<DDLField, Iterable<SQLAction>> render) {
        return this.renderFields(table, (Predicate<DDLField>)filter, (java.util.function.Function<DDLField, Iterable<SQLAction>>)render);
    }

    protected final Iterable<SQLAction> renderFields(DDLTable table, Predicate<DDLField> filter, java.util.function.Function<DDLField, Iterable<SQLAction>> render) {
        return Arrays.stream(table.getFields()).filter(filter).map(render).flatMap(it -> StreamSupport.stream(it.spliterator(), false)).collect(Collectors.toList());
    }

    protected Iterable<SQLAction> renderAlterTableAddColumn(NameConverters nameConverters, DDLTable table, DDLField field) {
        ArrayList<SQLAction> back = new ArrayList<SQLAction>();
        back.add(this.renderAlterTableAddColumnStatement(nameConverters, table, field).withUndoAction(this.renderAlterTableDropColumnStatement(table, field)));
        for (DDLForeignKey foreignKey : this.findForeignKeysForField(table, field)) {
            back.add(this.renderAlterTableAddKey(foreignKey).withUndoAction(this.renderAlterTableDropKey(foreignKey)));
        }
        this.renderAccessoriesForField(nameConverters, table, field).forEach(back::add);
        return Collections.unmodifiableList(back);
    }

    protected SQLAction renderAlterTableAddColumnStatement(NameConverters nameConverters, DDLTable table, DDLField field) {
        String addStmt = "ALTER TABLE " + this.withSchema(table.getName()) + " ADD COLUMN " + this.renderField(nameConverters, table, field, new RenderFieldOptions(true, true, true));
        return SQLAction.of(addStmt);
    }

    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        ArrayList<SQLAction> back = new ArrayList<SQLAction>();
        this.renderDropAccessoriesForField(nameConverters, table, oldField).forEach(back::add);
        back.add(this.renderAlterTableChangeColumnStatement(nameConverters, table, oldField, field, this.renderFieldOptionsInAlterColumn()));
        this.renderAccessoriesForField(nameConverters, table, field).forEach(back::add);
        return Collections.unmodifiableList(back);
    }

    protected RenderFieldOptions renderFieldOptionsInAlterColumn() {
        return new RenderFieldOptions(true, true, true, true);
    }

    protected SQLAction renderAlterTableChangeColumnStatement(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field, RenderFieldOptions options) {
        StringBuilder current = new StringBuilder();
        current.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" CHANGE COLUMN ");
        current.append(this.processID(oldField.getName())).append(' ');
        current.append(this.renderField(nameConverters, table, field, options));
        return SQLAction.of(current);
    }

    protected Iterable<SQLAction> renderAlterTableDropColumn(NameConverters nameConverters, DDLTable table, DDLField field) {
        ArrayList<SQLAction> back = new ArrayList<SQLAction>();
        for (DDLForeignKey foreignKey : this.findForeignKeysForField(table, field)) {
            back.add(this.renderAlterTableDropKey(foreignKey));
        }
        this.renderDropAccessoriesForField(nameConverters, table, field).forEach(back::add);
        back.add(this.renderAlterTableDropColumnStatement(table, field));
        return Collections.unmodifiableList(back);
    }

    protected SQLAction renderAlterTableDropColumnStatement(DDLTable table, DDLField field) {
        String dropStmt = "ALTER TABLE " + this.withSchema(table.getName()) + " DROP COLUMN " + this.processID(field.getName());
        return SQLAction.of(dropStmt);
    }

    protected SQLAction renderAlterTableAddKey(DDLForeignKey key) {
        return SQLAction.of("ALTER TABLE " + this.withSchema(key.getDomesticTable()) + " ADD " + this.renderForeignKey(key));
    }

    protected SQLAction renderAlterTableDropKey(DDLForeignKey key) {
        return SQLAction.of("ALTER TABLE " + this.withSchema(key.getDomesticTable()) + " DROP FOREIGN KEY " + this.processID(key.getFKName()));
    }

    protected SQLAction renderCreateIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String statement = "CREATE INDEX " + this.withSchema(index.getIndexName()) + " ON " + this.withSchema(index.getTable()) + Stream.of(index.getFields()).map(DDLIndexField::getFieldName).map(this::processID).collect(Collectors.joining(",", "(", ")"));
        return SQLAction.of(statement);
    }

    @Deprecated
    public SQLAction renderCreateCompositeIndex(String tableName, String indexName, List<String> fields) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE INDEX " + this.processID(indexName));
        statement.append(" ON " + this.withSchema(tableName));
        statement.append(" (");
        boolean needDelimiter = false;
        for (String field : fields) {
            if (needDelimiter) {
                statement.append(",");
            }
            statement.append(this.processID(field));
            needDelimiter = true;
        }
        statement.append(")");
        return SQLAction.of(statement);
    }

    protected SQLAction renderDropIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String indexName = index.getIndexName();
        String tableName = index.getTable();
        if (this.hasIndex(tableName, indexName)) {
            return SQLAction.of("DROP INDEX " + this.withSchema(indexName) + " ON " + this.withSchema(tableName));
        }
        return null;
    }

    protected boolean hasIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String indexName = index.getIndexName();
        return this.hasIndex(index.getTable(), indexName);
    }

    protected boolean hasIndex(String tableName, String indexName) {
        Connection connection = null;
        try {
            connection = this.getConnection();
            ResultSet indexes = this.getIndexes(connection, tableName);
            while (indexes.next()) {
                if (!indexName.equalsIgnoreCase(indexes.getString("INDEX_NAME"))) continue;
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        catch (SQLException e) {
            throw new ActiveObjectsException(e);
        }
        finally {
            Common.closeQuietly(connection);
        }
    }

    protected String renderAppend() {
        return null;
    }

    protected final String renderField(NameConverters nameConverters, DDLTable table, DDLField field, RenderFieldOptions options) {
        String renderUniqueString;
        StringBuilder back = new StringBuilder();
        back.append(this.processID(field.getName()));
        back.append(" ");
        back.append(this.renderFieldType(field));
        if (field.isAutoIncrement()) {
            String autoIncrementValue = this.renderAutoIncrement();
            if (!autoIncrementValue.trim().equals("")) {
                back.append(' ').append(autoIncrementValue);
            }
        } else if (options.forceNull && !field.isNotNull() && !field.isUnique() && !field.isPrimaryKey() || options.renderDefault && field.getDefaultValue() != null) {
            back.append(this.renderFieldDefault(table, field));
        }
        if (options.renderUnique && field.isUnique() && !(renderUniqueString = this.renderUnique(nameConverters.getUniqueNameConverter(), table, field)).trim().equals("")) {
            back.append(' ').append(renderUniqueString);
        }
        if (options.renderNotNull && (field.isNotNull() || field.isUnique())) {
            back.append(" NOT NULL");
        }
        return back.toString();
    }

    protected String renderFieldDefault(DDLTable table, DDLField field) {
        return " DEFAULT " + this.renderValue(field.getDefaultValue());
    }

    protected String renderValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Date) {
            return "'" + this.renderDate((Date)value) + "'";
        }
        if (value instanceof Boolean) {
            return (Boolean)value != false ? "1" : "0";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        return "'" + value.toString() + "'";
    }

    protected String renderDate(Date date) {
        return new SimpleDateFormat(this.getDateFormat()).format(date);
    }

    protected String renderUnique(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        return "UNIQUE";
    }

    protected String getDateFormat() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    protected String renderFieldType(DDLField field) {
        return this.convertTypeToString(field.getType());
    }

    public Object handleBlob(ResultSet res, Class<?> type, String field) throws SQLException {
        Blob blob = res.getBlob(field);
        if (blob == null) {
            return null;
        }
        if (type.equals(InputStream.class)) {
            return blob.getBinaryStream();
        }
        if (type.equals(byte[].class)) {
            return blob.getBytes(1L, (int)blob.length());
        }
        return null;
    }

    protected String _getTriggerNameForField(TriggerNameConverter triggerNameConverter, DDLTable table, DDLField field) {
        return null;
    }

    protected SQLAction _renderTriggerForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return null;
    }

    protected SQLAction _renderDropTriggerForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        String trigger = this._getTriggerNameForField(nameConverters.getTriggerNameConverter(), table, field);
        if (trigger != null) {
            return SQLAction.of("DROP TRIGGER " + this.processID(trigger));
        }
        return null;
    }

    protected String _getFunctionNameForField(TriggerNameConverter triggerNameConverter, DDLTable table, DDLField field) {
        String triggerName = this._getTriggerNameForField(triggerNameConverter, table, field);
        return triggerName != null ? triggerName + "()" : null;
    }

    protected SQLAction _renderFunctionForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return null;
    }

    protected SQLAction _renderDropFunctionForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        String functionName = this._getFunctionNameForField(nameConverters.getTriggerNameConverter(), table, field);
        if (functionName != null) {
            return SQLAction.of("DROP FUNCTION " + this.processID(functionName));
        }
        return null;
    }

    protected SQLAction _renderSequenceForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return null;
    }

    protected SQLAction _renderDropSequenceForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return null;
    }

    public <T extends RawEntity<K>, K> K insertReturningKey(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, boolean pkIdentity, String table, DBParam ... params) throws SQLException {
        String[] fieldNames = (String[])Stream.of(params).map(DBParam::getField).toArray(String[]::new);
        String sql = this.generateInsertSql(pkField, table, fieldNames);
        return this.executeInsertReturningKey(manager, conn, entityType, pkType, pkField, sql, params);
    }

    public <T extends RawEntity<K>, K> void insertBatch(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, boolean pkIdentity, String table, List<Map<String, Object>> rows) throws SQLException {
        Objects.requireNonNull(rows);
        if (rows.isEmpty()) {
            return;
        }
        String[] fieldNames = (String[])rows.stream().flatMap(m -> m.keySet().stream()).distinct().toArray(String[]::new);
        String sql = this.generateInsertSql(pkField, table, fieldNames);
        this.executeInsertBatch(manager, conn, sql, fieldNames, rows);
    }

    String generateInsertSql(String pkField, String table, String[] fieldNames) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + this.withSchema(table) + " (");
        if (fieldNames.length == 0) {
            sql.append(this.processID(pkField));
            sql.append(") VALUES (DEFAULT)");
        } else {
            sql.append(Arrays.stream(fieldNames).map(this::processID).collect(Collectors.joining(",")));
            sql.append(") VALUES (");
            sql.append(StringUtils.repeat((String)"?", (String)",", (int)fieldNames.length));
            sql.append(")");
        }
        return sql.toString();
    }

    protected <T extends RawEntity<K>, K> K executeInsertReturningKey(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, String sql, DBParam ... params) throws SQLException {
        Object back;
        block29: {
            back = null;
            try (PreparedStatement stmt = this.preparedStatement(conn, sql, 1);){
                for (int i = 0; i < params.length; ++i) {
                    Object value = params[i].getValue();
                    if (value instanceof RawEntity) {
                        value = Common.getPrimaryKeyValue((RawEntity)value);
                    }
                    if (params[i].getField().equalsIgnoreCase(pkField)) {
                        back = value;
                    }
                    if (value == null) {
                        this.putNull(stmt, i + 1);
                        continue;
                    }
                    TypeInfo<?> type = this.typeManager.getType(value.getClass());
                    type.getLogicalType().putToDatabase(manager, stmt, i + 1, value, type.getJdbcWriteType());
                }
                stmt.executeUpdate();
                if (back != null) break block29;
                try (ResultSet res = stmt.getGeneratedKeys();){
                    if (res.next()) {
                        back = this.typeManager.getType(pkType).getLogicalType().pullFromDatabase(null, res, pkType, 1);
                    }
                }
            }
        }
        return (K)back;
    }

    private void executeInsertBatch(EntityManager manager, Connection conn, String sql, String[] fieldNames, List<Map<String, Object>> rows) throws SQLException {
        try (PreparedStatement stmt = this.preparedStatement(conn, sql, 2);){
            for (Map<String, Object> row : rows) {
                for (int i = 0; i < fieldNames.length; ++i) {
                    Object value = row.get(fieldNames[i]);
                    if (value instanceof RawEntity) {
                        value = Common.getPrimaryKeyValue((RawEntity)value);
                    }
                    if (value == null) {
                        this.putNull(stmt, i + 1);
                        continue;
                    }
                    TypeInfo<?> type = this.typeManager.getType(value.getClass());
                    type.getLogicalType().putToDatabase(manager, stmt, i + 1, value, type.getJdbcWriteType());
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void putNull(PreparedStatement stmt, int index) throws SQLException {
        stmt.setNull(index, stmt.getParameterMetaData().getParameterType(index));
    }

    public void putBoolean(PreparedStatement stmt, int index, boolean value) throws SQLException {
        stmt.setBoolean(index, value);
    }

    protected boolean isNumericType(int type) {
        switch (type) {
            case -5: {
                return true;
            }
            case -7: {
                return true;
            }
            case 3: {
                return true;
            }
            case 8: {
                return true;
            }
            case 6: {
                return true;
            }
            case 4: {
                return true;
            }
            case 2: {
                return true;
            }
            case 7: {
                return true;
            }
            case 5: {
                return true;
            }
            case -6: {
                return true;
            }
        }
        return false;
    }

    protected String processOnClause(String on) {
        return this.cachingSqlProcessor.processOnClause(on, this.processID);
    }

    public final String processWhereClause(String where) {
        return this.cachingSqlProcessor.processWhereClause(where, this.processID);
    }

    public final String processID(String id) {
        return this.quote(this.shorten(id));
    }

    public final String processID(Query.FieldMetadata fmd) {
        boolean shouldQuoteColumnName = this.shouldQuoteID(fmd.getColumnName());
        boolean shouldQuoteAlias = fmd.getAlias().map(this::shouldQuoteID).orElse(false);
        return fmd.renderField(shouldQuoteColumnName, shouldQuoteAlias, this.quoteRef.get());
    }

    public final String processTableName(String tableName) {
        return this.quoteTableName(this.shorten(tableName));
    }

    public final String withSchema(String tableName) {
        String processedTableName = this.processID(tableName);
        return this.isSchemaNotEmpty() ? this.schema + "." + processedTableName : processedTableName;
    }

    protected final boolean isSchemaNotEmpty() {
        return this.schema != null && this.schema.length() > 0;
    }

    public final String shorten(String id) {
        return Common.shorten(id, this.getMaxIDLength());
    }

    public final String quote(String id) {
        return this.shouldQuoteID(id) ? this.quoteId(id) : id;
    }

    public final String quoteTableName(String tableName) {
        return this.shouldQuoteTableName(tableName) ? this.quoteId(tableName) : tableName;
    }

    private String quoteId(String id) {
        String quote = this.quoteRef.get();
        return quote + id + quote;
    }

    protected boolean shouldQuoteID(String id) {
        return this.getReservedWords().contains(Case.UPPER.apply(id));
    }

    protected boolean shouldQuoteTableName(String tableName) {
        return this.shouldQuoteID(tableName);
    }

    protected int getMaxIDLength() {
        return Integer.MAX_VALUE;
    }

    protected abstract Set<String> getReservedWords();

    public boolean isCaseSensitive() {
        return true;
    }

    public void handleUpdateError(String sql, SQLException e) throws SQLException {
        this.sqlLogger.error("Exception executing SQL update <" + sql + ">", (Throwable)e);
        throw e;
    }

    public final PreparedStatement preparedStatement(Connection c, CharSequence sql) throws SQLException {
        String sqlString = sql.toString();
        this.onSql(sqlString);
        return new ParameterMetadataCachingPreparedStatement(c.prepareStatement(sqlString));
    }

    public final PreparedStatement preparedStatement(Connection c, CharSequence sql, int autoGeneratedKeys) throws SQLException {
        String sqlString = sql.toString();
        this.onSql(sqlString);
        return new ParameterMetadataCachingPreparedStatement(c.prepareStatement(sqlString, autoGeneratedKeys));
    }

    public final PreparedStatement preparedStatement(Connection c, CharSequence sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String sqlString = sql.toString();
        this.onSql(sqlString);
        return new ParameterMetadataCachingPreparedStatement(c.prepareStatement(sqlString, resultSetType, resultSetConcurrency));
    }

    public final void executeUpdate(Statement stmt, CharSequence sql) throws SQLException {
        String sqlString = sql.toString();
        try {
            this.onSql(sqlString);
            Objects.requireNonNull(stmt).executeUpdate(sqlString);
        }
        catch (SQLException e) {
            this.handleUpdateError(sqlString, e);
        }
    }

    public final Iterable<String> executeUpdatesForActions(Statement stmt, Iterable<SQLAction> actions, Set<String> completedStatements) throws SQLException {
        LinkedList<SQLAction> completedActions = new LinkedList<SQLAction>();
        LinkedHashSet<String> newStatements = new LinkedHashSet<String>();
        for (SQLAction action : actions) {
            try {
                Set<String> statements = Stream.concat(completedStatements.stream(), newStatements.stream()).collect(Collectors.toSet());
                Iterable<String> elementsToAdd = this.executeUpdateForAction(stmt, action, statements);
                elementsToAdd.forEach(statements::add);
            }
            catch (SQLException e) {
                this.logger.warn("Error in schema creation: " + e.getMessage() + "; attempting to roll back last partially generated table");
                while (!completedActions.isEmpty()) {
                    SQLAction undoAction = ((SQLAction)completedActions.pop()).getUndoAction();
                    if (undoAction == null) continue;
                    try {
                        this.executeUpdateForAction(stmt, undoAction, completedStatements);
                    }
                    catch (SQLException e2) {
                        this.logger.warn("Unable to finish rolling back partial table creation due to error: " + e2.getMessage());
                        break;
                    }
                }
                throw e;
            }
            completedActions.push(action);
        }
        return newStatements;
    }

    public final Iterable<String> executeUpdateForAction(Statement stmt, SQLAction action, Set<String> completedStatements) throws SQLException {
        String sql = action.getStatement().trim();
        if (sql.isEmpty() || completedStatements.contains(sql)) {
            return Collections.emptyList();
        }
        this.executeUpdate(stmt, sql);
        return Collections.singletonList(sql);
    }

    public final void addSqlListener(SqlListener l) {
        this.sqlListeners.add(l);
    }

    public final void removeSqlListener(SqlListener l) {
        this.sqlListeners.remove(l);
    }

    protected final void onSql(String sql) {
        for (SqlListener sqlListener : this.sqlListeners) {
            sqlListener.onSql(sql);
        }
    }

    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    protected Iterable<DDLForeignKey> findForeignKeysForField(DDLTable table, DDLField field) {
        return Arrays.stream(table.getForeignKeys()).filter(fk -> fk.getField().equals(field.getName())).collect(Collectors.toList());
    }

    private static enum TransactionIsolationLevel {
        TRANSACTION_NONE(0),
        TRANSACTION_READ_UNCOMMITTED(1),
        TRANSACTION_READ_COMMITTED(2),
        TRANSACTION_REPEATABLE_READ(4),
        TRANSACTION_SERIALIZABLE(8);

        private final int level;

        private TransactionIsolationLevel(int level) {
            this.level = level;
        }

        private int getLevel() {
            return this.level;
        }
    }

    private static final class LoggingSqlListener
    implements SqlListener {
        private final Logger logger;

        public LoggingSqlListener(Logger logger) {
            this.logger = Objects.requireNonNull(logger, "logger can't be null");
        }

        @Override
        public void onSql(String sql) {
            this.logger.debug(sql);
        }
    }

    public static interface SqlListener {
        public void onSql(String var1);
    }

    protected static class RenderFieldOptions {
        public final boolean renderUnique;
        public final boolean renderDefault;
        public final boolean renderNotNull;
        public final boolean forceNull;

        public RenderFieldOptions(boolean renderUnique, boolean renderDefault, boolean renderNotNull) {
            this(renderUnique, renderDefault, renderNotNull, false);
        }

        public RenderFieldOptions(boolean renderUnique, boolean renderDefault, boolean renderNotNull, boolean forceNull) {
            this.renderUnique = renderUnique;
            this.renderDefault = renderDefault;
            this.renderNotNull = renderNotNull;
            this.forceNull = forceNull;
        }
    }
}

