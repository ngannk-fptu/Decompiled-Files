/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.ServerCookies;

public interface CookieProcessor {
    public void parseCookieHeader(MimeHeaders var1, ServerCookies var2);

    @Deprecated
    public String generateHeader(Cookie var1);

    default public String generateHeader(Cookie cookie, HttpServletRequest request) {
        return this.generateHeader(cookie);
    }

    public Charset getCharset();
}

