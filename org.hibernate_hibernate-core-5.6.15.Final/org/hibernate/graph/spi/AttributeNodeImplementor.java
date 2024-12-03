/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Subgraph
 */
package org.hibernate.graph.spi;

import java.util.Map;
import java.util.function.BiConsumer;
import javax.persistence.Subgraph;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.SubGraph;
import org.hibernate.graph.spi.GraphNodeImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;

public interface AttributeNodeImplementor<J>
extends AttributeNode<J>,
GraphNodeImplementor<J> {
    @Override
    public PersistentAttributeDescriptor<?, J> getAttributeDescriptor();

    public Map<Class<? extends J>, SubGraphImplementor<? extends J>> getSubGraphMap();

    public Map<Class<? extends J>, SubGraphImplementor<? extends J>> getKeySubGraphMap();

    default public void visitSubGraphs(BiConsumer<Class<?>, SubGraphImplementor<?>> consumer) {
        this.getSubGraphMap().forEach(consumer);
    }

    default public void visitKeySubGraphs(BiConsumer<Class<?>, SubGraphImplementor<?>> consumer) {
        this.getKeySubGraphMap().forEach(consumer);
    }

    @Override
    default public Map<Class<? extends J>, SubGraph<? extends J>> getSubGraphs() {
        return this.getSubGraphMap();
    }

    @Override
    default public Map<Class<? extends J>, SubGraph<? extends J>> getKeySubGraphs() {
        return this.getKeySubGraphMap();
    }

    @Override
    default public Map<Class, Subgraph> getSubgraphs() {
        return this.getSubGraphMap();
    }

    @Override
    default public Map<Class, Subgraph> getKeySubgraphs() {
        return this.getKeySubGraphMap();
    }

    @Override
    public AttributeNodeImplementor<J> makeCopy(boolean var1);

    @Override
    public SubGraphImplementor<J> makeSubGraph();

    @Override
    public SubGraphImplementor<J> makeKeySubGraph();

    @Override
    public <S extends J> SubGraphImplementor<S> makeSubGraph(Class<S> var1);

    @Override
    public <S extends J> SubGraphImplementor<S> makeKeySubGraph(Class<S> var1);

    @Override
    public <S extends J> SubGraphImplementor<S> makeSubGraph(ManagedTypeDescriptor<S> var1);

    @Override
    public <S extends J> SubGraphImplementor<S> makeKeySubGraph(ManagedTypeDescriptor<S> var1);

    public void merge(AttributeNodeImplementor<?> var1);
}

