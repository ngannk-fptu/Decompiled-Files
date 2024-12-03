/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.csp;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.net.URI;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.action.CspSettingsAware;
import org.apache.struts2.interceptor.csp.CspSettings;
import org.apache.struts2.interceptor.csp.DefaultCspSettings;

public final class CspInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(CspInterceptor.class);
    private Boolean enforcingMode;
    private String reportUri;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (action instanceof CspSettingsAware) {
            LOG.trace("Using CspSettings provided by the action: {}", action);
            this.applySettings(invocation, ((CspSettingsAware)action).getCspSettings());
        } else {
            LOG.trace("Using DefaultCspSettings with action: {}", action);
            this.applySettings(invocation, new DefaultCspSettings());
        }
        return invocation.invoke();
    }

    private void applySettings(ActionInvocation invocation, CspSettings cspSettings) {
        if (this.enforcingMode != null) {
            LOG.trace("Applying: {} to enforcingMode", (Object)this.enforcingMode);
            cspSettings.setEnforcingMode(this.enforcingMode);
        }
        if (this.reportUri != null) {
            LOG.trace("Applying: {} to reportUri", (Object)this.reportUri);
            cspSettings.setReportUri(this.reportUri);
        }
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        invocation.addPreResultListener((actionInvocation, resultCode) -> {
            LOG.trace("Applying CSP header: {} to the request", (Object)cspSettings);
            cspSettings.addCspHeaders(request, response);
        });
    }

    public void setReportUri(String reportUri) {
        Optional<URI> uri = this.buildUri(reportUri);
        if (!uri.isPresent()) {
            throw new IllegalArgumentException("Could not parse configured report URI for CSP interceptor: " + reportUri);
        }
        if (!uri.get().isAbsolute() && !reportUri.startsWith("/")) {
            throw new IllegalArgumentException("Illegal configuration: report URI is not relative to the root. Please set a report URI that starts with /");
        }
        this.reportUri = reportUri;
    }

    private Optional<URI> buildUri(String reportUri) {
        try {
            return Optional.of(URI.create(reportUri));
        }
        catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public void setEnforcingMode(String value) {
        this.enforcingMode = Boolean.parseBoolean(value);
    }
}

