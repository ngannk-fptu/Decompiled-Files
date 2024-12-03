/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.JoinCondition;

public interface ChildNodeJoinCondition
extends JoinCondition {
    public String getChildSelectorName();

    public String getParentSelectorName();
}

