/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.Condition;

public interface PositionalCondition
extends Condition {
    public int getPosition();

    public boolean getTypeNode();

    public boolean getType();
}

