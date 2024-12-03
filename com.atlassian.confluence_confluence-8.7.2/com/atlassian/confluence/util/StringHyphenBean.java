/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

public class StringHyphenBean {
    public static final int DEFAULT = 12;
    private int hyphenAfter;

    public StringHyphenBean() {
        this(12);
    }

    public StringHyphenBean(int hyphenAfter) {
        this.hyphenAfter = hyphenAfter;
    }

    public String getString(String original) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        char[] chars = original.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == ' ') {
                counter = 0;
            }
            if (counter == this.hyphenAfter) {
                result.append('-');
                result.append(' ');
                result.append(c);
                counter = 0;
                continue;
            }
            result.append(c);
            ++counter;
        }
        return result.toString();
    }

    public void setHyphenAfter(int hyphenAfter) {
        this.hyphenAfter = hyphenAfter;
    }
}

