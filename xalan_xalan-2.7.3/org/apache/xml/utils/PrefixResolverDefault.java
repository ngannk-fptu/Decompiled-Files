/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import org.apache.xml.utils.PrefixResolver;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PrefixResolverDefault
implements PrefixResolver {
    Node m_context;

    public PrefixResolverDefault(Node xpathExpressionContext) {
        this.m_context = xpathExpressionContext;
    }

    @Override
    public String getNamespaceForPrefix(String prefix) {
        return this.getNamespaceForPrefix(prefix, this.m_context);
    }

    @Override
    public String getNamespaceForPrefix(String prefix, Node namespaceContext) {
        String namespace = null;
        if (prefix.equals("xml")) {
            namespace = "http://www.w3.org/XML/1998/namespace";
        } else {
            short type;
            block0: for (Node parent = namespaceContext; null != parent && null == namespace && ((type = parent.getNodeType()) == 1 || type == 5); parent = parent.getParentNode()) {
                if (type != 1) continue;
                if (parent.getNodeName().indexOf(prefix + ":") == 0) {
                    return parent.getNamespaceURI();
                }
                NamedNodeMap nnm = parent.getAttributes();
                for (int i = 0; i < nnm.getLength(); ++i) {
                    String p;
                    Node attr = nnm.item(i);
                    String aname = attr.getNodeName();
                    boolean isPrefix = aname.startsWith("xmlns:");
                    if (!isPrefix && !aname.equals("xmlns")) continue;
                    int index = aname.indexOf(58);
                    String string = p = isPrefix ? aname.substring(index + 1) : "";
                    if (!p.equals(prefix)) continue;
                    namespace = attr.getNodeValue();
                    continue block0;
                }
            }
        }
        return namespace;
    }

    @Override
    public String getBaseIdentifier() {
        return null;
    }

    @Override
    public boolean handlesNullPrefixes() {
        return false;
    }
}

