/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 */
package com.atlassian.confluence.plugins.gatekeeper.service;

import com.atlassian.confluence.util.GeneralUtil;

public class ConfluenceVersion {
    private static int buildNumber = Integer.parseInt(GeneralUtil.getBuildNumber());
    private static boolean deleteOwnPermissionSupported = buildNumber >= 6441;
    private static boolean unlicensedAccessSupported = buildNumber >= 6207;

    public static boolean isDeleteOwnPermissionSupported() {
        return deleteOwnPermissionSupported;
    }

    public static boolean isUnlicensedAccessSupported() {
        return unlicensedAccessSupported;
    }
}

