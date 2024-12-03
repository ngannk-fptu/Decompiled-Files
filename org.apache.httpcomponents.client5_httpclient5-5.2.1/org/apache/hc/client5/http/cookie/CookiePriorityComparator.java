/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http.cookie;

import java.time.Instant;
import java.util.Comparator;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.STATELESS)
public class CookiePriorityComparator
implements Comparator<Cookie> {
    public static final CookiePriorityComparator INSTANCE = new CookiePriorityComparator();

    private int getPathLength(Cookie cookie) {
        String path = cookie.getPath();
        return path != null ? path.length() : 1;
    }

    @Override
    public int compare(Cookie c1, Cookie c2) {
        int l1 = this.getPathLength(c1);
        int l2 = this.getPathLength(c2);
        int result = l2 - l1;
        if (result == 0) {
            Instant d1 = c1.getCreationInstant();
            Instant d2 = c2.getCreationInstant();
            if (d1 != null && d2 != null) {
                return d1.compareTo(d2);
            }
        }
        return result;
    }
}

