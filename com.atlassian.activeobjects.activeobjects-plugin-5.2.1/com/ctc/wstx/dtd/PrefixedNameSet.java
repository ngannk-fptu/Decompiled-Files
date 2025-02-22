/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

public abstract class PrefixedNameSet {
    protected PrefixedNameSet() {
    }

    public abstract boolean hasMultiple();

    public abstract boolean contains(PrefixedName var1);

    public abstract void appendNames(StringBuffer var1, String var2);

    public final String toString() {
        return this.toString(", ");
    }

    public final String toString(String sep) {
        StringBuffer sb = new StringBuffer();
        this.appendNames(sb, sep);
        return sb.toString();
    }
}

