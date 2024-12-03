/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.mime;

public final class Header {
    private final String name;
    private final String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}

