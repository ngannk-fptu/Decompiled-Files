/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.util.FastStack;

public class XsonNamespaceContext
implements NamespaceContext {
    private FastStack nodes;

    public XsonNamespaceContext(FastStack nodes) {
        this.nodes = nodes;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        for (Node node : this.nodes) {
            String uri = node.getNamespaceURI(prefix);
            if (uri == null) continue;
            return uri;
        }
        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Node node : this.nodes) {
            String prefix = node.getNamespacePrefix(namespaceURI);
            if (prefix == null) continue;
            return prefix;
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}

