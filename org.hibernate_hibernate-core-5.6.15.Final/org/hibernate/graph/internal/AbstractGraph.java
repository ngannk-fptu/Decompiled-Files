/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.CannotBecomeEntityGraphException;
import org.hibernate.graph.CannotContainSubGraphException;
import org.hibernate.graph.internal.AbstractGraphNode;
import org.hibernate.graph.internal.AttributeNodeImpl;
import org.hibernate.graph.internal.RootGraphImpl;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;

public abstract class AbstractGraph<J>
extends AbstractGraphNode<J>
implements GraphImplementor<J> {
    private final ManagedTypeDescriptor<J> managedType;
    private Map<PersistentAttributeDescriptor<?, ?>, AttributeNodeImplementor<?>> attrNodeMap;

    public AbstractGraph(ManagedTypeDescriptor<J> managedType, boolean mutable, SessionFactoryImplementor sessionFactory) {
        super(mutable, sessionFactory);
        this.managedType = managedType;
    }

    protected AbstractGraph(boolean mutable, GraphImplementor<J> original) {
        this((ManagedTypeDescriptor<J>)original.getGraphedType(), mutable, original.sessionFactory());
        this.attrNodeMap = CollectionHelper.concurrentMap(original.getAttributeNodeList().size());
        original.visitAttributeNodes(node -> this.attrNodeMap.put((PersistentAttributeDescriptor<?, ?>)node.getAttributeDescriptor(), (AttributeNodeImplementor<?>)node.makeCopy(mutable)));
    }

    @Override
    public SessionFactoryImplementor sessionFactory() {
        return super.sessionFactory();
    }

    @Override
    public ManagedTypeDescriptor<J> getGraphedType() {
        return this.managedType;
    }

    @Override
    public RootGraphImplementor<J> makeRootGraph(String name, boolean mutable) {
        if (this.getGraphedType() instanceof EntityTypeDescriptor) {
            return new RootGraphImpl(name, mutable, this);
        }
        throw new CannotBecomeEntityGraphException("Cannot transform Graph to RootGraph - " + this.getGraphedType() + " is not an EntityType");
    }

    @Override
    public void merge(GraphImplementor<J> ... others) {
        if (others == null) {
            return;
        }
        for (GraphImplementor<J> other : others) {
            for (AttributeNodeImplementor<?> attributeNode : other.getAttributeNodeImplementors()) {
                AttributeNodeImplementor localAttributeNode = this.findAttributeNode((PersistentAttributeDescriptor)attributeNode.getAttributeDescriptor());
                if (localAttributeNode != null) {
                    localAttributeNode.merge(attributeNode);
                    continue;
                }
                this.addAttributeNode((AttributeNodeImplementor<?>)attributeNode.makeCopy(true));
            }
        }
    }

    @Override
    public AttributeNodeImplementor<?> addAttributeNode(AttributeNodeImplementor<?> incomingAttributeNode) {
        this.verifyMutability();
        AttributeNodeImplementor<?> attributeNode = null;
        if (this.attrNodeMap == null) {
            this.attrNodeMap = new HashMap();
        } else {
            attributeNode = this.attrNodeMap.get(incomingAttributeNode.getAttributeDescriptor());
        }
        if (attributeNode == null) {
            attributeNode = incomingAttributeNode;
            this.attrNodeMap.put((PersistentAttributeDescriptor<?, ?>)incomingAttributeNode.getAttributeDescriptor(), attributeNode);
        } else {
            AttributeNodeImplementor<?> attributeNodeFinal = attributeNode;
            incomingAttributeNode.visitSubGraphs((subType, subGraph) -> attributeNodeFinal.addSubGraph(subType, subGraph));
        }
        return attributeNode;
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> findAttributeNode(String attributeName) {
        PersistentAttributeDescriptor<J, ?> attribute = this.managedType.findAttribute(attributeName);
        if (attribute == null) {
            return null;
        }
        return this.findAttributeNode(attribute);
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> findAttributeNode(PersistentAttributeDescriptor<? extends J, AJ> attribute) {
        if (this.attrNodeMap == null) {
            return null;
        }
        return this.attrNodeMap.get(attribute);
    }

    @Override
    public List<AttributeNode<?>> getGraphAttributeNodes() {
        return this.getAttributeNodeImplementors();
    }

    @Override
    public List<AttributeNodeImplementor<?>> getAttributeNodeImplementors() {
        return this.attrNodeMap == null ? Collections.emptyList() : new ArrayList(this.attrNodeMap.values());
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(String attributeName) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attributeName);
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(PersistentAttributeDescriptor<? extends J, AJ> attribute) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attribute);
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> findOrCreateAttributeNode(PersistentAttributeDescriptor<? extends J, AJ> attribute) {
        this.verifyMutability();
        AttributeNodeImplementor<?> attrNode = null;
        if (this.attrNodeMap == null) {
            this.attrNodeMap = new HashMap();
        } else {
            attrNode = this.attrNodeMap.get(attribute);
        }
        if (attrNode == null) {
            attrNode = new AttributeNodeImpl<AJ>(this.isMutable(), attribute, this.sessionFactory());
            this.attrNodeMap.put(attribute, attrNode);
        }
        return attrNode;
    }
}

