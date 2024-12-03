/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeNode
 *  javax.persistence.Subgraph
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.graph;

import java.util.List;
import javax.persistence.AttributeNode;
import javax.persistence.Subgraph;
import javax.persistence.metamodel.Attribute;
import org.hibernate.graph.Graph;

public interface SubGraph<J>
extends Graph<J>,
Subgraph<J> {
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

    default public void addAttributeNodes(Attribute<J, ?> ... attribute) {
        if (attribute == null) {
            return;
        }
        for (Attribute<J, ?> node : attribute) {
            this.addAttributeNode(node);
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

    default public <X> SubGraph<X> addKeySubgraph(String name, Class<X> type) {
        return this.addKeySubGraph(name, type);
    }

    default public Class<J> getClassType() {
        return this.getGraphedType().getJavaType();
    }
}

