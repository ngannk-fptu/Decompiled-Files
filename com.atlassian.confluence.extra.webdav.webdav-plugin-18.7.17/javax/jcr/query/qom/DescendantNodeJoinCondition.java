/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.JoinCondition;

public interface DescendantNodeJoinCondition
extends JoinCondition {
    public String getDescendantSelectorName();

    public String getAncestorSelectorName();
}

