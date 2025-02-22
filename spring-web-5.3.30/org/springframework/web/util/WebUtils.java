/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.ServletResponse
 *  javax.servlet.ServletResponseWrapper
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class WebUtils {
    public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
    public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";
    public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
    public static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";
    public static final String INCLUDE_QUERY_STRING_ATTRIBUTE = "javax.servlet.include.query_string";
    public static final String FORWARD_REQUEST_URI_ATTRIBUTE = "javax.servlet.forward.request_uri";
    public static final String FORWARD_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.forward.context_path";
    public static final String FORWARD_SERVLET_PATH_ATTRIBUTE = "javax.servlet.forward.servlet_path";
    public static final String FORWARD_PATH_INFO_ATTRIBUTE = "javax.servlet.forward.path_info";
    public static final String FORWARD_QUERY_STRING_ATTRIBUTE = "javax.servlet.forward.query_string";
    public static final String ERROR_STATUS_CODE_ATTRIBUTE = "javax.servlet.error.status_code";
    public static final String ERROR_EXCEPTION_TYPE_ATTRIBUTE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE_ATTRIBUTE = "javax.servlet.error.message";
    public static final String ERROR_EXCEPTION_ATTRIBUTE = "javax.servlet.error.exception";
    public static final String ERROR_REQUEST_URI_ATTRIBUTE = "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME_ATTRIBUTE = "javax.servlet.error.servlet_name";
    public static final String CONTENT_TYPE_CHARSET_PREFIX = ";charset=";
    public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";
    public static final String TEMP_DIR_CONTEXT_ATTRIBUTE = "javax.servlet.context.tempdir";
    public static final String HTML_ESCAPE_CONTEXT_PARAM = "defaultHtmlEscape";
    public static final String RESPONSE_ENCODED_HTML_ESCAPE_CONTEXT_PARAM = "responseEncodedHtmlEscape";
    public static final String WEB_APP_ROOT_KEY_PARAM = "webAppRootKey";
    public static final String DEFAULT_WEB_APP_ROOT_KEY = "webapp.root";
    public static final String[] SUBMIT_IMAGE_SUFFIXES = new String[]{".x", ".y"};
    public static final String SESSION_MUTEX_ATTRIBUTE = WebUtils.class.getName() + ".MUTEX";

    public static void setWebAppRootSystemProperty(ServletContext servletContext) throws IllegalStateException {
        Assert.notNull((Object)servletContext, (String)"ServletContext must not be null");
        String root = servletContext.getRealPath("/");
        if (root == null) {
            throw new IllegalStateException("Cannot set web app root system property when WAR file is not expanded");
        }
        String param = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
        String key = param != null ? param : DEFAULT_WEB_APP_ROOT_KEY;
        String oldValue = System.getProperty(key);
        if (oldValue != null && !StringUtils.pathEquals((String)oldValue, (String)root)) {
            throw new IllegalStateException("Web app root system property already set to different value: '" + key + "' = [" + oldValue + "] instead of [" + root + "] - Choose unique values for the 'webAppRootKey' context-param in your web.xml files!");
        }
        System.setProperty(key, root);
        servletContext.log("Set web app root system property: '" + key + "' = [" + root + "]");
    }

    public static void removeWebAppRootSystemProperty(ServletContext servletContext) {
        Assert.notNull((Object)servletContext, (String)"ServletContext must not be null");
        String param = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
        String key = param != null ? param : DEFAULT_WEB_APP_ROOT_KEY;
        System.getProperties().remove(key);
    }

    @Nullable
    public static Boolean getDefaultHtmlEscape(@Nullable ServletContext servletContext) {
        if (servletContext == null) {
            return null;
        }
        String param = servletContext.getInitParameter(HTML_ESCAPE_CONTEXT_PARAM);
        return StringUtils.hasText((String)param) ? Boolean.valueOf(param) : null;
    }

    @Nullable
    public static Boolean getResponseEncodedHtmlEscape(@Nullable ServletContext servletContext) {
        if (servletContext == null) {
            return null;
        }
        String param = servletContext.getInitParameter(RESPONSE_ENCODED_HTML_ESCAPE_CONTEXT_PARAM);
        return StringUtils.hasText((String)param) ? Boolean.valueOf(param) : null;
    }

    public static File getTempDir(ServletContext servletContext) {
        Assert.notNull((Object)servletContext, (String)"ServletContext must not be null");
        return (File)servletContext.getAttribute(TEMP_DIR_CONTEXT_ATTRIBUTE);
    }

    public static String getRealPath(ServletContext servletContext, String path) throws FileNotFoundException {
        String realPath;
        Assert.notNull((Object)servletContext, (String)"ServletContext must not be null");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if ((realPath = servletContext.getRealPath(path)) == null) {
            throw new FileNotFoundException("ServletContext resource [" + path + "] cannot be resolved to absolute file path - web application archive not expanded?");
        }
        return realPath;
    }

    @Nullable
    public static String getSessionId(HttpServletRequest request) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        HttpSession session = request.getSession(false);
        return session != null ? session.getId() : null;
    }

    @Nullable
    public static Object getSessionAttribute(HttpServletRequest request, String name) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(name) : null;
    }

    public static Object getRequiredSessionAttribute(HttpServletRequest request, String name) throws IllegalStateException {
        Object attr = WebUtils.getSessionAttribute(request, name);
        if (attr == null) {
            throw new IllegalStateException("No session attribute '" + name + "' found");
        }
        return attr;
    }

    public static void setSessionAttribute(HttpServletRequest request, String name, @Nullable Object value) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        if (value != null) {
            request.getSession().setAttribute(name, value);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(name);
            }
        }
    }

    public static Object getSessionMutex(HttpSession session) {
        Assert.notNull((Object)session, (String)"Session must not be null");
        Object mutex = session.getAttribute(SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = session;
        }
        return mutex;
    }

    @Nullable
    public static <T> T getNativeRequest(ServletRequest request, @Nullable Class<T> requiredType) {
        if (requiredType != null) {
            if (requiredType.isInstance(request)) {
                return (T)request;
            }
            if (request instanceof ServletRequestWrapper) {
                return WebUtils.getNativeRequest(((ServletRequestWrapper)request).getRequest(), requiredType);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T getNativeResponse(ServletResponse response, @Nullable Class<T> requiredType) {
        if (requiredType != null) {
            if (requiredType.isInstance(response)) {
                return (T)response;
            }
            if (response instanceof ServletResponseWrapper) {
                return WebUtils.getNativeResponse(((ServletResponseWrapper)response).getResponse(), requiredType);
            }
        }
        return null;
    }

    public static boolean isIncludeRequest(ServletRequest request) {
        return request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }

    public static void exposeErrorRequestAttributes(HttpServletRequest request, Throwable ex, @Nullable String servletName) {
        WebUtils.exposeRequestAttributeIfNotPresent((ServletRequest)request, ERROR_STATUS_CODE_ATTRIBUTE, 200);
        WebUtils.exposeRequestAttributeIfNotPresent((ServletRequest)request, ERROR_EXCEPTION_TYPE_ATTRIBUTE, ex.getClass());
        WebUtils.exposeRequestAttributeIfNotPresent((ServletRequest)request, ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        WebUtils.exposeRequestAttributeIfNotPresent((ServletRequest)request, ERROR_EXCEPTION_ATTRIBUTE, ex);
        WebUtils.exposeRequestAttributeIfNotPresent((ServletRequest)request, ERROR_REQUEST_URI_ATTRIBUTE, request.getRequestURI());
        if (servletName != null) {
            WebUtils.exposeRequestAttributeIfNotPresent((ServletRequest)request, ERROR_SERVLET_NAME_ATTRIBUTE, servletName);
        }
    }

    private static void exposeRequestAttributeIfNotPresent(ServletRequest request, String name, Object value) {
        if (request.getAttribute(name) == null) {
            request.setAttribute(name, value);
        }
    }

    public static void clearErrorRequestAttributes(HttpServletRequest request) {
        request.removeAttribute(ERROR_STATUS_CODE_ATTRIBUTE);
        request.removeAttribute(ERROR_EXCEPTION_TYPE_ATTRIBUTE);
        request.removeAttribute(ERROR_MESSAGE_ATTRIBUTE);
        request.removeAttribute(ERROR_EXCEPTION_ATTRIBUTE);
        request.removeAttribute(ERROR_REQUEST_URI_ATTRIBUTE);
        request.removeAttribute(ERROR_SERVLET_NAME_ATTRIBUTE);
    }

    @Nullable
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!name.equals(cookie.getName())) continue;
                return cookie;
            }
        }
        return null;
    }

    public static boolean hasSubmitParameter(ServletRequest request, String name) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        if (request.getParameter(name) != null) {
            return true;
        }
        for (String suffix : SUBMIT_IMAGE_SUFFIXES) {
            if (request.getParameter(name + suffix) == null) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public static String findParameterValue(ServletRequest request, String name) {
        return WebUtils.findParameterValue(request.getParameterMap(), name);
    }

    @Nullable
    public static String findParameterValue(Map<String, ?> parameters, String name) {
        Object value = parameters.get(name);
        if (value instanceof String[]) {
            String[] values = (String[])value;
            return values.length > 0 ? values[0] : null;
        }
        if (value != null) {
            return value.toString();
        }
        String prefix = name + "_";
        for (String paramName : parameters.keySet()) {
            if (!paramName.startsWith(prefix)) continue;
            for (String suffix : SUBMIT_IMAGE_SUFFIXES) {
                if (!paramName.endsWith(suffix)) continue;
                return paramName.substring(prefix.length(), paramName.length() - suffix.length());
            }
            return paramName.substring(prefix.length());
        }
        return null;
    }

    public static Map<String, Object> getParametersStartingWith(ServletRequest request, @Nullable String prefix) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            if (!prefix.isEmpty() && !paramName.startsWith(prefix)) continue;
            String unprefixed = paramName.substring(prefix.length());
            String[] values = request.getParameterValues(paramName);
            if (values == null || values.length == 0) continue;
            if (values.length > 1) {
                params.put(unprefixed, values);
                continue;
            }
            params.put(unprefixed, values[0]);
        }
        return params;
    }

    public static MultiValueMap<String, String> parseMatrixVariables(String matrixVariables) {
        LinkedMultiValueMap result = new LinkedMultiValueMap();
        if (!StringUtils.hasText((String)matrixVariables)) {
            return result;
        }
        StringTokenizer pairs = new StringTokenizer(matrixVariables, ";");
        while (pairs.hasMoreTokens()) {
            String pair = pairs.nextToken();
            int index = pair.indexOf(61);
            if (index != -1) {
                String name = pair.substring(0, index);
                if (name.equalsIgnoreCase("jsessionid")) continue;
                String rawValue = pair.substring(index + 1);
                for (String value : StringUtils.commaDelimitedListToStringArray((String)rawValue)) {
                    result.add((Object)name, (Object)value);
                }
                continue;
            }
            result.add((Object)pair, (Object)"");
        }
        return result;
    }

    public static boolean isValidOrigin(HttpRequest request, Collection<String> allowedOrigins) {
        Assert.notNull((Object)request, (String)"Request must not be null");
        Assert.notNull(allowedOrigins, (String)"Allowed origins must not be null");
        String origin = request.getHeaders().getOrigin();
        if (origin == null || allowedOrigins.contains("*")) {
            return true;
        }
        if (CollectionUtils.isEmpty(allowedOrigins)) {
            return WebUtils.isSameOrigin(request);
        }
        return allowedOrigins.contains(origin);
    }

    public static boolean isSameOrigin(HttpRequest request) {
        int port;
        String host;
        String scheme;
        HttpHeaders headers = request.getHeaders();
        String origin = headers.getOrigin();
        if (origin == null) {
            return true;
        }
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
            scheme = servletRequest.getScheme();
            host = servletRequest.getServerName();
            port = servletRequest.getServerPort();
        } else {
            URI uri = request.getURI();
            scheme = uri.getScheme();
            host = uri.getHost();
            port = uri.getPort();
        }
        UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
        return ObjectUtils.nullSafeEquals((Object)scheme, (Object)originUrl.getScheme()) && ObjectUtils.nullSafeEquals((Object)host, (Object)originUrl.getHost()) && WebUtils.getPort(scheme, port) == WebUtils.getPort(originUrl.getScheme(), originUrl.getPort());
    }

    private static int getPort(@Nullable String scheme, int port) {
        if (port == -1) {
            if ("http".equals(scheme) || "ws".equals(scheme)) {
                port = 80;
            } else if ("https".equals(scheme) || "wss".equals(scheme)) {
                port = 443;
            }
        }
        return port;
    }
}

