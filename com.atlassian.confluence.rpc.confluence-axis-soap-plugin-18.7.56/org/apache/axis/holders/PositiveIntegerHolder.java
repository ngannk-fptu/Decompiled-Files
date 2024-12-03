/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.PositiveInteger;

public final class PositiveIntegerHolder
implements Holder {
    public PositiveInteger value;

    public PositiveIntegerHolder() {
    }

    public PositiveIntegerHolder(PositiveInteger value) {
        this.value = value;
    }
}

