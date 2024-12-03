/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.internal;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.persister.spi.UnknownPersisterException;

public class StandardPersisterClassResolver
implements PersisterClassResolver {
    @Override
    public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
        if (RootClass.class.isInstance(metadata)) {
            if (metadata.hasSubclasses()) {
                metadata = (PersistentClass)metadata.getDirectSubclasses().next();
            } else {
                return this.singleTableEntityPersister();
            }
        }
        if (JoinedSubclass.class.isInstance(metadata)) {
            return this.joinedSubclassEntityPersister();
        }
        if (UnionSubclass.class.isInstance(metadata)) {
            return this.unionSubclassEntityPersister();
        }
        if (SingleTableSubclass.class.isInstance(metadata)) {
            return this.singleTableEntityPersister();
        }
        throw new UnknownPersisterException("Could not determine persister implementation for entity [" + metadata.getEntityName() + "]");
    }

    public Class<? extends EntityPersister> singleTableEntityPersister() {
        return SingleTableEntityPersister.class;
    }

    public Class<? extends EntityPersister> joinedSubclassEntityPersister() {
        return JoinedSubclassEntityPersister.class;
    }

    public Class<? extends EntityPersister> unionSubclassEntityPersister() {
        return UnionSubclassEntityPersister.class;
    }

    @Override
    public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
        return metadata.isOneToMany() ? this.oneToManyPersister() : this.basicCollectionPersister();
    }

    private Class<OneToManyPersister> oneToManyPersister() {
        return OneToManyPersister.class;
    }

    private Class<BasicCollectionPersister> basicCollectionPersister() {
        return BasicCollectionPersister.class;
    }
}

