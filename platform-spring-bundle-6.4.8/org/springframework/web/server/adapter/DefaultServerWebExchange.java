/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.adapter;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

public class DefaultServerWebExchange
implements ServerWebExchange {
    private static final List<HttpMethod> SAFE_METHODS = Arrays.asList(HttpMethod.GET, HttpMethod.HEAD);
    private static final ResolvableType FORM_DATA_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);
    private static final ResolvableType MULTIPART_DATA_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
    private static final Mono<MultiValueMap<String, String>> EMPTY_FORM_DATA = Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(0))).cache();
    private static final Mono<MultiValueMap<String, Part>> EMPTY_MULTIPART_DATA = Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(0))).cache();
    private final ServerHttpRequest request;
    private final ServerHttpResponse response;
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    private final Mono<WebSession> sessionMono;
    private final LocaleContextResolver localeContextResolver;
    private final Mono<MultiValueMap<String, String>> formDataMono;
    private final Mono<MultiValueMap<String, Part>> multipartDataMono;
    @Nullable
    private final ApplicationContext applicationContext;
    private volatile boolean notModified;
    private Function<String, String> urlTransformer = url -> url;
    @Nullable
    private Object logId;
    private String logPrefix = "";

    public DefaultServerWebExchange(ServerHttpRequest request, ServerHttpResponse response, WebSessionManager sessionManager, ServerCodecConfigurer codecConfigurer, LocaleContextResolver localeContextResolver) {
        this(request, response, sessionManager, codecConfigurer, localeContextResolver, null);
    }

    DefaultServerWebExchange(ServerHttpRequest request, ServerHttpResponse response, WebSessionManager sessionManager, ServerCodecConfigurer codecConfigurer, LocaleContextResolver localeContextResolver, @Nullable ApplicationContext applicationContext) {
        Assert.notNull((Object)request, "'request' is required");
        Assert.notNull((Object)response, "'response' is required");
        Assert.notNull((Object)sessionManager, "'sessionManager' is required");
        Assert.notNull((Object)codecConfigurer, "'codecConfigurer' is required");
        Assert.notNull((Object)localeContextResolver, "'localeContextResolver' is required");
        this.attributes.put(ServerWebExchange.LOG_ID_ATTRIBUTE, request.getId());
        this.request = request;
        this.response = response;
        this.sessionMono = sessionManager.getSession(this).cache();
        this.localeContextResolver = localeContextResolver;
        this.formDataMono = DefaultServerWebExchange.initFormData(request, codecConfigurer, this.getLogPrefix());
        this.multipartDataMono = DefaultServerWebExchange.initMultipartData(request, codecConfigurer, this.getLogPrefix());
        this.applicationContext = applicationContext;
    }

    private static Mono<MultiValueMap<String, String>> initFormData(ServerHttpRequest request, ServerCodecConfigurer configurer, String logPrefix) {
        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return configurer.getReaders().stream().filter(reader -> reader.canRead(FORM_DATA_TYPE, MediaType.APPLICATION_FORM_URLENCODED)).findFirst().orElseThrow(() -> new IllegalStateException("No form data HttpMessageReader.")).readMono(FORM_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix)).switchIfEmpty(EMPTY_FORM_DATA).cache();
            }
        }
        catch (InvalidMediaTypeException invalidMediaTypeException) {
            // empty catch block
        }
        return EMPTY_FORM_DATA;
    }

    private static Mono<MultiValueMap<String, Part>> initMultipartData(ServerHttpRequest request, ServerCodecConfigurer configurer, String logPrefix) {
        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
                return configurer.getReaders().stream().filter(reader -> reader.canRead(MULTIPART_DATA_TYPE, MediaType.MULTIPART_FORM_DATA)).findFirst().orElseThrow(() -> new IllegalStateException("No multipart HttpMessageReader.")).readMono(MULTIPART_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix)).switchIfEmpty(EMPTY_MULTIPART_DATA).cache();
            }
        }
        catch (InvalidMediaTypeException invalidMediaTypeException) {
            // empty catch block
        }
        return EMPTY_MULTIPART_DATA;
    }

    @Override
    public ServerHttpRequest getRequest() {
        return this.request;
    }

    private HttpHeaders getRequestHeaders() {
        return this.getRequest().getHeaders();
    }

    @Override
    public ServerHttpResponse getResponse() {
        return this.response;
    }

    private HttpHeaders getResponseHeaders() {
        return this.getResponse().getHeaders();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Mono<WebSession> getSession() {
        return this.sessionMono;
    }

    @Override
    public <T extends Principal> Mono<T> getPrincipal() {
        return Mono.empty();
    }

    @Override
    public Mono<MultiValueMap<String, String>> getFormData() {
        return this.formDataMono;
    }

    @Override
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return this.multipartDataMono;
    }

    @Override
    public LocaleContext getLocaleContext() {
        return this.localeContextResolver.resolveLocaleContext(this);
    }

    @Override
    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public boolean isNotModified() {
        return this.notModified;
    }

    @Override
    public boolean checkNotModified(Instant lastModified) {
        return this.checkNotModified(null, lastModified);
    }

    @Override
    public boolean checkNotModified(String etag) {
        return this.checkNotModified(etag, Instant.MIN);
    }

    @Override
    public boolean checkNotModified(@Nullable String etag, Instant lastModified) {
        HttpStatus status = this.getResponse().getStatusCode();
        if (this.notModified || status != null && !HttpStatus.OK.equals((Object)status)) {
            return this.notModified;
        }
        if (this.validateIfUnmodifiedSince(lastModified)) {
            if (this.notModified) {
                this.getResponse().setStatusCode(HttpStatus.PRECONDITION_FAILED);
            }
            if (SAFE_METHODS.contains((Object)this.getRequest().getMethod())) {
                if (StringUtils.hasLength(etag) && this.getResponseHeaders().getETag() == null) {
                    this.getResponseHeaders().setETag(this.padEtagIfNecessary(etag));
                }
                this.getResponseHeaders().setLastModified(lastModified.toEpochMilli());
            }
            return this.notModified;
        }
        boolean validated = this.validateIfNoneMatch(etag);
        if (!validated) {
            this.validateIfModifiedSince(lastModified);
        }
        boolean isHttpGetOrHead = SAFE_METHODS.contains((Object)this.getRequest().getMethod());
        if (this.notModified) {
            this.getResponse().setStatusCode(isHttpGetOrHead ? HttpStatus.NOT_MODIFIED : HttpStatus.PRECONDITION_FAILED);
        }
        if (isHttpGetOrHead) {
            if (lastModified.isAfter(Instant.EPOCH) && this.getResponseHeaders().getLastModified() == -1L) {
                this.getResponseHeaders().setLastModified(lastModified.toEpochMilli());
            }
            if (StringUtils.hasLength(etag) && this.getResponseHeaders().getETag() == null) {
                this.getResponseHeaders().setETag(this.padEtagIfNecessary(etag));
            }
        }
        return this.notModified;
    }

    private boolean validateIfUnmodifiedSince(Instant lastModified) {
        if (lastModified.isBefore(Instant.EPOCH)) {
            return false;
        }
        long ifUnmodifiedSince = this.getRequestHeaders().getIfUnmodifiedSince();
        if (ifUnmodifiedSince == -1L) {
            return false;
        }
        Instant sinceInstant = Instant.ofEpochMilli(ifUnmodifiedSince);
        this.notModified = sinceInstant.isBefore(lastModified.truncatedTo(ChronoUnit.SECONDS));
        return true;
    }

    private boolean validateIfNoneMatch(@Nullable String etag) {
        List<String> ifNoneMatch;
        if (!StringUtils.hasLength(etag)) {
            return false;
        }
        try {
            ifNoneMatch = this.getRequestHeaders().getIfNoneMatch();
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
        if (ifNoneMatch.isEmpty()) {
            return false;
        }
        if ((etag = this.padEtagIfNecessary(etag)).startsWith("W/")) {
            etag = etag.substring(2);
        }
        for (String clientEtag : ifNoneMatch) {
            if (!StringUtils.hasLength(clientEtag)) continue;
            if (clientEtag.startsWith("W/")) {
                clientEtag = clientEtag.substring(2);
            }
            if (!clientEtag.equals(etag)) continue;
            this.notModified = true;
            break;
        }
        return true;
    }

    private String padEtagIfNecessary(String etag) {
        if (!StringUtils.hasLength(etag)) {
            return etag;
        }
        if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
            return etag;
        }
        return "\"" + etag + "\"";
    }

    private boolean validateIfModifiedSince(Instant lastModified) {
        if (lastModified.isBefore(Instant.EPOCH)) {
            return false;
        }
        long ifModifiedSince = this.getRequestHeaders().getIfModifiedSince();
        if (ifModifiedSince == -1L) {
            return false;
        }
        this.notModified = ChronoUnit.SECONDS.between(lastModified, Instant.ofEpochMilli(ifModifiedSince)) >= 0L;
        return true;
    }

    @Override
    public String transformUrl(String url) {
        return this.urlTransformer.apply(url);
    }

    @Override
    public void addUrlTransformer(Function<String, String> transformer) {
        Assert.notNull(transformer, "'encoder' must not be null");
        this.urlTransformer = this.urlTransformer.andThen(transformer);
    }

    @Override
    public String getLogPrefix() {
        Object value = this.getAttribute(LOG_ID_ATTRIBUTE);
        if (this.logId != value) {
            this.logId = value;
            this.logPrefix = value != null ? "[" + value + "] " : "";
        }
        return this.logPrefix;
    }
}

