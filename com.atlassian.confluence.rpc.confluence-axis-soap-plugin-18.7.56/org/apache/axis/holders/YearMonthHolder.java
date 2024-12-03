/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.YearMonth;

public final class YearMonthHolder
implements Holder {
    public YearMonth value;

    public YearMonthHolder() {
    }

    public YearMonthHolder(YearMonth value) {
        this.value = value;
    }
}

