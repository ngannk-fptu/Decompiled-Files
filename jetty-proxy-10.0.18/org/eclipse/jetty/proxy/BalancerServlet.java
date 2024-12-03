/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.eclipse.jetty.client.api.Response
 *  org.eclipse.jetty.util.URIUtil
 */
package org.eclipse.jetty.proxy;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.URIUtil;

public class BalancerServlet
extends ProxyServlet {
    private static final String BALANCER_MEMBER_PREFIX = "balancerMember.";
    private static final List<String> FORBIDDEN_CONFIG_PARAMETERS;
    private static final List<String> REVERSE_PROXY_HEADERS;
    private static final String JSESSIONID = "jsessionid";
    private static final String JSESSIONID_URL_PREFIX = "jsessionid=";
    private final List<BalancerMember> _balancerMembers = new ArrayList<BalancerMember>();
    private final AtomicLong counter = new AtomicLong();
    private boolean _stickySessions;
    private boolean _proxyPassReverse;

    @Override
    public void init() throws ServletException {
        this.validateConfig();
        super.init();
        this.initStickySessions();
        this.initBalancers();
        this.initProxyPassReverse();
    }

    private void validateConfig() throws ServletException {
        for (String initParameterName : Collections.list(this.getServletConfig().getInitParameterNames())) {
            if (!FORBIDDEN_CONFIG_PARAMETERS.contains(initParameterName)) continue;
            throw new UnavailableException(initParameterName + " not supported in " + ((Object)((Object)this)).getClass().getName());
        }
    }

    private void initStickySessions() {
        this._stickySessions = Boolean.parseBoolean(this.getServletConfig().getInitParameter("stickySessions"));
    }

    private void initBalancers() throws ServletException {
        HashSet<BalancerMember> members = new HashSet<BalancerMember>();
        for (String balancerName : this.getBalancerNames()) {
            String memberProxyToParam = BALANCER_MEMBER_PREFIX + balancerName + ".proxyTo";
            String proxyTo = this.getServletConfig().getInitParameter(memberProxyToParam);
            if (proxyTo == null || proxyTo.trim().length() == 0) {
                throw new UnavailableException(memberProxyToParam + " parameter is empty.");
            }
            members.add(new BalancerMember(balancerName, proxyTo));
        }
        this._balancerMembers.addAll(members);
    }

    private void initProxyPassReverse() {
        this._proxyPassReverse = Boolean.parseBoolean(this.getServletConfig().getInitParameter("proxyPassReverse"));
    }

    private Set<String> getBalancerNames() throws ServletException {
        HashSet<String> names = new HashSet<String>();
        for (String initParameterName : Collections.list(this.getServletConfig().getInitParameterNames())) {
            if (!initParameterName.startsWith(BALANCER_MEMBER_PREFIX)) continue;
            int endOfNameIndex = initParameterName.lastIndexOf(".");
            if (endOfNameIndex <= BALANCER_MEMBER_PREFIX.length()) {
                throw new UnavailableException(initParameterName + " parameter does not provide a balancer member name");
            }
            names.add(initParameterName.substring(BALANCER_MEMBER_PREFIX.length(), endOfNameIndex));
        }
        return names;
    }

    @Override
    protected String rewriteTarget(HttpServletRequest request) {
        BalancerMember balancerMember = this.selectBalancerMember(request);
        if (this._log.isDebugEnabled()) {
            this._log.debug("Selected {}", (Object)balancerMember);
        }
        Object path = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            path = (String)path + "?" + query;
        }
        return URI.create(balancerMember.getProxyTo() + "/" + (String)path).normalize().toString();
    }

    private BalancerMember selectBalancerMember(HttpServletRequest request) {
        BalancerMember balancerMember;
        String name;
        if (this._stickySessions && (name = this.getBalancerMemberNameFromSessionId(request)) != null && (balancerMember = this.findBalancerMemberByName(name)) != null) {
            return balancerMember;
        }
        int index = (int)(this.counter.getAndIncrement() % (long)this._balancerMembers.size());
        return this._balancerMembers.get(index);
    }

    private BalancerMember findBalancerMemberByName(String name) {
        for (BalancerMember balancerMember : this._balancerMembers) {
            if (!balancerMember.getName().equals(name)) continue;
            return balancerMember;
        }
        return null;
    }

    private String getBalancerMemberNameFromSessionId(HttpServletRequest request) {
        String name = this.getBalancerMemberNameFromSessionCookie(request);
        if (name == null) {
            name = this.getBalancerMemberNameFromURL(request);
        }
        return name;
    }

    private String getBalancerMemberNameFromSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!JSESSIONID.equalsIgnoreCase(cookie.getName())) continue;
                return this.extractBalancerMemberNameFromSessionId(cookie.getValue());
            }
        }
        return null;
    }

    private String getBalancerMemberNameFromURL(HttpServletRequest request) {
        String requestURISuffix;
        String requestURI = request.getRequestURI();
        int idx = requestURI.lastIndexOf(";");
        if (idx > 0 && (requestURISuffix = requestURI.substring(idx + 1)).startsWith(JSESSIONID_URL_PREFIX)) {
            return this.extractBalancerMemberNameFromSessionId(requestURISuffix.substring(JSESSIONID_URL_PREFIX.length()));
        }
        return null;
    }

    private String extractBalancerMemberNameFromSessionId(String sessionId) {
        int idx = sessionId.lastIndexOf(".");
        if (idx > 0) {
            String sessionIdSuffix = sessionId.substring(idx + 1);
            return sessionIdSuffix.length() > 0 ? sessionIdSuffix : null;
        }
        return null;
    }

    @Override
    protected String filterServerResponseHeader(HttpServletRequest request, Response serverResponse, String headerName, String headerValue) {
        URI locationURI;
        if (this._proxyPassReverse && REVERSE_PROXY_HEADERS.contains(headerName) && (locationURI = URI.create(headerValue).normalize()).isAbsolute() && this.isBackendLocation(locationURI)) {
            StringBuilder newURI = URIUtil.newURIBuilder((String)request.getScheme(), (String)request.getServerName(), (int)request.getServerPort());
            String component = locationURI.getRawPath();
            if (component != null) {
                newURI.append(component);
            }
            if ((component = locationURI.getRawQuery()) != null) {
                newURI.append('?').append(component);
            }
            if ((component = locationURI.getRawFragment()) != null) {
                newURI.append('#').append(component);
            }
            return URI.create(newURI.toString()).normalize().toString();
        }
        return headerValue;
    }

    private boolean isBackendLocation(URI locationURI) {
        for (BalancerMember balancerMember : this._balancerMembers) {
            URI backendURI = balancerMember.getBackendURI();
            if (!backendURI.getHost().equals(locationURI.getHost()) || !backendURI.getScheme().equals(locationURI.getScheme()) || backendURI.getPort() != locationURI.getPort()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean validateDestination(String host, int port) {
        return true;
    }

    static {
        LinkedList<String> params = new LinkedList<String>();
        params.add("hostHeader");
        params.add("whiteList");
        params.add("blackList");
        FORBIDDEN_CONFIG_PARAMETERS = Collections.unmodifiableList(params);
        params = new LinkedList();
        params.add("Location");
        params.add("Content-Location");
        params.add("URI");
        REVERSE_PROXY_HEADERS = Collections.unmodifiableList(params);
    }

    private static class BalancerMember {
        private final String _name;
        private final String _proxyTo;
        private final URI _backendURI;

        public BalancerMember(String name, String proxyTo) {
            this._name = name;
            this._proxyTo = proxyTo;
            this._backendURI = URI.create(this._proxyTo).normalize();
        }

        public String getName() {
            return this._name;
        }

        public String getProxyTo() {
            return this._proxyTo;
        }

        public URI getBackendURI() {
            return this._backendURI;
        }

        public String toString() {
            return String.format("%s[name=%s,proxyTo=%s]", this.getClass().getSimpleName(), this._name, this._proxyTo);
        }

        public int hashCode() {
            return this._name.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            BalancerMember that = (BalancerMember)obj;
            return this._name.equals(that._name);
        }
    }
}

