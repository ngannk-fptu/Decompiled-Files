/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.languages;

import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public abstract class LocaleParser {
    private LocaleParser() {
    }

    public static Locale toLocale(String localeString) {
        if (StringUtils.isBlank((CharSequence)localeString)) {
            return null;
        }
        return LocaleParser.parse(localeString, "_");
    }

    public static Locale toLocaleFromHttpHeader(String localeString) {
        if (StringUtils.isBlank((CharSequence)localeString)) {
            return null;
        }
        return LocaleParser.parse(localeString, "-");
    }

    private static Locale parse(String localeString, String separator) {
        StringTokenizer st = new StringTokenizer(localeString, separator);
        String language = st.nextToken();
        if (language.startsWith("q=")) {
            return null;
        }
        String country = "";
        if (st.hasMoreTokens()) {
            country = st.nextToken();
        }
        StringBuilder variant = new StringBuilder();
        if (st.hasMoreTokens()) {
            variant.append(st.nextToken());
        }
        while (st.hasMoreTokens()) {
            variant.append("_").append(st.nextToken());
        }
        return new Locale(language, country, variant.toString());
    }
}

