/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.result.plain;

import org.apache.struts2.result.plain.HttpHeader;

class DateHttpHeader
implements HttpHeader<Long> {
    private final String name;
    private final Long value;

    public DateHttpHeader(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Long getValue() {
        return this.value;
    }
}

