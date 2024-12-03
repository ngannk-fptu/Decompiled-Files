/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.AbstractElementSelector;
import org.w3c.dom.Element;

public class CSSElementSelector
extends AbstractElementSelector {
    public CSSElementSelector(String uri, String name) {
        super(uri, name);
    }

    public short getSelectorType() {
        return 4;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        String name = this.getLocalName();
        if (name == null) {
            return true;
        }
        String eName = e.getPrefix() == null ? e.getNodeName() : e.getLocalName();
        return eName.equals(name);
    }

    @Override
    public int getSpecificity() {
        return this.getLocalName() == null ? 0 : 1;
    }

    public String toString() {
        String name = this.getLocalName();
        if (name == null) {
            return "*";
        }
        return name;
    }
}

