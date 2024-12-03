/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.ConfluenceBuildUtil;

public class PdlUtil {
    private static final boolean PDL_ENABLED = ConfluenceBuildUtil.getBuildNumber() >= 4000;

    private PdlUtil() {
    }

    public static boolean isPdlEnabled() {
        return PDL_ENABLED;
    }
}

