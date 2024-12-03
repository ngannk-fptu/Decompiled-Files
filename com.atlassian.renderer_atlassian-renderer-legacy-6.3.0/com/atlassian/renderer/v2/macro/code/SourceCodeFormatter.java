/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code;

public interface SourceCodeFormatter {
    public String[] getSupportedLanguages();

    public String format(String var1, String var2);
}

