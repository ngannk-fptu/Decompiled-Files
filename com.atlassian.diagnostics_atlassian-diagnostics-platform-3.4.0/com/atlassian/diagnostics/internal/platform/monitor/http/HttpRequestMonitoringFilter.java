/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.internal.AlertProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nonnull
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.monitor.http;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.AlertProvider;
import com.atlassian.diagnostics.internal.platform.monitor.http.HttpMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.http.HttpRequestDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.http.HttpRequestMonitor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestMonitoringFilter
extends AlertProvider<HttpMonitorConfiguration>
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestMonitoringFilter.class);
    private final HttpRequestMonitor monitor;
    private final UserManager userManager;
    private final Clock clock;
    private final I18nResolver i18nResolver;
    private final String key = HttpRequestMonitor.class.getName();
    private final String I18N_NO_USERNAME_DETECTED = "diagnostics.http.no.username";

    public HttpRequestMonitoringFilter(@Nonnull HttpRequestMonitor monitor, @Nonnull UserManager userManager, @Nonnull HttpMonitorConfiguration config, @Nonnull Clock clock, @Nonnull I18nResolver i18nResolver) {
        super(HttpRequestMonitor.class.getName(), (MonitorConfiguration)config);
        this.monitor = monitor;
        this.userManager = userManager;
        this.clock = clock;
        this.i18nResolver = i18nResolver;
    }

    public void init(FilterConfig filterConfig) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Instant requestStartTime = this.clock.instant();
        String username = this.getUsernameFromRequest(request);
        try {
            chain.doFilter(request, response);
        }
        finally {
            try {
                Instant requestEndTime = this.clock.instant();
                if (this.isMonitoringEnabledFor(request) && ((HttpMonitorConfiguration)this.monitorConfiguration).isEnabled() && this.isRequestSlow(requestStartTime, requestEndTime)) {
                    this.raiseAlertForSlowHttpRequest(requestStartTime, requestEndTime, (HttpServletRequest)request, username);
                }
            }
            catch (Exception e) {
                logger.debug("Failed to raise alert", (Throwable)e);
            }
        }
    }

    private boolean isMonitoringEnabledFor(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            String path = ((HttpServletRequest)request).getRequestURI();
            return path.contains("/rest/") || path.contains("/servlet/");
        }
        return false;
    }

    private boolean isRequestSlow(Instant requestStartTime, Instant requestFinishTime) {
        return Duration.between(requestStartTime, requestFinishTime).toMillis() > ((HttpMonitorConfiguration)this.monitorConfiguration).getMaximumHttpRequestTime().toMillis();
    }

    private void raiseAlertForSlowHttpRequest(Instant requestStartTime, Instant requestEndTime, HttpServletRequest request, String username) {
        String url = request != null && request.getRequestURI() != null ? request.getRequestURI() : "";
        HttpRequestDiagnostic diagnostic = new HttpRequestDiagnostic(url, username, Duration.between(requestStartTime, requestEndTime));
        this.monitor.raiseAlertForSlowHttpRequest(Instant.now(), diagnostic);
    }

    private String getUsernameFromRequest(ServletRequest httpRequest) {
        Optional<UserProfile> remoteUser = Optional.ofNullable(this.userManager.getRemoteUser((HttpServletRequest)httpRequest));
        return remoteUser.isPresent() ? remoteUser.get().getUsername() : this.i18nResolver.getText("diagnostics.http.no.username");
    }

    public void destroy() {
    }

    public String getKey() {
        return this.key;
    }
}

