/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import java.util.Locale;

public interface LocaleProvider {
    public Locale getLocale();

    public boolean isValidLocaleString(String var1);

    public boolean isValidLocale(Locale var1);
}

