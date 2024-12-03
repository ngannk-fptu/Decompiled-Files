/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.xpath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.xpath.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

public class AXIOMXPath
extends BaseXPath {
    private static final long serialVersionUID = -5839161412925154639L;
    private Map namespaces = new HashMap();

    public AXIOMXPath(String xpathExpr) throws JaxenException {
        super(xpathExpr, new DocumentNavigator());
    }

    public AXIOMXPath(OMElement element, String xpathExpr) throws JaxenException {
        this(xpathExpr);
        this.addNamespaces(element);
    }

    public AXIOMXPath(OMAttribute attribute) throws JaxenException {
        this(attribute.getOwner(), attribute.getAttributeValue());
    }

    public void addNamespace(String prefix, String uri) throws JaxenException {
        super.addNamespace(prefix, uri);
        this.namespaces.put(prefix, uri);
    }

    public void addNamespaces(OMElement element) throws JaxenException {
        Iterator it = element.getNamespacesInScope();
        while (it.hasNext()) {
            OMNamespace ns = (OMNamespace)it.next();
            String prefix = ns.getPrefix();
            if (prefix.length() == 0) continue;
            this.addNamespace(prefix, ns.getNamespaceURI());
        }
    }

    public Map getNamespaces() {
        return this.namespaces;
    }
}

