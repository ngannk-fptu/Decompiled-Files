/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.ElementSelector;

public abstract class AbstractElementSelector
implements ElementSelector {
    protected String namespaceURI;
    protected String localName;

    protected AbstractElementSelector(String uri, String name) {
        this.namespaceURI = uri;
        this.localName = name;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public String getLocalName() {
        return this.localName;
    }
}

