/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters;

import com.atlassian.confluence.impl.backuprestore.backup.exception.DataConversionException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import java.util.Map;

public class CommonDatabaseDataConverter
extends AbstractDatabaseDataConverter {
    public CommonDatabaseDataConverter(ExportableEntityInfo entityInfo, HibernateMetadataHelper hibernateMetadataHelper, StatisticsCollector statisticsCollector) {
        super(entityInfo, hibernateMetadataHelper, statisticsCollector);
    }

    @Override
    protected Class<?> getHibernateEntityClass(Map<String, Object> objectProperties) {
        return this.getEntityInfo().getEntityClass();
    }

    @Override
    protected EntityObjectReadyForExport convertToObjectReadyForSerialisation(DbRawObjectData dbObject) throws DataConversionException {
        return this.convertToObjectReadyForSerialisationImpl(dbObject);
    }
}

