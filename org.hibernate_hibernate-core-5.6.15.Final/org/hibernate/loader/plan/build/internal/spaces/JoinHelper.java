/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.build.internal.spaces.JoinImpl;
import org.hibernate.loader.plan.spi.CollectionQuerySpace;
import org.hibernate.loader.plan.spi.CompositeQuerySpace;
import org.hibernate.loader.plan.spi.EntityQuerySpace;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;

public class JoinHelper {
    public static final JoinHelper INSTANCE = new JoinHelper();

    private JoinHelper() {
    }

    public JoinDefinedByMetadata createEntityJoin(QuerySpace leftHandSide, String lhsPropertyName, EntityQuerySpace rightHandSide, boolean rightHandSideRequired, EntityType joinedPropertyType, SessionFactoryImplementor sessionFactory) {
        return new JoinImpl(leftHandSide, lhsPropertyName, rightHandSide, JoinHelper.determineRhsColumnNames(joinedPropertyType, sessionFactory), joinedPropertyType, rightHandSideRequired);
    }

    public JoinDefinedByMetadata createCollectionJoin(QuerySpace leftHandSide, String lhsPropertyName, CollectionQuerySpace rightHandSide, boolean rightHandSideRequired, CollectionType joinedPropertyType, SessionFactoryImplementor sessionFactory) {
        return new JoinImpl(leftHandSide, lhsPropertyName, rightHandSide, joinedPropertyType.getAssociatedJoinable(sessionFactory).getKeyColumnNames(), joinedPropertyType, rightHandSideRequired);
    }

    public JoinDefinedByMetadata createCompositeJoin(QuerySpace leftHandSide, String lhsPropertyName, CompositeQuerySpace rightHandSide, boolean rightHandSideRequired, CompositeType joinedPropertyType) {
        return new JoinImpl(leftHandSide, lhsPropertyName, rightHandSide, null, joinedPropertyType, rightHandSideRequired);
    }

    private static String[] determineRhsColumnNames(EntityType entityType, SessionFactoryImplementor sessionFactory) {
        Joinable persister = entityType.getAssociatedJoinable(sessionFactory);
        return entityType.getRHSUniqueKeyPropertyName() == null ? persister.getKeyColumnNames() : ((PropertyMapping)((Object)persister)).toColumns(entityType.getRHSUniqueKeyPropertyName());
    }
}

