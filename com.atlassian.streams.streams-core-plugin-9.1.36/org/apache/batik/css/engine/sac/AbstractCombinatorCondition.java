/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.ExtendedCondition;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

public abstract class AbstractCombinatorCondition
implements CombinatorCondition,
ExtendedCondition {
    protected Condition firstCondition;
    protected Condition secondCondition;

    protected AbstractCombinatorCondition(Condition c1, Condition c2) {
        this.firstCondition = c1;
        this.secondCondition = c2;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractCombinatorCondition c = (AbstractCombinatorCondition)obj;
        return c.firstCondition.equals(this.firstCondition) && c.secondCondition.equals(this.secondCondition);
    }

    @Override
    public int getSpecificity() {
        return ((ExtendedCondition)this.getFirstCondition()).getSpecificity() + ((ExtendedCondition)this.getSecondCondition()).getSpecificity();
    }

    @Override
    public Condition getFirstCondition() {
        return this.firstCondition;
    }

    @Override
    public Condition getSecondCondition() {
        return this.secondCondition;
    }
}

