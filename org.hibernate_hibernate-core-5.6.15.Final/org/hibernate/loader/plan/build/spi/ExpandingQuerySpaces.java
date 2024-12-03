/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public interface ExpandingQuerySpaces
extends QuerySpaces {
    public String generateImplicitUid();

    public ExpandingEntityQuerySpace makeRootEntityQuerySpace(String var1, EntityPersister var2);

    public ExpandingEntityQuerySpace makeEntityQuerySpace(String var1, EntityPersister var2, boolean var3);

    public ExpandingCollectionQuerySpace makeRootCollectionQuerySpace(String var1, CollectionPersister var2);

    public ExpandingCollectionQuerySpace makeCollectionQuerySpace(String var1, CollectionPersister var2, boolean var3);

    public ExpandingCompositeQuerySpace makeCompositeQuerySpace(String var1, CompositePropertyMapping var2, boolean var3);

    public SessionFactoryImplementor getSessionFactory();
}

