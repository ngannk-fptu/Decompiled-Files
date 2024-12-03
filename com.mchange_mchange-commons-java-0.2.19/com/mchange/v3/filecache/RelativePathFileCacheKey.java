/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.filecache;

import com.mchange.v3.filecache.FileCacheKey;
import java.net.MalformedURLException;
import java.net.URL;

public class RelativePathFileCacheKey
implements FileCacheKey {
    final URL url;
    final String relPath;

    public RelativePathFileCacheKey(URL uRL, String string) throws MalformedURLException, IllegalArgumentException {
        String string2 = string.trim();
        if (uRL == null || string == null) {
            throw new IllegalArgumentException("parentURL [" + uRL + "] and relative path [" + string + "] must be non-null");
        }
        if (string2.length() == 0) {
            throw new IllegalArgumentException("relative path [" + string + "] must not be a blank string");
        }
        if (!string2.equals(string)) {
            throw new IllegalArgumentException("relative path [" + string + "] must not begin or end with whitespace.");
        }
        if (string.startsWith("/")) {
            throw new IllegalArgumentException("Path must be relative, '" + string + "' begins with '/'.");
        }
        this.url = new URL(uRL, string);
        this.relPath = string;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public String getCacheFilePath() {
        return this.relPath;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RelativePathFileCacheKey) {
            RelativePathFileCacheKey relativePathFileCacheKey = (RelativePathFileCacheKey)object;
            return this.url.equals(relativePathFileCacheKey.url) && this.relPath.equals(relativePathFileCacheKey.relPath);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.url.hashCode() ^ this.relPath.hashCode();
    }
}

