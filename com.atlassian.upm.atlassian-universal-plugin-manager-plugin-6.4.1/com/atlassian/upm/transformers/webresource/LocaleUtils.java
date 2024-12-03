/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.transformers.webresource;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class LocaleUtils {
    public static String serialize(Locale locale) {
        StringBuilder str = new StringBuilder(locale.getLanguage());
        String country = locale.getCountry();
        if (StringUtils.isBlank((CharSequence)country)) {
            return str.toString();
        }
        str.append("-").append(country);
        String variant = locale.getVariant();
        if (StringUtils.isBlank((CharSequence)variant)) {
            return str.toString();
        }
        str.append("-").append(variant);
        return str.toString();
    }

    public static Locale deserialize(String str) {
        String[] split = str.split("-");
        switch (split.length) {
            case 1: {
                return new Locale(split[0]);
            }
            case 2: {
                return new Locale(split[0], split[1]);
            }
            case 3: {
                return new Locale(split[0], split[1], split[2]);
            }
        }
        throw new IllegalArgumentException("Cannot parse locale: " + str);
    }
}

