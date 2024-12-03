/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

public interface DiagnosticsLogWriter {
    public void writeSectionKeyValue(String var1, long var2, String var4, long var5);

    public void writeSectionKeyValue(String var1, long var2, String var4, double var5);

    public void writeSectionKeyValue(String var1, long var2, String var4, String var5);

    public void startSection(String var1);

    public void endSection();

    public void writeEntry(String var1);

    public void writeKeyValueEntry(String var1, String var2);

    public void writeKeyValueEntry(String var1, double var2);

    public void writeKeyValueEntry(String var1, long var2);

    public void writeKeyValueEntry(String var1, boolean var2);

    public void writeKeyValueEntryAsDateTime(String var1, long var2);
}

