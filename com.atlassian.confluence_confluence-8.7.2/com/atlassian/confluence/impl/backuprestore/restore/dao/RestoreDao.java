/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.id.IdentifierGenerator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.backuprestore.restore.dao;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class RestoreDao {
    private static final Logger log = LoggerFactory.getLogger(RestoreDao.class);
    private static final String HIBERNATE_UNIQUE_KEY_TABLE = "hibernate_unique_key";
    private static final String HIBERNATE_UNIQUE_KEY_COLUMN = "next_hi";
    private final SessionFactory sessionFactory;
    protected final PlatformTransactionManager transactionManager;
    protected final Supplier<NamedParameterJdbcTemplate> jdbcTemplateSupplier;

    public RestoreDao(SessionFactory sessionFactory, PlatformTransactionManager transactionManager) {
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.jdbcTemplateSupplier = this::getNamedParameterJdbcTemplate;
    }

    @VisibleForTesting
    public RestoreDao(SessionFactory sessionFactory, PlatformTransactionManager transactionManager, Supplier<NamedParameterJdbcTemplate> jdbcTemplateSupplier) {
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.jdbcTemplateSupplier = jdbcTemplateSupplier;
    }

    public int setNextHiValue(long nextHi) {
        String UPDATE_SQL = "update hibernate_unique_key set next_hi = :nextHi";
        return (Integer)this.doInTransaction(tx -> {
            Map<String, Long> params;
            NamedParameterJdbcTemplate jdbcTemplate = this.jdbcTemplateSupplier.get();
            int updatedRecordsCount = jdbcTemplate.update("update hibernate_unique_key set next_hi = :nextHi", params = Map.of("nextHi", nextHi));
            if (updatedRecordsCount > 0) {
                return updatedRecordsCount;
            }
            String INSERT_SQL = "insert into hibernate_unique_key values(:nextHi)";
            return jdbcTemplate.update("insert into hibernate_unique_key values(:nextHi)", params);
        });
    }

    public Long getNextHiValue() {
        String sql = "select next_hi from hibernate_unique_key";
        Object nextHi = this.doInTransaction(tx -> this.jdbcTemplateSupplier.get().queryForObject("select next_hi from hibernate_unique_key", Collections.emptyMap(), Object.class));
        return AbstractDatabaseDataConverter.convertToLong(nextHi);
    }

    public Collection<Object> generateIds(IdentifierGenerator identifierGenerator, Object fakeObject, int count) {
        return (Collection)this.doInTransaction(tx -> {
            Session session = this.sessionFactory.getCurrentSession();
            return Stream.iterate(0, i -> i + 1).limit(count).map(i -> identifierGenerator.generate((SharedSessionContractImplementor)((SessionImplementor)session), fakeObject)).collect(Collectors.toList());
        });
    }

    public Collection<ImportedObjectV2> insertRecordsInTransaction(Collection<RecordsForBatchInsert> recordsForBatchInserts) {
        try {
            this.doInTransaction(tx -> {
                recordsForBatchInserts.forEach(records -> {
                    NamedParameterJdbcTemplate jdbcTemplate = this.jdbcTemplateSupplier.get();
                    jdbcTemplate.batchUpdate(records.insertSql, (Map[])records.values);
                });
                return null;
            });
            return recordsForBatchInserts.stream().flatMap(batch -> batch.getImportedObjects().stream()).collect(Collectors.toList());
        }
        catch (Exception e) {
            log.warn("Unable to persist batch of records. Records will be inserted one by one: " + e.getMessage());
            return recordsForBatchInserts.stream().map(this::insertRecordsOneByOne).flatMap(Collection::stream).collect(Collectors.toList());
        }
    }

    private Collection<ImportedObjectV2> insertRecordsOneByOne(RecordsForBatchInsert recordsForBatchInserts) {
        ArrayList<ImportedObjectV2> persistedObjects = new ArrayList<ImportedObjectV2>();
        for (int i = 0; i < recordsForBatchInserts.importedObjects.size(); ++i) {
            Map<String, Object> valuesForTheRecord = recordsForBatchInserts.values[i];
            try {
                this.doInTransaction(tx -> {
                    NamedParameterJdbcTemplate jdbcTemplate = this.jdbcTemplateSupplier.get();
                    jdbcTemplate.update(recordsForBatchInserts.insertSql, valuesForTheRecord);
                    return null;
                });
                persistedObjects.add(recordsForBatchInserts.getImportedObjects().get(i));
                continue;
            }
            catch (Exception e) {
                log.warn("Record was not persisted to the database: " + e.getMessage(), (Throwable)e);
            }
        }
        return persistedObjects;
    }

    public List<DbRawObjectData> runNativeQueryInTransaction(String query, Map<String, ?> paramMap, int limit) {
        return (List)this.doInTransaction(tx -> {
            NamedParameterJdbcTemplate template = this.getNamedParameterJdbcTemplate();
            template.getJdbcTemplate().setMaxRows(limit);
            List dbProperties = template.queryForList(query, paramMap);
            return dbProperties.stream().map(DbRawObjectData::new).collect(Collectors.toList());
        });
    }

    public int runNativeUpdateQuery(String query, Map<String, ?> paramMap) {
        return (Integer)this.doInTransaction(tx -> {
            NamedParameterJdbcTemplate template = this.getNamedParameterJdbcTemplate();
            return template.update(query, paramMap);
        });
    }

    private NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        DataSource dataSource = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).getDataSource();
        if (dataSource == null) {
            throw new IllegalStateException("dataSource is empty");
        }
        return new NamedParameterJdbcTemplate(dataSource);
    }

    public <T> T doInTransaction(TransactionCallback<T> callback) {
        return this.doInTransaction(callback, false);
    }

    public <T> T doInReadOnlyTransaction(TransactionCallback<T> callback) {
        return this.doInTransaction(callback, true);
    }

    private <T> T doInTransaction(TransactionCallback<T> callback, boolean readOnly) {
        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(3);
        if (readOnly) {
            transactionAttribute.setReadOnly(true);
        }
        return (T)new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionAttribute).execute(callback);
    }

    public static class RecordsForBatchInsert {
        private final String insertSql;
        private final Map<String, Object>[] values;
        private final List<ImportedObjectV2> importedObjects;

        public RecordsForBatchInsert(String insertSql, Map<String, Object>[] values, List<ImportedObjectV2> importedObjects) {
            this.importedObjects = importedObjects;
            this.insertSql = insertSql;
            this.values = (Map[])values.clone();
        }

        @VisibleForTesting
        public Map<String, Object>[] getValues() {
            return (Map[])this.values.clone();
        }

        public List<ImportedObjectV2> getImportedObjects() {
            return this.importedObjects;
        }
    }
}

