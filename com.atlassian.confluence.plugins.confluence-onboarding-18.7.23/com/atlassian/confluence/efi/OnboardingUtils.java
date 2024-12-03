/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.efi;

import javax.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;

public class OnboardingUtils {
    public static String METADATA_IS_FIRST_SPACE_CREATED = "is-first-space-created";

    public static boolean isCookieContains(Cookie[] cookies, String cookieName, String value) {
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if (!StringUtils.equals((CharSequence)cookie.getName(), (CharSequence)cookieName) || value != null && !StringUtils.equals((CharSequence)cookie.getValue(), (CharSequence)value)) continue;
            return true;
        }
        return false;
    }
}

