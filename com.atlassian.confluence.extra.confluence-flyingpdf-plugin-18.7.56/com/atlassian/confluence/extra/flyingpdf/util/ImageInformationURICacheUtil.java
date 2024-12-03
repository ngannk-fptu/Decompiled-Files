/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.confluence.extra.flyingpdf.util.ImageInformation;
import java.util.HashMap;

public class ImageInformationURICacheUtil {
    private static final ThreadLocal<HashMap<String, ImageInformation>> threadLocal = new ThreadLocal();

    public static void initializeCache() {
        threadLocal.set(new HashMap());
    }

    public static void purgeCache() {
        threadLocal.remove();
    }

    public static ImageInformation getCacheURI(String uri) {
        HashMap<String, ImageInformation> cache = threadLocal.get();
        if (cache != null) {
            return cache.get(uri);
        }
        return null;
    }

    public static void setCacheURI(String uri, ImageInformation cacheValue) {
        HashMap<String, ImageInformation> cache = threadLocal.get();
        if (cache != null) {
            cache.put(uri, cacheValue);
        }
    }
}

