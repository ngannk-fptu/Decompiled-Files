/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.reliability;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.mywork.client.ClientUtil;
import com.atlassian.mywork.client.reliability.UnreliableTask;
import com.atlassian.mywork.client.reliability.UnreliableTaskListener;
import com.atlassian.mywork.client.reliability.UnreliableWorker;
import com.atlassian.mywork.client.util.ResponseUtil;
import com.atlassian.mywork.service.HostService;
import com.atlassian.mywork.service.ImpersonationService;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUnreliableWorker
implements UnreliableWorker,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultUnreliableWorker.class);
    private static final int RESPONSE_BODY_MAX = 0x100000;
    private final ImpersonationService impersonationService;
    private final ExecutorService executorService;
    private final HostService hostService;
    private final ObjectMapper mapper = new ObjectMapper();

    public DefaultUnreliableWorker(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, ImpersonationService impersonationService, HostService hostService) {
        this.impersonationService = impersonationService;
        this.hostService = hostService;
        this.executorService = ClientUtil.newExecutorService(this.getClass().getName(), threadLocalDelegateExecutorFactory);
    }

    public void onStart() {
    }

    public void onStop() {
        this.executorService.shutdown();
    }

    @Override
    public void start(final UnreliableTask task, final UnreliableTaskListener listener) {
        try {
            final ReliableJsonRequest userWrapper = (ReliableJsonRequest)this.mapper.readValue(task.getTaskData(), ReliableJsonRequest.class);
            this.impersonationService.runAs(userWrapper.username, new Runnable(){

                @Override
                public void run() {
                    DefaultUnreliableWorker.this.executorService.execute(new Runnable(){

                        @Override
                        public void run() {
                            try {
                                ApplicationLink appLink = (ApplicationLink)Iterables.find(DefaultUnreliableWorker.this.hostService.getActiveHost(), (Predicate)new Predicate<ApplicationLink>(){

                                    public boolean apply(ApplicationLink availableAppLink) {
                                        return availableAppLink.getId().get().equals(task.appLinkId);
                                    }
                                });
                                listener.succeeded(DefaultUnreliableWorker.this.send(appLink, userWrapper));
                            }
                            catch (NoSuchElementException e) {
                                log.warn("Host {} for task {} is not the active host", (Object)task.appLinkId, (Object)task.getTaskData());
                                listener.cancel();
                            }
                            catch (UnauthorizedException e) {
                                log.debug("User is unauthorized for task {}", (Object)task.getTaskData());
                                listener.cancel();
                            }
                            catch (Exception exception) {
                                listener.failed(exception);
                                throw new RuntimeException(exception);
                            }
                        }
                    });
                }
            });
        }
        catch (Exception e) {
            listener.failed(e);
        }
    }

    private String send(ApplicationLink applicationLink, ReliableJsonRequest data) throws ResponseException {
        try {
            ApplicationLinkRequest request = applicationLink.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class).createRequest(data.type, data.path);
            request.setHeader("Content-Type", "application/json");
            if (data.json != null && !data.json.isEmpty()) {
                request.setRequestBody(data.json);
            }
            if (data.type != Request.MethodType.GET) {
                request.setHeader("X-Atlassian-Token", "no-check");
            }
            return (String)request.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<String>(){

                public String credentialsRequired(Response response) {
                    throw new UnauthorizedException();
                }

                public String handle(Response response) throws ResponseException {
                    if (!response.isSuccessful()) {
                        if (response.getStatusCode() == 401) {
                            throw new UnauthorizedException();
                        }
                        throw new ResponseException(response.getStatusCode() + " - " + response.getStatusText());
                    }
                    return ResponseUtil.readResponseBodyAsString(response, 0x100000);
                }
            });
        }
        catch (CredentialsRequiredException e) {
            throw new UnauthorizedException();
        }
    }

    private static class UnauthorizedException
    extends RuntimeException {
    }

    public static class ReliableJsonRequest {
        @JsonProperty
        private String username;
        @JsonProperty
        private Request.MethodType type;
        @JsonProperty
        private String json;
        @JsonProperty
        private String path;

        private ReliableJsonRequest() {
        }

        public ReliableJsonRequest(String username, Request.MethodType type, String json, String path) {
            this.username = username;
            this.type = type;
            this.json = json;
            this.path = path;
        }

        public String getJson() {
            return this.json;
        }
    }
}

