/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.shacache;

import java.io.InputStream;

public interface ShaSource {
    public boolean isFast();

    public InputStream get(String var1) throws Exception;
}

