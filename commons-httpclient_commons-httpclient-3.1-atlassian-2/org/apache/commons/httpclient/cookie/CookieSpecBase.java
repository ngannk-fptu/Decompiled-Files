/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.cookie;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CookieSpecBase
implements CookieSpec {
    protected static final Log LOG = LogFactory.getLog(CookieSpec.class);
    private Collection datepatterns = null;

    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, String header) throws MalformedCookieException {
        LOG.trace((Object)"enter CookieSpecBase.parse(String, port, path, boolean, Header)");
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null.");
        }
        if (path.trim().equals("")) {
            path = "/";
        }
        host = host.toLowerCase(Locale.ENGLISH);
        String defaultPath = path;
        int lastSlashIndex = defaultPath.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        HeaderElement[] headerElements = null;
        boolean isNetscapeCookie = false;
        int i1 = header.toLowerCase(Locale.ENGLISH).indexOf("expires=");
        if (i1 != -1) {
            int i2 = header.indexOf(";", i1 += "expires=".length());
            if (i2 == -1) {
                i2 = header.length();
            }
            try {
                DateUtil.parseDate(header.substring(i1, i2), this.datepatterns);
                isNetscapeCookie = true;
            }
            catch (DateParseException e) {
                // empty catch block
            }
        }
        headerElements = isNetscapeCookie ? new HeaderElement[]{new HeaderElement(header.toCharArray())} : HeaderElement.parseElements(header.toCharArray());
        Cookie[] cookies = new Cookie[headerElements.length];
        for (int i = 0; i < headerElements.length; ++i) {
            HeaderElement headerelement = headerElements[i];
            Cookie cookie = null;
            try {
                cookie = new Cookie(host, headerelement.getName(), headerelement.getValue(), defaultPath, null, false);
            }
            catch (IllegalArgumentException e) {
                throw new MalformedCookieException(e.getMessage());
            }
            NameValuePair[] parameters = headerelement.getParameters();
            if (parameters != null) {
                for (int j = 0; j < parameters.length; ++j) {
                    this.parseAttribute(parameters[j], cookie);
                }
            }
            cookies[i] = cookie;
        }
        return cookies;
    }

    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, Header header) throws MalformedCookieException {
        LOG.trace((Object)"enter CookieSpecBase.parse(String, port, path, boolean, String)");
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null.");
        }
        return this.parse(host, port, path, secure, header.getValue());
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
            if (paramValue == null || paramValue.trim().equals("")) {
                paramValue = "/";
            }
            cookie.setPath(paramValue);
            cookie.setPathAttributeSpecified(true);
        } else if (paramName.equals("domain")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for domain attribute");
            }
            if (paramValue.trim().equals("")) {
                throw new MalformedCookieException("Blank value for domain attribute");
            }
            cookie.setDomain(paramValue);
            cookie.setDomainAttributeSpecified(true);
        } else if (paramName.equals("max-age")) {
            int age;
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for max-age attribute");
            }
            try {
                age = Integer.parseInt(paramValue);
            }
            catch (NumberFormatException e) {
                throw new MalformedCookieException("Invalid max-age attribute: " + e.getMessage());
            }
            cookie.setExpiryDate(new Date(System.currentTimeMillis() + (long)age * 1000L));
        } else if (paramName.equals("secure")) {
            cookie.setSecure(true);
        } else if (paramName.equals("comment")) {
            cookie.setComment(paramValue);
        } else if (paramName.equals("expires")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for expires attribute");
            }
            try {
                cookie.setExpiryDate(DateUtil.parseDate(paramValue, this.datepatterns));
            }
            catch (DateParseException dpe) {
                LOG.debug((Object)"Error parsing cookie date", (Throwable)dpe);
                throw new MalformedCookieException("Unable to parse expiration date parameter: " + paramValue);
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Unrecognized cookie attribute: " + attribute.toString()));
        }
    }

    @Override
    public Collection getValidDateFormats() {
        return this.datepatterns;
    }

    @Override
    public void setValidDateFormats(Collection datepatterns) {
        this.datepatterns = datepatterns;
    }

    @Override
    public void validate(String host, int port, String path, boolean secure, Cookie cookie) throws MalformedCookieException {
        LOG.trace((Object)"enter CookieSpecBase.validate(String, port, path, boolean, Cookie)");
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        if (path.trim().equals("")) {
            path = "/";
        }
        host = host.toLowerCase(Locale.ENGLISH);
        if (cookie.getVersion() < 0) {
            throw new MalformedCookieException("Illegal version number " + cookie.getValue());
        }
        if (host.indexOf(".") >= 0) {
            if (!host.endsWith(cookie.getDomain())) {
                String s = cookie.getDomain();
                if (s.startsWith(".")) {
                    s = s.substring(1, s.length());
                }
                if (!host.equals(s)) {
                    throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain() + "\". Domain of origin: \"" + host + "\"");
                }
            }
        } else if (!host.equals(cookie.getDomain())) {
            throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain() + "\". Domain of origin: \"" + host + "\"");
        }
        if (!path.startsWith(cookie.getPath())) {
            throw new MalformedCookieException("Illegal path attribute \"" + cookie.getPath() + "\". Path of origin: \"" + path + "\"");
        }
    }

    @Override
    public boolean match(String host, int port, String path, boolean secure, Cookie cookie) {
        LOG.trace((Object)"enter CookieSpecBase.match(String, int, String, boolean, Cookie");
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (path.trim().equals("")) {
            path = "/";
        }
        host = host.toLowerCase(Locale.ENGLISH);
        if (cookie.getDomain() == null) {
            LOG.warn((Object)"Invalid cookie state: domain not specified");
            return false;
        }
        if (cookie.getPath() == null) {
            LOG.warn((Object)"Invalid cookie state: path not specified");
            return false;
        }
        return !(cookie.getExpiryDate() != null && !cookie.getExpiryDate().after(new Date()) || !this.domainMatch(host, cookie.getDomain()) || !this.pathMatch(path, cookie.getPath()) || cookie.getSecure() && !secure);
    }

    @Override
    public boolean domainMatch(String host, String domain) {
        if (host.equals(domain)) {
            return true;
        }
        if (!domain.startsWith(".")) {
            domain = "." + domain;
        }
        return host.endsWith(domain) || host.equals(domain.substring(1));
    }

    @Override
    public boolean pathMatch(String path, String topmostPath) {
        boolean match = path.startsWith(topmostPath);
        if (match && path.length() != topmostPath.length() && !topmostPath.endsWith("/")) {
            match = path.charAt(topmostPath.length()) == PATH_DELIM_CHAR;
        }
        return match;
    }

    @Override
    public Cookie[] match(String host, int port, String path, boolean secure, Cookie[] cookies) {
        LOG.trace((Object)"enter CookieSpecBase.match(String, int, String, boolean, Cookie[])");
        if (cookies == null) {
            return null;
        }
        LinkedList matching = new LinkedList();
        for (int i = 0; i < cookies.length; ++i) {
            if (!this.match(host, port, path, secure, cookies[i])) continue;
            CookieSpecBase.addInPathOrder(matching, cookies[i]);
        }
        return matching.toArray(new Cookie[matching.size()]);
    }

    private static void addInPathOrder(List list, Cookie addCookie) {
        Cookie c;
        int i = 0;
        for (i = 0; i < list.size() && addCookie.compare(addCookie, c = (Cookie)list.get(i)) <= 0; ++i) {
        }
        list.add(i, addCookie);
    }

    @Override
    public String formatCookie(Cookie cookie) {
        LOG.trace((Object)"enter CookieSpecBase.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        StringBuffer buf = new StringBuffer();
        buf.append(cookie.getName());
        buf.append("=");
        String s = cookie.getValue();
        if (s != null) {
            buf.append(s);
        }
        return buf.toString();
    }

    @Override
    public String formatCookies(Cookie[] cookies) throws IllegalArgumentException {
        LOG.trace((Object)"enter CookieSpecBase.formatCookies(Cookie[])");
        if (cookies == null) {
            throw new IllegalArgumentException("Cookie array may not be null");
        }
        if (cookies.length == 0) {
            throw new IllegalArgumentException("Cookie array may not be empty");
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cookies.length; ++i) {
            if (i > 0) {
                buffer.append("; ");
            }
            buffer.append(this.formatCookie(cookies[i]));
        }
        return buffer.toString();
    }

    @Override
    public Header formatCookieHeader(Cookie[] cookies) {
        LOG.trace((Object)"enter CookieSpecBase.formatCookieHeader(Cookie[])");
        return new Header("Cookie", this.formatCookies(cookies));
    }

    @Override
    public Header formatCookieHeader(Cookie cookie) {
        LOG.trace((Object)"enter CookieSpecBase.formatCookieHeader(Cookie)");
        return new Header("Cookie", this.formatCookie(cookie));
    }
}

