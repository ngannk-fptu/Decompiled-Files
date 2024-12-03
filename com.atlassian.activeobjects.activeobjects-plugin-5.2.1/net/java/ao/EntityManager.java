/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.base.Throwables
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.util.concurrent.ExecutionError
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao;

import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.java.ao.ActiveObjectsException;
import net.java.ao.Common;
import net.java.ao.DBParam;
import net.java.ao.DatabaseProvider;
import net.java.ao.DefaultPolymorphicTypeMapper;
import net.java.ao.EntityManagerConfiguration;
import net.java.ao.EntityProxy;
import net.java.ao.EntityProxyAccessor;
import net.java.ao.EntityStreamCallback;
import net.java.ao.FailedFastCountException;
import net.java.ao.PolymorphicTypeMapper;
import net.java.ao.Preload;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import net.java.ao.ReadOnlyEntityProxy;
import net.java.ao.SchemaConfiguration;
import net.java.ao.ValueGenerator;
import net.java.ao.db.MySQLDatabaseProvider;
import net.java.ao.db.PostgreSQLDatabaseProvider;
import net.java.ao.schema.CachingNameConverters;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.SchemaGenerator;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.EntityInfoResolver;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.LogicalType;
import net.java.ao.types.TypeInfo;
import net.java.ao.util.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManager {
    private static final String DB_AO_ENTITY_MANAGER_TIMER_NAME = "db.ao.entityManager";
    private static final String ENTITY_TYPE_TAG = "entityType";
    private static final Logger log = LoggerFactory.getLogger(EntityManager.class);
    private final DatabaseProvider provider;
    private final EntityManagerConfiguration configuration;
    private final SchemaConfiguration schemaConfiguration;
    private final NameConverters nameConverters;
    private final EntityInfoResolver entityInfoResolver;
    private PolymorphicTypeMapper typeMapper;
    private final ReadWriteLock typeMapperLock = new ReentrantReadWriteLock(true);
    private final LoadingCache<Class<? extends ValueGenerator<?>>, ValueGenerator<?>> valGenCache;

    public EntityManager(DatabaseProvider provider, EntityManagerConfiguration configuration) {
        this.provider = Objects.requireNonNull(provider, "provider can't be null");
        this.configuration = Objects.requireNonNull(configuration);
        this.valGenCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends ValueGenerator<?>>, ValueGenerator<?>>(){

            public ValueGenerator<?> load(Class<? extends ValueGenerator<?>> generatorClass) throws Exception {
                return generatorClass.newInstance();
            }
        });
        this.nameConverters = new CachingNameConverters(configuration.getNameConverters());
        this.schemaConfiguration = Objects.requireNonNull(configuration.getSchemaConfiguration(), "schema configuration can't be null");
        this.typeMapper = new DefaultPolymorphicTypeMapper(new HashMap());
        this.entityInfoResolver = Objects.requireNonNull(configuration.getEntityInfoResolverFactory().create(this.nameConverters, provider.getTypeManager()), "entityInfoResolver");
    }

    public void migrate(Class<? extends RawEntity<?>> ... entities) throws SQLException {
        SchemaGenerator.migrate(this.provider, this.schemaConfiguration, this.nameConverters, false, entities);
    }

    public void migrateDestructively(Class<? extends RawEntity<?>> ... entities) throws SQLException {
        SchemaGenerator.migrate(this.provider, this.schemaConfiguration, this.nameConverters, true, entities);
    }

    @Deprecated
    public void flushAll() {
    }

    @Deprecated
    public void flushEntityCache() {
    }

    @Deprecated
    public void flush(RawEntity<?> ... entities) {
    }

    public <T extends RawEntity<K>, K> T[] get(Class<T> type, K ... keys) throws SQLException {
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.get").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
            String primaryKeyField = entityInfo.getPrimaryKey().getName();
            RawEntity[] rawEntityArray = this.get(type, this.findByPrimaryKey(type, primaryKeyField), keys);
            return rawEntityArray;
        }
    }

    private <T extends RawEntity<K>, K> Function<T, K> findByPrimaryKey(final Class<T> type, final String primaryKeyField) {
        return new Function<T, K>(){

            @Override
            public T invoke(K k) throws SQLException {
                RawEntity[] ts = EntityManager.this.find(type, primaryKeyField + " = ?", new Object[]{k});
                if (ts.length == 1) {
                    return ts[0];
                }
                if (ts.length == 0) {
                    return null;
                }
                throw new ActiveObjectsException("Found more that one object of type '" + type.getName() + "' for key '" + k + "'");
            }
        };
    }

    protected <T extends RawEntity<K>, K> T[] peer(final EntityInfo<T, K> entityInfo, K ... keys) throws SQLException {
        return this.get(entityInfo.getEntityType(), new Function<T, K>(){

            @Override
            public T invoke(K key) {
                return EntityManager.this.getAndInstantiate(entityInfo, key);
            }
        }, keys);
    }

    private <T extends RawEntity<K>, K> T[] get(Class<T> type, Function<T, K> create, K ... keys) throws SQLException {
        RawEntity[] back = (RawEntity[])Array.newInstance(type, keys.length);
        int index = 0;
        for (K key : keys) {
            back[index++] = (RawEntity)create.invoke(key);
        }
        return back;
    }

    protected <T extends RawEntity<K>, K> T getAndInstantiate(EntityInfo<T, K> entityInfo, K key) {
        Class<T> type = entityInfo.getEntityType();
        EntityProxy<T, K> proxy = new EntityProxy<T, K>(this, entityInfo, key);
        RawEntity entity = (RawEntity)type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type, EntityProxyAccessor.class}, proxy));
        return (T)entity;
    }

    public <T extends RawEntity<K>, K> T get(Class<T> type, K key) throws SQLException {
        return this.get(type, (K)EntityManager.toArray(key))[0];
    }

    protected <T extends RawEntity<K>, K> T peer(EntityInfo<T, K> entityInfo, K key) throws SQLException {
        if (null == key) {
            return null;
        }
        return this.peer(entityInfo, (K)EntityManager.toArray(key))[0];
    }

    private static <K> K[] toArray(K key) {
        return new Object[]{key};
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends RawEntity<K>, K> T create(Class<T> type, DBParam ... params) throws SQLException {
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.create").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            RawEntity back = null;
            EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
            String table = entityInfo.getName();
            HashSet<DBParam> listParams = new HashSet<DBParam>();
            listParams.addAll(Arrays.asList(params));
            Collection fieldsInfoWithGenerators = entityInfo.getFields().stream().filter(FieldInfo.HAS_GENERATOR).collect(Collectors.toSet());
            for (Object fieldInfo : fieldsInfoWithGenerators) {
                ValueGenerator generator;
                try {
                    generator = (ValueGenerator)this.valGenCache.get(fieldInfo.getGeneratorType());
                }
                catch (ExecutionException e) {
                    throw Throwables.propagate((Throwable)e.getCause());
                }
                catch (UncheckedExecutionException e) {
                    throw Throwables.propagate((Throwable)e.getCause());
                }
                catch (ExecutionError e) {
                    throw Throwables.propagate((Throwable)e.getCause());
                }
                listParams.add(new DBParam(fieldInfo.getName(), generator.generateValue(this)));
            }
            Set requiredFields = entityInfo.getFields().stream().filter(FieldInfo.IS_REQUIRED).collect(Collectors.toSet());
            for (DBParam param : listParams) {
                FieldInfo field = entityInfo.getField(param.getField());
                Objects.requireNonNull(field, String.format("Entity %s does not have field %s", type.getName(), param.getField()));
                if (field.isPrimary()) {
                    Common.validatePrimaryKey(field, param.getValue());
                } else if (!field.isNullable()) {
                    Validate.isTrue((param.getValue() != null ? 1 : 0) != 0, (String)"Cannot set non-null field %s to null", (Object[])new Object[]{param.getField()});
                    if (param.getValue() instanceof String) {
                        Validate.isTrue((!StringUtils.isBlank((String)param.getValue()) ? 1 : 0) != 0, (String)"Cannot set non-null String field %s to ''", (Object[])new Object[]{param.getField()});
                    }
                }
                requiredFields.remove(field);
                TypeInfo dbType = field.getTypeInfo();
                if (dbType == null || param.getValue() == null) continue;
                dbType.getLogicalType().validate(param.getValue());
            }
            if (!requiredFields.isEmpty()) {
                String requiredFieldsAsString = requiredFields.stream().map(Object::toString).collect(Collectors.joining(", "));
                throw new IllegalArgumentException("The follow required fields were not set when trying to create entity '" + type.getName() + "', those fields are: " + requiredFieldsAsString);
            }
            Connection connection = null;
            try {
                connection = this.provider.getConnection();
                back = (RawEntity)this.peer(entityInfo, this.provider.insertReturningKey(this, connection, type, entityInfo.getPrimaryKey().getJavaType(), entityInfo.getPrimaryKey().getName(), entityInfo.getPrimaryKey().hasAutoIncrement(), table, listParams.toArray(new DBParam[listParams.size()])));
            }
            finally {
                SqlUtils.closeQuietly(connection);
            }
            back.init();
            RawEntity rawEntity = back;
            return (T)rawEntity;
        }
    }

    public <T extends RawEntity<K>, K> T create(Class<T> type, Map<String, Object> params) throws SQLException {
        DBParam[] arrParams = new DBParam[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            arrParams[i++] = new DBParam(key, params.get(key));
        }
        return this.create(type, arrParams);
    }

    public <T extends RawEntity<K>, K> void create(Class<T> type, List<Map<String, Object>> rows) throws SQLException {
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.create").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
            String table = entityInfo.getName();
            List<Map<String, Object>> injectedRows = this.injectAutoGeneratedFields(rows, entityInfo);
            this.validateRequiredFields(injectedRows, entityInfo);
            try (Connection connection = this.provider.getConnection();){
                this.provider.insertBatch(this, connection, type, entityInfo.getPrimaryKey().getJavaType(), entityInfo.getPrimaryKey().getName(), entityInfo.getPrimaryKey().hasAutoIncrement(), table, injectedRows);
            }
        }
    }

    private <T extends RawEntity<K>, K> void validateRequiredFields(List<Map<String, Object>> rows, EntityInfo<T, K> entityInfo) {
        for (Map<String, Object> row : rows) {
            Set requiredFields = entityInfo.getFields().stream().filter(FieldInfo.IS_REQUIRED).collect(Collectors.toSet());
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                FieldInfo field = entityInfo.getField(entry.getKey());
                Objects.requireNonNull(field, String.format("Entity %s does not have field %s", entityInfo.getEntityType().getName(), entry.getKey()));
                if (field.isPrimary()) {
                    Common.validatePrimaryKey(field, entry.getValue());
                } else if (!field.isNullable()) {
                    Objects.requireNonNull(entry.getValue(), String.format("Cannot set non-null field %s to null", entry.getKey()));
                    if (entry.getValue() instanceof String) {
                        Validate.isTrue((!StringUtils.isBlank((String)entry.getValue()) ? 1 : 0) != 0, (String)"Cannot set non-null String field %s to blank", (Object[])new Object[]{entry.getKey()});
                    }
                }
                requiredFields.remove(field);
                TypeInfo dbType = field.getTypeInfo();
                if (dbType == null || entry.getValue() == null) continue;
                dbType.getLogicalType().validate(entry.getValue());
            }
            if (requiredFields.isEmpty()) continue;
            throw new IllegalArgumentException("The following required fields were not set when trying to create entity '" + entityInfo.getEntityType().getName() + "': " + requiredFields.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
    }

    private <T extends RawEntity<K>, K> List<Map<String, Object>> injectAutoGeneratedFields(List<Map<String, Object>> rows, EntityInfo<T, K> entityInfo) {
        List autoGeneratedFields = entityInfo.getFields().stream().filter(arg_0 -> FieldInfo.HAS_GENERATOR.apply(arg_0)).collect(Collectors.toList());
        List<Map<String, Object>> result = rows.stream().map(HashMap::new).collect(Collectors.toList());
        for (FieldInfo fieldInfo : autoGeneratedFields) {
            ValueGenerator generator;
            try {
                generator = (ValueGenerator)this.valGenCache.get(fieldInfo.getGeneratorType());
            }
            catch (ExecutionError | UncheckedExecutionException | ExecutionException e) {
                throw Throwables.propagate((Throwable)e.getCause());
            }
            result.forEach(row -> row.put(fieldInfo.getName(), generator.generateValue(this)));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delete(RawEntity<?> ... entities) throws SQLException {
        if (entities.length == 0) {
            return;
        }
        HashMap organizedEntities = new HashMap();
        for (RawEntity<?> entity : entities) {
            this.verify(entity);
            Class<RawEntity<?>> type = this.getProxyForEntity(entity).getType();
            if (!organizedEntities.containsKey(type)) {
                organizedEntities.put(type, new LinkedList());
            }
            ((List)organizedEntities.get(type)).add(entity);
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.provider.getConnection();
            for (Class type : organizedEntities.keySet()) {
                Ticker ignored = Metrics.metric((String)"db.ao.entityManager.delete").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();
                Throwable throwable = null;
                try {
                    EntityInfo entityInfo = this.resolveEntityInfo(type);
                    List entityList = (List)organizedEntities.get(type);
                    StringBuilder sql = new StringBuilder("DELETE FROM ");
                    sql.append(this.provider.withSchema(entityInfo.getName()));
                    sql.append(" WHERE ").append(this.provider.processID(entityInfo.getPrimaryKey().getName())).append(" IN (?");
                    for (int i = 1; i < entityList.size(); ++i) {
                        sql.append(",?");
                    }
                    sql.append(')');
                    stmt = this.provider.preparedStatement(conn, sql);
                    int index = 1;
                    for (RawEntity entity : entityList) {
                        TypeInfo typeInfo = entityInfo.getPrimaryKey().getTypeInfo();
                        typeInfo.getLogicalType().putToDatabase(this, stmt, index++, Common.getPrimaryKeyValue(entity), typeInfo.getJdbcWriteType());
                    }
                    stmt.executeUpdate();
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (ignored == null) continue;
                    if (throwable != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    ignored.close();
                }
            }
        }
        finally {
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(conn);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <K> int deleteWithSQL(Class<? extends RawEntity<K>> type, String criteria, Object ... parameters) throws SQLException {
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.deleteWithSQL").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            int rowCount = 0;
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(this.provider.withSchema(this.nameConverters.getTableNameConverter().getName(type)));
            if (criteria != null) {
                sql.append(" WHERE ");
                sql.append(this.provider.processWhereClause(criteria));
            }
            try (Connection connection = this.provider.getConnection();
                 PreparedStatement stmt = this.provider.preparedStatement(connection, sql);){
                this.putStatementParameters(stmt, parameters);
                rowCount = stmt.executeUpdate();
            }
            int n = rowCount;
            return n;
        }
    }

    public <T extends RawEntity<K>, K> T[] find(Class<T> type) throws SQLException {
        return this.find(type, Query.select());
    }

    public <T extends RawEntity<K>, K> T[] find(Class<T> type, String criteria, Object ... parameters) throws SQLException {
        return this.find(type, Query.select().where(criteria, parameters));
    }

    public <T extends RawEntity<K>, K> T findSingleEntity(Class<T> type, String criteria, Object ... parameters) throws SQLException {
        RawEntity[] entities = this.find(type, criteria, parameters);
        if (entities.length < 1) {
            return null;
        }
        if (entities.length > 1) {
            throw new IllegalStateException("Found more than one entities of type '" + type.getSimpleName() + "' that matched the criteria '" + criteria + "' and parameters '" + parameters.toString() + "'.");
        }
        return (T)entities[0];
    }

    public <T extends RawEntity<K>, K> T[] find(Class<T> type, Query query) throws SQLException {
        EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
        query.resolvePrimaryKey(entityInfo.getPrimaryKey());
        String selectField = entityInfo.getPrimaryKey().getName();
        List queryFields = StreamSupport.stream(query.getFields().spliterator(), false).collect(Collectors.toList());
        if (queryFields.size() == 1) {
            selectField = (String)queryFields.get(0);
        }
        return this.find(type, selectField, query);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends RawEntity<K>, K> T[] find(Class<T> type, String field, Query query) throws SQLException {
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.find").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            Object selectedFields;
            ArrayList<T> back = new ArrayList<T>();
            EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
            query.resolvePrimaryKey(entityInfo.getPrimaryKey());
            Preload preloadAnnotation = type.getAnnotation(Preload.class);
            if (preloadAnnotation == null || ArrayUtils.contains((Object[])preloadAnnotation.value(), (Object)"*")) {
                selectedFields = Common.getValueFieldsNames(entityInfo, this.nameConverters.getFieldNameConverter());
            } else {
                selectedFields = new HashSet<String>(Common.preloadValue(preloadAnnotation, this.nameConverters.getFieldNameConverter()));
                for (String existingField : query.getFields()) {
                    selectedFields.add((String)existingField);
                }
            }
            query.setFields(selectedFields.toArray((String[])new String[selectedFields.size()]));
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet res = null;
            try {
                conn = this.provider.getConnection();
                String sql = query.toSQL(entityInfo, this.provider, this.getTableNameConverter(), false);
                stmt = this.provider.preparedStatement(conn, sql, 1004, 1007);
                this.provider.setQueryStatementProperties(stmt, query);
                query.setParameters(this, stmt);
                res = stmt.executeQuery();
                this.provider.setQueryResultSetProperties(res, query);
                TypeInfo fieldType = entityInfo.getField(field).getTypeInfo();
                Class fieldClassType = entityInfo.getField(field).getJavaType();
                String[] canonicalFields = query.getCanonicalFields(entityInfo);
                while (res.next()) {
                    T entity = this.peer(entityInfo, (K)fieldType.getLogicalType().pullFromDatabase(this, res, fieldClassType, field));
                    HashMap<String, Object> values = new HashMap<String, Object>();
                    for (String name : canonicalFields) {
                        FieldInfo fieldInfo = entityInfo.getField(name);
                        TypeInfo typeInfo = fieldInfo.getTypeInfo();
                        LogicalType logicalType = typeInfo.getLogicalType();
                        values.put(name, logicalType.pullFromDatabase(this, res, fieldInfo.getJavaType(), name));
                    }
                    if (!values.isEmpty()) {
                        EntityProxy<T, K> proxy = this.getProxyForEntity(entity);
                        proxy.updateValues(values);
                    }
                    back.add(entity);
                }
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(res, stmt, conn);
                throw throwable;
            }
            SqlUtils.closeQuietly(res, stmt, conn);
            RawEntity[] rawEntityArray = back.toArray((RawEntity[])Array.newInstance(type, back.size()));
            return rawEntityArray;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends RawEntity<K>, K> T[] findWithSQL(Class<T> type, String keyField, String sql, Object ... parameters) throws SQLException {
        ArrayList<T> back = new ArrayList<T>();
        EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            connection = this.provider.getConnection();
            stmt = this.provider.preparedStatement(connection, sql);
            this.putStatementParameters(stmt, parameters);
            res = stmt.executeQuery();
            while (res.next()) {
                back.add(this.peer(entityInfo, entityInfo.getPrimaryKey().getTypeInfo().getLogicalType().pullFromDatabase(this, res, type, keyField)));
            }
        }
        catch (Throwable throwable) {
            SqlUtils.closeQuietly(res);
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(connection);
            throw throwable;
        }
        SqlUtils.closeQuietly(res);
        SqlUtils.closeQuietly(stmt);
        SqlUtils.closeQuietly(connection);
        return back.toArray((RawEntity[])Array.newInstance(type, back.size()));
    }

    public <T extends RawEntity<K>, K> void stream(Class<T> type, EntityStreamCallback<T, K> streamCallback) throws SQLException {
        EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
        ImmutableSet<String> valueFields = Common.getValueFieldsNames(entityInfo, this.nameConverters.getFieldNameConverter());
        Query query = Query.select();
        query.setFields(valueFields.toArray((String[])new String[valueFields.size()]));
        this.stream(type, query, streamCallback);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends RawEntity<K>, K> void stream(Class<T> type, Query query, EntityStreamCallback<T, K> streamCallback) throws SQLException {
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.stream").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            EntityInfo<T, K> entityInfo = this.resolveEntityInfo(type);
            query.resolvePrimaryKey(entityInfo.getPrimaryKey());
            String[] canonicalFields = query.getCanonicalFields(entityInfo);
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet res = null;
            try {
                conn = this.provider.getConnection();
                String sql = query.toSQL(entityInfo, this.provider, this.getTableNameConverter(), false);
                stmt = this.provider.preparedStatement(conn, sql, 1004, 1007);
                this.provider.setQueryStatementProperties(stmt, query);
                query.setParameters(this, stmt);
                res = stmt.executeQuery();
                this.provider.setQueryResultSetProperties(res, query);
                ProxyCreationHandler<T, K> proxyCreationHandler = null;
                while (res.next()) {
                    if (proxyCreationHandler == null) {
                        proxyCreationHandler = this.returnCreationProxyHandler(entityInfo, res);
                    }
                    ReadOnlyEntityProxy<T, K> proxy = proxyCreationHandler.create(entityInfo, res);
                    T entity = this.castFromReadOnlyProxyToEntity(type, proxy);
                    for (String fieldName : canonicalFields) {
                        proxy.addValue(fieldName, res);
                    }
                    streamCallback.onRowRead(entity);
                }
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(res, stmt, conn);
                throw throwable;
            }
            SqlUtils.closeQuietly(res, stmt, conn);
        }
    }

    private <T extends RawEntity<K>, K> T castFromReadOnlyProxyToEntity(Class<T> entityClass, ReadOnlyEntityProxy<T, K> proxy) {
        return (T)((RawEntity)entityClass.cast(Proxy.newProxyInstance(entityClass.getClassLoader(), new Class[]{entityClass}, proxy)));
    }

    private <T extends RawEntity<K>, K> boolean resourceContainsIdColumn(EntityInfo<T, K> entityInfo, ResultSet res) throws SQLException {
        ResultSetMetaData resultSetMetaData = res.getMetaData();
        for (int columnNumber = 1; columnNumber <= resultSetMetaData.getColumnCount(); ++columnNumber) {
            if (!resultSetMetaData.getColumnName(columnNumber).equals(entityInfo.getPrimaryKey().getName())) continue;
            return true;
        }
        return false;
    }

    private <T extends RawEntity<K>, K> ProxyCreationHandler<T, K> returnCreationProxyHandler(EntityInfo<T, K> entityInfo, ResultSet res) throws SQLException {
        if (this.resourceContainsIdColumn(entityInfo, res)) {
            return (_entityInfo, _res) -> {
                Object primaryKey = _entityInfo.getPrimaryKey().getTypeInfo().getLogicalType().pullFromDatabase(this, _res, _entityInfo.getPrimaryKey().getJavaType(), _entityInfo.getPrimaryKey().getName());
                return this.createReadOnlyProxy(_entityInfo, primaryKey);
            };
        }
        log.debug("Id not found in column list, assuming no id");
        return (_entityInfo, _res) -> this.createReadOnlyProxy(_entityInfo, null);
    }

    private <T extends RawEntity<K>, K> ReadOnlyEntityProxy<T, K> createReadOnlyProxy(EntityInfo<T, K> entityInfo, K primaryKey) {
        return new ReadOnlyEntityProxy<T, K>(this, entityInfo, primaryKey);
    }

    public <K> int count(Class<? extends RawEntity<K>> type) throws SQLException {
        return this.count(type, Query.select());
    }

    public <K> int count(Class<? extends RawEntity<K>> type, String criteria, Object ... parameters) throws SQLException {
        return this.count(type, Query.select().where(criteria, parameters));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <K> int count(Class<? extends RawEntity<K>> type, Query query) throws SQLException {
        Throwable throwable = null;
        try (Ticker ignored = Metrics.metric((String)"db.ao.entityManager.count").tag(ENTITY_TYPE_TAG, type.getCanonicalName()).withAnalytics().invokerPluginKey(PluginKeyStack.getFirstPluginKey()).startTimer();){
            int n;
            EntityInfo<? extends RawEntity<K>, K> entityInfo = this.resolveEntityInfo(type);
            Connection connection = null;
            PreparedStatement stmt = null;
            ResultSet res = null;
            try {
                connection = this.provider.getConnection();
                String sql = query.toSQL(entityInfo, this.provider, this.getTableNameConverter(), true);
                stmt = this.provider.preparedStatement(connection, sql);
                this.provider.setQueryStatementProperties(stmt, query);
                query.setParameters(this, stmt);
                res = stmt.executeQuery();
                n = res.next() ? res.getInt(1) : -1;
            }
            catch (Throwable throwable2) {
                try {
                    SqlUtils.closeQuietly(res);
                    SqlUtils.closeQuietly(stmt);
                    SqlUtils.closeQuietly(connection);
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
            }
            SqlUtils.closeQuietly(res);
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(connection);
            return n;
        }
    }

    public <K> int getFastCountEstimate(Class<? extends RawEntity<K>> type) throws SQLException, FailedFastCountException {
        EntityInfo<RawEntity<K>, K> entityInfo = this.resolveEntityInfo(type);
        String tableName = entityInfo.getName();
        try {
            if (this.provider instanceof PostgreSQLDatabaseProvider) {
                return this.runQuery(String.format("SELECT reltuples FROM pg_catalog.pg_class WHERE relname = '%s'", tableName));
            }
            if (this.provider instanceof MySQLDatabaseProvider) {
                return this.runQuery(String.format("SELECT table_rows FROM information_schema.tables WHERE table_name = '%s';", tableName));
            }
        }
        catch (SQLException e) {
            throw new FailedFastCountException(e);
        }
        return this.count(type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int runQuery(String sqlQuery) throws SQLException {
        int n;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            connection = this.provider.getConnection();
            stmt = this.provider.preparedStatement(connection, sqlQuery);
            res = stmt.executeQuery();
            n = res.next() ? res.getInt(1) : -1;
        }
        catch (Throwable throwable) {
            SqlUtils.closeQuietly(res);
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(connection);
            throw throwable;
        }
        SqlUtils.closeQuietly(res);
        SqlUtils.closeQuietly(stmt);
        SqlUtils.closeQuietly(connection);
        return n;
    }

    public NameConverters getNameConverters() {
        return this.nameConverters;
    }

    protected <T extends RawEntity<K>, K> EntityInfo<T, K> resolveEntityInfo(Class<T> type) {
        return this.entityInfoResolver.resolve(type);
    }

    public TableNameConverter getTableNameConverter() {
        return this.nameConverters.getTableNameConverter();
    }

    public FieldNameConverter getFieldNameConverter() {
        return this.nameConverters.getFieldNameConverter();
    }

    public void setPolymorphicTypeMapper(PolymorphicTypeMapper typeMapper) {
        this.typeMapperLock.writeLock().lock();
        try {
            this.typeMapper = typeMapper;
            if (typeMapper instanceof DefaultPolymorphicTypeMapper) {
                ((DefaultPolymorphicTypeMapper)typeMapper).resolveMappings(this.getTableNameConverter());
            }
        }
        finally {
            this.typeMapperLock.writeLock().unlock();
        }
    }

    public PolymorphicTypeMapper getPolymorphicTypeMapper() {
        this.typeMapperLock.readLock().lock();
        try {
            if (this.typeMapper == null) {
                throw new RuntimeException("No polymorphic type mapper was specified");
            }
            PolymorphicTypeMapper polymorphicTypeMapper = this.typeMapper;
            return polymorphicTypeMapper;
        }
        finally {
            this.typeMapperLock.readLock().unlock();
        }
    }

    public DatabaseProvider getProvider() {
        return this.provider;
    }

    public SchemaConfiguration getSchemaConfiguration() {
        return this.schemaConfiguration;
    }

    <T extends RawEntity<K>, K> EntityProxy<T, K> getProxyForEntity(T entity) {
        return ((EntityProxyAccessor)((Object)entity)).getEntityProxy();
    }

    private void verify(RawEntity<?> entity) {
        if (entity.getEntityManager() != this) {
            throw new RuntimeException("Entities can only be used with a single EntityManager instance");
        }
    }

    private void putStatementParameters(PreparedStatement stmt, Object ... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; ++i) {
            Object parameter = parameters[i];
            Class<Object> entityTypeOrClass = parameter instanceof RawEntity ? ((RawEntity)parameter).getEntityType() : parameter.getClass();
            TypeInfo<?> typeInfo = this.provider.getTypeManager().getType(entityTypeOrClass);
            typeInfo.getLogicalType().putToDatabase(this, stmt, i + 1, parameter, typeInfo.getJdbcWriteType());
        }
    }

    private static interface ProxyCreationHandler<T extends RawEntity<K>, K> {
        public ReadOnlyEntityProxy<T, K> create(EntityInfo<T, K> var1, ResultSet var2) throws SQLException;
    }

    private static interface Function<R, F> {
        public R invoke(F var1) throws SQLException;
    }
}

