/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.DynamicOperand;

public interface PropertyValue
extends DynamicOperand {
    public String getSelectorName();

    public String getPropertyName();
}

