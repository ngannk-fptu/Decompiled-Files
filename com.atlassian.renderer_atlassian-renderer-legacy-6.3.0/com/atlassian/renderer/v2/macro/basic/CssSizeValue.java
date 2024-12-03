/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssSizeValue {
    private static final Pattern PATTERN = Pattern.compile("(\\d+)\\s*(px|pt|em)?");
    private final String raw;

    public CssSizeValue(String raw) {
        this.raw = raw;
    }

    public int value() {
        Matcher matcher = PATTERN.matcher(this.raw);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    public boolean isValid() {
        return PATTERN.matcher(this.raw).matches();
    }
}

