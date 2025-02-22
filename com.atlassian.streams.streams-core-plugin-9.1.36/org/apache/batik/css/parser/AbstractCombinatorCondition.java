/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

public abstract class AbstractCombinatorCondition
implements CombinatorCondition {
    protected Condition firstCondition;
    protected Condition secondCondition;

    protected AbstractCombinatorCondition(Condition c1, Condition c2) {
        this.firstCondition = c1;
        this.secondCondition = c2;
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

