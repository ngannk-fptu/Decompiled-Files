/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

public class OneToOne
extends ToOne {
    private boolean constrained;
    private ForeignKeyDirection foreignKeyType;
    private KeyValue identifier;
    private String propertyName;
    private String entityName;

    @Deprecated
    public OneToOne(MetadataImplementor metadata, Table table, PersistentClass owner) throws MappingException {
        super(metadata, table);
        this.identifier = owner.getKey();
        this.entityName = owner.getEntityName();
    }

    public OneToOne(MetadataBuildingContext buildingContext, Table table, PersistentClass owner) throws MappingException {
        super(buildingContext, table);
        this.identifier = owner.getKey();
        this.entityName = owner.getEntityName();
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName == null ? null : propertyName.intern();
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String propertyName) {
        this.entityName = this.entityName == null ? null : this.entityName.intern();
    }

    @Override
    public Type getType() throws MappingException {
        if (this.getColumnIterator().hasNext()) {
            return this.getMetadata().getTypeResolver().getTypeFactory().specialOneToOne(this.getReferencedEntityName(), this.foreignKeyType, this.referenceToPrimaryKey, this.referencedPropertyName, this.isLazy(), this.isUnwrapProxy(), this.entityName, this.propertyName, this.constrained);
        }
        return this.getMetadata().getTypeResolver().getTypeFactory().oneToOne(this.getReferencedEntityName(), this.foreignKeyType, this.referenceToPrimaryKey, this.referencedPropertyName, this.isLazy(), this.isUnwrapProxy(), this.entityName, this.propertyName, this.constrained);
    }

    @Override
    public void createForeignKey() throws MappingException {
        if (this.constrained && this.referencedPropertyName == null) {
            this.createForeignKeyOfEntity(((EntityType)this.getType()).getAssociatedEntityName());
        }
    }

    @Override
    public List getConstraintColumns() {
        ArrayList<Selectable> list = new ArrayList<Selectable>();
        Iterator<Selectable> iter = this.identifier.getColumnIterator();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    public boolean isConstrained() {
        return this.constrained;
    }

    public ForeignKeyDirection getForeignKeyType() {
        return this.foreignKeyType;
    }

    public KeyValue getIdentifier() {
        return this.identifier;
    }

    public void setConstrained(boolean constrained) {
        this.constrained = constrained;
    }

    public void setForeignKeyType(ForeignKeyDirection foreignKeyType) {
        this.foreignKeyType = foreignKeyType;
    }

    public void setIdentifier(KeyValue identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean isNullable() {
        return !this.constrained;
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean isSame(ToOne other) {
        return other instanceof OneToOne && this.isSame((OneToOne)other);
    }

    public boolean isSame(OneToOne other) {
        return super.isSame(other) && Objects.equals((Object)this.foreignKeyType, (Object)other.foreignKeyType) && OneToOne.isSame(this.identifier, other.identifier) && Objects.equals(this.propertyName, other.propertyName) && Objects.equals(this.entityName, other.entityName) && this.constrained == other.constrained;
    }
}

