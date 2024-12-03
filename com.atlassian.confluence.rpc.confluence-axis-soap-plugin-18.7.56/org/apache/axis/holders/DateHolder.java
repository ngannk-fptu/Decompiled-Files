/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import java.util.Date;
import javax.xml.rpc.holders.Holder;

public final class DateHolder
implements Holder {
    public Date value;

    public DateHolder() {
    }

    public DateHolder(Date value) {
        this.value = value;
    }
}

