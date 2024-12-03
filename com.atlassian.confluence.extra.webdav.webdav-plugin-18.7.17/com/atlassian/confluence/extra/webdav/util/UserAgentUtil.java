/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.util;

import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class UserAgentUtil {
    private static final Pattern MS_MINIREDIR_PATTERN = Pattern.compile("^Microsoft-WebDAV-MiniRedir/.*");
    private static final Pattern OSX_WEBDAVFS_PATTERN = Pattern.compile("^WebDAVFS/.*");

    public static boolean isMicrosoftMiniRedirector(String userAgent) {
        return MS_MINIREDIR_PATTERN.matcher(StringUtils.defaultString((String)userAgent)).matches();
    }

    public static boolean isOsxFinder(String userAgent) {
        return OSX_WEBDAVFS_PATTERN.matcher(StringUtils.defaultString((String)userAgent)).matches();
    }
}

