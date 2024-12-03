/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.lang.reflect.Constructor;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementHelper;
import org.hibernate.engine.internal.UnsavedValueFactory;
import org.hibernate.engine.spi.IdentifierValue;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.VersionValue;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.entity.EntityBasedAssociationAttribute;
import org.hibernate.tuple.entity.EntityBasedBasicAttribute;
import org.hibernate.tuple.entity.EntityBasedCompositionAttribute;
import org.hibernate.tuple.entity.VersionProperty;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;

public final class PropertyFactory {
    private PropertyFactory() {
    }

    public static IdentifierProperty buildIdentifierAttribute(PersistentClass mappedEntity, IdentifierGenerator generator) {
        String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
        Type type = mappedEntity.getIdentifier().getType();
        Property property = mappedEntity.getIdentifierProperty();
        IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(mappedUnsavedValue, PropertyFactory.getGetter(property), type, PropertyFactory.getConstructor(mappedEntity));
        if (property == null) {
            return new IdentifierProperty(type, mappedEntity.hasEmbeddedIdentifier(), mappedEntity.hasIdentifierMapper(), unsavedValue, generator);
        }
        return new IdentifierProperty(property.getName(), type, mappedEntity.hasEmbeddedIdentifier(), unsavedValue, generator);
    }

    public static VersionProperty buildVersionProperty(EntityPersister persister, SessionFactoryImplementor sessionFactory, int attributeNumber, Property property, boolean lazyAvailable) {
        String mappedUnsavedValue = ((KeyValue)property.getValue()).getNullValue();
        VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(mappedUnsavedValue, PropertyFactory.getGetter(property), (VersionType)property.getType(), PropertyFactory.getConstructor(property.getPersistentClass()));
        boolean lazy = lazyAvailable && property.isLazy();
        return new VersionProperty(persister, sessionFactory, attributeNumber, property.getName(), property.getValue().getType(), new BaselineAttributeInformation.Builder().setLazy(lazy).setInsertable(property.isInsertable()).setUpdateable(property.isUpdateable()).setValueGenerationStrategy(property.getValueGenerationStrategy()).setNullable(property.isOptional()).setDirtyCheckable(property.isUpdateable() && !lazy).setVersionable(property.isOptimisticLocked()).setCascadeStyle(property.getCascadeStyle()).createInformation(), unsavedValue);
    }

    public static NonIdentifierAttribute buildEntityBasedAttribute(EntityPersister persister, SessionFactoryImplementor sessionFactory, int attributeNumber, Property property, boolean lazyAvailable, PersisterCreationContext creationContext) {
        Type type = property.getValue().getType();
        NonIdentifierAttributeNature nature = PropertyFactory.decode(type);
        boolean alwaysDirtyCheck = type.isAssociationType() && ((AssociationType)type).isAlwaysDirtyChecked();
        SessionFactoryOptions sessionFactoryOptions = sessionFactory.getSessionFactoryOptions();
        boolean lazy = !EnhancementHelper.includeInBaseFetchGroup(property, lazyAvailable, entityName -> {
            MetadataImplementor metadata = creationContext.getMetadata();
            PersistentClass entityBinding = metadata.getEntityBinding(entityName);
            assert (entityBinding != null);
            return entityBinding.hasSubclasses();
        }, sessionFactoryOptions.isCollectionsInDefaultFetchGroupEnabled());
        switch (nature) {
            case BASIC: {
                return new EntityBasedBasicAttribute(persister, sessionFactory, attributeNumber, property.getName(), type, new BaselineAttributeInformation.Builder().setLazy(lazy).setInsertable(property.isInsertable()).setUpdateable(property.isUpdateable()).setValueGenerationStrategy(property.getValueGenerationStrategy()).setNullable(property.isOptional()).setDirtyCheckable(alwaysDirtyCheck || property.isUpdateable()).setVersionable(property.isOptimisticLocked()).setCascadeStyle(property.getCascadeStyle()).setFetchMode(property.getValue().getFetchMode()).createInformation());
            }
            case COMPOSITE: {
                return new EntityBasedCompositionAttribute(persister, sessionFactory, attributeNumber, property.getName(), (CompositeType)type, new BaselineAttributeInformation.Builder().setLazy(lazy).setInsertable(property.isInsertable()).setUpdateable(property.isUpdateable()).setValueGenerationStrategy(property.getValueGenerationStrategy()).setNullable(property.isOptional()).setDirtyCheckable(alwaysDirtyCheck || property.isUpdateable()).setVersionable(property.isOptimisticLocked()).setCascadeStyle(property.getCascadeStyle()).setFetchMode(property.getValue().getFetchMode()).createInformation());
            }
            case ENTITY: 
            case ANY: 
            case COLLECTION: {
                return new EntityBasedAssociationAttribute(persister, sessionFactory, attributeNumber, property.getName(), (AssociationType)type, new BaselineAttributeInformation.Builder().setLazy(lazy).setInsertable(property.isInsertable()).setUpdateable(property.isUpdateable()).setValueGenerationStrategy(property.getValueGenerationStrategy()).setNullable(property.isOptional()).setDirtyCheckable(alwaysDirtyCheck || property.isUpdateable()).setVersionable(property.isOptimisticLocked()).setCascadeStyle(property.getCascadeStyle()).setFetchMode(property.getValue().getFetchMode()).createInformation());
            }
        }
        throw new HibernateException("Internal error");
    }

    private static NonIdentifierAttributeNature decode(Type type) {
        if (type.isAssociationType()) {
            if (type.isComponentType()) {
                return NonIdentifierAttributeNature.ANY;
            }
            return type.isCollectionType() ? NonIdentifierAttributeNature.COLLECTION : NonIdentifierAttributeNature.ENTITY;
        }
        if (type.isComponentType()) {
            return NonIdentifierAttributeNature.COMPOSITE;
        }
        return NonIdentifierAttributeNature.BASIC;
    }

    @Deprecated
    public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
        Type type = property.getValue().getType();
        boolean alwaysDirtyCheck = type.isAssociationType() && ((AssociationType)type).isAlwaysDirtyChecked();
        return new StandardProperty(property.getName(), type, false, property.isInsertable(), property.isUpdateable(), property.getValueGenerationStrategy(), property.isOptional(), alwaysDirtyCheck || property.isUpdateable(), property.isOptimisticLocked(), property.getCascadeStyle(), property.getValue().getFetchMode());
    }

    private static Constructor getConstructor(PersistentClass persistentClass) {
        if (persistentClass == null || !persistentClass.hasPojoRepresentation()) {
            return null;
        }
        try {
            return ReflectHelper.getDefaultConstructor(persistentClass.getMappedClass());
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Getter getGetter(Property mappingProperty) {
        if (mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation()) {
            return null;
        }
        PropertyAccessStrategyResolver propertyAccessStrategyResolver = mappingProperty.getPersistentClass().getServiceRegistry().getService(PropertyAccessStrategyResolver.class);
        PropertyAccessStrategy propertyAccessStrategy = propertyAccessStrategyResolver.resolvePropertyAccessStrategy(mappingProperty.getClass(), mappingProperty.getPropertyAccessorName(), EntityMode.POJO);
        PropertyAccess propertyAccess = propertyAccessStrategy.buildPropertyAccess(mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName());
        return propertyAccess.getGetter();
    }

    public static enum NonIdentifierAttributeNature {
        BASIC,
        COMPOSITE,
        ANY,
        ENTITY,
        COLLECTION;

    }
}

