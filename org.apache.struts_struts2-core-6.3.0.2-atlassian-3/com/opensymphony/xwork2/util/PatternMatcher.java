/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import java.util.Map;

public interface PatternMatcher<E> {
    public boolean isLiteral(String var1);

    public E compilePattern(String var1);

    public boolean match(Map<String, String> var1, String var2, E var3);
}

