/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.holders;

import java.math.BigInteger;
import javax.xml.rpc.holders.Holder;

public final class BigIntegerHolder
implements Holder {
    public BigInteger value;

    public BigIntegerHolder() {
    }

    public BigIntegerHolder(BigInteger value) {
        this.value = value;
    }
}

