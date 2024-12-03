/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.JXPathContext
 */
package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointerFactory;
import org.apache.commons.jxpath.JXPathContext;

class XPathContextFactory {
    XPathContextFactory() {
    }

    public <T> JXPathContext createContext(T root, NodeHandler<T> handler) {
        JXPathContext context = JXPathContext.newContext((Object)ConfigurationNodePointerFactory.wrapNode(root, handler));
        context.setLenient(true);
        return context;
    }
}

