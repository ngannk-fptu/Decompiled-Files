/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.StaticOperand;

public interface FullTextSearch
extends Constraint {
    public String getSelectorName();

    public String getPropertyName();

    public StaticOperand getFullTextSearchExpression();
}

