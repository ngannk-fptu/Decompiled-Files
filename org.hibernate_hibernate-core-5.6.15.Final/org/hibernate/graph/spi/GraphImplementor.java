/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.graph.spi;

import java.util.List;
import java.util.function.Consumer;
import javax.persistence.metamodel.Attribute;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.CannotBecomeEntityGraphException;
import org.hibernate.graph.CannotContainSubGraphException;
import org.hibernate.graph.Graph;
import org.hibernate.graph.SubGraph;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.GraphNodeImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;

public interface GraphImplementor<J>
extends Graph<J>,
GraphNodeImplementor<J> {
    public boolean appliesTo(ManagedTypeDescriptor<? super J> var1);

    public boolean appliesTo(Class<? super J> var1);

    public void merge(GraphImplementor<J> ... var1);

    public SessionFactoryImplementor sessionFactory();

    @Override
    public ManagedTypeDescriptor<J> getGraphedType();

    @Override
    public RootGraphImplementor<J> makeRootGraph(String var1, boolean var2) throws CannotBecomeEntityGraphException;

    @Override
    public SubGraphImplementor<J> makeSubGraph(boolean var1);

    @Override
    public GraphImplementor<J> makeCopy(boolean var1);

    default public void visitAttributeNodes(Consumer<AttributeNodeImplementor<?>> consumer) {
        this.getAttributeNodeImplementors().forEach(consumer);
    }

    public AttributeNodeImplementor<?> addAttributeNode(AttributeNodeImplementor<?> var1);

    public List<AttributeNodeImplementor<?>> getAttributeNodeImplementors();

    @Override
    default public List<AttributeNode<?>> getAttributeNodeList() {
        return this.getAttributeNodeImplementors();
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> findAttributeNode(String var1);

    @Override
    public <AJ> AttributeNodeImplementor<AJ> findAttributeNode(PersistentAttributeDescriptor<? extends J, AJ> var1);

    @Override
    default public <AJ> AttributeNodeImplementor<AJ> findAttributeNode(Attribute<? extends J, AJ> attribute) {
        return this.findAttributeNode((PersistentAttributeDescriptor)attribute);
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(String var1) throws CannotContainSubGraphException;

    @Override
    public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(PersistentAttributeDescriptor<? extends J, AJ> var1) throws CannotContainSubGraphException;

    @Override
    default public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(Attribute<? extends J, AJ> attribute) throws CannotContainSubGraphException {
        return this.addAttributeNode((PersistentAttributeDescriptor)attribute);
    }

    default public <AJ> AttributeNodeImplementor<AJ> findOrCreateAttributeNode(String name) {
        return this.findOrCreateAttributeNode((PersistentAttributeDescriptor<? extends J, AJ>)this.getGraphedType().getAttribute(name));
    }

    public <AJ> AttributeNodeImplementor<AJ> findOrCreateAttributeNode(PersistentAttributeDescriptor<? extends J, AJ> var1);

    @Override
    default public <AJ> SubGraphImplementor<AJ> addSubGraph(String attributeName) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attributeName).makeSubGraph();
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addSubGraph(String attributeName, Class<AJ> subType) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attributeName).makeSubGraph(subType);
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addSubGraph(PersistentAttributeDescriptor<? extends J, AJ> attribute) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attribute).makeSubGraph();
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addSubGraph(PersistentAttributeDescriptor<? extends J, AJ> attribute, Class<AJ> subType) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attribute).makeSubGraph(subType);
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addSubGraph(Attribute<? extends J, AJ> attribute) throws CannotContainSubGraphException {
        return this.addSubGraph((PersistentAttributeDescriptor)attribute);
    }

    @Override
    default public <AJ> SubGraph<? extends AJ> addSubGraph(Attribute<? extends J, AJ> attribute, Class<? extends AJ> type) throws CannotContainSubGraphException {
        return this.addSubGraph((PersistentAttributeDescriptor)attribute, type);
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addKeySubGraph(String attributeName) {
        return this.findOrCreateAttributeNode(attributeName).makeKeySubGraph();
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addKeySubGraph(String attributeName, Class<AJ> subtype) {
        return this.findOrCreateAttributeNode(attributeName).makeKeySubGraph(subtype);
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addKeySubGraph(PersistentAttributeDescriptor<? extends J, AJ> attribute) {
        return this.findOrCreateAttributeNode(attribute).makeKeySubGraph();
    }

    @Override
    default public <AJ> SubGraphImplementor<AJ> addKeySubGraph(Attribute<? extends J, AJ> attribute) {
        return this.addKeySubGraph((PersistentAttributeDescriptor)attribute);
    }

    @Override
    default public <AJ> SubGraphImplementor<? extends AJ> addKeySubGraph(PersistentAttributeDescriptor<? extends J, AJ> attribute, Class<? extends AJ> subType) throws CannotContainSubGraphException {
        return this.findOrCreateAttributeNode(attribute).makeKeySubGraph(subType);
    }

    @Override
    default public <AJ> SubGraphImplementor<? extends AJ> addKeySubGraph(Attribute<? extends J, AJ> attribute, Class<? extends AJ> subType) throws CannotContainSubGraphException {
        return this.addKeySubGraph((PersistentAttributeDescriptor)attribute, subType);
    }
}

