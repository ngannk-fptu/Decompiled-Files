/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.attachments.OctetStream;

public final class OctetStreamHolder
implements Holder {
    public OctetStream value;

    public OctetStreamHolder() {
    }

    public OctetStreamHolder(OctetStream value) {
        this.value = value;
    }
}

