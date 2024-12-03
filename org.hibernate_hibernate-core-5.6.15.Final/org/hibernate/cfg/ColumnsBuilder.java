/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.ElementCollection
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinColumns
 *  javax.persistence.JoinTable
 *  javax.persistence.ManyToMany
 *  javax.persistence.ManyToOne
 *  javax.persistence.OneToMany
 *  javax.persistence.OneToOne
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.AnnotationException;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.internal.util.StringHelper;

class ColumnsBuilder {
    private PropertyHolder propertyHolder;
    private Nullability nullability;
    private XProperty property;
    private PropertyData inferredData;
    private EntityBinder entityBinder;
    private MetadataBuildingContext buildingContext;
    private Ejb3Column[] columns;
    private Ejb3JoinColumn[] joinColumns;

    public ColumnsBuilder(PropertyHolder propertyHolder, Nullability nullability, XProperty property, PropertyData inferredData, EntityBinder entityBinder, MetadataBuildingContext buildingContext) {
        this.propertyHolder = propertyHolder;
        this.nullability = nullability;
        this.property = property;
        this.inferredData = inferredData;
        this.entityBinder = entityBinder;
        this.buildingContext = buildingContext;
    }

    public Ejb3Column[] getColumns() {
        return this.columns;
    }

    public Ejb3JoinColumn[] getJoinColumns() {
        return this.joinColumns;
    }

    public ColumnsBuilder extractMetadata() {
        this.columns = null;
        this.joinColumns = this.buildExplicitJoinColumns(this.property, this.inferredData);
        if (this.property.isAnnotationPresent(Column.class) || this.property.isAnnotationPresent(Formula.class)) {
            Column ann = (Column)this.property.getAnnotation(Column.class);
            Formula formulaAnn = (Formula)this.property.getAnnotation(Formula.class);
            this.columns = Ejb3Column.buildColumnFromAnnotation(new Column[]{ann}, formulaAnn, (Comment)this.property.getAnnotation(Comment.class), this.nullability, this.propertyHolder, this.inferredData, this.entityBinder.getSecondaryTables(), this.buildingContext);
        } else if (this.property.isAnnotationPresent(Columns.class)) {
            Columns anns = (Columns)this.property.getAnnotation(Columns.class);
            this.columns = Ejb3Column.buildColumnFromAnnotation(anns.columns(), null, (Comment)this.property.getAnnotation(Comment.class), this.nullability, this.propertyHolder, this.inferredData, this.entityBinder.getSecondaryTables(), this.buildingContext);
        }
        if (this.joinColumns == null && (this.property.isAnnotationPresent(ManyToOne.class) || this.property.isAnnotationPresent(OneToOne.class))) {
            this.joinColumns = this.buildDefaultJoinColumnsForXToOne(this.property, this.inferredData);
        } else if (this.joinColumns == null && (this.property.isAnnotationPresent(OneToMany.class) || this.property.isAnnotationPresent(ElementCollection.class))) {
            OneToMany oneToMany = (OneToMany)this.property.getAnnotation(OneToMany.class);
            String mappedBy = oneToMany != null ? oneToMany.mappedBy() : "";
            this.joinColumns = Ejb3JoinColumn.buildJoinColumns(null, (Comment)this.property.getAnnotation(Comment.class), mappedBy, this.entityBinder.getSecondaryTables(), this.propertyHolder, this.inferredData.getPropertyName(), this.buildingContext);
        } else if (this.joinColumns == null && this.property.isAnnotationPresent(Any.class)) {
            throw new AnnotationException("@Any requires an explicit @JoinColumn(s): " + BinderHelper.getPath(this.propertyHolder, this.inferredData));
        }
        if (this.columns == null && !this.property.isAnnotationPresent(ManyToMany.class)) {
            this.columns = Ejb3Column.buildColumnFromAnnotation(null, null, (Comment)this.property.getAnnotation(Comment.class), this.nullability, this.propertyHolder, this.inferredData, this.entityBinder.getSecondaryTables(), this.buildingContext);
        }
        if (this.nullability == Nullability.FORCED_NOT_NULL) {
            for (Ejb3Column col : this.columns) {
                col.forceNotNull();
            }
        }
        return this;
    }

    Ejb3JoinColumn[] buildDefaultJoinColumnsForXToOne(XProperty property, PropertyData inferredData) {
        Ejb3JoinColumn[] joinColumns;
        JoinTable joinTableAnn = this.propertyHolder.getJoinTable(property);
        if (joinTableAnn != null) {
            joinColumns = Ejb3JoinColumn.buildJoinColumns(joinTableAnn.inverseJoinColumns(), (Comment)property.getAnnotation(Comment.class), null, this.entityBinder.getSecondaryTables(), this.propertyHolder, inferredData.getPropertyName(), this.buildingContext);
            if (StringHelper.isEmpty(joinTableAnn.name())) {
                throw new AnnotationException("JoinTable.name() on a @ToOne association has to be explicit: " + BinderHelper.getPath(this.propertyHolder, inferredData));
            }
        } else {
            OneToOne oneToOneAnn = (OneToOne)property.getAnnotation(OneToOne.class);
            String mappedBy = oneToOneAnn != null ? oneToOneAnn.mappedBy() : null;
            joinColumns = Ejb3JoinColumn.buildJoinColumns(null, (Comment)property.getAnnotation(Comment.class), mappedBy, this.entityBinder.getSecondaryTables(), this.propertyHolder, inferredData.getPropertyName(), this.buildingContext);
        }
        return joinColumns;
    }

    Ejb3JoinColumn[] buildExplicitJoinColumns(XProperty property, PropertyData inferredData) {
        JoinColumnsOrFormulas joinColumnsOrFormulasAnnotations;
        int length;
        JoinColumns joinColumnAnnotation;
        int length2;
        JoinColumn[] joinColumnAnnotations = null;
        if (property.isAnnotationPresent(JoinColumn.class)) {
            joinColumnAnnotations = new JoinColumn[]{(JoinColumn)property.getAnnotation(JoinColumn.class)};
        } else if (property.isAnnotationPresent(JoinColumns.class) && (length2 = (joinColumnAnnotations = (joinColumnAnnotation = (JoinColumns)property.getAnnotation(JoinColumns.class)).value()).length) == 0) {
            throw new AnnotationException("Cannot bind an empty @JoinColumns");
        }
        if (joinColumnAnnotations != null) {
            return Ejb3JoinColumn.buildJoinColumns(joinColumnAnnotations, (Comment)property.getAnnotation(Comment.class), null, this.entityBinder.getSecondaryTables(), this.propertyHolder, inferredData.getPropertyName(), this.buildingContext);
        }
        JoinColumnOrFormula[] joinColumnOrFormulaAnnotations = null;
        if (property.isAnnotationPresent(JoinColumnOrFormula.class)) {
            joinColumnOrFormulaAnnotations = new JoinColumnOrFormula[]{(JoinColumnOrFormula)property.getAnnotation(JoinColumnOrFormula.class)};
        } else if (property.isAnnotationPresent(JoinColumnsOrFormulas.class) && (length = (joinColumnOrFormulaAnnotations = (joinColumnsOrFormulasAnnotations = (JoinColumnsOrFormulas)property.getAnnotation(JoinColumnsOrFormulas.class)).value()).length) == 0) {
            throw new AnnotationException("Cannot bind an empty @JoinColumnsOrFormulas");
        }
        if (joinColumnOrFormulaAnnotations != null) {
            return Ejb3JoinColumn.buildJoinColumnsOrFormulas(joinColumnOrFormulaAnnotations, null, this.entityBinder.getSecondaryTables(), this.propertyHolder, inferredData.getPropertyName(), this.buildingContext);
        }
        if (property.isAnnotationPresent(JoinFormula.class)) {
            JoinFormula ann = (JoinFormula)property.getAnnotation(JoinFormula.class);
            Ejb3JoinColumn[] ejb3JoinColumns = new Ejb3JoinColumn[]{Ejb3JoinColumn.buildJoinFormula(ann, null, this.entityBinder.getSecondaryTables(), this.propertyHolder, inferredData.getPropertyName(), this.buildingContext)};
            return ejb3JoinColumns;
        }
        return null;
    }

    Ejb3Column[] overrideColumnFromMapperOrMapsIdProperty(boolean isId) {
        Ejb3Column[] result = this.columns;
        PropertyData overridingProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(isId, this.propertyHolder, this.property.getName(), this.buildingContext);
        if (overridingProperty != null) {
            result = this.buildExcplicitOrDefaultJoinColumn(overridingProperty);
        }
        return result;
    }

    Ejb3Column[] buildExcplicitOrDefaultJoinColumn(PropertyData overridingProperty) {
        Ejb3Column[] result = this.buildExplicitJoinColumns(overridingProperty.getProperty(), overridingProperty);
        if (result == null) {
            result = this.buildDefaultJoinColumnsForXToOne(overridingProperty.getProperty(), overridingProperty);
        }
        return result;
    }
}

