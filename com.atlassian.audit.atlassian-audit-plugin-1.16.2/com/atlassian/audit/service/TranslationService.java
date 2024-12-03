/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.service;

import java.util.Locale;

public interface TranslationService {
    public String getSiteLocaleText(String var1);

    public String getUserLocaleWithApplicationLocaleFallbackText(String var1);

    public Locale getUserLocale();
}

