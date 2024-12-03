/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.type.BooleanType
 *  org.hibernate.type.ManyToOneType
 *  org.hibernate.type.TrueFalseType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exception.DataConversionException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.PersonalInformation;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDatabaseDataConverter
implements Converter {
    private static final Logger log = LoggerFactory.getLogger(AbstractDatabaseDataConverter.class);
    protected final HibernateMetadataHelper hibernateMetadataHelper;
    protected final StatisticsCollector statisticsCollector;
    private final Map<Class<?>, ExportableEntityInfo> classToEntityInfoMap;

    public AbstractDatabaseDataConverter(ExportableEntityInfo entityInfo, HibernateMetadataHelper hibernateMetadataHelper, StatisticsCollector statisticsCollector) {
        this(Collections.singleton(entityInfo), hibernateMetadataHelper, statisticsCollector);
    }

    public AbstractDatabaseDataConverter(Collection<ExportableEntityInfo> entityInfos, HibernateMetadataHelper hibernateMetadataHelper, StatisticsCollector statisticsCollector) {
        this.classToEntityInfoMap = entityInfos.stream().collect(Collectors.toMap(ExportableEntityInfo::getEntityClass, Function.identity()));
        this.hibernateMetadataHelper = hibernateMetadataHelper;
        this.statisticsCollector = statisticsCollector;
    }

    protected abstract Class<?> getHibernateEntityClass(Map<String, Object> var1) throws DataConversionException;

    protected abstract EntityObjectReadyForExport convertToObjectReadyForSerialisation(DbRawObjectData var1) throws DataConversionException;

    protected EntityObjectReadyForExport convertToObjectReadyForSerialisationImpl(DbRawObjectData dbObject) throws DataConversionException {
        Map<String, Object> dbObjectProperties = dbObject.getObjectProperties();
        Class<?> entityClazz = this.getHibernateEntityClass(dbObjectProperties);
        ExportableEntityInfo entityInfo = this.getEntityInfo(entityClazz);
        HibernateField id = entityInfo.getId();
        List<String> keyColumnNames = id.getColumnNames();
        LinkedList<EntityObjectReadyForExport.Property> idProperties = new LinkedList<EntityObjectReadyForExport.Property>();
        for (String keyColumnName : keyColumnNames) {
            Object idValue = dbObjectProperties.get(HibernateMetadataHelper.removeQuotes(keyColumnName));
            String idPropertyName = id.getPropertyName(keyColumnName);
            idProperties.add(new EntityObjectReadyForExport.Property(id.getIdPropertyType(keyColumnName).getReturnedClass(), idPropertyName, this.convertToLongIfPossible(idValue)));
            if (entityClazz == null || !ContentEntityObject.class.isAssignableFrom(entityClazz)) continue;
            this.hibernateMetadataHelper.registerContentEntityObject(idValue, entityClazz);
        }
        EntityObjectReadyForExport object = new EntityObjectReadyForExport(idProperties, entityClazz);
        for (HibernateField field : entityInfo.getFields()) {
            Type type = field.getType();
            if (type.isCollectionType()) continue;
            String columnName = field.getSingleColumnName();
            String propertyName = field.getPropertyName();
            if (type.isAssociationType()) {
                this.addExternalReferenceToObject(dbObjectProperties, object, (ManyToOneType)type, columnName, propertyName);
                continue;
            }
            Object value = type.getClass().equals(TrueFalseType.class) || type.getClass().equals(BooleanType.class) ? AbstractDatabaseDataConverter.convertToBoolean(dbObjectProperties.get(HibernateMetadataHelper.removeQuotes(columnName))) : dbObjectProperties.get(HibernateMetadataHelper.removeQuotes(columnName));
            object.addProperty(new EntityObjectReadyForExport.Property(type.getReturnedClass(), propertyName, value));
        }
        return object;
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        if (this.classToEntityInfoMap.size() != 1) {
            throw new IllegalStateException("Unable to return default entity info. Expected to have 1 entity infos but found " + this.classToEntityInfoMap.size());
        }
        return this.classToEntityInfoMap.values().iterator().next();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> entityClass) {
        ExportableEntityInfo entityInfo = this.classToEntityInfoMap.get(entityClass);
        if (entityInfo != null) {
            return entityInfo;
        }
        throw new IllegalArgumentException("Unable to find the hibernate entity info for entity class: " + entityClass.getName());
    }

    protected void addExternalReferenceToObject(Map<String, Object> dbObjectProperties, EntityObjectReadyForExport object, ManyToOneType type, String columnName, String propertyName) throws DataConversionException {
        Object referencedValue = dbObjectProperties.get(columnName);
        Class<?> referencedClass = this.calculateReferencedClass(type, referencedValue);
        String idPropertyNameOfReferencedEntity = this.hibernateMetadataHelper.getIdPropertyName(referencedClass);
        Class idTypeOfReferencedClass = this.hibernateMetadataHelper.getPersister(type.getReturnedClass()).getIdentifierType().getReturnedClass();
        object.addReference(new EntityObjectReadyForExport.Reference(propertyName, referencedClass, new EntityObjectReadyForExport.Property(idTypeOfReferencedClass, idPropertyNameOfReferencedEntity, referencedValue)));
    }

    @Override
    public List<EntityObjectReadyForExport> convertToObjectsReadyForSerialisation(List<DbRawObjectData> dbObjectsProperties) {
        LinkedList<EntityObjectReadyForExport> objectsReadyForExport = new LinkedList<EntityObjectReadyForExport>();
        for (DbRawObjectData dbObjectProperty : dbObjectsProperties) {
            try {
                objectsReadyForExport.add(this.convertToObjectReadyForSerialisation(dbObjectProperty));
            }
            catch (DataConversionException e) {
                this.statisticsCollector.onObjectsSkipping(Collections.singleton(dbObjectProperty), SkippedObjectsReason.INVALID_FIELDS, e.getMessage());
            }
        }
        return objectsReadyForExport;
    }

    public static Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).longValue();
        }
        return (Long)value;
    }

    public static Integer convertToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).intValue();
        }
        return (Integer)value;
    }

    public static @Nullable Boolean convertToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Short) {
            return BooleanUtils.toBooleanObject((int)((Short)value).intValue());
        }
        if (value instanceof Long) {
            return BooleanUtils.toBooleanObject((int)((Long)value).intValue());
        }
        if (value instanceof Integer) {
            return BooleanUtils.toBooleanObject((Integer)((Integer)value));
        }
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        return BooleanUtils.toBooleanObject((String)value.toString());
    }

    Object convertToLongIfPossible(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).longValue();
        }
        return value;
    }

    public Class<?> getContentClassByContentType(String contentType) {
        switch (contentType) {
            case "SPACEDESCRIPTION": {
                return SpaceDescription.class;
            }
            case "BLOGPOST": {
                return BlogPost.class;
            }
            case "PAGE": {
                return Page.class;
            }
            case "COMMENT": {
                return Comment.class;
            }
            case "ATTACHMENT": {
                return Attachment.class;
            }
            case "CUSTOM": {
                return CustomContentEntityObject.class;
            }
            case "DRAFT": {
                return Draft.class;
            }
            case "USERINFO": {
                return PersonalInformation.class;
            }
            case "GLOBALDESCRIPTION": {
                return GlobalDescription.class;
            }
        }
        throw new IllegalArgumentException("Unexpected content type: " + contentType);
    }

    private Class<?> calculateReferencedClass(ManyToOneType type, Object referencedId) {
        String entityClassName = type.getAssociatedEntityName();
        Class<?> referencedClass = HibernateMetadataHelper.getClassByClassName(entityClassName);
        if (referencedId == null || !ContentEntityObject.class.equals(referencedClass)) {
            return referencedClass;
        }
        return this.calculateReferencedClass(referencedId, referencedClass);
    }

    protected Class<?> calculateReferencedClass(Object referencedId, Class<?> referencedClass) {
        Class<?> realClass = this.hibernateMetadataHelper.getRealContentEntityObjectSubclass(referencedId, referencedClass);
        if (realClass != null) {
            return realClass;
        }
        log.warn("Not found a real class for id {}. {} will be used instead. Not an issue for the new XML restore (Confluence 8.0+)", referencedId, referencedClass);
        return referencedClass;
    }
}

