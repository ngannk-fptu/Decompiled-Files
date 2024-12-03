/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.NonPositiveInteger;

public final class NonPositiveIntegerHolder
implements Holder {
    public NonPositiveInteger value;

    public NonPositiveIntegerHolder() {
    }

    public NonPositiveIntegerHolder(NonPositiveInteger value) {
        this.value = value;
    }
}

