/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserRole
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 */
package com.atlassian.plugin.web.springmvc.interceptor;

import com.atlassian.crowd.embedded.admin.authorisation.AuthorisationHandler;
import com.atlassian.plugin.web.springmvc.interceptor.PubliclyAccessible;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserRole;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

abstract class AuthorisationInterceptor
extends HandlerInterceptorAdapter
implements InitializingBean,
DisposableBean {
    protected final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationProperties applicationProperties;
    private ServiceTracker authorisationHandlerServiceTracker;

    AuthorisationInterceptor(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.applicationProperties = applicationProperties;
        this.authorisationHandlerServiceTracker = null;
    }

    AuthorisationInterceptor(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties, BundleContext bundleContext) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.applicationProperties = applicationProperties;
        this.authorisationHandlerServiceTracker = new ServiceTracker(bundleContext, AuthorisationHandler.class.getName(), null);
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object service;
        if (handler != null && handler.getClass().isAnnotationPresent(PubliclyAccessible.class)) {
            return true;
        }
        if (this.authorisationHandlerServiceTracker != null && (service = this.authorisationHandlerServiceTracker.getService()) != null) {
            AuthorisationHandler authorisationHandler = (AuthorisationHandler)service;
            return authorisationHandler.handle(request, response, handler);
        }
        UserKey remoteUserKey = this.userManager.getRemoteUserKey(request);
        boolean isPermitted = this.checkPermission(remoteUserKey);
        if (!isPermitted) {
            StringBuilder requestPathBuilder = new StringBuilder(request.getRequestURI().substring(request.getContextPath().length()));
            String sep = "?";
            if ("GET".equals(request.getMethod())) {
                for (Map.Entry entry : request.getParameterMap().entrySet()) {
                    requestPathBuilder.append(sep);
                    requestPathBuilder.append((String)entry.getKey());
                    requestPathBuilder.append("=");
                    requestPathBuilder.append(URLEncoder.encode(((String[])entry.getValue())[0], "UTF8"));
                    sep = "&";
                }
            }
            String requestPath = requestPathBuilder.toString();
            request.getSession().setAttribute("seraph_originalurl", (Object)requestPath);
            response.sendRedirect(this.getRelativeLoginUrl(request.getContextPath(), requestPath));
        }
        return isPermitted;
    }

    abstract boolean checkPermission(UserKey var1);

    abstract UserRole getRole();

    private String getRelativeLoginUrl(String contextPath, String originalRequestPath) throws URISyntaxException {
        String baseUrl;
        String loginUri = this.loginUriProvider.getLoginUriForRole(new URI(originalRequestPath), this.getRole()).toString();
        if (!loginUri.startsWith(baseUrl = this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE))) {
            return loginUri;
        }
        if (!(loginUri = loginUri.substring(baseUrl.length())).startsWith("/")) {
            loginUri = "/" + loginUri;
        }
        return contextPath + loginUri;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.authorisationHandlerServiceTracker != null) {
            this.authorisationHandlerServiceTracker.open();
        }
    }

    public void destroy() throws Exception {
        if (this.authorisationHandlerServiceTracker != null) {
            this.authorisationHandlerServiceTracker.close();
        }
    }

    @Deprecated
    public void setAuthorisationHandlerServiceTracker(ServiceTracker serviceTracker) throws Exception {
        this.authorisationHandlerServiceTracker = serviceTracker;
        this.afterPropertiesSet();
    }
}

