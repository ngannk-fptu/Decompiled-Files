/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Meta;

public class Header
extends Meta {
    private StringBuffer name;

    public Header(String name, String content) {
        super(0, content);
        this.name = new StringBuffer(name);
    }

    @Override
    public String getName() {
        return this.name.toString();
    }
}

