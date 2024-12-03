/*
 * Decompiled with CFR 0.152.
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
        return ((ExtendedSelector)this.ancestorSelector).getSpecificity() + ((ExtendedSelector)((Object)this.simpleSelector)).getSpecificity();
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

