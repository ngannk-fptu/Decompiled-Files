/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.spi.HeaderDelegateProvider;

public class StringProvider
implements HeaderDelegateProvider<String> {
    @Override
    public boolean supports(Class<?> type) {
        return type == String.class;
    }

    @Override
    public String toString(String header) {
        return header;
    }

    @Override
    public String fromString(String header) {
        return header;
    }
}

