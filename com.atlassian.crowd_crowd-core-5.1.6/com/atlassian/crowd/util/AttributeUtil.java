/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.primitives.Longs
 */
package com.atlassian.crowd.util;

import com.google.common.base.Strings;
import com.google.common.primitives.Longs;
import java.time.Duration;
import java.util.Optional;

public final class AttributeUtil {
    private AttributeUtil() {
    }

    public static Duration safeParseDurationMillis(String attribute, Duration defaultDuration) {
        return Optional.ofNullable(Longs.tryParse((String)Strings.nullToEmpty((String)attribute))).map(Duration::ofMillis).orElse(defaultDuration);
    }
}

