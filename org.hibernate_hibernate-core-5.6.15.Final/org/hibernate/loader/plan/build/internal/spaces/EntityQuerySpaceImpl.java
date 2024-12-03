/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.build.internal.spaces.AbstractExpandingSourceQuerySpace;
import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
import org.hibernate.loader.plan.build.internal.spaces.JoinHelper;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.CompositeType;

public class EntityQuerySpaceImpl
extends AbstractExpandingSourceQuerySpace
implements ExpandingEntityQuerySpace {
    private final EntityPersister persister;

    public EntityQuerySpaceImpl(EntityPersister persister, String uid, ExpandingQuerySpaces querySpaces, boolean canJoinsBeRequired) {
        super(uid, QuerySpace.Disposition.ENTITY, querySpaces, canJoinsBeRequired);
        this.persister = persister;
    }

    @Override
    protected SessionFactoryImplementor sessionFactory() {
        return super.sessionFactory();
    }

    @Override
    public PropertyMapping getPropertyMapping() {
        return (PropertyMapping)((Object)this.persister);
    }

    @Override
    public String[] toAliasedColumns(String alias, String propertyName) {
        return this.getPropertyMapping().toColumns(alias, propertyName);
    }

    @Override
    public EntityPersister getEntityPersister() {
        return this.persister;
    }

    @Override
    public ExpandingCompositeQuerySpace makeCompositeIdentifierQuerySpace() {
        String compositeQuerySpaceUid = this.getUid() + "-id";
        ExpandingCompositeQuerySpace rhs = this.getExpandingQuerySpaces().makeCompositeQuerySpace(compositeQuerySpaceUid, new CompositePropertyMapping((CompositeType)this.getEntityPersister().getIdentifierType(), (PropertyMapping)((Object)this.getEntityPersister()), this.getEntityPersister().getIdentifierPropertyName()), this.canJoinsBeRequired());
        JoinDefinedByMetadata join = JoinHelper.INSTANCE.createCompositeJoin(this, "id", rhs, this.canJoinsBeRequired(), (CompositeType)this.persister.getIdentifierType());
        this.internalGetJoins().add(join);
        return rhs;
    }
}

