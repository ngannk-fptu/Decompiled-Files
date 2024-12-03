/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FormHttpMessageReader
implements HttpMessageReader<MultiValueMap<String, String>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ResolvableType MULTIVALUE_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);
    private Charset defaultCharset = DEFAULT_CHARSET;

    public void setDefaultCharset(Charset charset) {
        Assert.notNull((Object)charset, "Charset must not be null");
        this.defaultCharset = charset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return (MULTIVALUE_TYPE.isAssignableFrom(elementType) || elementType.hasUnresolvableGenerics() && MultiValueMap.class.isAssignableFrom(elementType.resolve(Object.class))) && (mediaType == null || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType));
    }

    @Override
    public Flux<MultiValueMap<String, String>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.from(this.readMono(elementType, message, hints));
    }

    @Override
    public Mono<MultiValueMap<String, String>> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = message.getHeaders().getContentType();
        Charset charset = this.getMediaTypeCharset(contentType);
        return DataBufferUtils.join(message.getBody()).map(buffer -> {
            CharBuffer charBuffer = charset.decode(buffer.asByteBuffer());
            String body = charBuffer.toString();
            DataBufferUtils.release(buffer);
            return this.parseFormData(charset, body);
        });
    }

    private Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (mediaType != null && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        }
        return this.getDefaultCharset();
    }

    private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
        try {
            for (String pair : pairs) {
                int idx = pair.indexOf(61);
                if (idx == -1) {
                    result.add(URLDecoder.decode(pair, charset.name()), null);
                    continue;
                }
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add(name, value);
            }
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
    }
}

