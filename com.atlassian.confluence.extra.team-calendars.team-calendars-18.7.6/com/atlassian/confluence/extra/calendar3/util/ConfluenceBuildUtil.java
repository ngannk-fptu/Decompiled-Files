/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.util.GeneralUtil;

public class ConfluenceBuildUtil {
    private static final int CONFLUENCE_BUILD_NUMBER = Integer.parseInt(GeneralUtil.getBuildNumber());

    private ConfluenceBuildUtil() {
    }

    public static int getBuildNumber() {
        return CONFLUENCE_BUILD_NUMBER;
    }
}

