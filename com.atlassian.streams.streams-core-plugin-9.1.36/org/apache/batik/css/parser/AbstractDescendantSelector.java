/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

public abstract class AbstractDescendantSelector
implements DescendantSelector {
    protected Selector ancestorSelector;
    protected SimpleSelector simpleSelector;

    protected AbstractDescendantSelector(Selector ancestor, SimpleSelector simple) {
        this.ancestorSelector = ancestor;
        this.simpleSelector = simple;
    }

    @Override
    public Selector getAncestorSelector() {
        return this.ancestorSelector;
    }

    @Override
    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector;
    }
}

