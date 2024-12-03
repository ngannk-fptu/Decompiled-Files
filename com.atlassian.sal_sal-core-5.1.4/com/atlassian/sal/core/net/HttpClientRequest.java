/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFilePart
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpHost
 *  org.apache.http.NameValuePair
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.RequestBuilder
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.entity.mime.MultipartEntityBuilder
 *  org.apache.http.impl.auth.BasicScheme
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.message.BasicNameValuePair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.net;

import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.atlassian.sal.core.net.HttpClientResponse;
import com.atlassian.sal.core.net.SystemPropertiesConnectionConfig;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientRequest<T extends Request<?, ?>, RESP extends Response>
implements Request<HttpClientRequest<?, ?>, HttpClientResponse> {
    @VisibleForTesting
    static final String METRIC_NAME = "http.sal.request";
    private static final Logger log = LoggerFactory.getLogger(HttpClientRequest.class);
    private final CloseableHttpClient httpClient;
    private final List<NameValuePair> requestParameters;
    protected final HttpClientContext httpClientContext;
    final RequestBuilder requestBuilder;
    final RequestConfig.Builder requestConfigBuilder;
    private final Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private final String pluginKey;

    public HttpClientRequest(CloseableHttpClient httpClient, HttpClientContext httpClientContext, Request.MethodType initialMethodType, String initialUrl) {
        this.httpClient = httpClient;
        this.httpClientContext = httpClientContext;
        this.requestBuilder = RequestBuilder.create((String)initialMethodType.toString()).setUri(initialUrl);
        this.requestParameters = new LinkedList<NameValuePair>();
        this.pluginKey = PluginKeyStack.getFirstPluginKey();
        SystemPropertiesConnectionConfig connectionConfig = new SystemPropertiesConnectionConfig();
        this.requestConfigBuilder = RequestConfig.custom().setConnectTimeout(connectionConfig.getConnectionTimeout()).setSocketTimeout(connectionConfig.getSocketTimeout()).setMaxRedirects(connectionConfig.getMaxRedirects()).setCookieSpec("standard");
    }

    public String execute() throws ResponseException {
        return (String)this.executeAndReturn(response -> {
            if (!response.isSuccessful()) {
                throw new ResponseStatusException("Unexpected response received. Status code: " + response.getStatusCode(), response);
            }
            return response.getResponseBodyAsString();
        });
    }

    public void execute(ResponseHandler<? super HttpClientResponse> responseHandler) throws ResponseException {
        this.executeAndReturn(response -> {
            responseHandler.handle(response);
            return null;
        });
    }

    /*
     * Exception decompiling
     */
    public <RET> RET executeAndReturn(ReturningResponseHandler<? super HttpClientResponse, RET> responseHandler) throws ResponseException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 1[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    public HttpClientRequest addBasicAuthentication(String hostname, String username, String password) {
        this.httpClientContext.getCredentialsProvider().setCredentials(new AuthScope(hostname, -1), (Credentials)new UsernamePasswordCredentials(username, password));
        this.httpClientContext.getAuthCache().put(new HttpHost(hostname), (AuthScheme)new BasicScheme());
        return this;
    }

    public HttpClientRequest setConnectionTimeout(int connectionTimeout) {
        this.requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout);
        return this;
    }

    public HttpClientRequest setSoTimeout(int soTimeout) {
        this.requestConfigBuilder.setSocketTimeout(soTimeout);
        return this;
    }

    public HttpClientRequest setUrl(String url) {
        this.requestBuilder.setUri(url);
        return this;
    }

    public HttpClientRequest setRequestBody(String requestBody) {
        return this.setRequestBody(requestBody, ContentType.TEXT_PLAIN.getMimeType());
    }

    public HttpClientRequest setRequestBody(String requestBodyString, String contentTypeString) {
        Preconditions.checkNotNull((Object)requestBodyString);
        Preconditions.checkNotNull((Object)contentTypeString);
        Preconditions.checkState((boolean)this.isRequestBodyMethod(), (Object)"Only PUT or POST methods accept a request body.");
        this.requestBuilder.setEntity((HttpEntity)new StringEntity(requestBodyString, ContentType.create((String)contentTypeString, (Charset)StandardCharsets.UTF_8)));
        return this;
    }

    public HttpClientRequest setFiles(List<RequestFilePart> requestBodyFiles) {
        Preconditions.checkNotNull(requestBodyFiles);
        Preconditions.checkState((boolean)this.isRequestBodyMethod(), (Object)"Only PUT or POST methods accept a request body.");
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        for (RequestFilePart requestBodyFile : requestBodyFiles) {
            ContentType fileContentType = ContentType.create((String)requestBodyFile.getContentType());
            multipartEntityBuilder.addBinaryBody(requestBodyFile.getParameterName(), requestBodyFile.getFile(), fileContentType, requestBodyFile.getFileName());
        }
        this.requestBuilder.setEntity(multipartEntityBuilder.build());
        return this;
    }

    public HttpClientRequest addRequestParameters(String ... params) {
        Preconditions.checkNotNull((Object)params);
        Preconditions.checkState((boolean)this.isRequestBodyMethod(), (Object)"Only PUT or POST methods accept a request body.");
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("You must enter an even number of arguments.");
        }
        for (int i = 0; i < params.length; i += 2) {
            String name = params[i];
            String value = params[i + 1];
            this.requestParameters.add((NameValuePair)new BasicNameValuePair(name, value));
        }
        return this;
    }

    private boolean isRequestBodyMethod() {
        String methodType = this.requestBuilder.getMethod();
        return "POST".equals(methodType) || "PUT".equals(methodType);
    }

    public HttpClientRequest addHeader(String headerName, String headerValue) {
        this.headers.computeIfAbsent(headerName, k -> new ArrayList()).add(headerValue);
        this.requestBuilder.addHeader(headerName, headerValue);
        return this;
    }

    public HttpClientRequest setHeader(String headerName, String headerValue) {
        this.headers.put(headerName, new ArrayList<String>(Collections.singletonList(headerValue)));
        this.requestBuilder.setHeader(headerName, headerValue);
        return this;
    }

    public HttpClientRequest setFollowRedirects(boolean follow) {
        this.requestConfigBuilder.setRedirectsEnabled(follow);
        return this;
    }

    public HttpClientRequest setEntity(Object entity) {
        throw new UnsupportedOperationException("This SAL request does not support object marshalling. Use the RequestFactory component instead.");
    }
}

