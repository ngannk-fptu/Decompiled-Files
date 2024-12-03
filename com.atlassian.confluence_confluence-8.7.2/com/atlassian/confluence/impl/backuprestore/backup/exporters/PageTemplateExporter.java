/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.Collections;

public class PageTemplateExporter
implements Exporter,
Subscriber {
    private final CommonDatabaseDataExporter exporter;
    private final ExportableEntityInfo entityInfo;
    private static final String SPACE_IDS = "spaceIds";
    private static final String NON_HISTORICAL_TEMPLATE_IDS_BY_SPACE_ID_QUERY = "SELECT TEMPLATEID AS ID FROM PAGETEMPLATES WHERE SPACEID IN (:spaceIds) AND PREVVER IS NULL";
    private static final String ALL_TEMPLATES_BY_SPACE_ID_QUERY = "SELECT PAGETEMPLATES.* FROM PAGETEMPLATES, (SELECT TEMPLATEID AS ID FROM PAGETEMPLATES WHERE SPACEID IN (:spaceIds) AND PREVVER IS NULL) NON_HISTORICAL_PAGES WHERE PREVVER = NON_HISTORICAL_PAGES.ID OR TEMPLATEID = NON_HISTORICAL_PAGES.ID";

    public PageTemplateExporter(ExportableEntityInfo entityInfo, CommonDatabaseDataExporter exporter) {
        this.entityInfo = entityInfo;
        this.exporter = exporter;
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
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.singleton(Space.class);
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (!exportedClass.equals(Space.class)) {
            throw new IllegalArgumentException("PageTemplate exporter listens to Space objects only, but received events from unexpected class: " + exportedClass);
        }
        this.exporter.exportInBatchByQueryWithInCondition(ALL_TEMPLATES_BY_SPACE_ID_QUERY, SPACE_IDS, idList, "page template export by space id");
    }
}

