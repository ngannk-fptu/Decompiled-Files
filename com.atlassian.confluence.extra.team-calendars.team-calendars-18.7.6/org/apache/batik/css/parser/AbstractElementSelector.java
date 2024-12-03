/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.ElementSelector
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

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getLocalName() {
        return this.localName;
    }
}

