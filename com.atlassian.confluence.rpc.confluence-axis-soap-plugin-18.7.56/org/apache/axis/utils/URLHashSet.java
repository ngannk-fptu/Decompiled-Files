/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.StringTokenizer;

public class URLHashSet
extends HashSet {
    public boolean add(URL url) {
        return super.add(URLHashSet.normalize(url));
    }

    public boolean remove(URL url) {
        return super.remove(URLHashSet.normalize(url));
    }

    public boolean contains(URL url) {
        return super.contains(URLHashSet.normalize(url));
    }

    public static URL normalize(URL url) {
        if (url.getProtocol().equals("file")) {
            try {
                File f = new File(URLHashSet.cleanup(url.getFile()));
                if (f.exists()) {
                    return f.toURL();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return url;
    }

    private static String cleanup(String uri) {
        String[] dirty = URLHashSet.tokenize(uri, "/\\", false);
        int length = dirty.length;
        String[] clean = new String[length];
        while (true) {
            boolean path = false;
            boolean finished = true;
            int j = 0;
            for (int i = 0; i < length && dirty[i] != null; ++i) {
                if (".".equals(dirty[i])) continue;
                if ("..".equals(dirty[i])) {
                    clean[j++] = dirty[i];
                    if (!path) continue;
                    finished = false;
                    continue;
                }
                if (i + 1 < length && "..".equals(dirty[i + 1])) {
                    ++i;
                    continue;
                }
                clean[j++] = dirty[i];
                path = true;
            }
            if (finished) break;
            dirty = clean;
            clean = new String[length];
        }
        StringBuffer b = new StringBuffer(uri.length());
        for (int i = 0; i < length && clean[i] != null; ++i) {
            b.append(clean[i]);
            if (i + 1 >= length || clean[i + 1] == null) continue;
            b.append("/");
        }
        return b.toString();
    }

    private static String[] tokenize(String str, String delim, boolean returnTokens) {
        StringTokenizer tokenizer = new StringTokenizer(str, delim, returnTokens);
        String[] tokens = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            tokens[i] = tokenizer.nextToken();
            ++i;
        }
        return tokens;
    }
}

