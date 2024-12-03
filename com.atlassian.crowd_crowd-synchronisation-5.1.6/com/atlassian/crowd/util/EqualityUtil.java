/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.InternalEntityUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.InternalEntityUtils;
import org.apache.commons.lang3.StringUtils;

public final class EqualityUtil {
    private EqualityUtil() {
    }

    public static boolean different(String remoteString, String internalString) {
        if (StringUtils.isEmpty((CharSequence)remoteString)) {
            return StringUtils.isNotEmpty((CharSequence)internalString);
        }
        return !InternalEntityUtils.truncateValue((String)remoteString).equals(internalString);
    }
}

