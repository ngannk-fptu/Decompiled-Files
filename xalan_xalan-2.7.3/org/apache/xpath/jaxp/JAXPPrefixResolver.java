/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.jaxp;

import javax.xml.namespace.NamespaceContext;
import org.apache.xml.utils.PrefixResolver;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JAXPPrefixResolver
implements PrefixResolver {
    private NamespaceContext namespaceContext;
    public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";

    public JAXPPrefixResolver(NamespaceContext nsContext) {
        this.namespaceContext = nsContext;
    }

    @Override
    public String getNamespaceForPrefix(String prefix) {
        return this.namespaceContext.getNamespaceURI(prefix);
    }

    @Override
    public String getBaseIdentifier() {
        return null;
    }

    @Override
    public boolean handlesNullPrefixes() {
        return false;
    }

    @Override
    public String getNamespaceForPrefix(String prefix, Node namespaceContext) {
        String namespace = null;
        if (prefix.equals("xml")) {
            namespace = S_XMLNAMESPACEURI;
        } else {
            short type;
            block0: for (Node parent = namespaceContext; null != parent && null == namespace && ((type = parent.getNodeType()) == 1 || type == 5); parent = parent.getParentNode()) {
                if (type != 1) continue;
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
}

