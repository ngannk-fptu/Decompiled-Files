/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.api.util.Option
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import org.apache.commons.lang.StringUtils;

public class Strings {
    public static Option<String> getFirstNonEmpty(Iterable<String> vals) {
        for (String val : vals) {
            if (StringUtils.isEmpty(val)) continue;
            return Option.some((Object)val);
        }
        return Option.none(String.class);
    }
}

