/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

public class UrlUtils {
    public static final String SCHEME_URL = "://";

    private UrlUtils() {
    }

    public static final boolean isAcceptableReservedChar(char c) {
        return c == ';' || c == '/' || c == '?' || c == ':' || c == '@' || c == '&' || c == '=' || c == '+' || c == '$' || c == ',';
    }

    public static final boolean isAlpha(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    public static final boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static final boolean isOtherChar(char c) {
        return c == '#' || c == '%';
    }

    public static final boolean isUnreservedChar(char c) {
        return c == '-' || c == '_' || c == '.' || c == '!' || c == '~' || c == '*' || c == '\'' || c == '(' || c == ')';
    }

    public static final boolean isValidEmailChar(char c) {
        return UrlUtils.isAlpha(c) || UrlUtils.isDigit(c) || c == '_' || c == '-' || c == '.';
    }

    public static final boolean isValidScheme(String scheme) {
        if (scheme == null || scheme.length() == 0) {
            return false;
        }
        char[] schemeChars = scheme.toCharArray();
        if (!UrlUtils.isAlpha(schemeChars[0])) {
            return false;
        }
        for (int i = 1; i < schemeChars.length; ++i) {
            char schemeChar = schemeChars[i];
            if (UrlUtils.isValidSchemeChar(schemeChar)) continue;
            return false;
        }
        return true;
    }

    public static final boolean isValidSchemeChar(char c) {
        return UrlUtils.isAlpha(c) || UrlUtils.isDigit(c) || c == '+' || c == '-' || c == '.';
    }

    public static final boolean isValidURLChar(char c) {
        return UrlUtils.isAlpha(c) || UrlUtils.isDigit(c) || UrlUtils.isAcceptableReservedChar(c) || UrlUtils.isUnreservedChar(c) || UrlUtils.isOtherChar(c);
    }

    public static boolean verifyHierachicalURI(String uri) {
        return UrlUtils.verifyHierachicalURI(uri, null);
    }

    public static boolean verifyHierachicalURI(String uri, String[] schemesConsideredInvalid) {
        int i;
        if (uri == null || uri.length() < SCHEME_URL.length()) {
            return false;
        }
        int schemeUrlIndex = uri.indexOf(SCHEME_URL);
        if (schemeUrlIndex == -1) {
            return false;
        }
        String scheme = uri.substring(0, schemeUrlIndex);
        if (!UrlUtils.isValidScheme(scheme)) {
            return false;
        }
        if (schemesConsideredInvalid != null) {
            for (i = 0; i < schemesConsideredInvalid.length; ++i) {
                String invalidScheme = schemesConsideredInvalid[i];
                if (!scheme.equalsIgnoreCase(invalidScheme)) continue;
                return false;
            }
        }
        if (uri.length() < schemeUrlIndex + SCHEME_URL.length() + 1) {
            return false;
        }
        for (i = schemeUrlIndex + SCHEME_URL.length(); i < uri.length(); ++i) {
            char c = uri.charAt(i);
            if (UrlUtils.isValidURLChar(c)) continue;
            return false;
        }
        return true;
    }
}

