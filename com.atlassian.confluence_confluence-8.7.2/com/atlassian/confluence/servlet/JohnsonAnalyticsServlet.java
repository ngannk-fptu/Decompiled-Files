/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.config.util.BootstrapUtils
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.ServletRequestBindingException
 *  org.springframework.web.bind.ServletRequestUtils
 */
package com.atlassian.confluence.servlet;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsSender;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

@ParametersAreNonnullByDefault
public class JohnsonAnalyticsServlet
extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(JohnsonAnalyticsServlet.class);
    @VisibleForTesting
    static final String HEALTH_CHECK_ANALYTICS_SENDER_KEY = "healthCheckAnalyticsSender";
    private static final Collection<RequestHandler> HANDLER_CHAIN = ImmutableList.of((Object)new EventKbArticleClickedHandler(), (Object)new GeneralKbArticleLinkClickedHandler());

    private static HealthCheckAnalyticsSender getHealthCheckAnalyticsSender() {
        return (HealthCheckAnalyticsSender)BootstrapUtils.getBootstrapContext().getBean(HEALTH_CHECK_ANALYTICS_SENDER_KEY, HealthCheckAnalyticsSender.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Optional<RequestHandler> requestHandler = HANDLER_CHAIN.stream().filter(thisHandler -> thisHandler.canHandle(request)).findFirst();
        if (requestHandler.isPresent()) {
            requestHandler.get().handle(request, response);
        } else {
            response.setStatus(404);
        }
    }

    public static class GeneralKbArticleLinkClickedHandler
    implements RequestHandler {
        public static final String URL_SUFFIX = "/johnson/analytics/kb/general";

        @Override
        public boolean canHandle(HttpServletRequest request) {
            return request.getRequestURI().endsWith(URL_SUFFIX);
        }

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response) {
            try {
                String kbUrl = ServletRequestUtils.getRequiredStringParameter((ServletRequest)request, (String)"kbUrl");
                JohnsonAnalyticsServlet.getHealthCheckAnalyticsSender().sendGeneralHelpLinkClicked(kbUrl);
                response.setStatus(200);
            }
            catch (ServletRequestBindingException e) {
                LOGGER.warn("No KB URL was provided", (Throwable)e);
                response.setStatus(400);
            }
        }
    }

    public static class EventKbArticleClickedHandler
    implements RequestHandler {
        public static final String URL_SUFFIX = "/johnson/analytics/kb/event";

        @Override
        public boolean canHandle(HttpServletRequest request) {
            return request.getRequestURI().endsWith(URL_SUFFIX);
        }

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response) {
            try {
                String eventId = ServletRequestUtils.getRequiredStringParameter((ServletRequest)request, (String)"eventId");
                JohnsonAnalyticsServlet.getHealthCheckAnalyticsSender().sendHelpLinkClickedForEvent(eventId);
                response.setStatus(200);
            }
            catch (ServletRequestBindingException e) {
                LOGGER.warn("No event ID was provided", (Throwable)e);
                response.setStatus(400);
            }
        }
    }

    private static interface RequestHandler {
        public boolean canHandle(HttpServletRequest var1);

        public void handle(HttpServletRequest var1, HttpServletResponse var2);
    }
}

