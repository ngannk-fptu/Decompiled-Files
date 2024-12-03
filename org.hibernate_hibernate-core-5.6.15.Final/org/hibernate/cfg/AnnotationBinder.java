/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeOverride
 *  javax.persistence.AttributeOverrides
 *  javax.persistence.Basic
 *  javax.persistence.CascadeType
 *  javax.persistence.CollectionTable
 *  javax.persistence.Column
 *  javax.persistence.ConstraintMode
 *  javax.persistence.DiscriminatorColumn
 *  javax.persistence.DiscriminatorType
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.ElementCollection
 *  javax.persistence.Embeddable
 *  javax.persistence.Embedded
 *  javax.persistence.EmbeddedId
 *  javax.persistence.Entity
 *  javax.persistence.FetchType
 *  javax.persistence.ForeignKey
 *  javax.persistence.GeneratedValue
 *  javax.persistence.Id
 *  javax.persistence.IdClass
 *  javax.persistence.Index
 *  javax.persistence.Inheritance
 *  javax.persistence.InheritanceType
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinColumns
 *  javax.persistence.JoinTable
 *  javax.persistence.ManyToMany
 *  javax.persistence.ManyToOne
 *  javax.persistence.MapKey
 *  javax.persistence.MapKeyColumn
 *  javax.persistence.MapKeyJoinColumn
 *  javax.persistence.MapKeyJoinColumns
 *  javax.persistence.MappedSuperclass
 *  javax.persistence.MapsId
 *  javax.persistence.NamedNativeQueries
 *  javax.persistence.NamedNativeQuery
 *  javax.persistence.NamedQueries
 *  javax.persistence.NamedQuery
 *  javax.persistence.NamedStoredProcedureQueries
 *  javax.persistence.NamedStoredProcedureQuery
 *  javax.persistence.OneToMany
 *  javax.persistence.OneToOne
 *  javax.persistence.OrderBy
 *  javax.persistence.OrderColumn
 *  javax.persistence.PrimaryKeyJoinColumn
 *  javax.persistence.PrimaryKeyJoinColumns
 *  javax.persistence.SecondaryTable
 *  javax.persistence.SecondaryTables
 *  javax.persistence.SequenceGenerator
 *  javax.persistence.SequenceGenerators
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.SqlResultSetMapping
 *  javax.persistence.SqlResultSetMappings
 *  javax.persistence.Table
 *  javax.persistence.TableGenerator
 *  javax.persistence.TableGenerators
 *  javax.persistence.UniqueConstraint
 *  javax.persistence.Version
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XMethod
 *  org.hibernate.annotations.common.reflection.XPackage
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.ConstraintMode;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.SequenceGenerators;
import javax.persistence.SharedCacheMode;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.TableGenerators;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.GenericGenerators;
import org.hibernate.annotations.LazyGroup;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.ListIndexBase;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Parent;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Source;
import org.hibernate.annotations.Tables;
import org.hibernate.annotations.Tuplizer;
import org.hibernate.annotations.Tuplizers;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.annotations.common.reflection.XPackage;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.AnnotatedClassType;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.CannotForceNonNullableException;
import org.hibernate.cfg.ColumnsBuilder;
import org.hibernate.cfg.ComponentPropertyHolder;
import org.hibernate.cfg.CopyIdentifierComponentSecondPass;
import org.hibernate.cfg.CreateKeySecondPass;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3DiscriminatorColumn;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.IdGeneratorResolverSecondPass;
import org.hibernate.cfg.IndexColumn;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.JoinedSubclassFkSecondPass;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.cfg.OneToOneSecondPass;
import org.hibernate.cfg.PropertyContainer;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.PropertyHolderBuilder;
import org.hibernate.cfg.PropertyInferredData;
import org.hibernate.cfg.PropertyPreloadedData;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.SecondaryTableSecondPass;
import org.hibernate.cfg.ToOneBinder;
import org.hibernate.cfg.ToOneFkSecondPass;
import org.hibernate.cfg.UniqueConstraintHolder;
import org.hibernate.cfg.VerifyFetchProfileReferenceSecondPass;
import org.hibernate.cfg.WrappedInferredData;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.HCANNHelper;
import org.hibernate.cfg.annotations.MapKeyColumnDelegator;
import org.hibernate.cfg.annotations.MapKeyJoinColumnDelegator;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.cfg.annotations.PropertyBinder;
import org.hibernate.cfg.annotations.QueryBinder;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.cfg.internal.NullableDiscriminatorColumnSecondPass;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.jpa.event.internal.CallbackDefinitionResolverLegacyImpl;
import org.hibernate.jpa.event.spi.CallbackType;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.type.Type;

public final class AnnotationBinder {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AnnotationBinder.class);

    private AnnotationBinder() {
    }

    public static void bindDefaults(MetadataBuildingContext context) {
        List annotations;
        IdentifierGeneratorDefinition idGen;
        Map defaults = context.getBootstrapContext().getReflectionManager().getDefaults();
        List anns = (List)defaults.get(SequenceGenerator.class);
        if (anns != null) {
            for (SequenceGenerator ann : anns) {
                idGen = AnnotationBinder.buildIdGenerator((Annotation)ann, context);
                if (idGen == null) continue;
                context.getMetadataCollector().addDefaultIdentifierGenerator(idGen);
            }
        }
        if ((anns = (List)defaults.get(TableGenerator.class)) != null) {
            for (SequenceGenerator ann : anns) {
                idGen = AnnotationBinder.buildIdGenerator((Annotation)ann, context);
                if (idGen == null) continue;
                context.getMetadataCollector().addDefaultIdentifierGenerator(idGen);
            }
        }
        if ((anns = (List)defaults.get(TableGenerators.class)) != null) {
            anns.forEach(tableGenerators -> {
                for (TableGenerator tableGenerator : tableGenerators.value()) {
                    IdentifierGeneratorDefinition idGen = AnnotationBinder.buildIdGenerator((Annotation)tableGenerator, context);
                    if (idGen == null) continue;
                    context.getMetadataCollector().addDefaultIdentifierGenerator(idGen);
                }
            });
        }
        if ((anns = (List)defaults.get(SequenceGenerators.class)) != null) {
            anns.forEach(sequenceGenerators -> {
                for (SequenceGenerator ann : sequenceGenerators.value()) {
                    IdentifierGeneratorDefinition idGen = AnnotationBinder.buildIdGenerator((Annotation)ann, context);
                    if (idGen == null) continue;
                    context.getMetadataCollector().addDefaultIdentifierGenerator(idGen);
                }
            });
        }
        if ((anns = (List)defaults.get(NamedQuery.class)) != null) {
            for (SequenceGenerator ann : anns) {
                QueryBinder.bindQuery((NamedQuery)ann, context, true);
            }
        }
        if ((anns = (List)defaults.get(NamedNativeQuery.class)) != null) {
            for (SequenceGenerator ann : anns) {
                QueryBinder.bindNativeQuery((NamedNativeQuery)ann, context, true);
            }
        }
        if ((anns = (List)defaults.get(SqlResultSetMapping.class)) != null) {
            for (SequenceGenerator ann : anns) {
                QueryBinder.bindSqlResultSetMapping((SqlResultSetMapping)ann, context, true);
            }
        }
        if ((annotations = (List)defaults.get(NamedStoredProcedureQuery.class)) != null) {
            for (NamedStoredProcedureQuery annotation : annotations) {
                AnnotationBinder.bindNamedStoredProcedureQuery(annotation, context, true);
            }
        }
        if ((annotations = (List)defaults.get(NamedStoredProcedureQueries.class)) != null) {
            for (NamedStoredProcedureQuery annotation : annotations) {
                AnnotationBinder.bindNamedStoredProcedureQueries((NamedStoredProcedureQueries)annotation, context, true);
            }
        }
    }

    public static void bindPackage(ClassLoaderService cls, String packageName, MetadataBuildingContext context) {
        Object idGen;
        SequenceGenerator ann;
        Package packaze = cls.packageForNameOrNull(packageName);
        if (packaze == null) {
            return;
        }
        XPackage pckg = context.getBootstrapContext().getReflectionManager().toXPackage(packaze);
        if (pckg.isAnnotationPresent(SequenceGenerator.class)) {
            ann = (SequenceGenerator)pckg.getAnnotation(SequenceGenerator.class);
            idGen = AnnotationBinder.buildIdGenerator((Annotation)ann, context);
            context.getMetadataCollector().addIdentifierGenerator((IdentifierGeneratorDefinition)idGen);
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Add sequence generator with name: {0}", ((IdentifierGeneratorDefinition)idGen).getName());
            }
        }
        if (pckg.isAnnotationPresent(SequenceGenerators.class)) {
            ann = (SequenceGenerators)pckg.getAnnotation(SequenceGenerators.class);
            for (SequenceGenerator sequenceGenerator : ann.value()) {
                context.getMetadataCollector().addIdentifierGenerator(AnnotationBinder.buildIdGenerator((Annotation)sequenceGenerator, context));
            }
        }
        if (pckg.isAnnotationPresent(TableGenerator.class)) {
            ann = (TableGenerator)pckg.getAnnotation(TableGenerator.class);
            idGen = AnnotationBinder.buildIdGenerator((Annotation)ann, context);
            context.getMetadataCollector().addIdentifierGenerator((IdentifierGeneratorDefinition)idGen);
        }
        if (pckg.isAnnotationPresent(TableGenerators.class)) {
            ann = (TableGenerators)pckg.getAnnotation(TableGenerators.class);
            for (TableGenerator tableGenerator : ann.value()) {
                context.getMetadataCollector().addIdentifierGenerator(AnnotationBinder.buildIdGenerator((Annotation)tableGenerator, context));
            }
        }
        AnnotationBinder.bindGenericGenerators((XAnnotatedElement)pckg, context);
        AnnotationBinder.bindQueries((XAnnotatedElement)pckg, context);
        AnnotationBinder.bindFilterDefs((XAnnotatedElement)pckg, context);
        AnnotationBinder.bindTypeDefs((XAnnotatedElement)pckg, context);
        BinderHelper.bindAnyMetaDefs((XAnnotatedElement)pckg, context);
    }

    private static void bindGenericGenerators(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        GenericGenerator defAnn = (GenericGenerator)annotatedElement.getAnnotation(GenericGenerator.class);
        GenericGenerators defsAnn = (GenericGenerators)annotatedElement.getAnnotation(GenericGenerators.class);
        if (defAnn != null) {
            AnnotationBinder.bindGenericGenerator(defAnn, context);
        }
        if (defsAnn != null) {
            for (GenericGenerator def : defsAnn.value()) {
                AnnotationBinder.bindGenericGenerator(def, context);
            }
        }
    }

    private static void bindGenericGenerator(GenericGenerator def, MetadataBuildingContext context) {
        context.getMetadataCollector().addIdentifierGenerator(AnnotationBinder.buildIdGenerator(def, context));
    }

    private static void bindNamedJpaQueries(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        QueryBinder.bindSqlResultSetMapping((SqlResultSetMapping)annotatedElement.getAnnotation(SqlResultSetMapping.class), context, false);
        SqlResultSetMappings ann = (SqlResultSetMappings)annotatedElement.getAnnotation(SqlResultSetMappings.class);
        if (ann != null) {
            for (SqlResultSetMapping current : ann.value()) {
                QueryBinder.bindSqlResultSetMapping(current, context, false);
            }
        }
        QueryBinder.bindQuery((NamedQuery)annotatedElement.getAnnotation(NamedQuery.class), context, false);
        QueryBinder.bindQueries((NamedQueries)annotatedElement.getAnnotation(NamedQueries.class), context, false);
        QueryBinder.bindNativeQuery((NamedNativeQuery)annotatedElement.getAnnotation(NamedNativeQuery.class), context, false);
        QueryBinder.bindNativeQueries((javax.persistence.NamedNativeQueries)annotatedElement.getAnnotation(javax.persistence.NamedNativeQueries.class), context, false);
    }

    private static void bindQueries(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        AnnotationBinder.bindNamedJpaQueries(annotatedElement, context);
        QueryBinder.bindQuery((org.hibernate.annotations.NamedQuery)annotatedElement.getAnnotation(org.hibernate.annotations.NamedQuery.class), context);
        QueryBinder.bindQueries((org.hibernate.annotations.NamedQueries)annotatedElement.getAnnotation(org.hibernate.annotations.NamedQueries.class), context);
        QueryBinder.bindNativeQuery((org.hibernate.annotations.NamedNativeQuery)annotatedElement.getAnnotation(org.hibernate.annotations.NamedNativeQuery.class), context);
        QueryBinder.bindNativeQueries((NamedNativeQueries)annotatedElement.getAnnotation(NamedNativeQueries.class), context);
        AnnotationBinder.bindNamedStoredProcedureQuery((NamedStoredProcedureQuery)annotatedElement.getAnnotation(NamedStoredProcedureQuery.class), context, false);
        AnnotationBinder.bindNamedStoredProcedureQueries((NamedStoredProcedureQueries)annotatedElement.getAnnotation(NamedStoredProcedureQueries.class), context, false);
    }

    private static void bindNamedStoredProcedureQueries(NamedStoredProcedureQueries annotation, MetadataBuildingContext context, boolean isDefault) {
        if (annotation != null) {
            for (NamedStoredProcedureQuery queryAnnotation : annotation.value()) {
                AnnotationBinder.bindNamedStoredProcedureQuery(queryAnnotation, context, isDefault);
            }
        }
    }

    private static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, MetadataBuildingContext context, boolean isDefault) {
        if (annotation != null) {
            QueryBinder.bindNamedStoredProcedureQuery(annotation, context, isDefault);
        }
    }

    private static IdentifierGeneratorDefinition buildIdGenerator(Annotation generatorAnn, MetadataBuildingContext context) {
        if (generatorAnn == null) {
            return null;
        }
        IdentifierGeneratorDefinition.Builder definitionBuilder = new IdentifierGeneratorDefinition.Builder();
        if (generatorAnn instanceof TableGenerator) {
            context.getBuildingOptions().getIdGenerationTypeInterpreter().interpretTableGenerator((TableGenerator)generatorAnn, definitionBuilder);
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Add table generator with name: {0}", definitionBuilder.getName());
            }
        } else if (generatorAnn instanceof SequenceGenerator) {
            context.getBuildingOptions().getIdGenerationTypeInterpreter().interpretSequenceGenerator((SequenceGenerator)generatorAnn, definitionBuilder);
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Add sequence generator with name: {0}", definitionBuilder.getName());
            }
        } else if (generatorAnn instanceof GenericGenerator) {
            Parameter[] params;
            GenericGenerator genGen = (GenericGenerator)generatorAnn;
            definitionBuilder.setName(genGen.name());
            definitionBuilder.setStrategy(genGen.strategy());
            for (Parameter parameter : params = genGen.parameters()) {
                definitionBuilder.addParam(parameter.name(), parameter.value());
            }
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Add generic generator with name: {0}", definitionBuilder.getName());
            }
        } else {
            throw new AssertionFailure("Unknown Generator annotation: " + generatorAnn);
        }
        return definitionBuilder.build();
    }

    public static void bindClass(XClass clazzToProcess, Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext context) throws MappingException {
        PersistentClass superEntity;
        if (clazzToProcess.isAnnotationPresent(Entity.class) && clazzToProcess.isAnnotationPresent(MappedSuperclass.class)) {
            throw new AnnotationException("An entity cannot be annotated with both @Entity and @MappedSuperclass: " + clazzToProcess.getName());
        }
        if (clazzToProcess.isAnnotationPresent(Inheritance.class) && clazzToProcess.isAnnotationPresent(MappedSuperclass.class)) {
            LOG.unsupportedMappedSuperclassWithEntityInheritance(clazzToProcess.getName());
        }
        InheritanceState inheritanceState = inheritanceStatePerClass.get(clazzToProcess);
        AnnotatedClassType classType = context.getMetadataCollector().getClassType(clazzToProcess);
        if (AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals((Object)classType)) {
            AnnotationBinder.bindQueries((XAnnotatedElement)clazzToProcess, context);
            AnnotationBinder.bindTypeDefs((XAnnotatedElement)clazzToProcess, context);
            AnnotationBinder.bindFilterDefs((XAnnotatedElement)clazzToProcess, context);
        }
        if (!AnnotationBinder.isEntityClassType(clazzToProcess, classType)) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding entity from annotated class: %s", clazzToProcess.getName());
        }
        if ((superEntity = AnnotationBinder.getSuperEntity(clazzToProcess, inheritanceStatePerClass, context, inheritanceState)) != null && (clazzToProcess.getAnnotation(AttributeOverride.class) != null || clazzToProcess.getAnnotation(AttributeOverrides.class) != null)) {
            LOG.unsupportedAttributeOverrideWithEntityInheritance(clazzToProcess.getName());
        }
        PersistentClass persistentClass = AnnotationBinder.makePersistentClass(inheritanceState, superEntity, context);
        Entity entityAnn = (Entity)clazzToProcess.getAnnotation(Entity.class);
        org.hibernate.annotations.Entity hibEntityAnn = (org.hibernate.annotations.Entity)clazzToProcess.getAnnotation(org.hibernate.annotations.Entity.class);
        EntityBinder entityBinder = new EntityBinder(entityAnn, hibEntityAnn, clazzToProcess, persistentClass, context);
        entityBinder.setInheritanceState(inheritanceState);
        AnnotationBinder.bindQueries((XAnnotatedElement)clazzToProcess, context);
        AnnotationBinder.bindFilterDefs((XAnnotatedElement)clazzToProcess, context);
        AnnotationBinder.bindTypeDefs((XAnnotatedElement)clazzToProcess, context);
        BinderHelper.bindAnyMetaDefs((XAnnotatedElement)clazzToProcess, context);
        String schema = "";
        String table = "";
        String catalog = "";
        ArrayList<UniqueConstraintHolder> uniqueConstraints = new ArrayList();
        Table tabAnn = null;
        if (clazzToProcess.isAnnotationPresent(Table.class)) {
            tabAnn = (Table)clazzToProcess.getAnnotation(Table.class);
            table = tabAnn.name();
            schema = tabAnn.schema();
            catalog = tabAnn.catalog();
            uniqueConstraints = TableBinder.buildUniqueConstraintHolders(tabAnn.uniqueConstraints());
        }
        Ejb3JoinColumn[] inheritanceJoinedColumns = AnnotationBinder.makeInheritanceJoinColumns(clazzToProcess, context, inheritanceState, superEntity);
        Ejb3DiscriminatorColumn discriminatorColumn = InheritanceType.SINGLE_TABLE.equals((Object)inheritanceState.getType()) ? AnnotationBinder.processSingleTableDiscriminatorProperties(clazzToProcess, context, inheritanceState, entityBinder) : (InheritanceType.JOINED.equals((Object)inheritanceState.getType()) ? AnnotationBinder.processJoinedDiscriminatorProperties(clazzToProcess, context, inheritanceState, entityBinder) : null);
        entityBinder.setProxy((Proxy)clazzToProcess.getAnnotation(Proxy.class));
        entityBinder.setBatchSize((BatchSize)clazzToProcess.getAnnotation(BatchSize.class));
        entityBinder.setWhere((Where)clazzToProcess.getAnnotation(Where.class));
        AnnotationBinder.applyCacheSettings(entityBinder, clazzToProcess, context);
        AnnotationBinder.bindFilters(clazzToProcess, entityBinder, context);
        entityBinder.bindEntity();
        if (inheritanceState.hasTable()) {
            Check checkAnn = (Check)clazzToProcess.getAnnotation(Check.class);
            String constraints = checkAnn == null ? null : checkAnn.constraints();
            InFlightMetadataCollector.EntityTableXref denormalizedTableXref = inheritanceState.hasDenormalizedTable() ? context.getMetadataCollector().getEntityTableXref(superEntity.getEntityName()) : null;
            entityBinder.bindTable(schema, catalog, table, uniqueConstraints, constraints, denormalizedTableXref);
        } else {
            if (clazzToProcess.isAnnotationPresent(Table.class)) {
                LOG.invalidTableAnnotation(clazzToProcess.getName());
            }
            if (inheritanceState.getType() == InheritanceType.SINGLE_TABLE) {
                entityBinder.bindTableForDiscriminatedSubclass(context.getMetadataCollector().getEntityTableXref(superEntity.getEntityName()));
            }
        }
        PropertyHolder propertyHolder = PropertyHolderBuilder.buildPropertyHolder(clazzToProcess, persistentClass, entityBinder, context, inheritanceStatePerClass);
        SecondaryTable secTabAnn = (SecondaryTable)clazzToProcess.getAnnotation(SecondaryTable.class);
        SecondaryTables secTabsAnn = (SecondaryTables)clazzToProcess.getAnnotation(SecondaryTables.class);
        entityBinder.firstLevelSecondaryTablesBinding(secTabAnn, secTabsAnn);
        OnDelete onDeleteAnn = (OnDelete)clazzToProcess.getAnnotation(OnDelete.class);
        boolean onDeleteAppropriate = false;
        boolean isInheritanceRoot = !inheritanceState.hasParents();
        boolean hasSubclasses = inheritanceState.hasSiblings();
        if (InheritanceType.JOINED.equals((Object)inheritanceState.getType())) {
            if (inheritanceState.hasParents()) {
                onDeleteAppropriate = true;
                JoinedSubclass jsc = (JoinedSubclass)persistentClass;
                DependantValue key = new DependantValue(context, jsc.getTable(), jsc.getIdentifier());
                jsc.setKey(key);
                org.hibernate.annotations.ForeignKey fk = (org.hibernate.annotations.ForeignKey)clazzToProcess.getAnnotation(org.hibernate.annotations.ForeignKey.class);
                if (fk != null && !BinderHelper.isEmptyAnnotationValue(fk.name())) {
                    key.setForeignKeyName(fk.name());
                } else {
                    PrimaryKeyJoinColumn pkJoinColumn = (PrimaryKeyJoinColumn)clazzToProcess.getAnnotation(PrimaryKeyJoinColumn.class);
                    PrimaryKeyJoinColumns pkJoinColumns = (PrimaryKeyJoinColumns)clazzToProcess.getAnnotation(PrimaryKeyJoinColumns.class);
                    boolean noConstraintByDefault = context.getBuildingOptions().isNoConstraintByDefault();
                    if (pkJoinColumns != null && (pkJoinColumns.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || pkJoinColumns.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault)) {
                        key.setForeignKeyName("none");
                    } else if (pkJoinColumns != null && !StringHelper.isEmpty(pkJoinColumns.foreignKey().name())) {
                        key.setForeignKeyName(pkJoinColumns.foreignKey().name());
                    } else if (pkJoinColumn != null && (pkJoinColumn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || pkJoinColumn.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault)) {
                        key.setForeignKeyName("none");
                    } else if (pkJoinColumn != null && !StringHelper.isEmpty(pkJoinColumn.foreignKey().name())) {
                        key.setForeignKeyName(pkJoinColumn.foreignKey().name());
                    }
                }
                if (onDeleteAnn != null) {
                    key.setCascadeDeleteEnabled(OnDeleteAction.CASCADE.equals((Object)onDeleteAnn.action()));
                } else {
                    key.setCascadeDeleteEnabled(false);
                }
                context.getMetadataCollector().addSecondPass(new JoinedSubclassFkSecondPass(jsc, inheritanceJoinedColumns, key, context));
                context.getMetadataCollector().addSecondPass(new CreateKeySecondPass(jsc));
            }
            if (isInheritanceRoot && discriminatorColumn != null && (hasSubclasses || !discriminatorColumn.isImplicit())) {
                AnnotationBinder.bindDiscriminatorColumnToRootPersistentClass((RootClass)persistentClass, discriminatorColumn, entityBinder.getSecondaryTables(), propertyHolder, context);
                entityBinder.bindDiscriminatorValue();
            }
        } else if (InheritanceType.SINGLE_TABLE.equals((Object)inheritanceState.getType()) && isInheritanceRoot && (hasSubclasses || !discriminatorColumn.isImplicit())) {
            AnnotationBinder.bindDiscriminatorColumnToRootPersistentClass((RootClass)persistentClass, discriminatorColumn, entityBinder.getSecondaryTables(), propertyHolder, context);
            entityBinder.bindDiscriminatorValue();
        }
        if (onDeleteAnn != null && !onDeleteAppropriate) {
            LOG.invalidOnDeleteAnnotation(propertyHolder.getEntityName());
        }
        HashMap<String, IdentifierGeneratorDefinition> classGenerators = AnnotationBinder.buildGenerators((XAnnotatedElement)clazzToProcess, context);
        InheritanceState.ElementsToProcess elementsToProcess = inheritanceState.getElementsToProcess();
        inheritanceState.postProcess(persistentClass, entityBinder);
        boolean subclassAndSingleTableStrategy = inheritanceState.getType() == InheritanceType.SINGLE_TABLE && inheritanceState.hasParents();
        HashSet<String> idPropertiesIfIdClass = new HashSet<String>();
        boolean isIdClass = AnnotationBinder.mapAsIdClass(inheritanceStatePerClass, inheritanceState, persistentClass, entityBinder, propertyHolder, elementsToProcess, idPropertiesIfIdClass, context);
        if (!isIdClass) {
            entityBinder.setWrapIdsInEmbeddedComponents(elementsToProcess.getIdPropertyCount() > 1);
        }
        AnnotationBinder.processIdPropertiesIfNotAlready(inheritanceStatePerClass, context, persistentClass, entityBinder, propertyHolder, classGenerators, elementsToProcess, subclassAndSingleTableStrategy, idPropertiesIfIdClass);
        if (!inheritanceState.hasParents()) {
            RootClass rootClass = (RootClass)persistentClass;
            context.getMetadataCollector().addSecondPass(new CreateKeySecondPass(rootClass));
        } else {
            superEntity.addSubclass((Subclass)persistentClass);
        }
        context.getMetadataCollector().addEntityBinding(persistentClass);
        context.getMetadataCollector().addSecondPass(new SecondaryTableSecondPass(entityBinder, propertyHolder, (XAnnotatedElement)clazzToProcess));
        entityBinder.processComplementaryTableDefinitions((org.hibernate.annotations.Table)clazzToProcess.getAnnotation(org.hibernate.annotations.Table.class));
        entityBinder.processComplementaryTableDefinitions((Tables)clazzToProcess.getAnnotation(Tables.class));
        entityBinder.processComplementaryTableDefinitions(tabAnn);
        AnnotationBinder.bindCallbacks(clazzToProcess, persistentClass, context);
    }

    private static Ejb3DiscriminatorColumn processSingleTableDiscriminatorProperties(XClass clazzToProcess, MetadataBuildingContext context, InheritanceState inheritanceState, EntityBinder entityBinder) {
        boolean isRoot = !inheritanceState.hasParents();
        Ejb3DiscriminatorColumn discriminatorColumn = null;
        DiscriminatorColumn discAnn = (DiscriminatorColumn)clazzToProcess.getAnnotation(DiscriminatorColumn.class);
        DiscriminatorType discriminatorType = discAnn != null ? discAnn.discriminatorType() : DiscriminatorType.STRING;
        DiscriminatorFormula discFormulaAnn = (DiscriminatorFormula)clazzToProcess.getAnnotation(DiscriminatorFormula.class);
        if (isRoot) {
            discriminatorColumn = Ejb3DiscriminatorColumn.buildDiscriminatorColumn(discriminatorType, discAnn, discFormulaAnn, context);
        }
        if (discAnn != null && !isRoot) {
            LOG.invalidDiscriminatorAnnotation(clazzToProcess.getName());
        }
        String discriminatorValue = clazzToProcess.isAnnotationPresent(DiscriminatorValue.class) ? ((DiscriminatorValue)clazzToProcess.getAnnotation(DiscriminatorValue.class)).value() : null;
        entityBinder.setDiscriminatorValue(discriminatorValue);
        DiscriminatorOptions discriminatorOptions = (DiscriminatorOptions)clazzToProcess.getAnnotation(DiscriminatorOptions.class);
        if (discriminatorOptions != null) {
            entityBinder.setForceDiscriminator(discriminatorOptions.force());
            entityBinder.setInsertableDiscriminator(discriminatorOptions.insert());
        }
        return discriminatorColumn;
    }

    private static Ejb3DiscriminatorColumn processJoinedDiscriminatorProperties(XClass clazzToProcess, MetadataBuildingContext context, InheritanceState inheritanceState, EntityBinder entityBinder) {
        if (clazzToProcess.isAnnotationPresent(DiscriminatorFormula.class)) {
            throw new MappingException("@DiscriminatorFormula on joined inheritance not supported at this time");
        }
        DiscriminatorValue discriminatorValueAnnotation = (DiscriminatorValue)clazzToProcess.getAnnotation(DiscriminatorValue.class);
        String discriminatorValue = discriminatorValueAnnotation != null ? ((DiscriminatorValue)clazzToProcess.getAnnotation(DiscriminatorValue.class)).value() : null;
        entityBinder.setDiscriminatorValue(discriminatorValue);
        DiscriminatorColumn discriminatorColumnAnnotation = (DiscriminatorColumn)clazzToProcess.getAnnotation(DiscriminatorColumn.class);
        if (!inheritanceState.hasParents()) {
            boolean generateDiscriminatorColumn;
            if (discriminatorColumnAnnotation != null) {
                if (context.getBuildingOptions().ignoreExplicitDiscriminatorsForJoinedInheritance()) {
                    LOG.debugf("Ignoring explicit DiscriminatorColumn annotation on ", clazzToProcess.getName());
                    generateDiscriminatorColumn = false;
                } else {
                    LOG.applyingExplicitDiscriminatorColumnForJoined(clazzToProcess.getName(), "hibernate.discriminator.ignore_explicit_for_joined");
                    generateDiscriminatorColumn = true;
                }
            } else if (context.getBuildingOptions().createImplicitDiscriminatorsForJoinedInheritance()) {
                LOG.debug("Applying implicit DiscriminatorColumn using DiscriminatorColumn defaults");
                generateDiscriminatorColumn = true;
            } else {
                LOG.debug("Ignoring implicit (absent) DiscriminatorColumn");
                generateDiscriminatorColumn = false;
            }
            if (generateDiscriminatorColumn) {
                DiscriminatorType discriminatorType = discriminatorColumnAnnotation != null ? discriminatorColumnAnnotation.discriminatorType() : DiscriminatorType.STRING;
                return Ejb3DiscriminatorColumn.buildDiscriminatorColumn(discriminatorType, discriminatorColumnAnnotation, null, context);
            }
        } else if (discriminatorColumnAnnotation != null) {
            LOG.invalidDiscriminatorAnnotation(clazzToProcess.getName());
        }
        return null;
    }

    private static void processIdPropertiesIfNotAlready(Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext context, PersistentClass persistentClass, EntityBinder entityBinder, PropertyHolder propertyHolder, HashMap<String, IdentifierGeneratorDefinition> classGenerators, InheritanceState.ElementsToProcess elementsToProcess, boolean subclassAndSingleTableStrategy, Set<String> idPropertiesIfIdClass) {
        HashSet<String> missingIdProperties = new HashSet<String>(idPropertiesIfIdClass);
        for (PropertyData propertyAnnotatedElement : elementsToProcess.getElements()) {
            String propertyName = propertyAnnotatedElement.getPropertyName();
            if (!idPropertiesIfIdClass.contains(propertyName)) {
                AnnotationBinder.processElementAnnotations(propertyHolder, subclassAndSingleTableStrategy ? Nullability.FORCED_NULL : Nullability.NO_CONSTRAINT, propertyAnnotatedElement, classGenerators, entityBinder, false, false, false, context, inheritanceStatePerClass);
                continue;
            }
            missingIdProperties.remove(propertyName);
        }
        if (missingIdProperties.size() != 0) {
            StringBuilder missings = new StringBuilder();
            for (String property : missingIdProperties) {
                missings.append(property).append(", ");
            }
            throw new AnnotationException("Unable to find properties (" + missings.substring(0, missings.length() - 2) + ") in entity annotated with @IdClass:" + persistentClass.getEntityName());
        }
    }

    private static boolean mapAsIdClass(Map<XClass, InheritanceState> inheritanceStatePerClass, InheritanceState inheritanceState, PersistentClass persistentClass, EntityBinder entityBinder, PropertyHolder propertyHolder, InheritanceState.ElementsToProcess elementsToProcess, Set<String> idPropertiesIfIdClass, MetadataBuildingContext context) {
        XClass classWithIdClass = inheritanceState.getClassWithIdClass(false);
        if (classWithIdClass != null) {
            AccessType propertyAccessor;
            PropertyPreloadedData baseInferredData;
            PropertyPreloadedData inferredData;
            IdClass idClass = (IdClass)classWithIdClass.getAnnotation(IdClass.class);
            XClass compositeClass = context.getBootstrapContext().getReflectionManager().toXClass(idClass.value());
            boolean isFakeIdClass = AnnotationBinder.isIdClassPkOfTheAssociatedEntity(elementsToProcess, compositeClass, inferredData = new PropertyPreloadedData(entityBinder.getPropertyAccessType(), "id", compositeClass), baseInferredData = new PropertyPreloadedData(entityBinder.getPropertyAccessType(), "id", classWithIdClass), propertyAccessor = entityBinder.getPropertyAccessor((XAnnotatedElement)compositeClass), inheritanceStatePerClass, context);
            if (isFakeIdClass) {
                return false;
            }
            boolean isComponent = true;
            String generatorType = "assigned";
            String generator = "";
            boolean ignoreIdAnnotations = entityBinder.isIgnoreIdAnnotations();
            entityBinder.setIgnoreIdAnnotations(true);
            propertyHolder.setInIdClass(true);
            AnnotationBinder.bindIdClass(generatorType, generator, inferredData, baseInferredData, null, propertyHolder, isComponent, propertyAccessor, entityBinder, true, false, context, inheritanceStatePerClass);
            propertyHolder.setInIdClass(null);
            inferredData = new PropertyPreloadedData(propertyAccessor, "_identifierMapper", compositeClass);
            Component mapper = AnnotationBinder.fillComponent(propertyHolder, inferredData, baseInferredData, propertyAccessor, false, entityBinder, true, true, false, context, inheritanceStatePerClass);
            entityBinder.setIgnoreIdAnnotations(ignoreIdAnnotations);
            persistentClass.setIdentifierMapper(mapper);
            org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(classWithIdClass, inheritanceStatePerClass, context);
            if (superclass != null) {
                superclass.setDeclaredIdentifierMapper(mapper);
            } else {
                persistentClass.setDeclaredIdentifierMapper(mapper);
            }
            Property property = new Property();
            property.setName("_identifierMapper");
            property.setUpdateable(false);
            property.setInsertable(false);
            property.setValue(mapper);
            property.setPropertyAccessorName("embedded");
            persistentClass.addProperty(property);
            entityBinder.setIgnoreIdAnnotations(true);
            Iterator properties = mapper.getPropertyIterator();
            while (properties.hasNext()) {
                idPropertiesIfIdClass.add(((Property)properties.next()).getName());
            }
            return true;
        }
        return false;
    }

    private static boolean isIdClassPkOfTheAssociatedEntity(InheritanceState.ElementsToProcess elementsToProcess, XClass compositeClass, PropertyData inferredData, PropertyData baseInferredData, AccessType propertyAccessor, Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext context) {
        if (elementsToProcess.getIdPropertyCount() == 1) {
            PropertyData idPropertyOnBaseClass = AnnotationBinder.getUniqueIdPropertyFromBaseClass(inferredData, baseInferredData, propertyAccessor, context);
            InheritanceState state = inheritanceStatePerClass.get(idPropertyOnBaseClass.getClassOrElement());
            if (state == null) {
                return false;
            }
            XClass associatedClassWithIdClass = state.getClassWithIdClass(true);
            if (associatedClassWithIdClass == null) {
                XProperty property = idPropertyOnBaseClass.getProperty();
                return property.isAnnotationPresent(ManyToOne.class) || property.isAnnotationPresent(OneToOne.class);
            }
            XClass idClass = context.getBootstrapContext().getReflectionManager().toXClass(((IdClass)associatedClassWithIdClass.getAnnotation(IdClass.class)).value());
            return idClass.equals(compositeClass);
        }
        return false;
    }

    private static void applyCacheSettings(EntityBinder binder, XClass clazzToProcess, MetadataBuildingContext context) {
        binder.applyCaching(clazzToProcess, AnnotationBinder.determineSharedCacheMode(context), context);
    }

    private static SharedCacheMode determineSharedCacheMode(MetadataBuildingContext context) {
        return context.getBuildingOptions().getSharedCacheMode();
    }

    private static PersistentClass makePersistentClass(InheritanceState inheritanceState, PersistentClass superEntity, MetadataBuildingContext metadataBuildingContext) {
        if (!inheritanceState.hasParents()) {
            return new RootClass(metadataBuildingContext);
        }
        if (InheritanceType.SINGLE_TABLE.equals((Object)inheritanceState.getType())) {
            return new SingleTableSubclass(superEntity, metadataBuildingContext);
        }
        if (InheritanceType.JOINED.equals((Object)inheritanceState.getType())) {
            return new JoinedSubclass(superEntity, metadataBuildingContext);
        }
        if (InheritanceType.TABLE_PER_CLASS.equals((Object)inheritanceState.getType())) {
            return new UnionSubclass(superEntity, metadataBuildingContext);
        }
        throw new AssertionFailure("Unknown inheritance type: " + inheritanceState.getType());
    }

    private static Ejb3JoinColumn[] makeInheritanceJoinColumns(XClass clazzToProcess, MetadataBuildingContext context, InheritanceState inheritanceState, PersistentClass superEntity) {
        boolean hasJoinedColumns;
        Ejb3JoinColumn[] inheritanceJoinedColumns = null;
        boolean bl = hasJoinedColumns = inheritanceState.hasParents() && InheritanceType.JOINED.equals((Object)inheritanceState.getType());
        if (hasJoinedColumns) {
            boolean explicitInheritanceJoinedColumns;
            PrimaryKeyJoinColumns jcsAnn = (PrimaryKeyJoinColumns)clazzToProcess.getAnnotation(PrimaryKeyJoinColumns.class);
            boolean bl2 = explicitInheritanceJoinedColumns = jcsAnn != null && jcsAnn.value().length != 0;
            if (explicitInheritanceJoinedColumns) {
                int nbrOfInhJoinedColumns = jcsAnn.value().length;
                inheritanceJoinedColumns = new Ejb3JoinColumn[nbrOfInhJoinedColumns];
                for (int colIndex = 0; colIndex < nbrOfInhJoinedColumns; ++colIndex) {
                    PrimaryKeyJoinColumn jcAnn = jcsAnn.value()[colIndex];
                    inheritanceJoinedColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(jcAnn, null, superEntity.getIdentifier(), null, null, context);
                }
            } else {
                PrimaryKeyJoinColumn jcAnn = (PrimaryKeyJoinColumn)clazzToProcess.getAnnotation(PrimaryKeyJoinColumn.class);
                inheritanceJoinedColumns = new Ejb3JoinColumn[]{Ejb3JoinColumn.buildJoinColumn(jcAnn, null, superEntity.getIdentifier(), null, null, context)};
            }
            LOG.trace("Subclass joined column(s) created");
        } else if (clazzToProcess.isAnnotationPresent(PrimaryKeyJoinColumns.class) || clazzToProcess.isAnnotationPresent(PrimaryKeyJoinColumn.class)) {
            LOG.invalidPrimaryKeyJoinColumnAnnotation(clazzToProcess.getName());
        }
        return inheritanceJoinedColumns;
    }

    private static PersistentClass getSuperEntity(XClass clazzToProcess, Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext context, InheritanceState inheritanceState) {
        PersistentClass superEntity;
        InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(clazzToProcess, inheritanceStatePerClass);
        PersistentClass persistentClass = superEntity = superEntityState != null ? context.getMetadataCollector().getEntityBinding(superEntityState.getClazz().getName()) : null;
        if (superEntity == null && inheritanceState.hasParents()) {
            throw new AssertionFailure("Subclass has to be binded after it's mother class: " + superEntityState.getClazz().getName());
        }
        return superEntity;
    }

    private static boolean isEntityClassType(XClass clazzToProcess, AnnotatedClassType classType) {
        if (AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals((Object)classType) || AnnotatedClassType.NONE.equals((Object)classType) || AnnotatedClassType.EMBEDDABLE.equals((Object)classType)) {
            if (AnnotatedClassType.NONE.equals((Object)classType) && clazzToProcess.isAnnotationPresent(org.hibernate.annotations.Entity.class)) {
                LOG.missingEntityAnnotation(clazzToProcess.getName());
            }
            return false;
        }
        if (!classType.equals((Object)AnnotatedClassType.ENTITY)) {
            throw new AnnotationException("Annotated class should have a @javax.persistence.Entity, @javax.persistence.Embeddable or @javax.persistence.EmbeddedSuperclass annotation: " + clazzToProcess.getName());
        }
        return true;
    }

    private static void bindFilters(XClass annotatedClass, EntityBinder entityBinder, MetadataBuildingContext context) {
        AnnotatedClassType classType;
        AnnotationBinder.bindFilters((XAnnotatedElement)annotatedClass, entityBinder);
        for (XClass classToProcess = annotatedClass.getSuperclass(); classToProcess != null && AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals((Object)(classType = context.getMetadataCollector().getClassType(classToProcess))); classToProcess = classToProcess.getSuperclass()) {
            AnnotationBinder.bindFilters((XAnnotatedElement)classToProcess, entityBinder);
        }
    }

    private static void bindFilters(XAnnotatedElement annotatedElement, EntityBinder entityBinder) {
        Filter filterAnn;
        Filters filtersAnn = (Filters)annotatedElement.getAnnotation(Filters.class);
        if (filtersAnn != null) {
            for (Filter filter : filtersAnn.value()) {
                entityBinder.addFilter(filter);
            }
        }
        if ((filterAnn = (Filter)annotatedElement.getAnnotation(Filter.class)) != null) {
            entityBinder.addFilter(filterAnn);
        }
    }

    private static void bindFilterDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        FilterDef defAnn = (FilterDef)annotatedElement.getAnnotation(FilterDef.class);
        FilterDefs defsAnn = (FilterDefs)annotatedElement.getAnnotation(FilterDefs.class);
        if (defAnn != null) {
            AnnotationBinder.bindFilterDef(defAnn, context);
        }
        if (defsAnn != null) {
            for (FilterDef def : defsAnn.value()) {
                AnnotationBinder.bindFilterDef(def, context);
            }
        }
    }

    private static void bindFilterDef(FilterDef defAnn, MetadataBuildingContext context) {
        HashMap<String, Type> params = new HashMap<String, Type>();
        for (ParamDef param : defAnn.parameters()) {
            params.put(param.name(), context.getMetadataCollector().getTypeResolver().heuristicType(param.type()));
        }
        FilterDefinition def = new FilterDefinition(defAnn.name(), defAnn.defaultCondition(), params);
        LOG.debugf("Binding filter definition: %s", def.getFilterName());
        context.getMetadataCollector().addFilterDefinition(def);
    }

    private static void bindTypeDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        TypeDef defAnn = (TypeDef)annotatedElement.getAnnotation(TypeDef.class);
        TypeDefs defsAnn = (TypeDefs)annotatedElement.getAnnotation(TypeDefs.class);
        if (defAnn != null) {
            AnnotationBinder.bindTypeDef(defAnn, context);
        }
        if (defsAnn != null) {
            for (TypeDef def : defsAnn.value()) {
                AnnotationBinder.bindTypeDef(def, context);
            }
        }
    }

    private static void bindTypeDef(TypeDef defAnn, MetadataBuildingContext context) {
        Properties params = new Properties();
        for (Parameter param : defAnn.parameters()) {
            params.setProperty(param.name(), param.value());
        }
        if (BinderHelper.isEmptyAnnotationValue(defAnn.name()) && defAnn.defaultForType().equals(Void.TYPE)) {
            throw new AnnotationException("Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass " + defAnn.typeClass().getName());
        }
        String typeBindMessageF = "Binding type definition: %s";
        if (!BinderHelper.isEmptyAnnotationValue(defAnn.name())) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Binding type definition: %s", defAnn.name());
            }
            context.getMetadataCollector().addTypeDefinition(new TypeDefinition(defAnn.name(), (Class)defAnn.typeClass(), null, params));
        }
        if (!defAnn.defaultForType().equals(Void.TYPE)) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Binding type definition: %s", defAnn.defaultForType().getName());
            }
            context.getMetadataCollector().addTypeDefinition(new TypeDefinition(defAnn.defaultForType().getName(), defAnn.typeClass(), new String[]{defAnn.defaultForType().getName()}, params));
        }
    }

    private static void bindCallbacks(XClass entityClass, final PersistentClass persistentClass, MetadataBuildingContext context) {
        final ReflectionManager reflectionManager = context.getBootstrapContext().getReflectionManager();
        for (CallbackType callbackType : CallbackType.values()) {
            persistentClass.addCallbackDefinitions(CallbackDefinitionResolverLegacyImpl.resolveEntityCallbacks(reflectionManager, entityClass, callbackType));
        }
        context.getMetadataCollector().addSecondPass(new SecondPass(){

            @Override
            public void doSecondPass(Map persistentClasses) throws MappingException {
                Iterator propertyIterator = persistentClass.getDeclaredPropertyIterator();
                while (propertyIterator.hasNext()) {
                    Property property = (Property)propertyIterator.next();
                    if (!property.isComposite()) continue;
                    for (CallbackType callbackType : CallbackType.values()) {
                        property.addCallbackDefinitions(CallbackDefinitionResolverLegacyImpl.resolveEmbeddableCallbacks(reflectionManager, persistentClass.getMappedClass(), property, callbackType));
                    }
                }
            }
        });
    }

    public static void bindFetchProfilesForClass(XClass clazzToProcess, MetadataBuildingContext context) {
        AnnotationBinder.bindFetchProfiles((XAnnotatedElement)clazzToProcess, context);
    }

    public static void bindFetchProfilesForPackage(ClassLoaderService cls, String packageName, MetadataBuildingContext context) {
        Package packaze = cls.packageForNameOrNull(packageName);
        if (packaze == null) {
            return;
        }
        ReflectionManager reflectionManager = context.getBootstrapContext().getReflectionManager();
        XPackage pckg = reflectionManager.toXPackage(packaze);
        AnnotationBinder.bindFetchProfiles((XAnnotatedElement)pckg, context);
    }

    private static void bindFetchProfiles(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        FetchProfile fetchProfileAnnotation = (FetchProfile)annotatedElement.getAnnotation(FetchProfile.class);
        FetchProfiles fetchProfileAnnotations = (FetchProfiles)annotatedElement.getAnnotation(FetchProfiles.class);
        if (fetchProfileAnnotation != null) {
            AnnotationBinder.bindFetchProfile(fetchProfileAnnotation, context);
        }
        if (fetchProfileAnnotations != null) {
            for (FetchProfile profile : fetchProfileAnnotations.value()) {
                AnnotationBinder.bindFetchProfile(profile, context);
            }
        }
    }

    private static void bindFetchProfile(FetchProfile fetchProfileAnnotation, MetadataBuildingContext context) {
        for (FetchProfile.FetchOverride fetch : fetchProfileAnnotation.fetchOverrides()) {
            org.hibernate.annotations.FetchMode mode = fetch.mode();
            if (!mode.equals((Object)org.hibernate.annotations.FetchMode.JOIN)) {
                throw new MappingException("Only FetchMode.JOIN is currently supported");
            }
            context.getMetadataCollector().addSecondPass(new VerifyFetchProfileReferenceSecondPass(fetchProfileAnnotation.name(), fetch, context));
        }
    }

    private static void bindDiscriminatorColumnToRootPersistentClass(RootClass rootClass, Ejb3DiscriminatorColumn discriminatorColumn, Map<String, Join> secondaryTables, PropertyHolder propertyHolder, MetadataBuildingContext context) {
        if (rootClass.getDiscriminator() == null) {
            if (discriminatorColumn == null) {
                throw new AssertionFailure("discriminator column should have been built");
            }
            discriminatorColumn.setJoins(secondaryTables);
            discriminatorColumn.setPropertyHolder(propertyHolder);
            SimpleValue discriminatorColumnBinding = new SimpleValue(context, rootClass.getTable());
            rootClass.setDiscriminator(discriminatorColumnBinding);
            discriminatorColumn.linkWithValue(discriminatorColumnBinding);
            discriminatorColumnBinding.setTypeName(discriminatorColumn.getDiscriminatorTypeName());
            rootClass.setPolymorphic(true);
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Setting discriminator for entity {0}", rootClass.getEntityName());
            }
            context.getMetadataCollector().addSecondPass(new NullableDiscriminatorColumnSecondPass(rootClass.getEntityName()));
        }
    }

    static int addElementsOfClass(List<PropertyData> elements, PropertyContainer propertyContainer, MetadataBuildingContext context) {
        int idPropertyCounter = 0;
        for (XProperty p : propertyContainer.propertyIterator()) {
            int currentIdPropertyCounter = AnnotationBinder.addProperty(propertyContainer, p, elements, context);
            idPropertyCounter += currentIdPropertyCounter;
        }
        return idPropertyCounter;
    }

    private static int addProperty(PropertyContainer propertyContainer, XProperty property, List<PropertyData> inFlightPropertyDataList, MetadataBuildingContext context) {
        for (PropertyData propertyData : inFlightPropertyDataList) {
            if (!propertyData.getPropertyName().equals(property.getName())) continue;
            Id incomingIdProperty = (Id)property.getAnnotation(Id.class);
            Id existingIdProperty = (Id)propertyData.getProperty().getAnnotation(Id.class);
            if (incomingIdProperty != null && existingIdProperty == null) {
                throw new MappingException(String.format("You cannot override the [%s] non-identifier property from the [%s] base class or @MappedSuperclass and make it an identifier in the [%s] subclass!", propertyData.getProperty().getName(), propertyData.getProperty().getDeclaringClass().getName(), property.getDeclaringClass().getName()));
            }
            return 0;
        }
        XClass declaringClass = propertyContainer.getDeclaringClass();
        XClass entity = propertyContainer.getEntityAtStake();
        int idPropertyCounter = 0;
        PropertyInferredData propertyAnnotatedElement = new PropertyInferredData(declaringClass, property, propertyContainer.getClassLevelAccessType().getType(), context.getBootstrapContext().getReflectionManager());
        XProperty element = propertyAnnotatedElement.getProperty();
        if (element.isAnnotationPresent(Id.class) || element.isAnnotationPresent(EmbeddedId.class)) {
            inFlightPropertyDataList.add(0, propertyAnnotatedElement);
            if (context.getBuildingOptions().isSpecjProprietarySyntaxEnabled() && element.isAnnotationPresent(Id.class) && element.isAnnotationPresent(javax.persistence.Column.class)) {
                String columnName = ((javax.persistence.Column)element.getAnnotation(javax.persistence.Column.class)).name();
                for (XProperty prop : declaringClass.getDeclaredProperties(AccessType.FIELD.getType())) {
                    if (prop.isAnnotationPresent(MapsId.class)) continue;
                    boolean isRequiredAnnotationPresent = false;
                    JoinColumns groupAnnotation = (JoinColumns)prop.getAnnotation(JoinColumns.class);
                    if (prop.isAnnotationPresent(JoinColumn.class) && ((JoinColumn)prop.getAnnotation(JoinColumn.class)).name().equals(columnName)) {
                        isRequiredAnnotationPresent = true;
                    } else if (prop.isAnnotationPresent(JoinColumns.class)) {
                        for (JoinColumn columnAnnotation : groupAnnotation.value()) {
                            if (!columnName.equals(columnAnnotation.name())) continue;
                            isRequiredAnnotationPresent = true;
                            break;
                        }
                    }
                    if (!isRequiredAnnotationPresent) continue;
                    PropertyInferredData specJPropertyData = new PropertyInferredData(declaringClass, prop, propertyContainer.getClassLevelAccessType().getType(), context.getBootstrapContext().getReflectionManager());
                    context.getMetadataCollector().addPropertyAnnotatedWithMapsIdSpecj(entity, specJPropertyData, element.toString());
                }
            }
            if (element.isAnnotationPresent(ManyToOne.class) || element.isAnnotationPresent(OneToOne.class)) {
                context.getMetadataCollector().addToOneAndIdProperty(entity, propertyAnnotatedElement);
            }
            ++idPropertyCounter;
        } else {
            inFlightPropertyDataList.add(propertyAnnotatedElement);
        }
        if (element.isAnnotationPresent(MapsId.class)) {
            context.getMetadataCollector().addPropertyAnnotatedWithMapsId(entity, propertyAnnotatedElement);
        }
        return idPropertyCounter;
    }

    /*
     * WARNING - void declaration
     */
    private static void processElementAnnotations(PropertyHolder propertyHolder, Nullability nullability, PropertyData inferredData, HashMap<String, IdentifierGeneratorDefinition> classGenerators, EntityBinder entityBinder, boolean isIdentifierMapper, boolean isComponentEmbedded, boolean inSecondPass, MetadataBuildingContext context, Map<XClass, InheritanceState> inheritanceStatePerClass) throws MappingException {
        block86: {
            NaturalId naturalIdAnn;
            XProperty property;
            if (!propertyHolder.isComponent() && entityBinder.isPropertyDefinedInSuperHierarchy(inferredData.getPropertyName())) {
                LOG.debugf("Skipping attribute [%s : %s] as it was already processed as part of super hierarchy", inferredData.getClassOrElementName(), inferredData.getPropertyName());
                return;
            }
            boolean traceEnabled = LOG.isTraceEnabled();
            if (traceEnabled) {
                LOG.tracev("Processing annotations of {0}.{1}", propertyHolder.getEntityName(), inferredData.getPropertyName());
            }
            if ((property = inferredData.getProperty()).isAnnotationPresent(Parent.class)) {
                if (!propertyHolder.isComponent()) {
                    throw new AnnotationException("@Parent cannot be applied outside an embeddable object: " + BinderHelper.getPath(propertyHolder, inferredData));
                }
                propertyHolder.setParentProperty(property.getName());
                return;
            }
            ColumnsBuilder columnsBuilder = new ColumnsBuilder(propertyHolder, nullability, property, inferredData, entityBinder, context).extractMetadata();
            Ejb3Column[] columns = columnsBuilder.getColumns();
            Ejb3JoinColumn[] joinColumns = columnsBuilder.getJoinColumns();
            XClass returnedClass = inferredData.getClassOrElement();
            PropertyBinder propertyBinder = new PropertyBinder();
            propertyBinder.setName(inferredData.getPropertyName());
            propertyBinder.setReturnedClassName(inferredData.getTypeName());
            propertyBinder.setAccessType(inferredData.getDefaultAccess());
            propertyBinder.setHolder(propertyHolder);
            propertyBinder.setProperty(property);
            propertyBinder.setReturnedClass(inferredData.getPropertyClass());
            propertyBinder.setBuildingContext(context);
            if (isIdentifierMapper) {
                propertyBinder.setInsertable(false);
                propertyBinder.setUpdatable(false);
            }
            propertyBinder.setDeclaringClass(inferredData.getDeclaringClass());
            propertyBinder.setEntityBinder(entityBinder);
            propertyBinder.setInheritanceStatePerClass(inheritanceStatePerClass);
            boolean isId = !entityBinder.isIgnoreIdAnnotations() && (property.isAnnotationPresent(Id.class) || property.isAnnotationPresent(EmbeddedId.class));
            propertyBinder.setId(isId);
            LazyGroup lazyGroupAnnotation = (LazyGroup)property.getAnnotation(LazyGroup.class);
            if (lazyGroupAnnotation != null) {
                propertyBinder.setLazyGroup(lazyGroupAnnotation.value());
            }
            if (property.isAnnotationPresent(Version.class)) {
                if (isIdentifierMapper) {
                    throw new AnnotationException("@IdClass class should not have @Version property");
                }
                if (!(propertyHolder.getPersistentClass() instanceof RootClass)) {
                    throw new AnnotationException("Unable to define/override @Version on a subclass: " + propertyHolder.getEntityName());
                }
                if (!propertyHolder.isEntity()) {
                    throw new AnnotationException("Unable to define @Version on an embedded class: " + propertyHolder.getEntityName());
                }
                if (traceEnabled) {
                    LOG.tracev("{0} is a version property", inferredData.getPropertyName());
                }
                RootClass rootClass = (RootClass)propertyHolder.getPersistentClass();
                propertyBinder.setColumns(columns);
                Property prop = propertyBinder.makePropertyValueAndBind();
                AnnotationBinder.setVersionInformation(property, propertyBinder);
                rootClass.setVersion(prop);
                org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(inferredData.getDeclaringClass(), inheritanceStatePerClass, context);
                if (superclass != null) {
                    superclass.setDeclaredVersion(prop);
                } else {
                    rootClass.setDeclaredVersion(prop);
                }
                SimpleValue simpleValue = (SimpleValue)prop.getValue();
                simpleValue.setNullValue("undefined");
                rootClass.setOptimisticLockStyle(OptimisticLockStyle.VERSION);
                if (traceEnabled) {
                    LOG.tracev("Version name: {0}, unsavedValue: {1}", rootClass.getVersion().getName(), ((SimpleValue)rootClass.getVersion().getValue()).getNullValue());
                }
            } else {
                boolean forcePersist;
                boolean bl = forcePersist = property.isAnnotationPresent(MapsId.class) || property.isAnnotationPresent(Id.class);
                if (property.isAnnotationPresent(ManyToOne.class)) {
                    ManyToOne ann = (ManyToOne)property.getAnnotation(ManyToOne.class);
                    if (property.isAnnotationPresent(javax.persistence.Column.class) || property.isAnnotationPresent(Columns.class)) {
                        throw new AnnotationException("@Column(s) not allowed on a @ManyToOne property: " + BinderHelper.getPath(propertyHolder, inferredData));
                    }
                    NotFound notFound = (NotFound)property.getAnnotation(NotFound.class);
                    NotFoundAction notFoundAction = notFound == null ? null : notFound.action();
                    boolean hasNotFound = notFoundAction != null;
                    AnnotationBinder.checkFetchModeAgainstNotFound(propertyHolder.getEntityName(), property.getName(), hasNotFound, ann.fetch());
                    Cascade cascade = (Cascade)property.getAnnotation(Cascade.class);
                    OnDelete onDeleteAnn = (OnDelete)property.getAnnotation(OnDelete.class);
                    boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals((Object)onDeleteAnn.action());
                    JoinTable assocTable = propertyHolder.getJoinTable(property);
                    if (assocTable != null) {
                        Join join = propertyHolder.addJoin(assocTable, false);
                        for (Ejb3JoinColumn ejb3JoinColumn : joinColumns) {
                            ejb3JoinColumn.setExplicitTableName(join.getTable().getName());
                        }
                    }
                    boolean mandatory = !ann.optional() || property.isAnnotationPresent(Id.class) || property.isAnnotationPresent(MapsId.class) && !hasNotFound;
                    AnnotationBinder.bindManyToOne(AnnotationBinder.getCascadeStrategy(ann.cascade(), cascade, false, forcePersist), joinColumns, !mandatory, notFoundAction, onDeleteCascade, ToOneBinder.getTargetEntity(inferredData, context), propertyHolder, inferredData, false, isIdentifierMapper, inSecondPass, propertyBinder, context);
                } else if (property.isAnnotationPresent(OneToOne.class)) {
                    boolean hasPkjc;
                    OneToOne ann = (OneToOne)property.getAnnotation(OneToOne.class);
                    if (property.isAnnotationPresent(javax.persistence.Column.class) || property.isAnnotationPresent(Columns.class)) {
                        throw new AnnotationException("@Column(s) not allowed on a @OneToOne property: " + BinderHelper.getPath(propertyHolder, inferredData));
                    }
                    boolean trueOneToOne = hasPkjc = property.isAnnotationPresent(PrimaryKeyJoinColumn.class) || property.isAnnotationPresent(PrimaryKeyJoinColumns.class);
                    Cascade hibernateCascade = (Cascade)property.getAnnotation(Cascade.class);
                    NotFound notFound = (NotFound)property.getAnnotation(NotFound.class);
                    NotFoundAction notFoundAction = notFound == null ? null : notFound.action();
                    boolean hasNotFound = notFoundAction != null;
                    AnnotationBinder.checkFetchModeAgainstNotFound(propertyHolder.getEntityName(), property.getName(), hasNotFound, ann.fetch());
                    boolean mandatory = !ann.optional() || property.isAnnotationPresent(Id.class) || property.isAnnotationPresent(MapsId.class) && !hasNotFound;
                    AnnotationBinder.checkFetchModeAgainstNotFound(propertyHolder.getEntityName(), property.getName(), hasNotFound, ann.fetch());
                    OnDelete onDeleteAnn = (OnDelete)property.getAnnotation(OnDelete.class);
                    boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals((Object)onDeleteAnn.action());
                    JoinTable assocTable = propertyHolder.getJoinTable(property);
                    if (assocTable != null) {
                        Join join = propertyHolder.addJoin(assocTable, false);
                        for (Ejb3JoinColumn joinColumn : joinColumns) {
                            joinColumn.setExplicitTableName(join.getTable().getName());
                        }
                    }
                    AnnotationBinder.bindOneToOne(AnnotationBinder.getCascadeStrategy(ann.cascade(), hibernateCascade, ann.orphanRemoval(), forcePersist), joinColumns, !mandatory, AnnotationBinder.getFetchMode(ann.fetch()), notFoundAction, onDeleteCascade, ToOneBinder.getTargetEntity(inferredData, context), propertyHolder, inferredData, ann.mappedBy(), trueOneToOne, isIdentifierMapper, inSecondPass, propertyBinder, context);
                } else if (property.isAnnotationPresent(Any.class)) {
                    if (property.isAnnotationPresent(javax.persistence.Column.class) || property.isAnnotationPresent(Columns.class)) {
                        throw new AnnotationException("@Column(s) not allowed on a @Any property: " + BinderHelper.getPath(propertyHolder, inferredData));
                    }
                    Cascade hibernateCascade = (Cascade)property.getAnnotation(Cascade.class);
                    OnDelete onDeleteAnn = (OnDelete)property.getAnnotation(OnDelete.class);
                    boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals((Object)onDeleteAnn.action());
                    JoinTable assocTable = propertyHolder.getJoinTable(property);
                    if (assocTable != null) {
                        Join join = propertyHolder.addJoin(assocTable, false);
                        for (Ejb3JoinColumn joinColumn : joinColumns) {
                            joinColumn.setExplicitTableName(join.getTable().getName());
                        }
                    }
                    AnnotationBinder.bindAny(AnnotationBinder.getCascadeStrategy(null, hibernateCascade, false, forcePersist), joinColumns, onDeleteCascade, nullability, propertyHolder, inferredData, entityBinder, isIdentifierMapper, context);
                } else if (property.isAnnotationPresent(OneToMany.class) || property.isAnnotationPresent(ManyToMany.class) || property.isAnnotationPresent(ElementCollection.class) || property.isAnnotationPresent(ManyToAny.class)) {
                    void var32_129;
                    void var32_123;
                    void var32_118;
                    Ejb3Column[] elementColumns;
                    PropertyData virtualProperty;
                    IndexColumn indexColumn;
                    OneToMany oneToManyAnn = (OneToMany)property.getAnnotation(OneToMany.class);
                    ManyToMany manyToManyAnn = (ManyToMany)property.getAnnotation(ManyToMany.class);
                    ElementCollection elementCollectionAnn = (ElementCollection)property.getAnnotation(ElementCollection.class);
                    if ((oneToManyAnn != null || manyToManyAnn != null || elementCollectionAnn != null) && AnnotationBinder.isToManyAssociationWithinEmbeddableCollection(propertyHolder)) {
                        throw new AnnotationException("@OneToMany, @ManyToMany or @ElementCollection cannot be used inside an @Embeddable that is also contained within an @ElementCollection: " + BinderHelper.getPath(propertyHolder, inferredData));
                    }
                    if (property.isAnnotationPresent(OrderColumn.class)) {
                        indexColumn = IndexColumn.buildColumnFromAnnotation((OrderColumn)property.getAnnotation(OrderColumn.class), propertyHolder, inferredData, entityBinder.getSecondaryTables(), context);
                        if (property.isAnnotationPresent(ListIndexBase.class)) {
                            indexColumn.setBase(((ListIndexBase)property.getAnnotation(ListIndexBase.class)).value());
                        }
                    } else {
                        indexColumn = IndexColumn.buildColumnFromAnnotation((org.hibernate.annotations.IndexColumn)property.getAnnotation(org.hibernate.annotations.IndexColumn.class), propertyHolder, inferredData, context);
                    }
                    CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(propertyHolder.getEntityName(), property, !indexColumn.isImplicit(), property.isAnnotationPresent(MapKeyType.class), context);
                    collectionBinder.setIndexColumn(indexColumn);
                    collectionBinder.setMapKey((MapKey)property.getAnnotation(MapKey.class));
                    collectionBinder.setPropertyName(inferredData.getPropertyName());
                    collectionBinder.setBatchSize((BatchSize)property.getAnnotation(BatchSize.class));
                    collectionBinder.setJpaOrderBy((OrderBy)property.getAnnotation(OrderBy.class));
                    collectionBinder.setSqlOrderBy((org.hibernate.annotations.OrderBy)property.getAnnotation(org.hibernate.annotations.OrderBy.class));
                    collectionBinder.setSort((Sort)property.getAnnotation(Sort.class));
                    collectionBinder.setNaturalSort((SortNatural)property.getAnnotation(SortNatural.class));
                    collectionBinder.setComparatorSort((SortComparator)property.getAnnotation(SortComparator.class));
                    Cache cachAnn = (Cache)property.getAnnotation(Cache.class);
                    collectionBinder.setCache(cachAnn);
                    collectionBinder.setPropertyHolder(propertyHolder);
                    Cascade hibernateCascade = (Cascade)property.getAnnotation(Cascade.class);
                    NotFound notFound = (NotFound)property.getAnnotation(NotFound.class);
                    boolean ignoreNotFound = notFound != null && notFound.action().equals((Object)NotFoundAction.IGNORE);
                    collectionBinder.setIgnoreNotFound(ignoreNotFound);
                    collectionBinder.setCollectionType(inferredData.getProperty().getElementClass());
                    collectionBinder.setBuildingContext(context);
                    collectionBinder.setAccessType(inferredData.getDefaultAccess());
                    boolean isJPA2ForValueMapping = property.isAnnotationPresent(ElementCollection.class);
                    PropertyData propertyData = virtualProperty = isJPA2ForValueMapping ? inferredData : new WrappedInferredData(inferredData, "element");
                    if (property.isAnnotationPresent(javax.persistence.Column.class) || property.isAnnotationPresent(Formula.class)) {
                        javax.persistence.Column column = (javax.persistence.Column)property.getAnnotation(javax.persistence.Column.class);
                        Formula formulaAnn = (Formula)property.getAnnotation(Formula.class);
                        elementColumns = Ejb3Column.buildColumnFromAnnotation(new javax.persistence.Column[]{column}, formulaAnn, (Comment)property.getAnnotation(Comment.class), nullability, propertyHolder, virtualProperty, entityBinder.getSecondaryTables(), context);
                    } else if (property.isAnnotationPresent(Columns.class)) {
                        Columns columns2 = (Columns)property.getAnnotation(Columns.class);
                        elementColumns = Ejb3Column.buildColumnFromAnnotation(columns2.columns(), null, (Comment)property.getAnnotation(Comment.class), nullability, propertyHolder, virtualProperty, entityBinder.getSecondaryTables(), context);
                    } else {
                        elementColumns = Ejb3Column.buildColumnFromAnnotation(null, null, (Comment)property.getAnnotation(Comment.class), nullability, propertyHolder, virtualProperty, entityBinder.getSecondaryTables(), context);
                    }
                    Object var32_116 = null;
                    Ejb3JoinColumn[] isJPA2 = null;
                    if (property.isAnnotationPresent(MapKeyColumn.class)) {
                        isJPA2 = Boolean.TRUE;
                        javax.persistence.Column[] columnArray = new javax.persistence.Column[]{new MapKeyColumnDelegator((MapKeyColumn)property.getAnnotation(MapKeyColumn.class))};
                    }
                    if (isJPA2 == null) {
                        isJPA2 = Boolean.TRUE;
                    }
                    javax.persistence.Column[] columnArray = var32_118 != null && ((void)var32_118).length > 0 ? var32_118 : null;
                    WrappedInferredData mapKeyVirtualProperty = new WrappedInferredData(inferredData, "mapkey");
                    Ejb3Column[] mapColumns = Ejb3Column.buildColumnFromAnnotation(columnArray, null, (Comment)property.getAnnotation(Comment.class), Nullability.FORCED_NOT_NULL, propertyHolder, isJPA2.booleanValue() ? inferredData : mapKeyVirtualProperty, isJPA2.booleanValue() ? "_KEY" : null, entityBinder.getSecondaryTables(), context);
                    collectionBinder.setMapKeyColumns(mapColumns);
                    Object var32_120 = null;
                    isJPA2 = null;
                    if (property.isAnnotationPresent(MapKeyJoinColumns.class)) {
                        isJPA2 = Boolean.TRUE;
                        MapKeyJoinColumn[] mapKeyJoinColumns = ((MapKeyJoinColumns)property.getAnnotation(MapKeyJoinColumns.class)).value();
                        JoinColumn[] joinColumnArray = new JoinColumn[mapKeyJoinColumns.length];
                        int index = 0;
                        for (MapKeyJoinColumn joinColumn : mapKeyJoinColumns) {
                            joinColumnArray[index] = new MapKeyJoinColumnDelegator(joinColumn);
                            ++index;
                        }
                        if (property.isAnnotationPresent(MapKeyJoinColumn.class)) {
                            throw new AnnotationException("@MapKeyJoinColumn and @MapKeyJoinColumns used on the same property: " + BinderHelper.getPath(propertyHolder, inferredData));
                        }
                    } else if (property.isAnnotationPresent(MapKeyJoinColumn.class)) {
                        isJPA2 = Boolean.TRUE;
                        JoinColumn[] joinColumnArray = new JoinColumn[]{new MapKeyJoinColumnDelegator((MapKeyJoinColumn)property.getAnnotation(MapKeyJoinColumn.class))};
                    }
                    if (isJPA2 == null) {
                        isJPA2 = Boolean.TRUE;
                    }
                    mapKeyVirtualProperty = new WrappedInferredData(inferredData, "mapkey");
                    Ejb3JoinColumn[] mapJoinColumns = Ejb3JoinColumn.buildJoinColumnsWithDefaultColumnSuffix((JoinColumn[])var32_123, (Comment)property.getAnnotation(Comment.class), null, entityBinder.getSecondaryTables(), propertyHolder, isJPA2.booleanValue() ? inferredData.getPropertyName() : mapKeyVirtualProperty.getPropertyName(), isJPA2.booleanValue() ? "_KEY" : null, context);
                    collectionBinder.setMapKeyManyToManyColumns(mapJoinColumns);
                    collectionBinder.setEmbedded(property.isAnnotationPresent(Embedded.class));
                    collectionBinder.setElementColumns(elementColumns);
                    collectionBinder.setProperty(property);
                    if (oneToManyAnn != null && manyToManyAnn != null) {
                        throw new AnnotationException("@OneToMany and @ManyToMany on the same property is not allowed: " + propertyHolder.getEntityName() + "." + inferredData.getPropertyName());
                    }
                    Object var32_124 = null;
                    if (oneToManyAnn != null) {
                        for (Ejb3JoinColumn column : joinColumns) {
                            if (!column.isSecondary()) continue;
                            throw new NotYetImplementedException("Collections having FK in secondary table");
                        }
                        collectionBinder.setFkJoinColumns(joinColumns);
                        String string = oneToManyAnn.mappedBy();
                        collectionBinder.setTargetEntity(context.getBootstrapContext().getReflectionManager().toXClass(oneToManyAnn.targetEntity()));
                        collectionBinder.setCascadeStrategy(AnnotationBinder.getCascadeStrategy(oneToManyAnn.cascade(), hibernateCascade, oneToManyAnn.orphanRemoval(), false));
                        collectionBinder.setOneToMany(true);
                    } else if (elementCollectionAnn != null) {
                        for (Ejb3JoinColumn column : joinColumns) {
                            if (!column.isSecondary()) continue;
                            throw new NotYetImplementedException("Collections having FK in secondary table");
                        }
                        collectionBinder.setFkJoinColumns(joinColumns);
                        String string = "";
                        Class targetElement = elementCollectionAnn.targetClass();
                        collectionBinder.setTargetEntity(context.getBootstrapContext().getReflectionManager().toXClass(targetElement));
                        collectionBinder.setOneToMany(true);
                    } else if (manyToManyAnn != null) {
                        String string = manyToManyAnn.mappedBy();
                        collectionBinder.setTargetEntity(context.getBootstrapContext().getReflectionManager().toXClass(manyToManyAnn.targetEntity()));
                        collectionBinder.setCascadeStrategy(AnnotationBinder.getCascadeStrategy(manyToManyAnn.cascade(), hibernateCascade, false, false));
                        collectionBinder.setOneToMany(false);
                    } else if (property.isAnnotationPresent(ManyToAny.class)) {
                        String string = "";
                        collectionBinder.setTargetEntity(context.getBootstrapContext().getReflectionManager().toXClass(Void.TYPE));
                        collectionBinder.setCascadeStrategy(AnnotationBinder.getCascadeStrategy(null, hibernateCascade, false, false));
                        collectionBinder.setOneToMany(false);
                    }
                    collectionBinder.setMappedBy((String)var32_129);
                    AnnotationBinder.bindJoinedTableAssociation(property, context, entityBinder, collectionBinder, propertyHolder, inferredData, (String)var32_129);
                    OnDelete onDeleteAnn = (OnDelete)property.getAnnotation(OnDelete.class);
                    boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals((Object)onDeleteAnn.action());
                    collectionBinder.setCascadeDeleteEnabled(onDeleteCascade);
                    if (isIdentifierMapper) {
                        collectionBinder.setInsertable(false);
                        collectionBinder.setUpdatable(false);
                    }
                    if (property.isAnnotationPresent(CollectionId.class)) {
                        HashMap localGenerators = (HashMap)classGenerators.clone();
                        localGenerators.putAll(AnnotationBinder.buildGenerators((XAnnotatedElement)property, context));
                        collectionBinder.setLocalGenerators(localGenerators);
                    }
                    collectionBinder.setInheritanceStatePerClass(inheritanceStatePerClass);
                    collectionBinder.setDeclaringClass(inferredData.getDeclaringClass());
                    collectionBinder.bind();
                } else if (!isId || !entityBinder.isIgnoreIdAnnotations()) {
                    PropertyData overridingProperty;
                    boolean isComponent = false;
                    boolean isOverridden = false;
                    if ((isId || propertyHolder.isOrWithinEmbeddedId() || propertyHolder.isInIdClass()) && (overridingProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(isId, propertyHolder, property.getName(), context)) != null) {
                        isOverridden = true;
                        InheritanceState state = inheritanceStatePerClass.get(overridingProperty.getClassOrElement());
                        if (state != null) {
                            isComponent = isComponent || state.hasIdClassOrEmbeddedId() != false;
                        }
                        columns = columnsBuilder.overrideColumnFromMapperOrMapsIdProperty(isId);
                    }
                    boolean bl2 = isComponent = isComponent || property.isAnnotationPresent(Embedded.class) || property.isAnnotationPresent(EmbeddedId.class) || returnedClass.isAnnotationPresent(Embeddable.class);
                    if (isComponent) {
                        String referencedEntityName = null;
                        if (isOverridden) {
                            PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(isId, propertyHolder, property.getName(), context);
                            referencedEntityName = mapsIdProperty.getClassOrElementName();
                        }
                        AccessType propertyAccessor = entityBinder.getPropertyAccessor((XAnnotatedElement)property);
                        propertyBinder = AnnotationBinder.bindComponent(inferredData, propertyHolder, propertyAccessor, entityBinder, isIdentifierMapper, context, isComponentEmbedded, isId, inheritanceStatePerClass, referencedEntityName, isOverridden ? (Ejb3JoinColumn[])columns : null);
                    } else {
                        boolean optional = true;
                        boolean lazy = false;
                        if (property.isAnnotationPresent(Basic.class)) {
                            Basic basic = (Basic)property.getAnnotation(Basic.class);
                            optional = basic.optional();
                            boolean bl3 = lazy = basic.fetch() == FetchType.LAZY;
                        }
                        if (isId || !optional && nullability != Nullability.FORCED_NULL) {
                            for (Ejb3Column col : columns) {
                                if (isId && col.isFormula()) {
                                    throw new CannotForceNonNullableException(String.format(Locale.ROOT, "Identifier property [%s] cannot contain formula mapping [%s]", HCANNHelper.annotatedElementSignature(property), col.getFormulaString()));
                                }
                                col.forceNotNull();
                            }
                        }
                        propertyBinder.setLazy(lazy);
                        propertyBinder.setColumns(columns);
                        if (isOverridden) {
                            PropertyData propertyData = BinderHelper.getPropertyOverriddenByMapperOrMapsId(isId, propertyHolder, property.getName(), context);
                            propertyBinder.setReferencedEntityName(propertyData.getClassOrElementName());
                        }
                        propertyBinder.makePropertyValueAndBind();
                    }
                    if (isOverridden) {
                        PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(isId, propertyHolder, property.getName(), context);
                        IdentifierGeneratorDefinition.Builder foreignGeneratorBuilder = new IdentifierGeneratorDefinition.Builder();
                        foreignGeneratorBuilder.setName("Hibernate-local--foreign generator");
                        foreignGeneratorBuilder.setStrategy("foreign");
                        foreignGeneratorBuilder.addParam("property", mapsIdProperty.getPropertyName());
                        IdentifierGeneratorDefinition identifierGeneratorDefinition = foreignGeneratorBuilder.build();
                        if (AnnotationBinder.isGlobalGeneratorNameGlobal(context)) {
                            IdGeneratorResolverSecondPass secondPass = new IdGeneratorResolverSecondPass((SimpleValue)propertyBinder.getValue(), property, identifierGeneratorDefinition.getStrategy(), identifierGeneratorDefinition.getName(), context, identifierGeneratorDefinition);
                            context.getMetadataCollector().addSecondPass(secondPass);
                        } else {
                            HashMap localGenerators = (HashMap)classGenerators.clone();
                            localGenerators.put(identifierGeneratorDefinition.getName(), identifierGeneratorDefinition);
                            BinderHelper.makeIdGenerator((SimpleValue)propertyBinder.getValue(), property, identifierGeneratorDefinition.getStrategy(), identifierGeneratorDefinition.getName(), context, localGenerators);
                        }
                    }
                    if (isId) {
                        SimpleValue value = (SimpleValue)propertyBinder.getValue();
                        if (!isOverridden) {
                            AnnotationBinder.processId(propertyHolder, inferredData, value, classGenerators, isIdentifierMapper, context);
                        }
                    }
                }
            }
            org.hibernate.annotations.Index index = (org.hibernate.annotations.Index)property.getAnnotation(org.hibernate.annotations.Index.class);
            if (index != null) {
                if (joinColumns != null) {
                    for (Ejb3JoinColumn column : joinColumns) {
                        column.addIndex(index, inSecondPass);
                    }
                } else if (columns != null) {
                    for (Ejb3Column column : columns) {
                        column.addIndex(index, inSecondPass);
                    }
                }
            }
            if ((naturalIdAnn = (NaturalId)property.getAnnotation(NaturalId.class)) == null) break block86;
            if (joinColumns != null) {
                for (Ejb3JoinColumn ejb3JoinColumn : joinColumns) {
                    String keyName = "UK_" + Constraint.hashedName(ejb3JoinColumn.getTable().getName() + "_NaturalID");
                    ejb3JoinColumn.addUniqueKey(keyName, inSecondPass);
                }
            } else {
                for (Ejb3Column ejb3Column : columns) {
                    String keyName = "UK_" + Constraint.hashedName(ejb3Column.getTable().getName() + "_NaturalID");
                    ejb3Column.addUniqueKey(keyName, inSecondPass);
                }
            }
        }
    }

    private static boolean isGlobalGeneratorNameGlobal(MetadataBuildingContext context) {
        return context.getBootstrapContext().getJpaCompliance().isGlobalGeneratorScopeEnabled();
    }

    private static boolean isToManyAssociationWithinEmbeddableCollection(PropertyHolder propertyHolder) {
        if (propertyHolder instanceof ComponentPropertyHolder) {
            ComponentPropertyHolder componentPropertyHolder = (ComponentPropertyHolder)propertyHolder;
            return componentPropertyHolder.isWithinElementCollection();
        }
        return false;
    }

    private static void setVersionInformation(XProperty property, PropertyBinder propertyBinder) {
        propertyBinder.getSimpleValueBinder().setVersion(true);
        if (property.isAnnotationPresent(Source.class)) {
            Source source = (Source)property.getAnnotation(Source.class);
            propertyBinder.getSimpleValueBinder().setTimestampVersionType(source.value().typeName());
        }
    }

    private static void processId(PropertyHolder propertyHolder, PropertyData inferredData, SimpleValue idValue, HashMap<String, IdentifierGeneratorDefinition> classGenerators, boolean isIdentifierMapper, MetadataBuildingContext buildingContext) {
        String generatorName;
        if (isIdentifierMapper) {
            throw new AnnotationException("@IdClass class should not have @Id nor @EmbeddedId properties: " + BinderHelper.getPath(propertyHolder, inferredData));
        }
        XClass entityXClass = inferredData.getClassOrElement();
        XProperty idXProperty = inferredData.getProperty();
        boolean isComponent = entityXClass.isAnnotationPresent(Embeddable.class) || idXProperty.isAnnotationPresent(EmbeddedId.class);
        GeneratedValue generatedValue = (GeneratedValue)idXProperty.getAnnotation(GeneratedValue.class);
        String generatorType = generatedValue != null ? AnnotationBinder.generatorType(generatedValue, buildingContext, entityXClass) : "assigned";
        String string = generatorName = generatedValue != null ? generatedValue.generator() : "";
        if (isComponent) {
            generatorType = "assigned";
        }
        if (AnnotationBinder.isGlobalGeneratorNameGlobal(buildingContext)) {
            AnnotationBinder.buildGenerators((XAnnotatedElement)idXProperty, buildingContext);
            IdGeneratorResolverSecondPass secondPass = new IdGeneratorResolverSecondPass(idValue, idXProperty, generatorType, generatorName, buildingContext);
            buildingContext.getMetadataCollector().addSecondPass(secondPass);
        } else {
            HashMap localGenerators = (HashMap)classGenerators.clone();
            localGenerators.putAll(AnnotationBinder.buildGenerators((XAnnotatedElement)idXProperty, buildingContext));
            BinderHelper.makeIdGenerator(idValue, idXProperty, generatorType, generatorName, buildingContext, localGenerators);
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Bind {0} on {1}", isComponent ? "@EmbeddedId" : "@Id", inferredData.getPropertyName());
        }
    }

    public static String generatorType(final GeneratedValue generatedValueAnn, final MetadataBuildingContext buildingContext, final XClass javaTypeXClass) {
        return buildingContext.getBuildingOptions().getIdGenerationTypeInterpreter().determineGeneratorName(generatedValueAnn.strategy(), new IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext(){
            Class javaType = null;

            @Override
            public Class getIdType() {
                if (this.javaType == null) {
                    this.javaType = buildingContext.getBootstrapContext().getReflectionManager().toClass(javaTypeXClass);
                }
                return this.javaType;
            }

            @Override
            public String getGeneratedValueGeneratorName() {
                return generatedValueAnn.generator();
            }
        });
    }

    private static void bindJoinedTableAssociation(XProperty property, MetadataBuildingContext buildingContext, EntityBinder entityBinder, CollectionBinder collectionBinder, PropertyHolder propertyHolder, PropertyData inferredData, String mappedBy) {
        JoinColumn[] annInverseJoins;
        JoinColumn[] annJoins;
        TableBinder associationTableBinder = new TableBinder();
        JoinTable assocTable = propertyHolder.getJoinTable(property);
        CollectionTable collectionTable = (CollectionTable)property.getAnnotation(CollectionTable.class);
        if (assocTable != null || collectionTable != null) {
            Index[] jpaIndexes;
            JoinColumn[] inverseJoins;
            JoinColumn[] joins;
            UniqueConstraint[] uniqueConstraints;
            String tableName;
            String schema;
            String catalog;
            if (collectionTable != null) {
                catalog = collectionTable.catalog();
                schema = collectionTable.schema();
                tableName = collectionTable.name();
                uniqueConstraints = collectionTable.uniqueConstraints();
                joins = collectionTable.joinColumns();
                inverseJoins = null;
                jpaIndexes = collectionTable.indexes();
            } else {
                catalog = assocTable.catalog();
                schema = assocTable.schema();
                tableName = assocTable.name();
                uniqueConstraints = assocTable.uniqueConstraints();
                joins = assocTable.joinColumns();
                inverseJoins = assocTable.inverseJoinColumns();
                jpaIndexes = assocTable.indexes();
            }
            collectionBinder.setExplicitAssociationTable(true);
            if (jpaIndexes != null && jpaIndexes.length > 0) {
                associationTableBinder.setJpaIndex(jpaIndexes);
            }
            if (!BinderHelper.isEmptyAnnotationValue(schema)) {
                associationTableBinder.setSchema(schema);
            }
            if (!BinderHelper.isEmptyAnnotationValue(catalog)) {
                associationTableBinder.setCatalog(catalog);
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableName)) {
                associationTableBinder.setName(tableName);
            }
            associationTableBinder.setUniqueConstraints(uniqueConstraints);
            associationTableBinder.setJpaIndex(jpaIndexes);
            annJoins = joins.length == 0 ? null : joins;
            annInverseJoins = inverseJoins == null || inverseJoins.length == 0 ? null : inverseJoins;
        } else {
            annJoins = null;
            annInverseJoins = null;
        }
        Ejb3JoinColumn[] joinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(annJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappedBy, buildingContext);
        Ejb3JoinColumn[] inverseJoinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(annInverseJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappedBy, buildingContext);
        associationTableBinder.setBuildingContext(buildingContext);
        collectionBinder.setTableBinder(associationTableBinder);
        collectionBinder.setJoinColumns(joinColumns);
        collectionBinder.setInverseJoinColumns(inverseJoinColumns);
    }

    private static PropertyBinder bindComponent(PropertyData inferredData, PropertyHolder propertyHolder, AccessType propertyAccessor, EntityBinder entityBinder, boolean isIdentifierMapper, MetadataBuildingContext buildingContext, boolean isComponentEmbedded, boolean isId, Map<XClass, InheritanceState> inheritanceStatePerClass, String referencedEntityName, Ejb3JoinColumn[] columns) {
        Component comp;
        if (referencedEntityName != null) {
            comp = AnnotationBinder.createComponent(propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, buildingContext);
            CopyIdentifierComponentSecondPass sp = new CopyIdentifierComponentSecondPass(comp, referencedEntityName, columns, buildingContext);
            buildingContext.getMetadataCollector().addSecondPass(sp);
        } else {
            comp = AnnotationBinder.fillComponent(propertyHolder, inferredData, propertyAccessor, !isId, entityBinder, isComponentEmbedded, isIdentifierMapper, false, buildingContext, inheritanceStatePerClass);
        }
        if (isId) {
            comp.setKey(true);
            if (propertyHolder.getPersistentClass().getIdentifier() != null) {
                throw new AnnotationException(comp.getComponentClassName() + " must not have @Id properties when used as an @EmbeddedId: " + BinderHelper.getPath(propertyHolder, inferredData));
            }
            if (referencedEntityName == null && comp.getPropertySpan() == 0) {
                throw new AnnotationException(comp.getComponentClassName() + " has no persistent id property: " + BinderHelper.getPath(propertyHolder, inferredData));
            }
        }
        XProperty property = inferredData.getProperty();
        AnnotationBinder.setupComponentTuplizer(property, comp);
        PropertyBinder binder = new PropertyBinder();
        binder.setDeclaringClass(inferredData.getDeclaringClass());
        binder.setName(inferredData.getPropertyName());
        binder.setValue(comp);
        binder.setProperty(inferredData.getProperty());
        binder.setAccessType(inferredData.getDefaultAccess());
        binder.setEmbedded(isComponentEmbedded);
        binder.setHolder(propertyHolder);
        binder.setId(isId);
        binder.setEntityBinder(entityBinder);
        binder.setInheritanceStatePerClass(inheritanceStatePerClass);
        binder.setBuildingContext(buildingContext);
        binder.makePropertyAndBind();
        return binder;
    }

    public static Component fillComponent(PropertyHolder propertyHolder, PropertyData inferredData, AccessType propertyAccessor, boolean isNullable, EntityBinder entityBinder, boolean isComponentEmbedded, boolean isIdentifierMapper, boolean inSecondPass, MetadataBuildingContext buildingContext, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        return AnnotationBinder.fillComponent(propertyHolder, inferredData, null, propertyAccessor, isNullable, entityBinder, isComponentEmbedded, isIdentifierMapper, inSecondPass, buildingContext, inheritanceStatePerClass);
    }

    public static Component fillComponent(PropertyHolder propertyHolder, PropertyData inferredData, PropertyData baseInferredData, AccessType propertyAccessor, boolean isNullable, EntityBinder entityBinder, boolean isComponentEmbedded, boolean isIdentifierMapper, boolean inSecondPass, MetadataBuildingContext buildingContext, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        PropertyContainer propContainer;
        Component comp = AnnotationBinder.createComponent(propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, buildingContext);
        String subpath = BinderHelper.getPath(propertyHolder, inferredData);
        LOG.tracev("Binding component with path: {0}", subpath);
        PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder(comp, subpath, inferredData, propertyHolder, buildingContext);
        propertyHolder.startingProperty(inferredData.getProperty());
        XClass xClassProcessed = inferredData.getPropertyClass();
        ArrayList<PropertyData> classElements = new ArrayList<PropertyData>();
        XClass returnedClassOrElement = inferredData.getClassOrElement();
        ArrayList<PropertyData> baseClassElements = null;
        HashMap<String, PropertyData> orderedBaseClassElements = new HashMap<String, PropertyData>();
        if (baseInferredData != null) {
            baseClassElements = new ArrayList<PropertyData>();
            XClass baseReturnedClassOrElement = baseInferredData.getClassOrElement();
            AnnotationBinder.bindTypeDefs((XAnnotatedElement)baseReturnedClassOrElement, buildingContext);
            while (!Object.class.getName().equals(baseReturnedClassOrElement.getName())) {
                propContainer = new PropertyContainer(baseReturnedClassOrElement, xClassProcessed, propertyAccessor);
                AnnotationBinder.addElementsOfClass(baseClassElements, propContainer, buildingContext);
                for (PropertyData element : baseClassElements) {
                    orderedBaseClassElements.put(element.getPropertyName(), element);
                }
                baseReturnedClassOrElement = baseReturnedClassOrElement.getSuperclass();
            }
        }
        AnnotationBinder.bindTypeDefs((XAnnotatedElement)returnedClassOrElement, buildingContext);
        propContainer = new PropertyContainer(returnedClassOrElement, xClassProcessed, propertyAccessor);
        AnnotationBinder.addElementsOfClass(classElements, propContainer, buildingContext);
        for (XClass superClass = xClassProcessed.getSuperclass(); superClass != null && superClass.isAnnotationPresent(MappedSuperclass.class); superClass = superClass.getSuperclass()) {
            propContainer = new PropertyContainer(superClass, xClassProcessed, propertyAccessor);
            AnnotationBinder.addElementsOfClass(classElements, propContainer, buildingContext);
        }
        if (baseClassElements != null && !AnnotationBinder.hasAnnotationsOnIdClass(xClassProcessed)) {
            for (int i = 0; i < classElements.size(); ++i) {
                PropertyData idClassPropertyData = (PropertyData)classElements.get(i);
                PropertyData entityPropertyData = (PropertyData)orderedBaseClassElements.get(idClassPropertyData.getPropertyName());
                if (propertyHolder.isInIdClass()) {
                    boolean isOfDifferentType;
                    if (entityPropertyData == null) {
                        throw new AnnotationException("Property of @IdClass not found in entity " + baseInferredData.getPropertyClass().getName() + ": " + idClassPropertyData.getPropertyName());
                    }
                    boolean hasXToOneAnnotation = entityPropertyData.getProperty().isAnnotationPresent(ManyToOne.class) || entityPropertyData.getProperty().isAnnotationPresent(OneToOne.class);
                    boolean bl = isOfDifferentType = !entityPropertyData.getClassOrElement().equals(idClassPropertyData.getClassOrElement());
                    if (hasXToOneAnnotation && isOfDifferentType) continue;
                    classElements.set(i, entityPropertyData);
                    continue;
                }
                classElements.set(i, entityPropertyData);
            }
        }
        for (PropertyData propertyAnnotatedElement : classElements) {
            String generator;
            AnnotationBinder.processElementAnnotations(subHolder, isNullable ? Nullability.NO_CONSTRAINT : Nullability.FORCED_NOT_NULL, propertyAnnotatedElement, new HashMap<String, IdentifierGeneratorDefinition>(), entityBinder, isIdentifierMapper, isComponentEmbedded, inSecondPass, buildingContext, inheritanceStatePerClass);
            XProperty property = propertyAnnotatedElement.getProperty();
            if (!property.isAnnotationPresent(GeneratedValue.class) || !property.isAnnotationPresent(Id.class)) continue;
            GeneratedValue generatedValue = (GeneratedValue)property.getAnnotation(GeneratedValue.class);
            String generatorType = generatedValue != null ? AnnotationBinder.generatorType(generatedValue, buildingContext, property.getType()) : "assigned";
            String string = generator = generatedValue != null ? generatedValue.generator() : "";
            if (AnnotationBinder.isGlobalGeneratorNameGlobal(buildingContext)) {
                AnnotationBinder.buildGenerators((XAnnotatedElement)property, buildingContext);
                IdGeneratorResolverSecondPass secondPass = new IdGeneratorResolverSecondPass((SimpleValue)comp.getProperty(property.getName()).getValue(), property, generatorType, generator, buildingContext);
                buildingContext.getMetadataCollector().addSecondPass(secondPass);
                continue;
            }
            HashMap<String, IdentifierGeneratorDefinition> localGenerators = new HashMap<String, IdentifierGeneratorDefinition>(AnnotationBinder.buildGenerators((XAnnotatedElement)property, buildingContext));
            BinderHelper.makeIdGenerator((SimpleValue)comp.getProperty(property.getName()).getValue(), property, generatorType, generator, buildingContext, localGenerators);
        }
        return comp;
    }

    public static Component createComponent(PropertyHolder propertyHolder, PropertyData inferredData, boolean isComponentEmbedded, boolean isIdentifierMapper, MetadataBuildingContext context) {
        Component comp = new Component(context, propertyHolder.getPersistentClass());
        comp.setEmbedded(isComponentEmbedded);
        comp.setTable(propertyHolder.getTable());
        if (isIdentifierMapper || isComponentEmbedded && inferredData.getPropertyName() == null) {
            comp.setComponentClassName(comp.getOwner().getClassName());
        } else {
            comp.setComponentClassName(inferredData.getClassOrElementName());
        }
        return comp;
    }

    private static void bindIdClass(String generatorType, String generatorName, PropertyData inferredData, PropertyData baseInferredData, Ejb3Column[] columns, PropertyHolder propertyHolder, boolean isComposite, AccessType propertyAccessor, EntityBinder entityBinder, boolean isEmbedded, boolean isIdentifierMapper, MetadataBuildingContext buildingContext, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        SimpleValue id;
        PersistentClass persistentClass = propertyHolder.getPersistentClass();
        if (!(persistentClass instanceof RootClass)) {
            throw new AnnotationException("Unable to define/override @Id(s) on a subclass: " + propertyHolder.getEntityName());
        }
        RootClass rootClass = (RootClass)persistentClass;
        String persistentClassName = rootClass.getClassName();
        String propertyName = inferredData.getPropertyName();
        if (isComposite) {
            id = AnnotationBinder.fillComponent(propertyHolder, inferredData, baseInferredData, propertyAccessor, false, entityBinder, isEmbedded, isIdentifierMapper, false, buildingContext, inheritanceStatePerClass);
            Component componentId = id;
            componentId.setKey(true);
            if (rootClass.getIdentifier() != null) {
                throw new AnnotationException(componentId.getComponentClassName() + " must not have @Id properties when used as an @EmbeddedId");
            }
            if (componentId.getPropertySpan() == 0) {
                throw new AnnotationException(componentId.getComponentClassName() + " has no persistent id property");
            }
            XProperty property = inferredData.getProperty();
            AnnotationBinder.setupComponentTuplizer(property, componentId);
        } else {
            for (Ejb3Column column : columns) {
                column.forceNotNull();
            }
            SimpleValueBinder value = new SimpleValueBinder();
            value.setPropertyName(propertyName);
            value.setReturnedClassName(inferredData.getTypeName());
            value.setColumns(columns);
            value.setPersistentClassName(persistentClassName);
            value.setBuildingContext(buildingContext);
            value.setType(inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName, null);
            value.setAccessType(propertyAccessor);
            id = value.make();
        }
        rootClass.setIdentifier(id);
        if (AnnotationBinder.isGlobalGeneratorNameGlobal(buildingContext)) {
            IdGeneratorResolverSecondPass secondPass = new IdGeneratorResolverSecondPass(id, inferredData.getProperty(), generatorType, generatorName, buildingContext);
            buildingContext.getMetadataCollector().addSecondPass(secondPass);
        } else {
            BinderHelper.makeIdGenerator(id, inferredData.getProperty(), generatorType, generatorName, buildingContext, Collections.emptyMap());
        }
        if (isEmbedded) {
            rootClass.setEmbeddedIdentifier(inferredData.getPropertyClass() == null);
        } else {
            PropertyBinder binder = new PropertyBinder();
            binder.setName(propertyName);
            binder.setValue(id);
            binder.setAccessType(inferredData.getDefaultAccess());
            binder.setProperty(inferredData.getProperty());
            Property prop = binder.makeProperty();
            rootClass.setIdentifierProperty(prop);
            org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(inferredData.getDeclaringClass(), inheritanceStatePerClass, buildingContext);
            if (superclass != null) {
                superclass.setDeclaredIdentifierProperty(prop);
            } else {
                rootClass.setDeclaredIdentifierProperty(prop);
            }
        }
    }

    private static PropertyData getUniqueIdPropertyFromBaseClass(PropertyData inferredData, PropertyData baseInferredData, AccessType propertyAccessor, MetadataBuildingContext context) {
        ArrayList<PropertyData> baseClassElements = new ArrayList<PropertyData>();
        XClass baseReturnedClassOrElement = baseInferredData.getClassOrElement();
        PropertyContainer propContainer = new PropertyContainer(baseReturnedClassOrElement, inferredData.getPropertyClass(), propertyAccessor);
        AnnotationBinder.addElementsOfClass(baseClassElements, propContainer, context);
        return (PropertyData)baseClassElements.get(0);
    }

    private static void setupComponentTuplizer(XProperty property, Component component) {
        if (property == null) {
            return;
        }
        if (property.isAnnotationPresent(Tuplizers.class)) {
            for (Tuplizer tuplizer : ((Tuplizers)property.getAnnotation(Tuplizers.class)).value()) {
                EntityMode mode = EntityMode.parse(tuplizer.entityMode());
                component.addTuplizer(mode, tuplizer.impl().getName());
            }
        }
        if (property.isAnnotationPresent(Tuplizer.class)) {
            Tuplizer tuplizer = (Tuplizer)property.getAnnotation(Tuplizer.class);
            EntityMode mode = EntityMode.parse(tuplizer.entityMode());
            component.addTuplizer(mode, tuplizer.impl().getName());
        }
    }

    private static void bindManyToOne(String cascadeStrategy, Ejb3JoinColumn[] columns, boolean optional, NotFoundAction notFoundAction, boolean cascadeOnDelete, XClass targetEntity, PropertyHolder propertyHolder, PropertyData inferredData, boolean unique, boolean isIdentifierMapper, boolean inSecondPass, PropertyBinder propertyBinder, MetadataBuildingContext context) {
        org.hibernate.mapping.ManyToOne value = new org.hibernate.mapping.ManyToOne(context, columns[0].getTable());
        if (unique) {
            value.markAsLogicalOneToOne();
        }
        value.setReferencedEntityName(ToOneBinder.getReferenceEntityName(inferredData, targetEntity, context));
        XProperty property = inferredData.getProperty();
        AnnotationBinder.defineFetchingStrategy(value, property);
        value.setNotFoundAction(notFoundAction);
        value.setCascadeDeleteEnabled(cascadeOnDelete);
        if (!optional) {
            for (Ejb3JoinColumn column : columns) {
                column.setNullable(false);
            }
        }
        if (property.isAnnotationPresent(MapsId.class)) {
            for (Ejb3JoinColumn column : columns) {
                column.setInsertable(false);
                column.setUpdatable(false);
            }
        }
        JoinColumn joinColumn = (JoinColumn)property.getAnnotation(JoinColumn.class);
        JoinColumns joinColumns = (JoinColumns)property.getAnnotation(JoinColumns.class);
        boolean hasSpecjManyToOne = false;
        if (context.getBuildingOptions().isSpecjProprietarySyntaxEnabled()) {
            String columnName = "";
            for (XProperty prop : inferredData.getDeclaringClass().getDeclaredProperties(AccessType.FIELD.getType())) {
                if (prop.isAnnotationPresent(Id.class) && prop.isAnnotationPresent(javax.persistence.Column.class)) {
                    columnName = ((javax.persistence.Column)prop.getAnnotation(javax.persistence.Column.class)).name();
                }
                if (!property.isAnnotationPresent(ManyToOne.class) || joinColumn == null || BinderHelper.isEmptyAnnotationValue(joinColumn.name()) || !joinColumn.name().equals(columnName) || property.isAnnotationPresent(MapsId.class)) continue;
                hasSpecjManyToOne = true;
                for (Ejb3JoinColumn column : columns) {
                    column.setInsertable(false);
                    column.setUpdatable(false);
                }
            }
        }
        value.setTypeName(inferredData.getClassOrElementName());
        String propertyName = inferredData.getPropertyName();
        value.setTypeUsingReflection(propertyHolder.getClassName(), propertyName);
        AnnotationBinder.bindForeignKeyNameAndDefinition(value, property, propertyHolder.getOverriddenForeignKey(StringHelper.qualify(propertyHolder.getPath(), propertyName)), joinColumn, joinColumns, context);
        String path = propertyHolder.getPath() + "." + propertyName;
        ToOneFkSecondPass secondPass = new ToOneFkSecondPass(value, columns, !optional && unique, propertyHolder.getEntityOwnerClassName(), path, context);
        if (inSecondPass) {
            secondPass.doSecondPass(context.getMetadataCollector().getEntityBindingMap());
        } else {
            context.getMetadataCollector().addSecondPass(secondPass);
        }
        Ejb3Column.checkPropertyConsistency(columns, propertyHolder.getEntityName() + "." + propertyName);
        propertyBinder.setName(propertyName);
        propertyBinder.setValue(value);
        if (isIdentifierMapper) {
            propertyBinder.setInsertable(false);
            propertyBinder.setUpdatable(false);
        } else if (hasSpecjManyToOne) {
            propertyBinder.setInsertable(false);
            propertyBinder.setUpdatable(false);
        } else {
            propertyBinder.setInsertable(columns[0].isInsertable());
            propertyBinder.setUpdatable(columns[0].isUpdatable());
        }
        propertyBinder.setColumns(columns);
        propertyBinder.setAccessType(inferredData.getDefaultAccess());
        propertyBinder.setCascade(cascadeStrategy);
        propertyBinder.setProperty(property);
        propertyBinder.setXToMany(true);
        Property boundProperty = propertyBinder.makePropertyAndBind();
        boundProperty.setOptional(optional && AnnotationBinder.isNullable(joinColumns, joinColumn));
    }

    private static boolean isNullable(JoinColumns joinColumns, JoinColumn joinColumn) {
        if (joinColumn != null) {
            return joinColumn.nullable();
        }
        if (joinColumns != null) {
            JoinColumn[] col = joinColumns.value();
            for (int i = 0; i < col.length; ++i) {
                if (!joinColumns.value()[i].nullable()) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    protected static void defineFetchingStrategy(ToOne toOne, XProperty property) {
        FetchType fetchType;
        LazyToOne lazy = (LazyToOne)property.getAnnotation(LazyToOne.class);
        Fetch fetch = (Fetch)property.getAnnotation(Fetch.class);
        ManyToOne manyToOne = (ManyToOne)property.getAnnotation(ManyToOne.class);
        OneToOne oneToOne = (OneToOne)property.getAnnotation(OneToOne.class);
        NotFound notFound = (NotFound)property.getAnnotation(NotFound.class);
        if (manyToOne != null) {
            fetchType = manyToOne.fetch();
        } else {
            if (oneToOne == null) throw new AssertionFailure("Define fetch strategy on a property not annotated with @OneToMany nor @OneToOne");
            fetchType = oneToOne.fetch();
        }
        if (notFound != null) {
            toOne.setLazy(false);
            toOne.setUnwrapProxy(true);
        } else if (lazy != null) {
            toOne.setLazy(lazy.value() != LazyToOneOption.FALSE);
            toOne.setUnwrapProxy(lazy.value() == LazyToOneOption.NO_PROXY);
        } else {
            toOne.setLazy(fetchType == FetchType.LAZY);
            toOne.setUnwrapProxy(fetchType != FetchType.LAZY);
            toOne.setUnwrapProxyImplicit(true);
        }
        if (fetch == null) {
            toOne.setFetchMode(AnnotationBinder.getFetchMode(fetchType));
            return;
        }
        if (fetch.value() == org.hibernate.annotations.FetchMode.JOIN) {
            toOne.setFetchMode(FetchMode.JOIN);
            toOne.setLazy(false);
            toOne.setUnwrapProxy(false);
            return;
        }
        if (fetch.value() == org.hibernate.annotations.FetchMode.SELECT) {
            toOne.setFetchMode(FetchMode.SELECT);
            return;
        }
        if (fetch.value() != org.hibernate.annotations.FetchMode.SUBSELECT) throw new AssertionFailure("Unknown FetchMode: " + (Object)((Object)fetch.value()));
        throw new AnnotationException("Use of FetchMode.SUBSELECT not allowed on ToOne associations");
    }

    private static void bindOneToOne(String cascadeStrategy, Ejb3JoinColumn[] joinColumns, boolean optional, FetchMode fetchMode, NotFoundAction notFoundAction, boolean cascadeOnDelete, XClass targetEntity, PropertyHolder propertyHolder, PropertyData inferredData, String mappedBy, boolean trueOneToOne, boolean isIdentifierMapper, boolean inSecondPass, PropertyBinder propertyBinder, MetadataBuildingContext context) {
        String propertyName = inferredData.getPropertyName();
        LOG.tracev("Fetching {0} with {1}", propertyName, (Object)fetchMode);
        boolean mapToPK = true;
        if (!trueOneToOne) {
            KeyValue identifier = propertyHolder.getIdentifier();
            if (identifier == null) {
                mapToPK = false;
            } else {
                Iterator<Selectable> idColumns = identifier.getColumnIterator();
                ArrayList<String> idColumnNames = new ArrayList<String>();
                if (identifier.getColumnSpan() != joinColumns.length) {
                    mapToPK = false;
                } else {
                    while (idColumns.hasNext()) {
                        Column currentColumn = (Column)idColumns.next();
                        idColumnNames.add(currentColumn.getName());
                    }
                    for (Ejb3JoinColumn col : joinColumns) {
                        if (idColumnNames.contains(col.getMappingColumn().getName())) continue;
                        mapToPK = false;
                        break;
                    }
                }
            }
        }
        if (trueOneToOne || mapToPK || !BinderHelper.isEmptyAnnotationValue(mappedBy)) {
            OneToOneSecondPass secondPass = new OneToOneSecondPass(mappedBy, propertyHolder.getEntityName(), propertyName, propertyHolder, inferredData, targetEntity, notFoundAction, cascadeOnDelete, optional, cascadeStrategy, joinColumns, context);
            if (inSecondPass) {
                secondPass.doSecondPass(context.getMetadataCollector().getEntityBindingMap());
            } else {
                context.getMetadataCollector().addSecondPass(secondPass, BinderHelper.isEmptyAnnotationValue(mappedBy));
            }
        } else {
            AnnotationBinder.bindManyToOne(cascadeStrategy, joinColumns, optional, notFoundAction, cascadeOnDelete, targetEntity, propertyHolder, inferredData, true, isIdentifierMapper, inSecondPass, propertyBinder, context);
        }
    }

    private static void bindAny(String cascadeStrategy, Ejb3JoinColumn[] columns, boolean cascadeOnDelete, Nullability nullability, PropertyHolder propertyHolder, PropertyData inferredData, EntityBinder entityBinder, boolean isIdentifierMapper, MetadataBuildingContext buildingContext) {
        Any anyAnn = (Any)inferredData.getProperty().getAnnotation(Any.class);
        if (anyAnn == null) {
            throw new AssertionFailure("Missing @Any annotation: " + BinderHelper.getPath(propertyHolder, inferredData));
        }
        boolean lazy = anyAnn.fetch() == FetchType.LAZY;
        org.hibernate.mapping.Any value = BinderHelper.buildAnyValue(anyAnn.metaDef(), columns, anyAnn.metaColumn(), inferredData, cascadeOnDelete, lazy, nullability, propertyHolder, entityBinder, anyAnn.optional(), buildingContext);
        PropertyBinder binder = new PropertyBinder();
        binder.setName(inferredData.getPropertyName());
        binder.setValue(value);
        binder.setLazy(lazy);
        if (isIdentifierMapper) {
            binder.setInsertable(false);
            binder.setUpdatable(false);
        } else {
            binder.setInsertable(columns[0].isInsertable());
            binder.setUpdatable(columns[0].isUpdatable());
        }
        binder.setAccessType(inferredData.getDefaultAccess());
        binder.setCascade(cascadeStrategy);
        Property prop = binder.makeProperty();
        propertyHolder.addProperty(prop, columns, inferredData.getDeclaringClass());
    }

    private static EnumSet<CascadeType> convertToHibernateCascadeType(javax.persistence.CascadeType[] ejbCascades) {
        EnumSet<CascadeType> hibernateCascadeSet = EnumSet.noneOf(CascadeType.class);
        if (ejbCascades != null && ejbCascades.length > 0) {
            block8: for (javax.persistence.CascadeType cascade : ejbCascades) {
                switch (cascade) {
                    case ALL: {
                        hibernateCascadeSet.add(CascadeType.ALL);
                        continue block8;
                    }
                    case PERSIST: {
                        hibernateCascadeSet.add(CascadeType.PERSIST);
                        continue block8;
                    }
                    case MERGE: {
                        hibernateCascadeSet.add(CascadeType.MERGE);
                        continue block8;
                    }
                    case REMOVE: {
                        hibernateCascadeSet.add(CascadeType.REMOVE);
                        continue block8;
                    }
                    case REFRESH: {
                        hibernateCascadeSet.add(CascadeType.REFRESH);
                        continue block8;
                    }
                    case DETACH: {
                        hibernateCascadeSet.add(CascadeType.DETACH);
                    }
                }
            }
        }
        return hibernateCascadeSet;
    }

    private static String getCascadeStrategy(javax.persistence.CascadeType[] ejbCascades, Cascade hibernateCascadeAnnotation, boolean orphanRemoval, boolean forcePersist) {
        CascadeType[] hibernateCascades;
        EnumSet<CascadeType> hibernateCascadeSet = AnnotationBinder.convertToHibernateCascadeType(ejbCascades);
        CascadeType[] cascadeTypeArray = hibernateCascades = hibernateCascadeAnnotation == null ? null : hibernateCascadeAnnotation.value();
        if (hibernateCascades != null && hibernateCascades.length > 0) {
            hibernateCascadeSet.addAll(Arrays.asList(hibernateCascades));
        }
        if (orphanRemoval) {
            hibernateCascadeSet.add(CascadeType.DELETE_ORPHAN);
            hibernateCascadeSet.add(CascadeType.REMOVE);
        }
        if (forcePersist) {
            hibernateCascadeSet.add(CascadeType.PERSIST);
        }
        StringBuilder cascade = new StringBuilder();
        for (CascadeType aHibernateCascadeSet : hibernateCascadeSet) {
            switch (aHibernateCascadeSet) {
                case ALL: {
                    cascade.append(",").append("all");
                    break;
                }
                case SAVE_UPDATE: {
                    cascade.append(",").append("save-update");
                    break;
                }
                case PERSIST: {
                    cascade.append(",").append("persist");
                    break;
                }
                case MERGE: {
                    cascade.append(",").append("merge");
                    break;
                }
                case LOCK: {
                    cascade.append(",").append("lock");
                    break;
                }
                case REFRESH: {
                    cascade.append(",").append("refresh");
                    break;
                }
                case REPLICATE: {
                    cascade.append(",").append("replicate");
                    break;
                }
                case EVICT: 
                case DETACH: {
                    cascade.append(",").append("evict");
                    break;
                }
                case DELETE: {
                    cascade.append(",").append("delete");
                    break;
                }
                case DELETE_ORPHAN: {
                    cascade.append(",").append("delete-orphan");
                    break;
                }
                case REMOVE: {
                    cascade.append(",").append("delete");
                }
            }
        }
        return cascade.length() > 0 ? cascade.substring(1) : "none";
    }

    public static FetchMode getFetchMode(FetchType fetch) {
        if (fetch == FetchType.EAGER) {
            return FetchMode.JOIN;
        }
        return FetchMode.SELECT;
    }

    public static void bindForeignKeyNameAndDefinition(SimpleValue value, XProperty property, ForeignKey fkOverride, JoinColumn joinColumn, JoinColumns joinColumns, MetadataBuildingContext context) {
        boolean noConstraintByDefault = context.getBuildingOptions().isNoConstraintByDefault();
        NotFound notFoundAnn = (NotFound)property.getAnnotation(NotFound.class);
        if (notFoundAnn != null) {
            value.setForeignKeyName("none");
        } else if (joinColumn != null && (joinColumn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || joinColumn.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault)) {
            value.setForeignKeyName("none");
        } else if (joinColumns != null && (joinColumns.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || joinColumns.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault)) {
            value.setForeignKeyName("none");
        } else {
            org.hibernate.annotations.ForeignKey fk = (org.hibernate.annotations.ForeignKey)property.getAnnotation(org.hibernate.annotations.ForeignKey.class);
            if (fk != null && StringHelper.isNotEmpty(fk.name())) {
                value.setForeignKeyName(fk.name());
            } else if (fkOverride != null && (fkOverride.value() == ConstraintMode.NO_CONSTRAINT || fkOverride.value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault)) {
                value.setForeignKeyName("none");
            } else if (fkOverride != null) {
                value.setForeignKeyName(StringHelper.nullIfEmpty(fkOverride.name()));
                value.setForeignKeyDefinition(StringHelper.nullIfEmpty(fkOverride.foreignKeyDefinition()));
            } else if (joinColumns != null) {
                value.setForeignKeyName(StringHelper.nullIfEmpty(joinColumns.foreignKey().name()));
                value.setForeignKeyDefinition(StringHelper.nullIfEmpty(joinColumns.foreignKey().foreignKeyDefinition()));
            } else if (joinColumn != null) {
                value.setForeignKeyName(StringHelper.nullIfEmpty(joinColumn.foreignKey().name()));
                value.setForeignKeyDefinition(StringHelper.nullIfEmpty(joinColumn.foreignKey().foreignKeyDefinition()));
            }
        }
    }

    private static HashMap<String, IdentifierGeneratorDefinition> buildGenerators(XAnnotatedElement annElt, MetadataBuildingContext context) {
        IdentifierGeneratorDefinition idGen;
        SequenceGenerators sequenceGenerators;
        InFlightMetadataCollector metadataCollector = context.getMetadataCollector();
        HashMap<String, IdentifierGeneratorDefinition> generators = new HashMap<String, IdentifierGeneratorDefinition>();
        TableGenerators tableGenerators = (TableGenerators)annElt.getAnnotation(TableGenerators.class);
        if (tableGenerators != null) {
            for (TableGenerator tableGenerator : tableGenerators.value()) {
                IdentifierGeneratorDefinition idGenerator = AnnotationBinder.buildIdGenerator((Annotation)tableGenerator, context);
                generators.put(idGenerator.getName(), idGenerator);
                metadataCollector.addIdentifierGenerator(idGenerator);
            }
        }
        if ((sequenceGenerators = (SequenceGenerators)annElt.getAnnotation(SequenceGenerators.class)) != null) {
            for (SequenceGenerator sequenceGenerator : sequenceGenerators.value()) {
                IdentifierGeneratorDefinition idGenerator = AnnotationBinder.buildIdGenerator((Annotation)sequenceGenerator, context);
                generators.put(idGenerator.getName(), idGenerator);
                metadataCollector.addIdentifierGenerator(idGenerator);
            }
        }
        TableGenerator tabGen = (TableGenerator)annElt.getAnnotation(TableGenerator.class);
        SequenceGenerator seqGen = (SequenceGenerator)annElt.getAnnotation(SequenceGenerator.class);
        GenericGenerator genGen = (GenericGenerator)annElt.getAnnotation(GenericGenerator.class);
        if (tabGen != null) {
            idGen = AnnotationBinder.buildIdGenerator((Annotation)tabGen, context);
            generators.put(idGen.getName(), idGen);
            metadataCollector.addIdentifierGenerator(idGen);
        }
        if (seqGen != null) {
            idGen = AnnotationBinder.buildIdGenerator((Annotation)seqGen, context);
            generators.put(idGen.getName(), idGen);
            metadataCollector.addIdentifierGenerator(idGen);
        }
        if (genGen != null) {
            idGen = AnnotationBinder.buildIdGenerator(genGen, context);
            generators.put(idGen.getName(), idGen);
            metadataCollector.addIdentifierGenerator(idGen);
        }
        return generators;
    }

    public static boolean isDefault(XClass clazz, MetadataBuildingContext context) {
        return context.getBootstrapContext().getReflectionManager().equals(clazz, Void.TYPE);
    }

    public static Map<XClass, InheritanceState> buildInheritanceStates(List<XClass> orderedClasses, MetadataBuildingContext buildingContext) {
        HashMap<XClass, InheritanceState> inheritanceStatePerClass = new HashMap<XClass, InheritanceState>(orderedClasses.size());
        for (XClass clazz : orderedClasses) {
            InheritanceState superclassState = InheritanceState.getSuperclassInheritanceState(clazz, inheritanceStatePerClass);
            InheritanceState state = new InheritanceState(clazz, inheritanceStatePerClass, buildingContext);
            if (superclassState != null) {
                boolean nonDefault;
                superclassState.setHasSiblings(true);
                InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(clazz, inheritanceStatePerClass);
                state.setHasParents(superEntityState != null);
                boolean bl = nonDefault = state.getType() != null && !InheritanceType.SINGLE_TABLE.equals((Object)state.getType());
                if (superclassState.getType() != null) {
                    boolean mixingStrategy;
                    boolean bl2 = mixingStrategy = state.getType() != null && !state.getType().equals((Object)superclassState.getType());
                    if (nonDefault && mixingStrategy) {
                        LOG.invalidSubStrategy(clazz.getName());
                    }
                    state.setType(superclassState.getType());
                }
            }
            inheritanceStatePerClass.put(clazz, state);
        }
        return inheritanceStatePerClass;
    }

    private static boolean hasAnnotationsOnIdClass(XClass idClass) {
        List properties = idClass.getDeclaredProperties("field");
        for (XProperty property : properties) {
            if (!property.isAnnotationPresent(javax.persistence.Column.class) && !property.isAnnotationPresent(OneToMany.class) && !property.isAnnotationPresent(ManyToOne.class) && !property.isAnnotationPresent(Id.class) && !property.isAnnotationPresent(GeneratedValue.class) && !property.isAnnotationPresent(OneToOne.class) && !property.isAnnotationPresent(ManyToMany.class)) continue;
            return true;
        }
        List methods = idClass.getDeclaredMethods();
        for (XMethod method : methods) {
            if (!method.isAnnotationPresent(javax.persistence.Column.class) && !method.isAnnotationPresent(OneToMany.class) && !method.isAnnotationPresent(ManyToOne.class) && !method.isAnnotationPresent(Id.class) && !method.isAnnotationPresent(GeneratedValue.class) && !method.isAnnotationPresent(OneToOne.class) && !method.isAnnotationPresent(ManyToMany.class)) continue;
            return true;
        }
        return false;
    }

    private static void checkFetchModeAgainstNotFound(String entity, String association, boolean hasNotFound, FetchType fetchType) {
        if (hasNotFound && fetchType == FetchType.LAZY) {
            LOG.ignoreNotFoundWithFetchTypeLazy(entity, association);
        }
    }
}

