/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  javax.servlet.http.HttpSession
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import org.apache.catalina.filters.CsrfPreventionFilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CsrfPreventionFilter
extends CsrfPreventionFilterBase {
    private final Log log = LogFactory.getLog(CsrfPreventionFilter.class);
    private final Set<String> entryPoints = new HashSet<String>();
    private int nonceCacheSize = 5;
    private String nonceRequestParameterName = "org.apache.catalina.filters.CSRF_NONCE";

    public void setEntryPoints(String entryPoints) {
        String[] values;
        for (String value : values = entryPoints.split(",")) {
            this.entryPoints.add(value.trim());
        }
    }

    public void setNonceCacheSize(int nonceCacheSize) {
        this.nonceCacheSize = nonceCacheSize;
    }

    public void setNonceRequestParameterName(String parameterName) {
        this.nonceRequestParameterName = parameterName;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute("org.apache.catalina.filters.CSRF_NONCE_PARAM_NAME", (Object)this.nonceRequestParameterName);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CsrfResponseWrapper wResponse = null;
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest)request;
            HttpServletResponse res = (HttpServletResponse)response;
            HttpSession session = req.getSession(false);
            boolean skipNonceCheck = this.skipNonceCheck(req);
            NonceCache<String> nonceCache = null;
            if (!skipNonceCheck) {
                String previousNonce = req.getParameter(this.nonceRequestParameterName);
                if (previousNonce == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Rejecting request for " + this.getRequestedPath(req) + ", session " + (null == session ? "(none)" : session.getId()) + " with no CSRF nonce found in request"));
                    }
                    res.sendError(this.getDenyStatus());
                    return;
                }
                nonceCache = this.getNonceCache(req, session);
                if (nonceCache == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Rejecting request for " + this.getRequestedPath(req) + ", session " + (null == session ? "(none)" : session.getId()) + " due to empty / missing nonce cache"));
                    }
                    res.sendError(this.getDenyStatus());
                    return;
                }
                if (!nonceCache.contains(previousNonce)) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Rejecting request for " + this.getRequestedPath(req) + ", session " + (null == session ? "(none)" : session.getId()) + " due to invalid nonce " + previousNonce));
                    }
                    res.sendError(this.getDenyStatus());
                    return;
                }
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Allowing request to " + this.getRequestedPath(req) + " with valid CSRF nonce " + previousNonce));
                }
            }
            if (!this.skipNonceGeneration(req)) {
                if (skipNonceCheck) {
                    nonceCache = this.getNonceCache(req, session);
                }
                if (nonceCache == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Creating new CSRF nonce cache with size=" + this.nonceCacheSize + " for session " + (null == session ? "(will create)" : session.getId())));
                    }
                    if (session == null) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)"Creating new session to store CSRF nonce cache");
                        }
                        session = req.getSession(true);
                    }
                    nonceCache = this.createNonceCache(req, session);
                }
                String newNonce = this.generateNonce(req);
                nonceCache.add(newNonce);
                request.setAttribute("org.apache.catalina.filters.CSRF_REQUEST_NONCE", (Object)newNonce);
                wResponse = new CsrfResponseWrapper(res, this.nonceRequestParameterName, newNonce);
            }
        }
        chain.doFilter(request, (ServletResponse)(wResponse == null ? response : wResponse));
    }

    protected boolean skipNonceCheck(HttpServletRequest request) {
        if (!"GET".equals(request.getMethod())) {
            return false;
        }
        String requestedPath = this.getRequestedPath(request);
        if (!this.entryPoints.contains(requestedPath)) {
            return false;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Skipping CSRF nonce-check for GET request to entry point " + requestedPath));
        }
        return true;
    }

    protected boolean skipNonceGeneration(HttpServletRequest request) {
        return false;
    }

    protected NonceCache<String> createNonceCache(HttpServletRequest request, HttpSession session) {
        LruCache<String> nonceCache = new LruCache<String>(this.nonceCacheSize);
        session.setAttribute("org.apache.catalina.filters.CSRF_NONCE", nonceCache);
        return nonceCache;
    }

    protected NonceCache<String> getNonceCache(HttpServletRequest request, HttpSession session) {
        if (session == null) {
            return null;
        }
        NonceCache nonceCache = (NonceCache)session.getAttribute("org.apache.catalina.filters.CSRF_NONCE");
        return nonceCache;
    }

    protected static interface NonceCache<T>
    extends Serializable {
        public void add(T var1);

        public boolean contains(T var1);
    }

    protected static class CsrfResponseWrapper
    extends HttpServletResponseWrapper {
        private final String nonceRequestParameterName;
        private final String nonce;

        public CsrfResponseWrapper(HttpServletResponse response, String nonceRequestParameterName, String nonce) {
            super(response);
            this.nonceRequestParameterName = nonceRequestParameterName;
            this.nonce = nonce;
        }

        @Deprecated
        public String encodeRedirectUrl(String url) {
            return this.encodeRedirectURL(url);
        }

        public String encodeRedirectURL(String url) {
            return this.addNonce(super.encodeRedirectURL(url));
        }

        @Deprecated
        public String encodeUrl(String url) {
            return this.encodeURL(url);
        }

        public String encodeURL(String url) {
            return this.addNonce(super.encodeURL(url));
        }

        private String addNonce(String url) {
            int question;
            if (url == null || this.nonce == null) {
                return url;
            }
            String path = url;
            String query = "";
            String anchor = "";
            int pound = path.indexOf(35);
            if (pound >= 0) {
                anchor = path.substring(pound);
                path = path.substring(0, pound);
            }
            if ((question = path.indexOf(63)) >= 0) {
                query = path.substring(question);
                path = path.substring(0, question);
            }
            StringBuilder sb = new StringBuilder(path);
            if (query.length() > 0) {
                sb.append(query);
                sb.append('&');
            } else {
                sb.append('?');
            }
            sb.append(this.nonceRequestParameterName);
            sb.append('=');
            sb.append(this.nonce);
            sb.append(anchor);
            return sb.toString();
        }
    }

    protected static class LruCache<T>
    implements NonceCache<T> {
        private static final long serialVersionUID = 1L;
        private final Map<T, T> cache;

        public LruCache(final int cacheSize) {
            this.cache = new LinkedHashMap<T, T>(){
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<T, T> eldest) {
                    return this.size() > cacheSize;
                }
            };
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(T key) {
            Map<T, T> map = this.cache;
            synchronized (map) {
                this.cache.put(key, null);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(T key) {
            Map<T, T> map = this.cache;
            synchronized (map) {
                return this.cache.containsKey(key);
            }
        }
    }
}

