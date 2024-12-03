/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import javax.xml.transform.Source;

public final class SourceHolder
implements Holder {
    public Source value;

    public SourceHolder() {
    }

    public SourceHolder(Source value) {
        this.value = value;
    }
}

