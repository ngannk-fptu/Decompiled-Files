/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

public class StyleProp {
    protected String name;
    protected String value;
    protected StyleProp next;

    public StyleProp(String name, String value, StyleProp next) {
        this.name = name;
        this.value = value;
        this.next = next;
    }
}

