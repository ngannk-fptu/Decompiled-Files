/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractEntityReference;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;

public class EntityReturnImpl
extends AbstractEntityReference
implements EntityReturn,
ExpandingFetchSource {
    public EntityReturnImpl(EntityDefinition entityDefinition, ExpandingQuerySpaces querySpaces) {
        super(querySpaces.makeRootEntityQuerySpace(querySpaces.generateImplicitUid(), entityDefinition.getEntityPersister()), new PropertyPath(entityDefinition.getEntityPersister().getEntityName()));
    }

    @Override
    public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
    }
}

