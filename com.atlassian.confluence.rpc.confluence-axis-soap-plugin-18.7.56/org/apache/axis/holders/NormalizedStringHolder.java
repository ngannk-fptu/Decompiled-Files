/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.NormalizedString;

public final class NormalizedStringHolder
implements Holder {
    public NormalizedString value;

    public NormalizedStringHolder() {
    }

    public NormalizedStringHolder(NormalizedString value) {
        this.value = value;
    }
}

