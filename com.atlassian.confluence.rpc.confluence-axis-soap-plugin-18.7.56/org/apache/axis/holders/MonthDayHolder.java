/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.MonthDay;

public final class MonthDayHolder
implements Holder {
    public MonthDay value;

    public MonthDayHolder() {
    }

    public MonthDayHolder(MonthDay value) {
        this.value = value;
    }
}

