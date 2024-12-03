/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractCollectionReference;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.persister.walking.spi.CollectionDefinition;

public class CollectionReturnImpl
extends AbstractCollectionReference
implements CollectionReturn {
    public CollectionReturnImpl(CollectionDefinition collectionDefinition, ExpandingQuerySpaces querySpaces) {
        super(querySpaces.makeRootCollectionQuerySpace(querySpaces.generateImplicitUid(), collectionDefinition.getCollectionPersister()), new PropertyPath("[" + collectionDefinition.getCollectionPersister().getRole() + "]"), true);
    }
}

