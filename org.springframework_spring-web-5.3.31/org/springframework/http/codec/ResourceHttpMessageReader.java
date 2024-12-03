/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Hints
 *  org.springframework.core.codec.ResourceDecoder
 *  org.springframework.core.io.Resource
 *  org.springframework.util.StringUtils
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
        return StringUtils.hasText((String)filename) ? Hints.from((String)ResourceDecoder.FILENAME_HINT, (Object)filename) : Hints.none();
    }

    @Override
    protected Map<String, Object> getReadHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
        return this.getReadHints(elementType, request);
    }
}

