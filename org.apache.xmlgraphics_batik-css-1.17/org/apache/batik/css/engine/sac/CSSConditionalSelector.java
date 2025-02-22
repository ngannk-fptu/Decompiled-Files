/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Condition
 *  org.w3c.css.sac.ConditionalSelector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.sac.ExtendedCondition;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.Element;

public class CSSConditionalSelector
implements ConditionalSelector,
ExtendedSelector {
    protected SimpleSelector simpleSelector;
    protected Condition condition;

    public CSSConditionalSelector(SimpleSelector s, Condition c) {
        this.simpleSelector = s;
        this.condition = c;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CSSConditionalSelector s = (CSSConditionalSelector)obj;
        return s.simpleSelector.equals(this.simpleSelector) && s.condition.equals(this.condition);
    }

    public short getSelectorType() {
        return 0;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        return ((ExtendedSelector)this.getSimpleSelector()).match(e, pseudoE) && ((ExtendedCondition)this.getCondition()).match(e, pseudoE);
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
        ((ExtendedSelector)this.getSimpleSelector()).fillAttributeSet(attrSet);
        ((ExtendedCondition)this.getCondition()).fillAttributeSet(attrSet);
    }

    @Override
    public int getSpecificity() {
        return ((ExtendedSelector)this.getSimpleSelector()).getSpecificity() + ((ExtendedCondition)this.getCondition()).getSpecificity();
    }

    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public String toString() {
        return String.valueOf(this.simpleSelector) + this.condition;
    }
}

