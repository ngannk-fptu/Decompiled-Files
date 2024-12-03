/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.springframework.util.StringUtils;

abstract class DateTimeFormatterUtils {
    DateTimeFormatterUtils() {
    }

    static DateTimeFormatter createStrictDateTimeFormatter(String pattern) {
        String patternToUse = StringUtils.replace((String)pattern, (String)"yy", (String)"uu");
        return DateTimeFormatter.ofPattern(patternToUse).withResolverStyle(ResolverStyle.STRICT);
    }
}

