/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.http.ResponseUtil
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.http.ResponseUtil;
import org.apache.tomcat.util.res.StringManager;

public class CorsFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    private static final StringManager sm = StringManager.getManager(CorsFilter.class);
    private transient Log log = LogFactory.getLog(CorsFilter.class);
    private final Collection<String> allowedOrigins = new HashSet<String>();
    private boolean anyOriginAllowed;
    private final Collection<String> allowedHttpMethods = new HashSet<String>();
    private final Collection<String> allowedHttpHeaders = new HashSet<String>();
    private final Collection<String> exposedHeaders = new HashSet<String>();
    private boolean supportsCredentials;
    private long preflightMaxAge;
    private boolean decorateRequest;
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    @Deprecated
    public static final String REQUEST_HEADER_VARY = "Vary";
    public static final String REQUEST_HEADER_ORIGIN = "Origin";
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String HTTP_REQUEST_ATTRIBUTE_PREFIX = "cors.";
    public static final String HTTP_REQUEST_ATTRIBUTE_ORIGIN = "cors.request.origin";
    public static final String HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST = "cors.isCorsRequest";
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE = "cors.request.type";
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS = "cors.request.headers";
    public static final Collection<String> SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("application/x-www-form-urlencoded", "multipart/form-data", "text/plain")));
    public static final String DEFAULT_ALLOWED_ORIGINS = "";
    public static final String DEFAULT_ALLOWED_HTTP_METHODS = "GET,POST,HEAD,OPTIONS";
    public static final String DEFAULT_PREFLIGHT_MAXAGE = "1800";
    public static final String DEFAULT_SUPPORTS_CREDENTIALS = "false";
    public static final String DEFAULT_ALLOWED_HTTP_HEADERS = "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers";
    public static final String DEFAULT_EXPOSED_HEADERS = "";
    public static final String DEFAULT_DECORATE_REQUEST = "true";
    public static final String PARAM_CORS_ALLOWED_ORIGINS = "cors.allowed.origins";
    public static final String PARAM_CORS_SUPPORT_CREDENTIALS = "cors.support.credentials";
    public static final String PARAM_CORS_EXPOSED_HEADERS = "cors.exposed.headers";
    public static final String PARAM_CORS_ALLOWED_HEADERS = "cors.allowed.headers";
    public static final String PARAM_CORS_ALLOWED_METHODS = "cors.allowed.methods";
    public static final String PARAM_CORS_PREFLIGHT_MAXAGE = "cors.preflight.maxage";
    public static final String PARAM_CORS_REQUEST_DECORATE = "cors.request.decorate";

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException(sm.getString("corsFilter.onlyHttp"));
        }
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        CORSRequestType requestType = this.checkRequestType(request);
        if (this.isDecorateRequest()) {
            CorsFilter.decorateCORSProperties(request, requestType);
        }
        switch (requestType) {
            case SIMPLE: 
            case ACTUAL: {
                this.handleSimpleCORS(request, response, filterChain);
                break;
            }
            case PRE_FLIGHT: {
                this.handlePreflightCORS(request, response, filterChain);
                break;
            }
            case NOT_CORS: {
                this.handleNonCORS(request, response, filterChain);
                break;
            }
            default: {
                this.handleInvalidCORS(request, response, filterChain);
            }
        }
    }

    public void init() throws ServletException {
        this.parseAndStore(this.getInitParameter(PARAM_CORS_ALLOWED_ORIGINS, ""), this.getInitParameter(PARAM_CORS_ALLOWED_METHODS, DEFAULT_ALLOWED_HTTP_METHODS), this.getInitParameter(PARAM_CORS_ALLOWED_HEADERS, DEFAULT_ALLOWED_HTTP_HEADERS), this.getInitParameter(PARAM_CORS_EXPOSED_HEADERS, ""), this.getInitParameter(PARAM_CORS_SUPPORT_CREDENTIALS, DEFAULT_SUPPORTS_CREDENTIALS), this.getInitParameter(PARAM_CORS_PREFLIGHT_MAXAGE, DEFAULT_PREFLIGHT_MAXAGE), this.getInitParameter(PARAM_CORS_REQUEST_DECORATE, DEFAULT_DECORATE_REQUEST));
    }

    private String getInitParameter(String name, String defaultValue) {
        String value = this.getInitParameter(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    protected void handleSimpleCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CORSRequestType requestType = this.checkRequestType(request);
        if (requestType != CORSRequestType.SIMPLE && requestType != CORSRequestType.ACTUAL) {
            throw new IllegalArgumentException(sm.getString("corsFilter.wrongType2", new Object[]{CORSRequestType.SIMPLE, CORSRequestType.ACTUAL}));
        }
        String origin = request.getHeader(REQUEST_HEADER_ORIGIN);
        String method = request.getMethod();
        if (!this.isOriginAllowed(origin)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        if (!this.getAllowedHttpMethods().contains(method)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        this.addStandardHeaders(request, response);
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    protected void handlePreflightCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CORSRequestType requestType = this.checkRequestType(request);
        if (requestType != CORSRequestType.PRE_FLIGHT) {
            throw new IllegalArgumentException(sm.getString("corsFilter.wrongType1", new Object[]{CORSRequestType.PRE_FLIGHT.name().toLowerCase(Locale.ENGLISH)}));
        }
        String origin = request.getHeader(REQUEST_HEADER_ORIGIN);
        if (!this.isOriginAllowed(origin)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        String accessControlRequestMethod = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
        if (accessControlRequestMethod == null) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        accessControlRequestMethod = accessControlRequestMethod.trim();
        String accessControlRequestHeadersHeader = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
        ArrayList<String> accessControlRequestHeaders = new ArrayList<String>();
        if (accessControlRequestHeadersHeader != null && !accessControlRequestHeadersHeader.trim().isEmpty()) {
            String[] headers;
            for (String header : headers = accessControlRequestHeadersHeader.trim().split(",")) {
                accessControlRequestHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
            }
        }
        if (!this.getAllowedHttpMethods().contains(accessControlRequestMethod)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        if (!accessControlRequestHeaders.isEmpty()) {
            for (String header : accessControlRequestHeaders) {
                if (this.getAllowedHttpHeaders().contains(header)) continue;
                this.handleInvalidCORS(request, response, filterChain);
                return;
            }
        }
        this.addStandardHeaders(request, response);
    }

    private void handleNonCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!this.isAnyOriginAllowed()) {
            ResponseUtil.addVaryFieldName((HttpServletResponse)response, (String)REQUEST_HEADER_ORIGIN);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private void handleInvalidCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String origin = request.getHeader(REQUEST_HEADER_ORIGIN);
        String method = request.getMethod();
        String accessControlRequestHeaders = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
        response.setContentType("text/plain");
        response.setStatus(403);
        response.resetBuffer();
        if (this.log.isDebugEnabled()) {
            StringBuilder message = new StringBuilder("Invalid CORS request; Origin=");
            message.append(origin);
            message.append(";Method=");
            message.append(method);
            if (accessControlRequestHeaders != null) {
                message.append(";Access-Control-Request-Headers=");
                message.append(accessControlRequestHeaders);
            }
            this.log.debug((Object)message.toString());
        }
    }

    private void addStandardHeaders(HttpServletRequest request, HttpServletResponse response) {
        Collection<String> exposedHeaders;
        String method = request.getMethod();
        String origin = request.getHeader(REQUEST_HEADER_ORIGIN);
        boolean anyOriginAllowed = this.isAnyOriginAllowed();
        if (!anyOriginAllowed) {
            ResponseUtil.addVaryFieldName((HttpServletResponse)response, (String)REQUEST_HEADER_ORIGIN);
        }
        if (anyOriginAllowed) {
            response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        } else {
            response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        }
        if (this.isSupportsCredentials()) {
            response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, DEFAULT_DECORATE_REQUEST);
        }
        if ((exposedHeaders = this.getExposedHeaders()) != null && exposedHeaders.size() > 0) {
            String exposedHeadersString = CorsFilter.join(exposedHeaders, ",");
            response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, exposedHeadersString);
        }
        if ("OPTIONS".equals(method)) {
            Collection<String> allowedHttpHeaders;
            Collection<String> allowedHttpMethods;
            ResponseUtil.addVaryFieldName((HttpServletResponse)response, (String)REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
            ResponseUtil.addVaryFieldName((HttpServletResponse)response, (String)REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
            long preflightMaxAge = this.getPreflightMaxAge();
            if (preflightMaxAge > 0L) {
                response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE, String.valueOf(preflightMaxAge));
            }
            if ((allowedHttpMethods = this.getAllowedHttpMethods()) != null && !allowedHttpMethods.isEmpty()) {
                response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS, CorsFilter.join(allowedHttpMethods, ","));
            }
            if ((allowedHttpHeaders = this.getAllowedHttpHeaders()) != null && !allowedHttpHeaders.isEmpty()) {
                response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS, CorsFilter.join(allowedHttpHeaders, ","));
            }
        }
    }

    protected static void decorateCORSProperties(HttpServletRequest request, CORSRequestType corsRequestType) {
        if (request == null) {
            throw new IllegalArgumentException(sm.getString("corsFilter.nullRequest"));
        }
        if (corsRequestType == null) {
            throw new IllegalArgumentException(sm.getString("corsFilter.nullRequestType"));
        }
        switch (corsRequestType) {
            case SIMPLE: 
            case ACTUAL: {
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, (Object)Boolean.TRUE);
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_ORIGIN, (Object)request.getHeader(REQUEST_HEADER_ORIGIN));
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, (Object)corsRequestType.name().toLowerCase(Locale.ENGLISH));
                break;
            }
            case PRE_FLIGHT: {
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, (Object)Boolean.TRUE);
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_ORIGIN, (Object)request.getHeader(REQUEST_HEADER_ORIGIN));
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, (Object)corsRequestType.name().toLowerCase(Locale.ENGLISH));
                String headers = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
                if (headers == null) {
                    headers = "";
                }
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS, (Object)headers);
                break;
            }
            case NOT_CORS: {
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, (Object)Boolean.FALSE);
                break;
            }
        }
    }

    protected static String join(Collection<String> elements, String joinSeparator) {
        String separator = ",";
        if (elements == null) {
            return null;
        }
        if (joinSeparator != null) {
            separator = joinSeparator;
        }
        StringBuilder buffer = new StringBuilder();
        boolean isFirst = true;
        for (String element : elements) {
            if (!isFirst) {
                buffer.append(separator);
            } else {
                isFirst = false;
            }
            if (element == null) continue;
            buffer.append(element);
        }
        return buffer.toString();
    }

    protected CORSRequestType checkRequestType(HttpServletRequest request) {
        CORSRequestType requestType = CORSRequestType.INVALID_CORS;
        if (request == null) {
            throw new IllegalArgumentException(sm.getString("corsFilter.nullRequest"));
        }
        String originHeader = request.getHeader(REQUEST_HEADER_ORIGIN);
        if (originHeader != null) {
            if (originHeader.isEmpty()) {
                requestType = CORSRequestType.INVALID_CORS;
            } else if (!RequestUtil.isValidOrigin((String)originHeader)) {
                requestType = CORSRequestType.INVALID_CORS;
            } else {
                if (RequestUtil.isSameOrigin((HttpServletRequest)request, (String)originHeader)) {
                    return CORSRequestType.NOT_CORS;
                }
                String method = request.getMethod();
                if (method != null) {
                    if ("OPTIONS".equals(method)) {
                        String accessControlRequestMethodHeader = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
                        requestType = accessControlRequestMethodHeader != null && !accessControlRequestMethodHeader.isEmpty() ? CORSRequestType.PRE_FLIGHT : (accessControlRequestMethodHeader != null && accessControlRequestMethodHeader.isEmpty() ? CORSRequestType.INVALID_CORS : CORSRequestType.ACTUAL);
                    } else if ("GET".equals(method) || "HEAD".equals(method)) {
                        requestType = CORSRequestType.SIMPLE;
                    } else if ("POST".equals(method)) {
                        String mediaType = this.getMediaType(request.getContentType());
                        if (mediaType != null) {
                            requestType = SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES.contains(mediaType) ? CORSRequestType.SIMPLE : CORSRequestType.ACTUAL;
                        }
                    } else {
                        requestType = CORSRequestType.ACTUAL;
                    }
                }
            }
        } else {
            requestType = CORSRequestType.NOT_CORS;
        }
        return requestType;
    }

    private String getMediaType(String contentType) {
        if (contentType == null) {
            return null;
        }
        String result = contentType.toLowerCase(Locale.ENGLISH);
        int firstSemiColonIndex = result.indexOf(59);
        if (firstSemiColonIndex > -1) {
            result = result.substring(0, firstSemiColonIndex);
        }
        result = result.trim();
        return result;
    }

    private boolean isOriginAllowed(String origin) {
        if (this.isAnyOriginAllowed()) {
            return true;
        }
        return this.getAllowedOrigins().contains(origin);
    }

    private void parseAndStore(String allowedOrigins, String allowedHttpMethods, String allowedHttpHeaders, String exposedHeaders, String supportsCredentials, String preflightMaxAge, String decorateRequest) throws ServletException {
        if (allowedOrigins.trim().equals("*")) {
            this.anyOriginAllowed = true;
        } else {
            this.anyOriginAllowed = false;
            Set<String> setAllowedOrigins = this.parseStringToSet(allowedOrigins);
            this.allowedOrigins.clear();
            this.allowedOrigins.addAll(setAllowedOrigins);
        }
        Set<String> setAllowedHttpMethods = this.parseStringToSet(allowedHttpMethods);
        this.allowedHttpMethods.clear();
        this.allowedHttpMethods.addAll(setAllowedHttpMethods);
        Set<String> setAllowedHttpHeaders = this.parseStringToSet(allowedHttpHeaders);
        HashSet<String> lowerCaseHeaders = new HashSet<String>();
        for (String header : setAllowedHttpHeaders) {
            String lowerCase = header.toLowerCase(Locale.ENGLISH);
            lowerCaseHeaders.add(lowerCase);
        }
        this.allowedHttpHeaders.clear();
        this.allowedHttpHeaders.addAll(lowerCaseHeaders);
        Set<String> setExposedHeaders = this.parseStringToSet(exposedHeaders);
        this.exposedHeaders.clear();
        this.exposedHeaders.addAll(setExposedHeaders);
        this.supportsCredentials = Boolean.parseBoolean(supportsCredentials);
        if (this.supportsCredentials && this.anyOriginAllowed) {
            throw new ServletException(sm.getString("corsFilter.invalidSupportsCredentials"));
        }
        try {
            this.preflightMaxAge = !preflightMaxAge.isEmpty() ? Long.parseLong(preflightMaxAge) : 0L;
        }
        catch (NumberFormatException e) {
            throw new ServletException(sm.getString("corsFilter.invalidPreflightMaxAge"), (Throwable)e);
        }
        this.decorateRequest = Boolean.parseBoolean(decorateRequest);
    }

    private Set<String> parseStringToSet(String data) {
        String[] splits = data != null && data.length() > 0 ? data.split(",") : new String[]{};
        HashSet<String> set = new HashSet<String>();
        if (splits.length > 0) {
            for (String split : splits) {
                set.add(split.trim());
            }
        }
        return set;
    }

    @Deprecated
    protected static boolean isValidOrigin(String origin) {
        return RequestUtil.isValidOrigin((String)origin);
    }

    public boolean isAnyOriginAllowed() {
        return this.anyOriginAllowed;
    }

    public Collection<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public boolean isSupportsCredentials() {
        return this.supportsCredentials;
    }

    public long getPreflightMaxAge() {
        return this.preflightMaxAge;
    }

    public Collection<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public Collection<String> getAllowedHttpMethods() {
        return this.allowedHttpMethods;
    }

    public Collection<String> getAllowedHttpHeaders() {
        return this.allowedHttpHeaders;
    }

    public boolean isDecorateRequest() {
        return this.decorateRequest;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog(CorsFilter.class);
    }

    protected static enum CORSRequestType {
        SIMPLE,
        ACTUAL,
        PRE_FLIGHT,
        NOT_CORS,
        INVALID_CORS;

    }
}

