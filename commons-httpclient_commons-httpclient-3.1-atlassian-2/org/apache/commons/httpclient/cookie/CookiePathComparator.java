/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import java.util.Comparator;
import org.apache.commons.httpclient.Cookie;

public class CookiePathComparator
implements Comparator {
    private String normalizePath(Cookie cookie) {
        String path = cookie.getPath();
        if (path == null) {
            path = "/";
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    public int compare(Object o1, Object o2) {
        String path2;
        Cookie c1 = (Cookie)o1;
        Cookie c2 = (Cookie)o2;
        String path1 = this.normalizePath(c1);
        if (path1.equals(path2 = this.normalizePath(c2))) {
            return 0;
        }
        if (path1.startsWith(path2)) {
            return -1;
        }
        if (path2.startsWith(path1)) {
            return 1;
        }
        return 0;
    }
}

