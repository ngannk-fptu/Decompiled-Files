/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import java.util.List;
import java.util.Map;
import org.hibernate.type.Type;

public class AliasedKeyDatabaseDataConverter
extends AbstractDatabaseDataConverter {
    public AliasedKeyDatabaseDataConverter(ExportableEntityInfo entityInfo, HibernateMetadataHelper hibernateMetadataHelper, StatisticsCollector statisticsCollector) {
        super(entityInfo, hibernateMetadataHelper, statisticsCollector);
    }

    @Override
    protected Class<?> getHibernateEntityClass(Map<String, Object> objectProperties) {
        return this.getEntityInfo().getEntityClass();
    }

    @Override
    protected EntityObjectReadyForExport convertToObjectReadyForSerialisation(DbRawObjectData dbObject) {
        Map<String, Object> dbObjectProperties = dbObject.getObjectProperties();
        Class<?> entityClazz = this.getHibernateEntityClass(dbObjectProperties);
        ExportableEntityInfo entityInfo = this.getEntityInfo(entityClazz);
        HibernateField id = entityInfo.getId();
        String keyColumnName = id.getSingleColumnName();
        Object idValue = dbObjectProperties.get(keyColumnName);
        String idPropertyName = id.getPropertyName(keyColumnName);
        EntityObjectReadyForExport.Property idProperty = new EntityObjectReadyForExport.Property(id.getIdPropertyType(keyColumnName).getReturnedClass(), idPropertyName, this.convertToLongIfPossible(idValue));
        EntityObjectReadyForExport object = new EntityObjectReadyForExport(idProperty, entityClazz);
        for (HibernateField field : entityInfo.getFields()) {
            Class<AliasedKey> propertyType;
            Object value;
            Type type = field.getType();
            List<String> columnNames = field.getColumnNames();
            String propertyName = field.getPropertyName();
            if (columnNames.size() == 1) {
                value = dbObjectProperties.get(columnNames.get(0)).toString();
                propertyType = type.getReturnedClass();
            } else {
                value = new KeyTransferBean(dbObjectProperties.get("TYPE").toString(), dbObjectProperties.get("ALGORITHM").toString(), dbObjectProperties.get("KEYSPEC").toString());
                propertyType = AliasedKey.class;
            }
            object.addProperty(new EntityObjectReadyForExport.Property(propertyType, propertyName, value));
        }
        return object;
    }
}

