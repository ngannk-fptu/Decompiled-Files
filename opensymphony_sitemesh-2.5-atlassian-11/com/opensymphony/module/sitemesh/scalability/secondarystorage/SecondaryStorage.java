/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.IOException;
import java.io.Writer;

public interface SecondaryStorage {
    public long getMemoryLimitBeforeUse();

    public void write(int var1) throws IOException;

    public void write(char[] var1, int var2, int var3) throws IOException;

    public void write(String var1, int var2, int var3) throws IOException;

    public void write(String var1) throws IOException;

    public void writeTo(Writer var1) throws IOException;

    public void cleanUp();
}

