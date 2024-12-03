/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.NegativeInteger;

public final class NegativeIntegerHolder
implements Holder {
    public NegativeInteger value;

    public NegativeIntegerHolder() {
    }

    public NegativeIntegerHolder(NegativeInteger value) {
        this.value = value;
    }
}

