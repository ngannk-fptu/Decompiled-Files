/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.IdentifiableType
 *  javax.persistence.metamodel.MappedSuperclassType
 *  javax.persistence.metamodel.SingularAttribute
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.AssertionFailure;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metamodel.internal.AttributeFactory;
import org.hibernate.metamodel.internal.JpaMetaModelPopulationSetting;
import org.hibernate.metamodel.internal.JpaStaticMetaModelPopulationSetting;
import org.hibernate.metamodel.model.domain.internal.AbstractIdentifiableType;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.hibernate.metamodel.model.domain.internal.MappedSuperclassTypeImpl;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.MappedSuperclassTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;
import org.hibernate.type.CompositeType;

class MetadataContext {
    private static final EntityManagerMessageLogger LOG = HEMLogging.messageLogger(MetadataContext.class);
    private final SessionFactoryImplementor sessionFactory;
    private Set<MappedSuperclass> knownMappedSuperclasses;
    private final boolean ignoreUnsupported;
    private final AttributeFactory attributeFactory = new AttributeFactory(this);
    private Map<Class<?>, EntityTypeDescriptor<?>> entityTypes = new HashMap();
    private Map<String, EntityTypeDescriptor<?>> entityTypesByEntityName = new HashMap();
    private Map<PersistentClass, EntityTypeDescriptor<?>> entityTypesByPersistentClass = new HashMap();
    private Map<Class, List<EmbeddedTypeDescriptor<?>>> embeddablesToProcess = new HashMap();
    private Map<EmbeddedTypeDescriptor<?>, CompositeType> componentByEmbeddable = new HashMap();
    private Map<MappedSuperclass, MappedSuperclassTypeDescriptor<?>> mappedSuperclassByMappedSuperclassMapping = new HashMap();
    private Map<MappedSuperclassTypeDescriptor<?>, PersistentClass> mappedSuperClassTypeToPersistentClass = new HashMap();
    private List<Object> orderedMappings = new ArrayList<Object>();
    private List<PersistentClass> stackOfPersistentClassesBeingProcessed = new ArrayList<PersistentClass>();
    private final Set<Class> processedMetamodelClasses = new HashSet<Class>();

    public MetadataContext(SessionFactoryImplementor sessionFactory, Set<MappedSuperclass> mappedSuperclasses, JpaMetaModelPopulationSetting jpaMetaModelPopulationSetting) {
        this.sessionFactory = sessionFactory;
        this.knownMappedSuperclasses = mappedSuperclasses;
        this.ignoreUnsupported = jpaMetaModelPopulationSetting == JpaMetaModelPopulationSetting.IGNORE_UNSUPPORTED;
    }

    SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    boolean isIgnoreUnsupported() {
        return this.ignoreUnsupported;
    }

    public Map<Class<?>, EntityTypeDescriptor<?>> getEntityTypeMap() {
        return Collections.unmodifiableMap(this.entityTypes);
    }

    public Set<EmbeddedTypeDescriptor<?>> getEmbeddableTypeSet() {
        return Collections.unmodifiableSet(this.componentByEmbeddable.keySet());
    }

    public Map<Class<?>, MappedSuperclassType<?>> getMappedSuperclassTypeMap() {
        Map<Class<?>, MappedSuperclassType<?>> mappedSuperClassTypeMap = CollectionHelper.mapOfSize(this.mappedSuperclassByMappedSuperclassMapping.size());
        for (MappedSuperclassTypeDescriptor<?> mappedSuperclassType : this.mappedSuperclassByMappedSuperclassMapping.values()) {
            mappedSuperClassTypeMap.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);
        }
        return mappedSuperClassTypeMap;
    }

    void registerEntityType(PersistentClass persistentClass, EntityTypeImpl<?> entityType) {
        if (this.ignoreUnsupported && entityType.getBindableJavaType() == null) {
            return;
        }
        if (entityType.getBindableJavaType() != null) {
            this.entityTypes.put(entityType.getBindableJavaType(), entityType);
        }
        this.entityTypesByEntityName.put(persistentClass.getEntityName(), entityType);
        this.entityTypesByPersistentClass.put(persistentClass, entityType);
        this.orderedMappings.add(persistentClass);
    }

    void registerEmbeddableType(EmbeddedTypeDescriptor<?> embeddableType, CompositeType component) {
        List existingEmbeddables = this.embeddablesToProcess.computeIfAbsent(embeddableType.getJavaType(), k -> new ArrayList(1));
        existingEmbeddables.add(embeddableType);
        if (!this.ignoreUnsupported || embeddableType.getParent().getJavaType() != null) {
            this.componentByEmbeddable.put(embeddableType, component);
        }
    }

    void registerMappedSuperclassType(MappedSuperclass mappedSuperclass, MappedSuperclassTypeDescriptor<?> mappedSuperclassType) {
        this.mappedSuperclassByMappedSuperclassMapping.put(mappedSuperclass, mappedSuperclassType);
        this.orderedMappings.add(mappedSuperclass);
        this.mappedSuperClassTypeToPersistentClass.put(mappedSuperclassType, this.getEntityWorkedOn());
        this.knownMappedSuperclasses.remove(mappedSuperclass);
    }

    public EntityTypeDescriptor<?> locateEntityType(PersistentClass persistentClass) {
        return this.entityTypesByPersistentClass.get(persistentClass);
    }

    public EntityTypeDescriptor<?> locateEntityType(Class<?> javaType) {
        return this.entityTypes.get(javaType);
    }

    public <E> EntityTypeDescriptor<E> locateEntityType(String entityName) {
        return this.entityTypesByEntityName.get(entityName);
    }

    public Map<String, EntityTypeDescriptor<?>> getEntityTypesByEntityName() {
        return Collections.unmodifiableMap(this.entityTypesByEntityName);
    }

    public <J> EmbeddedTypeDescriptor<J> locateEmbeddable(Class<J> embeddableClass, CompositeType component) {
        List<EmbeddedTypeDescriptor<?>> embeddableDomainTypes = this.embeddablesToProcess.get(embeddableClass);
        if (embeddableDomainTypes != null) {
            for (EmbeddedTypeDescriptor<?> embeddableDomainType : embeddableDomainTypes) {
                CompositeType cachedComponent = this.componentByEmbeddable.get(embeddableDomainType);
                if (Arrays.equals(cachedComponent.getPropertyNames(), component.getPropertyNames())) {
                    return embeddableDomainType;
                }
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedComponentMapping(embeddableClass.getName());
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void wrapUp() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Wrapping up metadata context...");
        }
        boolean staticMetamodelScanEnabled = JpaStaticMetaModelPopulationSetting.determineJpaMetaModelPopulationSetting(this.sessionFactory.getProperties()) != JpaStaticMetaModelPopulationSetting.DISABLED;
        for (Object object : this.orderedMappings) {
            PersistentAttributeDescriptor attribute;
            Property property;
            Iterator properties;
            Object safeMapping;
            if (PersistentClass.class.isAssignableFrom(object.getClass())) {
                safeMapping = (PersistentClass)object;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Starting entity [" + ((PersistentClass)safeMapping).getEntityName() + ']');
                }
                try {
                    EntityTypeDescriptor<?> jpaMapping = this.entityTypesByPersistentClass.get(safeMapping);
                    this.applyIdMetadata((PersistentClass)safeMapping, jpaMapping);
                    this.applyVersionAttribute((PersistentClass)safeMapping, jpaMapping);
                    properties = ((PersistentClass)safeMapping).getDeclaredPropertyIterator();
                    while (properties.hasNext()) {
                        property = (Property)properties.next();
                        if (property.getValue() == ((PersistentClass)safeMapping).getIdentifierMapper() || ((PersistentClass)safeMapping).isVersioned() && property == ((PersistentClass)safeMapping).getVersion() || (attribute = this.attributeFactory.buildAttribute(jpaMapping, property)) == null) continue;
                        jpaMapping.getInFlightAccess().addAttribute(attribute);
                    }
                    jpaMapping.getInFlightAccess().finishUp();
                    if (!staticMetamodelScanEnabled) continue;
                    this.populateStaticMetamodel(jpaMapping);
                    continue;
                }
                finally {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Completed entity [" + ((PersistentClass)safeMapping).getEntityName() + ']');
                    }
                    continue;
                }
            }
            if (MappedSuperclass.class.isAssignableFrom(object.getClass())) {
                safeMapping = (MappedSuperclass)object;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Starting mapped superclass [" + ((MappedSuperclass)safeMapping).getMappedClass().getName() + ']');
                }
                try {
                    MappedSuperclassTypeDescriptor<?> jpaType = this.mappedSuperclassByMappedSuperclassMapping.get(safeMapping);
                    this.applyIdMetadata((MappedSuperclass)safeMapping, jpaType);
                    this.applyVersionAttribute((MappedSuperclass)safeMapping, jpaType);
                    properties = ((MappedSuperclass)safeMapping).getDeclaredPropertyIterator();
                    while (properties.hasNext()) {
                        property = (Property)properties.next();
                        if (((MappedSuperclass)safeMapping).isVersioned() && property == ((MappedSuperclass)safeMapping).getVersion() || (attribute = this.attributeFactory.buildAttribute(jpaType, property)) == null) continue;
                        jpaType.getInFlightAccess().addAttribute(attribute);
                    }
                    jpaType.getInFlightAccess().finishUp();
                    if (!staticMetamodelScanEnabled) continue;
                    this.populateStaticMetamodel(jpaType);
                    continue;
                }
                finally {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Completed mapped superclass [" + ((MappedSuperclass)safeMapping).getMappedClass().getName() + ']');
                    }
                    continue;
                }
            }
            throw new AssertionFailure("Unexpected mapping type: " + object.getClass());
        }
        if (staticMetamodelScanEnabled) {
            for (EmbeddedTypeDescriptor embeddedTypeDescriptor : this.componentByEmbeddable.keySet()) {
                this.populateStaticMetamodel(embeddedTypeDescriptor);
            }
        }
    }

    private <X> void applyIdMetadata(PersistentClass persistentClass, IdentifiableTypeDescriptor<?> identifiableType) {
        if (persistentClass.hasIdentifierProperty()) {
            Property declaredIdentifierProperty = persistentClass.getDeclaredIdentifierProperty();
            if (declaredIdentifierProperty != null) {
                identifiableType.getInFlightAccess().applyIdAttribute(this.attributeFactory.buildIdAttribute(identifiableType, declaredIdentifierProperty));
            }
        } else if (persistentClass.hasIdentifierMapper()) {
            Iterator propertyIterator = persistentClass.getIdentifierMapper().getPropertyIterator();
            identifiableType.getInFlightAccess().applyIdClassAttributes(this.buildIdClassAttributes(identifiableType, propertyIterator));
        } else {
            Component component;
            KeyValue value = persistentClass.getIdentifier();
            if (value instanceof Component && (component = (Component)value).getPropertySpan() <= 1) {
                identifiableType.getInFlightAccess().applyIdAttribute(this.attributeFactory.buildIdAttribute(identifiableType, (Property)component.getPropertyIterator().next()));
            }
        }
    }

    private <X> void applyIdMetadata(MappedSuperclass mappingType, MappedSuperclassTypeDescriptor<X> jpaMappingType) {
        if (mappingType.hasIdentifierProperty()) {
            Property declaredIdentifierProperty = mappingType.getDeclaredIdentifierProperty();
            if (declaredIdentifierProperty != null) {
                jpaMappingType.getInFlightAccess().applyIdAttribute(this.attributeFactory.buildIdAttribute(jpaMappingType, declaredIdentifierProperty));
            }
        } else if (mappingType.getIdentifierMapper() != null) {
            Iterator propertyIterator = mappingType.getIdentifierMapper().getPropertyIterator();
            Set attributes = this.buildIdClassAttributes(jpaMappingType, propertyIterator);
            jpaMappingType.getInFlightAccess().applyIdClassAttributes(attributes);
        }
    }

    private <X> void applyVersionAttribute(PersistentClass persistentClass, EntityTypeDescriptor<X> jpaEntityType) {
        Property declaredVersion = persistentClass.getDeclaredVersion();
        if (declaredVersion != null) {
            jpaEntityType.getInFlightAccess().applyVersionAttribute(this.attributeFactory.buildVersionAttribute(jpaEntityType, declaredVersion));
        }
    }

    private <X> void applyVersionAttribute(MappedSuperclass mappingType, MappedSuperclassTypeDescriptor<X> jpaMappingType) {
        Property declaredVersion = mappingType.getDeclaredVersion();
        if (declaredVersion != null) {
            jpaMappingType.getInFlightAccess().applyVersionAttribute(this.attributeFactory.buildVersionAttribute(jpaMappingType, declaredVersion));
        }
    }

    private <X> Set<SingularPersistentAttribute<? super X, ?>> buildIdClassAttributes(IdentifiableTypeDescriptor<X> ownerType, Iterator<Property> propertyIterator) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Building old-school composite identifier [" + ownerType.getJavaType().getName() + ']');
        }
        HashSet attributes = new HashSet();
        while (propertyIterator.hasNext()) {
            attributes.add(this.attributeFactory.buildIdAttribute(ownerType, propertyIterator.next()));
        }
        return attributes;
    }

    private <X> void populateStaticMetamodel(ManagedTypeDescriptor<X> managedType) {
        Class managedTypeClass = managedType.getJavaType();
        if (managedTypeClass == null) {
            return;
        }
        String metamodelClassName = managedTypeClass.getName() + '_';
        try {
            Class<?> metamodelClass = Class.forName(metamodelClassName, true, managedTypeClass.getClassLoader());
            this.registerAttributes(metamodelClass, managedType);
        }
        catch (ClassNotFoundException metamodelClass) {
            // empty catch block
        }
        ManagedTypeDescriptor<X> superType = managedType.getSuperType();
        if (superType != null) {
            this.populateStaticMetamodel(superType);
        }
    }

    private <X> void registerAttributes(Class metamodelClass, ManagedTypeDescriptor<X> managedType) {
        if (!this.processedMetamodelClasses.add(metamodelClass)) {
            return;
        }
        for (Attribute attribute : managedType.getDeclaredAttributes()) {
            this.registerAttribute(metamodelClass, attribute);
        }
        if (IdentifiableType.class.isInstance(managedType)) {
            Set attributes;
            AbstractIdentifiableType entityType = (AbstractIdentifiableType)managedType;
            if (entityType.hasDeclaredVersionAttribute()) {
                this.registerAttribute(metamodelClass, (Attribute<X, ?>)entityType.getDeclaredVersion());
            }
            if (entityType.hasIdClass() && (attributes = entityType.getIdClassAttributesSafely()) != null) {
                for (SingularAttribute singularAttribute : attributes) {
                    this.registerAttribute(metamodelClass, (Attribute<X, ?>)singularAttribute);
                }
            }
        }
    }

    private <X> void registerAttribute(Class metamodelClass, Attribute<X, ?> attribute) {
        String name = attribute.getName();
        try {
            boolean allowNonDeclaredFieldReference = attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED || attribute.getDeclaringType().getPersistenceType() == Type.PersistenceType.EMBEDDABLE;
            Field field = allowNonDeclaredFieldReference ? metamodelClass.getField(name) : metamodelClass.getDeclaredField(name);
            try {
                ReflectHelper.ensureAccessibility(field);
                field.set(null, attribute);
            }
            catch (IllegalAccessException e) {
                throw new AssertionFailure("Unable to inject static metamodel attribute : " + metamodelClass.getName() + '#' + name, e);
            }
            catch (IllegalArgumentException e) {
                LOG.illegalArgumentOnStaticMetamodelFieldInjection(metamodelClass.getName(), name, attribute.getClass().getName(), field.getType().getName());
            }
        }
        catch (NoSuchFieldException e) {
            LOG.unableToLocateStaticMetamodelField(metamodelClass.getName(), name);
        }
    }

    public MappedSuperclassTypeDescriptor<?> locateMappedSuperclassType(MappedSuperclass mappedSuperclass) {
        return this.mappedSuperclassByMappedSuperclassMapping.get(mappedSuperclass);
    }

    public void pushEntityWorkedOn(PersistentClass persistentClass) {
        this.stackOfPersistentClassesBeingProcessed.add(persistentClass);
    }

    public void popEntityWorkedOn(PersistentClass persistentClass) {
        PersistentClass stackTop = this.stackOfPersistentClassesBeingProcessed.remove(this.stackOfPersistentClassesBeingProcessed.size() - 1);
        if (stackTop != persistentClass) {
            throw new AssertionFailure("Inconsistent popping: " + persistentClass.getEntityName() + " instead of " + stackTop.getEntityName());
        }
    }

    private PersistentClass getEntityWorkedOn() {
        return this.stackOfPersistentClassesBeingProcessed.get(this.stackOfPersistentClassesBeingProcessed.size() - 1);
    }

    public PersistentClass getPersistentClassHostingProperties(MappedSuperclassTypeImpl<?> mappedSuperclassType) {
        PersistentClass persistentClass = this.mappedSuperClassTypeToPersistentClass.get(mappedSuperclassType);
        if (persistentClass == null) {
            throw new AssertionFailure("Could not find PersistentClass for MappedSuperclassType: " + mappedSuperclassType.getJavaType());
        }
        return persistentClass;
    }

    public Set<MappedSuperclass> getUnusedMappedSuperclasses() {
        return new HashSet<MappedSuperclass>(this.knownMappedSuperclasses);
    }
}

