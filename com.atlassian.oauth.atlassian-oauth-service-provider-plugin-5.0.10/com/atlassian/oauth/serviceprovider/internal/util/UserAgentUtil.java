/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth.serviceprovider.internal.util;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class UserAgentUtil {
    private static final Pattern MS_MINIREDIR_PATTERN = Pattern.compile("^Microsoft-WebDAV-MiniRedir/.*");
    private static final Pattern OSX_WEBDAVFS_PATTERN = Pattern.compile("^WebDAVFS/.*");
    private static final Pattern OSX_WEBDAVLIB_PATTERN = Pattern.compile("^WebDAVLib/.*");
    public static final String HEADER_USER_AGENT = "User-Agent";

    public static boolean isMicrosoftMiniRedirector(String userAgent) {
        return MS_MINIREDIR_PATTERN.matcher(StringUtils.defaultString((String)userAgent)).matches();
    }

    public static boolean isOsxFinder(String userAgent) {
        return OSX_WEBDAVFS_PATTERN.matcher(StringUtils.defaultString((String)userAgent)).matches() || OSX_WEBDAVLIB_PATTERN.matcher(StringUtils.defaultString((String)userAgent)).matches();
    }
}

