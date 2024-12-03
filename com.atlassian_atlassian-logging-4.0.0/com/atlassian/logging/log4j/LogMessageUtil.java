/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.logging.log4j;

import org.apache.commons.lang3.StringUtils;

public class LogMessageUtil {
    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char VERTICAL_TAB = '\u000b';
    public static final char FORM_FEED = '\f';
    public static final char NEXT_LINE = '\u0085';
    public static final char LINE_SEPARATOR = '\u2028';
    public static final char PARAGRAPH_SEPARATOR = '\u2029';

    private LogMessageUtil() {
    }

    public static String appendLineIndent(String messageText, String lineIndent) {
        int i;
        if (StringUtils.isEmpty((CharSequence)messageText)) {
            return messageText;
        }
        char[] chars = messageText.toCharArray();
        int lastPos = 0;
        StringBuilder builder = null;
        for (i = 0; i < chars.length; ++i) {
            if (chars[i] != '\r' && chars[i] != '\n' && chars[i] != '\u000b' && chars[i] != '\f' && chars[i] != '\u0085' && chars[i] != '\u2028' && chars[i] != '\u2029') continue;
            if (builder == null) {
                builder = new StringBuilder(messageText.length());
            }
            if (i + 1 < chars.length && chars[i] == '\r' && chars[i + 1] == '\n') {
                ++i;
            }
            builder.append(chars, lastPos, i + 1 - lastPos).append(lineIndent);
            lastPos = i + 1;
        }
        if (builder == null) {
            return messageText;
        }
        if (lastPos < chars.length) {
            builder.append(chars, lastPos, i - lastPos);
        }
        return builder.toString();
    }
}

