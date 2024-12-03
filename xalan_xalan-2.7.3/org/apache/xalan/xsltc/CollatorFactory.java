/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc;

import java.text.Collator;
import java.util.Locale;

public interface CollatorFactory {
    public Collator getCollator(String var1, String var2);

    public Collator getCollator(Locale var1);
}

