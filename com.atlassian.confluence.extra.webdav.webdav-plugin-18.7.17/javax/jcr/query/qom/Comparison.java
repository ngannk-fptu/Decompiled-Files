/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.StaticOperand;

public interface Comparison
extends Constraint {
    public DynamicOperand getOperand1();

    public String getOperator();

    public StaticOperand getOperand2();
}

