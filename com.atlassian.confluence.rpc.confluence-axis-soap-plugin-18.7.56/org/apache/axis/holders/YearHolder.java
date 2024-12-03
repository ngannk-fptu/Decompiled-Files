/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.Year;

public final class YearHolder
implements Holder {
    public Year value;

    public YearHolder() {
    }

    public YearHolder(Year value) {
        this.value = value;
    }
}

