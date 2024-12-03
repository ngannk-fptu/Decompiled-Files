/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bandana;

import com.atlassian.bandana.BandanaContext;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BandanaPersister {
    public Object retrieve(BandanaContext var1, String var2);

    public Map<String, Object> retrieve(BandanaContext var1);

    public Iterable<String> retrieveKeys(BandanaContext var1);

    public void store(BandanaContext var1, String var2, Object var3);

    public void flushCaches();

    public void remove(BandanaContext var1);

    public void remove(BandanaContext var1, String var2);
}

