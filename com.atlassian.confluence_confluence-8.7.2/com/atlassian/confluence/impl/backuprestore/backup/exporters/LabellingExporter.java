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
import com.atlassian.confluence.pages.templates.PageTemplate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LabellingExporter
implements Exporter,
Subscriber {
    private final List<Class<?>> monitoredClasses = Arrays.asList(PageTemplate.class, ContentEntityObject.class);
    private static final String CONTENT_IDS = "contentIds";
    private static final String PAGE_TEMPLATE_IDS = "pageTemplateIds";
    private static final String LABELLING_BY_CONTENT_ID_QUERY = "SELECT * FROM CONTENT_LABEL WHERE CONTENTID IN (:contentIds)";
    private static final String LABELLING_BY_PAGE_TEMPLATE_ID_QUERY = "SELECT * FROM CONTENT_LABEL WHERE PAGETEMPLATEID IN (:pageTemplateIds)";
    ExportableEntityInfo entityInfo;
    CommonDatabaseDataExporter exporter;

    public LabellingExporter(ExportableEntityInfo entityInfo, CommonDatabaseDataExporter exporter) {
        this.entityInfo = entityInfo;
        this.exporter = exporter;
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (ContentEntityObject.class.isAssignableFrom(exportedClass)) {
            this.exporter.exportInBatchByQueryWithInCondition(LABELLING_BY_CONTENT_ID_QUERY, CONTENT_IDS, idList, "labelling export by content id");
        } else if (PageTemplate.class.equals(exportedClass)) {
            this.exporter.exportInBatchByQueryWithInCondition(LABELLING_BY_PAGE_TEMPLATE_ID_QUERY, PAGE_TEMPLATE_IDS, idList, "labelling export by page template id");
        } else {
            throw new IllegalStateException("LabellingExporter should not receive any notifications except from " + this.monitoredClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) + ", but got " + exportedClass.getSimpleName());
        }
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
}

