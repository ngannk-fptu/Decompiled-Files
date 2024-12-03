/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Persister;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.Collection;
import java.util.Set;

public class SimpleEntityExporter
implements Exporter,
Persister {
    private final String ENTITY_BY_ID_QUERY;
    private final Converter converter;
    private final DatabaseExporterHelper helper;
    private final CommonDatabaseDataExporter exporter;

    public SimpleEntityExporter(CommonDatabaseDataExporter exporter) {
        this.converter = exporter.getConverter();
        this.helper = exporter.getHelper();
        this.exporter = exporter;
        this.ENTITY_BY_ID_QUERY = "SELECT * FROM " + this.helper.checkNameDoesNotHaveSqlInjections(this.converter.getEntityInfo().getTableName()) + " WHERE " + this.helper.checkNameDoesNotHaveSqlInjections(this.converter.getEntityInfo().getId().getSingleColumnName()) + " IN (:ids)";
    }

    @Override
    public void persistObjects(Collection<Object> objectIds) throws InterruptedException, BackupRestoreException {
        this.exporter.exportUniqueInBatchByQueryWithInCondition(this.ENTITY_BY_ID_QUERY, "ids", objectIds, this.getEntityInfo().getEntityClass().getSimpleName());
    }

    public static boolean isSuitableForExporter(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntities) {
        return entityInfo.getAllExternalReferences().stream().allMatch(r -> simpleEntities.contains(r.getReferencedClass()));
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.converter.getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.converter.getEntityInfo(exportedClass);
    }
}

