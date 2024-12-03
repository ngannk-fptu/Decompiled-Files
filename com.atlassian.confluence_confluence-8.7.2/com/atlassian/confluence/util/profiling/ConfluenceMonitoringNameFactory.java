/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.CharMatcher
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;

@Internal
final class ConfluenceMonitoringNameFactory {
    private static final CharMatcher LEGAL_CHAR = CharMatcher.inRange((char)'a', (char)'z').or(CharMatcher.inRange((char)'A', (char)'Z')).or(CharMatcher.inRange((char)'0', (char)'9')).or(CharMatcher.anyOf((CharSequence)".-_[],@$%()<>")).precomputed();
    private static final String UNKNOWN_NAME = "<UNKNOWN>";

    ConfluenceMonitoringNameFactory() {
    }

    static String createName(String name, String ... optional) {
        CharSequence fullName;
        if (optional.length == 0) {
            fullName = StringUtils.trimToEmpty((String)name);
        } else {
            StringBuilder buffer = new StringBuilder();
            buffer.append(StringUtils.trimToEmpty((String)name));
            for (String item : optional) {
                buffer.append('.').append(StringUtils.trimToEmpty((String)item));
            }
            fullName = buffer;
        }
        return (String)StringUtils.defaultIfBlank((CharSequence)LEGAL_CHAR.retainFrom(fullName), (CharSequence)UNKNOWN_NAME);
    }
}

