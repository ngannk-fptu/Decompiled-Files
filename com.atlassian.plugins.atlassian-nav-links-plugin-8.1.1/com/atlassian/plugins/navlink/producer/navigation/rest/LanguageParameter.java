/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.LocaleUtils
 */
package com.atlassian.plugins.navlink.producer.navigation.rest;

import com.google.common.base.Strings;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.LocaleUtils;

public class LanguageParameter {
    static final String PARAMETER_NAME = "lang";

    @Nonnull
    public static String encodeValue(@Nullable Locale locale) {
        StringBuilder builder = new StringBuilder();
        if (locale != null) {
            builder.append(locale.getLanguage());
            if (!Strings.isNullOrEmpty((String)locale.getCountry())) {
                builder.append("-").append(locale.getCountry());
            }
        }
        return builder.toString();
    }

    @Nonnull
    public static Locale extractFrom(@Nonnull HttpServletRequest httpServletRequest, @Nonnull Locale defaultValue) {
        String parameterValue = LanguageParameter.extractParameterValue(httpServletRequest);
        Locale locale = LanguageParameter.isValid(parameterValue) ? LanguageParameter.convertToLocale(parameterValue) : null;
        return locale != null ? locale : defaultValue;
    }

    @Nullable
    private static String extractParameterValue(@Nonnull HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter(PARAMETER_NAME);
    }

    private static boolean isValid(@Nullable String parameterValue) {
        return !Strings.isNullOrEmpty((String)parameterValue);
    }

    @Nullable
    private static Locale convertToLocale(@Nullable String parameterValue) {
        String decodedParameter = LanguageParameter.decodeValue(parameterValue);
        try {
            return LocaleUtils.toLocale((String)decodedParameter);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nonnull
    private static String decodeValue(@Nullable String parameterValue) {
        return Strings.nullToEmpty((String)parameterValue).replace("-", "_");
    }
}

