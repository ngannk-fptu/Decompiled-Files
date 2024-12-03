/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import org.apache.commons.lang3.StringUtils;

public final class ExceptionMapperUtil {
    private ExceptionMapperUtil() {
    }

    public static String stripNonValidXMLCharacters(String in) {
        int current;
        if (StringUtils.isEmpty((CharSequence)in)) {
            return in;
        }
        StringBuilder out = new StringBuilder();
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; i += Character.charCount(current)) {
            current = Character.codePointAt(chars, i);
            if (!(current == 9 || current == 10 || current == 13 || current >= 32 && current <= 55295 || current >= 57344 && current <= 65533) && (current < 65536 || current > 0x10FFFF)) continue;
            out.appendCodePoint(current);
        }
        return out.toString();
    }
}

