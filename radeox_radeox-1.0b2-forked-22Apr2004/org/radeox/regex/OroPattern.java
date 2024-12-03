/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.oro.text.regex.Pattern
 */
package org.radeox.regex;

import org.radeox.regex.Pattern;

public class OroPattern
implements Pattern {
    private String regex;
    private boolean multiline;
    private org.apache.oro.text.regex.Pattern internPattern;

    public OroPattern(String regex, boolean multiline, org.apache.oro.text.regex.Pattern pattern) {
        this.regex = regex;
        this.multiline = multiline;
        this.internPattern = pattern;
    }

    protected org.apache.oro.text.regex.Pattern getPattern() {
        return this.internPattern;
    }

    public String getRegex() {
        return this.regex;
    }

    public boolean getMultiline() {
        return this.multiline;
    }
}

