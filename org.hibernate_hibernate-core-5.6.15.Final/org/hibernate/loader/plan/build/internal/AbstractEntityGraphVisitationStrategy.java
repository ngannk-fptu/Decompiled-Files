/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Subgraph
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.build.internal;

import java.util.Map;
import javax.persistence.Subgraph;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.internal.util.collections.StandardStack;
import org.hibernate.loader.plan.build.internal.AbstractLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.internal.LoadPlanImpl;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.CollectionElementDefinition;
import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.jboss.logging.Logger;

public abstract class AbstractEntityGraphVisitationStrategy
extends AbstractLoadPlanBuildingAssociationVisitationStrategy {
    private static final Logger LOG = CoreLogging.logger(AbstractEntityGraphVisitationStrategy.class);
    protected static final FetchStrategy DEFAULT_EAGER = new FetchStrategy(FetchTiming.IMMEDIATE, FetchStyle.JOIN);
    protected static final FetchStrategy DEFAULT_LAZY = new FetchStrategy(FetchTiming.DELAYED, FetchStyle.SELECT);
    protected final LoadQueryInfluencers loadQueryInfluencers;
    private final Stack<GraphImplementor> graphStack = new StandardStack<GraphImplementor>();
    private final Stack<AttributeNodeImplementor> attributeStack = new StandardStack<AttributeNodeImplementor>();
    private EntityReturn rootEntityReturn;
    private final LockMode lockMode;

    protected AbstractEntityGraphVisitationStrategy(SessionFactoryImplementor sessionFactory, LoadQueryInfluencers loadQueryInfluencers, LockMode lockMode) {
        super(sessionFactory);
        this.loadQueryInfluencers = loadQueryInfluencers;
        this.lockMode = lockMode;
    }

    @Override
    public void start() {
        super.start();
        this.graphStack.push(this.getRootEntityGraph());
    }

    @Override
    public void finish() {
        super.finish();
        this.graphStack.pop();
        if (!this.graphStack.isEmpty() || !this.attributeStack.isEmpty()) {
            throw new WalkingException("Internal stack error [" + this.graphStack.depth() + ", " + this.attributeStack.depth() + "]");
        }
    }

    @Override
    public boolean startingAttribute(AttributeDefinition attributeDefinition) {
        String attrName;
        AttributeNode attributeNode = null;
        GraphImplementor subGraphNode = null;
        GraphImplementor currentGraph = this.graphStack.getCurrent();
        if (currentGraph != null && (attributeNode = currentGraph.findAttributeNode(attrName = attributeDefinition.getName())) != null) {
            Map<Class, Subgraph> subGraphs = attributeNode.getSubgraphs();
            Class javaType = attributeDefinition.getType().getReturnedClass();
            if (!subGraphs.isEmpty() && subGraphs.containsKey(javaType)) {
                subGraphNode = (GraphImplementor)subGraphs.get(javaType);
            }
        }
        this.attributeStack.push((AttributeNodeImplementor)attributeNode);
        this.graphStack.push(subGraphNode);
        return super.startingAttribute(attributeDefinition);
    }

    @Override
    public void finishingAttribute(AttributeDefinition attributeDefinition) {
        this.attributeStack.pop();
        this.graphStack.pop();
        super.finishingAttribute(attributeDefinition);
    }

    @Override
    public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
        AttributeNodeImplementor attributeNode = this.attributeStack.getCurrent();
        GraphImplementor subGraphNode = null;
        if (attributeNode != null) {
            Class javaType = elementDefinition.getType().getReturnedClass();
            Map<Class, Subgraph> subGraphs = attributeNode.getSubgraphs();
            if (!subGraphs.isEmpty() && subGraphs.containsKey(javaType)) {
                subGraphNode = (GraphImplementor)subGraphs.get(javaType);
            }
        }
        this.graphStack.push(subGraphNode);
        super.startingCollectionElements(elementDefinition);
    }

    @Override
    public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
        super.finishingCollectionElements(elementDefinition);
        this.graphStack.pop();
    }

    @Override
    public void startingCollectionIndex(CollectionIndexDefinition indexDefinition) {
        AttributeNodeImplementor attributeNode = this.attributeStack.getCurrent();
        GraphImplementor subGraphNode = null;
        if (attributeNode != null) {
            Map<Class, Subgraph> subGraphs = attributeNode.getKeySubgraphs();
            Class javaType = indexDefinition.getType().getReturnedClass();
            if (!subGraphs.isEmpty() && subGraphs.containsKey(javaType)) {
                subGraphNode = (GraphImplementor)subGraphs.get(javaType);
            }
        }
        this.graphStack.push(subGraphNode);
        super.startingCollectionIndex(indexDefinition);
    }

    @Override
    public void finishingCollectionIndex(CollectionIndexDefinition indexDefinition) {
        super.finishingCollectionIndex(indexDefinition);
        this.graphStack.pop();
    }

    @Override
    protected boolean supportsRootCollectionReturns() {
        return false;
    }

    @Override
    protected void addRootReturn(Return rootReturn) {
        if (this.rootEntityReturn != null) {
            throw new HibernateException("Root return already identified");
        }
        if (!(rootReturn instanceof EntityReturn)) {
            throw new HibernateException("Load entity graph only supports EntityReturn");
        }
        this.rootEntityReturn = (EntityReturn)rootReturn;
    }

    @Override
    protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
        AttributeNodeImplementor currentAttrNode = this.attributeStack.getCurrent();
        return currentAttrNode != null ? DEFAULT_EAGER : this.resolveImplicitFetchStrategyFromEntityGraph(attributeDefinition);
    }

    protected abstract FetchStrategy resolveImplicitFetchStrategyFromEntityGraph(AssociationAttributeDefinition var1);

    protected FetchStrategy adjustJoinFetchIfNeeded(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
        if (this.lockMode.greaterThan(LockMode.READ)) {
            return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
        }
        Integer maxFetchDepth = this.sessionFactory().getSettings().getMaximumFetchDepth();
        if (maxFetchDepth != null && this.currentDepth() > maxFetchDepth) {
            return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
        }
        if (attributeDefinition.getType().isCollectionType() && this.isTooManyCollections()) {
            return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
        }
        return fetchStrategy;
    }

    @Override
    public LoadPlan buildLoadPlan() {
        LOG.debug((Object)"Building LoadPlan...");
        return new LoadPlanImpl(this.rootEntityReturn, (QuerySpaces)this.getQuerySpaces());
    }

    protected abstract GraphImplementor getRootEntityGraph();

    @Override
    public void foundCircularAssociation(AssociationAttributeDefinition attributeDefinition) {
        FetchStrategy fetchStrategy = this.determineFetchStrategy(attributeDefinition);
        if (fetchStrategy.getStyle() != FetchStyle.JOIN) {
            return;
        }
        GraphImplementor currentGraph = this.graphStack.getCurrent();
        if (attributeDefinition.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.COLLECTION && currentGraph != null && currentGraph.findAttributeNode(attributeDefinition.getName()) != null) {
            this.currentSource().buildCollectionAttributeFetch(attributeDefinition, fetchStrategy);
        }
        super.foundCircularAssociation(attributeDefinition);
    }
}

