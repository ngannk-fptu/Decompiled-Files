/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.Condition;

public interface CombinatorCondition
extends Condition {
    public Condition getFirstCondition();

    public Condition getSecondCondition();
}

