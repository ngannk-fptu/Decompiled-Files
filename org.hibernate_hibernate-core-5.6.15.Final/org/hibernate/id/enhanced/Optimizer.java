/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.enhanced;

import java.io.Serializable;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;

public interface Optimizer {
    public Serializable generate(AccessCallback var1);

    public IntegralDataTypeHolder getLastSourceValue();

    public int getIncrementSize();

    public boolean applyIncrementSizeToSourceValues();
}

