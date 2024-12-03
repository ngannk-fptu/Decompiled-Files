/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.sac.AbstractDescendantSelector;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CSSChildSelector
extends AbstractDescendantSelector {
    public CSSChildSelector(Selector ancestor, SimpleSelector simple) {
        super(ancestor, simple);
    }

    public short getSelectorType() {
        return 11;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        Node n = e.getParentNode();
        if (n != null && n.getNodeType() == 1) {
            return ((ExtendedSelector)this.getAncestorSelector()).match((Element)n, null) && ((ExtendedSelector)this.getSimpleSelector()).match(e, pseudoE);
        }
        return false;
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
        ((ExtendedSelector)this.getAncestorSelector()).fillAttributeSet(attrSet);
        ((ExtendedSelector)this.getSimpleSelector()).fillAttributeSet(attrSet);
    }

    public String toString() {
        SimpleSelector s = this.getSimpleSelector();
        if (s.getSelectorType() == 9) {
            return String.valueOf(this.getAncestorSelector()) + s;
        }
        return this.getAncestorSelector() + " > " + s;
    }
}

