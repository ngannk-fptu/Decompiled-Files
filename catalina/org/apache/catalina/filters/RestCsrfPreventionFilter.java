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
 *  javax.servlet.http.HttpSession
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.filters.CsrfPreventionFilterBase;
import org.apache.catalina.filters.FilterBase;

public class RestCsrfPreventionFilter
extends CsrfPreventionFilterBase {
    private static final Pattern NON_MODIFYING_METHODS_PATTERN = Pattern.compile("GET|HEAD|OPTIONS");
    private static final Predicate<String> nonModifyingMethods = m -> Objects.nonNull(m) && NON_MODIFYING_METHODS_PATTERN.matcher((CharSequence)m).matches();
    private Set<String> pathsAcceptingParams = new HashSet<String>();
    private String pathsDelimiter = ",";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute("org.apache.catalina.filters.CSRF_REST_NONCE_HEADER_NAME", (Object)"X-CSRF-Token");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            RestCsrfPreventionStrategy strategy;
            MethodType mType = MethodType.MODIFYING_METHOD;
            if (nonModifyingMethods.test(((HttpServletRequest)request).getMethod())) {
                mType = MethodType.NON_MODIFYING_METHOD;
            }
            switch (mType) {
                case NON_MODIFYING_METHOD: {
                    strategy = new FetchRequest();
                    break;
                }
                default: {
                    strategy = new StateChangingRequest();
                }
            }
            if (!strategy.apply((HttpServletRequest)request, (HttpServletResponse)response)) {
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public void setPathsAcceptingParams(String pathsList) {
        if (Objects.nonNull(pathsList)) {
            Arrays.asList(pathsList.split(this.pathsDelimiter)).forEach(e -> this.pathsAcceptingParams.add(e.trim()));
        }
    }

    public Set<String> getPathsAcceptingParams() {
        return this.pathsAcceptingParams;
    }

    private static enum MethodType {
        NON_MODIFYING_METHOD,
        MODIFYING_METHOD;

    }

    private class FetchRequest
    implements RestCsrfPreventionStrategy {
        private final Predicate<String> fetchRequest = "Fetch"::equalsIgnoreCase;

        private FetchRequest() {
        }

        @Override
        public boolean apply(HttpServletRequest request, HttpServletResponse response) {
            if (this.fetchRequest.test((String)nonceFromRequestHeader.getNonce(request, "X-CSRF-Token"))) {
                String nonceFromSessionStr = (String)nonceFromSession.getNonce(request.getSession(false), "org.apache.catalina.filters.CSRF_REST_NONCE");
                if (nonceFromSessionStr == null) {
                    nonceFromSessionStr = RestCsrfPreventionFilter.this.generateNonce(request);
                    nonceToSession.setNonce(Objects.requireNonNull(request.getSession(true)), "org.apache.catalina.filters.CSRF_REST_NONCE", nonceFromSessionStr);
                }
                nonceToResponse.setNonce(response, "X-CSRF-Token", nonceFromSessionStr);
                if (RestCsrfPreventionFilter.this.getLogger().isDebugEnabled()) {
                    RestCsrfPreventionFilter.this.getLogger().debug((Object)FilterBase.sm.getString("restCsrfPreventionFilter.fetch.debug", new Object[]{request.getMethod(), request.getRequestURI()}));
                }
            }
            return true;
        }
    }

    private class StateChangingRequest
    implements RestCsrfPreventionStrategy {
        private StateChangingRequest() {
        }

        @Override
        public boolean apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
            HttpSession session;
            String nonceSession;
            String nonceRequest = this.extractNonceFromRequest(request);
            if (this.isValidStateChangingRequest(nonceRequest, nonceSession = (String)nonceFromSession.getNonce(session = request.getSession(false), "org.apache.catalina.filters.CSRF_REST_NONCE"))) {
                return true;
            }
            nonceToResponse.setNonce(response, "X-CSRF-Token", "Required");
            response.sendError(RestCsrfPreventionFilter.this.getDenyStatus(), FilterBase.sm.getString("restCsrfPreventionFilter.invalidNonce"));
            if (RestCsrfPreventionFilter.this.getLogger().isDebugEnabled()) {
                RestCsrfPreventionFilter.this.getLogger().debug((Object)FilterBase.sm.getString("restCsrfPreventionFilter.invalidNonce.debug", new Object[]{request.getMethod(), request.getRequestURI(), request.getRequestedSessionId() != null, session, nonceRequest != null, nonceSession != null}));
            }
            return false;
        }

        private boolean isValidStateChangingRequest(String reqNonce, String sessionNonce) {
            return Objects.nonNull(reqNonce) && Objects.nonNull(sessionNonce) && Objects.equals(reqNonce, sessionNonce);
        }

        private String extractNonceFromRequest(HttpServletRequest request) {
            String nonceFromRequest = (String)nonceFromRequestHeader.getNonce(request, "X-CSRF-Token");
            if ((Objects.isNull(nonceFromRequest) || Objects.equals("", nonceFromRequest)) && !RestCsrfPreventionFilter.this.getPathsAcceptingParams().isEmpty() && RestCsrfPreventionFilter.this.getPathsAcceptingParams().contains(RestCsrfPreventionFilter.this.getRequestedPath(request))) {
                nonceFromRequest = this.extractNonceFromRequestParams(request);
            }
            return nonceFromRequest;
        }

        private String extractNonceFromRequestParams(HttpServletRequest request) {
            String[] params = (String[])nonceFromRequestParams.getNonce(request, "X-CSRF-Token");
            if (Objects.nonNull(params) && params.length > 0) {
                String nonce = params[0];
                for (String param : params) {
                    if (Objects.equals(param, nonce)) continue;
                    if (RestCsrfPreventionFilter.this.getLogger().isDebugEnabled()) {
                        RestCsrfPreventionFilter.this.getLogger().debug((Object)FilterBase.sm.getString("restCsrfPreventionFilter.multipleNonce.debug", new Object[]{request.getMethod(), request.getRequestURI()}));
                    }
                    return null;
                }
                return nonce;
            }
            return null;
        }
    }

    private static interface RestCsrfPreventionStrategy {
        public static final NonceSupplier<HttpServletRequest, String> nonceFromRequestHeader = HttpServletRequest::getHeader;
        public static final NonceSupplier<HttpServletRequest, String[]> nonceFromRequestParams = ServletRequest::getParameterValues;
        public static final NonceSupplier<HttpSession, String> nonceFromSession = (s, k) -> Objects.isNull(s) ? null : (String)s.getAttribute(k);
        public static final NonceConsumer<HttpServletResponse> nonceToResponse = HttpServletResponse::setHeader;
        public static final NonceConsumer<HttpSession> nonceToSession = HttpSession::setAttribute;

        public boolean apply(HttpServletRequest var1, HttpServletResponse var2) throws IOException;
    }

    @FunctionalInterface
    private static interface NonceConsumer<T> {
        public void setNonce(T var1, String var2, String var3);
    }

    @FunctionalInterface
    private static interface NonceSupplier<T, R> {
        public R getNonce(T var1, String var2);
    }
}

