/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.DispatcherType
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpUpgradeHandler
 *  javax.servlet.http.Part
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 */
package com.atlassian.springframework.mock.web;

import com.atlassian.springframework.mock.web.DelegatingServletInputStream;
import com.atlassian.springframework.mock.web.HeaderValueHolder;
import com.atlassian.springframework.mock.web.MockAsyncContext;
import com.atlassian.springframework.mock.web.MockHttpSession;
import com.atlassian.springframework.mock.web.MockRequestDispatcher;
import com.atlassian.springframework.mock.web.MockServletContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

public class MockHttpServletRequest
implements HttpServletRequest {
    public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";
    public static final String DEFAULT_SERVER_NAME = "localhost";
    public static final int DEFAULT_SERVER_PORT = 80;
    public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
    public static final String DEFAULT_REMOTE_HOST = "localhost";
    private static final String HTTP = "http";
    public static final String DEFAULT_PROTOCOL = "http";
    private static final String HTTPS = "https";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String HOST_HEADER = "Host";
    private static final String CHARSET_PREFIX = "charset=";
    private static final ServletInputStream EMPTY_SERVLET_INPUT_STREAM = new DelegatingServletInputStream(new ByteArrayInputStream(new byte[0]));
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    private final Map<String, String[]> parameters = new LinkedHashMap<String, String[]>(16);
    private final List<Locale> locales = new LinkedList<Locale>();
    private final ServletContext servletContext;
    private final Map<String, HeaderValueHolder> headers = new LinkedCaseInsensitiveMap();
    private final Set<String> userRoles = new HashSet<String>();
    private final Map<String, Part> parts = new LinkedHashMap<String, Part>();
    private boolean active = true;
    private String characterEncoding;
    private byte[] content;
    private String contentType;
    private String protocol = "http";
    private String scheme = "http";
    private String serverName = "localhost";
    private int serverPort = 80;
    private String remoteAddr = "127.0.0.1";
    private String remoteHost = "localhost";
    private boolean secure = false;
    private int remotePort = 80;
    private String localName = "localhost";
    private String localAddr = "127.0.0.1";
    private int localPort = 80;
    private boolean asyncStarted = false;
    private boolean asyncSupported = false;
    private MockAsyncContext asyncContext;
    private DispatcherType dispatcherType = DispatcherType.REQUEST;
    private String authType;
    private Cookie[] cookies;
    private String method;
    private String pathInfo;
    private String contextPath = "";
    private String queryString;
    private String remoteUser;
    private Principal userPrincipal;
    private String requestedSessionId;
    private String requestURI;
    private String servletPath = "";
    private HttpSession session;
    private boolean requestedSessionIdValid = true;
    private boolean requestedSessionIdFromCookie = true;
    private boolean requestedSessionIdFromURL = false;

    public MockHttpServletRequest() {
        this(null, "", "");
    }

    public MockHttpServletRequest(String method, String requestURI) {
        this(null, method, requestURI);
    }

    public MockHttpServletRequest(ServletContext servletContext) {
        this(servletContext, "", "");
    }

    public MockHttpServletRequest(ServletContext servletContext, String method, String requestURI) {
        this.servletContext = servletContext != null ? servletContext : new MockServletContext();
        this.method = method;
        this.requestURI = requestURI;
        this.locales.add(Locale.ENGLISH);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public boolean isActive() {
        return this.active;
    }

    public void close() {
        this.active = false;
    }

    public void invalidate() {
        this.close();
        this.clearAttributes();
    }

    protected void checkActive() throws IllegalStateException {
        if (!this.active) {
            throw new IllegalStateException("Request is not active anymore");
        }
    }

    public Object getAttribute(String name) {
        this.checkActive();
        return this.attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        this.checkActive();
        return Collections.enumeration(new LinkedHashSet<String>(this.attributes.keySet()));
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
        this.updateContentTypeHeader();
    }

    private void updateContentTypeHeader() {
        if (StringUtils.hasLength((String)this.contentType)) {
            StringBuilder sb = new StringBuilder(this.contentType);
            if (!this.contentType.toLowerCase().contains(CHARSET_PREFIX) && StringUtils.hasLength((String)this.characterEncoding)) {
                sb.append(";").append(CHARSET_PREFIX).append(this.characterEncoding);
            }
            this.doAddHeaderValue(CONTENT_TYPE_HEADER, sb.toString(), true);
        }
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getContentLength() {
        return this.content != null ? this.content.length : -1;
    }

    public long getContentLengthLong() {
        return this.getContentLength();
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        if (contentType != null) {
            int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
            if (charsetIndex != -1) {
                this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
            }
            this.updateContentTypeHeader();
        }
    }

    public ServletInputStream getInputStream() {
        if (this.content != null) {
            return new DelegatingServletInputStream(new ByteArrayInputStream(this.content));
        }
        return EMPTY_SERVLET_INPUT_STREAM;
    }

    public void setParameter(String name, String value) {
        this.setParameter(name, new String[]{value});
    }

    public void setParameter(String name, String[] values) {
        Assert.notNull((Object)name, (String)"Parameter name must not be null");
        this.parameters.put(name, values);
    }

    public void setParameters(Map params) {
        Assert.notNull((Object)params, (String)"Parameter map must not be null");
        for (Object key : params.keySet()) {
            Assert.isInstanceOf(String.class, key, (String)("Parameter map key must be of type [" + String.class.getName() + "]"));
            Object value = params.get(key);
            if (value instanceof String) {
                this.setParameter((String)key, (String)value);
                continue;
            }
            if (value instanceof String[]) {
                this.setParameter((String)key, (String[])value);
                continue;
            }
            throw new IllegalArgumentException("Parameter map value must be single value  or array of type [" + String.class.getName() + "]");
        }
    }

    public void addParameter(String name, String value) {
        this.addParameter(name, new String[]{value});
    }

    public void addParameter(String name, String[] values) {
        Assert.notNull((Object)name, (String)"Parameter name must not be null");
        String[] oldArr = this.parameters.get(name);
        if (oldArr != null) {
            String[] newArr = new String[oldArr.length + values.length];
            System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
            System.arraycopy(values, 0, newArr, oldArr.length, values.length);
            this.parameters.put(name, newArr);
        } else {
            this.parameters.put(name, values);
        }
    }

    public void addParameters(Map params) {
        Assert.notNull((Object)params, (String)"Parameter map must not be null");
        for (Object key : params.keySet()) {
            Assert.isInstanceOf(String.class, key, (String)("Parameter map key must be of type [" + String.class.getName() + "]"));
            Object value = params.get(key);
            if (value instanceof String) {
                this.addParameter((String)key, (String)value);
                continue;
            }
            if (value instanceof String[]) {
                this.addParameter((String)key, (String[])value);
                continue;
            }
            throw new IllegalArgumentException("Parameter map value must be single value  or array of type [" + String.class.getName() + "]");
        }
    }

    public void removeParameter(String name) {
        Assert.notNull((Object)name, (String)"Parameter name must not be null");
        this.parameters.remove(name);
    }

    public void removeAllParameters() {
        this.parameters.clear();
    }

    public String getParameter(String name) {
        String[] arr = name != null ? this.parameters.get(name) : null;
        return arr != null && arr.length > 0 ? arr[0] : null;
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    public String[] getParameterValues(String name) {
        return name != null ? this.parameters.get(name) : null;
    }

    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getServerName() {
        String host = this.getHeader(HOST_HEADER);
        if (host != null) {
            if ((host = host.trim()).startsWith("[")) {
                host = host.substring(1, host.indexOf(93));
            } else if (host.contains(":")) {
                host = host.substring(0, host.indexOf(58));
            }
            return host;
        }
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        int idx;
        String host = this.getHeader(HOST_HEADER);
        if (host != null && (idx = (host = host.trim()).startsWith("[") ? host.indexOf(58, host.indexOf(93)) : host.indexOf(58)) != -1) {
            return Integer.parseInt(host.substring(idx + 1));
        }
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public BufferedReader getReader() throws UnsupportedEncodingException {
        if (this.content != null) {
            ByteArrayInputStream sourceStream = new ByteArrayInputStream(this.content);
            InputStreamReader sourceReader = this.characterEncoding != null ? new InputStreamReader((InputStream)sourceStream, this.characterEncoding) : new InputStreamReader(sourceStream);
            return new BufferedReader(sourceReader);
        }
        return null;
    }

    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setAttribute(String name, Object value) {
        this.checkActive();
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            this.attributes.remove(name);
        }
    }

    public void removeAttribute(String name) {
        this.checkActive();
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        this.attributes.remove(name);
    }

    public void clearAttributes() {
        this.attributes.clear();
    }

    public void addPreferredLocale(Locale locale) {
        Assert.notNull((Object)locale, (String)"Locale must not be null");
        this.locales.add(0, locale);
    }

    public void setPreferredLocales(List<Locale> locales) {
        Assert.notEmpty(locales, (String)"Locale list must not be empty");
        this.locales.clear();
        this.locales.addAll(locales);
    }

    public Locale getLocale() {
        return this.locales.get(0);
    }

    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(this.locales);
    }

    public boolean isSecure() {
        return this.secure || HTTPS.equalsIgnoreCase(this.scheme);
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return new MockRequestDispatcher(path);
    }

    @Deprecated
    public String getRealPath(String path) {
        return this.servletContext.getRealPath(path);
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalAddr() {
        return this.localAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public AsyncContext startAsync() {
        return this.startAsync((ServletRequest)this, null);
    }

    public AsyncContext startAsync(ServletRequest request, ServletResponse response) {
        if (!this.asyncSupported) {
            throw new IllegalStateException("Async not supported");
        }
        this.asyncStarted = true;
        this.asyncContext = new MockAsyncContext(request, response);
        return this.asyncContext;
    }

    public boolean isAsyncStarted() {
        return this.asyncStarted;
    }

    public void setAsyncStarted(boolean asyncStarted) {
        this.asyncStarted = asyncStarted;
    }

    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public AsyncContext getAsyncContext() {
        return this.asyncContext;
    }

    public void setAsyncContext(MockAsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    public DispatcherType getDispatcherType() {
        return this.dispatcherType;
    }

    public void setDispatcherType(DispatcherType dispatcherType) {
        this.dispatcherType = dispatcherType;
    }

    public String getAuthType() {
        return this.authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Cookie[] getCookies() {
        return this.cookies;
    }

    public void setCookies(Cookie ... cookies) {
        this.cookies = cookies;
    }

    public void addHeader(String name, Object value) {
        if (CONTENT_TYPE_HEADER.equalsIgnoreCase(name)) {
            this.setContentType((String)value);
            return;
        }
        this.doAddHeaderValue(name, value, false);
    }

    private void doAddHeaderValue(String name, Object value, boolean replace) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Assert.notNull((Object)value, (String)"Header value must not be null");
        if (header == null || replace) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (value instanceof Collection) {
            header.addValues((Collection)value);
        } else if (value.getClass().isArray()) {
            header.addValueArray(value);
        } else {
            header.addValue(value);
        }
    }

    public long getDateHeader(String name) {
        Object value;
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object object = value = header != null ? header.getValue() : null;
        if (value instanceof Date) {
            return ((Date)value).getTime();
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        if (value != null) {
            throw new IllegalArgumentException("Value for header '" + name + "' is neither a Date nor a Number: " + value);
        }
        return -1L;
    }

    public String getHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return header != null ? header.getStringValue() : null;
    }

    public Enumeration<String> getHeaders(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return Collections.enumeration(header != null ? header.getStringValues() : new LinkedList());
    }

    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headers.keySet());
    }

    public int getIntHeader(String name) {
        Object value;
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object object = value = header != null ? header.getValue() : null;
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String)value);
        }
        if (value != null) {
            throw new NumberFormatException("Value for header '" + name + "' is not a Number: " + value);
        }
        return -1;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathInfo() {
        return this.pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getPathTranslated() {
        return this.pathInfo != null ? this.getRealPath(this.pathInfo) : null;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRemoteUser() {
        return this.remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public void addUserRole(String role) {
        this.userRoles.add(role);
    }

    public boolean isUserInRole(String role) {
        return this.userRoles.contains(role) || this.servletContext instanceof MockServletContext && ((MockServletContext)this.servletContext).getDeclaredRoles().contains(role);
    }

    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer(this.scheme).append("://").append(this.serverName);
        if (this.serverPort > 0 && ("http".equalsIgnoreCase(this.scheme) && this.serverPort != 80 || HTTPS.equalsIgnoreCase(this.scheme) && this.serverPort != 443)) {
            url.append(':').append(this.serverPort);
        }
        if (StringUtils.hasText((String)this.getRequestURI())) {
            url.append(this.getRequestURI());
        }
        return url;
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public HttpSession getSession(boolean create) {
        this.checkActive();
        if (this.session instanceof MockHttpSession && ((MockHttpSession)this.session).isInvalid()) {
            this.session = null;
        }
        if (this.session == null && create) {
            this.session = new MockHttpSession(this.servletContext);
        }
        return this.session;
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public void setSession(HttpSession session) {
        this.session = session;
        if (session instanceof MockHttpSession) {
            MockHttpSession mockSession = (MockHttpSession)session;
            mockSession.access();
        }
    }

    public String changeSessionId() {
        Assert.isTrue((this.session != null ? 1 : 0) != 0, (String)"The request does not have a session");
        if (this.session instanceof MockHttpSession) {
            return ((MockHttpSession)this.session).changeSessionId();
        }
        return this.session.getId();
    }

    public boolean isRequestedSessionIdValid() {
        return this.requestedSessionIdValid;
    }

    public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
        this.requestedSessionIdValid = requestedSessionIdValid;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return this.requestedSessionIdFromCookie;
    }

    public void setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
        this.requestedSessionIdFromCookie = requestedSessionIdFromCookie;
    }

    public boolean isRequestedSessionIdFromURL() {
        return this.requestedSessionIdFromURL;
    }

    public void setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
        this.requestedSessionIdFromURL = requestedSessionIdFromURL;
    }

    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return this.isRequestedSessionIdFromURL();
    }

    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public void logout() throws ServletException {
        this.userPrincipal = null;
        this.remoteUser = null;
        this.authType = null;
    }

    public void addPart(Part part) {
        this.parts.put(part.getName(), part);
    }

    public Part getPart(String name) throws IOException, IllegalStateException, ServletException {
        return this.parts.get(name);
    }

    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        return this.parts.values();
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        try {
            return (T)((HttpUpgradeHandler)handlerClass.newInstance());
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new ServletException((Throwable)e);
        }
    }
}

