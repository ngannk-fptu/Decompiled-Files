/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.TextParseUtil;

public interface TextParser {
    public static final int DEFAULT_LOOP_COUNT = 1;

    public Object evaluate(char[] var1, String var2, TextParseUtil.ParsedValueEvaluator var3, int var4);
}

