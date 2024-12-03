/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class ManyToOne
extends ToOne {
    private NotFoundAction notFoundAction;
    private boolean isLogicalOneToOne;

    @Deprecated
    public ManyToOne(MetadataImplementor metadata, Table table) {
        super(metadata, table);
    }

    public ManyToOne(MetadataBuildingContext buildingContext, Table table) {
        super(buildingContext, table);
    }

    @Override
    public Type getType() throws MappingException {
        return this.getMetadata().getTypeResolver().getTypeFactory().manyToOne(this.getReferencedEntityName(), this.referenceToPrimaryKey, this.getReferencedPropertyName(), this.getPropertyName(), this.isLazy(), this.isUnwrapProxy(), this.getNotFoundAction(), this.isLogicalOneToOne);
    }

    @Override
    public void createForeignKey() throws MappingException {
        if (this.referencedPropertyName == null && !this.hasFormula()) {
            this.createForeignKeyOfEntity(((EntityType)this.getType()).getAssociatedEntityName());
        }
    }

    public void createPropertyRefConstraints(Map persistentClasses) {
        if (this.referencedPropertyName != null) {
            PersistentClass pc = (PersistentClass)persistentClasses.get(this.getReferencedEntityName());
            Property property = pc.getReferencedProperty(this.getReferencedPropertyName());
            if (property == null) {
                throw new MappingException("Could not find property " + this.getReferencedPropertyName() + " on " + this.getReferencedEntityName());
            }
            if (!this.hasFormula() && !"none".equals(this.getForeignKeyName())) {
                ArrayList<Column> refColumns = new ArrayList<Column>();
                Iterator iter = property.getColumnIterator();
                while (iter.hasNext()) {
                    Column col = (Column)iter.next();
                    refColumns.add(col);
                }
                ForeignKey fk = this.getTable().createForeignKey(this.getForeignKeyName(), this.getConstraintColumns(), ((EntityType)this.getType()).getAssociatedEntityName(), this.getForeignKeyDefinition(), refColumns);
                fk.setCascadeDeleteEnabled(this.isCascadeDeleteEnabled());
            }
        }
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    public NotFoundAction getNotFoundAction() {
        return this.notFoundAction;
    }

    public void setNotFoundAction(NotFoundAction notFoundAction) {
        this.notFoundAction = notFoundAction;
    }

    public boolean isIgnoreNotFound() {
        return this.notFoundAction == NotFoundAction.IGNORE;
    }

    public void setIgnoreNotFound(boolean ignoreNotFound) {
        this.notFoundAction = ignoreNotFound ? NotFoundAction.IGNORE : null;
    }

    public void markAsLogicalOneToOne() {
        this.isLogicalOneToOne = true;
    }

    public boolean isLogicalOneToOne() {
        return this.isLogicalOneToOne;
    }
}

