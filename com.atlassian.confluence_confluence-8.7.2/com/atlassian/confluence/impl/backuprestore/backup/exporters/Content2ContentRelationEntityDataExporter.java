/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Content2ContentRelationEntityDataExporter
implements Exporter,
Subscriber {
    private final List<Class<?>> monitoredClasses = Collections.singletonList(ContentEntityObject.class);
    private static final String CONTENT_IDS = "contentIds";
    private static final String CONTENT_RELATION_BY_CONTENT_ID = "SELECT * FROM CONTENT_RELATION WHERE (TARGETCONTENTID IN (:contentIds) OR SOURCECONTENTID IN (:contentIds)) AND (TARGETTYPE != 'SPACE' OR SOURCETYPE != 'SPACE')";
    CommonDatabaseDataExporter exporter;
    ExportableEntityInfo entityInfo;

    public Content2ContentRelationEntityDataExporter(ExportableEntityInfo entityInfo, CommonDatabaseDataExporter exporter) {
        this.entityInfo = entityInfo;
        this.exporter = exporter;
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return this.monitoredClasses;
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.entityInfo;
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        if (!this.entityInfo.getEntityClass().equals(exportedClass)) {
            throw new IllegalArgumentException("Unable to find entity info for class " + exportedClass);
        }
        return this.entityInfo;
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (!ContentEntityObject.class.isAssignableFrom(exportedClass)) {
            String monitoredClassesName = this.monitoredClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(", "));
            throw new IllegalStateException("Content2ContentRelationEntityDataExporter should not receive any notifications except from " + monitoredClassesName + ", but got " + exportedClass.getSimpleName());
        }
        this.exporter.exportInBatchByQueryWithInCondition(CONTENT_RELATION_BY_CONTENT_ID, CONTENT_IDS, idList, "content relation export by content id");
    }
}

