/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.URI;

public final class URIHolder
implements Holder {
    public URI value;

    public URIHolder() {
    }

    public URIHolder(URI value) {
        this.value = value;
    }
}

