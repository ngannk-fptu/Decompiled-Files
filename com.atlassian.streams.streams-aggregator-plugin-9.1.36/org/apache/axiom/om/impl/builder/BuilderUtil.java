/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMElementEx;

class BuilderUtil {
    private BuilderUtil() {
    }

    static void setNamespace(OMElement element, String namespaceURI, String prefix, boolean namespaceURIInterning) {
        OMNamespace namespace;
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if ((namespace = element.findNamespaceURI(prefix)) == null && namespaceURI.length() > 0 || namespace != null && !namespace.getNamespaceURI().equals(namespaceURI)) {
            if (namespaceURIInterning) {
                namespaceURI = namespaceURI.intern();
            }
            namespace = ((OMElementEx)element).addNamespaceDeclaration(namespaceURI, prefix);
        }
        if (namespace != null && namespaceURI.length() > 0) {
            element.setNamespaceWithNoFindInCurrentScope(namespace);
        }
    }
}

