/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.CommonDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.ExportObjectsEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.List;

public class EntityWithAdditionalDataConverter
implements Converter {
    private final CommonDatabaseDataConverter commonDatabaseDataConverter;
    private final List<ExportObjectsEnrichment> exportObjectsEnrichmentList;

    public EntityWithAdditionalDataConverter(CommonDatabaseDataConverter commonDatabaseDataConverter, List<ExportObjectsEnrichment> exportObjectsEnrichmentList) {
        this.commonDatabaseDataConverter = commonDatabaseDataConverter;
        this.exportObjectsEnrichmentList = exportObjectsEnrichmentList;
    }

    @Override
    public List<EntityObjectReadyForExport> convertToObjectsReadyForSerialisation(List<DbRawObjectData> dbObjectsProperties) {
        List<EntityObjectReadyForExport> entityObjects = this.commonDatabaseDataConverter.convertToObjectsReadyForSerialisation(dbObjectsProperties);
        this.exportObjectsEnrichmentList.forEach(collectionConverter -> collectionConverter.enrichElements(entityObjects));
        return entityObjects;
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.commonDatabaseDataConverter.getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.commonDatabaseDataConverter.getEntityInfo(exportedClass);
    }
}

