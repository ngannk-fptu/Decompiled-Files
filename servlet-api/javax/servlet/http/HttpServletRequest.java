/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.MappingMatch;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;

public interface HttpServletRequest
extends ServletRequest {
    public static final String BASIC_AUTH = "BASIC";
    public static final String FORM_AUTH = "FORM";
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    public static final String DIGEST_AUTH = "DIGEST";

    public String getAuthType();

    public Cookie[] getCookies();

    public long getDateHeader(String var1);

    public String getHeader(String var1);

    public Enumeration<String> getHeaders(String var1);

    public Enumeration<String> getHeaderNames();

    public int getIntHeader(String var1);

    default public HttpServletMapping getHttpServletMapping() {
        return new HttpServletMapping(){

            @Override
            public String getMatchValue() {
                return "";
            }

            @Override
            public String getPattern() {
                return "";
            }

            @Override
            public String getServletName() {
                return "";
            }

            @Override
            public MappingMatch getMappingMatch() {
                return null;
            }
        };
    }

    public String getMethod();

    public String getPathInfo();

    public String getPathTranslated();

    default public PushBuilder newPushBuilder() {
        return null;
    }

    public String getContextPath();

    public String getQueryString();

    public String getRemoteUser();

    public boolean isUserInRole(String var1);

    public Principal getUserPrincipal();

    public String getRequestedSessionId();

    public String getRequestURI();

    public StringBuffer getRequestURL();

    public String getServletPath();

    public HttpSession getSession(boolean var1);

    public HttpSession getSession();

    public String changeSessionId();

    public boolean isRequestedSessionIdValid();

    public boolean isRequestedSessionIdFromCookie();

    public boolean isRequestedSessionIdFromURL();

    @Deprecated
    public boolean isRequestedSessionIdFromUrl();

    public boolean authenticate(HttpServletResponse var1) throws IOException, ServletException;

    public void login(String var1, String var2) throws ServletException;

    public void logout() throws ServletException;

    public Collection<Part> getParts() throws IOException, ServletException;

    public Part getPart(String var1) throws IOException, ServletException;

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> var1) throws IOException, ServletException;

    default public Map<String, String> getTrailerFields() {
        return Collections.emptyMap();
    }

    default public boolean isTrailerFieldsReady() {
        return true;
    }
}

