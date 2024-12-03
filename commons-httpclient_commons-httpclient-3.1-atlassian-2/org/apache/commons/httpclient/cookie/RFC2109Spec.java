/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import java.util.Locale;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.util.ParameterFormatter;

public class RFC2109Spec
extends CookieSpecBase {
    private final ParameterFormatter formatter = new ParameterFormatter();
    public static final String SET_COOKIE_KEY = "set-cookie";

    public RFC2109Spec() {
        this.formatter.setAlwaysUseQuotes(true);
    }

    @Override
    public void parseAttribute(NameValuePair attribute, Cookie cookie) throws MalformedCookieException {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null.");
        }
        String paramName = attribute.getName().toLowerCase(Locale.ENGLISH);
        String paramValue = attribute.getValue();
        if (paramName.equals("path")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for path attribute");
            }
            if (paramValue.trim().equals("")) {
                throw new MalformedCookieException("Blank value for path attribute");
            }
            cookie.setPath(paramValue);
            cookie.setPathAttributeSpecified(true);
        } else if (paramName.equals("version")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for version attribute");
            }
            try {
                cookie.setVersion(Integer.parseInt(paramValue));
            }
            catch (NumberFormatException e) {
                throw new MalformedCookieException("Invalid version: " + e.getMessage());
            }
        } else {
            super.parseAttribute(attribute, cookie);
        }
    }

    @Override
    public void validate(String host, int port, String path, boolean secure, Cookie cookie) throws MalformedCookieException {
        LOG.trace((Object)"enter RFC2109Spec.validate(String, int, String, boolean, Cookie)");
        super.validate(host, port, path, secure, cookie);
        if (cookie.getName().indexOf(32) != -1) {
            throw new MalformedCookieException("Cookie name may not contain blanks");
        }
        if (cookie.getName().startsWith("$")) {
            throw new MalformedCookieException("Cookie name may not start with $");
        }
        if (cookie.isDomainAttributeSpecified() && !cookie.getDomain().equals(host)) {
            if (!cookie.getDomain().startsWith(".")) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must start with a dot");
            }
            int dotIndex = cookie.getDomain().indexOf(46, 1);
            if (dotIndex < 0 || dotIndex == cookie.getDomain().length() - 1) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must contain an embedded dot");
            }
            if (!(host = host.toLowerCase(Locale.ENGLISH)).endsWith(cookie.getDomain())) {
                throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain() + "\". Domain of origin: \"" + host + "\"");
            }
            String hostWithoutDomain = host.substring(0, host.length() - cookie.getDomain().length());
            if (hostWithoutDomain.indexOf(46) != -1) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: host minus domain may not contain any dots");
            }
        }
    }

    @Override
    public boolean domainMatch(String host, String domain) {
        boolean match = host.equals(domain) || domain.startsWith(".") && host.endsWith(domain);
        return match;
    }

    private void formatParam(StringBuffer buffer, NameValuePair param, int version) {
        if (version < 1) {
            buffer.append(param.getName());
            buffer.append("=");
            if (param.getValue() != null) {
                buffer.append(param.getValue());
            }
        } else {
            this.formatter.format(buffer, param);
        }
    }

    private void formatCookieAsVer(StringBuffer buffer, Cookie cookie, int version) {
        String value = cookie.getValue();
        if (value == null) {
            value = "";
        }
        this.formatParam(buffer, new NameValuePair(cookie.getName(), value), version);
        if (cookie.getPath() != null && cookie.isPathAttributeSpecified()) {
            buffer.append("; ");
            this.formatParam(buffer, new NameValuePair("$Path", cookie.getPath()), version);
        }
        if (cookie.getDomain() != null && cookie.isDomainAttributeSpecified()) {
            buffer.append("; ");
            this.formatParam(buffer, new NameValuePair("$Domain", cookie.getDomain()), version);
        }
    }

    @Override
    public String formatCookie(Cookie cookie) {
        LOG.trace((Object)"enter RFC2109Spec.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        int version = cookie.getVersion();
        StringBuffer buffer = new StringBuffer();
        this.formatParam(buffer, new NameValuePair("$Version", Integer.toString(version)), version);
        buffer.append("; ");
        this.formatCookieAsVer(buffer, cookie, version);
        return buffer.toString();
    }

    @Override
    public String formatCookies(Cookie[] cookies) {
        LOG.trace((Object)"enter RFC2109Spec.formatCookieHeader(Cookie[])");
        int version = Integer.MAX_VALUE;
        for (int i = 0; i < cookies.length; ++i) {
            Cookie cookie = cookies[i];
            if (cookie.getVersion() >= version) continue;
            version = cookie.getVersion();
        }
        StringBuffer buffer = new StringBuffer();
        this.formatParam(buffer, new NameValuePair("$Version", Integer.toString(version)), version);
        for (int i = 0; i < cookies.length; ++i) {
            buffer.append("; ");
            this.formatCookieAsVer(buffer, cookies[i], version);
        }
        return buffer.toString();
    }
}

