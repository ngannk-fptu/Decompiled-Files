/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.inlinecomments.utils;

public class ResolveCommentConverter {
    public static String getStatus(boolean resolved, boolean isDangling) {
        if (resolved) {
            if (isDangling) {
                return "dangling";
            }
            return "resolved";
        }
        return "reopened";
    }

    public static boolean isResolved(String status) {
        return "resolved".equals(status) || "dangling".equals(status);
    }

    public static boolean isResolvedByDangling(String status) {
        return "dangling".equals(status);
    }
}

