/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.RequestFilePart
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 */
package com.atlassian.plugins.rest.module.jersey;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugins.rest.module.jersey.EntityConversionException;
import com.atlassian.plugins.rest.module.jersey.JerseyEntityHandler;
import com.atlassian.plugins.rest.module.jersey.JerseyResponse;
import com.atlassian.plugins.rest.module.jersey.UnsupportedContentTypeException;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.core.MediaType;

public class JerseyRequest
implements Request<JerseyRequest, JerseyResponse> {
    private final Request delegateRequest;
    private final JerseyEntityHandler jerseyEntityHandler;
    private final Plugin plugin;
    private Object entity;

    public JerseyRequest(Request delegateRequest, JerseyEntityHandler jerseyEntityHandler, Plugin plugin) {
        this.delegateRequest = delegateRequest;
        this.jerseyEntityHandler = jerseyEntityHandler;
        this.plugin = plugin;
    }

    public JerseyRequest addBasicAuthentication(String hostname, String username, String password) {
        this.delegateRequest.addBasicAuthentication(hostname, username, password);
        return this;
    }

    public JerseyRequest setEntity(Object entity) {
        this.entity = Objects.requireNonNull(entity);
        return this;
    }

    public JerseyRequest setConnectionTimeout(int i) {
        this.delegateRequest.setConnectionTimeout(i);
        return this;
    }

    public JerseyRequest setSoTimeout(int i) {
        this.delegateRequest.setSoTimeout(i);
        return this;
    }

    public JerseyRequest setUrl(String s) {
        this.delegateRequest.setUrl(s);
        return this;
    }

    public JerseyRequest setRequestBody(String s) {
        this.delegateRequest.setRequestBody(s);
        return this;
    }

    public JerseyRequest setRequestBody(String requestBody, String contentType) {
        this.delegateRequest.setRequestBody(requestBody, contentType);
        return this;
    }

    public JerseyRequest setFiles(List<RequestFilePart> files) {
        this.delegateRequest.setFiles(files);
        return this;
    }

    public JerseyRequest addRequestParameters(String ... strings) {
        this.delegateRequest.addRequestParameters(strings);
        return this;
    }

    public JerseyRequest addHeader(String s, String s1) {
        this.delegateRequest.addHeader(s, s1);
        return this;
    }

    public JerseyRequest setHeader(String s, String s1) {
        this.delegateRequest.setHeader(s, s1);
        return this;
    }

    public JerseyRequest setFollowRedirects(boolean follow) {
        this.delegateRequest.setFollowRedirects(follow);
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return this.delegateRequest.getHeaders();
    }

    public void execute(final ResponseHandler<? super JerseyResponse> responseHandler) throws ResponseException {
        this.executeAndReturn(new ReturningResponseHandler<JerseyResponse, Void>(){

            public Void handle(JerseyResponse jerseyResponse) throws ResponseException {
                responseHandler.handle((Response)jerseyResponse);
                return null;
            }
        });
    }

    public String execute() throws ResponseException {
        this.marshallEntity();
        return this.delegateRequest.execute();
    }

    public <RET> RET executeAndReturn(final ReturningResponseHandler<? super JerseyResponse, RET> responseHandler) throws ResponseException {
        this.marshallEntity();
        Object result = this.delegateRequest.executeAndReturn(new ReturningResponseHandler<Response, RET>(){

            public RET handle(Response response) throws ResponseException {
                JerseyResponse res = new JerseyResponse(response, JerseyRequest.this.jerseyEntityHandler, JerseyRequest.this.plugin);
                return responseHandler.handle((Response)res);
            }
        });
        return (RET)result;
    }

    private void marshallEntity() {
        if (this.entity != null) {
            MediaType type;
            String contentType = this.getOrSetSingleHeaderValue("Content-Type", "application/xml");
            this.getOrSetSingleHeaderValue("Accept", contentType);
            Charset charset = StandardCharsets.UTF_8;
            try {
                type = MediaType.valueOf(contentType);
            }
            catch (IllegalArgumentException e) {
                throw new UnsupportedContentTypeException(e.getMessage(), e);
            }
            try {
                String body = this.jerseyEntityHandler.marshall(this.entity, type, charset);
                this.setRequestBody(body, contentType);
            }
            catch (IOException e) {
                throw new EntityConversionException(e);
            }
        }
    }

    private String getOrSetSingleHeaderValue(String headerName, String defaultValue) {
        String value = defaultValue;
        List<String> headers = this.getHeaders().get(headerName);
        if (headers != null && !headers.isEmpty()) {
            if (headers.size() == 1) {
                value = headers.get(0);
            }
        } else {
            this.setHeader(headerName, defaultValue);
        }
        return value;
    }
}

