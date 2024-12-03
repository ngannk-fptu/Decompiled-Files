/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.AbstractElementSelector;
import org.w3c.dom.Element;

public class CSSPseudoElementSelector
extends AbstractElementSelector {
    public CSSPseudoElementSelector(String uri, String name) {
        super(uri, name);
    }

    @Override
    public short getSelectorType() {
        return 9;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        return this.getLocalName().equalsIgnoreCase(pseudoE);
    }

    @Override
    public int getSpecificity() {
        return 0;
    }

    public String toString() {
        return ":" + this.getLocalName();
    }
}

