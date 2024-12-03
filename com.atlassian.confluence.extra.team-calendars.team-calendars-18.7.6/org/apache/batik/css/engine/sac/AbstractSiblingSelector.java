/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SiblingSelector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

public abstract class AbstractSiblingSelector
implements SiblingSelector,
ExtendedSelector {
    protected short nodeType;
    protected Selector selector;
    protected SimpleSelector simpleSelector;

    protected AbstractSiblingSelector(short type, Selector sel, SimpleSelector simple) {
        this.nodeType = type;
        this.selector = sel;
        this.simpleSelector = simple;
    }

    public short getNodeType() {
        return this.nodeType;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractSiblingSelector s = (AbstractSiblingSelector)obj;
        return s.simpleSelector.equals(this.simpleSelector);
    }

    @Override
    public int getSpecificity() {
        return ((ExtendedSelector)this.selector).getSpecificity() + ((ExtendedSelector)this.simpleSelector).getSpecificity();
    }

    public Selector getSelector() {
        return this.selector;
    }

    public SimpleSelector getSiblingSelector() {
        return this.simpleSelector;
    }
}

