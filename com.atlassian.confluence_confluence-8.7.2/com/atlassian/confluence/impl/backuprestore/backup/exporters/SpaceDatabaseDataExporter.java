/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceDatabaseDataExporter
implements Exporter {
    private static final int SPACE_LIMIT = Integer.getInteger("confluence.restore.space-limit", 1000);
    private static final String SPACE_BY_KEY_QUERY = "SELECT * FROM SPACES WHERE LOWERSPACEKEY IN (:spaceKeys)";
    private final Converter converter;
    private final DatabaseExporterHelper helper;

    public SpaceDatabaseDataExporter(Converter converter, DatabaseExporterHelper helper) {
        this.converter = converter;
        this.helper = helper;
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.converter.getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.converter.getEntityInfo(exportedClass);
    }

    public void export(Collection<String> spaceKeys) throws BackupRestoreException {
        if (spaceKeys.size() > SPACE_LIMIT) {
            throw new IllegalArgumentException("Too many spaces are exported. Max " + SPACE_LIMIT + " spaces allowed.");
        }
        Collection<EntityObjectReadyForExport> spaceEntityObjectsReadyForExport = this.retrieveSpacesFromDB(spaceKeys);
        this.helper.runTaskAsync(() -> {
            this.helper.writeObjectsAndNotifyOtherExporters(spaceEntityObjectsReadyForExport);
            return null;
        }, "persisting space");
    }

    private Collection<EntityObjectReadyForExport> retrieveSpacesFromDB(Collection<String> spaceKeys) throws BackupRestoreException {
        Collection lowerSpaceKeys = spaceKeys.stream().map(String::toLowerCase).collect(Collectors.toList());
        List spaceEntitiesReadyForExport = (List)this.helper.doInReadOnlyTransaction(tx -> {
            List<DbRawObjectData> rawObjectData = this.helper.runQueryWithInCondition(SPACE_BY_KEY_QUERY, "spaceKeys", lowerSpaceKeys);
            return this.converter.convertToObjectsReadyForSerialisation(rawObjectData);
        });
        if (lowerSpaceKeys.size() != spaceEntitiesReadyForExport.size()) {
            List foundSpaces = spaceEntitiesReadyForExport.stream().map(space -> space.getProperty("spaceKey").getValue()).collect(Collectors.toList());
            throw new BackupRestoreException("Some spaces to export are not found. Space to export: " + spaceKeys + ", found spaces: " + foundSpaces);
        }
        return spaceEntitiesReadyForExport;
    }
}

