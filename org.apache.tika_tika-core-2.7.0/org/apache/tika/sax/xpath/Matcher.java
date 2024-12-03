/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

public class Matcher {
    public static final Matcher FAIL = new Matcher();

    public Matcher descend(String namespace, String name) {
        return FAIL;
    }

    public boolean matchesElement() {
        return false;
    }

    public boolean matchesAttribute(String namespace, String name) {
        return false;
    }

    public boolean matchesText() {
        return false;
    }
}

