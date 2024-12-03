/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.synchrony.utils;

import org.apache.commons.lang3.StringUtils;

public class JdbcUrlUtil {
    public static String normalizeSchemeAndSubprotocol(String jdbcUrl) {
        int doubleSlashesIndex = jdbcUrl.indexOf("//");
        if (doubleSlashesIndex == -1) {
            return jdbcUrl;
        }
        String schemeAndSubprotocol = jdbcUrl.substring(0, doubleSlashesIndex);
        return StringUtils.join((Object[])new String[]{StringUtils.lowerCase((String)schemeAndSubprotocol), jdbcUrl.substring(doubleSlashesIndex)});
    }
}

