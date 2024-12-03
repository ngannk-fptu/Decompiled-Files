/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.PushBuilder
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.http.Parameters
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.res.StringManager;

class ApplicationHttpRequest
extends HttpServletRequestWrapper {
    private static final StringManager sm = StringManager.getManager(ApplicationHttpRequest.class);
    protected static final String[] specials = new String[]{"javax.servlet.include.request_uri", "javax.servlet.include.context_path", "javax.servlet.include.servlet_path", "javax.servlet.include.path_info", "javax.servlet.include.query_string", "javax.servlet.include.mapping", "javax.servlet.forward.request_uri", "javax.servlet.forward.context_path", "javax.servlet.forward.servlet_path", "javax.servlet.forward.path_info", "javax.servlet.forward.query_string", "javax.servlet.forward.mapping"};
    private static final Map<String, Integer> specialsMap = new HashMap<String, Integer>();
    private static final int SPECIALS_FIRST_FORWARD_INDEX = 6;
    protected final Context context;
    protected String contextPath = null;
    protected final boolean crossContext;
    protected DispatcherType dispatcherType = null;
    protected Map<String, String[]> parameters = null;
    private boolean parsedParams = false;
    protected String pathInfo = null;
    private String queryParamString = null;
    protected String queryString = null;
    protected Object requestDispatcherPath = null;
    protected String requestURI = null;
    protected String servletPath = null;
    private HttpServletMapping mapping = null;
    protected Session session = null;
    protected final Object[] specialAttributes = new Object[specials.length];

    ApplicationHttpRequest(HttpServletRequest request, Context context, boolean crossContext) {
        super(request);
        this.context = context;
        this.crossContext = crossContext;
        this.setRequest(request);
    }

    public ServletContext getServletContext() {
        if (this.context == null) {
            return null;
        }
        return this.context.getServletContext();
    }

    public Object getAttribute(String name) {
        if (name.equals("org.apache.catalina.core.DISPATCHER_TYPE")) {
            return this.dispatcherType;
        }
        if (name.equals("org.apache.catalina.core.DISPATCHER_REQUEST_PATH")) {
            if (this.requestDispatcherPath != null) {
                return this.requestDispatcherPath.toString();
            }
            return null;
        }
        int pos = this.getSpecial(name);
        if (pos == -1) {
            return this.getRequest().getAttribute(name);
        }
        if (this.specialAttributes[pos] == null && this.specialAttributes[6] == null && pos >= 6) {
            return this.getRequest().getAttribute(name);
        }
        return this.specialAttributes[pos];
    }

    public Enumeration<String> getAttributeNames() {
        return new AttributeNamesEnumerator();
    }

    public void removeAttribute(String name) {
        if (!this.removeSpecial(name)) {
            this.getRequest().removeAttribute(name);
        }
    }

    public void setAttribute(String name, Object value) {
        if (name.equals("org.apache.catalina.core.DISPATCHER_TYPE")) {
            this.dispatcherType = (DispatcherType)value;
            return;
        }
        if (name.equals("org.apache.catalina.core.DISPATCHER_REQUEST_PATH")) {
            this.requestDispatcherPath = value;
            return;
        }
        if (!this.setSpecial(name, value)) {
            this.getRequest().setAttribute(name, value);
        }
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        if (this.context == null) {
            return null;
        }
        if (path == null) {
            return null;
        }
        int fragmentPos = path.indexOf(35);
        if (fragmentPos > -1) {
            this.context.getLogger().warn((Object)sm.getString("applicationHttpRequest.fragmentInDispatchPath", new Object[]{path}));
            path = path.substring(0, fragmentPos);
        }
        if (path.startsWith("/")) {
            return this.context.getServletContext().getRequestDispatcher(path);
        }
        String servletPath = (String)this.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = this.getServletPath();
        }
        String pathInfo = this.getPathInfo();
        String requestPath = null;
        requestPath = pathInfo == null ? servletPath : servletPath + pathInfo;
        int pos = requestPath.lastIndexOf(47);
        String relative = null;
        relative = this.context.getDispatchersUseEncodedPaths() ? (pos >= 0 ? URLEncoder.DEFAULT.encode(requestPath.substring(0, pos + 1), StandardCharsets.UTF_8) + path : URLEncoder.DEFAULT.encode(requestPath, StandardCharsets.UTF_8) + path) : (pos >= 0 ? requestPath.substring(0, pos + 1) + path : requestPath + path);
        return this.context.getServletContext().getRequestDispatcher(relative);
    }

    public DispatcherType getDispatcherType() {
        return this.dispatcherType;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public String getParameter(String name) {
        this.parseParameters();
        String[] value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        return value[0];
    }

    public Map<String, String[]> getParameterMap() {
        this.parseParameters();
        return this.parameters;
    }

    public Enumeration<String> getParameterNames() {
        this.parseParameters();
        return Collections.enumeration(this.parameters.keySet());
    }

    public String[] getParameterValues(String name) {
        this.parseParameters();
        return this.parameters.get(name);
    }

    public String getPathInfo() {
        return this.pathInfo;
    }

    public String getPathTranslated() {
        if (this.getPathInfo() == null || this.getServletContext() == null) {
            return null;
        }
        return this.getServletContext().getRealPath(this.getPathInfo());
    }

    public String getQueryString() {
        return this.queryString;
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    public StringBuffer getRequestURL() {
        return RequestUtil.getRequestURL((HttpServletRequest)this);
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public HttpServletMapping getHttpServletMapping() {
        return this.mapping;
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public HttpSession getSession(boolean create) {
        if (this.crossContext) {
            if (this.context == null) {
                return null;
            }
            if (this.session != null && this.session.isValid()) {
                return this.session.getSession();
            }
            HttpSession other = super.getSession(false);
            if (create && other == null) {
                other = super.getSession(true);
            }
            if (other != null) {
                Session localSession = null;
                try {
                    localSession = this.context.getManager().findSession(other.getId());
                    if (localSession != null && !localSession.isValid()) {
                        localSession = null;
                    }
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (localSession == null && create) {
                    localSession = this.context.getManager().createSession(other.getId());
                }
                if (localSession != null) {
                    localSession.access();
                    this.session = localSession;
                    return this.session.getSession();
                }
            }
            return null;
        }
        return super.getSession(create);
    }

    public boolean isRequestedSessionIdValid() {
        if (this.crossContext) {
            String requestedSessionId = this.getRequestedSessionId();
            if (requestedSessionId == null) {
                return false;
            }
            if (this.context == null) {
                return false;
            }
            Manager manager = this.context.getManager();
            if (manager == null) {
                return false;
            }
            Session session = null;
            try {
                session = manager.findSession(requestedSessionId);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return session != null && session.isValid();
        }
        return super.isRequestedSessionIdValid();
    }

    public PushBuilder newPushBuilder() {
        ServletRequest current = this.getRequest();
        while (current instanceof ServletRequestWrapper) {
            current = ((ServletRequestWrapper)current).getRequest();
        }
        if (current instanceof RequestFacade) {
            return ((RequestFacade)current).newPushBuilder((HttpServletRequest)this);
        }
        return null;
    }

    public void recycle() {
        if (this.session != null) {
            try {
                this.session.endAccess();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.context.getLogger().warn((Object)sm.getString("applicationHttpRequest.sessionEndAccessFail"), t);
            }
        }
    }

    void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    void setRequest(HttpServletRequest request) {
        super.setRequest((ServletRequest)request);
        this.dispatcherType = (DispatcherType)request.getAttribute("org.apache.catalina.core.DISPATCHER_TYPE");
        this.requestDispatcherPath = request.getAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH");
        this.contextPath = request.getContextPath();
        this.pathInfo = request.getPathInfo();
        this.queryString = request.getQueryString();
        this.requestURI = request.getRequestURI();
        this.servletPath = request.getServletPath();
        this.mapping = request.getHttpServletMapping();
    }

    void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    void parseParameters() {
        if (this.parsedParams) {
            return;
        }
        this.parameters = new ParameterMap<String, String[]>();
        this.parameters.putAll(this.getRequest().getParameterMap());
        this.mergeParameters();
        ((ParameterMap)this.parameters).setLocked(true);
        this.parsedParams = true;
    }

    void setQueryParams(String queryString) {
        this.queryParamString = queryString;
    }

    void setMapping(HttpServletMapping mapping) {
        this.mapping = mapping;
    }

    protected boolean isSpecial(String name) {
        return specialsMap.containsKey(name);
    }

    protected int getSpecial(String name) {
        Integer index = specialsMap.get(name);
        if (index == null) {
            return -1;
        }
        return index;
    }

    protected boolean setSpecial(String name, Object value) {
        Integer index = specialsMap.get(name);
        if (index == null) {
            return false;
        }
        this.specialAttributes[index.intValue()] = value;
        return true;
    }

    protected boolean removeSpecial(String name) {
        return this.setSpecial(name, null);
    }

    private String[] mergeValues(String[] values1, String[] values2) {
        ArrayList<String> results = new ArrayList<String>();
        if (values1 != null) {
            results.addAll(Arrays.asList(values1));
        }
        if (values2 != null) {
            results.addAll(Arrays.asList(values2));
        }
        return results.toArray(new String[0]);
    }

    private void mergeParameters() {
        if (this.queryParamString == null || this.queryParamString.length() < 1) {
            return;
        }
        Parameters paramParser = new Parameters();
        MessageBytes queryMB = MessageBytes.newInstance();
        queryMB.setString(this.queryParamString);
        String encoding = this.getCharacterEncoding();
        Charset charset = null;
        if (encoding != null) {
            try {
                charset = B2CConverter.getCharset((String)encoding);
                queryMB.setCharset(charset);
            }
            catch (UnsupportedEncodingException e) {
                charset = StandardCharsets.ISO_8859_1;
            }
        }
        paramParser.setQuery(queryMB);
        paramParser.setQueryStringCharset(charset);
        paramParser.handleQueryParameters();
        Enumeration dispParamNames = paramParser.getParameterNames();
        while (dispParamNames.hasMoreElements()) {
            String dispParamName = (String)dispParamNames.nextElement();
            String[] dispParamValues = paramParser.getParameterValues(dispParamName);
            String[] originalValues = this.parameters.get(dispParamName);
            if (originalValues == null) {
                this.parameters.put(dispParamName, dispParamValues);
                continue;
            }
            this.parameters.put(dispParamName, this.mergeValues(dispParamValues, originalValues));
        }
    }

    static {
        for (int i = 0; i < specials.length; ++i) {
            specialsMap.put(specials[i], i);
        }
    }

    protected class AttributeNamesEnumerator
    implements Enumeration<String> {
        protected int pos = -1;
        protected final int last;
        protected final Enumeration<String> parentEnumeration;
        protected String next = null;

        public AttributeNamesEnumerator() {
            int last = -1;
            this.parentEnumeration = ApplicationHttpRequest.this.getRequest().getAttributeNames();
            for (int i = ApplicationHttpRequest.this.specialAttributes.length - 1; i >= 0; --i) {
                if (ApplicationHttpRequest.this.getAttribute(specials[i]) == null) continue;
                last = i;
                break;
            }
            this.last = last;
        }

        @Override
        public boolean hasMoreElements() {
            return this.pos != this.last || this.next != null || (this.next = this.findNext()) != null;
        }

        @Override
        public String nextElement() {
            if (this.pos != this.last) {
                for (int i = this.pos + 1; i <= this.last; ++i) {
                    if (ApplicationHttpRequest.this.getAttribute(specials[i]) == null) continue;
                    this.pos = i;
                    return specials[i];
                }
            }
            String result = this.next;
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.next = this.findNext();
            return result;
        }

        protected String findNext() {
            String result = null;
            while (result == null && this.parentEnumeration.hasMoreElements()) {
                String current = this.parentEnumeration.nextElement();
                if (ApplicationHttpRequest.this.isSpecial(current)) continue;
                result = current;
            }
            return result;
        }
    }
}

