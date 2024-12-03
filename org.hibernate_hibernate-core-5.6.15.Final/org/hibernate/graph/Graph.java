/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.ManagedType
 */
package org.hibernate.graph;

import java.util.List;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.CannotBecomeEntityGraphException;
import org.hibernate.graph.CannotContainSubGraphException;
import org.hibernate.graph.GraphNode;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.SubGraph;

public interface Graph<J>
extends GraphNode<J> {
    public ManagedType<J> getGraphedType();

    public RootGraph<J> makeRootGraph(String var1, boolean var2) throws CannotBecomeEntityGraphException;

    public SubGraph<J> makeSubGraph(boolean var1);

    @Override
    public Graph<J> makeCopy(boolean var1);

    public List<AttributeNode<?>> getGraphAttributeNodes();

    public <AJ> AttributeNode<AJ> findAttributeNode(String var1);

    public <AJ> AttributeNode<AJ> findAttributeNode(Attribute<? extends J, AJ> var1);

    public List<AttributeNode<?>> getAttributeNodeList();

    public <AJ> AttributeNode<AJ> addAttributeNode(String var1);

    public <AJ> AttributeNode<AJ> addAttributeNode(Attribute<? extends J, AJ> var1);

    public <AJ> SubGraph<AJ> addSubGraph(String var1) throws CannotContainSubGraphException;

    public <AJ> SubGraph<AJ> addSubGraph(String var1, Class<AJ> var2) throws CannotContainSubGraphException;

    public <AJ> SubGraph<AJ> addSubGraph(Attribute<? extends J, AJ> var1) throws CannotContainSubGraphException;

    public <AJ> SubGraph<? extends AJ> addSubGraph(Attribute<? extends J, AJ> var1, Class<? extends AJ> var2) throws CannotContainSubGraphException;

    public <AJ> SubGraph<AJ> addKeySubGraph(String var1) throws CannotContainSubGraphException;

    public <AJ> SubGraph<AJ> addKeySubGraph(String var1, Class<AJ> var2) throws CannotContainSubGraphException;

    public <AJ> SubGraph<AJ> addKeySubGraph(Attribute<? extends J, AJ> var1) throws CannotContainSubGraphException;

    public <AJ> SubGraph<? extends AJ> addKeySubGraph(Attribute<? extends J, AJ> var1, Class<? extends AJ> var2) throws CannotContainSubGraphException;
}

