/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.config;

import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

final class ParserUtils {
    static final String NAMESPACE = "http://www.springframework.org/schema/ldap";

    private ParserUtils() {
    }

    static boolean getBoolean(Element element, String attribute, boolean defaultValue) {
        String theValue = element.getAttribute(attribute);
        if (StringUtils.hasText((String)theValue)) {
            return Boolean.valueOf(theValue);
        }
        return defaultValue;
    }

    static String getString(Element element, String attribute, String defaultValue) {
        String theValue = element.getAttribute(attribute);
        if (StringUtils.hasText((String)theValue)) {
            return theValue;
        }
        return defaultValue;
    }

    static int getInt(Element element, String attribute, int defaultValue) {
        String theValue = element.getAttribute(attribute);
        if (StringUtils.hasText((String)theValue)) {
            return Integer.parseInt(theValue);
        }
        return defaultValue;
    }
}

