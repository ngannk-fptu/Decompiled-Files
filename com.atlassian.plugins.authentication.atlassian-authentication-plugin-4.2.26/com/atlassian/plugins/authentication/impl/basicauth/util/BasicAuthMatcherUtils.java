/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.impl.basicauth.util;

import javax.annotation.Nonnull;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BasicAuthMatcherUtils {
    public static final char[] WILDCARD_CHARACTERS = new char[]{'*', '?'};

    private BasicAuthMatcherUtils() {
    }

    public static boolean wildcardMatch(@Nonnull String value, @Nonnull String pattern) {
        return FilenameUtils.wildcardMatch((String)value, (String)pattern);
    }

    public static String normalizePathPattern(@Nonnull String pathPattern) {
        if (!ArrayUtils.contains((char[])WILDCARD_CHARACTERS, (char)pathPattern.charAt(0))) {
            pathPattern = StringUtils.prependIfMissing((String)pathPattern, (CharSequence)"/", (CharSequence[])new CharSequence[0]);
        }
        return StringUtils.removeEnd((String)pathPattern, (String)"/");
    }

    public static String normalizePath(@Nonnull String path) {
        return StringUtils.removeEnd((String)StringUtils.prependIfMissing((String)path, (CharSequence)"/", (CharSequence[])new CharSequence[0]), (String)"/");
    }
}

