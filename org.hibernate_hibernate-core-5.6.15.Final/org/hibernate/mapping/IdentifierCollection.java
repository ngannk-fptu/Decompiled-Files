/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;

public abstract class IdentifierCollection
extends Collection {
    public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
    private KeyValue identifier;

    @Deprecated
    public IdentifierCollection(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public IdentifierCollection(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    public KeyValue getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(KeyValue identifier) {
        this.identifier = identifier;
    }

    @Override
    public final boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isSame(Collection other) {
        return other instanceof IdentifierCollection && this.isSame((IdentifierCollection)other);
    }

    public boolean isSame(IdentifierCollection other) {
        return super.isSame(other) && IdentifierCollection.isSame(this.identifier, other.identifier);
    }

    @Override
    void createPrimaryKey() {
        if (!this.isOneToMany()) {
            PrimaryKey pk = new PrimaryKey(this.getCollectionTable());
            pk.addColumns(this.getIdentifier().getColumnIterator());
            this.getCollectionTable().setPrimaryKey(pk);
        }
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        super.validate(mapping);
        assert (this.getElement() != null) : "IdentifierCollection identifier not bound : " + this.getRole();
        if (!this.getIdentifier().isValid(mapping)) {
            throw new MappingException("collection id mapping has wrong number of columns: " + this.getRole() + " type: " + this.getIdentifier().getType().getName());
        }
    }
}

