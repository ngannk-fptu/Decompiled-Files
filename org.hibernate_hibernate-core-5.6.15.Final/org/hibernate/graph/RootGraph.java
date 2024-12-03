/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeNode
 *  javax.persistence.EntityGraph
 *  javax.persistence.Subgraph
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.graph;

import java.util.List;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.Subgraph;
import javax.persistence.metamodel.Attribute;
import org.hibernate.graph.Graph;
import org.hibernate.graph.SubGraph;

public interface RootGraph<J>
extends Graph<J>,
EntityGraph<J> {
    public boolean appliesTo(String var1);

    public boolean appliesTo(Class var1);

    @Override
    public RootGraph<J> makeRootGraph(String var1, boolean var2);

    @Override
    public SubGraph<J> makeSubGraph(boolean var1);

    public <T1> SubGraph<? extends T1> addSubclassSubgraph(Class<? extends T1> var1);

    default public List<AttributeNode<?>> getAttributeNodes() {
        return this.getAttributeNodeList();
    }

    default public void addAttributeNodes(String ... names) {
        if (names == null) {
            return;
        }
        for (String name : names) {
            this.addAttributeNode(name);
        }
    }

    default public void addAttributeNodes(Attribute<J, ?> ... attributes) {
        if (attributes == null) {
            return;
        }
        for (Attribute<J, ?> attribute : attributes) {
            this.addAttributeNode(attribute);
        }
    }

    default public <X> SubGraph<X> addSubgraph(Attribute<J, X> attribute) {
        return this.addSubGraph(attribute);
    }

    default public <X> SubGraph<? extends X> addSubgraph(Attribute<J, X> attribute, Class<? extends X> type) {
        return this.addSubGraph(attribute, type);
    }

    default public <X> SubGraph<X> addSubgraph(String name) {
        return this.addSubGraph(name);
    }

    default public <X> SubGraph<X> addSubgraph(String name, Class<X> type) {
        return this.addSubGraph(name, type);
    }

    default public <X> SubGraph<X> addKeySubgraph(Attribute<J, X> attribute) {
        return this.addKeySubGraph(attribute);
    }

    default public <X> SubGraph<? extends X> addKeySubgraph(Attribute<J, X> attribute, Class<? extends X> type) {
        return this.addKeySubGraph(attribute, type);
    }

    default public <X> SubGraph<X> addKeySubgraph(String name) {
        return this.addKeySubGraph(name);
    }

    default public <X> Subgraph<X> addKeySubgraph(String name, Class<X> type) {
        return this.addKeySubGraph(name, type);
    }
}

