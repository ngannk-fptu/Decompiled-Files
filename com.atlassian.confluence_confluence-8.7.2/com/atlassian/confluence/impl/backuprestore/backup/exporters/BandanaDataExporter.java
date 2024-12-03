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

public class BandanaDataExporter
implements Exporter,
Subscriber {
    private final CommonDatabaseDataExporter exporter;
    private final ExportableEntityInfo entityInfo;
    private static final String SPACE_IDS = "spaceIds";
    private static final String ENTITY_BY_CONTEXT_QUERY = "SELECT BANDANA.* FROM BANDANA JOIN SPACES ON SPACES.SPACEKEY = BANDANA.BANDANACONTEXT WHERE SPACES.SPACEID IN  (:spaceIds)";

    public BandanaDataExporter(ExportableEntityInfo entityInfo, CommonDatabaseDataExporter exporter) {
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
            throw new IllegalArgumentException("Bandana exporter listens to Space objects only, but received events from unexpected class: " + exportedClass);
        }
        this.exporter.exportInBatchByQueryWithInCondition(ENTITY_BY_CONTEXT_QUERY, SPACE_IDS, idList, "bandana export by space id");
    }
}

