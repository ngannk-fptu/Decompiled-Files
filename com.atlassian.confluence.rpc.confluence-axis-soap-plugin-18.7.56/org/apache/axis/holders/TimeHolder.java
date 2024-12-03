/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.Time;

public final class TimeHolder
implements Holder {
    public Time value;

    public TimeHolder() {
    }

    public TimeHolder(Time value) {
        this.value = value;
    }
}

