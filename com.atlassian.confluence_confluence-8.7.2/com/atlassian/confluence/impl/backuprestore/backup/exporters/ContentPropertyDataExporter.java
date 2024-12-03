/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentPropertyDataExporter
implements Exporter,
Subscriber {
    public static final String CONTENT_FIELD_NAME = "content";
    private final boolean keepCollectionsForContentProperties;
    private final CommonDatabaseDataExporter exporter;
    private final String ENTITY_BY_ID_QUERY;

    public ContentPropertyDataExporter(CommonDatabaseDataExporter exporter, boolean keepCollectionsForContentProperties) {
        this.exporter = exporter;
        this.keepCollectionsForContentProperties = keepCollectionsForContentProperties;
        this.ENTITY_BY_ID_QUERY = this.buildQuery(this.getEntityInfo());
    }

    private String buildQuery(ExportableEntityInfo entityInfo) {
        return "SELECT * FROM " + this.exporter.getHelper().checkNameDoesNotHaveSqlInjections(entityInfo.getTableName()) + " WHERE CONTENTID IN (:values)";
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.singleton(ContentEntityObject.class);
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (this.keepCollectionsForContentProperties) {
            return;
        }
        if (!ContentEntityObject.class.isAssignableFrom(exportedClass)) {
            throw new IllegalStateException("ContentDependantDataExporter should not receive any notifications except from ContentEntityObject");
        }
        this.exporter.exportInBatchByQueryWithInCondition(this.ENTITY_BY_ID_QUERY, "values", idList, "content properties export");
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.exporter.getConverter().getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.exporter.getConverter().getEntityInfo(exportedClass);
    }

    public Map<Long, List<EntityObjectReadyForExport>> findContentPropertiesForContentEntityObjects(Collection<?> contentEntityIds) {
        DatabaseExporterHelper helper = this.exporter.getHelper();
        List partitions = Lists.partition(new ArrayList(contentEntityIds), (int)helper.getBatchSize(this.getEntityInfo()));
        ArrayList<EntityObjectReadyForExport> contentProperties = new ArrayList<EntityObjectReadyForExport>();
        for (List partition : partitions) {
            List<EntityObjectReadyForExport> contentPropertiesPartition = this.exporter.getEntityObjectReadyForExports(this.ENTITY_BY_ID_QUERY, "values", partition);
            contentProperties.addAll(contentPropertiesPartition);
        }
        return contentProperties.stream().filter(e -> e.findReferenceByName(CONTENT_FIELD_NAME).isPresent()).collect(Collectors.groupingBy(e -> AbstractDatabaseDataConverter.convertToLong(e.findReferenceByName(CONTENT_FIELD_NAME).get().getReferencedId().getValue())));
    }
}

