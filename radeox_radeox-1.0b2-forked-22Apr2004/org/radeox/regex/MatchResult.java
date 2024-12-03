/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.JdkMatchResult;
import org.radeox.regex.Matcher;

public abstract class MatchResult {
    public static MatchResult create(Matcher matcher) {
        return new JdkMatchResult(matcher);
    }

    public abstract int groups();

    public abstract String group(int var1);

    public abstract int beginOffset(int var1);

    public abstract int endOffset(int var1);
}

