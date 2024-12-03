/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.LinkHeader;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class LinkHeaderProvider
implements HeaderDelegateProvider<LinkHeader> {
    @Override
    public boolean supports(Class<?> type) {
        return LinkHeader.class.isAssignableFrom(type);
    }

    @Override
    public LinkHeader fromString(String value) throws IllegalArgumentException {
        return LinkHeader.valueOf(value);
    }

    @Override
    public String toString(LinkHeader value) {
        return value.toString();
    }
}

