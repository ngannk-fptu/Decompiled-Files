/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.impl.web;

import com.atlassian.soy.spi.web.WebContextProvider;
import java.util.Locale;

public class SimpleWebContextProvider
implements WebContextProvider {
    private final String contextPath;

    public SimpleWebContextProvider() {
        this("");
    }

    public SimpleWebContextProvider(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
}

