/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal;

import org.hibernate.LockMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.loader.plan.build.internal.AbstractEntityGraphVisitationStrategy;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;

public class LoadGraphLoadPlanBuildingStrategy
extends AbstractEntityGraphVisitationStrategy {
    private final RootGraphImplementor<?> rootEntityGraph;

    public LoadGraphLoadPlanBuildingStrategy(SessionFactoryImplementor sessionFactory, LoadQueryInfluencers loadQueryInfluencers, LockMode lockMode) {
        this(sessionFactory, loadQueryInfluencers.getEffectiveEntityGraph().getGraph(), loadQueryInfluencers, lockMode);
        assert (loadQueryInfluencers.getEffectiveEntityGraph().getSemantic() == GraphSemantic.LOAD);
    }

    public LoadGraphLoadPlanBuildingStrategy(SessionFactoryImplementor factory, RootGraphImplementor<?> graph, LoadQueryInfluencers queryInfluencers, LockMode lockMode) {
        super(factory, queryInfluencers, lockMode);
        this.rootEntityGraph = graph;
    }

    @Override
    protected RootGraphImplementor getRootEntityGraph() {
        return this.rootEntityGraph;
    }

    @Override
    protected FetchStrategy resolveImplicitFetchStrategyFromEntityGraph(AssociationAttributeDefinition attributeDefinition) {
        FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan(this.loadQueryInfluencers, this.currentPropertyPath);
        if (fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN) {
            fetchStrategy = this.adjustJoinFetchIfNeeded(attributeDefinition, fetchStrategy);
        }
        return fetchStrategy;
    }
}

