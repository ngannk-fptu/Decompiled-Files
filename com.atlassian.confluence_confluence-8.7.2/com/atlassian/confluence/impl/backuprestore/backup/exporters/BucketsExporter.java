/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.Collection;
import java.util.Collections;

public class BucketsExporter
implements Exporter,
Subscriber {
    private final String ENTITY_BY_ID_QUERY;
    private final Converter converter;
    private final DatabaseExporterHelper helper;
    private final CommonDatabaseDataExporter exporter;

    public BucketsExporter(CommonDatabaseDataExporter exporter) {
        this.converter = exporter.getConverter();
        this.helper = exporter.getHelper();
        this.exporter = exporter;
        this.ENTITY_BY_ID_QUERY = "SELECT * FROM " + this.helper.checkNameDoesNotHaveSqlInjections(this.converter.getEntityInfo().getTableName()) + " WHERE entity_id IN (:ids) AND entity_name = 'confluence_ContentEntityObject'";
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.converter.getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.converter.getEntityInfo(exportedClass);
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (!ContentEntityObject.class.isAssignableFrom(exportedClass)) {
            throw new IllegalArgumentException("Bucket exporter listens to ContentEntityObject objects only, but received events from unexpected class: " + exportedClass);
        }
        this.exporter.exportUniqueInBatchByQueryWithInCondition(this.ENTITY_BY_ID_QUERY, "ids", idList, this.getEntityInfo().getEntityClass().getSimpleName());
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.singleton(ContentEntityObject.class);
    }
}

