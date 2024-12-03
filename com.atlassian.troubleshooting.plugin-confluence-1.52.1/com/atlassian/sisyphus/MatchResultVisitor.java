/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.SisyphusPattern;

public interface MatchResultVisitor {
    public void patternMatched(String var1, LogLine var2, SisyphusPattern var3);

    public boolean isCancelled();
}

