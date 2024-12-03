/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.hunspell;

import java.util.regex.Pattern;

public class HunspellAffix {
    private String append;
    private char[] appendFlags;
    private String strip;
    private String condition;
    private Pattern conditionPattern;
    private char flag;
    private boolean crossProduct;

    public boolean checkCondition(CharSequence text) {
        return this.conditionPattern.matcher(text).matches();
    }

    public String getAppend() {
        return this.append;
    }

    public void setAppend(String append) {
        this.append = append;
    }

    public char[] getAppendFlags() {
        return this.appendFlags;
    }

    public void setAppendFlags(char[] appendFlags) {
        this.appendFlags = appendFlags;
    }

    public String getStrip() {
        return this.strip;
    }

    public void setStrip(String strip) {
        this.strip = strip;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition, String pattern) {
        this.condition = condition;
        this.conditionPattern = Pattern.compile(pattern);
    }

    public char getFlag() {
        return this.flag;
    }

    public void setFlag(char flag) {
        this.flag = flag;
    }

    public boolean isCrossProduct() {
        return this.crossProduct;
    }

    public void setCrossProduct(boolean crossProduct) {
        this.crossProduct = crossProduct;
    }
}

