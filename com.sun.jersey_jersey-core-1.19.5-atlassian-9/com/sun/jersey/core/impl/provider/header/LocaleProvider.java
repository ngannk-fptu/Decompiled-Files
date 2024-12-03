/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.LanguageTag;
import com.sun.jersey.spi.HeaderDelegateProvider;
import java.text.ParseException;
import java.util.Locale;

public class LocaleProvider
implements HeaderDelegateProvider<Locale> {
    @Override
    public boolean supports(Class<?> type) {
        return Locale.class.isAssignableFrom(type);
    }

    public String toString(Locale header) {
        if (header.getCountry().length() == 0) {
            return header.getLanguage();
        }
        StringBuilder sb = new StringBuilder(header.getLanguage());
        return sb.append('-').append(header.getCountry()).toString();
    }

    public Locale fromString(String header) {
        try {
            LanguageTag lt = new LanguageTag(header);
            return lt.getAsLocale();
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing date '" + header + "'", ex);
        }
    }
}

