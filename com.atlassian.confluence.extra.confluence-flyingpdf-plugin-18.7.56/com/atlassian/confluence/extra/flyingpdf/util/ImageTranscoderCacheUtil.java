/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import java.util.HashMap;

public class ImageTranscoderCacheUtil {
    private static final ThreadLocal<HashMap<String, String>> threadLocal = new ThreadLocal();

    public static void initializeCache() {
        threadLocal.set(new HashMap());
    }

    public static void purgeCache() {
        threadLocal.remove();
    }

    public static String getCacheImage(String uri) {
        HashMap<String, String> cache = threadLocal.get();
        if (cache != null) {
            return cache.get(uri);
        }
        return null;
    }

    public static void setCacheImage(String uri, String imageName) {
        HashMap<String, String> cache = threadLocal.get();
        if (cache != null) {
            cache.put(uri, imageName);
        }
    }
}

