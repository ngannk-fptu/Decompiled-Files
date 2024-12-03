/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SingleDependencyGenericExporter
implements Exporter,
Subscriber {
    protected final CommonDatabaseDataExporter commonExporter;
    protected final Class<?> monitoredClass;
    protected final String ENTITY_BY_ID_QUERY;

    public SingleDependencyGenericExporter(CommonDatabaseDataExporter commonExporter, ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        this.commonExporter = commonExporter;
        this.monitoredClass = this.getMonitoredClass(entityInfo, simpleEntities);
        this.ENTITY_BY_ID_QUERY = this.buildQuery(entityInfo, simpleEntities);
    }

    protected String buildQuery(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        HibernateField theOnlyExternalReference = SingleDependencyGenericExporter.findTheOnlyMonitoredReference(entityInfo, simpleEntities);
        return "SELECT * FROM " + this.commonExporter.getHelper().checkNameDoesNotHaveSqlInjections(entityInfo.getTableName()) + " WHERE " + this.commonExporter.getHelper().checkNameDoesNotHaveSqlInjections(theOnlyExternalReference.getSingleColumnName()) + " IN (:values)";
    }

    protected Class<?> getMonitoredClass(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        HibernateField theOnlyExternalReference = SingleDependencyGenericExporter.findTheOnlyMonitoredReference(entityInfo, simpleEntities);
        return theOnlyExternalReference.getReferencedClass();
    }

    public static boolean isSuitableForExporter(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        return SingleDependencyGenericExporter.getAllExternalReferencesExceptSimpleOnes(entityInfo, simpleEntities).size() == 1;
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.singleton(this.monitoredClass);
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.commonExporter.getConverter().getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.commonExporter.getConverter().getEntityInfo(exportedClass);
    }

    protected static List<HibernateField> getAllExternalReferencesExceptSimpleOnes(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        return entityInfo.getFields().stream().filter(f -> f.getReferencedClass() != null).filter(f -> !simpleEntities.contains(f.getReferencedClass())).collect(Collectors.toList());
    }

    private static HibernateField findTheOnlyMonitoredReference(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        List<HibernateField> externalReferences = SingleDependencyGenericExporter.getAllExternalReferencesExceptSimpleOnes(entityInfo, simpleEntities);
        if (externalReferences.size() != 1) {
            throw new IllegalStateException("The generic persister expects to have only one external reference, but found " + externalReferences.size() + " ones.");
        }
        return externalReferences.get(0);
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (!this.monitoredClass.equals(exportedClass)) {
            throw new IllegalStateException("GenericDataExporter with entity class " + this.getEntityInfo().getEntityClass().getSimpleName() + " should not receive any notifications except from " + this.monitoredClass.getSimpleName() + ", but got " + exportedClass.getSimpleName());
        }
        this.export(idList);
    }

    protected void export(Collection<Object> objectIdList) {
        this.commonExporter.exportInBatchByQueryWithInCondition(this.ENTITY_BY_ID_QUERY, "values", objectIdList, "simple entity export " + this.getEntityInfo().getEntityClass().getSimpleName());
    }
}

