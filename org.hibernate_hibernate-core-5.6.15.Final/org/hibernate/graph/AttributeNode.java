/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeNode
 *  javax.persistence.Subgraph
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.graph;

import java.util.Map;
import javax.persistence.Subgraph;
import javax.persistence.metamodel.Attribute;
import org.hibernate.graph.GraphNode;
import org.hibernate.graph.SubGraph;

public interface AttributeNode<J>
extends GraphNode<J>,
javax.persistence.AttributeNode<J> {
    public Attribute<?, J> getAttributeDescriptor();

    public Map<Class<? extends J>, SubGraph<? extends J>> getSubGraphs();

    public Map<Class<? extends J>, SubGraph<? extends J>> getKeySubGraphs();

    default public Map<Class, Subgraph> getSubgraphs() {
        return this.getSubGraphs();
    }

    default public Map<Class, Subgraph> getKeySubgraphs() {
        return this.getKeySubGraphs();
    }

    public <S extends J> void addSubGraph(Class<S> var1, SubGraph<S> var2);

    public <S extends J> void addKeySubGraph(Class<S> var1, SubGraph<S> var2);

    public SubGraph<J> makeSubGraph();

    public SubGraph<J> makeKeySubGraph();

    public <S extends J> SubGraph<S> makeSubGraph(Class<S> var1);

    public <S extends J> SubGraph<S> makeKeySubGraph(Class<S> var1);
}

