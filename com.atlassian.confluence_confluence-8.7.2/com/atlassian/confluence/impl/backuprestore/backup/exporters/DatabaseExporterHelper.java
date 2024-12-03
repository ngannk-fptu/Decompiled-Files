/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ContentEntityDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Persister;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SpaceDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.helpers.TableAndFieldNameValidator;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class DatabaseExporterHelper {
    private final BackupContainerWriter containerWriter;
    private final HibernateMetadataHelper hibernateMetadataHelper;
    private final ParallelTasksExecutor parallelTasksExecutor;
    private final PlatformTransactionManager transactionManager;
    private final SessionFactory sessionFactory;
    private final StatisticsCollector statisticsCollector;
    private final AtomicReference<Collection<Exporter>> exporters = new AtomicReference();
    private final Map<Class<?>, Persister> simpleExporters = new HashMap();
    private final Map<Class<?>, List<Subscriber>> exportersWatchingEntityClasses = new HashMap();
    private ContentEntityDatabaseDataExporter contentEntityDatabaseDataExporter;
    protected static final int PROCESSING_BATCH_SIZE = Integer.getInteger("confluence.backup.processing-batch-size", 1000);
    protected static final int PROCESSING_BATCH_SIZE_FOR_LARGE_ENTITIES = Integer.getInteger("confluence.backup.processing-batch-size-for-large-entities", PROCESSING_BATCH_SIZE / 10);

    public DatabaseExporterHelper(BackupContainerWriter containerWriter, HibernateMetadataHelper hibernateMetadataHelper, ParallelTasksExecutor parallelTasksExecutor, PlatformTransactionManager transactionManager, SessionFactory sessionFactory, StatisticsCollector statisticsCollector) {
        this.containerWriter = containerWriter;
        this.hibernateMetadataHelper = hibernateMetadataHelper;
        this.parallelTasksExecutor = parallelTasksExecutor;
        this.transactionManager = transactionManager;
        this.sessionFactory = sessionFactory;
        this.statisticsCollector = statisticsCollector;
    }

    public BackupContainerWriter getContainerWriter() {
        return this.containerWriter;
    }

    public HibernateMetadataHelper getHibernateMetadataHelper() {
        return this.hibernateMetadataHelper;
    }

    public ParallelTasksExecutor getParallelTasksExecutor() {
        return this.parallelTasksExecutor;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void writeAllReferencedSimpleObjects(Collection<EntityObjectReadyForExport> entities) throws InterruptedException, BackupRestoreException {
        HashMap entitiesByClass = new HashMap();
        entities.forEach(entity -> {
            for (EntityObjectReadyForExport.Reference reference : entity.getReferences()) {
                entitiesByClass.computeIfAbsent(reference.getReferencedClazz(), k -> new ArrayList()).add(reference.getReferencedId().getValue());
            }
        });
        for (Map.Entry entry : entitiesByClass.entrySet()) {
            Class entityClass = (Class)entry.getKey();
            Persister simpleEntityExporter = this.simpleExporters.get(entityClass);
            if (simpleEntityExporter != null) {
                simpleEntityExporter.persistObjects((Collection)entry.getValue());
            }
            if (this.contentEntityDatabaseDataExporter == null || !CustomContentEntityObject.class.isAssignableFrom(entityClass)) continue;
            this.contentEntityDatabaseDataExporter.persistObjects((Collection)entry.getValue());
        }
    }

    public void notifyExportersAboutPersistedObjects(Collection<EntityObjectReadyForExport> exportedObjects) throws InterruptedException, BackupRestoreException {
        Map objectsByClass = exportedObjects.stream().collect(Collectors.groupingBy(EntityObjectReadyForExport::getClazz, Collectors.toList()));
        for (Map.Entry entry : objectsByClass.entrySet()) {
            List<Object> ids = entry.getValue().stream().filter(r -> r.getIds().size() == 1).map(r -> r.getId().getValue()).collect(Collectors.toList());
            if (ContentEntityObject.class.isAssignableFrom(entry.getKey())) {
                this.fireNotificationsForClass(ContentEntityObject.class, ids);
                continue;
            }
            this.fireNotificationsForClass(entry.getKey(), ids);
        }
    }

    public void registerSubscriber(Subscriber exporter) {
        Collection<Class<?>> watchedClasses = exporter.getWatchingEntityClasses();
        watchedClasses.forEach(aClass -> this.exportersWatchingEntityClasses.computeIfAbsent((Class<?>)aClass, k -> new ArrayList()).add(exporter));
    }

    private void fireNotificationsForClass(Class<?> clazz, List<Object> idList) throws InterruptedException, BackupRestoreException {
        List exportersToReceiveEvents = this.exportersWatchingEntityClasses.getOrDefault(clazz, Collections.emptyList());
        for (Subscriber exporter : exportersToReceiveEvents) {
            exporter.onMonitoredObjectsExport(clazz, idList);
        }
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        DataSource dataSource = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).getDataSource();
        if (dataSource == null) {
            throw new IllegalStateException("dataSource is empty");
        }
        return new NamedParameterJdbcTemplate(dataSource);
    }

    public <T> T doInReadOnlyTransaction(TransactionCallback<T> callback) {
        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(3);
        transactionAttribute.setReadOnly(true);
        return (T)new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionAttribute).execute(callback);
    }

    public ExportableEntityInfo getEntityInfoByClass(Class<?> entityClazz) {
        return this.getHibernateMetadataHelper().getEntityInfoByClass(entityClazz);
    }

    public void setAllExporters(Collection<Exporter> exporters) {
        this.exporters.set(Collections.unmodifiableCollection(exporters));
        exporters.forEach(exporter -> {
            if (exporter instanceof Subscriber) {
                this.registerSubscriber((Subscriber)((Object)exporter));
            }
        });
        exporters.forEach(exporter -> {
            if (exporter instanceof Persister) {
                if (exporter instanceof ContentEntityDatabaseDataExporter) {
                    this.contentEntityDatabaseDataExporter = (ContentEntityDatabaseDataExporter)exporter;
                } else {
                    this.simpleExporters.put(exporter.getEntityInfo().getEntityClass(), (Persister)((Object)exporter));
                }
            }
        });
    }

    public SpaceDatabaseDataExporter findSpaceDatabaseExporters() {
        for (Exporter exporter : this.exporters.get()) {
            if (!(exporter instanceof SpaceDatabaseDataExporter)) continue;
            return (SpaceDatabaseDataExporter)exporter;
        }
        throw new IllegalStateException("Unable to find SpaceDatabaseDataExporters in the list");
    }

    public void writeObjects(Collection<EntityObjectReadyForExport> entities) throws BackupRestoreException {
        this.getContainerWriter().writeObjects(entities);
    }

    public void writeObjectsAndNotifyOtherExporters(Collection<EntityObjectReadyForExport> entities) throws BackupRestoreException, InterruptedException {
        this.writeObjects(entities);
        this.writeAllReferencedSimpleObjects(entities);
        this.notifyExportersAboutPersistedObjects(entities);
        this.statisticsCollector.onObjectsExporting(entities);
    }

    public int getBatchSize(ExportableEntityInfo entityInfo) {
        boolean largeEntity = BodyContent.class.equals(entityInfo.getEntityClass());
        return largeEntity ? PROCESSING_BATCH_SIZE_FOR_LARGE_ENTITIES : PROCESSING_BATCH_SIZE;
    }

    public void runTaskAsync(Callable<Void> task, String callerName) {
        this.getParallelTasksExecutor().runTaskAsync(task, callerName);
    }

    @Deprecated
    public String checkNameDoesNotHaveSqlInjections(String tableOrFieldName) {
        if (TableAndFieldNameValidator.TABLE_OR_FIELD_NAME_PATTERN.matcher(tableOrFieldName).find()) {
            return tableOrFieldName;
        }
        throw new IllegalArgumentException("Table or field name is not allowed: " + tableOrFieldName);
    }

    public List<DbRawObjectData> runQueryWithInCondition(String query, String fieldName, Collection<?> values) {
        NamedParameterJdbcTemplate template = this.getNamedParameterJdbcTemplate();
        Map<String, Collection<?>> paramMap = Collections.singletonMap(fieldName, values);
        List dbProperties = template.queryForList(query, paramMap);
        return dbProperties.stream().map(DbRawObjectData::new).collect(Collectors.toList());
    }

    public List<DbRawObjectData> runNativeQueryInTransaction(String query, Map<String, ?> paramMap, int limit) {
        return (List)this.doInReadOnlyTransaction(tx -> {
            NamedParameterJdbcTemplate template = this.getNamedParameterJdbcTemplate();
            template.getJdbcTemplate().setMaxRows(limit);
            List dbProperties = template.queryForList(query, paramMap);
            return dbProperties.stream().map(DbRawObjectData::new).collect(Collectors.toList());
        });
    }

    public int getRegularBatchSize() {
        return PROCESSING_BATCH_SIZE;
    }
}

