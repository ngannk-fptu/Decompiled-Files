/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractSiblingSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

public class DefaultDirectAdjacentSelector
extends AbstractSiblingSelector {
    public DefaultDirectAdjacentSelector(short type, Selector parent, SimpleSelector simple) {
        super(type, parent, simple);
    }

    public short getSelectorType() {
        return 12;
    }

    public String toString() {
        return this.getSelector() + " + " + this.getSiblingSelector();
    }
}

