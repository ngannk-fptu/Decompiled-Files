/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.MappingMatch
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.util;

import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.MappingMatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

public class UrlPathHelper {
    public static final String PATH_ATTRIBUTE = UrlPathHelper.class.getName() + ".PATH";
    static final boolean servlet4Present = ClassUtils.hasMethod(HttpServletRequest.class, "getHttpServletMapping", new Class[0]);
    private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";
    private static final Log logger = LogFactory.getLog(UrlPathHelper.class);
    @Nullable
    static volatile Boolean websphereComplianceFlag;
    private boolean alwaysUseFullPath = false;
    private boolean urlDecode = true;
    private boolean removeSemicolonContent = true;
    private String defaultEncoding = "ISO-8859-1";
    private boolean readOnly = false;
    public static final UrlPathHelper defaultInstance;
    public static final UrlPathHelper rawPathInstance;

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.checkReadOnly();
        this.alwaysUseFullPath = alwaysUseFullPath;
    }

    public void setUrlDecode(boolean urlDecode) {
        this.checkReadOnly();
        this.urlDecode = urlDecode;
    }

    public boolean isUrlDecode() {
        return this.urlDecode;
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.checkReadOnly();
        this.removeSemicolonContent = removeSemicolonContent;
    }

    public boolean shouldRemoveSemicolonContent() {
        return this.removeSemicolonContent;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.checkReadOnly();
        this.defaultEncoding = defaultEncoding;
    }

    protected String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    private void setReadOnly() {
        this.readOnly = true;
    }

    private void checkReadOnly() {
        Assert.isTrue(!this.readOnly, "This instance cannot be modified");
    }

    public String resolveAndCacheLookupPath(HttpServletRequest request) {
        String lookupPath = this.getLookupPathForRequest(request);
        request.setAttribute(PATH_ATTRIBUTE, (Object)lookupPath);
        return lookupPath;
    }

    public static String getResolvedLookupPath(ServletRequest request) {
        String lookupPath = (String)request.getAttribute(PATH_ATTRIBUTE);
        Assert.notNull((Object)lookupPath, () -> "Expected lookupPath in request attribute \"" + PATH_ATTRIBUTE + "\".");
        return lookupPath;
    }

    @Deprecated
    public String getLookupPathForRequest(HttpServletRequest request, @Nullable String name) {
        String result = null;
        if (name != null) {
            result = (String)request.getAttribute(name);
        }
        return result != null ? result : this.getLookupPathForRequest(request);
    }

    public String getLookupPathForRequest(HttpServletRequest request) {
        String pathWithinApp = this.getPathWithinApplication(request);
        if (this.alwaysUseFullPath || this.skipServletPathDetermination(request)) {
            return pathWithinApp;
        }
        String rest = this.getPathWithinServletMapping(request, pathWithinApp);
        if (StringUtils.hasLength(rest)) {
            return rest;
        }
        return pathWithinApp;
    }

    private boolean skipServletPathDetermination(HttpServletRequest request) {
        if (servlet4Present) {
            return Servlet4Delegate.skipServletPathDetermination(request);
        }
        return false;
    }

    public String getPathWithinServletMapping(HttpServletRequest request) {
        return this.getPathWithinServletMapping(request, this.getPathWithinApplication(request));
    }

    protected String getPathWithinServletMapping(HttpServletRequest request, String pathWithinApp) {
        String sanitizedPathWithinApp;
        String servletPath = this.getServletPath(request);
        String path = servletPath.contains(sanitizedPathWithinApp = UrlPathHelper.getSanitizedPath(pathWithinApp)) ? this.getRemainingPath(sanitizedPathWithinApp, servletPath, false) : this.getRemainingPath(pathWithinApp, servletPath, false);
        if (path != null) {
            return path;
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            return pathInfo;
        }
        if (!this.urlDecode && (path = this.getRemainingPath(this.decodeInternal(request, pathWithinApp), servletPath, false)) != null) {
            return pathWithinApp;
        }
        return servletPath;
    }

    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = this.getContextPath(request);
        String requestUri = this.getRequestUri(request);
        String path = this.getRemainingPath(requestUri, contextPath, true);
        if (path != null) {
            return StringUtils.hasText(path) ? path : "/";
        }
        return requestUri;
    }

    @Nullable
    private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
        int index2;
        int index1 = 0;
        for (index2 = 0; index1 < requestUri.length() && index2 < mapping.length(); ++index1, ++index2) {
            char c1 = requestUri.charAt(index1);
            char c2 = mapping.charAt(index2);
            if (c1 == ';') {
                if ((index1 = requestUri.indexOf(47, index1)) == -1) {
                    return null;
                }
                c1 = requestUri.charAt(index1);
            }
            if (c1 == c2 || ignoreCase && Character.toLowerCase(c1) == Character.toLowerCase(c2)) continue;
            return null;
        }
        if (index2 != mapping.length()) {
            return null;
        }
        if (index1 == requestUri.length()) {
            return "";
        }
        if (requestUri.charAt(index1) == ';') {
            index1 = requestUri.indexOf(47, index1);
        }
        return index1 != -1 ? requestUri.substring(index1) : "";
    }

    private static String getSanitizedPath(String path) {
        int start = path.indexOf("//");
        if (start == -1) {
            return path;
        }
        char[] content = path.toCharArray();
        int slowIndex = start;
        for (int fastIndex = start + 1; fastIndex < content.length; ++fastIndex) {
            if (content[fastIndex] == '/' && content[slowIndex] == '/') continue;
            content[++slowIndex] = content[fastIndex];
        }
        return new String(content, 0, slowIndex + 1);
    }

    public String getRequestUri(HttpServletRequest request) {
        String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return this.decodeAndCleanUriString(request, uri);
    }

    public String getContextPath(HttpServletRequest request) {
        String contextPath = (String)request.getAttribute("javax.servlet.include.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        if (StringUtils.matchesCharacter(contextPath, '/')) {
            contextPath = "";
        }
        return this.decodeRequestString(request, contextPath);
    }

    public String getServletPath(HttpServletRequest request) {
        String servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        if (servletPath.length() > 1 && servletPath.endsWith("/") && this.shouldRemoveTrailingServletPathSlash(request)) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
        return servletPath;
    }

    public String getOriginatingRequestUri(HttpServletRequest request) {
        String uri = (String)request.getAttribute(WEBSPHERE_URI_ATTRIBUTE);
        if (uri == null && (uri = (String)request.getAttribute("javax.servlet.forward.request_uri")) == null) {
            uri = request.getRequestURI();
        }
        return this.decodeAndCleanUriString(request, uri);
    }

    public String getOriginatingContextPath(HttpServletRequest request) {
        String contextPath = (String)request.getAttribute("javax.servlet.forward.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        return this.decodeRequestString(request, contextPath);
    }

    public String getOriginatingServletPath(HttpServletRequest request) {
        String servletPath = (String)request.getAttribute("javax.servlet.forward.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        return servletPath;
    }

    public String getOriginatingQueryString(HttpServletRequest request) {
        if (request.getAttribute("javax.servlet.forward.request_uri") != null || request.getAttribute("javax.servlet.error.request_uri") != null) {
            return (String)request.getAttribute("javax.servlet.forward.query_string");
        }
        return request.getQueryString();
    }

    private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        uri = this.removeSemicolonContent(uri);
        uri = this.decodeRequestString(request, uri);
        uri = UrlPathHelper.getSanitizedPath(uri);
        return uri;
    }

    public String decodeRequestString(HttpServletRequest request, String source) {
        if (this.urlDecode) {
            return this.decodeInternal(request, source);
        }
        return source;
    }

    private String decodeInternal(HttpServletRequest request, String source) {
        String enc = this.determineEncoding(request);
        try {
            return UriUtils.decode(source, enc);
        }
        catch (UnsupportedCharsetException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Could not decode request string [" + source + "] with encoding '" + enc + "': falling back to platform default encoding; exception message: " + ex.getMessage()));
            }
            return URLDecoder.decode(source);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = this.getDefaultEncoding();
        }
        return enc;
    }

    public String removeSemicolonContent(String requestUri) {
        return this.removeSemicolonContent ? UrlPathHelper.removeSemicolonContentInternal(requestUri) : this.removeJsessionid(requestUri);
    }

    private static String removeSemicolonContentInternal(String requestUri) {
        int semicolonIndex = requestUri.indexOf(59);
        if (semicolonIndex == -1) {
            return requestUri;
        }
        StringBuilder sb = new StringBuilder(requestUri);
        while (semicolonIndex != -1) {
            int slashIndex = sb.indexOf("/", semicolonIndex + 1);
            if (slashIndex == -1) {
                return sb.substring(0, semicolonIndex);
            }
            sb.delete(semicolonIndex, slashIndex);
            semicolonIndex = sb.indexOf(";", semicolonIndex);
        }
        return sb.toString();
    }

    private String removeJsessionid(String requestUri) {
        String key = ";jsessionid=";
        int index = requestUri.toLowerCase().indexOf(key);
        if (index == -1) {
            return requestUri;
        }
        String start = requestUri.substring(0, index);
        for (int i2 = index + key.length(); i2 < requestUri.length(); ++i2) {
            char c = requestUri.charAt(i2);
            if (c != ';' && c != '/') continue;
            return start + requestUri.substring(i2);
        }
        return start;
    }

    public Map<String, String> decodePathVariables(HttpServletRequest request, Map<String, String> vars) {
        if (this.urlDecode) {
            return vars;
        }
        LinkedHashMap<String, String> decodedVars = CollectionUtils.newLinkedHashMap(vars.size());
        vars.forEach((key, value) -> decodedVars.put((String)key, this.decodeInternal(request, (String)value)));
        return decodedVars;
    }

    public MultiValueMap<String, String> decodeMatrixVariables(HttpServletRequest request, MultiValueMap<String, String> vars) {
        if (this.urlDecode) {
            return vars;
        }
        LinkedMultiValueMap<String, String> decodedVars = new LinkedMultiValueMap<String, String>(vars.size());
        vars.forEach((key, values) -> {
            for (String value : values) {
                decodedVars.add((String)key, this.decodeInternal(request, value));
            }
        });
        return decodedVars;
    }

    private boolean shouldRemoveTrailingServletPathSlash(HttpServletRequest request) {
        if (request.getAttribute(WEBSPHERE_URI_ATTRIBUTE) == null) {
            return false;
        }
        Boolean flagToUse = websphereComplianceFlag;
        if (flagToUse == null) {
            boolean flag;
            block4: {
                ClassLoader classLoader = UrlPathHelper.class.getClassLoader();
                String className = "com.ibm.ws.webcontainer.WebContainer";
                String methodName = "getWebContainerProperties";
                String propName = "com.ibm.ws.webcontainer.removetrailingservletpathslash";
                flag = false;
                try {
                    Class<?> cl = classLoader.loadClass(className);
                    Properties prop = (Properties)cl.getMethod(methodName, new Class[0]).invoke(null, new Object[0]);
                    flag = Boolean.parseBoolean(prop.getProperty(propName));
                }
                catch (Throwable ex) {
                    if (!logger.isDebugEnabled()) break block4;
                    logger.debug((Object)("Could not introspect WebSphere web container properties: " + ex));
                }
            }
            flagToUse = flag;
            websphereComplianceFlag = flag;
        }
        return flagToUse == false;
    }

    static {
        defaultInstance = new UrlPathHelper();
        defaultInstance.setReadOnly();
        rawPathInstance = new UrlPathHelper(){

            @Override
            public String removeSemicolonContent(String requestUri) {
                return requestUri;
            }
        };
        rawPathInstance.setAlwaysUseFullPath(true);
        rawPathInstance.setUrlDecode(false);
        rawPathInstance.setRemoveSemicolonContent(false);
        rawPathInstance.setReadOnly();
    }

    private static class Servlet4Delegate {
        private Servlet4Delegate() {
        }

        public static boolean skipServletPathDetermination(HttpServletRequest request) {
            MappingMatch match;
            HttpServletMapping mapping = (HttpServletMapping)request.getAttribute("javax.servlet.include.mapping");
            if (mapping == null) {
                mapping = request.getHttpServletMapping();
            }
            return (match = mapping.getMappingMatch()) != null && (!match.equals((Object)MappingMatch.PATH) || mapping.getPattern().equals("/*"));
        }
    }
}

