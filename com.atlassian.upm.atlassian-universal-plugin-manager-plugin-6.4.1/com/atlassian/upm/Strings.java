/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import org.apache.commons.lang3.StringUtils;

public class Strings {
    public static Option<String> getFirstNonEmpty(Iterable<String> vals) {
        for (String val : vals) {
            if (StringUtils.isEmpty((CharSequence)val)) continue;
            return Option.some(val);
        }
        return Option.none(String.class);
    }
}

