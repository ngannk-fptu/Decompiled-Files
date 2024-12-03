/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number;

import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.number.ModifierStore;
import com.ibm.icu.impl.number.NumberStringBuilder;
import com.ibm.icu.text.NumberFormat;

public interface Modifier {
    public int apply(NumberStringBuilder var1, int var2, int var3);

    public int getPrefixLength();

    public int getCodePointCount();

    public boolean isStrong();

    public boolean containsField(NumberFormat.Field var1);

    public Parameters getParameters();

    public boolean semanticallyEquivalent(Modifier var1);

    public static class Parameters {
        public ModifierStore obj;
        public int signum;
        public StandardPlural plural;
    }
}

