/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

public class Style {
    protected String tag;
    protected String tagClass;
    protected String properties;
    protected Style next;

    public Style(String tag, String tagClass, String properties, Style next) {
        this.tag = tag;
        this.tagClass = tagClass;
        this.properties = properties;
        this.next = next;
    }
}

