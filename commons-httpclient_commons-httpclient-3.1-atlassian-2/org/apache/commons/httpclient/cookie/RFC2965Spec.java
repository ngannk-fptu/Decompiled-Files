/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.Cookie2;
import org.apache.commons.httpclient.cookie.CookieAttributeHandler;
import org.apache.commons.httpclient.cookie.CookieOrigin;
import org.apache.commons.httpclient.cookie.CookiePathComparator;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.cookie.CookieVersionSupport;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.cookie.RFC2109Spec;
import org.apache.commons.httpclient.util.ParameterFormatter;

public class RFC2965Spec
extends CookieSpecBase
implements CookieVersionSupport {
    private static final Comparator PATH_COMPOARATOR = new CookiePathComparator();
    public static final String SET_COOKIE2_KEY = "set-cookie2";
    private final ParameterFormatter formatter = new ParameterFormatter();
    private final List attribHandlerList;
    private final Map attribHandlerMap;
    private final CookieSpec rfc2109;

    public RFC2965Spec() {
        this.formatter.setAlwaysUseQuotes(true);
        this.attribHandlerMap = new HashMap(10);
        this.attribHandlerList = new ArrayList(10);
        this.rfc2109 = new RFC2109Spec();
        this.registerAttribHandler("path", new Cookie2PathAttributeHandler());
        this.registerAttribHandler("domain", new Cookie2DomainAttributeHandler());
        this.registerAttribHandler("port", new Cookie2PortAttributeHandler());
        this.registerAttribHandler("max-age", new Cookie2MaxageAttributeHandler());
        this.registerAttribHandler("secure", new CookieSecureAttributeHandler());
        this.registerAttribHandler("comment", new CookieCommentAttributeHandler());
        this.registerAttribHandler("commenturl", new CookieCommentUrlAttributeHandler());
        this.registerAttribHandler("discard", new CookieDiscardAttributeHandler());
        this.registerAttribHandler("version", new Cookie2VersionAttributeHandler());
    }

    protected void registerAttribHandler(String name, CookieAttributeHandler handler) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name may not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Attribute handler may not be null");
        }
        if (!this.attribHandlerList.contains(handler)) {
            this.attribHandlerList.add(handler);
        }
        this.attribHandlerMap.put(name, handler);
    }

    protected CookieAttributeHandler findAttribHandler(String name) {
        return (CookieAttributeHandler)this.attribHandlerMap.get(name);
    }

    protected CookieAttributeHandler getAttribHandler(String name) {
        CookieAttributeHandler handler = this.findAttribHandler(name);
        if (handler == null) {
            throw new IllegalStateException("Handler not registered for " + name + " attribute.");
        }
        return handler;
    }

    protected Iterator getAttribHandlerIterator() {
        return this.attribHandlerList.iterator();
    }

    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, Header header) throws MalformedCookieException {
        LOG.trace((Object)"enter RFC2965.parse(String, int, String, boolean, Header)");
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null.");
        }
        if (header.getName() == null) {
            throw new IllegalArgumentException("Header name may not be null.");
        }
        if (header.getName().equalsIgnoreCase(SET_COOKIE2_KEY)) {
            return this.parse(host, port, path, secure, header.getValue());
        }
        if (header.getName().equalsIgnoreCase("set-cookie")) {
            return this.rfc2109.parse(host, port, path, secure, header.getValue());
        }
        throw new MalformedCookieException("Header name is not valid. RFC 2965 supports \"set-cookie\" and \"set-cookie2\" headers.");
    }

    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, String header) throws MalformedCookieException {
        LOG.trace((Object)"enter RFC2965Spec.parse(String, int, String, boolean, String)");
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
        host = RFC2965Spec.getEffectiveHost(host);
        HeaderElement[] headerElements = HeaderElement.parseElements(header.toCharArray());
        LinkedList<Cookie2> cookies = new LinkedList<Cookie2>();
        for (int i = 0; i < headerElements.length; ++i) {
            HeaderElement headerelement = headerElements[i];
            Cookie2 cookie = null;
            try {
                cookie = new Cookie2(host, headerelement.getName(), headerelement.getValue(), path, null, false, new int[]{port});
            }
            catch (IllegalArgumentException ex) {
                throw new MalformedCookieException(ex.getMessage());
            }
            NameValuePair[] parameters = headerelement.getParameters();
            if (parameters != null) {
                HashMap<String, NameValuePair> attribmap = new HashMap<String, NameValuePair>(parameters.length);
                for (int j = parameters.length - 1; j >= 0; --j) {
                    NameValuePair param = parameters[j];
                    attribmap.put(param.getName().toLowerCase(Locale.ENGLISH), param);
                }
                for (Map.Entry entry : attribmap.entrySet()) {
                    this.parseAttribute((NameValuePair)entry.getValue(), cookie);
                }
            }
            cookies.add(cookie);
        }
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    @Override
    public void parseAttribute(NameValuePair attribute, Cookie cookie) throws MalformedCookieException {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute may not be null.");
        }
        if (attribute.getName() == null) {
            throw new IllegalArgumentException("Attribute Name may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null.");
        }
        String paramName = attribute.getName().toLowerCase(Locale.ENGLISH);
        String paramValue = attribute.getValue();
        CookieAttributeHandler handler = this.findAttribHandler(paramName);
        if (handler == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Unrecognized cookie attribute: " + attribute.toString()));
            }
        } else {
            handler.parse(cookie, paramValue);
        }
    }

    @Override
    public void validate(String host, int port, String path, boolean secure, Cookie cookie) throws MalformedCookieException {
        LOG.trace((Object)"enter RFC2965Spec.validate(String, int, String, boolean, Cookie)");
        if (cookie instanceof Cookie2) {
            if (cookie.getName().indexOf(32) != -1) {
                throw new MalformedCookieException("Cookie name may not contain blanks");
            }
            if (cookie.getName().startsWith("$")) {
                throw new MalformedCookieException("Cookie name may not start with $");
            }
            CookieOrigin origin = new CookieOrigin(RFC2965Spec.getEffectiveHost(host), port, path, secure);
            Iterator i = this.getAttribHandlerIterator();
            while (i.hasNext()) {
                CookieAttributeHandler handler = (CookieAttributeHandler)i.next();
                handler.validate(cookie, origin);
            }
        } else {
            this.rfc2109.validate(host, port, path, secure, cookie);
        }
    }

    @Override
    public boolean match(String host, int port, String path, boolean secure, Cookie cookie) {
        LOG.trace((Object)"enter RFC2965.match(String, int, String, boolean, Cookie");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (cookie instanceof Cookie2) {
            if (cookie.isPersistent() && cookie.isExpired()) {
                return false;
            }
            CookieOrigin origin = new CookieOrigin(RFC2965Spec.getEffectiveHost(host), port, path, secure);
            Iterator i = this.getAttribHandlerIterator();
            while (i.hasNext()) {
                CookieAttributeHandler handler = (CookieAttributeHandler)i.next();
                if (handler.match(cookie, origin)) continue;
                return false;
            }
            return true;
        }
        return this.rfc2109.match(host, port, path, secure, cookie);
    }

    private void doFormatCookie2(Cookie2 cookie, StringBuffer buffer) {
        String name = cookie.getName();
        String value = cookie.getValue();
        if (value == null) {
            value = "";
        }
        this.formatter.format(buffer, new NameValuePair(name, value));
        if (cookie.getDomain() != null && cookie.isDomainAttributeSpecified()) {
            buffer.append("; ");
            this.formatter.format(buffer, new NameValuePair("$Domain", cookie.getDomain()));
        }
        if (cookie.getPath() != null && cookie.isPathAttributeSpecified()) {
            buffer.append("; ");
            this.formatter.format(buffer, new NameValuePair("$Path", cookie.getPath()));
        }
        if (cookie.isPortAttributeSpecified()) {
            String portValue = "";
            if (!cookie.isPortAttributeBlank()) {
                portValue = this.createPortAttribute(cookie.getPorts());
            }
            buffer.append("; ");
            this.formatter.format(buffer, new NameValuePair("$Port", portValue));
        }
    }

    @Override
    public String formatCookie(Cookie cookie) {
        LOG.trace((Object)"enter RFC2965Spec.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (cookie instanceof Cookie2) {
            Cookie2 cookie2 = (Cookie2)cookie;
            int version = cookie2.getVersion();
            StringBuffer buffer = new StringBuffer();
            this.formatter.format(buffer, new NameValuePair("$Version", Integer.toString(version)));
            buffer.append("; ");
            this.doFormatCookie2(cookie2, buffer);
            return buffer.toString();
        }
        return this.rfc2109.formatCookie(cookie);
    }

    @Override
    public String formatCookies(Cookie[] cookies) {
        LOG.trace((Object)"enter RFC2965Spec.formatCookieHeader(Cookie[])");
        if (cookies == null) {
            throw new IllegalArgumentException("Cookies may not be null");
        }
        boolean hasOldStyleCookie = false;
        int version = -1;
        for (int i = 0; i < cookies.length; ++i) {
            Cookie cookie = cookies[i];
            if (!(cookie instanceof Cookie2)) {
                hasOldStyleCookie = true;
                break;
            }
            if (cookie.getVersion() <= version) continue;
            version = cookie.getVersion();
        }
        if (version < 0) {
            version = 0;
        }
        if (hasOldStyleCookie || version < 1) {
            return this.rfc2109.formatCookies(cookies);
        }
        Arrays.sort(cookies, PATH_COMPOARATOR);
        StringBuffer buffer = new StringBuffer();
        this.formatter.format(buffer, new NameValuePair("$Version", Integer.toString(version)));
        for (int i = 0; i < cookies.length; ++i) {
            buffer.append("; ");
            Cookie2 cookie = (Cookie2)cookies[i];
            this.doFormatCookie2(cookie, buffer);
        }
        return buffer.toString();
    }

    private String createPortAttribute(int[] ports) {
        StringBuffer portValue = new StringBuffer();
        int len = ports.length;
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                portValue.append(",");
            }
            portValue.append(ports[i]);
        }
        return portValue.toString();
    }

    private int[] parsePortAttribute(String portValue) throws MalformedCookieException {
        StringTokenizer st = new StringTokenizer(portValue, ",");
        int[] ports = new int[st.countTokens()];
        try {
            int i = 0;
            while (st.hasMoreTokens()) {
                ports[i] = Integer.parseInt(st.nextToken().trim());
                if (ports[i] < 0) {
                    throw new MalformedCookieException("Invalid Port attribute.");
                }
                ++i;
            }
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid Port attribute: " + e.getMessage());
        }
        return ports;
    }

    private static String getEffectiveHost(String host) {
        String effectiveHost = host.toLowerCase(Locale.ENGLISH);
        if (host.indexOf(46) < 0) {
            effectiveHost = effectiveHost + ".local";
        }
        return effectiveHost;
    }

    @Override
    public boolean domainMatch(String host, String domain) {
        boolean match = host.equals(domain) || domain.startsWith(".") && host.endsWith(domain);
        return match;
    }

    private boolean portMatch(int port, int[] ports) {
        boolean portInList = false;
        int len = ports.length;
        for (int i = 0; i < len; ++i) {
            if (port != ports[i]) continue;
            portInList = true;
            break;
        }
        return portInList;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public Header getVersionHeader() {
        ParameterFormatter formatter = new ParameterFormatter();
        StringBuffer buffer = new StringBuffer();
        formatter.format(buffer, new NameValuePair("$Version", Integer.toString(this.getVersion())));
        return new Header("Cookie2", buffer.toString(), true);
    }

    private class Cookie2VersionAttributeHandler
    implements CookieAttributeHandler {
        private Cookie2VersionAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String value) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (cookie instanceof Cookie2) {
                Cookie2 cookie2 = (Cookie2)cookie;
                if (value == null) {
                    throw new MalformedCookieException("Missing value for version attribute");
                }
                int version = -1;
                try {
                    version = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {
                    version = -1;
                }
                if (version < 0) {
                    throw new MalformedCookieException("Invalid cookie version.");
                }
                cookie2.setVersion(version);
                cookie2.setVersionAttributeSpecified(true);
            }
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            Cookie2 cookie2;
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (cookie instanceof Cookie2 && !(cookie2 = (Cookie2)cookie).isVersionAttributeSpecified()) {
                throw new MalformedCookieException("Violates RFC 2965. Version attribute is required.");
            }
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            return true;
        }
    }

    private class CookieDiscardAttributeHandler
    implements CookieAttributeHandler {
        private CookieDiscardAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String commenturl) throws MalformedCookieException {
            if (cookie instanceof Cookie2) {
                Cookie2 cookie2 = (Cookie2)cookie;
                cookie2.setDiscard(true);
            }
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            return true;
        }
    }

    private class CookieCommentUrlAttributeHandler
    implements CookieAttributeHandler {
        private CookieCommentUrlAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String commenturl) throws MalformedCookieException {
            if (cookie instanceof Cookie2) {
                Cookie2 cookie2 = (Cookie2)cookie;
                cookie2.setCommentURL(commenturl);
            }
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            return true;
        }
    }

    private class CookieCommentAttributeHandler
    implements CookieAttributeHandler {
        private CookieCommentAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String comment) throws MalformedCookieException {
            cookie.setComment(comment);
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            return true;
        }
    }

    private class CookieSecureAttributeHandler
    implements CookieAttributeHandler {
        private CookieSecureAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String secure) throws MalformedCookieException {
            cookie.setSecure(true);
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            return cookie.getSecure() == origin.isSecure();
        }
    }

    private class Cookie2MaxageAttributeHandler
    implements CookieAttributeHandler {
        private Cookie2MaxageAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String value) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (value == null) {
                throw new MalformedCookieException("Missing value for max-age attribute");
            }
            int age = -1;
            try {
                age = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                age = -1;
            }
            if (age < 0) {
                throw new MalformedCookieException("Invalid max-age attribute.");
            }
            cookie.setExpiryDate(new Date(System.currentTimeMillis() + (long)age * 1000L));
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) {
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            return true;
        }
    }

    private class Cookie2PortAttributeHandler
    implements CookieAttributeHandler {
        private Cookie2PortAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String portValue) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (cookie instanceof Cookie2) {
                Cookie2 cookie2 = (Cookie2)cookie;
                if (portValue == null || portValue.trim().equals("")) {
                    cookie2.setPortAttributeBlank(true);
                } else {
                    int[] ports = RFC2965Spec.this.parsePortAttribute(portValue);
                    cookie2.setPorts(ports);
                }
                cookie2.setPortAttributeSpecified(true);
            }
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            if (cookie instanceof Cookie2) {
                Cookie2 cookie2 = (Cookie2)cookie;
                int port = origin.getPort();
                if (cookie2.isPortAttributeSpecified() && !RFC2965Spec.this.portMatch(port, cookie2.getPorts())) {
                    throw new MalformedCookieException("Port attribute violates RFC 2965: Request port not found in cookie's port list.");
                }
            }
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            if (cookie instanceof Cookie2) {
                Cookie2 cookie2 = (Cookie2)cookie;
                int port = origin.getPort();
                if (cookie2.isPortAttributeSpecified()) {
                    if (cookie2.getPorts() == null) {
                        CookieSpecBase.LOG.warn((Object)"Invalid cookie state: port not specified");
                        return false;
                    }
                    if (!RFC2965Spec.this.portMatch(port, cookie2.getPorts())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private class Cookie2DomainAttributeHandler
    implements CookieAttributeHandler {
        private Cookie2DomainAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String domain) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (domain == null) {
                throw new MalformedCookieException("Missing value for domain attribute");
            }
            if (domain.trim().equals("")) {
                throw new MalformedCookieException("Blank value for domain attribute");
            }
            if (!(domain = domain.toLowerCase(Locale.ENGLISH)).startsWith(".")) {
                domain = "." + domain;
            }
            cookie.setDomain(domain);
            cookie.setDomainAttributeSpecified(true);
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            String host = origin.getHost().toLowerCase(Locale.ENGLISH);
            if (cookie.getDomain() == null) {
                throw new MalformedCookieException("Invalid cookie state: domain not specified");
            }
            String cookieDomain = cookie.getDomain().toLowerCase(Locale.ENGLISH);
            if (cookie.isDomainAttributeSpecified()) {
                if (!cookieDomain.startsWith(".")) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must start with a dot");
                }
                int dotIndex = cookieDomain.indexOf(46, 1);
                if (!(dotIndex >= 0 && dotIndex != cookieDomain.length() - 1 || cookieDomain.equals(".local"))) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: the value contains no embedded dots " + "and the value is not .local");
                }
                if (!RFC2965Spec.this.domainMatch(host, cookieDomain)) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: effective host name does not " + "domain-match domain attribute.");
                }
                String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
                if (effectiveHostWithoutDomain.indexOf(46) != -1) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: " + "effective host minus domain may not contain any dots");
                }
            } else if (!cookie.getDomain().equals(host)) {
                throw new MalformedCookieException("Illegal domain attribute: \"" + cookie.getDomain() + "\"." + "Domain of origin: \"" + host + "\"");
            }
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            String cookieDomain;
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            String host = origin.getHost().toLowerCase(Locale.ENGLISH);
            if (!RFC2965Spec.this.domainMatch(host, cookieDomain = cookie.getDomain())) {
                return false;
            }
            String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
            return effectiveHostWithoutDomain.indexOf(46) == -1;
        }
    }

    private class Cookie2PathAttributeHandler
    implements CookieAttributeHandler {
        private Cookie2PathAttributeHandler() {
        }

        @Override
        public void parse(Cookie cookie, String path) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (path == null) {
                throw new MalformedCookieException("Missing value for path attribute");
            }
            if (path.trim().equals("")) {
                throw new MalformedCookieException("Blank value for path attribute");
            }
            cookie.setPath(path);
            cookie.setPathAttributeSpecified(true);
        }

        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            String path = origin.getPath();
            if (path == null) {
                throw new IllegalArgumentException("Path of origin host may not be null.");
            }
            if (cookie.getPath() == null) {
                throw new MalformedCookieException("Invalid cookie state: path attribute is null.");
            }
            if (path.trim().equals("")) {
                path = "/";
            }
            if (!RFC2965Spec.this.pathMatch(path, cookie.getPath())) {
                throw new MalformedCookieException("Illegal path attribute \"" + cookie.getPath() + "\". Path of origin: \"" + path + "\"");
            }
        }

        @Override
        public boolean match(Cookie cookie, CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            String path = origin.getPath();
            if (cookie.getPath() == null) {
                CookieSpecBase.LOG.warn((Object)"Invalid cookie state: path attribute is null.");
                return false;
            }
            if (path.trim().equals("")) {
                path = "/";
            }
            return RFC2965Spec.this.pathMatch(path, cookie.getPath());
        }
    }
}

