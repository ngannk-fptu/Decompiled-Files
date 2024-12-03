/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.ElementSelector;

public abstract class AbstractElementSelector
implements ElementSelector,
ExtendedSelector {
    protected String namespaceURI;
    protected String localName;

    protected AbstractElementSelector(String uri, String name) {
        this.namespaceURI = uri;
        this.localName = name;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractElementSelector s = (AbstractElementSelector)obj;
        return s.namespaceURI.equals(this.namespaceURI) && s.localName.equals(this.localName);
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public String getLocalName() {
        return this.localName;
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
    }
}

