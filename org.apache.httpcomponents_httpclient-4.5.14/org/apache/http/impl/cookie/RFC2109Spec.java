/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.Obsolete
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.message.BufferedHeader
 *  org.apache.http.util.Args
 *  org.apache.http.util.CharArrayBuffer
 */
package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePathComparator;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicCommentHandler;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.impl.cookie.BasicMaxAgeHandler;
import org.apache.http.impl.cookie.BasicPathHandler;
import org.apache.http.impl.cookie.BasicSecureHandler;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.impl.cookie.RFC2109DomainHandler;
import org.apache.http.impl.cookie.RFC2109VersionHandler;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Obsolete
@Contract(threading=ThreadingBehavior.SAFE)
public class RFC2109Spec
extends CookieSpecBase {
    static final String[] DATE_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy"};
    private final boolean oneHeader;

    public RFC2109Spec(String[] datepatterns, boolean oneHeader) {
        super(new RFC2109VersionHandler(), new BasicPathHandler(){

            @Override
            public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
                if (!this.match(cookie, origin)) {
                    throw new CookieRestrictionViolationException("Illegal 'path' attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
                }
            }
        }, new RFC2109DomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler(datepatterns != null ? (String[])datepatterns.clone() : DATE_PATTERNS));
        this.oneHeader = oneHeader;
    }

    public RFC2109Spec() {
        this(null, false);
    }

    protected RFC2109Spec(boolean oneHeader, CommonCookieAttributeHandler ... handlers) {
        super(handlers);
        this.oneHeader = oneHeader;
    }

    @Override
    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull((Object)header, (String)"Header");
        Args.notNull((Object)origin, (String)"Cookie origin");
        if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        HeaderElement[] elems = header.getElements();
        return this.parse(elems, origin);
    }

    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        String name = cookie.getName();
        if (name.indexOf(32) != -1) {
            throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
        }
        if (name.startsWith("$")) {
            throw new CookieRestrictionViolationException("Cookie name may not start with $");
        }
        super.validate(cookie, origin);
    }

    @Override
    public List<Header> formatCookies(List<Cookie> cookies) {
        List<Cookie> cookieList;
        Args.notEmpty(cookies, (String)"List of cookies");
        if (cookies.size() > 1) {
            cookieList = new ArrayList<Cookie>(cookies);
            Collections.sort(cookieList, CookiePathComparator.INSTANCE);
        } else {
            cookieList = cookies;
        }
        return this.oneHeader ? this.doFormatOneHeader(cookieList) : this.doFormatManyHeaders(cookieList);
    }

    private List<Header> doFormatOneHeader(List<Cookie> cookies) {
        int version = Integer.MAX_VALUE;
        for (Cookie cookie : cookies) {
            if (cookie.getVersion() >= version) continue;
            version = cookie.getVersion();
        }
        CharArrayBuffer buffer = new CharArrayBuffer(40 * cookies.size());
        buffer.append("Cookie");
        buffer.append(": ");
        buffer.append("$Version=");
        buffer.append(Integer.toString(version));
        for (Cookie cooky : cookies) {
            buffer.append("; ");
            Cookie cookie = cooky;
            this.formatCookieAsVer(buffer, cookie, version);
        }
        ArrayList<Header> headers = new ArrayList<Header>(1);
        headers.add((Header)new BufferedHeader(buffer));
        return headers;
    }

    private List<Header> doFormatManyHeaders(List<Cookie> cookies) {
        ArrayList<Header> headers = new ArrayList<Header>(cookies.size());
        for (Cookie cookie : cookies) {
            int version = cookie.getVersion();
            CharArrayBuffer buffer = new CharArrayBuffer(40);
            buffer.append("Cookie: ");
            buffer.append("$Version=");
            buffer.append(Integer.toString(version));
            buffer.append("; ");
            this.formatCookieAsVer(buffer, cookie, version);
            headers.add((Header)new BufferedHeader(buffer));
        }
        return headers;
    }

    protected void formatParamAsVer(CharArrayBuffer buffer, String name, String value, int version) {
        buffer.append(name);
        buffer.append("=");
        if (value != null) {
            if (version > 0) {
                buffer.append('\"');
                buffer.append(value);
                buffer.append('\"');
            } else {
                buffer.append(value);
            }
        }
    }

    protected void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
        this.formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
        if (cookie.getPath() != null && cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("path")) {
            buffer.append("; ");
            this.formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
        }
        if (cookie.getDomain() != null && cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("domain")) {
            buffer.append("; ");
            this.formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
        }
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public Header getVersionHeader() {
        return null;
    }

    public String toString() {
        return "rfc2109";
    }
}

