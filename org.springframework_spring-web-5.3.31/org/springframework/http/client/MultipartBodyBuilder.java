/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.ResolvableTypeProvider
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.client;

import java.util.Arrays;
import java.util.List;
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
        Assert.hasLength((String)name, (String)"'name' must not be empty");
        Assert.notNull((Object)part, (String)"'part' must not be null");
        if (part instanceof Part) {
            Part partObject = (Part)part;
            PartBuilder builder = this.asyncPart(name, (Publisher)partObject.content(), (Class)DataBuffer.class);
            if (!partObject.headers().isEmpty()) {
                builder.headers(headers -> {
                    headers.putAll((Map<? extends String, ? extends List<String>>)((Object)partObject.headers()));
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
            this.parts.add((Object)name, builder);
            return builder;
        }
        HttpHeaders partHeaders = null;
        if (part instanceof HttpEntity) {
            partBody = ((HttpEntity)part).getBody();
            partHeaders = new HttpHeaders();
            partHeaders.putAll((Map<? extends String, ? extends List<String>>)((Object)((HttpEntity)part).getHeaders()));
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
        this.parts.add((Object)name, (Object)builder);
        return builder;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, Class<T> elementClass) {
        Assert.hasLength((String)name, (String)"'name' must not be empty");
        Assert.notNull(publisher, (String)"'publisher' must not be null");
        Assert.notNull(elementClass, (String)"'elementClass' must not be null");
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<T, P>(name, null, publisher, elementClass);
        this.parts.add((Object)name, builder);
        return builder;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, ParameterizedTypeReference<T> typeReference) {
        Assert.hasLength((String)name, (String)"'name' must not be empty");
        Assert.notNull(publisher, (String)"'publisher' must not be null");
        Assert.notNull(typeReference, (String)"'typeReference' must not be null");
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<T, P>(name, null, publisher, typeReference);
        this.parts.add((Object)name, builder);
        return builder;
    }

    public MultiValueMap<String, HttpEntity<?>> build() {
        LinkedMultiValueMap result = new LinkedMultiValueMap(this.parts.size());
        for (Map.Entry entry : this.parts.entrySet()) {
            for (DefaultPartBuilder builder : (List)entry.getValue()) {
                HttpEntity<?> entity = builder.build();
                result.add(entry.getKey(), entity);
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
            Assert.notNull(publisher, (String)"'publisher' must not be null");
            Assert.notNull((Object)resolvableType, (String)"'resolvableType' must not be null");
            this.resolvableType = resolvableType;
        }

        @NonNull
        public ResolvableType getResolvableType() {
            return this.resolvableType;
        }
    }

    private static class PublisherPartBuilder<S, P extends Publisher<S>>
    extends DefaultPartBuilder {
        private final ResolvableType resolvableType;

        public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body, Class<S> elementClass) {
            super(name, headers, body);
            this.resolvableType = ResolvableType.forClass(elementClass);
        }

        public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body, ParameterizedTypeReference<S> typeRef) {
            super(name, headers, body);
            this.resolvableType = ResolvableType.forType(typeRef);
        }

        public PublisherPartBuilder(String name, PublisherEntity<S, P> other) {
            super(name, other.getHeaders(), other.getBody());
            this.resolvableType = other.getResolvableType();
        }

        @Override
        public HttpEntity<?> build() {
            Publisher publisher = (Publisher)this.body;
            Assert.state((publisher != null ? 1 : 0) != 0, (String)"Publisher must not be null");
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

        public DefaultPartBuilder(String name, @Nullable HttpHeaders headers, @Nullable Object body) {
            this.name = name;
            this.headers = headers;
            this.body = body;
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

