/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.ri.QName
 *  org.apache.commons.jxpath.ri.compiler.NodeNameTest
 *  org.apache.commons.jxpath.ri.compiler.NodeTest
 *  org.apache.commons.jxpath.ri.compiler.NodeTypeTest
 *  org.apache.commons.jxpath.ri.model.NodePointer
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodeIteratorBase;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.lang3.StringUtils;

class ConfigurationNodeIteratorChildren<T>
extends ConfigurationNodeIteratorBase<T> {
    private final List<T> subNodes;

    public ConfigurationNodeIteratorChildren(ConfigurationNodePointer<T> parent, NodeTest nodeTest, boolean reverse, ConfigurationNodePointer<T> startsWith) {
        super(parent, reverse);
        T root = parent.getConfigurationNode();
        this.subNodes = this.createSubNodeList(root, nodeTest);
        if (startsWith != null) {
            this.setStartOffset(this.findStartIndex(this.subNodes, startsWith.getConfigurationNode()));
        } else if (reverse) {
            this.setStartOffset(this.size());
        }
    }

    @Override
    protected NodePointer createNodePointer(int position) {
        return new ConfigurationNodePointer(this.getParent(), this.subNodes.get(position), this.getNodeHandler());
    }

    @Override
    protected int size() {
        return this.subNodes.size();
    }

    private List<T> createSubNodeList(T node, NodeTest test) {
        NodeTypeTest typeTest;
        if (test == null) {
            return this.getNodeHandler().getChildren(node);
        }
        if (test instanceof NodeNameTest) {
            NodeNameTest nameTest = (NodeNameTest)test;
            QName name = nameTest.getNodeName();
            return nameTest.isWildcard() ? this.createSubNodeListForWildcardName(node, name) : this.createSubNodeListForName(node, name);
        }
        if (test instanceof NodeTypeTest && ((typeTest = (NodeTypeTest)test).getNodeType() == 1 || typeTest.getNodeType() == 2)) {
            return this.getNodeHandler().getChildren(node);
        }
        return Collections.emptyList();
    }

    private List<T> createSubNodeListForName(T node, QName name) {
        String compareName = ConfigurationNodeIteratorChildren.qualifiedName(name);
        ArrayList result = new ArrayList();
        this.getNodeHandler().getChildren(node).forEach(child -> {
            if (StringUtils.equals((CharSequence)compareName, (CharSequence)this.getNodeHandler().nodeName(child))) {
                result.add(child);
            }
        });
        return result;
    }

    private List<T> createSubNodeListForWildcardName(T node, QName name) {
        List<Object> children = this.getNodeHandler().getChildren(node);
        if (name.getPrefix() == null) {
            return children;
        }
        ArrayList prefixChildren = new ArrayList(children.size());
        String prefix = ConfigurationNodeIteratorChildren.prefixName(name.getPrefix(), null);
        children.forEach(child -> {
            if (StringUtils.startsWith((CharSequence)this.getNodeHandler().nodeName(child), (CharSequence)prefix)) {
                prefixChildren.add(child);
            }
        });
        return prefixChildren;
    }

    private int findStartIndex(List<T> children, T startNode) {
        int index = 0;
        for (T child : children) {
            if (child == startNode) {
                return index;
            }
            ++index;
        }
        return -1;
    }
}

