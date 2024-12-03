/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.ri.QName
 *  org.apache.commons.jxpath.ri.compiler.NodeTest
 *  org.apache.commons.jxpath.ri.compiler.NodeTypeTest
 *  org.apache.commons.jxpath.ri.model.NodeIterator
 *  org.apache.commons.jxpath.ri.model.NodePointer
 */
package org.apache.commons.configuration2.tree.xpath;

import java.util.Locale;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodeIteratorAttribute;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodeIteratorChildren;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

class ConfigurationNodePointer<T>
extends NodePointer {
    private static final long serialVersionUID = -1087475639680007713L;
    private final NodeHandler<T> handler;
    private final T node;

    public ConfigurationNodePointer(T node, Locale locale, NodeHandler<T> handler) {
        super(null, locale);
        this.node = node;
        this.handler = handler;
    }

    public ConfigurationNodePointer(ConfigurationNodePointer<T> parent, T node, NodeHandler<T> handler) {
        super(parent);
        this.node = node;
        this.handler = handler;
    }

    public boolean isLeaf() {
        return this.getNodeHandler().getChildrenCount(this.node, null) < 1;
    }

    public boolean isCollection() {
        return false;
    }

    public int getLength() {
        return 1;
    }

    public boolean isAttribute() {
        return false;
    }

    public QName getName() {
        return new QName(null, this.getNodeHandler().nodeName(this.node));
    }

    public Object getBaseValue() {
        return this.node;
    }

    public Object getImmediateNode() {
        return this.node;
    }

    public Object getValue() {
        return this.getNodeHandler().getValue(this.node);
    }

    public void setValue(Object value) {
        throw new UnsupportedOperationException("Node value cannot be set!");
    }

    public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2) {
        Object node1 = pointer1.getBaseValue();
        Object node2 = pointer2.getBaseValue();
        for (T child : this.getNodeHandler().getChildren(this.node)) {
            if (child == node1) {
                return -1;
            }
            if (child != node2) continue;
            return 1;
        }
        return 0;
    }

    public NodeIterator attributeIterator(QName name) {
        return new ConfigurationNodeIteratorAttribute(this, name);
    }

    public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
        return new ConfigurationNodeIteratorChildren<T>(this, test, reverse, this.castPointer(startWith));
    }

    public boolean testNode(NodeTest test) {
        if (test instanceof NodeTypeTest && ((NodeTypeTest)test).getNodeType() == 2) {
            return true;
        }
        return super.testNode(test);
    }

    public NodeHandler<T> getNodeHandler() {
        return this.handler;
    }

    public T getConfigurationNode() {
        return this.node;
    }

    private ConfigurationNodePointer<T> castPointer(NodePointer p) {
        ConfigurationNodePointer result = (ConfigurationNodePointer)p;
        return result;
    }
}

