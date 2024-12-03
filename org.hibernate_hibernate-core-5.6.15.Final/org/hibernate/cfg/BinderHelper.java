/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.GeneratedValue
 *  javax.persistence.GenerationType
 *  javax.persistence.Index
 *  javax.persistence.SequenceGenerator
 *  javax.persistence.TableGenerator
 *  javax.persistence.UniqueConstraint
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XPackage
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.AnyMetaDefs;
import org.hibernate.annotations.MetaValue;
import org.hibernate.annotations.SqlFragmentAlias;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XPackage;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.id.MultipleHiLoPerTableGenerator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SyntheticProperty;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.type.DiscriminatorType;
import org.jboss.logging.Logger;

public class BinderHelper {
    public static final String ANNOTATION_STRING_DEFAULT = "";
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(BinderHelper.class);
    private static final Logger log = CoreLogging.logger(BinderHelper.class);
    public static final Set<String> PRIMITIVE_NAMES;

    private BinderHelper() {
    }

    public static Property shallowCopy(Property property) {
        Property clone = new Property();
        clone.setCascade(property.getCascade());
        clone.setInsertable(property.isInsertable());
        clone.setLazy(property.isLazy());
        clone.setName(property.getName());
        clone.setNaturalIdentifier(property.isNaturalIdentifier());
        clone.setOptimisticLocked(property.isOptimisticLocked());
        clone.setOptional(property.isOptional());
        clone.setPersistentClass(property.getPersistentClass());
        clone.setPropertyAccessorName(property.getPropertyAccessorName());
        clone.setSelectable(property.isSelectable());
        clone.setUpdateable(property.isUpdateable());
        clone.setValue(property.getValue());
        return clone;
    }

    public static void createSyntheticPropertyReference(Ejb3JoinColumn[] columns, PersistentClass ownerEntity, PersistentClass associatedEntity, Value value, boolean inverse, MetadataBuildingContext context) {
        PersistentClass associatedClass;
        if (columns[0].isImplicit() || StringHelper.isNotEmpty(columns[0].getMappedBy())) {
            return;
        }
        int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType(columns, ownerEntity, context);
        PersistentClass persistentClass = associatedClass = columns[0].getPropertyHolder() != null ? columns[0].getPropertyHolder().getPersistentClass() : null;
        if (2 == fkEnum) {
            Component embeddedComp;
            StringBuilder propertyNameBuffer = new StringBuilder("_");
            propertyNameBuffer.append(associatedClass.getEntityName().replace('.', '_'));
            propertyNameBuffer.append("_").append(columns[0].getPropertyName().replace('.', '_'));
            String syntheticPropertyName = propertyNameBuffer.toString();
            Object columnOwner = BinderHelper.findColumnOwner(ownerEntity, columns[0].getReferencedColumn(), context);
            List<Property> properties = BinderHelper.findPropertiesByColumns(columnOwner, columns, context);
            if (properties != null) {
                embeddedComp = columnOwner instanceof PersistentClass ? new Component(context, (PersistentClass)columnOwner) : new Component(context, (Join)columnOwner);
                embeddedComp.setEmbedded(true);
                embeddedComp.setComponentClassName(embeddedComp.getOwner().getClassName());
                for (Property property : properties) {
                    Property clone = BinderHelper.shallowCopy(property);
                    clone.setInsertable(false);
                    clone.setUpdateable(false);
                    clone.setNaturalIdentifier(false);
                    clone.setValueGenerationStrategy(property.getValueGenerationStrategy());
                    embeddedComp.addProperty(clone);
                }
            } else {
                StringBuilder columnsList = new StringBuilder();
                columnsList.append("referencedColumnNames(");
                for (Ejb3JoinColumn column : columns) {
                    columnsList.append(column.getReferencedColumn()).append(", ");
                }
                columnsList.setLength(columnsList.length() - 2);
                columnsList.append(") ");
                if (associatedEntity != null) {
                    columnsList.append("of ").append(associatedEntity.getEntityName()).append(".").append(columns[0].getPropertyName()).append(" ");
                } else if (columns[0].getPropertyHolder() != null) {
                    columnsList.append("of ").append(columns[0].getPropertyHolder().getEntityName()).append(".").append(columns[0].getPropertyName()).append(" ");
                }
                columnsList.append("referencing ").append(ownerEntity.getEntityName()).append(" not mapped to a single property");
                throw new AnnotationException(columnsList.toString());
            }
            SyntheticProperty synthProp = new SyntheticProperty();
            synthProp.setName(syntheticPropertyName);
            synthProp.setPersistentClass(ownerEntity);
            synthProp.setUpdateable(false);
            synthProp.setInsertable(false);
            synthProp.setValue(embeddedComp);
            synthProp.setPropertyAccessorName("embedded");
            ownerEntity.addProperty(synthProp);
            TableBinder.createUniqueConstraint(embeddedComp);
            if (value instanceof ToOne) {
                ((ToOne)value).setReferencedPropertyName(syntheticPropertyName);
                ((ToOne)value).setReferenceToPrimaryKey(syntheticPropertyName == null);
                context.getMetadataCollector().addUniquePropertyReference(ownerEntity.getEntityName(), syntheticPropertyName);
            } else if (value instanceof Collection) {
                ((Collection)value).setReferencedPropertyName(syntheticPropertyName);
                context.getMetadataCollector().addPropertyReference(ownerEntity.getEntityName(), syntheticPropertyName);
            } else {
                throw new AssertionFailure("Do a property ref on an unexpected Value type: " + value.getClass().getName());
            }
            context.getMetadataCollector().addPropertyReferencedAssociation((inverse ? "inverse__" : ANNOTATION_STRING_DEFAULT) + associatedClass.getEntityName(), columns[0].getPropertyName(), syntheticPropertyName);
        }
    }

    private static List<Property> findPropertiesByColumns(Object columnOwner, Ejb3JoinColumn[] columns, MetadataBuildingContext context) {
        Iterator it;
        Table referencedTable;
        HashMap<Column, Set<Property>> columnsToProperty = new HashMap<Column, Set<Property>>();
        ArrayList<Column> orderedColumns = new ArrayList<Column>(columns.length);
        if (columnOwner instanceof PersistentClass) {
            referencedTable = ((PersistentClass)columnOwner).getTable();
        } else if (columnOwner instanceof Join) {
            referencedTable = ((Join)columnOwner).getTable();
        } else {
            throw new AssertionFailure(columnOwner == null ? "columnOwner is null" : "columnOwner neither PersistentClass nor Join: " + columnOwner.getClass());
        }
        for (Ejb3JoinColumn column1 : columns) {
            Column column = new Column(context.getMetadataCollector().getPhysicalColumnName(referencedTable, column1.getReferencedColumn()));
            orderedColumns.add(column);
            columnsToProperty.put(column, new HashSet());
        }
        boolean isPersistentClass = columnOwner instanceof PersistentClass;
        Iterator iterator = it = isPersistentClass ? ((PersistentClass)columnOwner).getPropertyIterator() : ((Join)columnOwner).getPropertyIterator();
        while (it.hasNext()) {
            BinderHelper.matchColumnsByProperty((Property)it.next(), columnsToProperty);
        }
        if (isPersistentClass) {
            BinderHelper.matchColumnsByProperty(((PersistentClass)columnOwner).getIdentifierProperty(), columnsToProperty);
        }
        ArrayList<Property> orderedProperties = new ArrayList<Property>();
        for (Column column : orderedColumns) {
            boolean found = false;
            for (Property property : (Set)columnsToProperty.get(column)) {
                if (property.getColumnSpan() != 1) continue;
                orderedProperties.add(property);
                found = true;
                break;
            }
            if (found) continue;
            return null;
        }
        return orderedProperties;
    }

    private static void matchColumnsByProperty(Property property, Map<Column, Set<Property>> columnsToProperty) {
        if (property == null) {
            return;
        }
        if ("noop".equals(property.getPropertyAccessorName()) || "embedded".equals(property.getPropertyAccessorName())) {
            return;
        }
        Iterator columnIt = property.getColumnIterator();
        while (columnIt.hasNext()) {
            Object column = columnIt.next();
            if (!columnsToProperty.containsKey(column)) continue;
            columnsToProperty.get(column).add(property);
        }
    }

    public static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
        Property property = null;
        Property idProperty = associatedClass.getIdentifierProperty();
        String idName = idProperty != null ? idProperty.getName() : null;
        try {
            if (propertyName == null || propertyName.length() == 0 || propertyName.equals(idName)) {
                property = idProperty;
            } else {
                if (propertyName.indexOf(idName + ".") == 0) {
                    property = idProperty;
                    propertyName = propertyName.substring(idName.length() + 1);
                }
                StringTokenizer st = new StringTokenizer(propertyName, ".", false);
                while (st.hasMoreElements()) {
                    String element = (String)st.nextElement();
                    if (property == null) {
                        property = associatedClass.getProperty(element);
                        continue;
                    }
                    if (!property.isComposite()) {
                        return null;
                    }
                    property = ((Component)property.getValue()).getProperty(element);
                }
            }
        }
        catch (MappingException e) {
            try {
                if (associatedClass.getIdentifierMapper() == null) {
                    return null;
                }
                StringTokenizer st = new StringTokenizer(propertyName, ".", false);
                while (st.hasMoreElements()) {
                    String element = (String)st.nextElement();
                    if (property == null) {
                        property = associatedClass.getIdentifierMapper().getProperty(element);
                        continue;
                    }
                    if (!property.isComposite()) {
                        return null;
                    }
                    property = ((Component)property.getValue()).getProperty(element);
                }
            }
            catch (MappingException ee) {
                return null;
            }
        }
        return property;
    }

    public static Property findPropertyByName(Component component, String propertyName) {
        Property property = null;
        try {
            if (propertyName == null || propertyName.length() == 0) {
                return null;
            }
            StringTokenizer st = new StringTokenizer(propertyName, ".", false);
            while (st.hasMoreElements()) {
                String element = (String)st.nextElement();
                if (property == null) {
                    property = component.getProperty(element);
                    continue;
                }
                if (!property.isComposite()) {
                    return null;
                }
                property = ((Component)property.getValue()).getProperty(element);
            }
        }
        catch (MappingException e) {
            try {
                if (component.getOwner().getIdentifierMapper() == null) {
                    return null;
                }
                StringTokenizer st = new StringTokenizer(propertyName, ".", false);
                while (st.hasMoreElements()) {
                    String element = (String)st.nextElement();
                    if (property == null) {
                        property = component.getOwner().getIdentifierMapper().getProperty(element);
                        continue;
                    }
                    if (!property.isComposite()) {
                        return null;
                    }
                    property = ((Component)property.getValue()).getProperty(element);
                }
            }
            catch (MappingException ee) {
                return null;
            }
        }
        return property;
    }

    public static String getRelativePath(PropertyHolder propertyHolder, String propertyName) {
        if (propertyHolder == null) {
            return propertyName;
        }
        String path = propertyHolder.getPath();
        String entityName = propertyHolder.getPersistentClass().getEntityName();
        if (path.length() == entityName.length()) {
            return propertyName;
        }
        return StringHelper.qualify(path.substring(entityName.length() + 1), propertyName);
    }

    public static Object findColumnOwner(PersistentClass persistentClass, String columnName, MetadataBuildingContext context) {
        PersistentClass result;
        if (StringHelper.isEmpty(columnName)) {
            return persistentClass;
        }
        PersistentClass current = persistentClass;
        boolean found = false;
        do {
            result = current;
            Table currentTable = current.getTable();
            try {
                context.getMetadataCollector().getPhysicalColumnName(currentTable, columnName);
                found = true;
            }
            catch (MappingException mappingException) {
                // empty catch block
            }
            Iterator joins = current.getJoinIterator();
            while (!found && joins.hasNext()) {
                result = joins.next();
                currentTable = ((Join)((Object)result)).getTable();
                try {
                    context.getMetadataCollector().getPhysicalColumnName(currentTable, columnName);
                    found = true;
                }
                catch (MappingException mappingException) {}
            }
            current = current.getSuperclass();
        } while (!found && current != null);
        return found ? result : null;
    }

    public static void makeIdGenerator(SimpleValue id, XProperty idXProperty, String generatorType, String generatorName, MetadataBuildingContext buildingContext, Map<String, IdentifierGeneratorDefinition> localGenerators) {
        log.debugf("#makeIdGenerator(%s, %s, %s, %s, ...)", new Object[]{id, idXProperty, generatorType, generatorName});
        Table table = id.getTable();
        table.setIdentifierValue(id);
        id.setIdentifierGeneratorStrategy(generatorType);
        Properties params = new Properties();
        params.setProperty("target_table", table.getName());
        if (id.getColumnSpan() == 1) {
            params.setProperty("target_column", ((Column)id.getColumnIterator().next()).getName());
        }
        params.put("identifier_normalizer", buildingContext.getObjectNameNormalizer());
        params.put("GENERATOR_NAME", generatorName);
        if (!BinderHelper.isEmptyAnnotationValue(generatorName)) {
            boolean avoidOverriding;
            IdentifierGeneratorDefinition gen = BinderHelper.getIdentifierGenerator(generatorName, idXProperty, localGenerators, buildingContext);
            if (gen == null) {
                throw new AnnotationException("Unknown named generator (@GeneratedValue#generatorName): " + generatorName);
            }
            String identifierGeneratorStrategy = gen.getStrategy();
            boolean bl = avoidOverriding = identifierGeneratorStrategy.equals("identity") || identifierGeneratorStrategy.equals("seqhilo") || identifierGeneratorStrategy.equals(MultipleHiLoPerTableGenerator.class.getName());
            if (generatorType == null || !avoidOverriding) {
                id.setIdentifierGeneratorStrategy(identifierGeneratorStrategy);
            }
            for (Map.Entry<String, String> o : gen.getParameters().entrySet()) {
                Map.Entry<String, String> elt = o;
                if (elt.getKey() == null) continue;
                params.setProperty(elt.getKey(), elt.getValue());
            }
        }
        if ("assigned".equals(generatorType)) {
            id.setNullValue("undefined");
        }
        id.setIdentifierGeneratorProperties(params);
    }

    public static void makeIdGenerator(SimpleValue id, XProperty idXProperty, String generatorType, String generatorName, MetadataBuildingContext buildingContext, IdentifierGeneratorDefinition foreignKGeneratorDefinition) {
        HashMap<String, IdentifierGeneratorDefinition> localIdentifiers = null;
        if (foreignKGeneratorDefinition != null) {
            localIdentifiers = new HashMap<String, IdentifierGeneratorDefinition>();
            localIdentifiers.put(foreignKGeneratorDefinition.getName(), foreignKGeneratorDefinition);
        }
        BinderHelper.makeIdGenerator(id, idXProperty, generatorType, generatorName, buildingContext, localIdentifiers);
    }

    private static IdentifierGeneratorDefinition getIdentifierGenerator(final String name, final XProperty idXProperty, Map<String, IdentifierGeneratorDefinition> localGenerators, final MetadataBuildingContext buildingContext) {
        IdentifierGeneratorDefinition result;
        if (localGenerators != null && (result = localGenerators.get(name)) != null) {
            return result;
        }
        IdentifierGeneratorDefinition globalDefinition = buildingContext.getMetadataCollector().getIdentifierGenerator(name);
        if (globalDefinition != null) {
            return globalDefinition;
        }
        log.debugf("Could not resolve explicit IdentifierGeneratorDefinition - using implicit interpretation (%s)", (Object)name);
        final GeneratedValue generatedValueAnn = (GeneratedValue)idXProperty.getAnnotation(GeneratedValue.class);
        if (generatedValueAnn == null) {
            return new IdentifierGeneratorDefinition("assigned", "assigned");
        }
        IdGeneratorStrategyInterpreter generationInterpreter = buildingContext.getBuildingOptions().getIdGenerationTypeInterpreter();
        GenerationType generationType = BinderHelper.interpretGenerationType(generatedValueAnn);
        if (generationType == null || generationType == GenerationType.SEQUENCE) {
            log.debugf("Building implicit sequence-based IdentifierGeneratorDefinition (%s)", (Object)name);
            IdentifierGeneratorDefinition.Builder builder = new IdentifierGeneratorDefinition.Builder();
            generationInterpreter.interpretSequenceGenerator(new SequenceGenerator(){

                public String name() {
                    return name;
                }

                public String sequenceName() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public String catalog() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public String schema() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public int initialValue() {
                    return 1;
                }

                public int allocationSize() {
                    return 50;
                }

                public Class<? extends Annotation> annotationType() {
                    return SequenceGenerator.class;
                }
            }, builder);
            return builder.build();
        }
        if (generationType == GenerationType.TABLE) {
            log.debugf("Building implicit table-based IdentifierGeneratorDefinition (%s)", (Object)name);
            IdentifierGeneratorDefinition.Builder builder = new IdentifierGeneratorDefinition.Builder();
            generationInterpreter.interpretTableGenerator(new TableGenerator(){

                public String name() {
                    return name;
                }

                public String table() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public int initialValue() {
                    return 0;
                }

                public int allocationSize() {
                    return 50;
                }

                public String catalog() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public String schema() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public String pkColumnName() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public String valueColumnName() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public String pkColumnValue() {
                    return BinderHelper.ANNOTATION_STRING_DEFAULT;
                }

                public UniqueConstraint[] uniqueConstraints() {
                    return new UniqueConstraint[0];
                }

                public Index[] indexes() {
                    return new Index[0];
                }

                public Class<? extends Annotation> annotationType() {
                    return TableGenerator.class;
                }
            }, builder);
            return builder.build();
        }
        String strategyName = generationType == GenerationType.IDENTITY ? "identity" : generationInterpreter.determineGeneratorName(generationType, new IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext(){

            @Override
            public Class getIdType() {
                return buildingContext.getBootstrapContext().getReflectionManager().toClass(idXProperty.getType());
            }

            @Override
            public String getGeneratedValueGeneratorName() {
                return generatedValueAnn.generator();
            }
        });
        log.debugf("Building implicit generic IdentifierGeneratorDefinition (%s) : %s", (Object)name, (Object)strategyName);
        return new IdentifierGeneratorDefinition(name, strategyName, Collections.singletonMap("GENERATOR_NAME", name));
    }

    private static GenerationType interpretGenerationType(GeneratedValue generatedValueAnn) {
        if (generatedValueAnn.strategy() == null) {
            return GenerationType.AUTO;
        }
        return generatedValueAnn.strategy();
    }

    public static boolean isEmptyAnnotationValue(String annotationString) {
        return annotationString != null && annotationString.length() == 0;
    }

    public static boolean isEmptyOrNullAnnotationValue(String annotationString) {
        return annotationString == null || annotationString.length() == 0;
    }

    public static Any buildAnyValue(String anyMetaDefName, Ejb3JoinColumn[] columns, javax.persistence.Column metaColumn, PropertyData inferredData, boolean cascadeOnDelete, boolean lazy, Nullability nullability, PropertyHolder propertyHolder, EntityBinder entityBinder, boolean optional, MetadataBuildingContext context) {
        Ejb3Column[] metaColumns;
        Any value = new Any(context, columns[0].getTable());
        AnyMetaDef metaAnnDef = (AnyMetaDef)inferredData.getProperty().getAnnotation(AnyMetaDef.class);
        value.setLazy(lazy);
        if (metaAnnDef != null) {
            BinderHelper.bindAnyMetaDefs((XAnnotatedElement)inferredData.getProperty(), context);
        } else {
            metaAnnDef = context.getMetadataCollector().getAnyMetaDef(anyMetaDefName);
        }
        if (metaAnnDef != null) {
            value.setIdentifierType(metaAnnDef.idType());
            value.setMetaType(metaAnnDef.metaType());
            Ejb3JoinColumn[] values = new HashMap();
            Ejb3Column[] metaType = context.getMetadataCollector().getTypeResolver().heuristicType(value.getMetaType());
            MetaValue[] metaValueArray = metaAnnDef.metaValues();
            int n = metaValueArray.length;
            for (int i = 0; i < n; ++i) {
                MetaValue metaValue = metaValueArray[i];
                try {
                    Object discrim = ((DiscriminatorType)metaType).stringToObject(metaValue.value());
                    String entityName = metaValue.targetEntity().getName();
                    values.put(discrim, entityName);
                    continue;
                }
                catch (ClassCastException cce) {
                    throw new MappingException("metaType was not a DiscriminatorType: " + metaType.getName());
                }
                catch (Exception e) {
                    throw new MappingException("could not interpret metaValue", e);
                }
            }
            if (!values.isEmpty()) {
                value.setMetaValues((Map)values);
            }
        } else {
            throw new AnnotationException("Unable to find @AnyMetaDef for an @(ManyTo)Any mapping: " + StringHelper.qualify(propertyHolder.getPath(), inferredData.getPropertyName()));
        }
        value.setCascadeDeleteEnabled(cascadeOnDelete);
        if (!optional) {
            for (Ejb3JoinColumn column : columns) {
                column.setNullable(false);
            }
        }
        for (Ejb3Column column : metaColumns = Ejb3Column.buildColumnFromAnnotation(new javax.persistence.Column[]{metaColumn}, null, null, nullability, propertyHolder, inferredData, entityBinder.getSecondaryTables(), context)) {
            column.setTable(value.getTable());
        }
        for (Ejb3Column column : metaColumns) {
            column.linkWithValue(value);
        }
        String propertyName = inferredData.getPropertyName();
        Ejb3Column.checkPropertyConsistency(columns, propertyHolder.getEntityName() + "." + propertyName);
        for (Ejb3JoinColumn column : columns) {
            column.linkWithValue(value);
        }
        return value;
    }

    public static void bindAnyMetaDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
        boolean mustHaveName;
        AnyMetaDef defAnn = (AnyMetaDef)annotatedElement.getAnnotation(AnyMetaDef.class);
        AnyMetaDefs defsAnn = (AnyMetaDefs)annotatedElement.getAnnotation(AnyMetaDefs.class);
        boolean bl = mustHaveName = XClass.class.isAssignableFrom(annotatedElement.getClass()) || XPackage.class.isAssignableFrom(annotatedElement.getClass());
        if (defAnn != null) {
            BinderHelper.checkAnyMetaDefValidity(mustHaveName, defAnn, annotatedElement);
            BinderHelper.bindAnyMetaDef(defAnn, context);
        }
        if (defsAnn != null) {
            for (AnyMetaDef def : defsAnn.value()) {
                BinderHelper.checkAnyMetaDefValidity(mustHaveName, def, annotatedElement);
                BinderHelper.bindAnyMetaDef(def, context);
            }
        }
    }

    private static void checkAnyMetaDefValidity(boolean mustHaveName, AnyMetaDef defAnn, XAnnotatedElement annotatedElement) {
        if (mustHaveName && BinderHelper.isEmptyAnnotationValue(defAnn.name())) {
            String name = XClass.class.isAssignableFrom(annotatedElement.getClass()) ? ((XClass)annotatedElement).getName() : ((XPackage)annotatedElement).getName();
            throw new AnnotationException("@AnyMetaDef.name cannot be null on an entity or a package: " + name);
        }
    }

    private static void bindAnyMetaDef(AnyMetaDef defAnn, MetadataBuildingContext context) {
        if (BinderHelper.isEmptyAnnotationValue(defAnn.name())) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding Any Meta definition: %s", defAnn.name());
        }
        context.getMetadataCollector().addAnyMetaDef(defAnn);
    }

    public static MappedSuperclass getMappedSuperclassOrNull(XClass declaringClass, Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext context) {
        boolean retrieve = false;
        if (declaringClass != null) {
            InheritanceState inheritanceState = inheritanceStatePerClass.get(declaringClass);
            if (inheritanceState == null) {
                throw new AssertionFailure("Declaring class is not found in the inheritance state hierarchy: " + declaringClass);
            }
            if (inheritanceState.isEmbeddableSuperclass()) {
                retrieve = true;
            }
        }
        if (retrieve) {
            return context.getMetadataCollector().getMappedSuperclass(context.getBootstrapContext().getReflectionManager().toClass(declaringClass));
        }
        return null;
    }

    public static String getPath(PropertyHolder holder, PropertyData property) {
        return StringHelper.qualify(holder.getPath(), property.getPropertyName());
    }

    static PropertyData getPropertyOverriddenByMapperOrMapsId(boolean isId, PropertyHolder propertyHolder, String propertyName, MetadataBuildingContext buildingContext) {
        XClass persistentXClass = buildingContext.getBootstrapContext().getReflectionManager().toXClass(propertyHolder.getPersistentClass().getMappedClass());
        if (propertyHolder.isInIdClass()) {
            PropertyData pd = buildingContext.getMetadataCollector().getPropertyAnnotatedWithIdAndToOne(persistentXClass, propertyName);
            if (pd == null && buildingContext.getBuildingOptions().isSpecjProprietarySyntaxEnabled()) {
                pd = buildingContext.getMetadataCollector().getPropertyAnnotatedWithMapsId(persistentXClass, propertyName);
            }
            return pd;
        }
        String propertyPath = isId ? ANNOTATION_STRING_DEFAULT : propertyName;
        return buildingContext.getMetadataCollector().getPropertyAnnotatedWithMapsId(persistentXClass, propertyPath);
    }

    public static Map<String, String> toAliasTableMap(SqlFragmentAlias[] aliases) {
        HashMap<String, String> ret = new HashMap<String, String>();
        for (SqlFragmentAlias aliase : aliases) {
            if (!StringHelper.isNotEmpty(aliase.table())) continue;
            ret.put(aliase.alias(), aliase.table());
        }
        return ret;
    }

    public static Map<String, String> toAliasEntityMap(SqlFragmentAlias[] aliases) {
        HashMap<String, String> ret = new HashMap<String, String>();
        for (SqlFragmentAlias aliase : aliases) {
            if (aliase.entity() == Void.TYPE) continue;
            ret.put(aliase.alias(), aliase.entity().getName());
        }
        return ret;
    }

    static {
        HashSet<String> primitiveNames = new HashSet<String>();
        primitiveNames.add(Byte.TYPE.getName());
        primitiveNames.add(Short.TYPE.getName());
        primitiveNames.add(Integer.TYPE.getName());
        primitiveNames.add(Long.TYPE.getName());
        primitiveNames.add(Float.TYPE.getName());
        primitiveNames.add(Double.TYPE.getName());
        primitiveNames.add(Character.TYPE.getName());
        primitiveNames.add(Boolean.TYPE.getName());
        PRIMITIVE_NAMES = Collections.unmodifiableSet(primitiveNames);
    }
}

