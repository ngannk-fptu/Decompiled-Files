/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import javax.xml.namespace.QName;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class FOMExtensionIterator
extends OMFilterIterator {
    private String namespace = null;
    private String extns = null;
    private FOMFactory factory = null;

    public FOMExtensionIterator(OMElement parent) {
        super(parent.getChildren());
        this.namespace = parent.getQName().getNamespaceURI();
        this.factory = (FOMFactory)parent.getOMFactory();
    }

    public FOMExtensionIterator(OMElement parent, String extns) {
        this(parent);
        this.extns = extns;
    }

    public Object next() {
        return this.factory.getElementWrapper((Element)super.next());
    }

    protected boolean matches(OMNode node) {
        return node instanceof OMElement && this.isQNamesMatch(((OMElement)node).getQName(), this.namespace);
    }

    private boolean isQNamesMatch(QName elementQName, String namespace) {
        boolean namespaceURIMatch;
        String elns = elementQName == null ? "" : elementQName.getNamespaceURI();
        boolean bl = namespaceURIMatch = namespace == null || namespace == "" || elns.equals(namespace);
        if (!namespaceURIMatch && this.extns != null && !elns.equals(this.extns)) {
            return false;
        }
        return !namespaceURIMatch;
    }
}

