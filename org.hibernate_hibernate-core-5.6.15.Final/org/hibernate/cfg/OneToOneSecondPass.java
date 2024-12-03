/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ForeignKey
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinColumns
 *  org.hibernate.annotations.common.reflection.XClass
 */
package org.hibernate.cfg;

import java.util.Iterator;
import java.util.Map;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.annotations.LazyGroup;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.ToOneBinder;
import org.hibernate.cfg.ToOneFkSecondPass;
import org.hibernate.cfg.annotations.PropertyBinder;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.type.ForeignKeyDirection;

public class OneToOneSecondPass
implements SecondPass {
    private MetadataBuildingContext buildingContext;
    private String mappedBy;
    private String ownerEntity;
    private String ownerProperty;
    private PropertyHolder propertyHolder;
    private NotFoundAction notFoundAction;
    private PropertyData inferredData;
    private XClass targetEntity;
    private boolean cascadeOnDelete;
    private boolean optional;
    private String cascadeStrategy;
    private Ejb3JoinColumn[] joinColumns;

    public OneToOneSecondPass(String mappedBy, String ownerEntity, String ownerProperty, PropertyHolder propertyHolder, PropertyData inferredData, XClass targetEntity, NotFoundAction notFoundAction, boolean cascadeOnDelete, boolean optional, String cascadeStrategy, Ejb3JoinColumn[] columns, MetadataBuildingContext buildingContext) {
        this.ownerEntity = ownerEntity;
        this.ownerProperty = ownerProperty;
        this.mappedBy = mappedBy;
        this.propertyHolder = propertyHolder;
        this.buildingContext = buildingContext;
        this.notFoundAction = notFoundAction;
        this.inferredData = inferredData;
        this.targetEntity = targetEntity;
        this.cascadeOnDelete = cascadeOnDelete;
        this.optional = optional;
        this.cascadeStrategy = cascadeStrategy;
        this.joinColumns = columns;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        OneToOne value = new OneToOne(this.buildingContext, this.propertyHolder.getTable(), this.propertyHolder.getPersistentClass());
        String propertyName = this.inferredData.getPropertyName();
        value.setPropertyName(propertyName);
        String referencedEntityName = ToOneBinder.getReferenceEntityName(this.inferredData, this.targetEntity, this.buildingContext);
        value.setReferencedEntityName(referencedEntityName);
        AnnotationBinder.defineFetchingStrategy(value, this.inferredData.getProperty());
        value.setCascadeDeleteEnabled(this.cascadeOnDelete);
        value.setConstrained(!this.optional);
        ForeignKeyDirection foreignKeyDirection = !BinderHelper.isEmptyAnnotationValue(this.mappedBy) ? ForeignKeyDirection.TO_PARENT : ForeignKeyDirection.FROM_PARENT;
        value.setForeignKeyType(foreignKeyDirection);
        AnnotationBinder.bindForeignKeyNameAndDefinition(value, this.inferredData.getProperty(), (ForeignKey)this.inferredData.getProperty().getAnnotation(ForeignKey.class), (JoinColumn)this.inferredData.getProperty().getAnnotation(JoinColumn.class), (JoinColumns)this.inferredData.getProperty().getAnnotation(JoinColumns.class), this.buildingContext);
        PropertyBinder binder = new PropertyBinder();
        binder.setName(propertyName);
        binder.setProperty(this.inferredData.getProperty());
        binder.setValue(value);
        binder.setCascade(this.cascadeStrategy);
        binder.setAccessType(this.inferredData.getDefaultAccess());
        LazyGroup lazyGroupAnnotation = (LazyGroup)this.inferredData.getProperty().getAnnotation(LazyGroup.class);
        if (lazyGroupAnnotation != null) {
            binder.setLazyGroup(lazyGroupAnnotation.value());
        }
        Property prop = binder.makeProperty();
        prop.setOptional(this.optional);
        if (BinderHelper.isEmptyAnnotationValue(this.mappedBy)) {
            boolean rightOrder = true;
            if (rightOrder) {
                String path = StringHelper.qualify(this.propertyHolder.getPath(), propertyName);
                ToOneFkSecondPass secondPass = new ToOneFkSecondPass(value, this.joinColumns, !this.optional, this.propertyHolder.getEntityOwnerClassName(), path, this.buildingContext);
                secondPass.doSecondPass(persistentClasses);
                this.propertyHolder.addProperty(prop, this.inferredData.getDeclaringClass());
            }
        } else {
            Property otherSideProperty;
            PersistentClass otherSide = (PersistentClass)persistentClasses.get(value.getReferencedEntityName());
            try {
                if (otherSide == null) {
                    throw new MappingException("Unable to find entity: " + value.getReferencedEntityName());
                }
                otherSideProperty = BinderHelper.findPropertyByName(otherSide, this.mappedBy);
            }
            catch (MappingException e) {
                throw new AnnotationException("Unknown mappedBy in: " + StringHelper.qualify(this.ownerEntity, this.ownerProperty) + ", referenced property unknown: " + StringHelper.qualify(value.getReferencedEntityName(), this.mappedBy));
            }
            if (otherSideProperty == null) {
                throw new AnnotationException("Unknown mappedBy in: " + StringHelper.qualify(this.ownerEntity, this.ownerProperty) + ", referenced property unknown: " + StringHelper.qualify(value.getReferencedEntityName(), this.mappedBy));
            }
            if (otherSideProperty.getValue() instanceof OneToOne) {
                this.propertyHolder.addProperty(prop, this.inferredData.getDeclaringClass());
            } else if (otherSideProperty.getValue() instanceof ManyToOne) {
                Iterator it = otherSide.getJoinIterator();
                Join otherSideJoin = null;
                while (it.hasNext()) {
                    Join otherSideJoinValue = (Join)it.next();
                    if (!otherSideJoinValue.containsProperty(otherSideProperty)) continue;
                    otherSideJoin = otherSideJoinValue;
                    break;
                }
                if (otherSideJoin != null) {
                    Join mappedByJoin = this.buildJoinFromMappedBySide((PersistentClass)persistentClasses.get(this.ownerEntity), otherSideProperty, otherSideJoin);
                    ManyToOne manyToOne = new ManyToOne(this.buildingContext, mappedByJoin.getTable());
                    manyToOne.setNotFoundAction(this.notFoundAction);
                    manyToOne.setCascadeDeleteEnabled(value.isCascadeDeleteEnabled());
                    manyToOne.setFetchMode(value.getFetchMode());
                    manyToOne.setLazy(value.isLazy());
                    manyToOne.setReferencedEntityName(value.getReferencedEntityName());
                    manyToOne.setUnwrapProxy(value.isUnwrapProxy());
                    prop.setValue(manyToOne);
                    Iterator<Selectable> otherSideJoinKeyColumns = otherSideJoin.getKey().getColumnIterator();
                    while (otherSideJoinKeyColumns.hasNext()) {
                        Column column = (Column)otherSideJoinKeyColumns.next();
                        Column copy = new Column();
                        copy.setLength(column.getLength());
                        copy.setScale(column.getScale());
                        copy.setValue(manyToOne);
                        copy.setName(column.getQuotedName());
                        copy.setNullable(column.isNullable());
                        copy.setPrecision(column.getPrecision());
                        copy.setUnique(column.isUnique());
                        copy.setSqlType(column.getSqlType());
                        copy.setCheckConstraint(column.getCheckConstraint());
                        copy.setComment(column.getComment());
                        copy.setDefaultValue(column.getDefaultValue());
                        manyToOne.addColumn(copy);
                    }
                    mappedByJoin.addProperty(prop);
                } else {
                    this.propertyHolder.addProperty(prop, this.inferredData.getDeclaringClass());
                }
                value.setReferencedPropertyName(this.mappedBy);
                boolean referencesDerivedId = false;
                try {
                    referencesDerivedId = otherSide.getIdentifier() instanceof Component && ((Component)otherSide.getIdentifier()).getProperty(this.mappedBy) != null;
                }
                catch (MappingException manyToOne) {
                    // empty catch block
                }
                boolean referenceToPrimaryKey = referencesDerivedId || this.mappedBy == null;
                value.setReferenceToPrimaryKey(referenceToPrimaryKey);
                String propertyRef = value.getReferencedPropertyName();
                if (propertyRef != null) {
                    this.buildingContext.getMetadataCollector().addUniquePropertyReference(value.getReferencedEntityName(), propertyRef);
                }
            } else {
                throw new AnnotationException("Referenced property not a (One|Many)ToOne: " + StringHelper.qualify(otherSide.getEntityName(), this.mappedBy) + " in mappedBy of " + StringHelper.qualify(this.ownerEntity, this.ownerProperty));
            }
        }
    }

    private Join buildJoinFromMappedBySide(PersistentClass persistentClass, Property otherSideProperty, Join originalJoin) {
        Join join = new Join();
        join.setPersistentClass(persistentClass);
        join.setTable(originalJoin.getTable());
        join.setInverse(true);
        DependantValue key = new DependantValue(this.buildingContext, join.getTable(), persistentClass.getIdentifier());
        join.setKey(key);
        join.setSequentialSelect(false);
        join.setOptional(true);
        key.setCascadeDeleteEnabled(false);
        Iterator<Selectable> mappedByColumns = otherSideProperty.getValue().getColumnIterator();
        while (mappedByColumns.hasNext()) {
            Column column = (Column)mappedByColumns.next();
            Column copy = new Column();
            copy.setLength(column.getLength());
            copy.setScale(column.getScale());
            copy.setValue(key);
            copy.setName(column.getQuotedName());
            copy.setNullable(column.isNullable());
            copy.setPrecision(column.getPrecision());
            copy.setUnique(column.isUnique());
            copy.setSqlType(column.getSqlType());
            copy.setCheckConstraint(column.getCheckConstraint());
            copy.setComment(column.getComment());
            copy.setDefaultValue(column.getDefaultValue());
            key.addColumn(copy);
        }
        persistentClass.addJoin(join);
        return join;
    }
}

