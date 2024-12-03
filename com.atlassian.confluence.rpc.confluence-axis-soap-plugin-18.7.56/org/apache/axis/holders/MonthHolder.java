/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.Month;

public final class MonthHolder
implements Holder {
    public Month value;

    public MonthHolder() {
    }

    public MonthHolder(Month value) {
        this.value = value;
    }
}

