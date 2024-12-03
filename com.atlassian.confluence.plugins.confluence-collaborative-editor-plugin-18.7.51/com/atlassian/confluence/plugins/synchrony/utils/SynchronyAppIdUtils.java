/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.synchrony.utils;

import org.apache.commons.lang3.StringUtils;

public class SynchronyAppIdUtils {
    public static boolean isValidAppId(String appID) {
        if (StringUtils.isBlank((CharSequence)appID)) {
            return false;
        }
        return appID.matches("^[a-zA-Z0-9_.-]*$");
    }
}

