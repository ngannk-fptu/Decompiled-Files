/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.DescendantSelector
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

public abstract class AbstractDescendantSelector
implements DescendantSelector,
ExtendedSelector {
    protected Selector ancestorSelector;
    protected SimpleSelector simpleSelector;

    protected AbstractDescendantSelector(Selector ancestor, SimpleSelector simple) {
        this.ancestorSelector = ancestor;
        this.simpleSelector = simple;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractDescendantSelector s = (AbstractDescendantSelector)obj;
        return s.simpleSelector.equals(this.simpleSelector);
    }

    @Override
    public int getSpecificity() {
        return ((ExtendedSelector)this.ancestorSelector).getSpecificity() + ((ExtendedSelector)this.simpleSelector).getSpecificity();
    }

    public Selector getAncestorSelector() {
        return this.ancestorSelector;
    }

    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector;
    }
}

