/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

public abstract class AbstractSiblingSelector
implements SiblingSelector {
    protected short nodeType;
    protected Selector selector;
    protected SimpleSelector simpleSelector;

    protected AbstractSiblingSelector(short type, Selector sel, SimpleSelector simple) {
        this.nodeType = type;
        this.selector = sel;
        this.simpleSelector = simple;
    }

    @Override
    public short getNodeType() {
        return this.nodeType;
    }

    @Override
    public Selector getSelector() {
        return this.selector;
    }

    @Override
    public SimpleSelector getSiblingSelector() {
        return this.simpleSelector;
    }
}

