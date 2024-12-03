/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

public class ListContext {
    public static final String NUMBERED = "#";
    public static final String BULLETED = "*";
    public static final String SQUARE = "-";
    private String stack = "";

    public ListContext(String type, ListContext current) {
        this.stack = current.getStack() + type;
    }

    public ListContext() {
    }

    public ListContext(String type) {
        this.stack = type;
    }

    public String getStack() {
        return this.stack;
    }

    public String decorateText(String s) {
        return this.stack + " " + s;
    }

    public boolean isInList() {
        return this.stack.length() > 0;
    }
}

