/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Source;

public interface Join
extends Source {
    public Source getLeft();

    public Source getRight();

    public String getJoinType();

    public JoinCondition getJoinCondition();
}

