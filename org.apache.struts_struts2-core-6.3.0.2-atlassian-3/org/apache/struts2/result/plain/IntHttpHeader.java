/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.result.plain;

import org.apache.struts2.result.plain.HttpHeader;

class IntHttpHeader
implements HttpHeader<Integer> {
    private final String name;
    private final Integer value;

    public IntHttpHeader(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}

