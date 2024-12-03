/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractDescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

public class DefaultChildSelector
extends AbstractDescendantSelector {
    public DefaultChildSelector(Selector ancestor, SimpleSelector simple) {
        super(ancestor, simple);
    }

    public short getSelectorType() {
        return 11;
    }

    public String toString() {
        SimpleSelector s = this.getSimpleSelector();
        if (s.getSelectorType() == 9) {
            return String.valueOf(this.getAncestorSelector()) + s;
        }
        return this.getAncestorSelector() + " > " + s;
    }
}

