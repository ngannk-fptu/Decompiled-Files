/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import java.util.Locale;

public interface LocaleService {
    public Iterable<Locale> getLocales();

    public Locale getDefaultLocale();
}

