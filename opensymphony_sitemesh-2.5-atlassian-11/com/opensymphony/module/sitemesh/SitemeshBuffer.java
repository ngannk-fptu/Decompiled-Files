/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import java.io.IOException;
import java.io.Writer;

public interface SitemeshBuffer {
    public char[] getCharArray();

    public int getBufferLength();

    public int getTotalLength();

    public int getTotalLength(int var1, int var2);

    public void writeTo(Writer var1, int var2, int var3) throws IOException;

    public boolean hasFragments();

    public boolean hasSecondaryStorage();

    public SecondaryStorage getSecondaryStorage();
}

