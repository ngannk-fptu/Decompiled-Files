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
import com.atlassian.confluence.spaces.Space;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDatabaseDataExporter
implements Exporter,
Subscriber {
    private final List<Class<?>> monitoredClasses = Arrays.asList(Space.class, ContentEntityObject.class);
    private static final String CONTENT_IDS = "contentIds";
    private static final String SPACE_IDS = "spaceIds";
    private static final String NOTIFICATION_BY_CONTENT_ID_QUERY = "SELECT * FROM NOTIFICATIONS WHERE CONTENTID IN (:contentIds) AND SPACEID IS NULL";
    private static final String NOTIFICATION_BY_SPACE_ID_QUERY = "SELECT * FROM NOTIFICATIONS WHERE SPACEID IN (:spaceIds) AND CONTENTID IS NULL";
    CommonDatabaseDataExporter exporter;
    ExportableEntityInfo entityInfo;

    public NotificationDatabaseDataExporter(ExportableEntityInfo entityInfo, CommonDatabaseDataExporter exporter) {
        this.entityInfo = entityInfo;
        this.exporter = exporter;
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.unmodifiableList(this.monitoredClasses);
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
        if (ContentEntityObject.class.isAssignableFrom(exportedClass)) {
            this.exporter.exportInBatchByQueryWithInCondition(NOTIFICATION_BY_CONTENT_ID_QUERY, CONTENT_IDS, idList, "notifications export by content id");
        } else if (Space.class.equals(exportedClass)) {
            this.exporter.exportInBatchByQueryWithInCondition(NOTIFICATION_BY_SPACE_ID_QUERY, SPACE_IDS, idList, "notifications export by space id");
        } else {
            throw new IllegalStateException("NotificationDatabaseDataExporter should not receive any notifications except from " + this.monitoredClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) + ", but got " + exportedClass.getSimpleName());
        }
    }
}

