/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http;

import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.res.StringManager;

public class ServerCookies {
    private static final StringManager sm = StringManager.getManager(ServerCookies.class);
    private ServerCookie[] serverCookies;
    private int cookieCount = 0;
    private int limit = 200;

    public ServerCookies(int initialSize) {
        this.serverCookies = new ServerCookie[initialSize];
    }

    public ServerCookie addCookie() {
        ServerCookie c;
        if (this.limit > -1 && this.cookieCount >= this.limit) {
            throw new IllegalArgumentException(sm.getString("cookies.maxCountFail", new Object[]{this.limit}));
        }
        if (this.cookieCount >= this.serverCookies.length) {
            int newSize = this.limit > -1 ? Math.min(2 * this.cookieCount, this.limit) : 2 * this.cookieCount;
            ServerCookie[] scookiesTmp = new ServerCookie[newSize];
            System.arraycopy(this.serverCookies, 0, scookiesTmp, 0, this.cookieCount);
            this.serverCookies = scookiesTmp;
        }
        if ((c = this.serverCookies[this.cookieCount]) == null) {
            this.serverCookies[this.cookieCount] = c = new ServerCookie();
        }
        ++this.cookieCount;
        return c;
    }

    public ServerCookie getCookie(int idx) {
        return this.serverCookies[idx];
    }

    public int getCookieCount() {
        return this.cookieCount;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        if (limit > -1 && this.serverCookies.length > limit && this.cookieCount <= limit) {
            ServerCookie[] scookiesTmp = new ServerCookie[limit];
            System.arraycopy(this.serverCookies, 0, scookiesTmp, 0, this.cookieCount);
            this.serverCookies = scookiesTmp;
        }
    }

    public void recycle() {
        for (int i = 0; i < this.cookieCount; ++i) {
            this.serverCookies[i].recycle();
        }
        this.cookieCount = 0;
    }
}

