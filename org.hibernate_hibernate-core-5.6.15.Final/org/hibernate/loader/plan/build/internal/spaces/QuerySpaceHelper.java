/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
import org.hibernate.loader.plan.build.internal.spaces.JoinHelper;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;

public class QuerySpaceHelper {
    public static final QuerySpaceHelper INSTANCE = new QuerySpaceHelper();

    private QuerySpaceHelper() {
    }

    public ExpandingEntityQuerySpace makeEntityQuerySpace(ExpandingQuerySpace lhsQuerySpace, AssociationAttributeDefinition attribute, String querySpaceUid, FetchStrategy fetchStrategy) {
        EntityType fetchedType = (EntityType)attribute.getType();
        EntityPersister fetchedPersister = attribute.toEntityDefinition().getEntityPersister();
        if (fetchedPersister == null) {
            throw new WalkingException(String.format("Unable to locate EntityPersister [%s] for fetch [%s]", fetchedType.getAssociatedEntityName(), attribute.getName()));
        }
        boolean isMultiTable = ((Queryable)Queryable.class.cast(fetchedPersister)).isMultiTable();
        boolean required = lhsQuerySpace.canJoinsBeRequired() && !isMultiTable && !attribute.isNullable();
        return this.makeEntityQuerySpace(lhsQuerySpace, fetchedPersister, attribute.getName(), (EntityType)attribute.getType(), querySpaceUid, required, this.shouldIncludeJoin(fetchStrategy));
    }

    public ExpandingEntityQuerySpace makeEntityQuerySpace(ExpandingQuerySpace lhsQuerySpace, EntityPersister fetchedPersister, String attributeName, EntityType attributeType, String querySpaceUid, boolean required, boolean shouldIncludeJoin) {
        ExpandingEntityQuerySpace rhs = lhsQuerySpace.getExpandingQuerySpaces().makeEntityQuerySpace(querySpaceUid, fetchedPersister, required);
        if (shouldIncludeJoin) {
            JoinDefinedByMetadata join = JoinHelper.INSTANCE.createEntityJoin(lhsQuerySpace, attributeName, rhs, required, attributeType, fetchedPersister.getFactory());
            lhsQuerySpace.addJoin(join);
        }
        return rhs;
    }

    public ExpandingCompositeQuerySpace makeCompositeQuerySpace(ExpandingQuerySpace lhsQuerySpace, AttributeDefinition attributeDefinition, String querySpaceUid, boolean shouldIncludeJoin) {
        boolean required = lhsQuerySpace.canJoinsBeRequired() && !attributeDefinition.isNullable();
        return this.makeCompositeQuerySpace(lhsQuerySpace, new CompositePropertyMapping((CompositeType)attributeDefinition.getType(), lhsQuerySpace.getPropertyMapping(), attributeDefinition.getName()), attributeDefinition.getName(), (CompositeType)attributeDefinition.getType(), querySpaceUid, required, shouldIncludeJoin);
    }

    public ExpandingCompositeQuerySpace makeCompositeQuerySpace(ExpandingQuerySpace lhsQuerySpace, CompositePropertyMapping compositePropertyMapping, String attributeName, CompositeType attributeType, String querySpaceUid, boolean required, boolean shouldIncludeJoin) {
        ExpandingCompositeQuerySpace rhs = lhsQuerySpace.getExpandingQuerySpaces().makeCompositeQuerySpace(querySpaceUid, compositePropertyMapping, required);
        if (shouldIncludeJoin) {
            JoinDefinedByMetadata join = JoinHelper.INSTANCE.createCompositeJoin(lhsQuerySpace, attributeName, rhs, required, attributeType);
            lhsQuerySpace.addJoin(join);
        }
        return rhs;
    }

    public ExpandingCollectionQuerySpace makeCollectionQuerySpace(ExpandingQuerySpace lhsQuerySpace, AssociationAttributeDefinition attributeDefinition, String querySpaceUid, FetchStrategy fetchStrategy) {
        CollectionType fetchedType = (CollectionType)attributeDefinition.getType();
        CollectionPersister fetchedPersister = attributeDefinition.toCollectionDefinition().getCollectionPersister();
        if (fetchedPersister == null) {
            throw new WalkingException(String.format("Unable to locate CollectionPersister [%s] for fetch [%s]", fetchedType.getRole(), attributeDefinition.getName()));
        }
        boolean required = lhsQuerySpace.canJoinsBeRequired() && !attributeDefinition.isNullable();
        ExpandingCollectionQuerySpace rhs = lhsQuerySpace.getExpandingQuerySpaces().makeCollectionQuerySpace(querySpaceUid, fetchedPersister, required);
        if (this.shouldIncludeJoin(fetchStrategy)) {
            JoinDefinedByMetadata join = JoinHelper.INSTANCE.createCollectionJoin(lhsQuerySpace, attributeDefinition.getName(), rhs, required, (CollectionType)attributeDefinition.getType(), fetchedPersister.getFactory());
            lhsQuerySpace.addJoin(join);
        }
        return rhs;
    }

    public boolean shouldIncludeJoin(FetchStrategy fetchStrategy) {
        return fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN;
    }
}

