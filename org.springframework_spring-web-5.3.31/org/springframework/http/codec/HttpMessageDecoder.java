/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Decoder
 */
package org.springframework.http.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

public interface HttpMessageDecoder<T>
extends Decoder<T> {
    public Map<String, Object> getDecodeHints(ResolvableType var1, ResolvableType var2, ServerHttpRequest var3, ServerHttpResponse var4);
}

