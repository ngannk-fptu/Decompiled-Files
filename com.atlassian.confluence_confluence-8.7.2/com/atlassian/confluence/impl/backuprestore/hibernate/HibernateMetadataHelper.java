/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl
 *  com.atlassian.crowd.directory.DirectorySynchronisationToken
 *  javax.persistence.metamodel.EntityType
 *  org.hibernate.SessionFactory
 *  org.hibernate.metamodel.spi.MetamodelImplementor
 *  org.hibernate.persister.entity.AbstractEntityPersister
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfoFactory;
import com.atlassian.confluence.security.persistence.dao.hibernate.legacy.HibernateKey;
import com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl;
import com.atlassian.crowd.directory.DirectorySynchronisationToken;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.metamodel.EntityType;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.Type;

public class HibernateMetadataHelper {
    private final ExportableEntityInfoFactory exportableEntityInfoFactory;
    private final SessionFactory sessionFactory;
    private final boolean realReferenceClassesUsedInExport;
    private final ConcurrentHashMap<Object, Class<?>> allPersistedContentEntities = new ConcurrentHashMap();
    private static final Map<String, Class<?>> classByClassName = new ConcurrentHashMap();
    private final Map<Class<?>, AbstractEntityPersister> persistersByClass = new ConcurrentHashMap();
    private final Map<EntityType<?>, ExportableEntityInfo> allExportableEntitiesByType = new HashMap();
    private final Map<Class<?>, ExportableEntityInfo> allExportableEntitiesByClass = new HashMap();
    private final Map<Class<?>, ExportableEntityInfo> additionalExportableEntitiesByClass = new HashMap();
    private final Collection<ExportableEntityInfo> allSpaceImportableEntities = new HashSet<ExportableEntityInfo>();
    private final Collection<ExportableEntityInfo> allSiteImportableEntities = new HashSet<ExportableEntityInfo>();
    private final Set<Class<?>> nonExportableEntities = Set.of(HibernateKey.class, DirectorySynchronisationStatusImpl.class, DirectorySynchronisationToken.class);

    public HibernateMetadataHelper(ExportableEntityInfoFactory exportableEntityInfoFactory, SessionFactory sessionFactory, boolean realReferenceClassesUsedInExport) {
        this.exportableEntityInfoFactory = exportableEntityInfoFactory;
        this.sessionFactory = sessionFactory;
        this.realReferenceClassesUsedInExport = realReferenceClassesUsedInExport;
        this.initExportableEntities();
    }

    public Class<?> getRealContentEntityObjectSubclass(Object id, Class<?> clazz) {
        if (this.realReferenceClassesUsedInExport) {
            return this.allPersistedContentEntities.get(id);
        }
        return clazz;
    }

    public void registerContentEntityObject(Object id, Class<?> clazz) {
        if (this.realReferenceClassesUsedInExport) {
            this.allPersistedContentEntities.put(id, clazz);
        }
    }

    private void initExportableEntities() {
        Set entityTypes = this.sessionFactory.getMetamodel().getEntities();
        entityTypes.forEach(entityType -> {
            if (this.isExportable((EntityType<?>)entityType)) {
                AbstractEntityPersister entityPersister = this.getPersister(entityType.getJavaType());
                ExportableEntityInfo exportableEntityInfo = this.exportableEntityInfoFactory.createExportableEntityInfo(entityPersister);
                this.allExportableEntitiesByType.put((EntityType<?>)entityType, exportableEntityInfo);
                this.allExportableEntitiesByClass.put(entityType.getJavaType(), exportableEntityInfo);
                if (this.isSuitableForSpaceRestore(exportableEntityInfo.getEntityClass())) {
                    this.allSpaceImportableEntities.add(exportableEntityInfo);
                }
                this.allSiteImportableEntities.add(exportableEntityInfo);
            }
        });
        this.initAdditionalExportableEntities();
    }

    private boolean isExportable(EntityType<?> entityType) {
        return !NotExportable.class.isAssignableFrom(entityType.getJavaType()) && !this.nonExportableEntities.contains(entityType.getJavaType());
    }

    private void initAdditionalExportableEntities() {
        this.addAdditionalExportableEntity(this.exportableEntityInfoFactory.createAncestorsEntityInfo());
        this.addAdditionalExportableEntity(this.exportableEntityInfoFactory.createApplicationAttributeEntityInfo());
        this.addAdditionalExportableEntity(this.exportableEntityInfoFactory.createDirectoryAttributeEntityInfo());
        this.addAdditionalExportableEntity(this.exportableEntityInfoFactory.createDirectoryOperationEntityInfo());
        this.addAdditionalExportableEntity(this.exportableEntityInfoFactory.createDirectoryMappingOperationEntityInfo());
        this.allExportableEntitiesByClass.putAll(this.additionalExportableEntitiesByClass);
    }

    private void addAdditionalExportableEntity(ExportableEntityInfo additionalExportableEntity) {
        this.additionalExportableEntitiesByClass.put(additionalExportableEntity.getEntityClass(), additionalExportableEntity);
    }

    private boolean isSuitableForSpaceRestore(Class<?> entityClass) {
        if (Modifier.isAbstract(entityClass.getModifiers())) {
            return false;
        }
        if (entityClass.getPackage().getName().startsWith("com.atlassian.crowd")) {
            return false;
        }
        if (entityClass.getPackage().getName().startsWith("com.atlassian.confluence.impl.audit")) {
            return false;
        }
        if (entityClass.getPackage().getName().startsWith("com.atlassian.confluence.internal.diagnostics")) {
            return false;
        }
        try {
            entityClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            return true;
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to create an instance of class " + entityClass);
        }
    }

    public Map<EntityType<?>, ExportableEntityInfo> getAllExportableEntities() {
        return this.allExportableEntitiesByType;
    }

    public static Class<?> getClassByClassName(String entityClassName) {
        return classByClassName.computeIfAbsent(entityClassName, k -> {
            try {
                return Class.forName(entityClassName);
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unable to find class for " + entityClassName, e);
            }
        });
    }

    public String getTableName(Class<?> entityClass) {
        return this.getPersister(entityClass).getTableName();
    }

    public AbstractEntityPersister getPersister(Class<?> clazz) {
        return this.persistersByClass.computeIfAbsent(clazz, k -> (AbstractEntityPersister)((MetamodelImplementor)this.sessionFactory.getMetamodel()).entityPersister(clazz));
    }

    private String getSingleValue(String[] propertyColumnNames) {
        if (propertyColumnNames.length != 1) {
            throw new IllegalArgumentException("The database type has to consist of one column. But it has " + propertyColumnNames.length + " columns");
        }
        return propertyColumnNames[0];
    }

    public Type getIdType(Class<?> clazz) {
        return this.getPersister(clazz).getIdentifierType();
    }

    public String getIdColumnName(Class<?> clazz) {
        return this.getSingleValue(this.getPersister(clazz).getKeyColumnNames());
    }

    public String getIdPropertyName(Class<?> clazz) {
        return this.getPersister(clazz).getIdentifierPropertyName();
    }

    public String[] getFieldNames(Class<?> clazz) {
        return this.getPersister(clazz).getPropertyNames();
    }

    public Type[] getHibernateTypes(Class<?> clazz) {
        return this.getPersister(clazz).getPropertyTypes();
    }

    public String getColumnName(Class<?> clazz, String fieldName) {
        return this.getSingleValue(this.getPersister(clazz).getPropertyColumnNames(fieldName));
    }

    public static String removeQuotes(String name) {
        return name.replace("\"", "").replace("'", "").replace("[", "").replace("]", "").replace("`", "");
    }

    public ExportableEntityInfo getEntityInfoByClass(Class<?> clazz) {
        return this.allExportableEntitiesByClass.get(clazz);
    }

    public Map<Class<?>, ExportableEntityInfo> getAdditionalExportableEntitiesByClass() {
        return this.additionalExportableEntitiesByClass;
    }

    public Collection<ExportableEntityInfo> getAllSpaceImportableEntities() {
        return this.allSpaceImportableEntities;
    }

    public Collection<ExportableEntityInfo> getAllSiteImportableEntities() {
        return this.allSiteImportableEntities;
    }
}

