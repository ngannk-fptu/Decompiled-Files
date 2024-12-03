/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementHelper;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadeStyles;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
import org.hibernate.tuple.InMemoryValueGenerationStrategy;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.PropertyFactory;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.tuple.ValueGenerator;
import org.hibernate.tuple.entity.BytecodeEnhancementMetadataNonPojoImpl;
import org.hibernate.tuple.entity.BytecodeEnhancementMetadataPojoImpl;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.tuple.entity.EntityTuplizerFactory;
import org.hibernate.tuple.entity.VersionProperty;
import org.hibernate.type.AssociationType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class EntityMetamodel
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EntityMetamodel.class);
    private static final int NO_VERSION_INDX = -66;
    private final SessionFactoryImplementor sessionFactory;
    private final String name;
    private final String rootName;
    private EntityType entityType;
    private final IdentifierProperty identifierAttribute;
    private final boolean versioned;
    private final int propertySpan;
    private final int versionPropertyIndex;
    private final NonIdentifierAttribute[] properties;
    private final String[] propertyNames;
    private final Type[] propertyTypes;
    private final boolean[] propertyLaziness;
    private final boolean[] propertyUpdateability;
    private final boolean[] nonlazyPropertyUpdateability;
    private final boolean[] propertyCheckability;
    private final boolean[] propertyInsertability;
    private final boolean[] propertyNullability;
    private final boolean[] propertyVersionability;
    private final CascadeStyle[] cascadeStyles;
    private final boolean hasPreInsertGeneratedValues;
    private final boolean hasPreUpdateGeneratedValues;
    private final boolean hasInsertGeneratedValues;
    private final boolean hasUpdateGeneratedValues;
    private final InMemoryValueGenerationStrategy[] inMemoryValueGenerationStrategies;
    private final InDatabaseValueGenerationStrategy[] inDatabaseValueGenerationStrategies;
    private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
    private final boolean hasCollections;
    private final BitSet mutablePropertiesIndexes;
    private final boolean hasLazyProperties;
    private final boolean hasNonIdentifierPropertyNamedId;
    private final int[] naturalIdPropertyNumbers;
    private final boolean hasImmutableNaturalId;
    private final boolean hasCacheableNaturalId;
    private boolean lazy;
    private final boolean hasCascades;
    private final boolean mutable;
    private final boolean isAbstract;
    private final boolean selectBeforeUpdate;
    private final boolean dynamicUpdate;
    private final boolean dynamicInsert;
    private final OptimisticLockStyle optimisticLockStyle;
    private final boolean polymorphic;
    private final String superclass;
    private final boolean explicitPolymorphism;
    private final boolean inherited;
    private final boolean hasSubclasses;
    private final Set subclassEntityNames;
    private final Map<Class, String> entityNameByInheritenceClassMap;
    private final EntityMode entityMode;
    private final EntityTuplizer entityTuplizer;
    private final BytecodeEnhancementMetadata bytecodeEnhancementMetadata;
    private static final GenerationStrategyPair NO_GEN_PAIR = new GenerationStrategyPair();

    public EntityMetamodel(PersistentClass persistentClass, EntityPersister persister, PersisterCreationContext creationContext) {
        this.sessionFactory = creationContext.getSessionFactory();
        this.name = persistentClass.getEntityName();
        this.rootName = persistentClass.getRootClass().getEntityName();
        this.identifierAttribute = PropertyFactory.buildIdentifierAttribute(persistentClass, this.sessionFactory.getIdentifierGenerator(this.rootName));
        this.versioned = persistentClass.isVersioned();
        SessionFactoryOptions sessionFactoryOptions = this.sessionFactory.getSessionFactoryOptions();
        if (persistentClass.hasPojoRepresentation()) {
            Set<String> idAttributeNames;
            CompositeType nonAggregatedCidMapper;
            Component identifierMapperComponent = persistentClass.getIdentifierMapper();
            if (identifierMapperComponent != null) {
                nonAggregatedCidMapper = (CompositeType)identifierMapperComponent.getType();
                idAttributeNames = new HashSet<String>();
                Iterator propertyItr = identifierMapperComponent.getPropertyIterator();
                while (propertyItr.hasNext()) {
                    idAttributeNames.add(((Property)propertyItr.next()).getName());
                }
            } else {
                nonAggregatedCidMapper = null;
                idAttributeNames = Collections.singleton(this.identifierAttribute.getName());
            }
            this.bytecodeEnhancementMetadata = BytecodeEnhancementMetadataPojoImpl.from(persistentClass, idAttributeNames, nonAggregatedCidMapper, sessionFactoryOptions.isCollectionsInDefaultFetchGroupEnabled(), creationContext);
        } else {
            this.bytecodeEnhancementMetadata = new BytecodeEnhancementMetadataNonPojoImpl(persistentClass.getEntityName());
        }
        boolean hasLazy = false;
        this.propertySpan = persistentClass.getPropertyClosureSpan();
        this.properties = new NonIdentifierAttribute[this.propertySpan];
        ArrayList<Integer> naturalIdNumbers = new ArrayList<Integer>();
        this.propertyNames = new String[this.propertySpan];
        this.propertyTypes = new Type[this.propertySpan];
        this.propertyUpdateability = new boolean[this.propertySpan];
        this.propertyInsertability = new boolean[this.propertySpan];
        this.nonlazyPropertyUpdateability = new boolean[this.propertySpan];
        this.propertyCheckability = new boolean[this.propertySpan];
        this.propertyNullability = new boolean[this.propertySpan];
        this.propertyVersionability = new boolean[this.propertySpan];
        this.propertyLaziness = new boolean[this.propertySpan];
        this.cascadeStyles = new CascadeStyle[this.propertySpan];
        this.inMemoryValueGenerationStrategies = new InMemoryValueGenerationStrategy[this.propertySpan];
        this.inDatabaseValueGenerationStrategies = new InDatabaseValueGenerationStrategy[this.propertySpan];
        boolean foundPreInsertGeneratedValues = false;
        boolean foundPreUpdateGeneratedValues = false;
        boolean foundPostInsertGeneratedValues = false;
        boolean foundPostUpdateGeneratedValues = false;
        Iterator iter = persistentClass.getPropertyClosureIterator();
        int i = 0;
        int tempVersionProperty = -66;
        boolean foundCascade = false;
        boolean foundCollection = false;
        BitSet mutableIndexes = new BitSet();
        boolean foundNonIdentifierPropertyNamedId = false;
        boolean foundUpdateableNaturalIdProperty = false;
        while (iter.hasNext()) {
            ValueGenerator generator;
            GenerationTiming timing;
            boolean lazy;
            Property prop = (Property)iter.next();
            if (prop == persistentClass.getVersion()) {
                tempVersionProperty = i;
                this.properties[i] = PropertyFactory.buildVersionProperty(persister, this.sessionFactory, i, prop, this.bytecodeEnhancementMetadata.isEnhancedForLazyLoading());
            } else {
                this.properties[i] = PropertyFactory.buildEntityBasedAttribute(persister, this.sessionFactory, i, prop, this.bytecodeEnhancementMetadata.isEnhancedForLazyLoading(), creationContext);
            }
            if (prop.isNaturalIdentifier()) {
                naturalIdNumbers.add(i);
                if (prop.isUpdateable()) {
                    foundUpdateableNaturalIdProperty = true;
                }
            }
            if ("id".equals(prop.getName())) {
                foundNonIdentifierPropertyNamedId = true;
            }
            boolean bl = lazy = !EnhancementHelper.includeInBaseFetchGroup(prop, this.bytecodeEnhancementMetadata.isEnhancedForLazyLoading(), entityName -> {
                MetadataImplementor metadata = creationContext.getMetadata();
                PersistentClass entityBinding = metadata.getEntityBinding(entityName);
                assert (entityBinding != null);
                return entityBinding.hasSubclasses();
            }, sessionFactoryOptions.isCollectionsInDefaultFetchGroupEnabled());
            if (lazy) {
                hasLazy = true;
            }
            this.propertyLaziness[i] = lazy;
            this.propertyNames[i] = this.properties[i].getName();
            this.propertyTypes[i] = this.properties[i].getType();
            this.propertyNullability[i] = this.properties[i].isNullable();
            this.propertyUpdateability[i] = this.properties[i].isUpdateable();
            this.propertyInsertability[i] = this.properties[i].isInsertable();
            this.propertyVersionability[i] = this.properties[i].isVersionable();
            this.nonlazyPropertyUpdateability[i] = this.properties[i].isUpdateable() && !lazy;
            this.propertyCheckability[i] = this.propertyUpdateability[i] || this.propertyTypes[i].isAssociationType() && ((AssociationType)this.propertyTypes[i]).isAlwaysDirtyChecked();
            this.cascadeStyles[i] = this.properties[i].getCascadeStyle();
            GenerationStrategyPair pair = EntityMetamodel.buildGenerationStrategyPair(this.sessionFactory, prop);
            this.inMemoryValueGenerationStrategies[i] = pair.getInMemoryStrategy();
            this.inDatabaseValueGenerationStrategies[i] = pair.getInDatabaseStrategy();
            if (pair.getInMemoryStrategy() != null && (timing = pair.getInMemoryStrategy().getGenerationTiming()) != GenerationTiming.NEVER && (generator = pair.getInMemoryStrategy().getValueGenerator()) != null) {
                if (timing == GenerationTiming.INSERT) {
                    foundPreInsertGeneratedValues = true;
                } else if (timing == GenerationTiming.ALWAYS) {
                    foundPreInsertGeneratedValues = true;
                    foundPreUpdateGeneratedValues = true;
                }
            }
            if (pair.getInDatabaseStrategy() != null) {
                timing = pair.getInDatabaseStrategy().getGenerationTiming();
                if (timing == GenerationTiming.INSERT) {
                    foundPostInsertGeneratedValues = true;
                } else if (timing == GenerationTiming.ALWAYS) {
                    foundPostInsertGeneratedValues = true;
                    foundPostUpdateGeneratedValues = true;
                }
            }
            if (this.properties[i].isLazy()) {
                hasLazy = true;
            }
            if (this.properties[i].getCascadeStyle() != CascadeStyles.NONE) {
                foundCascade = true;
            }
            if (this.indicatesCollection(this.properties[i].getType())) {
                foundCollection = true;
            }
            if (this.propertyTypes[i].isMutable() && this.propertyCheckability[i] && !(this.propertyTypes[i] instanceof ComponentType)) {
                mutableIndexes.set(i);
            }
            this.mapPropertyToIndex(prop, i);
            ++i;
        }
        if (naturalIdNumbers.size() == 0) {
            this.naturalIdPropertyNumbers = null;
            this.hasImmutableNaturalId = false;
            this.hasCacheableNaturalId = false;
        } else {
            this.naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
            this.hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
            this.hasCacheableNaturalId = persistentClass.getNaturalIdCacheRegionName() != null;
        }
        this.hasPreInsertGeneratedValues = foundPreInsertGeneratedValues;
        this.hasPreUpdateGeneratedValues = foundPreUpdateGeneratedValues;
        this.hasInsertGeneratedValues = foundPostInsertGeneratedValues;
        this.hasUpdateGeneratedValues = foundPostUpdateGeneratedValues;
        this.hasCascades = foundCascade;
        this.hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
        this.versionPropertyIndex = tempVersionProperty;
        this.hasLazyProperties = hasLazy;
        if (this.hasLazyProperties) {
            LOG.lazyPropertyFetchingAvailable(this.name);
        }
        this.lazy = persistentClass.isLazy() && (!persistentClass.hasPojoRepresentation() || !ReflectHelper.isFinalClass(persistentClass.getProxyInterface()));
        this.mutable = persistentClass.isMutable();
        if (persistentClass.isAbstract() == null) {
            this.isAbstract = persistentClass.hasPojoRepresentation() && ReflectHelper.isAbstractClass(persistentClass.getMappedClass());
        } else {
            this.isAbstract = persistentClass.isAbstract();
            if (!this.isAbstract && persistentClass.hasPojoRepresentation() && ReflectHelper.isAbstractClass(persistentClass.getMappedClass())) {
                LOG.entityMappedAsNonAbstract(this.name);
            }
        }
        this.selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
        this.dynamicUpdate = persistentClass.useDynamicUpdate() || this.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading() && this.getBytecodeEnhancementMetadata().getLazyAttributesMetadata().getFetchGroupNames().size() > 1;
        this.dynamicInsert = persistentClass.useDynamicInsert();
        this.polymorphic = persistentClass.isPolymorphic();
        this.explicitPolymorphism = persistentClass.isExplicitPolymorphism();
        this.inherited = persistentClass.isInherited();
        this.superclass = this.inherited ? persistentClass.getSuperclass().getEntityName() : null;
        this.hasSubclasses = persistentClass.hasSubclasses();
        this.optimisticLockStyle = persistentClass.getOptimisticLockStyle();
        boolean isAllOrDirty = this.optimisticLockStyle.isAllOrDirty();
        if (isAllOrDirty && !this.dynamicUpdate) {
            throw new MappingException("optimistic-lock=all|dirty requires dynamic-update=\"true\": " + this.name);
        }
        if (this.versionPropertyIndex != -66 && isAllOrDirty) {
            throw new MappingException("version and optimistic-lock=all|dirty are not a valid combination : " + this.name);
        }
        this.hasCollections = foundCollection;
        this.mutablePropertiesIndexes = mutableIndexes;
        iter = persistentClass.getSubclassIterator();
        HashSet<String> subclassEntityNamesLocal = new HashSet<String>();
        while (iter.hasNext()) {
            subclassEntityNamesLocal.add(((PersistentClass)iter.next()).getEntityName());
        }
        subclassEntityNamesLocal.add(this.name);
        this.subclassEntityNames = CollectionHelper.toSmallSet(subclassEntityNamesLocal);
        HashMap<Class, String> entityNameByInheritenceClassMapLocal = new HashMap<Class, String>();
        if (persistentClass.hasPojoRepresentation()) {
            entityNameByInheritenceClassMapLocal.put(persistentClass.getMappedClass(), persistentClass.getEntityName());
            iter = persistentClass.getSubclassIterator();
            while (iter.hasNext()) {
                PersistentClass pc = (PersistentClass)iter.next();
                entityNameByInheritenceClassMapLocal.put(pc.getMappedClass(), pc.getEntityName());
            }
        }
        this.entityNameByInheritenceClassMap = CollectionHelper.toSmallMap(entityNameByInheritenceClassMapLocal);
        this.entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
        EntityTuplizerFactory entityTuplizerFactory = sessionFactoryOptions.getEntityTuplizerFactory();
        String tuplizerClassName = persistentClass.getTuplizerImplClassName(this.entityMode);
        this.entityTuplizer = tuplizerClassName == null ? entityTuplizerFactory.constructDefaultTuplizer(this.entityMode, this, persistentClass) : entityTuplizerFactory.constructTuplizer(tuplizerClassName, this, persistentClass);
    }

    private static GenerationStrategyPair buildGenerationStrategyPair(SessionFactoryImplementor sessionFactory, Property mappingProperty) {
        ValueGeneration valueGeneration = mappingProperty.getValueGenerationStrategy();
        if (valueGeneration != null && valueGeneration.getGenerationTiming() != GenerationTiming.NEVER) {
            if (valueGeneration.getValueGenerator() != null) {
                return new GenerationStrategyPair(FullInMemoryValueGenerationStrategy.create(valueGeneration));
            }
            return new GenerationStrategyPair(EntityMetamodel.create(sessionFactory, mappingProperty, valueGeneration));
        }
        if (mappingProperty.getValue() instanceof Component) {
            CompositeGenerationStrategyPairBuilder builder = new CompositeGenerationStrategyPairBuilder(mappingProperty);
            EntityMetamodel.interpretPartialCompositeValueGeneration(sessionFactory, (Component)mappingProperty.getValue(), builder);
            return builder.buildPair();
        }
        return NO_GEN_PAIR;
    }

    private static void interpretPartialCompositeValueGeneration(SessionFactoryImplementor sessionFactory, Component composite, CompositeGenerationStrategyPairBuilder builder) {
        Iterator subProperties = composite.getPropertyIterator();
        while (subProperties.hasNext()) {
            Property subProperty = (Property)subProperties.next();
            builder.addPair(EntityMetamodel.buildGenerationStrategyPair(sessionFactory, subProperty));
        }
    }

    public static InDatabaseValueGenerationStrategyImpl create(SessionFactoryImplementor sessionFactoryImplementor, Property mappingProperty, ValueGeneration valueGeneration) {
        int numberOfMappedColumns = mappingProperty.getType().getColumnSpan(sessionFactoryImplementor);
        if (numberOfMappedColumns == 1) {
            return new InDatabaseValueGenerationStrategyImpl(valueGeneration.getGenerationTiming(), valueGeneration.referenceColumnInSql(), new String[]{valueGeneration.getDatabaseGeneratedReferencedColumnValue()});
        }
        if (valueGeneration.getDatabaseGeneratedReferencedColumnValue() != null) {
            LOG.debugf("Value generator specified column value in reference to multi-column attribute [%s -> %s]; ignoring", mappingProperty.getPersistentClass(), mappingProperty.getName());
        }
        return new InDatabaseValueGenerationStrategyImpl(valueGeneration.getGenerationTiming(), valueGeneration.referenceColumnInSql(), new String[numberOfMappedColumns]);
    }

    private void mapPropertyToIndex(Property prop, int i) {
        this.propertyIndexes.put(prop.getName(), i);
        if (prop.getValue() instanceof Component) {
            Iterator iter = ((Component)prop.getValue()).getPropertyIterator();
            while (iter.hasNext()) {
                Property subprop = (Property)iter.next();
                this.propertyIndexes.put(prop.getName() + '.' + subprop.getName(), i);
            }
        }
    }

    public EntityTuplizer getTuplizer() {
        return this.entityTuplizer;
    }

    public boolean isNaturalIdentifierInsertGenerated() {
        InDatabaseValueGenerationStrategy strategy = this.inDatabaseValueGenerationStrategies[this.naturalIdPropertyNumbers[0]];
        return strategy != null && strategy.getGenerationTiming() != GenerationTiming.NEVER;
    }

    public boolean isVersionGenerated() {
        InDatabaseValueGenerationStrategy strategy = this.inDatabaseValueGenerationStrategies[this.versionPropertyIndex];
        return strategy != null && strategy.getGenerationTiming() != GenerationTiming.NEVER;
    }

    public int[] getNaturalIdentifierProperties() {
        return this.naturalIdPropertyNumbers;
    }

    public boolean hasNaturalIdentifier() {
        return this.naturalIdPropertyNumbers != null;
    }

    public boolean isNaturalIdentifierCached() {
        return this.hasNaturalIdentifier() && this.hasCacheableNaturalId;
    }

    public boolean hasImmutableNaturalId() {
        return this.hasImmutableNaturalId;
    }

    public Set getSubclassEntityNames() {
        return this.subclassEntityNames;
    }

    private boolean indicatesCollection(Type type) {
        if (type.isCollectionType()) {
            return true;
        }
        if (type.isComponentType()) {
            Type[] subtypes;
            for (Type subtype : subtypes = ((CompositeType)type).getSubtypes()) {
                if (!this.indicatesCollection(subtype)) continue;
                return true;
            }
        }
        return false;
    }

    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    public String getName() {
        return this.name;
    }

    public String getRootName() {
        return this.rootName;
    }

    public EntityType getEntityType() {
        if (this.entityType == null) {
            this.entityType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(this.name);
        }
        return this.entityType;
    }

    public IdentifierProperty getIdentifierProperty() {
        return this.identifierAttribute;
    }

    public int getPropertySpan() {
        return this.propertySpan;
    }

    public int getVersionPropertyIndex() {
        return this.versionPropertyIndex;
    }

    public VersionProperty getVersionProperty() {
        if (-66 == this.versionPropertyIndex) {
            return null;
        }
        return (VersionProperty)this.properties[this.versionPropertyIndex];
    }

    public NonIdentifierAttribute[] getProperties() {
        return this.properties;
    }

    public int getPropertyIndex(String propertyName) {
        Integer index = this.getPropertyIndexOrNull(propertyName);
        if (index == null) {
            throw new HibernateException("Unable to resolve property: " + propertyName);
        }
        return index;
    }

    public Integer getPropertyIndexOrNull(String propertyName) {
        return this.propertyIndexes.get(propertyName);
    }

    public boolean hasCollections() {
        return this.hasCollections;
    }

    public boolean hasMutableProperties() {
        return !this.mutablePropertiesIndexes.isEmpty();
    }

    public BitSet getMutablePropertiesIndexes() {
        return this.mutablePropertiesIndexes;
    }

    public boolean hasNonIdentifierPropertyNamedId() {
        return this.hasNonIdentifierPropertyNamedId;
    }

    public boolean hasLazyProperties() {
        return this.hasLazyProperties;
    }

    public boolean hasCascades() {
        return this.hasCascades;
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public boolean isSelectBeforeUpdate() {
        return this.selectBeforeUpdate;
    }

    public boolean isDynamicUpdate() {
        return this.dynamicUpdate;
    }

    public boolean isDynamicInsert() {
        return this.dynamicInsert;
    }

    public OptimisticLockStyle getOptimisticLockStyle() {
        return this.optimisticLockStyle;
    }

    public boolean isPolymorphic() {
        return this.polymorphic;
    }

    public String getSuperclass() {
        return this.superclass;
    }

    public boolean isExplicitPolymorphism() {
        return this.explicitPolymorphism;
    }

    public boolean isInherited() {
        return this.inherited;
    }

    public boolean hasSubclasses() {
        return this.hasSubclasses;
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isVersioned() {
        return this.versioned;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public String findEntityNameByEntityClass(Class inheritenceClass) {
        return this.entityNameByInheritenceClassMap.get(inheritenceClass);
    }

    public String toString() {
        return "EntityMetamodel(" + this.name + ':' + ArrayHelper.toString(this.properties) + ')';
    }

    public String[] getPropertyNames() {
        return this.propertyNames;
    }

    public Type[] getPropertyTypes() {
        return this.propertyTypes;
    }

    public boolean[] getPropertyLaziness() {
        return this.propertyLaziness;
    }

    public boolean[] getPropertyUpdateability() {
        return this.propertyUpdateability;
    }

    public boolean[] getPropertyCheckability() {
        return this.propertyCheckability;
    }

    public boolean[] getNonlazyPropertyUpdateability() {
        return this.nonlazyPropertyUpdateability;
    }

    public boolean[] getPropertyInsertability() {
        return this.propertyInsertability;
    }

    public boolean[] getPropertyNullability() {
        return this.propertyNullability;
    }

    public boolean[] getPropertyVersionability() {
        return this.propertyVersionability;
    }

    public CascadeStyle[] getCascadeStyles() {
        return this.cascadeStyles;
    }

    public boolean hasPreInsertGeneratedValues() {
        return this.hasPreInsertGeneratedValues;
    }

    public boolean hasPreUpdateGeneratedValues() {
        return this.hasPreUpdateGeneratedValues;
    }

    public boolean hasInsertGeneratedValues() {
        return this.hasInsertGeneratedValues;
    }

    public boolean hasUpdateGeneratedValues() {
        return this.hasUpdateGeneratedValues;
    }

    public InMemoryValueGenerationStrategy[] getInMemoryValueGenerationStrategies() {
        return this.inMemoryValueGenerationStrategies;
    }

    public InDatabaseValueGenerationStrategy[] getInDatabaseValueGenerationStrategies() {
        return this.inDatabaseValueGenerationStrategies;
    }

    public EntityMode getEntityMode() {
        return this.entityMode;
    }

    public boolean isInstrumented() {
        return this.bytecodeEnhancementMetadata.isEnhancedForLazyLoading();
    }

    public BytecodeEnhancementMetadata getBytecodeEnhancementMetadata() {
        return this.bytecodeEnhancementMetadata;
    }

    private static class InDatabaseValueGenerationStrategyImpl
    implements InDatabaseValueGenerationStrategy {
        private final GenerationTiming timing;
        private final boolean referenceColumnInSql;
        private final String[] referencedColumnValues;

        private InDatabaseValueGenerationStrategyImpl(GenerationTiming timing, boolean referenceColumnInSql, String[] referencedColumnValues) {
            this.timing = timing;
            this.referenceColumnInSql = referenceColumnInSql;
            this.referencedColumnValues = referencedColumnValues;
        }

        @Override
        public GenerationTiming getGenerationTiming() {
            return this.timing;
        }

        @Override
        public boolean referenceColumnsInSql() {
            return this.referenceColumnInSql;
        }

        @Override
        public String[] getReferencedColumnValues() {
            return this.referencedColumnValues;
        }
    }

    private static class NoInDatabaseValueGenerationStrategy
    implements InDatabaseValueGenerationStrategy {
        public static final NoInDatabaseValueGenerationStrategy INSTANCE = new NoInDatabaseValueGenerationStrategy();

        private NoInDatabaseValueGenerationStrategy() {
        }

        @Override
        public GenerationTiming getGenerationTiming() {
            return GenerationTiming.NEVER;
        }

        @Override
        public boolean referenceColumnsInSql() {
            return true;
        }

        @Override
        public String[] getReferencedColumnValues() {
            return null;
        }
    }

    private static class FullInMemoryValueGenerationStrategy
    implements InMemoryValueGenerationStrategy {
        private final GenerationTiming timing;
        private final ValueGenerator generator;

        private FullInMemoryValueGenerationStrategy(GenerationTiming timing, ValueGenerator generator) {
            this.timing = timing;
            this.generator = generator;
        }

        public static FullInMemoryValueGenerationStrategy create(ValueGeneration valueGeneration) {
            return new FullInMemoryValueGenerationStrategy(valueGeneration.getGenerationTiming(), valueGeneration.getValueGenerator());
        }

        @Override
        public GenerationTiming getGenerationTiming() {
            return this.timing;
        }

        @Override
        public ValueGenerator getValueGenerator() {
            return this.generator;
        }
    }

    private static class NoInMemoryValueGenerationStrategy
    implements InMemoryValueGenerationStrategy {
        public static final NoInMemoryValueGenerationStrategy INSTANCE = new NoInMemoryValueGenerationStrategy();

        private NoInMemoryValueGenerationStrategy() {
        }

        @Override
        public GenerationTiming getGenerationTiming() {
            return GenerationTiming.NEVER;
        }

        @Override
        public ValueGenerator getValueGenerator() {
            return null;
        }
    }

    private static class CompositeGenerationStrategyPairBuilder {
        private final Property mappingProperty;
        private boolean hadInMemoryGeneration;
        private boolean hadInDatabaseGeneration;
        private List<InDatabaseValueGenerationStrategy> inDatabaseStrategies;

        public CompositeGenerationStrategyPairBuilder(Property mappingProperty) {
            this.mappingProperty = mappingProperty;
        }

        public void addPair(GenerationStrategyPair generationStrategyPair) {
            this.add(generationStrategyPair.getInMemoryStrategy());
            this.add(generationStrategyPair.getInDatabaseStrategy());
        }

        private void add(InMemoryValueGenerationStrategy inMemoryStrategy) {
            if (inMemoryStrategy.getGenerationTiming() != GenerationTiming.NEVER) {
                this.hadInMemoryGeneration = true;
            }
        }

        private void add(InDatabaseValueGenerationStrategy inDatabaseStrategy) {
            if (this.inDatabaseStrategies == null) {
                this.inDatabaseStrategies = new ArrayList<InDatabaseValueGenerationStrategy>();
            }
            this.inDatabaseStrategies.add(inDatabaseStrategy);
            if (inDatabaseStrategy.getGenerationTiming() != GenerationTiming.NEVER) {
                this.hadInDatabaseGeneration = true;
            }
        }

        public GenerationStrategyPair buildPair() {
            if (this.hadInMemoryGeneration && this.hadInDatabaseGeneration) {
                throw new ValueGenerationStrategyException("Composite attribute [" + this.mappingProperty.getName() + "] contained both in-memory and in-database value generation");
            }
            if (this.hadInMemoryGeneration) {
                throw new NotYetImplementedException("Still need to wire in composite in-memory value generation");
            }
            if (this.hadInDatabaseGeneration) {
                Component composite = (Component)this.mappingProperty.getValue();
                if (this.inDatabaseStrategies.size() != composite.getPropertySpan()) {
                    throw new ValueGenerationStrategyException("Internal error : mismatch between number of collected in-db generation strategies and number of attributes for composite attribute : " + this.mappingProperty.getName());
                }
                GenerationTiming timing = GenerationTiming.INSERT;
                boolean referenceColumns = false;
                String[] columnValues = new String[composite.getColumnSpan()];
                int propertyIndex = -1;
                int columnIndex = 0;
                Iterator subProperties = composite.getPropertyIterator();
                while (subProperties.hasNext()) {
                    InDatabaseValueGenerationStrategy subStrategy;
                    Property subProperty = (Property)subProperties.next();
                    if ((subStrategy = this.inDatabaseStrategies.get(++propertyIndex)).getGenerationTiming() == GenerationTiming.ALWAYS) {
                        timing = GenerationTiming.ALWAYS;
                    }
                    if (subStrategy.referenceColumnsInSql()) {
                        referenceColumns = true;
                    }
                    if (subStrategy.getReferencedColumnValues() == null) continue;
                    if (subStrategy.getReferencedColumnValues().length != subProperty.getColumnSpan()) {
                        throw new ValueGenerationStrategyException("Internal error : mismatch between number of collected 'referenced column values' and number of columns for composite attribute : " + this.mappingProperty.getName() + '.' + subProperty.getName());
                    }
                    System.arraycopy(subStrategy.getReferencedColumnValues(), 0, columnValues, columnIndex, subProperty.getColumnSpan());
                }
                return new GenerationStrategyPair(new InDatabaseValueGenerationStrategyImpl(timing, referenceColumns, columnValues));
            }
            return NO_GEN_PAIR;
        }
    }

    public static class ValueGenerationStrategyException
    extends HibernateException {
        public ValueGenerationStrategyException(String message) {
            super(message);
        }
    }

    public static class GenerationStrategyPair {
        private final InMemoryValueGenerationStrategy inMemoryStrategy;
        private final InDatabaseValueGenerationStrategy inDatabaseStrategy;

        public GenerationStrategyPair() {
            this(NoInMemoryValueGenerationStrategy.INSTANCE, NoInDatabaseValueGenerationStrategy.INSTANCE);
        }

        public GenerationStrategyPair(FullInMemoryValueGenerationStrategy inMemoryStrategy) {
            this(inMemoryStrategy, NoInDatabaseValueGenerationStrategy.INSTANCE);
        }

        public GenerationStrategyPair(InDatabaseValueGenerationStrategyImpl inDatabaseStrategy) {
            this(NoInMemoryValueGenerationStrategy.INSTANCE, inDatabaseStrategy);
        }

        public GenerationStrategyPair(InMemoryValueGenerationStrategy inMemoryStrategy, InDatabaseValueGenerationStrategy inDatabaseStrategy) {
            if (inMemoryStrategy == null) {
                inMemoryStrategy = NoInMemoryValueGenerationStrategy.INSTANCE;
            }
            if (inDatabaseStrategy == null) {
                inDatabaseStrategy = NoInDatabaseValueGenerationStrategy.INSTANCE;
            }
            if (inMemoryStrategy.getGenerationTiming() != GenerationTiming.NEVER && inDatabaseStrategy.getGenerationTiming() != GenerationTiming.NEVER) {
                throw new ValueGenerationStrategyException("in-memory and in-database value generation are mutually exclusive");
            }
            this.inMemoryStrategy = inMemoryStrategy;
            this.inDatabaseStrategy = inDatabaseStrategy;
        }

        public InMemoryValueGenerationStrategy getInMemoryStrategy() {
            return this.inMemoryStrategy;
        }

        public InDatabaseValueGenerationStrategy getInDatabaseStrategy() {
            return this.inDatabaseStrategy;
        }
    }
}

