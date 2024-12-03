/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.graph.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.CannotContainSubGraphException;
import org.hibernate.graph.SubGraph;
import org.hibernate.graph.internal.AbstractGraphNode;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.metamodel.model.domain.SimpleDomainType;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.jboss.logging.Logger;

public class AttributeNodeImpl<J>
extends AbstractGraphNode<J>
implements AttributeNodeImplementor<J> {
    private final PersistentAttributeDescriptor<?, J> attribute;
    private Map<Class<? extends J>, SubGraphImplementor<? extends J>> subGraphMap;
    private Map<Class<? extends J>, SubGraphImplementor<? extends J>> keySubGraphMap;
    private static final Logger log = Logger.getLogger(AttributeNodeImpl.class);

    public <X> AttributeNodeImpl(boolean mutable, PersistentAttributeDescriptor<X, J> attribute, SessionFactoryImplementor sessionFactory) {
        this(mutable, attribute, null, null, sessionFactory);
    }

    private AttributeNodeImpl(boolean mutable, PersistentAttributeDescriptor<?, J> attribute, Map<Class<? extends J>, SubGraphImplementor<? extends J>> subGraphMap, Map<Class<? extends J>, SubGraphImplementor<? extends J>> keySubGraphMap, SessionFactoryImplementor sessionFactory) {
        super(mutable, sessionFactory);
        this.attribute = attribute;
        this.subGraphMap = subGraphMap;
        this.keySubGraphMap = keySubGraphMap;
    }

    public String getAttributeName() {
        return this.getAttributeDescriptor().getName();
    }

    @Override
    public PersistentAttributeDescriptor<?, J> getAttributeDescriptor() {
        return this.attribute;
    }

    @Override
    public Map<Class<? extends J>, SubGraphImplementor<? extends J>> getSubGraphMap() {
        if (this.subGraphMap == null) {
            return Collections.emptyMap();
        }
        return this.subGraphMap;
    }

    @Override
    public Map<Class<? extends J>, SubGraphImplementor<? extends J>> getKeySubGraphMap() {
        if (this.keySubGraphMap == null) {
            return Collections.emptyMap();
        }
        return this.keySubGraphMap;
    }

    @Override
    public SubGraphImplementor<J> makeSubGraph() {
        return this.internalMakeSubgraph((Class)null);
    }

    @Override
    public <S extends J> SubGraphImplementor<S> makeSubGraph(Class<S> subtype) {
        return this.internalMakeSubgraph(subtype);
    }

    @Override
    public <S extends J> SubGraphImplementor<S> makeSubGraph(ManagedTypeDescriptor<S> subtype) {
        return this.internalMakeSubgraph(subtype);
    }

    private <S extends J> SubGraphImplementor<S> internalMakeSubgraph(ManagedTypeDescriptor<S> type) {
        assert (type != null);
        log.debugf("Making sub-graph : ( (%s) %s )", (Object)type.getName(), (Object)this.getAttributeName());
        SubGraphImplementor<S> subGraph = type.makeSubGraph();
        this.internalAddSubGraph(type.getJavaType(), subGraph);
        return subGraph;
    }

    private <T extends J> ManagedTypeDescriptor<T> valueGraphTypeAsManaged() {
        SimpleDomainType valueGraphType = this.getAttributeDescriptor().getValueGraphType();
        if (valueGraphType instanceof ManagedTypeDescriptor) {
            return (ManagedTypeDescriptor)valueGraphType;
        }
        throw new CannotContainSubGraphException(String.format(Locale.ROOT, "Attribute [%s] (%s) cannot contain value sub-graphs", this.getAttributeName(), this.getAttributeDescriptor().getPersistentAttributeType().name()));
    }

    private <S extends J> SubGraphImplementor<S> internalMakeSubgraph(Class<S> subType) {
        this.verifyMutability();
        ManagedTypeDescriptor managedType = this.valueGraphTypeAsManaged();
        if (subType == null) {
            subType = managedType.getJavaType();
        }
        return this.internalMakeSubgraph(managedType.findSubType(subType));
    }

    protected <S extends J> void internalAddSubGraph(Class<S> subType, SubGraphImplementor<S> subGraph) {
        SubGraphImplementor<S> previous;
        log.tracef("Adding sub-graph : ( (%s) %s )", (Object)subGraph.getGraphedType().getName(), (Object)this.getAttributeName());
        if (this.subGraphMap == null) {
            this.subGraphMap = new HashMap<Class<? extends J>, SubGraphImplementor<? extends J>>();
        }
        if ((previous = this.subGraphMap.put(subType, subGraph)) != null) {
            log.debugf("Adding sub-graph [%s] over-wrote existing [%s]", subGraph, previous);
        }
    }

    @Override
    public <S extends J> void addSubGraph(Class<S> subType, SubGraph<S> subGraph) {
        this.verifyMutability();
        this.internalAddSubGraph(subType, (SubGraphImplementor)subGraph);
    }

    @Override
    public SubGraphImplementor<J> makeKeySubGraph() {
        return this.internalMakeKeySubgraph((Class)null);
    }

    @Override
    public <S extends J> SubGraphImplementor<S> makeKeySubGraph(Class<S> subtype) {
        return this.internalMakeKeySubgraph(subtype);
    }

    @Override
    public <S extends J> SubGraphImplementor<S> makeKeySubGraph(ManagedTypeDescriptor<S> subtype) {
        return this.internalMakeKeySubgraph(subtype);
    }

    private <S extends J> SubGraphImplementor<S> internalMakeKeySubgraph(ManagedTypeDescriptor<S> type) {
        log.debugf("Making key sub-graph : ( (%s) %s )", (Object)type.getName(), (Object)this.getAttributeName());
        SubGraphImplementor<S> subGraph = type.makeSubGraph();
        this.internalAddKeySubGraph(type.getJavaType(), subGraph);
        return subGraph;
    }

    private <S extends J> SubGraphImplementor<S> internalMakeKeySubgraph(Class<S> type) {
        this.verifyMutability();
        ManagedTypeDescriptor managedType = this.keyGraphTypeAsManaged();
        ManagedTypeDescriptor<Object> subType = type == null ? managedType : managedType.findSubType(type);
        subType.getJavaType();
        return this.internalMakeKeySubgraph(subType);
    }

    protected <S extends J> void internalAddKeySubGraph(Class<S> subType, SubGraph<S> subGraph) {
        SubGraphImplementor previous;
        log.tracef("Adding key sub-graph : ( (%s) %s )", (Object)subType.getName(), (Object)this.getAttributeName());
        if (this.keySubGraphMap == null) {
            this.keySubGraphMap = new HashMap<Class<? extends J>, SubGraphImplementor<? extends J>>();
        }
        if ((previous = this.keySubGraphMap.put(subType, (SubGraphImplementor)subGraph)) != null) {
            log.debugf("Adding key sub-graph [%s] over-wrote existing [%]", subGraph, (Object)previous);
        }
    }

    private <T extends J> ManagedTypeDescriptor<T> keyGraphTypeAsManaged() {
        SimpleDomainType keyGraphType = this.getAttributeDescriptor().getKeyGraphType();
        if (keyGraphType instanceof ManagedTypeDescriptor) {
            return (ManagedTypeDescriptor)keyGraphType;
        }
        throw new CannotContainSubGraphException(String.format(Locale.ROOT, "Attribute [%s#%s] (%s) cannot contain key sub-graphs - %s", this.getAttributeDescriptor().getDeclaringType().getName(), this.getAttributeName(), this.getAttributeDescriptor().getPersistentAttributeType().name(), keyGraphType));
    }

    @Override
    public <S extends J> void addKeySubGraph(Class<S> subType, SubGraph<S> subGraph) {
        this.internalAddKeySubGraph(subType, subGraph);
    }

    @Override
    public AttributeNodeImplementor<J> makeCopy(boolean mutable) {
        return new AttributeNodeImpl<J>(mutable, this.attribute, this.makeMapCopy(mutable, this.subGraphMap), this.makeMapCopy(mutable, this.keySubGraphMap), this.sessionFactory());
    }

    private <S extends J> Map<Class<S>, SubGraphImplementor<S>> makeMapCopy(boolean mutable, Map<Class<S>, SubGraphImplementor<S>> nodeMap) {
        if (nodeMap == null) {
            return null;
        }
        return CollectionHelper.makeCopy(nodeMap, type -> type, subGraph -> subGraph.makeCopy(mutable));
    }

    @Override
    public void merge(AttributeNodeImplementor<?> attributeNode) {
        attributeNode.visitSubGraphs((incomingSubType, incomingGraph) -> {
            SubGraphImplementor<? extends J> existing = null;
            if (this.subGraphMap == null) {
                this.subGraphMap = new HashMap<Class<? extends J>, SubGraphImplementor<? extends J>>();
            } else {
                existing = this.subGraphMap.get(incomingSubType);
            }
            if (existing != null) {
                existing.merge(new GraphImplementor[]{incomingGraph});
            } else {
                this.internalAddSubGraph((Class)incomingSubType, (SubGraphImplementor)incomingGraph.makeCopy(true));
            }
        });
        attributeNode.visitKeySubGraphs((incomingSubType, incomingGraph) -> {
            SubGraphImplementor<? extends J> existing = null;
            if (this.keySubGraphMap == null) {
                this.keySubGraphMap = new HashMap<Class<? extends J>, SubGraphImplementor<? extends J>>();
            } else {
                existing = this.keySubGraphMap.get(incomingSubType);
            }
            if (existing != null) {
                existing.merge(new GraphImplementor[]{incomingGraph});
            } else {
                this.internalAddKeySubGraph((Class)incomingSubType, (SubGraph)incomingGraph.makeCopy(true));
            }
        });
    }
}

