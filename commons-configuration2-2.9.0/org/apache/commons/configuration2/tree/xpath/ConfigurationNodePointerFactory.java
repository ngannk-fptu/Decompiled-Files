/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.ri.QName
 *  org.apache.commons.jxpath.ri.model.NodePointer
 *  org.apache.commons.jxpath.ri.model.NodePointerFactory
 */
package org.apache.commons.configuration2.tree.xpath;

import java.util.Locale;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

public class ConfigurationNodePointerFactory
implements NodePointerFactory {
    public static final int CONFIGURATION_NODE_POINTER_FACTORY_ORDER = 200;

    public int getOrder() {
        return 200;
    }

    public NodePointer createNodePointer(QName name, Object bean, Locale locale) {
        if (bean instanceof NodeWrapper) {
            NodeWrapper wrapper = (NodeWrapper)bean;
            return new ConfigurationNodePointer(wrapper.getNode(), locale, wrapper.getNodeHandler());
        }
        return null;
    }

    public NodePointer createNodePointer(NodePointer parent, QName name, Object bean) {
        if (bean instanceof NodeWrapper) {
            NodeWrapper wrapper = (NodeWrapper)bean;
            return new ConfigurationNodePointer((ConfigurationNodePointer)parent, wrapper.getNode(), wrapper.getNodeHandler());
        }
        return null;
    }

    public static <T> Object wrapNode(T node, NodeHandler<T> handler) {
        return new NodeWrapper<T>(node, handler);
    }

    static class NodeWrapper<T> {
        private final T node;
        private final NodeHandler<T> nodeHandler;

        public NodeWrapper(T nd, NodeHandler<T> handler) {
            this.node = nd;
            this.nodeHandler = handler;
        }

        public T getNode() {
            return this.node;
        }

        public NodeHandler<T> getNodeHandler() {
            return this.nodeHandler;
        }
    }
}

