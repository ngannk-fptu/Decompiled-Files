/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.modification;

public class HtmlLayoutChange {
    private Type type = null;
    private String openingTag = "";
    private String endingTag = "";

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getOpeningTag() {
        return this.openingTag;
    }

    public void setOpeningTag(String openingTag) {
        this.openingTag = openingTag;
    }

    public String getEndingTag() {
        return this.endingTag;
    }

    public void setEndingTag(String endingTag) {
        this.endingTag = endingTag;
    }

    public static enum Type {
        TAG_ADDED,
        TAG_REMOVED;

    }
}

