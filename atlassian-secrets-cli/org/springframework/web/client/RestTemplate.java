/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.AbstractUriTemplateHandler;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

public class RestTemplate
extends InterceptingHttpAccessor
implements RestOperations {
    private static final boolean romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", RestTemplate.class.getClassLoader());
    private static final boolean jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", RestTemplate.class.getClassLoader());
    private static final boolean jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", RestTemplate.class.getClassLoader()) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", RestTemplate.class.getClassLoader());
    private static final boolean jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", RestTemplate.class.getClassLoader());
    private static final boolean jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", RestTemplate.class.getClassLoader());
    private static final boolean jackson2CborPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.cbor.CBORFactory", RestTemplate.class.getClassLoader());
    private static final boolean gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", RestTemplate.class.getClassLoader());
    private static final boolean jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", RestTemplate.class.getClassLoader());
    private final List<HttpMessageConverter<?>> messageConverters = new ArrayList();
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    private UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();
    private final ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();

    public RestTemplate() {
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        this.messageConverters.add(new ResourceHttpMessageConverter(false));
        this.messageConverters.add(new SourceHttpMessageConverter());
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        if (romePresent) {
            this.messageConverters.add(new AtomFeedHttpMessageConverter());
            this.messageConverters.add(new RssChannelHttpMessageConverter());
        }
        if (jackson2XmlPresent) {
            this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
        } else if (jaxb2Present) {
            this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
            this.messageConverters.add(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            this.messageConverters.add(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            this.messageConverters.add(new JsonbHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            this.messageConverters.add(new MappingJackson2SmileHttpMessageConverter());
        }
        if (jackson2CborPresent) {
            this.messageConverters.add(new MappingJackson2CborHttpMessageConverter());
        }
    }

    public RestTemplate(ClientHttpRequestFactory requestFactory) {
        this();
        this.setRequestFactory(requestFactory);
    }

    public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
        this.messageConverters.addAll(messageConverters);
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
        if (this.messageConverters != messageConverters) {
            this.messageConverters.clear();
            this.messageConverters.addAll(messageConverters);
        }
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull((Object)errorHandler, "ResponseErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    public ResponseErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setDefaultUriVariables(Map<String, ?> uriVars) {
        if (this.uriTemplateHandler instanceof DefaultUriBuilderFactory) {
            ((DefaultUriBuilderFactory)this.uriTemplateHandler).setDefaultUriVariables(uriVars);
        } else if (this.uriTemplateHandler instanceof AbstractUriTemplateHandler) {
            ((AbstractUriTemplateHandler)this.uriTemplateHandler).setDefaultUriVariables(uriVars);
        } else {
            throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
        }
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        Assert.notNull((Object)handler, "UriTemplateHandler must not be null");
        this.uriTemplateHandler = handler;
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return this.uriTemplateHandler;
    }

    @Override
    @Nullable
    public <T> T getForObject(String url, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, HttpMethod.GET, requestCallback, responseExtractor));
    }

    @Override
    public HttpHeaders headForHeaders(String url, Object ... uriVariables) throws RestClientException {
        return RestTemplate.nonNull(this.execute(url, HttpMethod.HEAD, null, this.headersExtractor(), uriVariables));
    }

    @Override
    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        return RestTemplate.nonNull(this.execute(url, HttpMethod.HEAD, null, this.headersExtractor(), uriVariables));
    }

    @Override
    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return RestTemplate.nonNull(this.execute(url, HttpMethod.HEAD, null, this.headersExtractor()));
    }

    @Override
    @Nullable
    public URI postForLocation(String url, @Nullable Object request, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request);
        HttpHeaders headers = this.execute(url, HttpMethod.POST, requestCallback, this.headersExtractor(), uriVariables);
        return headers != null ? headers.getLocation() : null;
    }

    @Override
    @Nullable
    public URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request);
        HttpHeaders headers = this.execute(url, HttpMethod.POST, requestCallback, this.headersExtractor(), uriVariables);
        return headers != null ? headers.getLocation() : null;
    }

    @Override
    @Nullable
    public URI postForLocation(URI url, @Nullable Object request) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request);
        HttpHeaders headers = this.execute(url, HttpMethod.POST, requestCallback, this.headersExtractor());
        return headers != null ? headers.getLocation() : null;
    }

    @Override
    @Nullable
    public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T postForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters());
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, HttpMethod.POST, requestCallback, responseExtractor));
    }

    @Override
    public void put(String url, @Nullable Object request, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request);
        this.execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
    }

    @Override
    public void put(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request);
        this.execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
    }

    @Override
    public void put(URI url, @Nullable Object request) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request);
        this.execute(url, HttpMethod.PUT, requestCallback, null);
    }

    @Override
    @Nullable
    public <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor responseExtractor = new HttpMessageConverterExtractor(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T patchForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters());
        return this.execute(url, HttpMethod.PATCH, requestCallback, responseExtractor);
    }

    @Override
    public void delete(String url, Object ... uriVariables) throws RestClientException {
        this.execute(url, HttpMethod.DELETE, null, null, uriVariables);
    }

    @Override
    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        this.execute(url, HttpMethod.DELETE, null, null, uriVariables);
    }

    @Override
    public void delete(URI url) throws RestClientException {
        this.execute(url, HttpMethod.DELETE, null, null);
    }

    @Override
    public Set<HttpMethod> optionsForAllow(String url, Object ... uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        HttpHeaders headers = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor, uriVariables);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override
    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        HttpHeaders headers = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor, uriVariables);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override
    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        HttpHeaders headers = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object ... uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.execute(url, method, requestCallback, responseExtractor));
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object ... uriVariables) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return RestTemplate.nonNull(this.execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return RestTemplate.nonNull(this.execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return RestTemplate.nonNull(this.execute(url, method, requestCallback, responseExtractor));
    }

    @Override
    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor));
    }

    @Override
    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return RestTemplate.nonNull(this.doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor));
    }

    @Override
    @Nullable
    public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object ... uriVariables) throws RestClientException {
        URI expanded = this.getUriTemplateHandler().expand(url, uriVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override
    @Nullable
    public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        URI expanded = this.getUriTemplateHandler().expand(url, uriVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override
    @Nullable
    public <T> T execute(URI url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return this.doExecute(url, method, requestCallback, responseExtractor);
    }

    @Nullable
    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull((Object)url, "URI is required");
        Assert.notNull((Object)method, "HttpMethod is required");
        try (ClientHttpResponse response = null;){
            ClientHttpRequest request = this.createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
            this.handleResponse(url, method, response);
            T t = responseExtractor != null ? (T)responseExtractor.extractData(response) : null;
            return t;
        }
    }

    protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler errorHandler = this.getErrorHandler();
        boolean hasError = errorHandler.hasError(response);
        if (this.logger.isDebugEnabled()) {
            try {
                this.logger.debug(method.name() + " request for \"" + url + "\" resulted in " + response.getRawStatusCode() + " (" + response.getStatusText() + ")" + (hasError ? "; invoking error handler" : ""));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (hasError) {
            errorHandler.handleError(url, method, response);
        }
    }

    protected <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return new AcceptHeaderRequestCallback(responseType);
    }

    protected <T> RequestCallback httpEntityCallback(@Nullable Object requestBody) {
        return new HttpEntityRequestCallback(requestBody);
    }

    protected <T> RequestCallback httpEntityCallback(@Nullable Object requestBody, Type responseType) {
        return new HttpEntityRequestCallback(requestBody, responseType);
    }

    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return new ResponseEntityResponseExtractor(responseType);
    }

    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.headersExtractor;
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "No result");
        return result;
    }

    private static class HeadersExtractor
    implements ResponseExtractor<HttpHeaders> {
        private HeadersExtractor() {
        }

        @Override
        public HttpHeaders extractData(ClientHttpResponse response) {
            return response.getHeaders();
        }
    }

    private class ResponseEntityResponseExtractor<T>
    implements ResponseExtractor<ResponseEntity<T>> {
        @Nullable
        private final HttpMessageConverterExtractor<T> delegate;

        public ResponseEntityResponseExtractor(Type responseType) {
            this.delegate = responseType != null && Void.class != responseType ? new HttpMessageConverterExtractor(responseType, RestTemplate.this.getMessageConverters(), RestTemplate.this.logger) : null;
        }

        @Override
        public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
            if (this.delegate != null) {
                T body = this.delegate.extractData(response);
                return ((ResponseEntity.BodyBuilder)ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders())).body(body);
            }
            return ((ResponseEntity.BodyBuilder)ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders())).build();
        }
    }

    private class HttpEntityRequestCallback
    extends AcceptHeaderRequestCallback {
        private final HttpEntity<?> requestEntity;

        public HttpEntityRequestCallback(Object requestBody) {
            this(requestBody, null);
        }

        public HttpEntityRequestCallback(@Nullable Object requestBody, Type responseType) {
            super(responseType);
            this.requestEntity = requestBody instanceof HttpEntity ? (HttpEntity<Object>)requestBody : (requestBody != null ? new HttpEntity<Object>(requestBody) : HttpEntity.EMPTY);
        }

        @Override
        public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
            super.doWithRequest(httpRequest);
            Object requestBody = this.requestEntity.getBody();
            if (requestBody == null) {
                HttpHeaders httpHeaders = httpRequest.getHeaders();
                HttpHeaders requestHeaders = this.requestEntity.getHeaders();
                if (!requestHeaders.isEmpty()) {
                    requestHeaders.forEach((key, values) -> httpHeaders.put((String)key, (List<String>)new LinkedList<String>((Collection<String>)values)));
                }
                if (httpHeaders.getContentLength() < 0L) {
                    httpHeaders.setContentLength(0L);
                }
            } else {
                Class<?> requestBodyClass = requestBody.getClass();
                Class<?> requestBodyType = this.requestEntity instanceof RequestEntity ? ((RequestEntity)this.requestEntity).getType() : requestBodyClass;
                HttpHeaders httpHeaders = httpRequest.getHeaders();
                HttpHeaders requestHeaders = this.requestEntity.getHeaders();
                MediaType requestContentType = requestHeaders.getContentType();
                for (HttpMessageConverter<?> messageConverter : RestTemplate.this.getMessageConverters()) {
                    if (messageConverter instanceof GenericHttpMessageConverter) {
                        GenericHttpMessageConverter genericConverter = (GenericHttpMessageConverter)messageConverter;
                        if (!genericConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) continue;
                        if (!requestHeaders.isEmpty()) {
                            requestHeaders.forEach((key, values) -> httpHeaders.put((String)key, (List<String>)new LinkedList<String>((Collection<String>)values)));
                        }
                        if (RestTemplate.this.logger.isDebugEnabled()) {
                            if (requestContentType != null) {
                                RestTemplate.this.logger.debug("Writing [" + requestBody + "] as \"" + requestContentType + "\" using [" + messageConverter + "]");
                            } else {
                                RestTemplate.this.logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
                            }
                        }
                        genericConverter.write(requestBody, requestBodyType, requestContentType, httpRequest);
                        return;
                    }
                    if (!messageConverter.canWrite(requestBodyClass, requestContentType)) continue;
                    if (!requestHeaders.isEmpty()) {
                        requestHeaders.forEach((key, values) -> httpHeaders.put((String)key, (List<String>)new LinkedList<String>((Collection<String>)values)));
                    }
                    if (RestTemplate.this.logger.isDebugEnabled()) {
                        if (requestContentType != null) {
                            RestTemplate.this.logger.debug("Writing [" + requestBody + "] as \"" + requestContentType + "\" using [" + messageConverter + "]");
                        } else {
                            RestTemplate.this.logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
                        }
                    }
                    messageConverter.write(requestBody, requestContentType, httpRequest);
                    return;
                }
                String message = "Could not write request: no suitable HttpMessageConverter found for request type [" + requestBodyClass.getName() + "]";
                if (requestContentType != null) {
                    message = message + " and content type [" + requestContentType + "]";
                }
                throw new RestClientException(message);
            }
        }
    }

    private class AcceptHeaderRequestCallback
    implements RequestCallback {
        @Nullable
        private final Type responseType;

        public AcceptHeaderRequestCallback(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public void doWithRequest(ClientHttpRequest request) throws IOException {
            if (this.responseType != null) {
                List<MediaType> allSupportedMediaTypes = RestTemplate.this.getMessageConverters().stream().filter(converter -> this.canReadResponse(this.responseType, (HttpMessageConverter<?>)converter)).flatMap(this::getSupportedMediaTypes).distinct().sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
                if (RestTemplate.this.logger.isDebugEnabled()) {
                    RestTemplate.this.logger.debug("Setting request Accept header to " + allSupportedMediaTypes);
                }
                request.getHeaders().setAccept(allSupportedMediaTypes);
            }
        }

        private boolean canReadResponse(Type responseType, HttpMessageConverter<?> converter) {
            Class responseClass;
            Class clazz = responseClass = responseType instanceof Class ? (Class)responseType : null;
            if (responseClass != null) {
                return converter.canRead(responseClass, null);
            }
            if (converter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter genericConverter = (GenericHttpMessageConverter)converter;
                return genericConverter.canRead(responseType, null, null);
            }
            return false;
        }

        private Stream<MediaType> getSupportedMediaTypes(HttpMessageConverter<?> messageConverter) {
            return messageConverter.getSupportedMediaTypes().stream().map(mediaType -> {
                if (mediaType.getCharset() != null) {
                    return new MediaType(mediaType.getType(), mediaType.getSubtype());
                }
                return mediaType;
            });
        }
    }
}

