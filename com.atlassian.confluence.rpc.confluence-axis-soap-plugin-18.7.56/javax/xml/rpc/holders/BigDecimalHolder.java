/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.holders;

import java.math.BigDecimal;
import javax.xml.rpc.holders.Holder;

public final class BigDecimalHolder
implements Holder {
    public BigDecimal value;

    public BigDecimalHolder() {
    }

    public BigDecimalHolder(BigDecimal value) {
        this.value = value;
    }
}

