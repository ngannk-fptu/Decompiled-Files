/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.type.ManyToOneType
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
import org.hibernate.type.ManyToOneType;

public class Content2ContentRelationEntityDataConverter
extends AbstractDatabaseDataConverter {
    public Content2ContentRelationEntityDataConverter(ExportableEntityInfo entityInfo, HibernateMetadataHelper hibernateMetadataHelper, StatisticsCollector statisticsCollector) {
        super(entityInfo, hibernateMetadataHelper, statisticsCollector);
    }

    @Override
    protected Class<?> getHibernateEntityClass(Map<String, Object> objectProperties) {
        return this.getEntityInfo().getEntityClass();
    }

    @Override
    protected void addExternalReferenceToObject(Map<String, Object> dbObjectProperties, EntityObjectReadyForExport object, ManyToOneType type, String columnName, String propertyName) throws DataConversionException {
        String entityClassName = type.getAssociatedEntityName();
        Class<?> referencedClass = HibernateMetadataHelper.getClassByClassName(entityClassName);
        Object referencedValue = dbObjectProperties.get(columnName);
        String idPropertyNameOfReferencedEntity = this.hibernateMetadataHelper.getIdPropertyName(referencedClass);
        if (columnName.equals("TARGETCONTENTID")) {
            referencedClass = this.getContentClassByContentType(object, (String)dbObjectProperties.get("TARGETTYPE"));
        } else if (columnName.equals("SOURCECONTENTID")) {
            referencedClass = this.getContentClassByContentType(object, (String)dbObjectProperties.get("SOURCETYPE"));
        }
        object.addReference(new EntityObjectReadyForExport.Reference(propertyName, referencedClass, new EntityObjectReadyForExport.Property(idPropertyNameOfReferencedEntity, referencedValue)));
    }

    private Class<?> getContentClassByContentType(EntityObjectReadyForExport object, String contentType) throws DataConversionException {
        try {
            return this.getContentClassByContentType(contentType);
        }
        catch (IllegalArgumentException e) {
            String message = String.format("Unexpected content type %s for entity %s with ID %s", contentType, object.getClazz(), object.getId().getValue());
            throw new DataConversionException(message, e);
        }
    }

    @Override
    protected EntityObjectReadyForExport convertToObjectReadyForSerialisation(DbRawObjectData dbObject) throws DataConversionException {
        return this.convertToObjectReadyForSerialisationImpl(dbObject);
    }
}

