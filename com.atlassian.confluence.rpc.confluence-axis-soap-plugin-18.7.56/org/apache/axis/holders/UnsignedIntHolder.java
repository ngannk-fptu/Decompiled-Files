/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.UnsignedInt;

public final class UnsignedIntHolder
implements Holder {
    public UnsignedInt value;

    public UnsignedIntHolder() {
    }

    public UnsignedIntHolder(UnsignedInt value) {
        this.value = value;
    }
}

