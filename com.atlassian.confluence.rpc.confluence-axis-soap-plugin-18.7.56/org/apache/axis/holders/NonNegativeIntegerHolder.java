/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.NonNegativeInteger;

public final class NonNegativeIntegerHolder
implements Holder {
    public NonNegativeInteger value;

    public NonNegativeIntegerHolder() {
    }

    public NonNegativeIntegerHolder(NonNegativeInteger value) {
        this.value = value;
    }
}

