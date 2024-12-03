/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 */
package org.apache.struts2.result.plain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Cookie;

class HttpCookies {
    private final List<Cookie> cookies = new ArrayList<Cookie>();

    HttpCookies() {
    }

    public HttpCookies add(String name, String value) {
        this.cookies.add(new Cookie(name, value));
        return this;
    }

    public List<Cookie> getCookies() {
        return Collections.unmodifiableList(this.cookies);
    }
}

