/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeOverride
 *  javax.persistence.AttributeOverrides
 *  javax.persistence.ConstraintMode
 *  javax.persistence.ForeignKey
 *  javax.persistence.InheritanceType
 *  javax.persistence.MapKeyClass
 *  javax.persistence.MapKeyColumn
 *  javax.persistence.MapKeyJoinColumn
 *  javax.persistence.MapKeyJoinColumns
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg.annotations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.InheritanceType;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchMode;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.AnnotatedClassType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.CollectionPropertyHolder;
import org.hibernate.cfg.CollectionSecondPass;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.PropertyHolderBuilder;
import org.hibernate.cfg.PropertyPreloadedData;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.sql.Template;

public class MapBinder
extends CollectionBinder {
    public MapBinder(boolean sorted) {
        super(sorted);
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new Map(this.getBuildingContext(), persistentClass);
    }

    @Override
    public SecondPass getSecondPass(final Ejb3JoinColumn[] fkJoinColumns, final Ejb3JoinColumn[] keyColumns, final Ejb3JoinColumn[] inverseColumns, final Ejb3Column[] elementColumns, final Ejb3Column[] mapKeyColumns, final Ejb3JoinColumn[] mapKeyManyToManyColumns, final boolean isEmbedded, final XProperty property, final XClass collType, final NotFoundAction notFoundAction, final boolean unique, final TableBinder assocTableBinder, final MetadataBuildingContext buildingContext) {
        return new CollectionSecondPass(buildingContext, this.collection){

            @Override
            public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) {
                MapBinder.this.bindStarToManySecondPass(persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns, isEmbedded, property, unique, assocTableBinder, notFoundAction, buildingContext);
                MapBinder.this.bindKeyFromAssociationTable(collType, persistentClasses, MapBinder.this.mapKeyPropertyName, property, isEmbedded, buildingContext, mapKeyColumns, mapKeyManyToManyColumns, inverseColumns != null ? inverseColumns[0].getPropertyName() : null);
                MapBinder.this.makeOneToManyMapKeyColumnNullableIfNotInProperty(property);
            }
        };
    }

    private void makeOneToManyMapKeyColumnNullableIfNotInProperty(XProperty property) {
        Map map = (Map)this.collection;
        if (map.isOneToMany() && property.isAnnotationPresent(MapKeyColumn.class)) {
            PersistentClass persistentClass;
            Value indexValue = map.getIndex();
            if (indexValue.getColumnSpan() != 1) {
                throw new AssertionFailure("Map key mapped by @MapKeyColumn does not have 1 column");
            }
            Selectable selectable = indexValue.getColumnIterator().next();
            if (selectable.isFormula()) {
                throw new AssertionFailure("Map key mapped by @MapKeyColumn is a Formula");
            }
            Column column = (Column)map.getIndex().getColumnIterator().next();
            if (!column.isNullable() && !this.propertyIteratorContainsColumn((persistentClass = ((OneToMany)map.getElement()).getAssociatedClass()).getUnjoinedPropertyIterator(), column)) {
                column.setNullable(true);
            }
        }
    }

    private boolean propertyIteratorContainsColumn(Iterator propertyIterator, Column column) {
        Iterator it = propertyIterator;
        while (it.hasNext()) {
            Property property = (Property)it.next();
            Iterator selectableIterator = property.getColumnIterator();
            while (selectableIterator.hasNext()) {
                Selectable selectable = (Selectable)selectableIterator.next();
                if (!column.equals(selectable)) continue;
                Column iteratedColumn = (Column)selectable;
                if (!column.getValue().getTable().equals(iteratedColumn.getValue().getTable())) continue;
                return true;
            }
        }
        return false;
    }

    private void bindKeyFromAssociationTable(XClass collType, java.util.Map persistentClasses, String mapKeyPropertyName, XProperty property, boolean isEmbedded, MetadataBuildingContext buildingContext, Ejb3Column[] mapKeyColumns, Ejb3JoinColumn[] mapKeyManyToManyColumns, String targetPropertyName) {
        if (mapKeyPropertyName != null) {
            PersistentClass associatedClass = (PersistentClass)persistentClasses.get(collType.getName());
            if (associatedClass == null) {
                throw new AnnotationException("Associated class not found: " + collType);
            }
            Property mapProperty = BinderHelper.findPropertyByName(associatedClass, mapKeyPropertyName);
            if (mapProperty == null) {
                throw new AnnotationException("Map key property not found: " + collType + "." + mapKeyPropertyName);
            }
            Map map = (Map)this.collection;
            InheritanceState inheritanceState = (InheritanceState)this.inheritanceStatePerClass.get(collType);
            PersistentClass targetPropertyPersistentClass = InheritanceType.JOINED.equals((Object)inheritanceState.getType()) ? mapProperty.getPersistentClass() : associatedClass;
            Value indexValue = this.createFormulatedValue(mapProperty.getValue(), map, targetPropertyName, associatedClass, targetPropertyPersistentClass, buildingContext);
            map.setIndex(indexValue);
        } else {
            ForeignKey foreignKey;
            Class target = Void.TYPE;
            if (property.isAnnotationPresent(MapKeyClass.class)) {
                target = ((MapKeyClass)property.getAnnotation(MapKeyClass.class)).value();
            }
            String mapKeyType = !Void.TYPE.equals(target) ? target.getName() : property.getMapKey().getName();
            PersistentClass collectionEntity = (PersistentClass)persistentClasses.get(mapKeyType);
            boolean isIndexOfEntities = collectionEntity != null;
            ManyToOne element = null;
            Map mapValue = (Map)this.collection;
            if (isIndexOfEntities) {
                element = new ManyToOne(buildingContext, mapValue.getCollectionTable());
                mapValue.setIndex(element);
                element.setReferencedEntityName(mapKeyType);
                element.setFetchMode(FetchMode.JOIN);
                element.setLazy(false);
            } else {
                AccessType accessType;
                Ejb3JoinColumn[] keyXClass;
                AnnotatedClassType classType;
                if (BinderHelper.PRIMITIVE_NAMES.contains(mapKeyType)) {
                    classType = AnnotatedClassType.NONE;
                    keyXClass = null;
                } else {
                    BootstrapContext bootstrapContext = buildingContext.getBootstrapContext();
                    Class mapKeyClass = bootstrapContext.getClassLoaderAccess().classForName(mapKeyType);
                    keyXClass = bootstrapContext.getReflectionManager().toXClass(mapKeyClass);
                    classType = buildingContext.getMetadataCollector().getClassType((XClass)keyXClass);
                    if (isEmbedded || this.mappingDefinedAttributeOverrideOnMapKey(property)) {
                        classType = AnnotatedClassType.EMBEDDABLE;
                    }
                }
                CollectionPropertyHolder holder = PropertyHolderBuilder.buildPropertyHolder(mapValue, StringHelper.qualify(mapValue.getRole(), "mapkey"), (XClass)keyXClass, property, this.propertyHolder, buildingContext);
                this.propertyHolder.startingProperty(property);
                holder.prepare(property);
                PersistentClass owner = mapValue.getOwner();
                if (owner.getIdentifierProperty() != null) {
                    accessType = owner.getIdentifierProperty().getPropertyAccessorName().equals("property") ? AccessType.PROPERTY : AccessType.FIELD;
                } else if (owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0) {
                    Property prop = (Property)owner.getIdentifierMapper().getPropertyIterator().next();
                    accessType = prop.getPropertyAccessorName().equals("property") ? AccessType.PROPERTY : AccessType.FIELD;
                } else {
                    throw new AssertionFailure("Unable to guess collection property accessor name");
                }
                if (AnnotatedClassType.EMBEDDABLE.equals((Object)classType)) {
                    EntityBinder entityBinder = new EntityBinder();
                    PropertyPreloadedData inferredData = this.isHibernateExtensionMapping() ? new PropertyPreloadedData(AccessType.PROPERTY, "index", (XClass)keyXClass) : new PropertyPreloadedData(AccessType.PROPERTY, "key", (XClass)keyXClass);
                    Component component = AnnotationBinder.fillComponent(holder, inferredData, accessType, true, entityBinder, false, false, true, buildingContext, this.inheritanceStatePerClass);
                    mapValue.setIndex(component);
                } else {
                    SimpleValueBinder elementBinder = new SimpleValueBinder();
                    elementBinder.setBuildingContext(buildingContext);
                    elementBinder.setReturnedClassName(mapKeyType);
                    Ejb3Column[] elementColumns = mapKeyColumns;
                    if (elementColumns == null || elementColumns.length == 0) {
                        elementColumns = new Ejb3Column[1];
                        Ejb3Column column = new Ejb3Column();
                        column.setImplicit(false);
                        column.setNullable(true);
                        column.setLength(255);
                        column.setLogicalColumnName("id");
                        column.setJoins(new HashMap<String, Join>());
                        column.setBuildingContext(buildingContext);
                        column.bind();
                        elementColumns[0] = column;
                    }
                    for (Ejb3Column column : elementColumns) {
                        column.setTable(mapValue.getCollectionTable());
                    }
                    elementBinder.setColumns(elementColumns);
                    elementBinder.setKey(true);
                    elementBinder.setType(property, (XClass)keyXClass, this.collection.getOwnerEntityName(), holder.mapKeyAttributeConverterDescriptor(property, (XClass)keyXClass));
                    elementBinder.setPersistentClassName(this.propertyHolder.getEntityName());
                    elementBinder.setAccessType(accessType);
                    mapValue.setIndex(elementBinder.make());
                }
            }
            if (!this.collection.isOneToMany()) {
                for (Ejb3JoinColumn col : mapKeyManyToManyColumns) {
                    col.forceNotNull();
                }
            }
            if (element != null && (foreignKey = this.getMapKeyForeignKey(property)) != null) {
                if (foreignKey.value() == ConstraintMode.NO_CONSTRAINT || foreignKey.value() == ConstraintMode.PROVIDER_DEFAULT && this.getBuildingContext().getBuildingOptions().isNoConstraintByDefault()) {
                    element.setForeignKeyName("none");
                } else {
                    element.setForeignKeyName(StringHelper.nullIfEmpty(foreignKey.name()));
                    element.setForeignKeyDefinition(StringHelper.nullIfEmpty(foreignKey.foreignKeyDefinition()));
                }
            }
            if (isIndexOfEntities) {
                MapBinder.bindManytoManyInverseFk(collectionEntity, mapKeyManyToManyColumns, element, false, buildingContext);
            }
        }
    }

    private ForeignKey getMapKeyForeignKey(XProperty property) {
        MapKeyJoinColumns mapKeyJoinColumns = (MapKeyJoinColumns)property.getAnnotation(MapKeyJoinColumns.class);
        if (mapKeyJoinColumns != null) {
            return mapKeyJoinColumns.foreignKey();
        }
        MapKeyJoinColumn mapKeyJoinColumn = (MapKeyJoinColumn)property.getAnnotation(MapKeyJoinColumn.class);
        if (mapKeyJoinColumn != null) {
            return mapKeyJoinColumn.foreignKey();
        }
        return null;
    }

    private boolean mappingDefinedAttributeOverrideOnMapKey(XProperty property) {
        if (property.isAnnotationPresent(AttributeOverride.class)) {
            return this.namedMapKey((AttributeOverride)property.getAnnotation(AttributeOverride.class));
        }
        if (property.isAnnotationPresent(AttributeOverrides.class)) {
            AttributeOverrides annotations = (AttributeOverrides)property.getAnnotation(AttributeOverrides.class);
            for (AttributeOverride attributeOverride : annotations.value()) {
                if (!this.namedMapKey(attributeOverride)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean namedMapKey(AttributeOverride annotation) {
        return annotation.name().startsWith("key.");
    }

    protected Value createFormulatedValue(Value value, Collection collection, String targetPropertyName, PersistentClass associatedClass, PersistentClass targetPropertyPersistentClass, MetadataBuildingContext buildingContext) {
        Value element = collection.getElement();
        String fromAndWhere = null;
        StandardServiceRegistry serviceRegistry = buildingContext.getBootstrapContext().getServiceRegistry();
        ConfigurationService configurationService = serviceRegistry.getService(ConfigurationService.class);
        SqlStringGenerationContext generationContext = SqlStringGenerationContextImpl.fromExplicit(serviceRegistry.getService(JdbcServices.class).getJdbcEnvironment(), buildingContext.getMetadataCollector().getDatabase(), configurationService.getSetting("hibernate.default_catalog", String.class, null), configurationService.getSetting("hibernate.default_schema", String.class, null));
        if (!(element instanceof OneToMany)) {
            Iterator referencedEntityColumns;
            String referencedPropertyName = null;
            if (element instanceof ToOne) {
                referencedPropertyName = ((ToOne)element).getReferencedPropertyName();
            } else if (element instanceof DependantValue) {
                if (this.propertyName != null) {
                    referencedPropertyName = collection.getReferencedPropertyName();
                } else {
                    throw new AnnotationException("SecondaryTable JoinColumn cannot reference a non primary key");
                }
            }
            if (referencedPropertyName == null) {
                referencedEntityColumns = associatedClass.getIdentifier().getColumnIterator();
            } else {
                Property referencedProperty = associatedClass.getRecursiveProperty(referencedPropertyName);
                referencedEntityColumns = referencedProperty.getColumnIterator();
            }
            fromAndWhere = this.getFromAndWhereFormula(generationContext.format(associatedClass.getTable().getQualifiedTableName()), element.getColumnIterator(), referencedEntityColumns);
        } else if (!associatedClass.equals(targetPropertyPersistentClass)) {
            fromAndWhere = this.getFromAndWhereFormula(generationContext.format(targetPropertyPersistentClass.getTable().getQualifiedTableName()), element.getColumnIterator(), associatedClass.getIdentifier().getColumnIterator());
        }
        if (value instanceof Component) {
            Component component = (Component)value;
            Iterator properties = component.getPropertyIterator();
            Component indexComponent = new Component(this.getBuildingContext(), collection);
            indexComponent.setComponentClassName(component.getComponentClassName());
            while (properties.hasNext()) {
                Property current = (Property)properties.next();
                Property newProperty = new Property();
                newProperty.setCascade(current.getCascade());
                newProperty.setValueGenerationStrategy(current.getValueGenerationStrategy());
                newProperty.setInsertable(false);
                newProperty.setUpdateable(false);
                newProperty.setMetaAttributes(current.getMetaAttributes());
                newProperty.setName(current.getName());
                newProperty.setNaturalIdentifier(false);
                newProperty.setOptional(false);
                newProperty.setPersistentClass(current.getPersistentClass());
                newProperty.setPropertyAccessorName(current.getPropertyAccessorName());
                newProperty.setSelectable(current.isSelectable());
                newProperty.setValue(this.createFormulatedValue(current.getValue(), collection, targetPropertyName, associatedClass, associatedClass, buildingContext));
                indexComponent.addProperty(newProperty);
            }
            return indexComponent;
        }
        if (value instanceof SimpleValue) {
            SimpleValue targetValue;
            SimpleValue sourceValue = (SimpleValue)value;
            if (value instanceof ManyToOne) {
                ManyToOne sourceManyToOne = (ManyToOne)sourceValue;
                ManyToOne targetManyToOne = new ManyToOne(this.getBuildingContext(), collection.getCollectionTable());
                targetManyToOne.setFetchMode(FetchMode.DEFAULT);
                targetManyToOne.setLazy(true);
                targetManyToOne.setReferencedEntityName(sourceManyToOne.getReferencedEntityName());
                targetValue = targetManyToOne;
            } else {
                targetValue = new SimpleValue(this.getBuildingContext(), collection.getCollectionTable());
                targetValue.copyTypeFrom(sourceValue);
            }
            Iterator<Selectable> columns = sourceValue.getColumnIterator();
            Random random = new Random();
            while (columns.hasNext()) {
                String formulaString;
                Selectable current = columns.next();
                Formula formula = new Formula();
                if (current instanceof Column) {
                    formulaString = ((Column)current).getQuotedName();
                } else if (current instanceof Formula) {
                    formulaString = ((Formula)current).getFormula();
                } else {
                    throw new AssertionFailure("Unknown element in column iterator: " + current.getClass());
                }
                if (fromAndWhere != null) {
                    formulaString = Template.renderWhereStringTemplate(formulaString, "$alias$", new HSQLDialect());
                    formulaString = "(select " + formulaString + fromAndWhere + ")";
                    formulaString = StringHelper.replace(formulaString, "$alias$", "a" + random.nextInt(16));
                }
                formula.setFormula(formulaString);
                targetValue.addFormula(formula);
            }
            return targetValue;
        }
        throw new AssertionFailure("Unknown type encounters for map key: " + value.getClass());
    }

    private String getFromAndWhereFormula(String tableName, Iterator<Selectable> collectionTableColumns, Iterator<Selectable> referencedEntityColumns) {
        String alias = "$alias$";
        StringBuilder fromAndWhereSb = new StringBuilder(" from ").append(tableName).append(" ").append(alias).append(" where ");
        while (collectionTableColumns.hasNext()) {
            Column colColumn = (Column)collectionTableColumns.next();
            Column refColumn = (Column)referencedEntityColumns.next();
            fromAndWhereSb.append(alias).append('.').append(refColumn.getQuotedName()).append('=').append(colColumn.getQuotedName()).append(" and ");
        }
        return fromAndWhereSb.substring(0, fromAndWhereSb.length() - 5);
    }
}

