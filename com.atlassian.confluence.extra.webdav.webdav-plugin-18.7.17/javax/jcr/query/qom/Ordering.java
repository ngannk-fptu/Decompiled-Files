/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.DynamicOperand;

public interface Ordering {
    public DynamicOperand getOperand();

    public String getOrder();
}

