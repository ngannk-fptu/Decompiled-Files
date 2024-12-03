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
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Value;

public abstract class IndexedCollection
extends Collection {
    public static final String DEFAULT_INDEX_COLUMN_NAME = "idx";
    private Value index;

    @Deprecated
    public IndexedCollection(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public IndexedCollection(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    public Value getIndex() {
        return this.index;
    }

    public void setIndex(Value index) {
        this.index = index;
    }

    @Override
    public final boolean isIndexed() {
        return true;
    }

    @Override
    public boolean isSame(Collection other) {
        return other instanceof IndexedCollection && this.isSame((IndexedCollection)other);
    }

    public boolean isSame(IndexedCollection other) {
        return super.isSame(other) && IndexedCollection.isSame(this.index, other.index);
    }

    @Override
    void createPrimaryKey() {
        if (!this.isOneToMany()) {
            PrimaryKey pk = new PrimaryKey(this.getCollectionTable());
            pk.addColumns(this.getKey().getColumnIterator());
            boolean isFormula = false;
            Iterator<Selectable> iter = this.getIndex().getColumnIterator();
            while (iter.hasNext()) {
                if (!iter.next().isFormula()) continue;
                isFormula = true;
            }
            if (isFormula) {
                pk.addColumns(this.getElement().getColumnIterator());
            } else {
                pk.addColumns(this.getIndex().getColumnIterator());
            }
            this.getCollectionTable().setPrimaryKey(pk);
        }
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        super.validate(mapping);
        assert (this.getElement() != null) : "IndexedCollection index not bound : " + this.getRole();
        if (!this.getIndex().isValid(mapping)) {
            throw new MappingException("collection index mapping has wrong number of columns: " + this.getRole() + " type: " + this.getIndex().getType().getName());
        }
    }

    public boolean isList() {
        return false;
    }
}

