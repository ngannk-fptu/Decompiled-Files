/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import java.util.Collection;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.MalformedCookieException;

public interface CookieSpec {
    public static final String PATH_DELIM = "/";
    public static final char PATH_DELIM_CHAR = "/".charAt(0);

    public Cookie[] parse(String var1, int var2, String var3, boolean var4, String var5) throws MalformedCookieException, IllegalArgumentException;

    public Cookie[] parse(String var1, int var2, String var3, boolean var4, Header var5) throws MalformedCookieException, IllegalArgumentException;

    public void parseAttribute(NameValuePair var1, Cookie var2) throws MalformedCookieException, IllegalArgumentException;

    public void validate(String var1, int var2, String var3, boolean var4, Cookie var5) throws MalformedCookieException, IllegalArgumentException;

    public void setValidDateFormats(Collection var1);

    public Collection getValidDateFormats();

    public boolean match(String var1, int var2, String var3, boolean var4, Cookie var5);

    public Cookie[] match(String var1, int var2, String var3, boolean var4, Cookie[] var5);

    public boolean domainMatch(String var1, String var2);

    public boolean pathMatch(String var1, String var2);

    public String formatCookie(Cookie var1);

    public String formatCookies(Cookie[] var1) throws IllegalArgumentException;

    public Header formatCookieHeader(Cookie[] var1) throws IllegalArgumentException;

    public Header formatCookieHeader(Cookie var1) throws IllegalArgumentException;
}

