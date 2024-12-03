/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;

public interface HttpMessageEncoder<T>
extends Encoder<T> {
    public List<MediaType> getStreamingMediaTypes();

    default public Map<String, Object> getEncodeHints(ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        return Hints.none();
    }
}

