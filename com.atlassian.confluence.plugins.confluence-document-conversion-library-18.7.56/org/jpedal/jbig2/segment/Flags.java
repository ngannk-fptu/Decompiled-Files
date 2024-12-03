/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Flags {
    protected int flagsAsInt;
    protected Map flags = new LinkedHashMap();

    public int getFlagValue(String string) {
        Integer n = (Integer)this.flags.get(string);
        return n;
    }

    public abstract void setFlags(int var1);
}

