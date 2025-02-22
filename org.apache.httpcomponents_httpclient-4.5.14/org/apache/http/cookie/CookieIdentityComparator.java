/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 */
package org.apache.http.cookie;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.Cookie;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class CookieIdentityComparator
implements Serializable,
Comparator<Cookie> {
    private static final long serialVersionUID = 4466565437490631532L;

    @Override
    public int compare(Cookie c1, Cookie c2) {
        int res = c1.getName().compareTo(c2.getName());
        if (res == 0) {
            String d1 = c1.getDomain();
            if (d1 == null) {
                d1 = "";
            } else if (d1.indexOf(46) == -1) {
                d1 = d1 + ".local";
            }
            String d2 = c2.getDomain();
            if (d2 == null) {
                d2 = "";
            } else if (d2.indexOf(46) == -1) {
                d2 = d2 + ".local";
            }
            res = d1.compareToIgnoreCase(d2);
        }
        if (res == 0) {
            String p2;
            String p1 = c1.getPath();
            if (p1 == null) {
                p1 = "/";
            }
            if ((p2 = c2.getPath()) == null) {
                p2 = "/";
            }
            res = p1.compareTo(p2);
        }
        return res;
    }
}

