/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exception.DataConversionException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import java.util.Collection;
import java.util.Map;

public class ContentEntityDatabaseDataConverter
extends AbstractDatabaseDataConverter {
    public ContentEntityDatabaseDataConverter(Collection<ExportableEntityInfo> entityInfos, HibernateMetadataHelper hibernateMetadataHelper, StatisticsCollector statisticsCollector) {
        super(entityInfos, hibernateMetadataHelper, statisticsCollector);
    }

    @Override
    protected Class<?> getHibernateEntityClass(Map<String, Object> objectProperties) throws DataConversionException {
        String contentType = (String)objectProperties.get("CONTENTTYPE");
        try {
            return this.getContentClassByContentType(contentType);
        }
        catch (IllegalArgumentException e) {
            String message = String.format("Unexpected content type %s for entity %s with ID %s", contentType, ContentEntityObject.class, objectProperties.get("CONTENTID"));
            throw new DataConversionException(message, e);
        }
    }

    @Override
    protected EntityObjectReadyForExport convertToObjectReadyForSerialisation(DbRawObjectData dbObject) throws DataConversionException {
        return this.convertToObjectReadyForSerialisationImpl(dbObject);
    }
}

