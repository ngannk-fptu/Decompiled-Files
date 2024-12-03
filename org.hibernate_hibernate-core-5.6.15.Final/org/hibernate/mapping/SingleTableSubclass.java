/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Iterator;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.Subclass;

public class SingleTableSubclass
extends Subclass {
    public SingleTableSubclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
        super(superclass, metadataBuildingContext);
    }

    @Override
    protected Iterator getNonDuplicatedPropertyIterator() {
        return new JoinedIterator(this.getSuperclass().getUnjoinedPropertyIterator(), this.getUnjoinedPropertyIterator());
    }

    @Override
    protected Iterator getDiscriminatorColumnIterator() {
        if (this.isDiscriminatorInsertable() && !this.getDiscriminator().hasFormula()) {
            return this.getDiscriminator().getColumnIterator();
        }
        return super.getDiscriminatorColumnIterator();
    }

    @Override
    public Object accept(PersistentClassVisitor mv) {
        return mv.accept(this);
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        if (this.getDiscriminator() == null) {
            throw new MappingException("No discriminator found for " + this.getEntityName() + ". Discriminator is needed when 'single-table-per-hierarchy' is used and a class has subclasses");
        }
        super.validate(mapping);
    }
}

