/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Iterator;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.TableOwner;

public class UnionSubclass
extends Subclass
implements TableOwner {
    private Table table;
    private KeyValue key;

    public UnionSubclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
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
    public Set getSynchronizedTables() {
        return this.synchronizedTables;
    }

    @Override
    protected Iterator getNonDuplicatedPropertyIterator() {
        return this.getPropertyClosureIterator();
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        super.validate(mapping);
        if (this.key != null && !this.key.isValid(mapping)) {
            throw new MappingException("subclass key mapping has wrong number of columns: " + this.getEntityName() + " type: " + this.key.getType().getName());
        }
    }

    @Override
    public Table getIdentityTable() {
        return this.getTable();
    }

    @Override
    public Object accept(PersistentClassVisitor mv) {
        return mv.accept(this);
    }
}

