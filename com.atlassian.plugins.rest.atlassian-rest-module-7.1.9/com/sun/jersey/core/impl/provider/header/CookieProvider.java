/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.impl.provider.header.WriterUtil;
import com.sun.jersey.spi.HeaderDelegateProvider;
import javax.ws.rs.core.Cookie;

public class CookieProvider
implements HeaderDelegateProvider<Cookie> {
    @Override
    public boolean supports(Class<?> type) {
        return type == Cookie.class;
    }

    @Override
    public String toString(Cookie cookie) {
        StringBuilder b = new StringBuilder();
        b.append("$Version=").append(cookie.getVersion()).append(';');
        b.append(cookie.getName()).append('=');
        WriterUtil.appendQuotedIfWhitespace(b, cookie.getValue());
        if (cookie.getDomain() != null) {
            b.append(";$Domain=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            b.append(";$Path=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getPath());
        }
        return b.toString();
    }

    @Override
    public Cookie fromString(String header) {
        if (header == null) {
            throw new IllegalArgumentException();
        }
        return HttpHeaderReader.readCookie(header);
    }
}

