/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 */
package org.springframework.http.client;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public final class MultipartBodyBuilder {
    private final LinkedMultiValueMap<String, DefaultPartBuilder> parts = new LinkedMultiValueMap();

    public PartBuilder part(String name, Object part) {
        return this.part(name, part, null);
    }

    public PartBuilder part(String name, Object part, @Nullable MediaType contentType) {
        Object partBody;
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(part, "'part' must not be null");
        if (part instanceof Part) {
            Part partObject = (Part)part;
            PartBuilder builder = this.asyncPart(name, (Publisher)partObject.content(), (Class)DataBuffer.class);
            if (!partObject.headers().isEmpty()) {
                builder.headers(headers -> {
                    headers.putAll(partObject.headers());
                    String filename = headers.getContentDisposition().getFilename();
                    headers.setContentDispositionFormData(name, filename);
                });
            }
            if (contentType != null) {
                builder.contentType(contentType);
            }
            return builder;
        }
        if (part instanceof PublisherEntity) {
            PublisherPartBuilder builder = new PublisherPartBuilder(name, (PublisherEntity)part);
            if (contentType != null) {
                builder.contentType(contentType);
            }
            this.parts.add(name, builder);
            return builder;
        }
        HttpHeaders partHeaders = null;
        if (part instanceof HttpEntity) {
            partBody = ((HttpEntity)part).getBody();
            partHeaders = new HttpHeaders();
            partHeaders.putAll(((HttpEntity)part).getHeaders());
        } else {
            partBody = part;
        }
        if (partBody instanceof Publisher) {
            throw new IllegalArgumentException("Use asyncPart(String, Publisher, Class) or asyncPart(String, Publisher, ParameterizedTypeReference) or or MultipartBodyBuilder.PublisherEntity");
        }
        DefaultPartBuilder builder = new DefaultPartBuilder(name, partHeaders, partBody);
        if (contentType != null) {
            builder.contentType(contentType);
        }
        this.parts.add(name, builder);
        return builder;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, Class<T> elementClass) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(publisher, "'publisher' must not be null");
        Assert.notNull(elementClass, "'elementClass' must not be null");
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<T, P>(name, null, publisher, elementClass);
        this.parts.add(name, builder);
        return builder;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, ParameterizedTypeReference<T> typeReference) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(publisher, "'publisher' must not be null");
        Assert.notNull(typeReference, "'typeReference' must not be null");
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<T, P>(name, null, publisher, typeReference);
        this.parts.add(name, builder);
        return builder;
    }

    public MultiValueMap<String, HttpEntity<?>> build() {
        LinkedMultiValueMap result = new LinkedMultiValueMap(this.parts.size());
        for (Map.Entry entry : this.parts.entrySet()) {
            for (DefaultPartBuilder builder : entry.getValue()) {
                HttpEntity<?> entity = builder.build();
                result.add((String)entry.getKey(), entity);
            }
        }
        return result;
    }

    static final class PublisherEntity<T, P extends Publisher<T>>
    extends HttpEntity<P>
    implements ResolvableTypeProvider {
        private final ResolvableType resolvableType;

        PublisherEntity(@Nullable MultiValueMap<String, String> headers, P publisher, ResolvableType resolvableType) {
            super(publisher, headers);
            Assert.notNull(publisher, "'publisher' must not be null");
            Assert.notNull((Object)resolvableType, "'resolvableType' must not be null");
            this.resolvableType = resolvableType;
        }

        @Override
        @NonNull
        public ResolvableType getResolvableType() {
            return this.resolvableType;
        }
    }

    private static class PublisherPartBuilder<S, P extends Publisher<S>>
    extends DefaultPartBuilder {
        private final ResolvableType resolvableType;

        public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body2, Class<S> elementClass) {
            super(name, headers, body2);
            this.resolvableType = ResolvableType.forClass(elementClass);
        }

        public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body2, ParameterizedTypeReference<S> typeRef) {
            super(name, headers, body2);
            this.resolvableType = ResolvableType.forType(typeRef);
        }

        public PublisherPartBuilder(String name, PublisherEntity<S, P> other) {
            super(name, other.getHeaders(), other.getBody());
            this.resolvableType = other.getResolvableType();
        }

        @Override
        public HttpEntity<?> build() {
            Publisher publisher = (Publisher)this.body;
            Assert.state(publisher != null, "Publisher must not be null");
            return new PublisherEntity(this.headers, publisher, this.resolvableType);
        }
    }

    private static class DefaultPartBuilder
    implements PartBuilder {
        private final String name;
        @Nullable
        protected HttpHeaders headers;
        @Nullable
        protected final Object body;

        public DefaultPartBuilder(String name, @Nullable HttpHeaders headers, @Nullable Object body2) {
            this.name = name;
            this.headers = headers;
            this.body = body2;
        }

        @Override
        public PartBuilder contentType(MediaType contentType) {
            this.initHeadersIfNecessary().setContentType(contentType);
            return this;
        }

        @Override
        public PartBuilder filename(String filename) {
            this.initHeadersIfNecessary().setContentDispositionFormData(this.name, filename);
            return this;
        }

        @Override
        public PartBuilder header(String headerName, String ... headerValues) {
            this.initHeadersIfNecessary().addAll(headerName, Arrays.asList(headerValues));
            return this;
        }

        @Override
        public PartBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.initHeadersIfNecessary());
            return this;
        }

        private HttpHeaders initHeadersIfNecessary() {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
            }
            return this.headers;
        }

        public HttpEntity<?> build() {
            return new HttpEntity<Object>(this.body, this.headers);
        }
    }

    public static interface PartBuilder {
        public PartBuilder contentType(MediaType var1);

        public PartBuilder filename(String var1);

        public PartBuilder header(String var1, String ... var2);

        public PartBuilder headers(Consumer<HttpHeaders> var1);
    }
}

