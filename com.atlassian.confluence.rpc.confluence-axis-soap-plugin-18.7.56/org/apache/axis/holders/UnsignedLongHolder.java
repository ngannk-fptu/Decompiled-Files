/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.UnsignedLong;

public final class UnsignedLongHolder
implements Holder {
    public UnsignedLong value;

    public UnsignedLongHolder() {
    }

    public UnsignedLongHolder(UnsignedLong value) {
        this.value = value;
    }
}

