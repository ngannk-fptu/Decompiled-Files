/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

public class Placeholder {
    private String type;
    private String displayText;

    public Placeholder(String type, String displayContent) {
        this.type = type;
        this.displayText = displayContent;
    }

    public Placeholder(Placeholder toCopy) {
        this.type = toCopy.type;
        this.displayText = toCopy.displayText;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayText() {
        return this.displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}

