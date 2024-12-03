/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.Constraint;

public interface ChildNode
extends Constraint {
    public String getSelectorName();

    public String getParentPath();
}

