/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.sac.AbstractSiblingSelector;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CSSDirectAdjacentSelector
extends AbstractSiblingSelector {
    public CSSDirectAdjacentSelector(short type, Selector parent, SimpleSelector simple) {
        super(type, parent, simple);
    }

    public short getSelectorType() {
        return 12;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        Node n = e;
        if (!((ExtendedSelector)this.getSiblingSelector()).match(e, pseudoE)) {
            return false;
        }
        while ((n = n.getPreviousSibling()) != null && n.getNodeType() != 1) {
        }
        if (n == null) {
            return false;
        }
        return ((ExtendedSelector)this.getSelector()).match((Element)n, null);
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
        ((ExtendedSelector)this.getSelector()).fillAttributeSet(attrSet);
        ((ExtendedSelector)this.getSiblingSelector()).fillAttributeSet(attrSet);
    }

    public String toString() {
        return this.getSelector() + " + " + this.getSiblingSelector();
    }
}

