/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Iterator;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.TableOwner;

public class JoinedSubclass
extends Subclass
implements TableOwner {
    private Table table;
    private KeyValue key;

    public JoinedSubclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
        super(superclass, metadataBuildingContext);
    }

    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
        this.getSuperclass().addSubclassTable(table);
    }

    @Override
    public KeyValue getKey() {
        return this.key;
    }

    public void setKey(KeyValue key) {
        this.key = key;
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        super.validate(mapping);
        if (this.key != null && !this.key.isValid(mapping)) {
            throw new MappingException("subclass key mapping has wrong number of columns: " + this.getEntityName() + " type: " + this.key.getType().getName());
        }
    }

    @Override
    public Iterator getReferenceablePropertyIterator() {
        return this.getPropertyIterator();
    }

    @Override
    public Object accept(PersistentClassVisitor mv) {
        return mv.accept(this);
    }
}

