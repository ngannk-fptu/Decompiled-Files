/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.InterceptingAsyncHttpAccessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.AsyncRestOperations;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.AbstractUriTemplateHandler;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

@Deprecated
public class AsyncRestTemplate
extends InterceptingAsyncHttpAccessor
implements AsyncRestOperations {
    private final RestTemplate syncTemplate;

    public AsyncRestTemplate() {
        this(new SimpleAsyncTaskExecutor());
    }

    public AsyncRestTemplate(AsyncListenableTaskExecutor taskExecutor) {
        Assert.notNull((Object)taskExecutor, "AsyncTaskExecutor must not be null");
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setTaskExecutor(taskExecutor);
        this.syncTemplate = new RestTemplate(requestFactory);
        this.setAsyncRequestFactory(requestFactory);
    }

    public AsyncRestTemplate(AsyncClientHttpRequestFactory asyncRequestFactory) {
        this(asyncRequestFactory, (ClientHttpRequestFactory)((Object)asyncRequestFactory));
    }

    public AsyncRestTemplate(AsyncClientHttpRequestFactory asyncRequestFactory, ClientHttpRequestFactory syncRequestFactory) {
        this(asyncRequestFactory, new RestTemplate(syncRequestFactory));
    }

    public AsyncRestTemplate(AsyncClientHttpRequestFactory requestFactory, RestTemplate restTemplate) {
        Assert.notNull((Object)restTemplate, "RestTemplate must not be null");
        this.syncTemplate = restTemplate;
        this.setAsyncRequestFactory(requestFactory);
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        this.syncTemplate.setErrorHandler(errorHandler);
    }

    public ResponseErrorHandler getErrorHandler() {
        return this.syncTemplate.getErrorHandler();
    }

    public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
        UriTemplateHandler handler = this.syncTemplate.getUriTemplateHandler();
        if (handler instanceof DefaultUriBuilderFactory) {
            ((DefaultUriBuilderFactory)handler).setDefaultUriVariables(defaultUriVariables);
        } else if (handler instanceof AbstractUriTemplateHandler) {
            ((AbstractUriTemplateHandler)handler).setDefaultUriVariables(defaultUriVariables);
        } else {
            throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
        }
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        this.syncTemplate.setUriTemplateHandler(handler);
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return this.syncTemplate.getUriTemplateHandler();
    }

    @Override
    public RestOperations getRestOperations() {
        return this.syncTemplate;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.syncTemplate.setMessageConverters(messageConverters);
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.syncTemplate.getMessageConverters();
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        AsyncRequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    @Override
    public ListenableFuture<HttpHeaders> headForHeaders(String url, Object ... uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        return this.execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
    }

    @Override
    public ListenableFuture<HttpHeaders> headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        return this.execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
    }

    @Override
    public ListenableFuture<HttpHeaders> headForHeaders(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        return this.execute(url, HttpMethod.HEAD, null, headersExtractor);
    }

    @Override
    public ListenableFuture<URI> postForLocation(String url, @Nullable HttpEntity<?> request, Object ... uriVars) throws RestClientException {
        AsyncRequestCallback callback = this.httpEntityCallback(request);
        ResponseExtractor<HttpHeaders> extractor = this.headersExtractor();
        ListenableFuture<HttpHeaders> future = this.execute(url, HttpMethod.POST, callback, extractor, uriVars);
        return AsyncRestTemplate.adaptToLocationHeader(future);
    }

    @Override
    public ListenableFuture<URI> postForLocation(String url, @Nullable HttpEntity<?> request, Map<String, ?> uriVars) throws RestClientException {
        AsyncRequestCallback callback = this.httpEntityCallback(request);
        ResponseExtractor<HttpHeaders> extractor = this.headersExtractor();
        ListenableFuture<HttpHeaders> future = this.execute(url, HttpMethod.POST, callback, extractor, uriVars);
        return AsyncRestTemplate.adaptToLocationHeader(future);
    }

    @Override
    public ListenableFuture<URI> postForLocation(URI url, @Nullable HttpEntity<?> request) throws RestClientException {
        AsyncRequestCallback callback = this.httpEntityCallback(request);
        ResponseExtractor<HttpHeaders> extractor = this.headersExtractor();
        ListenableFuture<HttpHeaders> future = this.execute(url, HttpMethod.POST, callback, extractor);
        return AsyncRestTemplate.adaptToLocationHeader(future);
    }

    private static ListenableFuture<URI> adaptToLocationHeader(ListenableFuture<HttpHeaders> future) {
        return new ListenableFutureAdapter<URI, HttpHeaders>(future){

            @Override
            @Nullable
            protected URI adapt(HttpHeaders headers) throws ExecutionException {
                return headers.getLocation();
            }
        };
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, @Nullable HttpEntity<?> request, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, @Nullable HttpEntity<?> request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI url, @Nullable HttpEntity<?> request, Class<T> responseType) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }

    @Override
    public ListenableFuture<?> put(String url, @Nullable HttpEntity<?> request, Object ... uriVars) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        return this.execute(url, HttpMethod.PUT, requestCallback, null, uriVars);
    }

    @Override
    public ListenableFuture<?> put(String url, @Nullable HttpEntity<?> request, Map<String, ?> uriVars) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        return this.execute(url, HttpMethod.PUT, requestCallback, null, uriVars);
    }

    @Override
    public ListenableFuture<?> put(URI url, @Nullable HttpEntity<?> request) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        return this.execute(url, HttpMethod.PUT, requestCallback, null);
    }

    @Override
    public ListenableFuture<?> delete(String url, Object ... uriVariables) throws RestClientException {
        return this.execute(url, HttpMethod.DELETE, null, null, uriVariables);
    }

    @Override
    public ListenableFuture<?> delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        return this.execute(url, HttpMethod.DELETE, null, null, uriVariables);
    }

    @Override
    public ListenableFuture<?> delete(URI url) throws RestClientException {
        return this.execute(url, HttpMethod.DELETE, null, null);
    }

    @Override
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Object ... uriVars) throws RestClientException {
        ResponseExtractor<HttpHeaders> extractor = this.headersExtractor();
        ListenableFuture<HttpHeaders> future = this.execute(url, HttpMethod.OPTIONS, null, extractor, uriVars);
        return AsyncRestTemplate.adaptToAllowHeader(future);
    }

    @Override
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Map<String, ?> uriVars) throws RestClientException {
        ResponseExtractor<HttpHeaders> extractor = this.headersExtractor();
        ListenableFuture<HttpHeaders> future = this.execute(url, HttpMethod.OPTIONS, null, extractor, uriVars);
        return AsyncRestTemplate.adaptToAllowHeader(future);
    }

    @Override
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> extractor = this.headersExtractor();
        ListenableFuture<HttpHeaders> future = this.execute(url, HttpMethod.OPTIONS, null, extractor);
        return AsyncRestTemplate.adaptToAllowHeader(future);
    }

    private static ListenableFuture<Set<HttpMethod>> adaptToAllowHeader(ListenableFuture<HttpHeaders> future) {
        return new ListenableFutureAdapter<Set<HttpMethod>, HttpHeaders>(future){

            @Override
            protected Set<HttpMethod> adapt(HttpHeaders headers) throws ExecutionException {
                return headers.getAllow();
            }
        };
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object ... uriVariables) throws RestClientException {
        Type type = responseType.getType();
        AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        Type type = responseType.getType();
        AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor);
    }

    @Override
    public <T> ListenableFuture<T> execute(String url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object ... uriVariables) throws RestClientException {
        URI expanded = this.getUriTemplateHandler().expand(url, uriVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override
    public <T> ListenableFuture<T> execute(String url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        URI expanded = this.getUriTemplateHandler().expand(url, uriVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override
    public <T> ListenableFuture<T> execute(URI url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return this.doExecute(url, method, requestCallback, responseExtractor);
    }

    protected <T> ListenableFuture<T> doExecute(URI url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull((Object)url, "'url' must not be null");
        Assert.notNull((Object)method, "'method' must not be null");
        try {
            AsyncClientHttpRequest request = this.createAsyncRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            ListenableFuture<ClientHttpResponse> responseFuture = request.executeAsync();
            return new ResponseExtractorFuture<T>(method, url, responseFuture, responseExtractor);
        }
        catch (IOException ex) {
            throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + url + "\":" + ex.getMessage(), ex);
        }
    }

    private void logResponseStatus(HttpMethod method, URI url, ClientHttpResponse response) {
        if (this.logger.isDebugEnabled()) {
            try {
                this.logger.debug((Object)("Async " + method.name() + " request for \"" + url + "\" resulted in " + response.getRawStatusCode() + " (" + response.getStatusText() + ")"));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void handleResponseError(HttpMethod method, URI url, ClientHttpResponse response) throws IOException {
        if (this.logger.isDebugEnabled()) {
            try {
                this.logger.debug((Object)("Async " + method.name() + " request for \"" + url + "\" resulted in " + response.getRawStatusCode() + " (" + response.getStatusText() + "); invoking error handler"));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.getErrorHandler().handleError(url, method, response);
    }

    protected <T> AsyncRequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.acceptHeaderRequestCallback(responseType));
    }

    protected <T> AsyncRequestCallback httpEntityCallback(@Nullable HttpEntity<T> requestBody) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(requestBody));
    }

    protected <T> AsyncRequestCallback httpEntityCallback(@Nullable HttpEntity<T> request, Type responseType) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(request, responseType));
    }

    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return this.syncTemplate.responseEntityExtractor(responseType);
    }

    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.syncTemplate.headersExtractor();
    }

    private static class AsyncRequestCallbackAdapter
    implements AsyncRequestCallback {
        private final RequestCallback adaptee;

        public AsyncRequestCallbackAdapter(RequestCallback requestCallback) {
            this.adaptee = requestCallback;
        }

        @Override
        public void doWithRequest(final AsyncClientHttpRequest request) throws IOException {
            this.adaptee.doWithRequest(new ClientHttpRequest(){

                @Override
                public ClientHttpResponse execute() throws IOException {
                    throw new UnsupportedOperationException("execute not supported");
                }

                @Override
                public OutputStream getBody() throws IOException {
                    return request.getBody();
                }

                @Override
                @Nullable
                public HttpMethod getMethod() {
                    return request.getMethod();
                }

                @Override
                public String getMethodValue() {
                    return request.getMethodValue();
                }

                @Override
                public URI getURI() {
                    return request.getURI();
                }

                @Override
                public HttpHeaders getHeaders() {
                    return request.getHeaders();
                }
            });
        }
    }

    private class ResponseExtractorFuture<T>
    extends ListenableFutureAdapter<T, ClientHttpResponse> {
        private final HttpMethod method;
        private final URI url;
        @Nullable
        private final ResponseExtractor<T> responseExtractor;

        public ResponseExtractorFuture(HttpMethod method, URI url, @Nullable ListenableFuture<ClientHttpResponse> clientHttpResponseFuture, ResponseExtractor<T> responseExtractor) {
            super(clientHttpResponseFuture);
            this.method = method;
            this.url = url;
            this.responseExtractor = responseExtractor;
        }

        @Override
        @Nullable
        protected final T adapt(ClientHttpResponse response) throws ExecutionException {
            try {
                if (!AsyncRestTemplate.this.getErrorHandler().hasError(response)) {
                    AsyncRestTemplate.this.logResponseStatus(this.method, this.url, response);
                } else {
                    AsyncRestTemplate.this.handleResponseError(this.method, this.url, response);
                }
                T t = this.convertResponse(response);
                return t;
            }
            catch (Throwable ex) {
                throw new ExecutionException(ex);
            }
            finally {
                response.close();
            }
        }

        @Nullable
        protected T convertResponse(ClientHttpResponse response) throws IOException {
            return this.responseExtractor != null ? (T)this.responseExtractor.extractData(response) : null;
        }
    }
}

