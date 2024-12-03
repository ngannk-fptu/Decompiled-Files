/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.NamespaceContext
 */
package org.jdom2.xpath.jaxen;

import java.util.HashMap;
import java.util.List;
import org.jaxen.NamespaceContext;
import org.jdom2.Namespace;
import org.jdom2.NamespaceAware;
import org.jdom2.xpath.jaxen.JDOMCoreNavigator;
import org.jdom2.xpath.jaxen.NamespaceContainer;

final class JDOMNavigator
extends JDOMCoreNavigator
implements NamespaceContext {
    private static final long serialVersionUID = 200L;
    private final HashMap<String, String> nsFromContext = new HashMap();
    private final HashMap<String, String> nsFromUser = new HashMap();

    JDOMNavigator() {
    }

    void reset() {
        super.reset();
        this.nsFromContext.clear();
    }

    void setContext(Object node) {
        this.nsFromContext.clear();
        List<Namespace> nsl = null;
        if (node instanceof NamespaceAware) {
            nsl = ((NamespaceAware)node).getNamespacesInScope();
        } else if (node instanceof NamespaceContainer) {
            nsl = ((NamespaceContainer)node).getParentElement().getNamespacesInScope();
        }
        if (nsl != null) {
            for (Namespace ns : nsl) {
                this.nsFromContext.put(ns.getPrefix(), ns.getURI());
            }
        }
    }

    void includeNamespace(Namespace namespace) {
        this.nsFromUser.put(namespace.getPrefix(), namespace.getURI());
    }

    public String translateNamespacePrefixToUri(String prefix) {
        if (prefix == null) {
            return null;
        }
        String uri = this.nsFromUser.get(prefix);
        if (uri != null) {
            return uri;
        }
        return this.nsFromContext.get(prefix);
    }
}

