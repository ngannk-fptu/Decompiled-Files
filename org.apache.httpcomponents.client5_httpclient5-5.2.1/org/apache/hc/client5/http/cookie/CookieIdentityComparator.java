/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http.cookie;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.STATELESS)
public class CookieIdentityComparator
implements Serializable,
Comparator<Cookie> {
    public static final CookieIdentityComparator INSTANCE = new CookieIdentityComparator();
    private static final long serialVersionUID = 4466565437490631532L;

    @Override
    public int compare(Cookie c1, Cookie c2) {
        int res = c1.getName().compareTo(c2.getName());
        if (res == 0) {
            String d2;
            String d1 = c1.getDomain();
            if (d1 == null) {
                d1 = "";
            }
            if ((d2 = c2.getDomain()) == null) {
                d2 = "";
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

