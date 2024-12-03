/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.Pattern;

public class JdkPattern
implements Pattern {
    private String regex;
    private boolean multiline;
    private java.util.regex.Pattern internPattern;

    public JdkPattern(String regex, boolean multiline) {
        this.regex = regex;
        this.multiline = multiline;
        this.internPattern = java.util.regex.Pattern.compile(regex, 0x20 | (multiline ? 8 : 0));
    }

    protected java.util.regex.Pattern getPattern() {
        return this.internPattern;
    }

    public String getRegex() {
        return this.regex;
    }

    public boolean getMultiline() {
        return this.multiline;
    }
}

