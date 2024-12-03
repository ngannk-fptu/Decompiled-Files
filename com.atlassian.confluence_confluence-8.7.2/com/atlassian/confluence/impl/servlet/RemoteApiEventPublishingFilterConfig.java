/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Resource
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.servlet;

import com.atlassian.confluence.impl.servlet.RequestEventPublishingFilter;
import com.atlassian.event.api.EventPublisher;
import java.util.function.Predicate;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteApiEventPublishingFilterConfig {
    @Resource
    private EventPublisher eventPublisher;

    @Bean
    public RequestEventPublishingFilter legacyRemoteApiEventPublishingFilter() {
        return new RequestEventPublishingFilter(this.eventPublisher, RequestEventPublishingFilter.createHandler("confluence.remote.soapaxis", RemoteApiEventPublishingFilterConfig.isSoapAxisRequest()), RequestEventPublishingFilter.createHandler("confluence.remote.rpc", RemoteApiEventPublishingFilterConfig.isRpcRequest()), RequestEventPublishingFilter.createHandler("confluence.remote.rest.prototype", RemoteApiEventPublishingFilterConfig.isPrototypeRestRequest()));
    }

    private static Predicate<HttpServletRequest> isSoapAxisRequest() {
        return request -> "/plugins/servlet".equals(request.getServletPath()) && request.getPathInfo() != null && request.getPathInfo().startsWith("/soap-axis1/");
    }

    private static Predicate<HttpServletRequest> isRpcRequest() {
        return request -> request.getServletPath().startsWith("/rpc/");
    }

    private static Predicate<HttpServletRequest> isPrototypeRestRequest() {
        return request -> "/rest".equals(request.getServletPath()) && request.getPathInfo() != null && request.getPathInfo().startsWith("/prototype/");
    }
}

