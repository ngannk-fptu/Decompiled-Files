/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.masterdetail;

public class DetailsSummaryMacroThreadLocalContext {
    private static ThreadLocal<Integer> contextRecursionDepthThreadLocal = new ThreadLocal();

    public static void setContextRecursionDepth(int recursionDepth) {
        contextRecursionDepthThreadLocal.set(recursionDepth);
    }

    public static int getContextRecursionDepth() {
        Integer recursiveDepth = contextRecursionDepthThreadLocal.get();
        if (recursiveDepth == null) {
            return 0;
        }
        return recursiveDepth;
    }
}

