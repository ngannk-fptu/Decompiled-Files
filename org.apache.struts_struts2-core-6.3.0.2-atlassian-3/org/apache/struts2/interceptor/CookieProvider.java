/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 */
package org.apache.struts2.interceptor;

import java.util.Set;
import javax.servlet.http.Cookie;

public interface CookieProvider {
    public Set<Cookie> getCookies();
}

