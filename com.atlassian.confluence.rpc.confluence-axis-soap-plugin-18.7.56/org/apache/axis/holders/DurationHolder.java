/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.Duration;

public final class DurationHolder
implements Holder {
    public Duration value;

    public DurationHolder() {
    }

    public DurationHolder(Duration value) {
        this.value = value;
    }
}

