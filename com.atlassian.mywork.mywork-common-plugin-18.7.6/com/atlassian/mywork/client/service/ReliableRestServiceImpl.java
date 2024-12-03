/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.google.common.base.Function
 *  com.google.common.util.concurrent.Futures
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.mywork.client.reliability.DefaultUnreliableWorker;
import com.atlassian.mywork.client.reliability.ReliabilityService;
import com.atlassian.mywork.client.reliability.UnreliableTask;
import com.atlassian.mywork.client.service.ReliableRestService;
import com.atlassian.mywork.client.util.FutureUtil;
import com.atlassian.mywork.rest.JsonObject;
import com.atlassian.mywork.service.HostService;
import com.atlassian.mywork.service.ImpersonationService;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReliableRestServiceImpl
implements ReliableRestService {
    private static final Logger log = LoggerFactory.getLogger(ReliableRestServiceImpl.class);
    private final ImpersonationService impersonationService;
    private final ReliabilityService reliabilityService;
    private final HostService hostService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReliableRestServiceImpl(ImpersonationService impersonationService, ReliabilityService reliabilityService, HostService hostService) {
        this.impersonationService = impersonationService;
        this.reliabilityService = reliabilityService;
        this.hostService = hostService;
    }

    @Override
    public <T extends JsonObject> Future<List<T>> post(String username, String path, List<T> objects, TypeReference<List<T>> type) {
        return this.submit(username, Request.MethodType.POST, path, objects, type);
    }

    @Override
    public <T extends JsonObject> Future<T> post(String username, String path, final T object) {
        return this.submit(username, Request.MethodType.POST, path, object, new TypeReference<T>(){

            public Type getType() {
                return object.getClass();
            }
        });
    }

    @Override
    public void delete(String username, String path) {
        this.submit(username, Request.MethodType.DELETE, path, "");
    }

    @Override
    public String get(String username, String url) throws CredentialsRequiredException {
        return this.get(username, url, new Function<Response, String>(){

            public String apply(Response from) {
                try {
                    return from.getResponseBodyAsString();
                }
                catch (ResponseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public <T> T get(String username, String url, final Class<T> type) throws CredentialsRequiredException {
        return this.get(username, url, new TypeReference<T>(){

            public Type getType() {
                return type;
            }
        });
    }

    @Override
    public <T> T get(String username, String url, final TypeReference<T> type) throws CredentialsRequiredException {
        return this.get(username, url, new Function<Response, T>(){

            public T apply(Response response) {
                try {
                    return new ObjectMapper().readValue(response.getResponseBodyAsStream(), type);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private <T> T get(String username, final String url, final Function<Response, T> callback) throws CredentialsRequiredException {
        Iterator iterator = this.hostService.getActiveHost().iterator();
        if (iterator.hasNext()) {
            final ApplicationLink link = (ApplicationLink)iterator.next();
            try {
                return (T)this.impersonationService.runAs(username, new Callable<T>(){

                    @Override
                    public T call() throws Exception {
                        return link.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class).createRequest(Request.MethodType.GET, url).executeAndReturn(new ReturningResponseHandler<Response, T>(){

                            public T handle(Response response) {
                                return callback.apply((Object)response);
                            }
                        });
                    }
                });
            }
            catch (CredentialsRequiredException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        log.warn("Task failed: Could not find an available host");
        return null;
    }

    private Future<String> submit(String string) {
        Iterator iterator = this.hostService.getActiveHost().iterator();
        if (iterator.hasNext()) {
            ApplicationLink link = (ApplicationLink)iterator.next();
            return this.reliabilityService.submit(new UnreliableTask(link.getId().get(), string));
        }
        log.warn("Task submission failed: Could not find an available host");
        return Futures.immediateFuture((Object)"{}");
    }

    private <T> Future<T> submit(String username, Request.MethodType type, String path, T object, final TypeReference<T> refType) {
        return FutureUtil.map(this.submit(username, type, path, this.writeValueAsString(object)), new Function<String, T>(){

            public T apply(String input) {
                try {
                    return ReliableRestServiceImpl.this.mapper.readValue(input, refType);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Future<String> submit(String username, Request.MethodType type, String path, String json) {
        return this.submit(this.writeValueAsString(new DefaultUnreliableWorker.ReliableJsonRequest(username, type, json, path)));
    }

    private <T> String writeValueAsString(T object) {
        try {
            return this.mapper.writeValueAsString(object);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

