/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.BitSet;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.http.parser.Cookie;
import org.apache.tomcat.util.res.StringManager;

public class Rfc6265CookieProcessor
extends CookieProcessorBase {
    private static final Log log;
    private static final StringManager sm;
    private static final BitSet domainValid;

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public void parseCookieHeader(MimeHeaders headers, ServerCookies serverCookies) {
        if (headers == null) {
            return;
        }
        int pos = headers.findHeader("Cookie", 0);
        while (pos >= 0) {
            MessageBytes cookieValue = headers.getValue(pos);
            if (cookieValue != null && !cookieValue.isNull()) {
                if (cookieValue.getType() != 2) {
                    if (log.isDebugEnabled()) {
                        Exception e = new Exception();
                        log.debug((Object)"Cookies: Parsing cookie as String. Expected bytes.", (Throwable)e);
                    }
                    cookieValue.toBytes();
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Cookies: Parsing b[]: " + cookieValue.toString()));
                }
                ByteChunk bc = cookieValue.getByteChunk();
                Cookie.parseCookie(bc.getBytes(), bc.getOffset(), bc.getLength(), serverCookies);
            }
            ++pos;
            pos = headers.findHeader("Cookie", pos);
        }
    }

    @Override
    public String generateHeader(javax.servlet.http.Cookie cookie) {
        return this.generateHeader(cookie, null);
    }

    @Override
    public String generateHeader(javax.servlet.http.Cookie cookie, HttpServletRequest request) {
        SameSiteCookies sameSiteCookiesValue;
        String path;
        String domain;
        int maxAge;
        StringBuffer header = new StringBuffer();
        header.append(cookie.getName());
        header.append('=');
        String value = cookie.getValue();
        if (value != null && value.length() > 0) {
            this.validateCookieValue(value);
            header.append(value);
        }
        if ((maxAge = cookie.getMaxAge()) > -1) {
            header.append("; Max-Age=");
            header.append(maxAge);
            header.append("; Expires=");
            if (maxAge == 0) {
                header.append(ANCIENT_DATE);
            } else {
                ((DateFormat)COOKIE_DATE_FORMAT.get()).format(new Date(System.currentTimeMillis() + (long)maxAge * 1000L), header, new FieldPosition(0));
            }
        }
        if ((domain = cookie.getDomain()) != null && domain.length() > 0) {
            this.validateDomain(domain);
            header.append("; Domain=");
            header.append(domain);
        }
        if ((path = cookie.getPath()) != null && path.length() > 0) {
            this.validatePath(path);
            header.append("; Path=");
            header.append(path);
        }
        if (cookie.getSecure()) {
            header.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            header.append("; HttpOnly");
        }
        if (!(sameSiteCookiesValue = this.getSameSiteCookies()).equals((Object)SameSiteCookies.UNSET)) {
            header.append("; SameSite=");
            header.append(sameSiteCookiesValue.getValue());
        }
        return header.toString();
    }

    private void validateCookieValue(String value) {
        int start = 0;
        int end = value.length();
        if (end > 1 && value.charAt(0) == '\"' && value.charAt(end - 1) == '\"') {
            start = 1;
            --end;
        }
        char[] chars = value.toCharArray();
        for (int i = start; i < end; ++i) {
            char c = chars[i];
            if (c >= '!' && c != '\"' && c != ',' && c != ';' && c != '\\' && c != '\u007f') continue;
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidCharInValue", new Object[]{Integer.toString(c)}));
        }
    }

    private void validateDomain(String domain) {
        int prev = -1;
        int cur = -1;
        char[] chars = domain.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            prev = cur;
            cur = chars[i];
            if (!domainValid.get(cur)) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", new Object[]{domain}));
            }
            if (!(prev != 46 && prev != -1 || cur != 46 && cur != 45)) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", new Object[]{domain}));
            }
            if (prev != 45 || cur != 46) continue;
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", new Object[]{domain}));
        }
        if (cur == 46 || cur == 45) {
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", new Object[]{domain}));
        }
    }

    private void validatePath(String path) {
        char[] chars;
        for (char ch : chars = path.toCharArray()) {
            if (ch >= ' ' && ch <= '~' && ch != ';') continue;
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidPath", new Object[]{path}));
        }
    }

    static {
        int c;
        log = LogFactory.getLog(Rfc6265CookieProcessor.class);
        sm = StringManager.getManager((String)Rfc6265CookieProcessor.class.getPackage().getName());
        domainValid = new BitSet(128);
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            domainValid.set(c);
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            domainValid.set(c);
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            domainValid.set(c);
        }
        domainValid.set(46);
        domainValid.set(45);
    }
}

