/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.ri.QName
 *  org.apache.commons.jxpath.ri.compiler.NodeTest
 *  org.apache.commons.jxpath.ri.compiler.NodeTypeTest
 *  org.apache.commons.jxpath.ri.model.NodePointer
 */
package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;

class ConfigurationAttributePointer<T>
extends NodePointer {
    private static final long serialVersionUID = 5504551041716043748L;
    private final QueryResult<T> attributeResult;

    public ConfigurationAttributePointer(ConfigurationNodePointer<T> parent, String attrName) {
        super(parent);
        this.attributeResult = QueryResult.createAttributeResult(parent.getConfigurationNode(), attrName);
    }

    public ConfigurationNodePointer<T> getParentPointer() {
        ConfigurationNodePointer configurationNodePointer = (ConfigurationNodePointer)this.getParent();
        return configurationNodePointer;
    }

    public int compareChildNodePointers(NodePointer p1, NodePointer p2) {
        return 0;
    }

    public Object getBaseValue() {
        return this.getValue();
    }

    public Object getImmediateNode() {
        return this.attributeResult;
    }

    public int getLength() {
        return 1;
    }

    public QName getName() {
        return new QName(null, this.attributeResult.getAttributeName());
    }

    public boolean isCollection() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean isAttribute() {
        return true;
    }

    public Object getValue() {
        return this.attributeResult.getAttributeValue(this.getNodeHandler());
    }

    public void setValue(Object value) {
        throw new UnsupportedOperationException("Updating the value is not supported!");
    }

    public boolean testNode(NodeTest test) {
        if (test instanceof NodeTypeTest && ((NodeTypeTest)test).getNodeType() == 2) {
            return true;
        }
        return super.testNode(test);
    }

    private NodeHandler<T> getNodeHandler() {
        return this.getParentPointer().getNodeHandler();
    }
}

