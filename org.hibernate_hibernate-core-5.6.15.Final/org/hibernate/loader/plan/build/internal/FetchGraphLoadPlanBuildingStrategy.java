/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal;

import org.hibernate.LockMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.loader.plan.build.internal.AbstractEntityGraphVisitationStrategy;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;

public class FetchGraphLoadPlanBuildingStrategy
extends AbstractEntityGraphVisitationStrategy {
    private final RootGraphImplementor rootEntityGraph;

    public FetchGraphLoadPlanBuildingStrategy(SessionFactoryImplementor sessionFactory, LoadQueryInfluencers loadQueryInfluencers, LockMode lockMode) {
        this(sessionFactory, loadQueryInfluencers.getEffectiveEntityGraph().getGraph(), loadQueryInfluencers, lockMode);
        assert (loadQueryInfluencers.getEffectiveEntityGraph().getSemantic() == GraphSemantic.FETCH);
    }

    public FetchGraphLoadPlanBuildingStrategy(SessionFactoryImplementor sessionFactory, RootGraphImplementor graph, LoadQueryInfluencers loadQueryInfluencers, LockMode lockMode) {
        super(sessionFactory, loadQueryInfluencers, lockMode);
        this.rootEntityGraph = graph;
    }

    @Override
    protected RootGraphImplementor getRootEntityGraph() {
        return this.rootEntityGraph;
    }

    @Override
    protected FetchStrategy resolveImplicitFetchStrategyFromEntityGraph(AssociationAttributeDefinition attributeDefinition) {
        return DEFAULT_LAZY;
    }
}

