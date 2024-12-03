/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gzipfilter.selector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PatternMatcher {
    private final Map<String, Pattern> patternCache = Collections.synchronizedMap(new HashMap());

    public boolean matches(String contentType, String mimeTypesToGzip) {
        String[] mimeTypes;
        for (String type : mimeTypes = mimeTypesToGzip.split(",")) {
            String mimeType = type.trim();
            Pattern p = this.patternCache.get(mimeType);
            if (p == null) {
                p = Pattern.compile(mimeType);
                this.patternCache.put(mimeType, p);
            }
            if (!p.matcher(contentType).matches()) continue;
            return true;
        }
        return false;
    }
}

