/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.javanet.staxutils;

public interface Indentation {
    public static final String DEFAULT_INDENT = "  ";
    public static final String NORMAL_END_OF_LINE = "\n";

    public void setIndent(String var1);

    public String getIndent();

    public void setNewLine(String var1);

    public String getNewLine();
}

