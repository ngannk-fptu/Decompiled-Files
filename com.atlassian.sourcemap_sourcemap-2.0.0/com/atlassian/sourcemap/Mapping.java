/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

public interface Mapping {
    public int getGeneratedLine();

    public void setGeneratedLine(int var1);

    public int getGeneratedColumn();

    public int getSourceLine();

    public int getSourceColumn();

    public String getSourceFileName();

    public void setSourceFileName(String var1);

    public String getSourceSymbolName();
}

