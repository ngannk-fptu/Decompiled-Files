/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Iterator;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.CollectionType;

public class Set
extends Collection {
    @Deprecated
    public Set(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public Set(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        super.validate(mapping);
    }

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public CollectionType getDefaultCollectionType() {
        if (this.isSorted()) {
            return this.getMetadata().getTypeResolver().getTypeFactory().sortedSet(this.getRole(), this.getReferencedPropertyName(), this.getComparator());
        }
        if (this.hasOrder()) {
            return this.getMetadata().getTypeResolver().getTypeFactory().orderedSet(this.getRole(), this.getReferencedPropertyName());
        }
        return this.getMetadata().getTypeResolver().getTypeFactory().set(this.getRole(), this.getReferencedPropertyName());
    }

    @Override
    void createPrimaryKey() {
        if (!this.isOneToMany()) {
            PrimaryKey pk = new PrimaryKey(this.getCollectionTable());
            pk.addColumns(this.getKey().getColumnIterator());
            Iterator<Selectable> iter = this.getElement().getColumnIterator();
            while (iter.hasNext()) {
                Selectable selectable = iter.next();
                if (!(selectable instanceof Column)) continue;
                Column col = (Column)selectable;
                if (!col.isNullable()) {
                    pk.addColumn(col);
                    continue;
                }
                return;
            }
            if (pk.getColumnSpan() != this.getKey().getColumnSpan()) {
                this.getCollectionTable().setPrimaryKey(pk);
            }
        }
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }
}

