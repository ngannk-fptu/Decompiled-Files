/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.date;

import org.jfree.date.SerialDate;

public abstract class AnnualDateRule
implements Cloneable {
    protected AnnualDateRule() {
    }

    public abstract SerialDate getDate(int var1);

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

