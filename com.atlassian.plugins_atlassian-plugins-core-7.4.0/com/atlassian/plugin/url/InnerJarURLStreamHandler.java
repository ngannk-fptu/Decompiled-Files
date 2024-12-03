/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.url;

import com.atlassian.plugin.url.InnerJarURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class InnerJarURLStreamHandler
extends URLStreamHandler {
    @Override
    public URLConnection openConnection(URL url) throws IOException {
        return new InnerJarURLConnection(url);
    }

    @Override
    public void parseURL(URL url, String spec, int start, int limit) {
        int lastSlashLoc;
        String relPath;
        int bangLoc;
        String specPath = spec.substring(start, limit);
        String urlPath = specPath.charAt(0) == '/' ? specPath : (specPath.charAt(0) == '!' ? ((bangLoc = (relPath = url.getFile()).lastIndexOf(33)) < 0 ? relPath + specPath : relPath.substring(0, bangLoc) + specPath) : ((relPath = url.getFile()) != null ? ((lastSlashLoc = relPath.lastIndexOf(47)) < 0 ? "/" + specPath : relPath.substring(0, lastSlashLoc + 1) + specPath) : specPath));
        this.setURL(url, "jar", "", 0, null, null, urlPath, null, null);
    }
}

