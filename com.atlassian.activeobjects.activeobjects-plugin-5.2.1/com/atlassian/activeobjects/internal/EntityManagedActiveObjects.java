/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.sal.api.transaction.TransactionCallback
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsModuleMetaData;
import com.atlassian.activeobjects.external.FailedFastCountException;
import com.atlassian.activeobjects.internal.AbstractActiveObjectsMetaData;
import com.atlassian.activeobjects.internal.ActiveObjectsSqlException;
import com.atlassian.activeobjects.internal.TransactionManager;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.sal.api.transaction.TransactionCallback;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.java.ao.DBParam;
import net.java.ao.DatabaseProvider;
import net.java.ao.DefaultPolymorphicTypeMapper;
import net.java.ao.EntityManager;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.helper.DatabaseMetaDataReaderImpl;

public class EntityManagedActiveObjects
implements ActiveObjects {
    private final DatabaseType dbType;
    private final EntityManager entityManager;
    private final TransactionManager transactionManager;

    protected EntityManagedActiveObjects(EntityManager entityManager, TransactionManager transactionManager, DatabaseType dbType) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.dbType = Objects.requireNonNull(dbType);
    }

    @Override
    public final void migrate(Class<? extends RawEntity<?>> ... entities) {
        try {
            this.entityManager.setPolymorphicTypeMapper(new DefaultPolymorphicTypeMapper(entities));
            this.entityManager.migrate(entities);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public void migrateDestructively(Class<? extends RawEntity<?>> ... entities) {
        try {
            this.entityManager.setPolymorphicTypeMapper(new DefaultPolymorphicTypeMapper(entities));
            this.entityManager.migrateDestructively(entities);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final void flushAll() {
        this.entityManager.flushAll();
    }

    @Override
    public final void flush(RawEntity<?> ... entities) {
        this.entityManager.flush(entities);
    }

    @Override
    public final <T extends RawEntity<K>, K> T[] get(Class<T> type, K ... keys) {
        try {
            return this.entityManager.get(type, keys);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T get(Class<T> type, K key) {
        try {
            return this.entityManager.get(type, key);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T create(Class<T> type, DBParam ... params) {
        try {
            return this.entityManager.create(type, params);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T create(Class<T> type, Map<String, Object> params) {
        try {
            return this.entityManager.create(type, params);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public <T extends RawEntity<K>, K> void create(Class<T> type, List<Map<String, Object>> rows) {
        try {
            this.entityManager.create(type, rows);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final void delete(RawEntity<?> ... entities) {
        try {
            this.entityManager.delete(entities);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public <K> int deleteWithSQL(Class<? extends RawEntity<K>> type, String criteria, Object ... parameters) {
        try {
            return this.entityManager.deleteWithSQL(type, criteria, parameters);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T[] find(Class<T> type) {
        try {
            return this.entityManager.find(type);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T[] find(Class<T> type, String criteria, Object ... parameters) {
        try {
            return this.entityManager.find(type, criteria, parameters);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T[] find(Class<T> type, Query query) {
        try {
            return this.entityManager.find(type, query);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T[] find(Class<T> type, String field, Query query) {
        try {
            return this.entityManager.find(type, field, query);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> T[] findWithSQL(Class<T> type, String keyField, String sql, Object ... parameters) {
        try {
            return this.entityManager.findWithSQL(type, keyField, sql, parameters);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> void stream(Class<T> type, Query query, EntityStreamCallback<T, K> streamCallback) {
        try {
            this.entityManager.stream(type, query, streamCallback);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <T extends RawEntity<K>, K> void stream(Class<T> type, EntityStreamCallback<T, K> streamCallback) {
        try {
            this.entityManager.stream(type, streamCallback);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <K> int count(Class<? extends RawEntity<K>> type) {
        try {
            return this.entityManager.count(type);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <K> int count(Class<? extends RawEntity<K>> type, String criteria, Object ... parameters) {
        try {
            return this.entityManager.count(type, criteria, parameters);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public final <K> int count(Class<? extends RawEntity<K>> type, Query query) {
        try {
            return this.entityManager.count(type, query);
        }
        catch (SQLException e) {
            throw new ActiveObjectsSqlException(this.entityManager, e);
        }
    }

    @Override
    public <K> int getFastCountEstimate(Class<? extends RawEntity<K>> type) throws SQLException, FailedFastCountException {
        try {
            return this.entityManager.getFastCountEstimate(type);
        }
        catch (net.java.ao.FailedFastCountException e) {
            throw new FailedFastCountException(e);
        }
    }

    @Override
    public final <T> T executeInTransaction(TransactionCallback<T> callback) {
        return this.transactionManager.doInTransaction(callback);
    }

    @Override
    public ActiveObjectsModuleMetaData moduleMetaData() {
        class EntityAOModuleMetaData
        extends AbstractActiveObjectsMetaData {
            EntityAOModuleMetaData() {
                super(EntityManagedActiveObjects.this.dbType);
            }

            @Override
            public boolean isInitialized() {
                return false;
            }

            @Override
            public boolean isDataSourcePresent() {
                return false;
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public boolean isTablePresent(Class<? extends RawEntity<?>> type) {
                DatabaseProvider databaseProvider = EntityManagedActiveObjects.this.entityManager.getProvider();
                NameConverters nameConverters = EntityManagedActiveObjects.this.entityManager.getNameConverters();
                SchemaConfiguration schemaConfiguration = EntityManagedActiveObjects.this.entityManager.getSchemaConfiguration();
                DatabaseMetaDataReaderImpl databaseMetaDataReader = new DatabaseMetaDataReaderImpl(databaseProvider, nameConverters, schemaConfiguration);
                try (Connection connection = databaseProvider.getConnection();){
                    boolean bl = databaseMetaDataReader.isTablePresent(connection.getMetaData(), type);
                    return bl;
                }
                catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void awaitInitialization() {
                throw new UnsupportedOperationException("Cannot call awaitModelInitialization directly on EntityManagedActiveObjects.\nawaitModelInitialization should not be called from within an upgrade task");
            }

            @Override
            public void awaitInitialization(long timeout, TimeUnit unit) {
                throw new UnsupportedOperationException("Cannot call awaitModelInitialization directly on EntityManagedActiveObjects.\nawaitModelInitialization should not be called from within an upgrade task");
            }
        }
        return new EntityAOModuleMetaData();
    }
}

