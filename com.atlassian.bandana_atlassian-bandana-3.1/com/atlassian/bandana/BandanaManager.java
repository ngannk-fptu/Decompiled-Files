/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bandana;

import com.atlassian.bandana.BandanaContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BandanaManager {
    public void init();

    public void setValue(BandanaContext var1, String var2, Object var3);

    public Object getValue(BandanaContext var1, String var2);

    public Object getValue(BandanaContext var1, String var2, boolean var3);

    public Iterable<String> getKeys(BandanaContext var1);

    public void removeValue(BandanaContext var1, String var2);
}

