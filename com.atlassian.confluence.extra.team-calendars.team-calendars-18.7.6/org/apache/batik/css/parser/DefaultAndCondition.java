/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Condition
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractCombinatorCondition;
import org.w3c.css.sac.Condition;

public class DefaultAndCondition
extends AbstractCombinatorCondition {
    public DefaultAndCondition(Condition c1, Condition c2) {
        super(c1, c2);
    }

    public short getConditionType() {
        return 0;
    }

    public String toString() {
        return String.valueOf(this.getFirstCondition()) + this.getSecondCondition();
    }
}

