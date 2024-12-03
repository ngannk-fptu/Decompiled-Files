/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.spi.web;

import java.util.Locale;

public interface WebContextProvider {
    public String getContextPath();

    public Locale getLocale();
}

