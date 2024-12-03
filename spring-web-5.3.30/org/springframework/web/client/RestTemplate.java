/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.SpringProperties
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.SpringProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
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
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag((String)"spring.xml.ignore");
    private static final boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean jackson2CborPresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private static final boolean kotlinSerializationJsonPresent;
    private final List<HttpMessageConverter<?>> messageConverters = new ArrayList();
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    private UriTemplateHandler uriTemplateHandler;
    private final ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();

    public RestTemplate() {
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        this.messageConverters.add(new ResourceHttpMessageConverter(false));
        if (!shouldIgnoreXml) {
            try {
                this.messageConverters.add(new SourceHttpMessageConverter());
            }
            catch (Error error) {
                // empty catch block
            }
        }
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        if (romePresent) {
            this.messageConverters.add(new AtomFeedHttpMessageConverter());
            this.messageConverters.add(new RssChannelHttpMessageConverter());
        }
        if (!shouldIgnoreXml) {
            if (jackson2XmlPresent) {
                this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
            } else if (jaxb2Present) {
                this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
            }
        }
        if (kotlinSerializationJsonPresent) {
            this.messageConverters.add(new KotlinSerializationJsonHttpMessageConverter());
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
        this.uriTemplateHandler = RestTemplate.initUriTemplateHandler();
    }

    public RestTemplate(ClientHttpRequestFactory requestFactory) {
        this();
        this.setRequestFactory(requestFactory);
    }

    public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        this.validateConverters(messageConverters);
        this.messageConverters.addAll(messageConverters);
        this.uriTemplateHandler = RestTemplate.initUriTemplateHandler();
    }

    private static DefaultUriBuilderFactory initUriTemplateHandler() {
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        return uriFactory;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.validateConverters(messageConverters);
        if (this.messageConverters != messageConverters) {
            this.messageConverters.clear();
            this.messageConverters.addAll(messageConverters);
        }
    }

    private void validateConverters(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, (String)"At least one HttpMessageConverter is required");
        Assert.noNullElements(messageConverters, (String)"The HttpMessageConverter list must not contain null elements");
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull((Object)errorHandler, (String)"ResponseErrorHandler must not be null");
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
        Assert.notNull((Object)handler, (String)"UriTemplateHandler must not be null");
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
    public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = this.httpEntityCallback(entity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return RestTemplate.nonNull(this.doExecute(this.resolveUrl(entity), entity.getMethod(), requestCallback, responseExtractor));
    }

    @Override
    public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = this.httpEntityCallback(entity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return RestTemplate.nonNull(this.doExecute(this.resolveUrl(entity), entity.getMethod(), requestCallback, responseExtractor));
    }

    private URI resolveUrl(RequestEntity<?> entity) {
        if (entity instanceof RequestEntity.UriTemplateRequestEntity) {
            RequestEntity.UriTemplateRequestEntity ext = (RequestEntity.UriTemplateRequestEntity)entity;
            if (ext.getVars() != null) {
                return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVars());
            }
            if (ext.getVarsMap() != null) {
                return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVarsMap());
            }
            throw new IllegalStateException("No variables specified for URI template: " + ext.getUriTemplate());
        }
        return entity.getUrl();
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
        Assert.notNull((Object)url, (String)"URI is required");
        Assert.notNull((Object)((Object)method), (String)"HttpMethod is required");
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
                int code = response.getRawStatusCode();
                HttpStatus status = HttpStatus.resolve(code);
                this.logger.debug((Object)("Response " + (status != null ? status : Integer.valueOf(code))));
            }
            catch (IOException ex) {
                this.logger.debug((Object)"Failed to obtain response status code", (Throwable)ex);
            }
        }
        if (hasError) {
            errorHandler.handleError(url, method, response);
        }
    }

    public <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return new AcceptHeaderRequestCallback(responseType);
    }

    public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody) {
        return new HttpEntityRequestCallback(requestBody);
    }

    public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody, Type responseType) {
        return new HttpEntityRequestCallback(requestBody, responseType);
    }

    public <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return new ResponseEntityResponseExtractor(responseType);
    }

    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.headersExtractor;
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result");
        return result;
    }

    static {
        ClassLoader classLoader = RestTemplate.class.getClassLoader();
        romePresent = ClassUtils.isPresent((String)"com.rometools.rome.feed.WireFeed", (ClassLoader)classLoader);
        jaxb2Present = ClassUtils.isPresent((String)"javax.xml.bind.Binder", (ClassLoader)classLoader);
        jackson2Present = ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)classLoader) && ClassUtils.isPresent((String)"com.fasterxml.jackson.core.JsonGenerator", (ClassLoader)classLoader);
        jackson2XmlPresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.xml.XmlMapper", (ClassLoader)classLoader);
        jackson2SmilePresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.smile.SmileFactory", (ClassLoader)classLoader);
        jackson2CborPresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.cbor.CBORFactory", (ClassLoader)classLoader);
        gsonPresent = ClassUtils.isPresent((String)"com.google.gson.Gson", (ClassLoader)classLoader);
        jsonbPresent = ClassUtils.isPresent((String)"javax.json.bind.Jsonb", (ClassLoader)classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent((String)"kotlinx.serialization.json.Json", (ClassLoader)classLoader);
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
                    requestHeaders.forEach((key, values) -> httpHeaders.put((String)key, (List<String>)new ArrayList<String>((Collection<String>)values)));
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
                            requestHeaders.forEach((key, values) -> httpHeaders.put((String)key, (List<String>)new ArrayList<String>((Collection<String>)values)));
                        }
                        this.logBody(requestBody, requestContentType, genericConverter);
                        genericConverter.write(requestBody, requestBodyType, requestContentType, httpRequest);
                        return;
                    }
                    if (!messageConverter.canWrite(requestBodyClass, requestContentType)) continue;
                    if (!requestHeaders.isEmpty()) {
                        requestHeaders.forEach((key, values) -> httpHeaders.put((String)key, (List<String>)new ArrayList<String>((Collection<String>)values)));
                    }
                    this.logBody(requestBody, requestContentType, messageConverter);
                    messageConverter.write(requestBody, requestContentType, httpRequest);
                    return;
                }
                String message = "No HttpMessageConverter for " + requestBodyClass.getName();
                if (requestContentType != null) {
                    message = message + " and content type \"" + requestContentType + "\"";
                }
                throw new RestClientException(message);
            }
        }

        private void logBody(Object body, @Nullable MediaType mediaType, HttpMessageConverter<?> converter) {
            if (RestTemplate.this.logger.isDebugEnabled()) {
                if (mediaType != null) {
                    RestTemplate.this.logger.debug((Object)("Writing [" + body + "] as \"" + mediaType + "\""));
                } else {
                    RestTemplate.this.logger.debug((Object)("Writing [" + body + "] with " + converter.getClass().getName()));
                }
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
                List<MediaType> allSupportedMediaTypes = RestTemplate.this.getMessageConverters().stream().filter(converter -> this.canReadResponse(this.responseType, (HttpMessageConverter<?>)converter)).flatMap(converter -> this.getSupportedMediaTypes(this.responseType, (HttpMessageConverter<?>)converter)).distinct().sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
                if (RestTemplate.this.logger.isDebugEnabled()) {
                    RestTemplate.this.logger.debug((Object)("Accept=" + allSupportedMediaTypes));
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

        private Stream<MediaType> getSupportedMediaTypes(Type type, HttpMessageConverter<?> converter) {
            Type rawType = type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type;
            Class clazz = rawType instanceof Class ? (Class)rawType : null;
            return (clazz != null ? converter.getSupportedMediaTypes(clazz) : converter.getSupportedMediaTypes()).stream().map(mediaType -> {
                if (mediaType.getCharset() != null) {
                    return new MediaType(mediaType.getType(), mediaType.getSubtype());
                }
                return mediaType;
            });
        }
    }
}

