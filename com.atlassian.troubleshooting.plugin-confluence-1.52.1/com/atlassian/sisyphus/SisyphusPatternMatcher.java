/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.MatchResultVisitor;
import com.atlassian.sisyphus.PatternMatchSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public interface SisyphusPatternMatcher {
    public Map<String, PatternMatchSet> match(BufferedReader var1) throws IOException, InterruptedException;

    public void match(BufferedReader var1, MatchResultVisitor var2, Pattern var3) throws IOException, InterruptedException;
}

