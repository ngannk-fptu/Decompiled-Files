/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.HexBinary;

public final class HexBinaryHolder
implements Holder {
    public HexBinary value;

    public HexBinaryHolder() {
    }

    public HexBinaryHolder(HexBinary value) {
        this.value = value;
    }
}

