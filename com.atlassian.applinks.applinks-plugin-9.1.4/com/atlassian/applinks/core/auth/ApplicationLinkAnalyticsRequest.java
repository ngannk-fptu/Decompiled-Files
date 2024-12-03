/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.net.RequestFilePart
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.analytics.ApplinksRequestExecutionEvent;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ApplicationLinkAnalyticsRequest
implements ApplicationLinkRequest {
    private final ApplicationLink remoteApplink;
    private final EventPublisher publisher;
    private ApplicationLinkRequest wrappedRequest;
    private boolean unknownReqBodySize;
    private long reqBodySize;
    private int reqUrlSize;

    ApplicationLinkAnalyticsRequest(ApplicationLinkRequest wrappedRequest, ApplicationLink remoteApplink, EventPublisher publisher) {
        this.wrappedRequest = wrappedRequest;
        this.remoteApplink = Objects.requireNonNull(remoteApplink);
        this.publisher = Objects.requireNonNull(publisher);
        this.unknownReqBodySize = false;
        this.reqBodySize = 0L;
        this.reqUrlSize = 0;
    }

    public <R> R execute(ApplicationLinkResponseHandler<R> responseHandler) throws ResponseException {
        ApplicationLinkAnalyticsResponseHandler<R> responseWrapper = new ApplicationLinkAnalyticsResponseHandler<R>(responseHandler);
        Object r = this.wrappedRequest.execute(responseWrapper);
        this.sendExecuteAnalytics(responseWrapper.getResponse().map(ApplicationLinkAnalyticsRequest::getResponseSize).orElse(null));
        return (R)r;
    }

    public ApplicationLinkRequest setConnectionTimeout(int connectionTimeout) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setConnectionTimeout(connectionTimeout);
        return this;
    }

    public ApplicationLinkRequest setSoTimeout(int soTimeout) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setSoTimeout(soTimeout);
        return this;
    }

    public ApplicationLinkRequest setUrl(String url) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setUrl(url);
        this.reqUrlSize = url.getBytes().length;
        return this;
    }

    public ApplicationLinkRequest setRequestBody(String requestBody) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setRequestBody(requestBody);
        this.reqBodySize = requestBody.getBytes().length;
        this.unknownReqBodySize = false;
        return this;
    }

    public ApplicationLinkRequest setRequestBody(String requestBody, String contentType) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setRequestBody(requestBody, contentType);
        this.reqBodySize = requestBody.getBytes().length;
        this.unknownReqBodySize = false;
        return this;
    }

    public ApplicationLinkRequest setFiles(List<RequestFilePart> files) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setFiles(files);
        this.reqBodySize = files.stream().map(f -> f.getFile().length()).mapToLong(Long::longValue).sum();
        this.unknownReqBodySize = false;
        return this;
    }

    public ApplicationLinkRequest setEntity(Object entity) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setEntity(entity);
        this.reqBodySize = 0L;
        this.unknownReqBodySize = true;
        return this;
    }

    public ApplicationLinkRequest addRequestParameters(String ... params) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.addRequestParameters(params);
        this.reqBodySize = Arrays.stream(params).map(p -> p.getBytes().length).mapToLong(Long::longValue).sum();
        this.unknownReqBodySize = false;
        return this;
    }

    public ApplicationLinkRequest addBasicAuthentication(String hostname, String username, String password) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.addBasicAuthentication(hostname, username, password);
        return this;
    }

    public ApplicationLinkRequest addHeader(String name, String value) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.addHeader(name, value);
        return this;
    }

    public ApplicationLinkRequest setHeader(String name, String value) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setHeader(name, value);
        return this;
    }

    public ApplicationLinkRequest setFollowRedirects(boolean followRedirects) {
        this.wrappedRequest = (ApplicationLinkRequest)this.wrappedRequest.setFollowRedirects(followRedirects);
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return this.wrappedRequest.getHeaders();
    }

    public void execute(ResponseHandler<? super Response> responseHandler) throws ResponseException {
        AnalyticsResponseHandler<? super Response> responseWrapper = new AnalyticsResponseHandler<Response>(responseHandler);
        this.wrappedRequest.execute(responseWrapper);
        this.sendExecuteAnalytics(responseWrapper.getResponse().map(ApplicationLinkAnalyticsRequest::getResponseSize).orElse(null));
    }

    public String execute() throws ResponseException {
        String result = this.wrappedRequest.execute();
        this.sendExecuteAnalytics(result != null ? Long.valueOf(result.getBytes().length) : null);
        return result;
    }

    public <T> T executeAndReturn(ReturningResponseHandler<? super Response, T> returningResponseHandler) throws ResponseException {
        ReturningAnalyticsResponseHandler<Response, T> responseWrapper = new ReturningAnalyticsResponseHandler<Response, T>(returningResponseHandler);
        Object t = this.wrappedRequest.executeAndReturn(responseWrapper);
        this.sendExecuteAnalytics(responseWrapper.getResponse().map(ApplicationLinkAnalyticsRequest::getResponseSize).orElse(null));
        return (T)t;
    }

    private void sendExecuteAnalytics(Long approxResponseSize) {
        Long approxRequestSize = this.unknownReqBodySize ? null : Long.valueOf(this.reqBodySize + (long)this.reqUrlSize + this.getHeaderSize());
        this.publisher.publish((Object)new ApplinksRequestExecutionEvent(approxRequestSize, approxResponseSize, this.remoteApplink.getId().get()));
    }

    private long getHeaderSize() {
        return this.getHeaders().entrySet().stream().map(entry -> {
            long keyLength = ((String)entry.getKey()).getBytes().length;
            long valueLength = ((List)entry.getValue()).stream().map(v -> v.getBytes().length).mapToLong(Long::longValue).sum();
            return keyLength + valueLength;
        }).mapToLong(Long::longValue).sum();
    }

    private static long getResponseSize(Response response) {
        long bodySize;
        long headerSize = response.getHeaders().entrySet().stream().map(entry -> (long)((String)entry.getKey()).getBytes().length + (long)((String)entry.getValue()).getBytes().length).mapToLong(Long::longValue).sum();
        try {
            bodySize = response.getResponseBodyAsString().getBytes().length;
        }
        catch (ResponseException e) {
            bodySize = 0L;
        }
        return headerSize + bodySize;
    }

    protected static class ReturningAnalyticsResponseHandler<R extends Response, T>
    implements ReturningResponseHandler<R, T> {
        private final ReturningResponseHandler<R, T> wrappedResponseHandler;
        private R response;

        ReturningAnalyticsResponseHandler(ReturningResponseHandler<R, T> wrappedResponseHandler) {
            this.wrappedResponseHandler = Objects.requireNonNull(wrappedResponseHandler);
            this.response = null;
        }

        public T handle(R response) throws ResponseException {
            this.response = response;
            return (T)this.wrappedResponseHandler.handle(response);
        }

        Optional<Response> getResponse() {
            return Optional.ofNullable(this.response);
        }
    }

    protected static class AnalyticsResponseHandler<R extends Response>
    implements ResponseHandler<R> {
        private final ResponseHandler<R> wrappedResponseHandler;
        private R response;

        AnalyticsResponseHandler(ResponseHandler<R> wrappedResponseHandler) {
            this.wrappedResponseHandler = Objects.requireNonNull(wrappedResponseHandler);
            this.response = null;
        }

        public void handle(R response) throws ResponseException {
            this.response = response;
            this.wrappedResponseHandler.handle(response);
        }

        Optional<Response> getResponse() {
            return Optional.ofNullable(this.response);
        }
    }

    protected static class ApplicationLinkAnalyticsResponseHandler<R>
    implements ApplicationLinkResponseHandler<R> {
        private final ApplicationLinkResponseHandler<R> wrappedResponseHandler;
        private Response response;

        ApplicationLinkAnalyticsResponseHandler(ApplicationLinkResponseHandler<R> wrappedResponseHandler) {
            this.wrappedResponseHandler = Objects.requireNonNull(wrappedResponseHandler);
            this.response = null;
        }

        public R credentialsRequired(Response response) throws ResponseException {
            return (R)this.wrappedResponseHandler.credentialsRequired(response);
        }

        public R handle(Response response) throws ResponseException {
            this.response = response;
            return (R)this.wrappedResponseHandler.handle(response);
        }

        Optional<Response> getResponse() {
            return Optional.ofNullable(this.response);
        }
    }
}

