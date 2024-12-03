/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.csskit.TermFloatValueImpl;

public class TermPercentImpl
extends TermFloatValueImpl
implements TermPercent {
    protected TermPercentImpl() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        sb.append(this.value).append("%");
        return sb.toString();
    }

    @Override
    public boolean isPercentage() {
        return true;
    }
}

