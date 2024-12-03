/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.io.Resource;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;

public class ResourceHttpMessageReader
extends DecoderHttpMessageReader<Resource> {
    public ResourceHttpMessageReader() {
        super(new ResourceDecoder());
    }

    public ResourceHttpMessageReader(ResourceDecoder resourceDecoder) {
        super(resourceDecoder);
    }

    @Override
    protected Map<String, Object> getReadHints(ResolvableType elementType, ReactiveHttpInputMessage message) {
        String filename = message.getHeaders().getContentDisposition().getFilename();
        return StringUtils.hasText(filename) ? Hints.from(ResourceDecoder.FILENAME_HINT, filename) : Hints.none();
    }

    @Override
    protected Map<String, Object> getReadHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
        return this.getReadHints(elementType, request);
    }
}

