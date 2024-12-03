/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath.jaxen;

import org.jdom2.Element;
import org.jdom2.Namespace;

final class NamespaceContainer {
    private final Namespace ns;
    private final Element emt;

    public NamespaceContainer(Namespace ns, Element emt) {
        this.ns = ns;
        this.emt = emt;
    }

    public Namespace getNamespace() {
        return this.ns;
    }

    public Element getParentElement() {
        return this.emt;
    }

    public String toString() {
        return this.ns.getPrefix() + "=" + this.ns.getURI();
    }
}

