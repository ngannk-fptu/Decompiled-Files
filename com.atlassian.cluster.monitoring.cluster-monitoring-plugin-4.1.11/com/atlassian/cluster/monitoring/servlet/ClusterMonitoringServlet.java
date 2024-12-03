/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cluster.monitoring.spi.ClusterMonitoring
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.cluster.monitoring.servlet;

import com.atlassian.cluster.monitoring.spi.ClusterMonitoring;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class ClusterMonitoringServlet
extends HttpServlet {
    private static final String CONF_SERAPH_SECURITY_ORIGINAL_URL = "seraph_originalurl";
    private static final String JIRA_SERAPH_SECURITY_ORIGINAL_URL = "os_security_originalurl";
    private final UserManager userManager;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final ClusterMonitoring clusterMonitoring;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationProperties applicationProperties;
    private final WebSudoManager webSudoManager;

    @Autowired
    public ClusterMonitoringServlet(@ComponentImport UserManager userManager, @ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport ClusterMonitoring clusterMonitoring, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport WebSudoManager webSudoManager) {
        this.userManager = Objects.requireNonNull(userManager);
        this.soyTemplateRenderer = Objects.requireNonNull(soyTemplateRenderer);
        this.clusterMonitoring = Objects.requireNonNull(clusterMonitoring);
        this.loginUriProvider = Objects.requireNonNull(loginUriProvider);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.webSudoManager = Objects.requireNonNull(webSudoManager);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserKey userKey = this.userManager.getRemoteUserKey(req);
        if (userKey == null || !this.userManager.isSystemAdmin(userKey)) {
            URI uri = this.getUri(req);
            this.addSessionAttributes(req, uri.toASCIIString());
            resp.sendRedirect(this.loginUriProvider.getLoginUri(uri).toASCIIString());
            return;
        }
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            resp.setContentType("text/html; charset=UTF-8");
            ImmutableMap context = ImmutableMap.of((Object)"clusteringAvailable", (Object)this.clusterMonitoring.isAvailable(), (Object)"dcLicensed", (Object)this.clusterMonitoring.isDataCenterLicensed(), (Object)"clusterSetupEnabled", (Object)this.clusterMonitoring.isClusterSetupEnabled(), (Object)"major", (Object)this.getProductMajorVersion(), (Object)"minor", (Object)this.getProductMinorVersion());
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), "com.atlassian.cluster.monitoring.cluster-monitoring-plugin:bootstrap-resource", "Cluster.Monitoring.Templates.bootstrap", (Map)context);
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
        catch (SoyException se) {
            throw new RuntimeException(se);
        }
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    private void addSessionAttributes(HttpServletRequest request, String uriString) {
        request.getSession().setAttribute(JIRA_SERAPH_SECURITY_ORIGINAL_URL, (Object)uriString);
        request.getSession().setAttribute(CONF_SERAPH_SECURITY_ORIGINAL_URL, (Object)uriString);
    }

    private String getProductMajorVersion() {
        return this.splitVersion().length > 0 ? this.splitVersion()[0] : "0";
    }

    private String getProductMinorVersion() {
        return this.splitVersion().length > 1 ? this.splitVersion()[1] : "0";
    }

    private String[] splitVersion() {
        String version = this.applicationProperties.getVersion();
        return version.split("\\.");
    }
}

