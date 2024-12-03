/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.Token;

public final class TokenHolder
implements Holder {
    public Token value;

    public TokenHolder() {
    }

    public TokenHolder(Token value) {
        this.value = value;
    }
}

