/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal;

import org.hibernate.LockMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;

public class CascadeStyleLoadPlanBuildingAssociationVisitationStrategy
extends FetchStyleLoadPlanBuildingAssociationVisitationStrategy {
    private static final FetchStrategy EAGER = new FetchStrategy(FetchTiming.IMMEDIATE, FetchStyle.JOIN);
    private static final FetchStrategy DELAYED = new FetchStrategy(FetchTiming.DELAYED, FetchStyle.SELECT);
    private final CascadingAction cascadeActionToMatch;

    public CascadeStyleLoadPlanBuildingAssociationVisitationStrategy(CascadingAction cascadeActionToMatch, SessionFactoryImplementor sessionFactory, LoadQueryInfluencers loadQueryInfluencers, LockMode lockMode) {
        super(sessionFactory, loadQueryInfluencers, lockMode);
        this.cascadeActionToMatch = cascadeActionToMatch;
    }

    @Override
    protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
        return attributeDefinition.determineCascadeStyle().doCascade(this.cascadeActionToMatch) ? EAGER : DELAYED;
    }
}

