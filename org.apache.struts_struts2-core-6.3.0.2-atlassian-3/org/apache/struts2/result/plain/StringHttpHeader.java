/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.result.plain;

import org.apache.struts2.result.plain.HttpHeader;

class StringHttpHeader
implements HttpHeader<String> {
    private final String name;
    private final String value;

    public StringHttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}

