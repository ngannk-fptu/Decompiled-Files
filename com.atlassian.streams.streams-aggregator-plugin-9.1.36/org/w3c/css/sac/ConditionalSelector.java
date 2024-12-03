/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;

public interface ConditionalSelector
extends SimpleSelector {
    public SimpleSelector getSimpleSelector();

    public Condition getCondition();
}

